package com.tacticalcommand.tactical.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalcommand.tactical.domain.TacticalMessage;
import com.tacticalcommand.tactical.domain.User;
import com.tacticalcommand.tactical.dto.MessageRequest;
import com.tacticalcommand.tactical.dto.MessageResponse;
import com.tacticalcommand.tactical.repository.TacticalMessageRepository;

/**
 * Service for handling secure real-time messaging between command centers and tactical units.
 * Provides end-to-end encryption, message classification, and real-time communication capabilities.
 */
@Service
@Transactional
public class MessagingService {

    @Autowired
    private TacticalMessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserService userService;

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * Send a secure message to specified recipients.
     *
     * @param request Message details including content, recipients, and classification
     * @return Created message with delivery status
     */
    public TacticalMessage sendMessage(MessageRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User sender = userService.findByUsername(auth.getName());

        TacticalMessage message = new TacticalMessage();
        message.setSender(sender);
        message.setMessageType(request.getMessageType());
        message.setClassification(request.getClassification());
        message.setSubject(request.getSubject());
        message.setContent(request.getContent());
        message.setCreatedAt(LocalDateTime.now());
        message.setDeliveryStatus("SENT");

        // Encrypt sensitive content based on classification
        if (!"UNCLASSIFIED".equals(request.getClassification())) {
            try {
                String encryptedContent = encryptMessage(request.getContent());
                message.setEncryptedContent(encryptedContent.getBytes());
            } catch (Exception e) {
                throw new RuntimeException("Failed to encrypt message content", e);
            }
        }

        // Save message to database
        TacticalMessage savedMessage = messageRepository.save(message);

        // Send real-time notification to recipients
        if (request.getRecipientIds() != null && !request.getRecipientIds().isEmpty()) {
            for (Long recipientId : request.getRecipientIds()) {
                sendRealTimeNotification(recipientId, savedMessage);
            }
        }

        // Send to group if specified
        if (request.getGroupId() != null) {
            sendGroupNotification(request.getGroupId(), savedMessage);
        }

        return savedMessage;
    }

    /**
     * Retrieve messages for the current user with pagination.
     *
     * @param pageable Pagination parameters
     * @return Page of messages for the authenticated user
     */
    public Page<TacticalMessage> getMessagesForCurrentUser(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());
        
        return messageRepository.findByRecipientOrSenderOrderByCreatedAtDesc(
            currentUser, currentUser, pageable);
    }

    /**
     * Get messages by classification level for authorized users.
     *
     * @param classification Message classification level
     * @param pageable Pagination parameters
     * @return Page of messages matching classification
     */
    public Page<TacticalMessage> getMessagesByClassification(String classification, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        // Verify user has clearance for requested classification
        if (!hasRequiredClearance(currentUser, classification)) {
            throw new SecurityException("Insufficient clearance for requested classification level");
        }

        return messageRepository.findByClassificationOrderByCreatedAtDesc(classification, pageable);
    }

    /**
     * Mark message as read by the current user.
     *
     * @param messageId ID of the message to mark as read
     * @return Updated message
     */
    public TacticalMessage markMessageAsRead(Long messageId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        Optional<TacticalMessage> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new RuntimeException("Message not found: " + messageId);
        }

        TacticalMessage message = messageOpt.get();
        
        // Verify user has access to this message
        if (!canAccessMessage(currentUser, message)) {
            throw new SecurityException("Access denied to message: " + messageId);
        }

        message.setReadAt(LocalDateTime.now());
        return messageRepository.save(message);
    }

    /**
     * Get unread message count for the current user.
     *
     * @return Number of unread messages
     */
    public long getUnreadMessageCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());
        
        return messageRepository.countByRecipientAndReadAtIsNull(currentUser);
    }

    /**
     * Send emergency alert to all users with appropriate clearance.
     *
     * @param alertMessage Emergency alert content
     * @param classification Alert classification level
     * @return Created alert message
     */
    public TacticalMessage sendEmergencyAlert(String alertMessage, String classification) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User sender = userService.findByUsername(auth.getName());

        TacticalMessage alert = new TacticalMessage();
        alert.setSender(sender);
        alert.setMessageType("EMERGENCY_ALERT");
        alert.setClassification(classification);
        alert.setSubject("EMERGENCY ALERT");
        alert.setContent(alertMessage);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setDeliveryStatus("BROADCAST");

        TacticalMessage savedAlert = messageRepository.save(alert);

        // Broadcast to all authorized users
        List<User> authorizedUsers = userService.findUsersByMinimumClearance(classification);
        for (User user : authorizedUsers) {
            sendRealTimeNotification(user.getId(), savedAlert);
        }

        return savedAlert;
    }

    /**
     * Send real-time notification to specific user via WebSocket.
     *
     * @param recipientId ID of the recipient user
     * @param message Message to send
     */
    private void sendRealTimeNotification(Long recipientId, TacticalMessage message) {
        try {
            MessageResponse response = new MessageResponse();
            response.setId(message.getId());
            response.setSenderName(message.getSender().getUsername());
            response.setMessageType(message.getMessageType());
            response.setClassification(message.getClassification());
            response.setSubject(message.getSubject());
            response.setContent(message.getContent());
            response.setCreatedAt(message.getCreatedAt());

            messagingTemplate.convertAndSend("/topic/user/" + recipientId, response);
        } catch (Exception e) {
            // Log error but don't fail the message send
            System.err.println("Failed to send real-time notification to user " + recipientId + ": " + e.getMessage());
        }
    }

    /**
     * Send notification to all members of a group.
     *
     * @param groupId ID of the target group
     * @param message Message to send
     */
    private void sendGroupNotification(Long groupId, TacticalMessage message) {
        try {
            MessageResponse response = new MessageResponse();
            response.setId(message.getId());
            response.setSenderName(message.getSender().getUsername());
            response.setMessageType(message.getMessageType());
            response.setClassification(message.getClassification());
            response.setSubject(message.getSubject());
            response.setContent(message.getContent());
            response.setCreatedAt(message.getCreatedAt());

            messagingTemplate.convertAndSend("/topic/group/" + groupId, response);
        } catch (Exception e) {
            System.err.println("Failed to send group notification: " + e.getMessage());
        }
    }

    /**
     * Encrypt message content using AES encryption.
     *
     * @param content Plain text content to encrypt
     * @return Base64 encoded encrypted content
     */
    private String encryptMessage(String content) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        byte[] encryptedBytes = cipher.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypt message content.
     *
     * @param encryptedContent Base64 encoded encrypted content
     * @param secretKey Secret key for decryption
     * @return Decrypted plain text content
     */
    private String decryptMessage(String encryptedContent, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
        return new String(decryptedBytes);
    }

    /**
     * Check if user has required security clearance for message classification.
     *
     * @param user User to check clearance for
     * @param classification Required classification level
     * @return true if user has sufficient clearance
     */
    private boolean hasRequiredClearance(User user, String classification) {
        String userClearance = user.getClearanceLevel();
        
        // Simple clearance hierarchy check
        if ("UNCLASSIFIED".equals(classification)) return true;
        if ("CONFIDENTIAL".equals(classification)) return !"UNCLASSIFIED".equals(userClearance);
        if ("SECRET".equals(classification)) return "SECRET".equals(userClearance) || "TOP_SECRET".equals(userClearance);
        if ("TOP_SECRET".equals(classification)) return "TOP_SECRET".equals(userClearance);
        
        return false;
    }

    /**
     * Check if user can access a specific message.
     *
     * @param user User requesting access
     * @param message Message to check access for
     * @return true if user can access the message
     */
    private boolean canAccessMessage(User user, TacticalMessage message) {
        return message.getSender().equals(user) || 
               hasRequiredClearance(user, message.getClassification());
    }
}
