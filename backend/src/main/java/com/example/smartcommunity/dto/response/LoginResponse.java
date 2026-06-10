package com.example.smartcommunity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    
    private Long userId;
    
    private String username;
    
    private String name;
    
    private Integer role;
    
    private String phone;
}