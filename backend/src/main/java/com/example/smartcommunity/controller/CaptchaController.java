package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @GetMapping("/captcha")
    public ApiResponse<Map<String, String>> getCaptcha() {
        Map<String, String> data = captchaService.generateCaptcha();
        return ApiResponse.success("获取成功", data);
    }
}
