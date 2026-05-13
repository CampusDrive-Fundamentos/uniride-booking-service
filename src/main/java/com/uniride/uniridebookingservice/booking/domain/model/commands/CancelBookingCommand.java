package com.uniride.uniridebookingservice.booking.domain.model.commands;
public record CancelBookingCommand(Long bookingId, Long leaderId, String token) {}