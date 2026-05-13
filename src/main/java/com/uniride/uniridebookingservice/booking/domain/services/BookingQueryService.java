package com.uniride.uniridebookingservice.booking.domain.services;

import com.uniride.uniridebookingservice.booking.domain.model.aggregates.Booking;
import com.uniride.uniridebookingservice.booking.domain.model.queries.*;
import java.util.List;
import java.util.Optional;

public interface BookingQueryService {
    Optional<Booking> handle(GetActiveBookingByPassengerIdQuery query);
    Optional<Booking> handle(GetBookingByIdQuery query);
    List<Booking> handle(SearchNearbyBookingsQuery query); // <-- Esta línea es vital
}