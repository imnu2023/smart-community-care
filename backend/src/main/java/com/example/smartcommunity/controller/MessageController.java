package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.Message;
import com.example.smartcommunity.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{userId}")
    public ApiResponse<List<Message>> getMessages(@PathVariable Long userId) {
        List<Message> messages = messageService.getMessagesByReceiver(userId);
        return ApiResponse.success(messages);
    }

    @GetMapping("/unread/{userId}")
    public ApiResponse<List<Message>> getUnreadMessages(@PathVariable Long userId) {
        List<Message> messages = messageService.getUnreadMessages(userId);
        return ApiResponse.success(messages);
    }

    @GetMapping("/unread/count/{userId}")
    public ApiResponse<Map<String, Integer>> countUnread(@PathVariable Long userId) {
        int count = messageService.countUnread(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return ApiResponse.success(result);
    }

    @PutMapping("/{messageId}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ApiResponse.success("已标为已读");
    }

    @PutMapping("/read-all/{userId}")
    public ApiResponse<Void> markAllAsRead(@PathVariable Long userId) {
        messageService.markAllAsRead(userId);
        return ApiResponse.success("全部已标为已读");
    }

    @DeleteMapping("/{messageId}")
    public ApiResponse<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ApiResponse.success("已删除");
    }
}
