package com.example.samuraitravel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "名前は必須です")
    private String name;
    
    @NotBlank(message = "ふりがなは必須です")
    private String furigana;
    
    private String postalCode;
    
    private String address;
    
    private String phoneNumber;
    
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;
    
    private String password;
    
    private Long roleId;
    
    private boolean enabled = true;
}
