package com.example.smartcommunity.dto.request;

import lombok.Data;

@Data
public class EmergencyCallRequest {
    
    private Long userId;
    private String callType;
    private Double latitude;
    private Double longitude;
    private String description;
}