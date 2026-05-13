package com.uniride.uniridebookingservice.booking.domain.model.commands;
public record LockBookingCommand(Long bookingId, Long leaderId, String token) {}