package com.example.samuraitravel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class samuraitravelDto {
    private Long id;
    
    @NotEmpty(message = "Name is required")
    private String name;
    
    @NotEmpty(message = "Description is required")
    private String description;
    
    @NotEmpty(message = "Address is required")
    private String address;
    
    private String city;
    
    private String prefecture;
    
    private String postalCode;
    
    private String phoneNumber;
    
    @Email(message = "Please provide a valid email address")
    private String email;
    
    private String website;
    
    private boolean active = true;
}
