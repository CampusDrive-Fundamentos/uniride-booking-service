package com.uniride.uniridebookingservice.booking.domain.model.commands;

public record JoinBookingCommand(
        Long bookingId,
        Long studentId,
        Double lat,
        Double lng,
        String address,
        String token
) {}