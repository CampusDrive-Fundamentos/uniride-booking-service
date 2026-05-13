package com.uniride.uniridebookingservice.booking.application.internal.commandservices;

import com.uniride.uniridebookingservice.booking.application.outboundservices.routes.RoutesServiceIntegration;
import com.uniride.uniridebookingservice.booking.domain.model.aggregates.Booking;
import com.uniride.uniridebookingservice.booking.domain.model.commands.*;
import com.uniride.uniridebookingservice.booking.domain.model.entities.Passenger;
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

                // 1. Llamamos a Routes para que añada la parada al mapa
                routesIntegration.addWaypoint(booking.getRouteId(), command.lat(), command.lng(), command.address(), command.token());

                // 2. Si se llenó (4 pasajeros), ocultamos el mapa
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
            boolean wasFull = booking.getStatus() == BookingStatus.FULL;

            booking.removeFollower(command.studentId());
            booking = bookingRepository.save(booking);

            // 1. Llamamos a Routes para que quite la parada y achique el mapa
            routesIntegration.removeWaypoint(booking.getRouteId(), command.lat(), command.lng(), command.token());

            // 2. Si estaba lleno y alguien se salió, el auto vuelve a ser visible en el radar
            if (wasFull) {
                routesIntegration.updateVisibility(booking.getRouteId(), "SEARCHABLE", command.token());
            }
            return booking;
        });
    }

    @Override
    public Optional<Booking> handle(LockBookingCommand command) {
        return bookingRepository.findById(command.bookingId()).map(booking -> {
            if (!booking.getLeaderId().equals(command.leaderId())) throw new RuntimeException("Solo el líder puede bloquear");

            booking.generatePinAndLock();
            routesIntegration.updateVisibility(booking.getRouteId(), "HIDDEN", command.token());

            return bookingRepository.save(booking);
        });
    }

    @Override
    public Optional<Booking> handle(UpdatePaymentCommand command) {
        return bookingRepository.findById(command.bookingId()).map(booking -> {
            for (Passenger p : booking.getPassengers()) {
                if (p.getStudentId().equals(command.passengerId())) {
                    p.setPaymentStatus(PaymentStatus.PAID);
                    p.setPaymentMethod(command.method());
                }
            }
            return bookingRepository.save(booking);
        });
    }

    @Override
    public void handle(CancelBookingCommand command) {
        bookingRepository.findById(command.bookingId()).ifPresent(booking -> {
            if (!booking.getLeaderId().equals(command.leaderId())) throw new RuntimeException("Solo el líder puede cancelar");

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            routesIntegration.deleteRoute(booking.getRouteId(), command.token());
        });
    }
}