package com.example.loginpage.repository;

import com.example.loginpage.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IChatMessageRepository extends JpaRepository<ChatMessage, String> {

    @Query("SELECT m FROM ChatMessage m WHERE m.conversationId = :conversationId ORDER BY m.createdAt ASC")
    List<ChatMessage> findByConversationId(@Param("conversationId") String conversationId);

    @Query("SELECT m FROM ChatMessage m WHERE m.senderId = :userId OR m.recipientId = :userId ORDER BY m.createdAt DESC")
    List<ChatMessage> findByUserId(@Param("userId") String userId);

    @Query("SELECT m FROM ChatMessage m WHERE m.conversationId = :conversationId AND m.createdAt > :since ORDER BY m.createdAt ASC")
    List<ChatMessage> findMessagesSince(@Param("conversationId") String conversationId,
                                        @Param("since") java.time.LocalDateTime since);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.recipientId = :userId AND m.isRead = false")
    long countUnreadMessages(@Param("userId") String userId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.recipientId = :userId AND m.senderId = :senderId AND m.isRead = false")
    long countUnreadMessagesFromSender(@Param("userId") String userId, @Param("senderId") String senderId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.recipientId = :userId AND m.senderId = :senderId AND m.isRead = false")
    void markMessagesAsRead(@Param("userId") String userId, @Param("senderId") String senderId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.recipientId = :userId AND m.isRead = false")
    void markAllMessagesAsRead(@Param("userId") String userId);

    @Query("SELECT DISTINCT m.conversationId FROM ChatMessage m WHERE m.senderId = :userId OR m.recipientId = :userId")
    List<String> findConversationIdsByUserId(@Param("userId") String userId);
}