package com.example.samuraitravel.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    
    private Long id;
    
    private Long userId;
    
    @NotNull(message = "民宿を選択してください")
    private Long houseId;
    
    @NotNull(message = "チェックイン日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;
    
    @NotNull(message = "チェックアウト日は必須です")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
    
    @NotNull(message = "宿泊人数は必須です")
    @Min(value = 1, message = "宿泊人数は1人以上で入力してください")
    private Integer numberOfPeople;
    
    private Integer amount;
    
    private String specialRequirements;
    
    private String status;
}
