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
@TableName("device")
public class Device {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String deviceName;
    
    private String deviceType;
    
    private String deviceId;
    
    private String status;
    
    private Double deviceValue;
    
    private String location;
    
    private LocalDateTime lastOnlineTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}