package com.example.samuraitravel.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.samuraitravel.dto.UserDto;
import com.example.samuraitravel.model.User;

public interface UserService extends UserDetailsService {
    
    User registerUser(UserDto userDto);
    
    User findById(Long id);
    
    User findByEmail(String email);
    
    List<User> findAllUsers();
    
    User updateUser(UserDto userDto);
    
    void deleteUser(Long id);
    
    boolean emailExists(String email);
    
    void createVerificationToken(User user);
    
    String validateVerificationToken(String token);
    
    void verifyUser(User user);
    
    void resendVerificationToken(String email);
}
