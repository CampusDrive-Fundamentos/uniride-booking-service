package com.uniride.uniridebookingservice.booking.domain.model.aggregates;

import com.uniride.uniridebookingservice.booking.domain.model.entities.Passenger;
import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.BookingStatus;
import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.PassengerRole;
import com.uniride.uniridebookingservice.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bookings")
public class Booking extends AuditableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long leaderId;

    @Column(nullable = false)
    private Long routeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    private String securityPin;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "booking_id")
    private List<Passenger> passengers = new ArrayList<>();

    public Booking(Long leaderId, Long routeId) {
        this.leaderId = leaderId;
        this.routeId = routeId;
        this.status = BookingStatus.OPEN;
        this.passengers.add(new Passenger(leaderId, PassengerRole.LEADER));
        this.securityPin = String.format("%04d", new Random().nextInt(10000));
    }

    public boolean addFollower(Long studentId) {
        if (this.passengers.size() >= 4) return false;
        if (this.passengers.stream().anyMatch(p -> p.getStudentId().equals(studentId))) return false;

        this.passengers.add(new Passenger(studentId, PassengerRole.FOLLOWER));

        if (this.passengers.size() == 4) this.status = BookingStatus.FULL;
        return true;
    }

    public void removeFollower(Long studentId) {
        this.passengers.removeIf(p -> p.getStudentId().equals(studentId) && p.getRole() == PassengerRole.FOLLOWER);
        if (this.status == BookingStatus.FULL) this.status = BookingStatus.OPEN;
    }

    public void generatePinAndLock() {
        this.securityPin = String.format("%04d", new Random().nextInt(10000));
        this.status = BookingStatus.LOCKED;
    }
}