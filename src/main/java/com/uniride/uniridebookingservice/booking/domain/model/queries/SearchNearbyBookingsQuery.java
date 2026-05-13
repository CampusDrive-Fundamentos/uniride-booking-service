package com.uniride.uniridebookingservice.booking.domain.model.queries;

public record SearchNearbyBookingsQuery(String campus, Double lat, Double lng, String token) {
}