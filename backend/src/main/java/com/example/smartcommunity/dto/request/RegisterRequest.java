package com.example.smartcommunity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    private String username;
    
    private String password;
    
    private String name;
    
    private String phone;
    
    private String email;
    
    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }
    
    private Integer age;
    
    private String gender;
    
    private String address;
    
    private Integer role;
    
    private String emergencyContact;
    
    private String emergencyPhone;
}