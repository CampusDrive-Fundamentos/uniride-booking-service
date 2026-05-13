package com.uniride.uniridebookingservice.booking.interfaces.rest.resources;
import java.util.List;
public record BookingResource(Long id, Long leaderId, Long routeId, String status, String securityPin, List<PassengerResource> passengers) {}