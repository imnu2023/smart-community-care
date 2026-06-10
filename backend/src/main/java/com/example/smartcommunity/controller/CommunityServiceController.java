package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.CommunityService;
import com.example.smartcommunity.service.CommunityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class CommunityServiceController {
    
    private final CommunityServiceInterface communityServiceInterface;
    
    @PostMapping
    public ApiResponse<CommunityService> addService(@RequestBody CommunityService service) {
        CommunityService saved = communityServiceInterface.addService(service);
        return ApiResponse.success(saved);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<CommunityService> updateService(@PathVariable Long id, @RequestBody CommunityService service) {
        service.setId(id);
        CommunityService updated = communityServiceInterface.updateService(service);
        return ApiResponse.success(updated);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteService(@PathVariable Long id) {
        communityServiceInterface.deleteService(id);
        return ApiResponse.success("删除成功");
    }
    
    @GetMapping
    public ApiResponse<List<CommunityService>> getAllServices() {
        List<CommunityService> services = communityServiceInterface.getAllServices();
        return ApiResponse.success(services);
    }
    
    @GetMapping("/type/{type}")
    public ApiResponse<List<CommunityService>> getServicesByType(@PathVariable String type) {
        List<CommunityService> services = communityServiceInterface.getServicesByType(type);
        return ApiResponse.success(services);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<CommunityService> getServiceById(@PathVariable Long id) {
        CommunityService service = communityServiceInterface.getServiceById(id);
        return ApiResponse.success(service);
    }
}