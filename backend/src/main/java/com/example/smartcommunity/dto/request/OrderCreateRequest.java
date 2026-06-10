package com.example.smartcommunity.dto.request;

import lombok.Data;

@Data
public class OrderCreateRequest {
    
    private Long userId;
    private Long serviceId;
    private String serviceTime;
    private String address;
    private String remark;
}