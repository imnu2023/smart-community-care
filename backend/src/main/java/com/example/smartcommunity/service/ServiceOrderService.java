package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.ServiceOrder;

import java.util.List;

public interface ServiceOrderService {
    
    ServiceOrder createOrder(Long userId, Long serviceId, String serviceTime, String address, String remark);
    
    ServiceOrder getOrderById(Long id);
    
    List<ServiceOrder> getOrdersByUserId(Long userId);
    
    List<ServiceOrder> getOrdersByStatus(String status);
    
    void updateOrderStatus(Long orderId, String status);
    
    void cancelOrder(Long orderId);
    
    List<ServiceOrder> getAllOrders();
}