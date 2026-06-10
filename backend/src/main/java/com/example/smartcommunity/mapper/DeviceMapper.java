package com.example.smartcommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartcommunity.entity.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {

    @Select("SELECT * FROM device WHERE user_id = #{userId}")
    List<Device> findByUserId(Long userId);

    @Select("SELECT * FROM device WHERE device_id = #{deviceId}")
    Device findByDeviceId(String deviceId);
}