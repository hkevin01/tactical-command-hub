package com.tacticalcommand.tactical.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for tactical messages.
 */
public class MessageResponse {

    private Long id;
    private String senderName;
    private String messageType;
    private String classification;
    private String subject;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String deliveryStatus;
    private Integer priorityLevel;
    private String threadId;
    private boolean isEncrypted;

    // Default constructor
    public MessageResponse() {}

    // Constructor for creating response from message entity
    public MessageResponse(Long id, String senderName, String messageType, 
                          String classification, String subject, String content,
                          LocalDateTime createdAt) {
        this.id = id;
        this.senderName = senderName;
        this.messageType = messageType;
        this.classification = classification;
        this.subject = subject;
        this.content = content;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
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

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "id=" + id +
                ", senderName='" + senderName + '\'' +
                ", messageType='" + messageType + '\'' +
                ", classification='" + classification + '\'' +
                ", subject='" + subject + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
