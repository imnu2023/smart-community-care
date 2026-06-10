package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.HealthData;
import com.example.smartcommunity.service.HealthDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthDataController {
    
    private final HealthDataService healthDataService;
    
    @GetMapping
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("Health service is running");
    }
    
    @PostMapping
    public ApiResponse<HealthData> saveHealthData(@RequestBody HealthData healthData) {
        HealthData saved = healthDataService.saveHealthData(healthData);
        return ApiResponse.success(saved);
    }
    
    @GetMapping("/weekly/{userId}")
    public ApiResponse<List<HealthData>> getWeeklyData(@PathVariable Long userId) {
        List<HealthData> data = healthDataService.getWeeklyData(userId);
        return ApiResponse.success(data);
    }
    
    @GetMapping("/warning/{userId}")
    public ApiResponse<List<HealthData>> getWarningData(@PathVariable Long userId) {
        List<HealthData> data = healthDataService.getWarningData(userId);
        return ApiResponse.success(data);
    }
    
    @GetMapping("/latest/{userId}")
    public ApiResponse<HealthData> getLatestData(@PathVariable Long userId) {
        HealthData data = healthDataService.getLatestData(userId);
        return ApiResponse.success(data);
    }
    
    @GetMapping("/analyze/{userId}")
    public ApiResponse<String> analyzeHealth(@PathVariable Long userId) {
        String report = healthDataService.analyzeHealth(userId);
        return ApiResponse.success("success", report);
    }
    
    @PostMapping("/report/{userId}")
    public ApiResponse<Void> generateReport(@PathVariable Long userId) {
        healthDataService.generateHealthReport(userId);
        return ApiResponse.success("报告生成成功");
    }
}