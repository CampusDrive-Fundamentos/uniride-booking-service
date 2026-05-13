package com.uniride.uniridebookingservice.booking.interfaces.rest.resources;

public record WaypointRequest(Double lat, Double lng, String address) {
}