package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.Message;

import java.util.List;

public interface MessageService {

    /** 获取用户收到的所有通知 */
    List<Message> getMessagesByReceiver(Long receiverId);

    /** 获取未读通知 */
    List<Message> getUnreadMessages(Long receiverId);

    /** 未读通知计数 */
    int countUnread(Long receiverId);

    /** 标记单条已读 */
    void markAsRead(Long messageId);

    /** 批量标记全部已读 */
    void markAllAsRead(Long receiverId);

    /** 删除单条通知 */
    void deleteMessage(Long messageId);
}
