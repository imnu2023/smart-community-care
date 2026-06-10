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
@TableName("service_order")
public class ServiceOrder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long serviceId;
    
    private String status;
    
    private LocalDateTime serviceTime;
    
    private String address;
    
    private String remark;
    
    private Double amount;
    
    private String paymentStatus;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}