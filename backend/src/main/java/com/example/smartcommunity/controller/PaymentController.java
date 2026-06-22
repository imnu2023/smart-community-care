package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.ServiceOrder;
import com.example.smartcommunity.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ApiResponse<Map<String, Object>> payOrder(@RequestBody Map<String, Object> params) {
        Long orderId = Long.parseLong(params.get("orderId").toString());
        Long payerId = params.get("payerId") != null ? Long.parseLong(params.get("payerId").toString()) : null;
        Map<String, Object> result = paymentService.payOrder(orderId, payerId);
        return ApiResponse.success(result);
    }

    @PostMapping("/family-pay")
    public ApiResponse<Map<String, Object>> familyPayOrder(@RequestBody Map<String, Object> params) {
        Long orderId = Long.parseLong(params.get("orderId").toString());
        Long familyId = Long.parseLong(params.get("familyId").toString());
        Map<String, Object> result = paymentService.familyPayOrder(orderId, familyId);
        return ApiResponse.success(result);
    }

    @PostMapping("/confirm/{orderId}")
    public ApiResponse<String> confirmPayment(@PathVariable Long orderId) {
        paymentService.confirmPayment(orderId);
        return ApiResponse.success("付款已确认");
    }

    @GetMapping("/orders/need-confirm")
    public ApiResponse<List<ServiceOrder>> getOrdersNeedConfirm() {
        List<ServiceOrder> orders = paymentService.getOrdersNeedConfirm();
        return ApiResponse.success(orders);
    }

    @GetMapping("/elder-orders/{familyId}")
    public ApiResponse<List<ServiceOrder>> getElderOrders(@PathVariable Long familyId) {
        List<ServiceOrder> orders = paymentService.getElderOrders(familyId);
        return ApiResponse.success(orders);
    }
}
