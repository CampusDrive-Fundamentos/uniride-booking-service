package com.uniride.uniridebookingservice.booking.application.internal.queryservices;

import com.uniride.uniridebookingservice.booking.application.outboundservices.routes.RoutesServiceIntegration;
import com.uniride.uniridebookingservice.booking.domain.model.aggregates.Booking;
import com.uniride.uniridebookingservice.booking.domain.model.queries.GetActiveBookingByPassengerIdQuery;
import com.uniride.uniridebookingservice.booking.domain.model.queries.GetBookingByIdQuery;
import com.uniride.uniridebookingservice.booking.domain.model.queries.SearchNearbyBookingsQuery;
import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.BookingStatus;
import com.uniride.uniridebookingservice.booking.domain.services.BookingQueryService;
import com.uniride.uniridebookingservice.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BookingQueryServiceImpl implements BookingQueryService {
    private final BookingRepository bookingRepository;
    private final RoutesServiceIntegration routesIntegration;

    // ¡Aquí está el constructor corregido que inicializa ambas variables!
    public BookingQueryServiceImpl(BookingRepository bookingRepository, RoutesServiceIntegration routesIntegration) {
        this.bookingRepository = bookingRepository;
        this.routesIntegration = routesIntegration;
    }

    @Override
    public Optional<Booking> handle(GetActiveBookingByPassengerIdQuery query) {
        return bookingRepository.findActiveBookingByPassengerId(query.passengerId());
    }

    @Override
    public Optional<Booking> handle(GetBookingByIdQuery query) {
        return bookingRepository.findById(query.bookingId());
    }

    @Override
    public List<Booking> handle(SearchNearbyBookingsQuery query) {
        // 1. Preguntamos a Routes qué mapas están a 500 metros
        List<Long> nearbyRouteIds = routesIntegration.searchNearbyRouteIds(query.campus(), query.lat(), query.lng(), query.token());

        if (nearbyRouteIds.isEmpty()) return Collections.emptyList();

        // 2. Devolvemos solo las reservas que usan esos mapas y que aún tienen asientos (OPEN)
        return bookingRepository.findByRouteIdInAndStatus(nearbyRouteIds, BookingStatus.OPEN);
    }
}