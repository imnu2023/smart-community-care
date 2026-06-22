package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.ServiceOrder;
import com.example.smartcommunity.entity.UserRelation;
import com.example.smartcommunity.mapper.ServiceOrderMapper;
import com.example.smartcommunity.mapper.UserRelationMapper;
import com.example.smartcommunity.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private ServiceOrderMapper orderMapper;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRelationMapper relationMapper;

    @PostMapping("/pay")
    public ApiResponse<Map<String, Object>> payOrder(@RequestBody Map<String, Object> params) {
        Long orderId = Long.parseLong(params.get("orderId").toString());
        Long payerId = params.get("payerId") != null ? Long.parseLong(params.get("payerId").toString()) : null;
        
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        
        if (!"pending".equals(order.getStatus())) {
            return ApiResponse.error("订单状态不允许支付");
        }
        
        BigDecimal amount = order.getAmount();
        boolean success = walletService.pay(payerId, amount, order.getOrderNo());
        
        Map<String, Object> result = new HashMap<>();
        if (success) {
            order.setPaymentStatus("paid");
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
            
            result.put("success", true);
            result.put("orderId", orderId);
            result.put("amount", order.getAmount());
            result.put("balance", walletService.getBalance(payerId));
        } else {
            result.put("success", false);
            result.put("message", "余额不足");
        }
        
        return ApiResponse.success(result);
    }

    @PostMapping("/family-pay")
    public ApiResponse<Map<String, Object>> familyPayOrder(@RequestBody Map<String, Object> params) {
        Long orderId = Long.parseLong(params.get("orderId").toString());
        Long familyId = Long.parseLong(params.get("familyId").toString());
        
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        
        if (!"pending".equals(order.getStatus())) {
            return ApiResponse.error("订单状态不允许支付");
        }
        
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("family_member_id", familyId);
        List<UserRelation> relations = relationMapper.selectByMap(queryMap);
        boolean isRelated = relations.stream().anyMatch(r -> r.getElderId().equals(order.getUserId()));
        
        if (!isRelated) {
            return ApiResponse.error("您不是该老人的家属，无法代付");
        }
        
        BigDecimal amount = order.getAmount();
        boolean success = walletService.pay(familyId, amount, order.getOrderNo());
        
        Map<String, Object> result = new HashMap<>();
        if (success) {
            order.setPaymentStatus("paid");
            order.setUpdatedAt(LocalDateTime.now());
            orderMapper.updateById(order);
            
            result.put("success", true);
            result.put("orderId", orderId);
            result.put("amount", order.getAmount());
            result.put("balance", walletService.getBalance(familyId));
        } else {
            result.put("success", false);
            result.put("message", "余额不足");
        }
        
        return ApiResponse.success(result);
    }

    @PostMapping("/confirm/{orderId}")
    public ApiResponse<String> confirmPayment(@PathVariable Long orderId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        
        if (!"paid".equals(order.getPaymentStatus())) {
            return ApiResponse.error("订单未支付");
        }
        
        order.setStatus("completed");
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        
        return ApiResponse.success("付款已确认");
    }

    @GetMapping("/orders/need-confirm")
    public ApiResponse<List<ServiceOrder>> getOrdersNeedConfirm() {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("paymentStatus", "paid");
        queryMap.put("status", "pending");
        List<ServiceOrder> orders = orderMapper.selectByMap(queryMap);
        return ApiResponse.success(orders);
    }

    @GetMapping("/elder-orders/{familyId}")
    public ApiResponse<List<ServiceOrder>> getElderOrders(@PathVariable Long familyId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("family_member_id", familyId);
        List<UserRelation> relations = relationMapper.selectByMap(queryMap);
        
        if (relations.isEmpty()) {
            return ApiResponse.success(new ArrayList<>());
        }
        
        List<Long> elderIds = relations.stream().map(UserRelation::getElderId).collect(Collectors.toList());
        Map<String, Object> orderQueryMap = new HashMap<>();
        orderQueryMap.put("userId", elderIds.get(0));
        List<ServiceOrder> orders = orderMapper.selectByMap(orderQueryMap);
        
        return ApiResponse.success(orders);
    }
}
