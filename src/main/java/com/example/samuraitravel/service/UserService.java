package com.example.samuraitravel.service;

import java.util.List;

import com.example.samuraitravel.dto.UserDto;
import com.example.samuraitravel.model.User;

public interface UserService {
    User registerNewUser(UserDto userDto);
    
    User findByEmail(String email);
    
    User findById(Long id);
    
    List<User> findAllUsers();
    
    User updateUser(UserDto userDto);
    
    void deleteUser(Long id);
    
    boolean emailExists(String email);
}
