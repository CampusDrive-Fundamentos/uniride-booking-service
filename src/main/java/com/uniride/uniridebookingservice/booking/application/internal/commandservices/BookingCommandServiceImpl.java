package com.uniride.uniridebookingservice.booking.application.internal.commandservices;

import com.uniride.uniridebookingservice.booking.application.outboundservices.routes.RoutesServiceIntegration;
import com.uniride.uniridebookingservice.booking.domain.model.aggregates.Booking;
import com.uniride.uniridebookingservice.booking.domain.model.commands.*;
import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.BookingStatus;
import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.PaymentStatus;
import com.uniride.uniridebookingservice.booking.domain.services.BookingCommandService;
import com.uniride.uniridebookingservice.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingCommandServiceImpl implements BookingCommandService {
    private final BookingRepository bookingRepository;
    private final RoutesServiceIntegration routesIntegration;

    public BookingCommandServiceImpl(BookingRepository bookingRepository, RoutesServiceIntegration routesIntegration) {
        this.bookingRepository = bookingRepository;
        this.routesIntegration = routesIntegration;
    }

    @Override
    public Optional<Booking> handle(CreateBookingCommand command) {
        Booking booking = new Booking(command.leaderId(), command.routeId());
        return Optional.of(bookingRepository.save(booking));
    }

    @Override
    public Optional<Booking> handle(JoinBookingCommand command) {
        return bookingRepository.findById(command.bookingId()).map(booking -> {
            if (booking.addFollower(command.studentId())) {
                booking = bookingRepository.save(booking);

                routesIntegration.addWaypoint(booking.getRouteId(), command.lat(), command.lng(), command.address(), command.token());

                if (booking.getStatus() == BookingStatus.FULL) {
                    routesIntegration.updateVisibility(booking.getRouteId(), "HIDDEN", command.token());
                }

                return booking;
            }
            throw new IllegalArgumentException("El grupo ya está lleno o el usuario ya pertenece a él");
        });
    }

    @Override
    public Optional<Booking> handle(LeaveBookingCommand command) {
        return bookingRepository.findById(command.bookingId()).map(booking -> {
            // BLOQUEO DE SEGURIDAD: El líder nunca debe llamar a "leave".
            if (booking.getLeaderId().equals(command.studentId())) {
                throw new IllegalArgumentException("El líder no puede salir del grupo. Debe usar la opción de Cancelar Grupo.");
            }

            booking.removeFollower(command.studentId());
            booking = bookingRepository.save(booking);
            routesIntegration.removeWaypoint(booking.getRouteId(), command.lat(), command.lng(), command.token());

            if (booking.getStatus() == BookingStatus.OPEN) {
                routesIntegration.updateVisibility(booking.getRouteId(), "VISIBLE", command.token());
            }
            return booking;
        });
    }

    @Override
    public Optional<Booking> handle(LockBookingCommand command) {
        return bookingRepository.findById(command.bookingId()).map(booking -> {
            booking.generatePinAndLock();
            routesIntegration.updateVisibility(booking.getRouteId(), "HIDDEN", command.token());
            return bookingRepository.save(booking);
        });
    }

    @Override
    public Optional<Booking> handle(UpdatePaymentCommand command) {
        return bookingRepository.findById(command.bookingId()).map(booking -> {
            booking.getPassengers().stream()
                .filter(p -> p.getStudentId().equals(command.passengerId()))
                .findFirst()
                .ifPresent(p -> {
                    p.setPaymentMethod(command.method());
                    p.setPaymentStatus(PaymentStatus.PAID);
                });
            return bookingRepository.save(booking);
        });
    }

    @Override
    public void handle(CancelBookingCommand command) {
        bookingRepository.findById(command.bookingId()).ifPresent(booking -> {
            // El líder borra tanto el grupo como la ruta de manera definitiva
            routesIntegration.deleteRoute(booking.getRouteId(), command.token());
            bookingRepository.delete(booking);
        });
    }
}