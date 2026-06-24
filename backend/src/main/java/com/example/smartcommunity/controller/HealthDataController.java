package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.HealthData;
import com.example.smartcommunity.service.HealthDataService;
import com.example.smartcommunity.service.ai.DeepSeekService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthDataController {

    private static final Logger log = LoggerFactory.getLogger(HealthDataController.class);

    private final HealthDataService healthDataService;
    private final DeepSeekService deepSeekService;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping
    public ApiResponse<String> healthCheck() {
        return ApiResponse.success("Health service is running. DeepSeek key=" +
            (deepSeekService.getApiKeyPrefix()) + ", model=" + deepSeekService.getModel());
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

    /**
     * AI-powered streaming health analysis via DeepSeek.
     * Uses SseEmitter for server-sent events.
     */
    @GetMapping(value = "/analyze/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter analyzeHealthStream(@PathVariable Long userId) {
        SseEmitter emitter = new SseEmitter(60000L); // 60s timeout

        try {
            List<HealthData> data = healthDataService.getWeeklyData(userId);
            if (data.isEmpty()) {
                executor.execute(() -> {
                    try {
                        emitter.send("data: 暂无足够的健康数据进行分析\n\n");
                        emitter.send("data: [DONE]\n\n");
                        emitter.complete();
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                });
                return emitter;
            }

            String prompt = healthDataService.buildAiPrompt(data);

            executor.execute(() -> {
                try {
                    log.info("Starting DeepSeek for userId={}, prompt length={}", userId, prompt.length());
                    StringBuilder full = new StringBuilder();
                    deepSeekService.streamChat(SYSTEM_PROMPT, prompt, token -> full.append(token));
                    // 收完全部 token 后一次性发送，彻底避免"data:"碎片
                    emitter.send("data: " + full.toString() + "\n\n");
                    emitter.send("data: [DONE]\n\n");
                    emitter.complete();
                    log.info("DeepSeek completed for userId={}, response length={}", userId, full.length());
                } catch (Exception e) {
                    log.error("DeepSeek streaming error for userId={}: {}", userId, e.getMessage(), e);
                    try {
                        emitter.send("data: [ERROR] AI分析暂时不可用，请稍后重试：" + e.getMessage() + "\n\n");
                        emitter.send("data: [DONE]\n\n");
                        emitter.complete();
                    } catch (IOException ex) {
                        log.error("Failed to send SSE error event", ex);
                        emitter.completeWithError(ex);
                    }
                }
            });

            return emitter;
        } catch (Exception e) {
            log.error("Failed to init health analysis stream for userId={}: {}", userId, e.getMessage(), e);
            executor.execute(() -> {
                try {
                    emitter.send("data: [ERROR] 系统异常：" + e.getMessage() + "\n\n");
                    emitter.send("data: [DONE]\n\n");
                    emitter.complete();
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }
            });
            return emitter;
        }
    }

    private static final String SYSTEM_PROMPT =
        "你是一位资深老年健康管理顾问，服务于智慧社区养老平台。" +
        "请根据提供的健康数据，用温暖、专业、易理解的中文给出分析。\n" +
        "要求：\n" +
        "1. 先给出总体健康评价（1-2句）\n" +
        "2. 逐项分析趋势，指出值得关注的指标\n" +
        "3. 给出3条具体、可执行的健康建议\n" +
        "4. 语气亲切温暖，面向家属阅读\n" +
        "5. 总字数控制在250字以内\n" +
        "6. 用纯文本回复，不要用markdown格式";

    @PostMapping("/report/{userId}")
    public ApiResponse<Void> generateReport(@PathVariable Long userId) {
        healthDataService.generateHealthReport(userId);
        return ApiResponse.success("报告生成成功");
    }
}
