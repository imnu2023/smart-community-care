package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.Device;

import java.util.List;

public interface DeviceService {
    
    Device addDevice(Device device);
    
    Device updateDevice(Device device);
    
    void deleteDevice(Long id);
    
    List<Device> getDevicesByUserId(Long userId);
    
    Device getDeviceById(Long id);
    
    void updateDeviceStatus(String deviceId, String status, Double value);
    
    void controlDevice(Long deviceId, String action);
    
    List<Device> getAllDevices();
}