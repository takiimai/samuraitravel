package com.example.samuraitravel.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private int capacity;
    
    @Column(nullable = false)
    private BigDecimal pricePerNight;
    
    @Column(nullable = false)
    private int totalRooms;
    
    private String amenities;
    
    @ManyToOne
    @JoinColumn(name = "samuraitravel_id", nullable = false)
    private samuraitravel samuraitravel;
    
    @OneToMany(mappedBy = "room")
    private Set<Booking> bookings = new HashSet<>();
}

