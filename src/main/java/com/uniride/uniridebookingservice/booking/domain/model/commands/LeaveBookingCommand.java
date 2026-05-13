package com.uniride.uniridebookingservice.booking.domain.model.commands;

public record LeaveBookingCommand(
        Long bookingId,
        Long studentId,
        Double lat,
        Double lng,
        String token
) {}