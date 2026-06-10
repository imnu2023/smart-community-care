package com.example.smartcommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartcommunity.entity.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ServiceOrderMapper extends BaseMapper<ServiceOrder> {

    @Select("SELECT * FROM service_order WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<ServiceOrder> findByUserId(Long userId);

    @Select("SELECT * FROM service_order WHERE status = #{status} ORDER BY created_at DESC")
    List<ServiceOrder> findByStatus(String status);
}