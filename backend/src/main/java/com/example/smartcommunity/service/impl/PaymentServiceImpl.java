package com.example.smartcommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.smartcommunity.entity.ServiceOrder;
import com.example.smartcommunity.entity.UserRelation;
import com.example.smartcommunity.mapper.ServiceOrderMapper;
import com.example.smartcommunity.mapper.UserRelationMapper;
import com.example.smartcommunity.service.PaymentService;
import com.example.smartcommunity.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ServiceOrderMapper orderMapper;
    private final WalletService walletService;
    private final UserRelationMapper relationMapper;

    @Override
    @Transactional
    public Map<String, Object> payOrder(Long orderId, Long payerId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return errorResult("订单不存在");
        }

        if (!"pending".equals(order.getStatus())) {
            return errorResult("订单状态不允许支付");
        }

        BigDecimal amount = order.getAmount();
        boolean success = walletService.pay(payerId, amount, order.getOrderNo());

        if (success) {
            order.setPaymentStatus("paid");
            order.setUpdatedAt(LocalDateTime.now());
            int rows = orderMapper.updateById(order);
            if (rows == 0) {
                throw new RuntimeException("订单支付并发冲突，请重试");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("orderId", orderId);
            result.put("amount", order.getAmount());
            result.put("balance", walletService.getBalance(payerId));
            return result;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "余额不足");
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> familyPayOrder(Long orderId, Long familyId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return errorResult("订单不存在");
        }

        if (!"pending".equals(order.getStatus())) {
            return errorResult("订单状态不允许支付");
        }

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("family_member_id", familyId);
        List<UserRelation> relations = relationMapper.selectByMap(queryMap);
        boolean isRelated = relations.stream().anyMatch(r -> r.getElderId().equals(order.getUserId()));

        if (!isRelated) {
            return errorResult("您不是该老人的家属，无法代付");
        }

        BigDecimal amount = order.getAmount();
        boolean success = walletService.pay(familyId, amount, order.getOrderNo());

        if (success) {
            order.setPaymentStatus("paid");
            order.setUpdatedAt(LocalDateTime.now());
            int rows = orderMapper.updateById(order);
            if (rows == 0) {
                throw new RuntimeException("订单支付并发冲突，请重试");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("orderId", orderId);
            result.put("amount", order.getAmount());
            result.put("balance", walletService.getBalance(familyId));
            return result;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "余额不足");
        return result;
    }

    @Override
    @Transactional
    public void confirmPayment(Long orderId) {
        ServiceOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!"paid".equals(order.getPaymentStatus())) {
            throw new RuntimeException("订单未支付");
        }

        order.setStatus("completed");
        order.setUpdatedAt(LocalDateTime.now());
        int rows = orderMapper.updateById(order);
        if (rows == 0) {
            throw new RuntimeException("确认付款并发冲突，请重试");
        }
    }

    @Override
    public List<ServiceOrder> getOrdersNeedConfirm() {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("payment_status", "paid");
        queryMap.put("status", "pending");
        return orderMapper.selectByMap(queryMap);
    }

    @Override
    public List<ServiceOrder> getElderOrders(Long familyId) {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("family_member_id", familyId);
        List<UserRelation> relations = relationMapper.selectByMap(queryMap);

        if (relations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> elderIds = relations.stream().map(UserRelation::getElderId).collect(Collectors.toList());
        LambdaQueryWrapper<ServiceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ServiceOrder::getUserId, elderIds);
        return orderMapper.selectList(wrapper);
    }

    private Map<String, Object> errorResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }
}
