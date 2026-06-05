package com.uniride.uniridebookingservice.booking.infrastructure.persistence.jpa.repositories;

import com.uniride.uniridebookingservice.booking.domain.model.aggregates.Booking;
import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN b.passengers p WHERE p.studentId = :userId AND b.status <> 'CANCELLED' ORDER BY b.id DESC")
    List<Booking> findActiveBookingByPassengerId(@Param("userId") Long userId);
    List<Booking> findByRouteIdInAndStatus(List<Long> routeIds, BookingStatus status);
}