package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.CommunityService;
import com.example.smartcommunity.entity.ServiceOrder;
import com.example.smartcommunity.exception.BusinessException;
import com.example.smartcommunity.mapper.CommunityServiceMapper;
import com.example.smartcommunity.mapper.ServiceOrderMapper;
import com.example.smartcommunity.service.NotificationHelper;
import com.example.smartcommunity.service.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl implements ServiceOrderService {

    private final ServiceOrderMapper serviceOrderMapper;
    private final CommunityServiceMapper communityServiceMapper;
    private final NotificationHelper notificationHelper;

    @Override
    public ServiceOrder createOrder(Long userId, Long serviceId, String serviceTime, String address, String remark) {
        CommunityService service = communityServiceMapper.selectById(serviceId);
        if (service == null) {
            throw new BusinessException("服务不存在");
        }

        ServiceOrder order = new ServiceOrder();
        order.setOrderNo("SO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setUserId(userId);
        order.setServiceId(serviceId);
        order.setStatus("pending");
        order.setServiceTime(parseServiceTime(serviceTime));
        order.setAddress(address);
        order.setRemark(remark);
        order.setAmount(service.getPrice());
        order.setPaymentStatus("unpaid");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        serviceOrderMapper.insert(order);

        notificationHelper.send(userId, NotificationHelper.TYPE_ORDER,
                "服务订单 " + order.getOrderNo() + " 已创建，" + service.getServiceName() + " 预计 "
                + serviceTime.replace("T", " "));

        return order;
    }

    @Override
    public ServiceOrder getOrderById(Long id) {
        return serviceOrderMapper.selectById(id);
    }

    @Override
    public List<ServiceOrder> getOrdersByUserId(Long userId) {
        return serviceOrderMapper.findByUserId(userId);
    }

    @Override
    public List<ServiceOrder> getOrdersByStatus(String status) {
        return serviceOrderMapper.findByStatus(status);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order != null) {
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            int rows = serviceOrderMapper.updateById(order);
            if (rows == 0) {
                throw new BusinessException("订单状态并发冲突，请重试");
            }
            notificationHelper.send(order.getUserId(), NotificationHelper.TYPE_ORDER,
                    "订单 " + order.getOrderNo() + " 状态更新为：" + statusLabel(status));
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order != null && "pending".equals(order.getStatus())) {
            order.setStatus("cancelled");
            order.setUpdatedAt(LocalDateTime.now());
            int rows = serviceOrderMapper.updateById(order);
            if (rows == 0) {
                throw new BusinessException("取消订单并发冲突，请重试");
            }
            notificationHelper.send(order.getUserId(), NotificationHelper.TYPE_ORDER,
                    "订单 " + order.getOrderNo() + " 已取消");
        }
    }

    @Override
    public List<ServiceOrder> getAllOrders() {
        return serviceOrderMapper.selectList(null);
    }

    @Override
    public void fixAddressData() {
        List<ServiceOrder> orders = serviceOrderMapper.selectList(null);
        for (ServiceOrder order : orders) {
            String address = order.getAddress();
            if (address != null) {
                if (address.contains("Community") || address.contains("Building")) {
                    order.setAddress("幸福社区1号楼101室");
                } else if (address.contains("?") || address.equals("1")) {
                    order.setAddress("幸福社区");
                }
                order.setUpdatedAt(LocalDateTime.now());
                serviceOrderMapper.updateById(order);
            }
        }
    }

    private LocalDateTime parseServiceTime(String serviceTime) {
        if (serviceTime == null || serviceTime.isEmpty()) {
            throw new BusinessException("服务时间不能为空");
        }
        
        String normalizedTime = serviceTime.trim();
        
        normalizedTime = normalizedTime.replace('\\', '/');
        normalizedTime = normalizedTime.replace('-', '/');
        
        if (normalizedTime.contains("T")) {
            normalizedTime = normalizedTime.replace("T", " ");
        }
        
        String[] parts = normalizedTime.split(" ");
        if (parts.length >= 2) {
            String datePart = parts[0];
            String timePart = parts[1];
            
            String[] dateParts = datePart.split("/");
            String[] timeParts = timePart.split(":");
            
            if (dateParts.length == 3 && timeParts.length >= 2) {
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int day = Integer.parseInt(dateParts[2]);
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                int second = timeParts.length >= 3 ? Integer.parseInt(timeParts[2]) : 0;
                
                return LocalDateTime.of(year, month, day, hour, minute, second);
            }
        }
        
        throw new BusinessException("服务时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 格式");
    }

    private String statusLabel(String status) {
        switch (status) {
            case "pending":   return "待服务";
            case "confirmed": return "已确认";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            default:          return status;
        }
    }
}