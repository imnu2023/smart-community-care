package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.HealthData;

import java.util.List;

public interface HealthDataService {
    
    HealthData saveHealthData(HealthData healthData);
    
    List<HealthData> getWeeklyData(Long userId);
    
    List<HealthData> getWarningData(Long userId);
    
    HealthData getLatestData(Long userId);
    
    String analyzeHealth(Long userId);

    void generateHealthReport(Long userId);

    /** Build a structured AI prompt from weekly health data for DeepSeek. */
    String buildAiPrompt(List<HealthData> weeklyData);
}