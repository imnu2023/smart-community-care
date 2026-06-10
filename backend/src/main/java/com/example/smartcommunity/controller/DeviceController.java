package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.request.DeviceControlRequest;
import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.Device;
import com.example.smartcommunity.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {
    
    private final DeviceService deviceService;
    
    @GetMapping
    public ApiResponse<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        return ApiResponse.success(devices);
    }
    
    @PostMapping
    public ApiResponse<Device> addDevice(@RequestBody Device device) {
        Device saved = deviceService.addDevice(device);
        return ApiResponse.success(saved);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        device.setId(id);
        Device updated = deviceService.updateDevice(device);
        return ApiResponse.success(updated);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ApiResponse.success("删除成功");
    }
    
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Device>> getDevicesByUserId(@PathVariable Long userId) {
        List<Device> devices = deviceService.getDevicesByUserId(userId);
        return ApiResponse.success(devices);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        return ApiResponse.success(device);
    }
    
    @PostMapping("/{deviceId}/control")
    public ApiResponse<Void> controlDevice(@PathVariable Long deviceId, @RequestBody DeviceControlRequest request) {
        deviceService.controlDevice(deviceId, request.getAction());
        return ApiResponse.success("控制成功");
    }
}