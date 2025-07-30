package com.tacticalcommand.tactical.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for sending tactical messages.
 */
public class MessageRequest {

    @NotBlank(message = "Message type is required")
    private String messageType;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Classification is required")
    private String classification = "UNCLASSIFIED";

    private List<Long> recipientIds;

    private Long groupId;

    private Integer priorityLevel = 3;

    private String threadId;

    private Long expirationMinutes;

    // Default constructor
    public MessageRequest() {}

    // Constructor with essential fields
    public MessageRequest(String messageType, String subject, String content) {
        this.messageType = messageType;
        this.subject = subject;
        this.content = content;
    }

    // Getters and Setters
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public List<Long> getRecipientIds() {
        return recipientIds;
    }

    public void setRecipientIds(List<Long> recipientIds) {
        this.recipientIds = recipientIds;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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

    public Long getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(Long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "messageType='" + messageType + '\'' +
                ", subject='" + subject + '\'' +
                ", classification='" + classification + '\'' +
                ", priorityLevel=" + priorityLevel +
                '}';
    }
}
