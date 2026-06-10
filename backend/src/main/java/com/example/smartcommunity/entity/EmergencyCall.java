package com.example.smartcommunity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("emergency_call")
public class EmergencyCall {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String callType;
    
    private String status;
    
    private Double latitude;
    
    private Double longitude;
    
    private String description;
    
    private String handler;
    
    private LocalDateTime callTime;
    
    private LocalDateTime responseTime;
    
    private LocalDateTime resolvedTime;
    
    private LocalDateTime createdAt;
}