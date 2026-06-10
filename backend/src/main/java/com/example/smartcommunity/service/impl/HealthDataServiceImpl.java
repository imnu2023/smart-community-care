package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.HealthData;
import com.example.smartcommunity.mapper.HealthDataMapper;
import com.example.smartcommunity.service.HealthDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthDataServiceImpl implements HealthDataService {
    
    private final HealthDataMapper healthDataMapper;
    
    @Override
    public HealthData saveHealthData(HealthData healthData) {
        healthData.setCreatedAt(LocalDateTime.now());
        healthData.setWarningLevel(analyzeWarningLevel(healthData));
        healthDataMapper.insert(healthData);
        return healthData;
    }
    
    @Override
    public List<HealthData> getWeeklyData(Long userId) {
        return healthDataMapper.findWeeklyData(userId);
    }
    
    @Override
    public List<HealthData> getWarningData(Long userId) {
        return healthDataMapper.findWarningData(userId);
    }
    
    @Override
    public HealthData getLatestData(Long userId) {
        List<HealthData> data = healthDataMapper.findWeeklyData(userId);
        return data.isEmpty() ? null : data.get(0);
    }
    
    @Override
    public String analyzeHealth(Long userId) {
        List<HealthData> data = getWeeklyData(userId);
        if (data.isEmpty()) {
            return "暂无足够的健康数据进行分析";
        }
        return generateHealthReport(data);
    }
    
    @Override
    public void generateHealthReport(Long userId) {
        List<HealthData> data = getWeeklyData(userId);
        if (!data.isEmpty()) {
            HealthData latest = data.get(0);
            latest.setHealthReport(generateHealthReport(data));
            healthDataMapper.updateById(latest);
        }
    }
    
    private Integer analyzeWarningLevel(HealthData data) {
        int level = 0;
        if (data.getHeartRate() != null) {
            if (data.getHeartRate() > 100 || data.getHeartRate() < 60) level++;
        }
        if (data.getBloodPressureHigh() != null && data.getBloodPressureHigh() > 140) level++;
        if (data.getBloodPressureLow() != null && data.getBloodPressureLow() > 90) level++;
        if (data.getBloodSugar() != null && data.getBloodSugar() > 7.0) level++;
        if (data.getBodyTemperature() != null && data.getBodyTemperature() > 37.5) level++;
        if (data.getSleepHours() != null && data.getSleepHours() < 6) level++;
        return level;
    }
    
    private String generateHealthReport(List<HealthData> data) {
        StringBuilder report = new StringBuilder("【健康分析报告】\n");
        report.append("分析周期：最近7天\n\n");
        
        int avgHeartRate = (int) data.stream().filter(d -> d.getHeartRate() != null)
                .mapToInt(HealthData::getHeartRate).average().orElse(0);
        report.append("平均心率：").append(avgHeartRate).append(" 次/分钟\n");
        
        double avgSystolic = data.stream().filter(d -> d.getBloodPressureHigh() != null)
                .mapToDouble(HealthData::getBloodPressureHigh).average().orElse(0);
        double avgDiastolic = data.stream().filter(d -> d.getBloodPressureLow() != null)
                .mapToDouble(HealthData::getBloodPressureLow).average().orElse(0);
        report.append("平均血压：").append(String.format("%.1f", avgSystolic))
                .append("/").append(String.format("%.1f", avgDiastolic)).append(" mmHg\n");
        
        double avgSleep = data.stream().filter(d -> d.getSleepHours() != null)
                .mapToInt(HealthData::getSleepHours).average().orElse(0);
        report.append("平均睡眠时长：").append(String.format("%.1f", avgSleep)).append(" 小时\n\n");
        
        report.append("【健康建议】\n");
        if (avgHeartRate > 100) report.append("- 心率偏高，建议适当休息和放松\n");
        if (avgSystolic > 130) report.append("- 血压偏高，建议低盐饮食\n");
        if (avgSleep < 7) report.append("- 睡眠不足，建议保证充足睡眠\n");
        
        return report.toString();
    }
}