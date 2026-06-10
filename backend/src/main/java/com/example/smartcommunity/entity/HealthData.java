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
@TableName("health_data")
public class HealthData {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Integer heartRate;
    
    private Double bloodPressureHigh;
    
    private Double bloodPressureLow;
    
    private Double bloodSugar;
    
    private Double bodyTemperature;
    
    private Integer sleepHours;
    
    private Integer sleepQuality;
    
    private Integer steps;
    
    private Double calories;
    
    private String healthReport;
    
    private Integer warningLevel;
    
    private LocalDateTime measuredAt;
    
    private LocalDateTime createdAt;
}