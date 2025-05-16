package com.example.samuraitravel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    
    @NotEmpty(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    
    private String password;
    
    @NotEmpty(message = "First name is required")
    private String firstName;
    
    @NotEmpty(message = "Last name is required")
    private String lastName;
    
    private String phoneNumber;
}
