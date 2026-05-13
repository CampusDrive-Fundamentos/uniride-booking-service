package com.uniride.uniridebookingservice.booking.domain.services;
import com.uniride.uniridebookingservice.booking.domain.model.aggregates.Booking;
import com.uniride.uniridebookingservice.booking.domain.model.commands.*;
import java.util.Optional;

public interface BookingCommandService {
    Optional<Booking> handle(CreateBookingCommand command);
    Optional<Booking> handle(JoinBookingCommand command);
    Optional<Booking> handle(LeaveBookingCommand command);
    Optional<Booking> handle(LockBookingCommand command);
    Optional<Booking> handle(UpdatePaymentCommand command);
    void handle(CancelBookingCommand command);
}