package com.example.loginpage.controller;

import com.example.loginpage.model.ChatConversation;
import com.example.loginpage.model.ChatMessage;
import com.example.loginpage.service.impl.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message) {
        ChatMessage savedMessage = chatService.saveMessage(message);

        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/messages",
                savedMessage
        );

        messagingTemplate.convertAndSendToUser(
                message.getSenderId(),
                "/queue/messages",
                savedMessage
        );
    }

    @MessageMapping("/chat.typing")
    public void typingIndicator(@Payload Map<String, String> payload) {
        String senderId = payload.get("senderId");
        String recipientId = payload.get("recipientId");
        boolean isTyping = Boolean.parseBoolean(payload.get("isTyping"));

        messagingTemplate.convertAndSendToUser(
                recipientId,
                "/queue/typing",
                Map.of("senderId", senderId, "isTyping", isTyping)
        );
    }

    @GetMapping("/history/{userId}/{otherUserId}")
    public List<ChatMessage> getChatHistory(@PathVariable String userId,
                                            @PathVariable String otherUserId) {
        return chatService.getChatHistory(userId, otherUserId);
    }

    @PostMapping("/mark-read")
    public Map<String, String> markMessagesAsRead(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String otherUserId = request.get("otherUserId");

        chatService.markMessagesAsRead(userId, otherUserId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Messages marked as read");
        return response;
    }

    @PostMapping("/mark-all-read")
    public Map<String, String> markAllMessagesAsRead(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");

        chatService.markAllMessagesAsRead(userId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "All messages marked as read");
        return response;
    }

    @GetMapping("/unread-count/{userId}")
    public long getUnreadCount(@PathVariable String userId) {
        return chatService.getUnreadCount(userId);
    }

    @GetMapping("/unread-count/{userId}/{senderId}")
    public long getUnreadCountFromSender(@PathVariable String userId,
                                         @PathVariable String senderId) {
        return chatService.getUnreadCountFromSender(userId, senderId);
    }

    @GetMapping("/conversations/{userId}")
    public List<ChatConversation> getConversations(@PathVariable String userId) {
        return chatService.getConversations(userId);
    }

    @GetMapping("/conversation/{user1Id}/{user2Id}")
    public ChatConversation getConversation(@PathVariable String user1Id,
                                            @PathVariable String user2Id) {
        return chatService.getConversationBetweenUsers(user1Id, user2Id)
                .orElse(null);
    }

    @GetMapping("/recent/{userId}/{limit}")
    public List<ChatMessage> getRecentMessages(@PathVariable String userId,
                                               @PathVariable int limit) {
        return chatService.getRecentMessages(userId, limit);
    }
}