package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.EmergencyCall;
import com.example.smartcommunity.mapper.EmergencyCallMapper;
import com.example.smartcommunity.service.EmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmergencyServiceImpl implements EmergencyService {
    
    private final EmergencyCallMapper emergencyCallMapper;
    
    @Override
    public EmergencyCall createEmergencyCall(Long userId, String callType, Double latitude, Double longitude, String description) {
        EmergencyCall call = new EmergencyCall();
        call.setUserId(userId);
        call.setCallType(callType);
        call.setStatus("pending");
        call.setLatitude(latitude);
        call.setLongitude(longitude);
        call.setDescription(description);
        call.setCallTime(LocalDateTime.now());
        call.setCreatedAt(LocalDateTime.now());
        emergencyCallMapper.insert(call);
        return call;
    }
    
    @Override
    public EmergencyCall getEmergencyCallById(Long id) {
        return emergencyCallMapper.selectById(id);
    }
    
    @Override
    public List<EmergencyCall> getEmergencyCallsByUserId(Long userId) {
        return emergencyCallMapper.findByUserId(userId);
    }
    
    @Override
    public List<EmergencyCall> getUnresolvedCalls() {
        return emergencyCallMapper.findUnresolvedCalls();
    }
    
    @Override
    public void respondToCall(Long callId, String handler) {
        EmergencyCall call = emergencyCallMapper.selectById(callId);
        if (call != null) {
            call.setStatus("responding");
            call.setHandler(handler);
            call.setResponseTime(LocalDateTime.now());
            emergencyCallMapper.updateById(call);
        }
    }
    
    @Override
    public void resolveCall(Long callId) {
        EmergencyCall call = emergencyCallMapper.selectById(callId);
        if (call != null) {
            call.setStatus("resolved");
            call.setResolvedTime(LocalDateTime.now());
            emergencyCallMapper.updateById(call);
        }
    }
    
    @Override
    public void cancelCall(Long callId) {
        EmergencyCall call = emergencyCallMapper.selectById(callId);
        if (call != null) {
            call.setStatus("cancelled");
            emergencyCallMapper.updateById(call);
        }
    }
}