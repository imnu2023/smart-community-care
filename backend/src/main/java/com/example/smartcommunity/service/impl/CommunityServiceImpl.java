package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.CommunityService;
import com.example.smartcommunity.mapper.CommunityServiceMapper;
import com.example.smartcommunity.service.CommunityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityServiceInterface {
    
    private final CommunityServiceMapper communityServiceMapper;
    
    @Override
    public CommunityService addService(CommunityService service) {
        service.setStatus("active");
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        communityServiceMapper.insert(service);
        return service;
    }
    
    @Override
    public CommunityService updateService(CommunityService service) {
        service.setUpdatedAt(LocalDateTime.now());
        communityServiceMapper.updateById(service);
        return service;
    }
    
    @Override
    public void deleteService(Long id) {
        communityServiceMapper.deleteById(id);
    }
    
    @Override
    public List<CommunityService> getAllServices() {
        return communityServiceMapper.findAllActive();
    }
    
    @Override
    public List<CommunityService> getServicesByType(String type) {
        return communityServiceMapper.findByType(type);
    }
    
    @Override
    public CommunityService getServiceById(Long id) {
        return communityServiceMapper.selectById(id);
    }
}