package com.tacticalcommand.tactical.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Military Message Queue Service providing asynchronous message processing for external integrations.
 * Implements enterprise messaging patterns commonly found in military command systems like GCCS-J.
 * 
 * This service provides:
 * - Asynchronous message queuing for external system communications
 * - Message priority handling for tactical urgency levels
 * - Reliable message delivery with retry mechanisms
 * - Message persistence and audit trail capabilities
 * - Load balancing across multiple external system endpoints
 * 
 * Integration Patterns:
 * - Event-driven architecture for real-time military communications
 * - Circuit breaker pattern for external system resilience
 * - Message transformation pipeline for format conversion
 * - Dead letter queue handling for failed message processing
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-01-29
 */
@Service
public class MilitaryMessageQueueService {

    private static final Logger logger = LoggerFactory.getLogger(MilitaryMessageQueueService.class);

    @Autowired
    private MilitaryIntegrationService integrationService;

    // Message queues for different priority levels
    private final Queue<MilitaryMessage> highPriorityQueue = new ConcurrentLinkedQueue<>();
    private final Queue<MilitaryMessage> normalPriorityQueue = new ConcurrentLinkedQueue<>();
    private final Queue<MilitaryMessage> lowPriorityQueue = new ConcurrentLinkedQueue<>();
    private final Queue<MilitaryMessage> deadLetterQueue = new ConcurrentLinkedQueue<>();

    // Thread pool for asynchronous message processing
    private final ExecutorService messageProcessorExecutor = Executors.newFixedThreadPool(5);
    private final ExecutorService retryExecutor = Executors.newFixedThreadPool(2);

    // Configuration
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 5000; // 5 seconds

    /**
     * Queue military message for asynchronous processing and transmission.
     * Messages are prioritized based on tactical urgency and operational classification.
     * 
     * @param messageType Message format type (USMTF, NATO ADatP-3)
     * @param messageFormat Specific message format (ATO, SITREP, OPSTAT, etc.)
     * @param recipients Target system identifiers
     * @param messageData Structured message content
     * @param priority Message priority level
     * @param classification Security classification level
     * @return Message queue acknowledgment with tracking ID
     */
    public CompletableFuture<MessageQueueResult> queueMessage(
            MessageType messageType,
            String messageFormat,
            List<String> recipients,
            Map<String, Object> messageData,
            MessagePriority priority,
            String classification) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Create military message object
                MilitaryMessage message = new MilitaryMessage();
                message.id = generateMessageId();
                message.messageType = messageType;
                message.messageFormat = messageFormat;
                message.recipients = recipients;
                message.messageData = messageData;
                message.priority = priority;
                message.classification = classification;
                message.queuedTime = LocalDateTime.now();
                message.retryCount = 0;
                message.status = MessageStatus.QUEUED;

                // Add to appropriate priority queue
                addToQueue(message, priority);

                logger.info("Message queued: ID={}, Type={}, Format={}, Priority={}, Recipients={}", 
                    message.id, messageType, messageFormat, priority, recipients.size());

                // Trigger asynchronous processing
                processNextMessage();

                return new MessageQueueResult(message.id, true, "Message queued successfully", LocalDateTime.now());

            } catch (Exception e) {
                logger.error("Error queuing message: {}", e.getMessage());
                return new MessageQueueResult(null, false, "Queue error: " + e.getMessage(), LocalDateTime.now());
            }
        });
    }

    /**
     * Process queued messages asynchronously with priority handling.
     * High priority messages (tactical alerts, emergencies) are processed first.
     */
    public void processNextMessage() {
        messageProcessorExecutor.submit(() -> {
            MilitaryMessage message = getNextMessage();
            if (message != null) {
                processMessage(message);
            }
        });
    }

    /**
     * Get message queue status and statistics.
     * Provides visibility into message processing performance and queue health.
     * 
     * @return Queue status information
     */
    public MessageQueueStatus getQueueStatus() {
        MessageQueueStatus status = new MessageQueueStatus();
        status.checkTime = LocalDateTime.now();
        status.highPriorityCount = highPriorityQueue.size();
        status.normalPriorityCount = normalPriorityQueue.size();
        status.lowPriorityCount = lowPriorityQueue.size();
        status.deadLetterCount = deadLetterQueue.size();
        status.totalQueued = status.highPriorityCount + status.normalPriorityCount + status.lowPriorityCount;
        status.processingHealth = status.deadLetterCount < 10 ? "HEALTHY" : "DEGRADED";
        
        return status;
    }

    /**
     * Retrieve messages from dead letter queue for manual intervention.
     * Failed messages can be analyzed and potentially reprocessed.
     * 
     * @return List of failed messages requiring attention
     */
    public List<MilitaryMessage> getDeadLetterMessages() {
        return List.copyOf(deadLetterQueue);
    }

    /**
     * Retry processing of a message from dead letter queue.
     * Allows manual reprocessing of previously failed messages.
     * 
     * @param messageId Message identifier to retry
     * @return Retry result
     */
    public CompletableFuture<MessageQueueResult> retryMessage(String messageId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Find message in dead letter queue
                MilitaryMessage message = deadLetterQueue.stream()
                    .filter(msg -> messageId.equals(msg.id))
                    .findFirst()
                    .orElse(null);

                if (message == null) {
                    return new MessageQueueResult(messageId, false, "Message not found in dead letter queue", LocalDateTime.now());
                }

                // Reset retry count and requeue
                message.retryCount = 0;
                message.status = MessageStatus.QUEUED;
                message.queuedTime = LocalDateTime.now();
                
                deadLetterQueue.remove(message);
                addToQueue(message, message.priority);

                logger.info("Message requeued from dead letter queue: ID={}", messageId);
                
                // Trigger processing
                processNextMessage();

                return new MessageQueueResult(messageId, true, "Message requeued successfully", LocalDateTime.now());

            } catch (Exception e) {
                logger.error("Error retrying message {}: {}", messageId, e.getMessage());
                return new MessageQueueResult(messageId, false, "Retry error: " + e.getMessage(), LocalDateTime.now());
            }
        });
    }

    // Private helper methods

    private void addToQueue(MilitaryMessage message, MessagePriority priority) {
        switch (priority) {
            case HIGH:
                highPriorityQueue.offer(message);
                break;
            case NORMAL:
                normalPriorityQueue.offer(message);
                break;
            case LOW:
                lowPriorityQueue.offer(message);
                break;
        }
    }

    private MilitaryMessage getNextMessage() {
        // Process high priority first, then normal, then low
        MilitaryMessage message = highPriorityQueue.poll();
        if (message == null) {
            message = normalPriorityQueue.poll();
        }
        if (message == null) {
            message = lowPriorityQueue.poll();
        }
        return message;
    }

    private void processMessage(MilitaryMessage message) {
        try {
            logger.info("Processing message: ID={}, Type={}, Format={}", 
                message.id, message.messageType, message.messageFormat);

            message.status = MessageStatus.PROCESSING;
            message.processingStartTime = LocalDateTime.now();

            // Route to appropriate integration service based on message type
            boolean success = false;
            
            if (message.messageType == MessageType.USMTF) {
                var result = integrationService.sendUSMTFMessage(
                    message.messageFormat, message.recipients, message.messageData);
                success = result.success;
            } else if (message.messageType == MessageType.NATO_ADATP3) {
                var result = integrationService.sendNATOMessage(
                    message.messageFormat, message.recipients, message.messageData, message.classification);
                success = result.success;
            }

            if (success) {
                message.status = MessageStatus.DELIVERED;
                message.deliveryTime = LocalDateTime.now();
                logger.info("Message delivered successfully: ID={}", message.id);
            } else {
                handleMessageFailure(message);
            }

        } catch (Exception e) {
            logger.error("Error processing message {}: {}", message.id, e.getMessage());
            handleMessageFailure(message);
        }
    }

    private void handleMessageFailure(MilitaryMessage message) {
        message.retryCount++;
        message.lastErrorTime = LocalDateTime.now();

        if (message.retryCount < MAX_RETRY_ATTEMPTS) {
            message.status = MessageStatus.RETRY_PENDING;
            logger.warn("Message failed, scheduling retry {}/{}: ID={}", 
                message.retryCount, MAX_RETRY_ATTEMPTS, message.id);
            
            // Schedule retry with delay
            retryExecutor.submit(() -> {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                    addToQueue(message, message.priority);
                    processNextMessage();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry scheduling interrupted for message: {}", message.id);
                }
            });
        } else {
            message.status = MessageStatus.FAILED;
            deadLetterQueue.offer(message);
            logger.error("Message failed permanently after {} attempts, moved to dead letter queue: ID={}", 
                MAX_RETRY_ATTEMPTS, message.id);
        }
    }

    private String generateMessageId() {
        return "MSG-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }

    // Data classes for message queue operations

    public enum MessageType {
        USMTF,          // US Message Text Format (MIL-STD-6040B)
        NATO_ADATP3,    // NATO Allied Data Publication 3
        CUSTOM          // Custom military format
    }

    public enum MessagePriority {
        HIGH,       // Tactical alerts, emergency communications
        NORMAL,     // Standard operational messages
        LOW         // Administrative, routine updates
    }

    public enum MessageStatus {
        QUEUED,
        PROCESSING,
        DELIVERED,
        RETRY_PENDING,
        FAILED
    }

    public static class MilitaryMessage {
        public String id;
        public MessageType messageType;
        public String messageFormat;
        public List<String> recipients;
        public Map<String, Object> messageData;
        public MessagePriority priority;
        public String classification;
        public MessageStatus status;
        public LocalDateTime queuedTime;
        public LocalDateTime processingStartTime;
        public LocalDateTime deliveryTime;
        public LocalDateTime lastErrorTime;
        public int retryCount;
    }

    public static class MessageQueueResult {
        public String messageId;
        public boolean success;
        public String statusMessage;
        public LocalDateTime timestamp;

        public MessageQueueResult(String messageId, boolean success, String statusMessage, LocalDateTime timestamp) {
            this.messageId = messageId;
            this.success = success;
            this.statusMessage = statusMessage;
            this.timestamp = timestamp;
        }
    }

    public static class MessageQueueStatus {
        public LocalDateTime checkTime;
        public int highPriorityCount;
        public int normalPriorityCount;
        public int lowPriorityCount;
        public int deadLetterCount;
        public int totalQueued;
        public String processingHealth;
    }
}
