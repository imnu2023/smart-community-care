package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.request.LoginRequest;
import com.example.smartcommunity.dto.request.RegisterRequest;
import com.example.smartcommunity.dto.response.LoginResponse;
import com.example.smartcommunity.entity.User;

import java.util.List;

public interface UserService {
    
    LoginResponse login(LoginRequest request);
    
    void register(RegisterRequest request);
    
    User getUserById(Long id);
    
    User getUserByUsername(String username);
    
    void updateUser(User user);
    
    void deleteUser(Long id);
    
    List<User> getAllUsers();
}