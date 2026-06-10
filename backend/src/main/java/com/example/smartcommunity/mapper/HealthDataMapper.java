package com.example.smartcommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartcommunity.entity.HealthData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HealthDataMapper extends BaseMapper<HealthData> {
    
    @Select("SELECT * FROM health_data WHERE user_id = #{userId} ORDER BY measured_at DESC LIMIT 7")
    List<HealthData> findWeeklyData(Long userId);
    
    @Select("SELECT * FROM health_data WHERE user_id = #{userId} AND warning_level > 0 ORDER BY measured_at DESC")
    List<HealthData> findWarningData(Long userId);
    
    @Select("SELECT * FROM health_data WHERE user_id = #{userId} AND measured_at BETWEEN #{startTime} AND #{endTime} ORDER BY measured_at DESC")
    List<HealthData> findByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}