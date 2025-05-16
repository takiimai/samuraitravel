package com.example.samuraitravel.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    
    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;
    
    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;
    
    @Min(value = 1, message = "Number of guests must be at least 1")
    private int numberOfGuests;
    
    private String specialRequests;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Room ID is required")
    private Long roomId;
}
