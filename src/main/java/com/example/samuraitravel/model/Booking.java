package com.example.samuraitravel.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate checkInDate;
    
    @Column(nullable = false)
    private LocalDate checkOutDate;
    
    @Column(nullable = false)
    private int numberOfGuests;
    
    @Column(nullable = false)
    private BigDecimal totalPrice;
    
    @Column(nullable = false)
    private LocalDateTime bookingDateTime;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    
    private String specialRequests;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELED, COMPLETED
    }
}
