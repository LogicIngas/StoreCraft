package com.example.loginpage.service.impl;

import com.example.loginpage.model.ChatConversation;
import com.example.loginpage.model.ChatMessage;
import com.example.loginpage.repository.IChatConversationRepository;
import com.example.loginpage.repository.IChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private IChatMessageRepository chatMessageRepository;

    @Autowired
    private IChatConversationRepository chatConversationRepository;

    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        // Generate conversation ID
        String conversationId = generateConversationId(message.getSenderId(), message.getRecipientId());
        message.setConversationId(conversationId);

        // Save the message
        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Update or create conversation
        updateConversation(savedMessage);

        return savedMessage;
    }

    @Transactional
    public void updateConversation(ChatMessage message) {
        String conversationId = message.getConversationId();
        Optional<ChatConversation> existingConv = chatConversationRepository.findById(conversationId);

        ChatConversation conversation;
        if (existingConv.isPresent()) {
            conversation = existingConv.get();
        } else {
            // Create new conversation
            conversation = new ChatConversation(message.getSenderId(), message.getRecipientId());
            conversation.setConversationId(conversationId);
        }

        // Update last message info
        conversation.setLastMessage(message.getContent());
        conversation.setLastMessageTime(message.getCreatedAt());

        // Increment unread count for recipient
        if (message.getRecipientId().equals(conversation.getUser1Id())) {
            conversation.setUser1UnreadCount(conversation.getUser1UnreadCount() + 1);
        } else if (message.getRecipientId().equals(conversation.getUser2Id())) {
            conversation.setUser2UnreadCount(conversation.getUser2UnreadCount() + 1);
        }

        chatConversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getChatHistory(String userId, String otherUserId) {
        String conversationId = generateConversationId(userId, otherUserId);
        return chatMessageRepository.findByConversationId(conversationId);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getRecentMessages(String userId, int limit) {
        return chatMessageRepository.findByUserId(userId)
                .stream()
                .limit(limit)
                .toList();
    }

    @Transactional
    public void markMessagesAsRead(String userId, String senderId) {
        // Mark messages as read
        chatMessageRepository.markMessagesAsRead(userId, senderId);

        // Reset unread count in conversation
        String conversationId = generateConversationId(userId, senderId);
        Optional<ChatConversation> conv = chatConversationRepository.findById(conversationId);
        if (conv.isPresent()) {
            ChatConversation conversation = conv.get();
            if (conversation.getUser1Id().equals(userId)) {
                conversation.setUser1UnreadCount(0);
            } else if (conversation.getUser2Id().equals(userId)) {
                conversation.setUser2UnreadCount(0);
            }
            chatConversationRepository.save(conversation);
        }
    }

    @Transactional
    public void markAllMessagesAsRead(String userId) {
        chatMessageRepository.markAllMessagesAsRead(userId);

        // Reset all unread counts for this user
        List<ChatConversation> conversations = chatConversationRepository.findByUserId(userId);
        for (ChatConversation conv : conversations) {
            if (conv.getUser1Id().equals(userId)) {
                conv.setUser1UnreadCount(0);
            } else if (conv.getUser2Id().equals(userId)) {
                conv.setUser2UnreadCount(0);
            }
            chatConversationRepository.save(conv);
        }
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return chatMessageRepository.countUnreadMessages(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCountFromSender(String userId, String senderId) {
        return chatMessageRepository.countUnreadMessagesFromSender(userId, senderId);
    }

    @Transactional(readOnly = true)
    public List<ChatConversation> getConversations(String userId) {
        return chatConversationRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<ChatConversation> getConversationBetweenUsers(String user1Id, String user2Id) {
        return chatConversationRepository.findConversationBetweenUsers(user1Id, user2Id);
    }

    private String generateConversationId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0
                ? userId1 + "-" + userId2
                : userId2 + "-" + userId1;
    }

    @Transactional
    public void deleteOldMessages(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        // You would need to add a custom query for this
        // chatMessageRepository.deleteOldMessages(cutoffDate);
    }
}