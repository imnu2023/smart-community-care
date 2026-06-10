package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.EmergencyCall;

import java.util.List;

public interface EmergencyService {
    
    EmergencyCall createEmergencyCall(Long userId, String callType, Double latitude, Double longitude, String description);
    
    EmergencyCall getEmergencyCallById(Long id);
    
    List<EmergencyCall> getEmergencyCallsByUserId(Long userId);
    
    List<EmergencyCall> getUnresolvedCalls();
    
    void respondToCall(Long callId, String handler);
    
    void resolveCall(Long callId);
    
    void cancelCall(Long callId);
}