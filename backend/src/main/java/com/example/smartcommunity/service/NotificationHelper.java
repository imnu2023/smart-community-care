package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.Message;
import com.example.smartcommunity.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 系统通知工具类
 * 供各业务 Service 调用，统一以系统身份向用户推送通知消息
 */
@Component
@RequiredArgsConstructor
public class NotificationHelper {

    private final MessageMapper messageMapper;

    /** 系统发送者 ID */
    private static final Long SYSTEM_SENDER_ID = 1L;

    /** 通知类型常量 */
    public static final String TYPE_ACTIVITY  = "activity";
    public static final String TYPE_PAYMENT   = "payment";
    public static final String TYPE_HEALTH    = "health";
    public static final String TYPE_ORDER     = "order";
    public static final String TYPE_EMERGENCY = "emergency";

    /**
     * 发送一条系统通知
     *
     * @param userId  接收者用户 ID
     * @param type    通知类型（activity / payment / health / order / emergency）
     * @param content 通知文案
     */
    public void send(Long userId, String type, String content) {
        Message msg = new Message();
        msg.setSenderId(SYSTEM_SENDER_ID);
        msg.setReceiverId(userId);
        msg.setType(type);
        msg.setContent(content);
        msg.setIsRead(false);
        msg.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(msg);
    }
}
