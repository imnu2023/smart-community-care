package com.example.smartcommunity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    // 修复精度丢失：与数据库 decimal(5,2) 匹配
    private BigDecimal bloodSugar;

    // 修复精度丢失：与数据库 decimal(4,2) 匹配
    private BigDecimal bodyTemperature;

    private Integer sleepHours;

    private Integer sleepQuality;

    private Integer steps;

    // 修复精度丢失：与数据库 decimal(8,2) 匹配
    private BigDecimal calories;

    private String healthReport;

    private Integer warningLevel;

    private LocalDateTime measuredAt;

    private LocalDateTime createdAt;
}