package com.uniride.uniridebookingservice.booking.infrastructure.persistence.jpa.repositories;

import com.uniride.uniridebookingservice.booking.domain.model.aggregates.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN b.passengers p WHERE p.studentId = :userId AND b.status <> 'CANCELLED' AND b.status <> 'LOCKED'")
    Optional<Booking> findActiveBookingByPassengerId(@Param("userId") Long userId);
    // NUEVO: Busca reservas que pertenezcan a una lista de rutas (mapas) y que tengan espacio (OPEN)
    List<Booking> findByRouteIdInAndStatus(List<Long> routeIds, com.uniride.uniridebookingservice.booking.domain.model.valueobjects.BookingStatus status);
}