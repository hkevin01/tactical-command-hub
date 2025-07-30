package com.tacticalcommand.tactical.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tacticalcommand.tactical.domain.TacticalMessage;
import com.tacticalcommand.tactical.domain.User;

/**
 * Repository for managing tactical messages with secure access controls.
 */
@Repository
public interface TacticalMessageRepository extends JpaRepository<TacticalMessage, Long> {

    /**
     * Find messages for a specific user (sent or received) ordered by creation time.
     *
     * @param recipient Target recipient user
     * @param sender Target sender user
     * @param pageable Pagination parameters
     * @return Page of messages for the user
     */
    Page<TacticalMessage> findByRecipientOrSenderOrderByCreatedAtDesc(
            User recipient, User sender, Pageable pageable);

    /**
     * Find messages by classification level ordered by creation time.
     *
     * @param classification Message classification level
     * @param pageable Pagination parameters
     * @return Page of messages with specified classification
     */
    Page<TacticalMessage> findByClassificationOrderByCreatedAtDesc(
            String classification, Pageable pageable);

    /**
     * Count unread messages for a specific recipient.
     *
     * @param recipient Target recipient user
     * @return Number of unread messages
     */
    long countByRecipientAndReadAtIsNull(User recipient);

    /**
     * Find messages by sender ordered by creation time.
     *
     * @param sender Message sender
     * @param pageable Pagination parameters
     * @return Page of messages from the sender
     */
    Page<TacticalMessage> findBySenderOrderByCreatedAtDesc(User sender, Pageable pageable);

    /**
     * Find messages by message type ordered by creation time.
     *
     * @param messageType Type of message
     * @param pageable Pagination parameters
     * @return Page of messages of specified type
     */
    Page<TacticalMessage> findByMessageTypeOrderByCreatedAtDesc(String messageType, Pageable pageable);

    /**
     * Find messages by priority level ordered by creation time.
     *
     * @param priorityLevel Message priority (1=HIGH, 2=MEDIUM, 3=LOW)
     * @param pageable Pagination parameters
     * @return Page of messages with specified priority
     */
    Page<TacticalMessage> findByPriorityLevelOrderByCreatedAtDesc(Integer priorityLevel, Pageable pageable);

    /**
     * Find messages within a time range.
     *
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param pageable Pagination parameters
     * @return Page of messages within time range
     */
    Page<TacticalMessage> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * Find messages by thread ID for conversation tracking.
     *
     * @param threadId Thread identifier
     * @param pageable Pagination parameters
     * @return Page of messages in the thread
     */
    Page<TacticalMessage> findByThreadIdOrderByCreatedAtAsc(String threadId, Pageable pageable);

    /**
     * Find messages by group ID.
     *
     * @param groupId Target group identifier
     * @param pageable Pagination parameters
     * @return Page of messages for the group
     */
    Page<TacticalMessage> findByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);

    /**
     * Find expired messages that should be cleaned up.
     *
     * @param currentTime Current timestamp for comparison
     * @return List of expired messages
     */
    List<TacticalMessage> findByExpiresAtBefore(LocalDateTime currentTime);

    /**
     * Find high-priority unread messages for urgent notifications.
     *
     * @param recipient Target recipient
     * @param maxPriority Maximum priority level (1=HIGH)
     * @return List of high-priority unread messages
     */
    @Query("SELECT m FROM TacticalMessage m WHERE m.recipient = :recipient " +
           "AND m.readAt IS NULL AND m.priorityLevel <= :maxPriority " +
           "ORDER BY m.priorityLevel ASC, m.createdAt DESC")
    List<TacticalMessage> findHighPriorityUnreadMessages(
            @Param("recipient") User recipient, 
            @Param("maxPriority") Integer maxPriority);

    /**
     * Count messages by classification for statistics.
     *
     * @param classification Message classification level
     * @return Number of messages with specified classification
     */
    long countByClassification(String classification);

    /**
     * Count messages by message type for analytics.
     *
     * @param messageType Type of message
     * @return Number of messages of specified type
     */
    long countByMessageType(String messageType);

    /**
     * Find recent messages within specified hours for activity monitoring.
     *
     * @param hoursAgo Number of hours back to search
     * @param currentTime Current timestamp
     * @return List of recent messages
     */
    @Query("SELECT m FROM TacticalMessage m WHERE m.createdAt >= :sinceTime " +
           "ORDER BY m.createdAt DESC")
    List<TacticalMessage> findRecentMessages(
            @Param("sinceTime") LocalDateTime sinceTime);

    /**
     * Find messages matching search criteria across content and subject.
     *
     * @param searchTerm Search term to match
     * @param user User requesting search (for access control)
     * @param pageable Pagination parameters
     * @return Page of matching messages
     */
    @Query("SELECT m FROM TacticalMessage m WHERE " +
           "(m.recipient = :user OR m.sender = :user) AND " +
           "(LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY m.createdAt DESC")
    Page<TacticalMessage> searchUserMessages(
            @Param("searchTerm") String searchTerm,
            @Param("user") User user,
            Pageable pageable);
}
