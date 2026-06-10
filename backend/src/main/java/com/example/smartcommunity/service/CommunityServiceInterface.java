package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.CommunityService;

import java.util.List;

public interface CommunityServiceInterface {
    
    CommunityService addService(CommunityService service);
    
    CommunityService updateService(CommunityService service);
    
    void deleteService(Long id);
    
    List<CommunityService> getAllServices();
    
    List<CommunityService> getServicesByType(String type);
    
    CommunityService getServiceById(Long id);
}