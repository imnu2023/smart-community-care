package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.Device;
import com.example.smartcommunity.exception.BusinessException;
import com.example.smartcommunity.mapper.DeviceMapper;
import com.example.smartcommunity.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    
    private final DeviceMapper deviceMapper;
    
    @Override
    public Device addDevice(Device device) {
        device.setStatus("online");
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        deviceMapper.insert(device);
        return device;
    }
    
    @Override
    public Device updateDevice(Device device) {
        device.setUpdatedAt(LocalDateTime.now());
        deviceMapper.updateById(device);
        return device;
    }
    
    @Override
    public void deleteDevice(Long id) {
        deviceMapper.deleteById(id);
    }
    
    @Override
    public List<Device> getDevicesByUserId(Long userId) {
        return deviceMapper.findByUserId(userId);
    }
    
    @Override
    public Device getDeviceById(Long id) {
        return deviceMapper.selectById(id);
    }
    
    @Override
    public void updateDeviceStatus(String deviceId, String status, Double value) {
        Device device = deviceMapper.findByDeviceId(deviceId);
        if (device != null) {
            device.setStatus(status);
            device.setDeviceValue(value);
            device.setLastOnlineTime(LocalDateTime.now());
            device.setUpdatedAt(LocalDateTime.now());
            deviceMapper.updateById(device);
        }
    }
    
    @Override
    public void controlDevice(Long deviceId, String action) {
        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException("设备不存在");
        }
        device.setStatus(action.equals("on") ? "active" : "inactive");
        device.setUpdatedAt(LocalDateTime.now());
        deviceMapper.updateById(device);
    }
    
    @Override
    public List<Device> getAllDevices() {
        return deviceMapper.selectList(null);
    }
}