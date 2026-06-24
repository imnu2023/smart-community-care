package com.example.smartcommunity.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponseEncodingInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 不要覆盖特殊 Content-Type（如 SSE text/event-stream）
        if (response.getContentType() == null) {
            response.setContentType("application/json;charset=UTF-8");
        }
        return true;
    }
}
