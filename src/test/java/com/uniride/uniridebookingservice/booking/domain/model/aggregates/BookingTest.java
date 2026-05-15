package com.uniride.uniridebookingservice.booking.domain.model.aggregates;

import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.BookingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    @Test
    @DisplayName("Should add follower and update status to FULL when limit reached")
    void shouldAddFollowerAndUpdateStatusToFull() {
        // Arrange
        Booking booking = new Booking(1L, 100L); // Leader is added automatically

        // Act
        booking.addFollower(2L);
        booking.addFollower(3L);
        boolean addedLast = booking.addFollower(4L);

        // Assert
        assertThat(addedLast).isTrue();
        assertThat(booking.getPassengers()).hasSize(4);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.FULL);
    }

    @Test
    @DisplayName("Should not add follower if already in the group")
    void shouldNotAddFollowerIfAlreadyInGroup() {
        // Arrange
        Booking booking = new Booking(1L, 100L);

        // Act
        boolean added = booking.addFollower(1L); // 1L is the leader

        // Assert
        assertThat(added).isFalse();
        assertThat(booking.getPassengers()).hasSize(1);
    }

    @Test
    @DisplayName("Should generate 4-digit PIN and lock booking")
    void shouldGeneratePinAndLockBooking() {
        // Arrange
        Booking booking = new Booking(1L, 100L);

        // Act
        booking.generatePinAndLock();

        // Assert
        assertThat(booking.getSecurityPin()).matches("\\d{4}");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.LOCKED);
    }
}
