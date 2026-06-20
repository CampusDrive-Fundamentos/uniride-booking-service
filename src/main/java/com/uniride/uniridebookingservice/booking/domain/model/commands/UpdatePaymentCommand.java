package com.uniride.uniridebookingservice.booking.domain.model.commands;

public record UpdatePaymentCommand(Long bookingId, Long passengerId, String method) {}