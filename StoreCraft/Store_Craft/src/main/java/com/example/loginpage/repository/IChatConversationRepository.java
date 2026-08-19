package com.example.loginpage.repository;

import com.example.loginpage.model.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface IChatConversationRepository extends JpaRepository<ChatConversation, String> {

    @Query("SELECT c FROM ChatConversation c WHERE c.user1Id = :userId OR c.user2Id = :userId ORDER BY c.lastMessageTime DESC")
    List<ChatConversation> findByUserId(@Param("userId") String userId);

    @Query("SELECT c FROM ChatConversation c WHERE (c.user1Id = :user1Id AND c.user2Id = :user2Id) OR (c.user1Id = :user2Id AND c.user2Id = :user1Id)")
    Optional<ChatConversation> findConversationBetweenUsers(@Param("user1Id") String user1Id,
                                                            @Param("user2Id") String user2Id);

    @Modifying
    @Transactional
    @Query("UPDATE ChatConversation c SET c.user1UnreadCount = c.user1UnreadCount + 1 WHERE c.user1Id = :userId AND c.user2Id = :senderId")
    void incrementUnreadCountForUser1(@Param("userId") String userId, @Param("senderId") String senderId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatConversation c SET c.user2UnreadCount = c.user2UnreadCount + 1 WHERE c.user2Id = :userId AND c.user1Id = :senderId")
    void incrementUnreadCountForUser2(@Param("userId") String userId, @Param("senderId") String senderId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatConversation c SET c.user1UnreadCount = 0 WHERE c.user1Id = :userId AND c.user2Id = :senderId")
    void resetUnreadCountForUser1(@Param("userId") String userId, @Param("senderId") String senderId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatConversation c SET c.user2UnreadCount = 0 WHERE c.user2Id = :userId AND c.user1Id = :senderId")
    void resetUnreadCountForUser2(@Param("userId") String userId, @Param("senderId") String senderId);

    @Query("SELECT COALESCE(SUM(c.user1UnreadCount), 0) + COALESCE(SUM(c.user2UnreadCount), 0) " +
            "FROM ChatConversation c WHERE c.user1Id = :userId OR c.user2Id = :userId")
    long getTotalUnreadCount(@Param("userId") String userId);
}