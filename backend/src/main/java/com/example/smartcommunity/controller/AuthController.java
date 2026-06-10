package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.request.LoginRequest;
import com.example.smartcommunity.dto.request.RegisterRequest;
import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.dto.response.LoginResponse;
import com.example.smartcommunity.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/hash/{password}")
    public ApiResponse<String> hashPassword(@PathVariable String password) {
        return ApiResponse.success(passwordEncoder.encode(password));
    }
    
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ApiResponse.success(response);
    }
    
    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.success("注册成功");
    }
}