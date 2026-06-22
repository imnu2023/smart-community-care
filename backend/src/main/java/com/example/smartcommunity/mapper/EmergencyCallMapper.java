package com.example.smartcommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartcommunity.entity.EmergencyCall;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmergencyCallMapper extends BaseMapper<EmergencyCall> {        

    @Select("SELECT * FROM emergency_call WHERE user_id = #{userId} ORDER BY call_time DESC")
    List<EmergencyCall> findByUserId(Long userId);

    @Select("SELECT * FROM emergency_call WHERE status IN ('pending', 'responding') ORDER BY call_time DESC")
    List<EmergencyCall> findUnresolvedCalls();
}