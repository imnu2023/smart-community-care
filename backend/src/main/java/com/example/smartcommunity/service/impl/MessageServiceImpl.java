package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.Message;
import com.example.smartcommunity.mapper.MessageMapper;
import com.example.smartcommunity.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public List<Message> getMessagesByReceiver(Long receiverId) {
        return messageMapper.findByReceiverId(receiverId);
    }

    @Override
    public List<Message> getUnreadMessages(Long receiverId) {
        return messageMapper.findUnreadByReceiverId(receiverId);
    }

    @Override
    public int countUnread(Long receiverId) {
        return messageMapper.countUnread(receiverId);
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageMapper.selectById(messageId);
        if (message != null && !message.getIsRead()) {
            message.setIsRead(true);
            messageMapper.updateById(message);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long receiverId) {
        messageMapper.markAllAsRead(receiverId);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        messageMapper.deleteById(messageId);
    }
}
