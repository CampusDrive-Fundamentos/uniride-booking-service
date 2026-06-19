package com.uniride.uniridebookingservice.booking.domain.model.aggregates;

import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Booking booking;

    @BeforeEach
    void setUp() {
        // Inicializamos un viaje con un líder (ID 1) y una ruta (ID 100)
        booking = new Booking(1L, 100L);
    }

    @Test
    void testAddFollower() {
        // AHORA PASAMOS LA DISTANCIA SIMULADA EN EL TEST
        assertTrue(booking.addFollower(2L, 3.5)); // Pasajero 2 a 3.5km
        assertTrue(booking.addFollower(3L, 5.0)); // Pasajero 3 a 5.0km
        assertTrue(booking.addFollower(4L, 8.2)); // Pasajero 4 a 8.2km

        // El grupo debería estar lleno (Líder + 3 seguidores = 4 pasajeros)
        assertEquals(BookingStatus.FULL, booking.getStatus());

        // No debería dejar agregar un 5to pasajero
        assertFalse(booking.addFollower(5L, 10.0));
    }

    @Test
    void testRemoveFollower() {
        // Agregamos y luego removemos
        booking.addFollower(2L, 5.0);
        booking.removeFollower(2L);

        // Solo debería quedar el líder en la lista
        assertEquals(1, booking.getPassengers().size());
        assertEquals(BookingStatus.OPEN, booking.getStatus());
    }
}