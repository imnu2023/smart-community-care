package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.ServiceOrder;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    Map<String, Object> payOrder(Long orderId, Long payerId);
    Map<String, Object> familyPayOrder(Long orderId, Long familyId);
    void confirmPayment(Long orderId);
    List<ServiceOrder> getOrdersNeedConfirm();
    List<ServiceOrder> getElderOrders(Long familyId);
}
