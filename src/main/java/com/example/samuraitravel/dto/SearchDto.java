package com.example.samuraitravel.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
    private String prefecture;
    private String city;
    private String keyword;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int guestCount;
}
