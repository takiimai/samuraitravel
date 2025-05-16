package com.example.samuraitravel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseDto {
    
    private Long id;
    
    @NotBlank(message = "名前は必須です")
    private String name;
    
    private MultipartFile imageFile;
    
    private String imageName;
    
    @NotBlank(message = "説明は必須です")
    private String description;
    
    @NotNull(message = "価格は必須です")
    @Min(value = 1, message = "価格は1円以上で入力してください")
    private Integer price;
    
    @NotNull(message = "定員は必須です")
    @Min(value = 1, message = "定員は1人以上で入力してください")
    private Integer capacity;
    
    private String postalCode;
    
    private String address;
    
    private String phoneNumber;
}
