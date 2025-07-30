package com.tacticalcommand.tactical.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * Entity representing secure tactical messages between command centers and units.
 * Supports message classification, encryption, and delivery tracking.
 */
@Entity
@Table(name = "tactical_messages")
public class TacticalMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(name = "message_type", nullable = false)
    private String messageType;

    @Column(name = "classification", nullable = false)
    private String classification = "UNCLASSIFIED";

    @Column(name = "subject")
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "encrypted_content")
    private byte[] encryptedContent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "delivery_status")
    private String deliveryStatus;

    @Column(name = "priority_level")
    private Integer priorityLevel = 3; // 1=HIGH, 2=MEDIUM, 3=LOW

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "thread_id")
    private String threadId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Default constructor
    public TacticalMessage() {}

    // Constructor with essential fields
    public TacticalMessage(User sender, String messageType, String content) {
        this.sender = sender;
        this.messageType = messageType;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(byte[] encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Check if message has expired.
     *
     * @return true if message has passed expiration time
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if message has been read.
     *
     * @return true if readAt timestamp is set
     */
    public boolean isRead() {
        return readAt != null;
    }

    /**
     * Check if message content is encrypted.
     *
     * @return true if encrypted content exists
     */
    public boolean isEncrypted() {
        return encryptedContent != null && encryptedContent.length > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TacticalMessage)) return false;
        TacticalMessage that = (TacticalMessage) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "TacticalMessage{" +
                "id=" + id +
                ", messageType='" + messageType + '\'' +
                ", classification='" + classification + '\'' +
                ", subject='" + subject + '\'' +
                ", createdAt=" + createdAt +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                '}';
    }
}
