package com.example.samuraitravel.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
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
@Table(name = "samuraitravels")
public class samuraitravel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String address;
    
    private String city;
    
    private String prefecture;
    
    private String postalCode;
    
    private String phoneNumber;
    
    private String email;
    
    private String website;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @OneToMany(mappedBy = "samuraitravel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Room> rooms = new HashSet<>();
    
    @OneToMany(mappedBy = "samuraitravel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<samuraitravelImage> images = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
