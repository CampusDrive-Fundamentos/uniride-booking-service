package com.uniride.uniridebookingservice.booking.interfaces.rest;

import com.uniride.uniridebookingservice.booking.domain.model.commands.*;
import com.uniride.uniridebookingservice.booking.domain.model.queries.GetActiveBookingByPassengerIdQuery;
import com.uniride.uniridebookingservice.booking.domain.model.queries.GetBookingByIdQuery;
import com.uniride.uniridebookingservice.booking.domain.model.queries.SearchNearbyBookingsQuery;
import com.uniride.uniridebookingservice.booking.domain.services.BookingCommandService;
import com.uniride.uniridebookingservice.booking.domain.services.BookingQueryService;
import com.uniride.uniridebookingservice.booking.interfaces.rest.resources.BookingResource;
import com.uniride.uniridebookingservice.booking.interfaces.rest.resources.PassengerResource;
import com.uniride.uniridebookingservice.booking.interfaces.rest.resources.WaypointRequest;
import com.uniride.uniridebookingservice.booking.interfaces.rest.transform.BookingResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Reservation, OTP and Payments Management")
public class BookingsController {

    private final BookingCommandService bookingCommandService;
    private final BookingQueryService bookingQueryService;

    public BookingsController(BookingCommandService bookingCommandService, BookingQueryService bookingQueryService) {
        this.bookingCommandService = bookingCommandService;
        this.bookingQueryService = bookingQueryService;
    }

    private Long getUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        return userId != null ? Long.parseLong(userId.toString()) : 1L;
    }

    private String extractToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    @GetMapping("/current")
    @Operation(summary = "Obtiene la reserva activa del usuario logueado")
    public ResponseEntity<BookingResource> getCurrentBooking(HttpServletRequest request) {
        return bookingQueryService.handle(new GetActiveBookingByPassengerIdQuery(getUserId(request)))
                .map(booking -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(booking)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Obtiene los detalles completos de una reserva (Usado por Finance y Trips)")
    public ResponseEntity<BookingResource> getBookingById(@PathVariable Long bookingId) {
        return bookingQueryService.handle(new GetBookingByIdQuery(bookingId))
                .map(booking -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(booking)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Busca grupos abiertos a 500m de distancia (Llama internamente a Routes)")
    public ResponseEntity<List<BookingResource>> searchNearbyBookings(
            @RequestParam String campus, @RequestParam Double lat, @RequestParam Double lng, HttpServletRequest request) {
        var query = new SearchNearbyBookingsQuery(campus, lat, lng, extractToken(request));
        var bookings = bookingQueryService.handle(query).stream()
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingId}/passengers")
    @Operation(summary = "Obtiene la lista de pasajeros y su estado de pago (Usado por el Líder durante el viaje)")
    public ResponseEntity<List<PassengerResource>> getBookingPassengers(@PathVariable Long bookingId) {
        return bookingQueryService.handle(new GetBookingByIdQuery(bookingId))
                .map(booking -> {
                    var passengers = booking.getPassengers().stream()
                            .map(p -> new PassengerResource(
                                    p.getStudentId(),
                                    p.getRole().name(),
                                    p.getPaymentStatus().name(),
                                    p.getPaymentMethod()))
                            .toList();
                    return ResponseEntity.ok(passengers);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{bookingId}/validate-pin")
    @Operation(summary = "Valida si el PIN del taxista es correcto (Usado por Trips para iniciar el viaje)")
    public ResponseEntity<Boolean> validatePin(@PathVariable Long bookingId, @RequestParam String pin) {
        return bookingQueryService.handle(new GetBookingByIdQuery(bookingId))
                .map(booking -> {
                    boolean isValid = booking.getSecurityPin() != null && booking.getSecurityPin().equals(pin);
                    return ResponseEntity.ok(isValid);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Líder crea grupo asociado a una ruta")
    public ResponseEntity<BookingResource> createBooking(@RequestParam Long routeId, HttpServletRequest request) {
        return bookingCommandService.handle(new CreateBookingCommand(getUserId(request), routeId))
                .map(booking -> new ResponseEntity<>(BookingResourceFromEntityAssembler.toResourceFromEntity(booking), HttpStatus.CREATED))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/{bookingId}/join")
    @Operation(summary = "Seguidor se une al grupo (Requiere coordenadas para la parada)")
    public ResponseEntity<BookingResource> joinBooking(@PathVariable Long bookingId, @RequestBody WaypointRequest requestBody, HttpServletRequest request) {
        JoinBookingCommand command = new JoinBookingCommand(
                bookingId,
                getUserId(request),
                requestBody.lat(),
                requestBody.lng(),
                requestBody.address(),
                extractToken(request)
        );
        return bookingCommandService.handle(command)
                .map(booking -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(booking)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{bookingId}/leave")
    @Operation(summary = "Seguidor sale del grupo (Requiere sus coordenadas originales para borrar la parada)")
    public ResponseEntity<BookingResource> leaveBooking(
            @PathVariable Long bookingId, @RequestParam Double lat, @RequestParam Double lng, HttpServletRequest request) {
        LeaveBookingCommand command = new LeaveBookingCommand(bookingId, getUserId(request), lat, lng, extractToken(request));
        return bookingCommandService.handle(command)
                .map(booking -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(booking)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/{bookingId}/lock")
    @Operation(summary = "Líder bloquea el grupo y genera PIN")
    public ResponseEntity<BookingResource> lockBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        return bookingCommandService.handle(new LockBookingCommand(bookingId, getUserId(request), extractToken(request)))
                .map(booking -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(booking)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{bookingId}/passengers/{passengerId}/payment-status")
    @Operation(summary = "Actualiza Checklist de pagos (YAPE/PLIN/CASH)")
    public ResponseEntity<BookingResource> updatePayment(@PathVariable Long bookingId, @PathVariable Long passengerId, @RequestParam String method) {
        return bookingCommandService.handle(new UpdatePaymentCommand(bookingId, passengerId, method))
                .map(booking -> ResponseEntity.ok(BookingResourceFromEntityAssembler.toResourceFromEntity(booking)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Líder cancela el viaje")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId, HttpServletRequest request) {
        bookingCommandService.handle(new CancelBookingCommand(bookingId, getUserId(request), extractToken(request)));
        return ResponseEntity.noContent().build();
    }
}