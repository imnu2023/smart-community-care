package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.HealthData;
import com.example.smartcommunity.mapper.HealthDataMapper;
import com.example.smartcommunity.service.HealthDataService;
import com.example.smartcommunity.service.NotificationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthDataServiceImpl implements HealthDataService {

    private final HealthDataMapper healthDataMapper;

    private final NotificationHelper notificationHelper;

    @Override
    public HealthData saveHealthData(HealthData healthData) {
        healthData.setCreatedAt(LocalDateTime.now());
        int warningLevel = analyzeWarningLevel(healthData);
        healthData.setWarningLevel(warningLevel);
        healthDataMapper.insert(healthData);

        if (warningLevel > 0) {
            StringBuilder alert = new StringBuilder("健康数据异常提醒：");
            if (healthData.getHeartRate() != null && (healthData.getHeartRate() > 100 || healthData.getHeartRate() < 60))
                alert.append("心率").append(healthData.getHeartRate()).append("次/分 ");
            if (healthData.getBloodPressureHigh() != null && healthData.getBloodPressureHigh() > 140)
                alert.append("收缩压").append(String.format("%.0f", healthData.getBloodPressureHigh())).append(" ");
            if (healthData.getBloodPressureLow() != null && healthData.getBloodPressureLow() > 90)
                alert.append("舒张压").append(String.format("%.0f", healthData.getBloodPressureLow())).append(" ");
            if (healthData.getBloodSugar() != null && healthData.getBloodSugar().compareTo(BLOOD_SUGAR_THRESHOLD) > 0)
                alert.append("血糖").append(healthData.getBloodSugar()).append(" ");
            if (healthData.getBodyTemperature() != null && healthData.getBodyTemperature().compareTo(BODY_TEMP_THRESHOLD) > 0)
                alert.append("体温").append(healthData.getBodyTemperature()).append("℃ ");
            alert.append("，建议就医检查");
            notificationHelper.send(healthData.getUserId(), NotificationHelper.TYPE_HEALTH, alert.toString());
        }

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

    @Override
    public String buildAiPrompt(List<HealthData> weeklyData) {
        StringBuilder sb = new StringBuilder();
        sb.append("以下是老人近7天的健康监测数据，请分析：\n\n");

        // Heart rate
        sb.append("心率(bpm): [");
        weeklyData.forEach(d -> sb.append(d.getHeartRate() != null ? d.getHeartRate() : "-").append(","));
        sb.setLength(sb.length() - 1);
        sb.append("]\n");

        // Blood pressure
        sb.append("血压(mmHg): [");
        weeklyData.forEach(d -> {
            if (d.getBloodPressureHigh() != null && d.getBloodPressureLow() != null)
                sb.append(String.format("%.0f/%.0f", d.getBloodPressureHigh(), d.getBloodPressureLow()));
            else sb.append("-");
            sb.append(",");
        });
        sb.setLength(sb.length() - 1);
        sb.append("]\n");

        // Blood sugar
        sb.append("血糖(mmol/L): [");
        weeklyData.forEach(d -> sb.append(d.getBloodSugar() != null ? d.getBloodSugar() : "-").append(","));
        sb.setLength(sb.length() - 1);
        sb.append("]\n");

        // Sleep
        sb.append("睡眠(小时): [");
        weeklyData.forEach(d -> sb.append(d.getSleepHours() != null ? d.getSleepHours() : "-").append(","));
        sb.setLength(sb.length() - 1);
        sb.append("]\n");

        // Steps
        sb.append("步数: [");
        weeklyData.forEach(d -> sb.append(d.getSteps() != null ? d.getSteps() : "-").append(","));
        sb.setLength(sb.length() - 1);
        sb.append("]\n");

        // Temperature
        boolean hasTemp = weeklyData.stream().anyMatch(d -> d.getBodyTemperature() != null);
        if (hasTemp) {
            sb.append("体温(℃): [");
            weeklyData.forEach(d -> sb.append(d.getBodyTemperature() != null ? d.getBodyTemperature() : "-").append(","));
            sb.setLength(sb.length() - 1);
            sb.append("]\n");
        }

        sb.append("\n请给出分析报告。");
        return sb.toString();
    }

    private static final BigDecimal BLOOD_SUGAR_THRESHOLD = new BigDecimal("7.0");
    private static final BigDecimal BODY_TEMP_THRESHOLD = new BigDecimal("37.5");

    private Integer analyzeWarningLevel(HealthData data) {
        int level = 0;
        if (data.getHeartRate() != null) {
            if (data.getHeartRate() > 100 || data.getHeartRate() < 60) level++;
        }
        if (data.getBloodPressureHigh() != null && data.getBloodPressureHigh() > 140) level++;
        if (data.getBloodPressureLow() != null && data.getBloodPressureLow() > 90) level++;
        if (data.getBloodSugar() != null && data.getBloodSugar().compareTo(BLOOD_SUGAR_THRESHOLD) > 0) level++;
        if (data.getBodyTemperature() != null && data.getBodyTemperature().compareTo(BODY_TEMP_THRESHOLD) > 0) level++;
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
        report.append("平均睡眠时长：").append(String.format("%.1f", avgSleep)).append(" 小时\n");
        
        double avgBloodSugar = data.stream().filter(d -> d.getBloodSugar() != null)
                .mapToDouble(d -> d.getBloodSugar().doubleValue()).average().orElse(0);
        report.append("平均血糖：").append(String.format("%.1f", avgBloodSugar)).append(" mmol/L\n\n");
        
        report.append("【健康建议】\n");
        boolean hasSpecificAdvice = false;
        
        if (avgHeartRate > 100) {
            report.append("- 心率偏高，建议适当休息，避免剧烈运动，保持心情舒畅\n");
            hasSpecificAdvice = true;
        } else if (avgHeartRate < 60 && avgHeartRate > 0) {
            report.append("- 心率偏低，建议适当进行轻度运动，如散步、太极拳等\n");
            hasSpecificAdvice = true;
        } else if (avgHeartRate > 0 && avgHeartRate >= 60 && avgHeartRate <= 100) {
            report.append("- 心率处于正常范围，继续保持规律作息和适度运动\n");
            hasSpecificAdvice = true;
        }
        
        if (avgSystolic > 140 || avgDiastolic > 90) {
            report.append("- 血压偏高，建议低盐饮食，减少油腻食物摄入，定期监测血压\n");
            hasSpecificAdvice = true;
        } else if (avgSystolic >= 120 && avgDiastolic >= 80) {
            report.append("- 血压处于正常高值，建议注意饮食健康，适当运动\n");
            hasSpecificAdvice = true;
        } else if (avgSystolic > 0 && avgDiastolic > 0) {
            report.append("- 血压控制良好，继续保持健康的生活方式\n");
            hasSpecificAdvice = true;
        }
        
        if (avgBloodSugar > 7.0) {
            report.append("- 血糖偏高，建议控制糖分摄入，遵循医生建议规律服药\n");
            hasSpecificAdvice = true;
        } else if (avgBloodSugar >= 6.1 && avgBloodSugar <= 7.0) {
            report.append("- 血糖处于临界值，建议注意饮食，定期复查\n");
            hasSpecificAdvice = true;
        } else if (avgBloodSugar > 0) {
            report.append("- 血糖控制在理想范围，继续保持健康饮食\n");
            hasSpecificAdvice = true;
        }
        
        if (avgSleep < 6) {
            report.append("- 睡眠不足，建议保证每晚7-8小时睡眠，睡前避免使用电子设备\n");
            hasSpecificAdvice = true;
        } else if (avgSleep >= 6 && avgSleep <= 9) {
            report.append("- 睡眠时长充足，继续保持良好的睡眠习惯\n");
            hasSpecificAdvice = true;
        }
        
        if (!hasSpecificAdvice) {
            report.append("- 建议定期监测健康数据，保持规律的生活作息\n");
            report.append("- 适当进行户外活动，保持积极乐观的心态\n");
            report.append("- 饮食均衡，多吃蔬菜水果，减少高盐高脂食物\n");
        }
        
        report.append("\n【温馨提示】如有不适请及时就医");
        
        return report.toString();
    }
}