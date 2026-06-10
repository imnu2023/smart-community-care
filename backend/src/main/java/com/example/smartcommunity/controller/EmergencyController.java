package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.request.EmergencyCallRequest;
import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.EmergencyCall;
import com.example.smartcommunity.service.EmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
public class EmergencyController {
    
    private final EmergencyService emergencyService;
    
    @PostMapping("/call")
    public ApiResponse<EmergencyCall> createEmergencyCall(@RequestBody EmergencyCallRequest request) {
        EmergencyCall call = emergencyService.createEmergencyCall(
                request.getUserId(), 
                request.getCallType(), 
                request.getLatitude(), 
                request.getLongitude(), 
                request.getDescription()
        );
        return ApiResponse.success(call);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<EmergencyCall> getEmergencyCallById(@PathVariable Long id) {
        EmergencyCall call = emergencyService.getEmergencyCallById(id);
        return ApiResponse.success(call);
    }
    
    @GetMapping("/user/{userId}")
    public ApiResponse<List<EmergencyCall>> getEmergencyCallsByUserId(@PathVariable Long userId) {
        List<EmergencyCall> calls = emergencyService.getEmergencyCallsByUserId(userId);
        return ApiResponse.success(calls);
    }
    
    @GetMapping("/unresolved")
    public ApiResponse<List<EmergencyCall>> getUnresolvedCalls() {
        List<EmergencyCall> calls = emergencyService.getUnresolvedCalls();
        return ApiResponse.success(calls);
    }
    
    @PutMapping("/{callId}/respond")
    public ApiResponse<Void> respondToCall(@PathVariable Long callId, @RequestParam String handler) {
        emergencyService.respondToCall(callId, handler);
        return ApiResponse.success("鍝嶅簲鎴愬姛");
    }
    
    @PutMapping("/{callId}/resolve")
    public ApiResponse<Void> resolveCall(@PathVariable Long callId) {
        emergencyService.resolveCall(callId);
        return ApiResponse.success("澶勭悊瀹屾垚");
    }
    
    @PutMapping("/{callId}/cancel")
    public ApiResponse<Void> cancelCall(@PathVariable Long callId) {
        emergencyService.cancelCall(callId);
        return ApiResponse.success("取消成功");
    }
}