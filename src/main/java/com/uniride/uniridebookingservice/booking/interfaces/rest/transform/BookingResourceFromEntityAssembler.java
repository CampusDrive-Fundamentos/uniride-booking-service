package com.uniride.uniridebookingservice.booking.interfaces.rest.transform;
import com.uniride.uniridebookingservice.booking.domain.model.aggregates.Booking;
import com.uniride.uniridebookingservice.booking.interfaces.rest.resources.BookingResource;
import com.uniride.uniridebookingservice.booking.interfaces.rest.resources.PassengerResource;
import java.util.stream.Collectors;

public class BookingResourceFromEntityAssembler {
    public static BookingResource toResourceFromEntity(Booking entity) {
        return new BookingResource(
                entity.getId(),
                entity.getLeaderId(),
                entity.getRouteId(),
                entity.getStatus().name(),
                entity.getSecurityPin(),
                entity.getPassengers().stream()
                        .map(p -> new PassengerResource(p.getStudentId(), p.getRole().name(), p.getPaymentStatus().name(), p.getPaymentMethod()))
                        .collect(Collectors.toList())
        );
    }
}