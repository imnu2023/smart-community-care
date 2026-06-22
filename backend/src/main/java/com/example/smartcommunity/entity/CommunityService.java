package com.example.smartcommunity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("community_service")
public class CommunityService {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String serviceName;

    private String serviceType;

    private String description;

    // 修复精度丢失问题
    private BigDecimal price;

    private String provider;

    private String phone;

    private String status;

    private Integer sortOrder;

    @TableLogic
    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}