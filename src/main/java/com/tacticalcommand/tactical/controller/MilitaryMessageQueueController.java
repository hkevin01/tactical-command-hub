package com.tacticalcommand.tactical.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tacticalcommand.tactical.service.MilitaryMessageQueueService;
import com.tacticalcommand.tactical.service.MilitaryMessageQueueService.*;

/**
 * Military Message Queue REST Controller providing API endpoints for asynchronous message processing.
 * Supports integration with external military command systems through message queuing patterns.
 * 
 * API Endpoints:
 * - POST /api/message-queue/send - Queue message for transmission
 * - GET /api/message-queue/status - Get queue status and statistics
 * - GET /api/message-queue/failed - Retrieve failed messages
 * - POST /api/message-queue/retry/{messageId} - Retry failed message
 * - POST /api/message-queue/process - Trigger message processing
 * 
 * Security:
 * - Requires ADMIN role for queue management operations
 * - Requires OPERATOR role for message transmission
 * - All endpoints require authenticated access
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/api/message-queue")
@PreAuthorize("hasRole('OPERATOR')")
public class MilitaryMessageQueueController {

    @Autowired
    private MilitaryMessageQueueService messageQueueService;

    /**
     * Queue military message for asynchronous transmission to external systems.
     * 
     * @param request Message queue request parameters
     * @return Message queue result with tracking ID
     */
    @PostMapping("/send")
    @PreAuthorize("hasRole('OPERATOR')")
    public CompletableFuture<ResponseEntity<MessageQueueResult>> queueMessage(@RequestBody MessageQueueRequest request) {
        return messageQueueService.queueMessage(
            request.messageType,
            request.messageFormat,
            request.recipients,
            request.messageData,
            request.priority,
            request.classification
        ).thenApply(result -> ResponseEntity.ok(result));
    }

    /**
     * Get current message queue status and processing statistics.
     * 
     * @return Queue status information
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<MessageQueueStatus> getQueueStatus() {
        MessageQueueStatus status = messageQueueService.getQueueStatus();
        return ResponseEntity.ok(status);
    }

    /**
     * Retrieve messages that failed processing and are in dead letter queue.
     * 
     * @return List of failed messages requiring attention
     */
    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MilitaryMessage>> getFailedMessages() {
        List<MilitaryMessage> failedMessages = messageQueueService.getDeadLetterMessages();
        return ResponseEntity.ok(failedMessages);
    }

    /**
     * Retry processing of a failed message from dead letter queue.
     * 
     * @param messageId Message identifier to retry
     * @return Retry operation result
     */
    @PostMapping("/retry/{messageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<MessageQueueResult>> retryMessage(@PathVariable String messageId) {
        return messageQueueService.retryMessage(messageId)
            .thenApply(result -> ResponseEntity.ok(result));
    }

    /**
     * Manually trigger message processing for queued messages.
     * Useful for testing and troubleshooting queue operations.
     * 
     * @return Processing trigger confirmation
     */
    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> triggerProcessing() {
        messageQueueService.processNextMessage();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Message processing triggered",
            "timestamp", java.time.LocalDateTime.now()
        ));
    }

    // Request/Response DTOs

    public static class MessageQueueRequest {
        public MessageType messageType;
        public String messageFormat;
        public List<String> recipients;
        public Map<String, Object> messageData;
        public MessagePriority priority;
        public String classification;
    }
}
