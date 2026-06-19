package com.uniride.uniridebookingservice.booking.domain.model.aggregates;

import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking(1L, 100L);
    }

    @Test
    void testAddFollower() {
        assertTrue(booking.addFollower(2L));
        assertTrue(booking.addFollower(3L));
        assertTrue(booking.addFollower(4L));

        assertEquals(BookingStatus.FULL, booking.getStatus());

        assertFalse(booking.addFollower(5L));
    }

    @Test
    void testRemoveFollower() {
        booking.addFollower(2L);
        booking.removeFollower(2L);

        assertEquals(1, booking.getPassengers().size());
        assertEquals(BookingStatus.OPEN, booking.getStatus());
    }
}