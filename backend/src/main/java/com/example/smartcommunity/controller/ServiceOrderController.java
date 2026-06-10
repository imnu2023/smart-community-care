package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.request.OrderCreateRequest;
import com.example.smartcommunity.dto.request.OrderStatusRequest;
import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.ServiceOrder;
import com.example.smartcommunity.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ServiceOrderController {
    
    private final ServiceOrderService serviceOrderService;
    
    @GetMapping
    public ApiResponse<List<ServiceOrder>> getAllOrders() {
        List<ServiceOrder> orders = serviceOrderService.getAllOrders();
        return ApiResponse.success(orders);
    }
    
    @PostMapping
    public ApiResponse<ServiceOrder> createOrder(@RequestBody OrderCreateRequest request) {
        ServiceOrder order = serviceOrderService.createOrder(
                request.getUserId(), 
                request.getServiceId(), 
                request.getServiceTime(), 
                request.getAddress(), 
                request.getRemark()
        );
        return ApiResponse.success(order);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<ServiceOrder> getOrderById(@PathVariable Long id) {
        ServiceOrder order = serviceOrderService.getOrderById(id);
        return ApiResponse.success(order);
    }
    
    @GetMapping("/user/{userId}")
    public ApiResponse<List<ServiceOrder>> getOrdersByUserId(@PathVariable Long userId) {
        List<ServiceOrder> orders = serviceOrderService.getOrdersByUserId(userId);
        return ApiResponse.success(orders);
    }
    
    @GetMapping("/status/{status}")
    public ApiResponse<List<ServiceOrder>> getOrdersByStatus(@PathVariable String status) {
        List<ServiceOrder> orders = serviceOrderService.getOrdersByStatus(status);
        return ApiResponse.success(orders);
    }
    
    @PutMapping("/{orderId}/status")
    public ApiResponse<Void> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusRequest request) {
        serviceOrderService.updateOrderStatus(orderId, request.getStatus());
        return ApiResponse.success("状态更新成功");
    }
    
    @PutMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable Long orderId) {
        serviceOrderService.cancelOrder(orderId);
        return ApiResponse.success("已取消");
    }
}