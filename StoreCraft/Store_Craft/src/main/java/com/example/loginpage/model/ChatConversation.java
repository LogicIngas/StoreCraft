package com.example.loginpage.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_conversations")
public class ChatConversation {

    @Id
    @UuidGenerator
    @Column(name = "conversation_id", updatable = false, nullable = false)
    private String conversationId;

    @Column(name = "user1_id", nullable = false)
    private String user1Id;

    @Column(name = "user2_id", nullable = false)
    private String user2Id;

    @Column(name = "last_message", columnDefinition = "TEXT")
    private String lastMessage;

    @Column(name = "last_message_time")
    private LocalDateTime lastMessageTime;

    @Column(name = "user1_unread_count")
    private Integer user1UnreadCount = 0;

    @Column(name = "user2_unread_count")
    private Integer user2UnreadCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ChatConversation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ChatConversation(String user1Id, String user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.user1UnreadCount = 0;
        this.user2UnreadCount = 0;
    }

    // Getters and Setters
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(String user1Id) {
        this.user1Id = user1Id;
    }

    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Integer getUser1UnreadCount() {
        return user1UnreadCount;
    }

    public void setUser1UnreadCount(Integer user1UnreadCount) {
        this.user1UnreadCount = user1UnreadCount;
    }

    public Integer getUser2UnreadCount() {
        return user2UnreadCount;
    }

    public void setUser2UnreadCount(Integer user2UnreadCount) {
        this.user2UnreadCount = user2UnreadCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}