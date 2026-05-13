package com.uniride.uniridebookingservice.booking.domain.model.entities;

import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.PassengerRole;
import com.uniride.uniridebookingservice.booking.domain.model.valueobjects.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "booking_passengers")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PassengerRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private String paymentMethod;

    public Passenger(Long studentId, PassengerRole role) {
        this.studentId = studentId;
        this.role = role;
        this.paymentStatus = PaymentStatus.PENDING;
    }
}