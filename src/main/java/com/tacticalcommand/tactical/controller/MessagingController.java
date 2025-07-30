package com.tacticalcommand.tactical.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tacticalcommand.tactical.domain.TacticalMessage;
import com.tacticalcommand.tactical.dto.MessageRequest;
import com.tacticalcommand.tactical.dto.MessageResponse;
import com.tacticalcommand.tactical.service.MessagingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for tactical messaging operations.
 * Provides secure endpoints for sending, receiving, and managing tactical messages.
 */
@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Tactical Messaging", description = "Secure tactical communication endpoints")
@SecurityRequirement(name = "bearerAuth")
public class MessagingController {

    @Autowired
    private MessagingService messagingService;

    /**
     * Send a new tactical message.
     *
     * @param request Message details including content, recipients, and classification
     * @return Created message details
     */
    @PostMapping
    @Operation(summary = "Send tactical message", 
               description = "Send a secure message to specified recipients or groups")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Message sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid message request"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions for classification level"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('OPERATOR') or hasRole('COMMANDER')")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        try {
            TacticalMessage message = messagingService.sendMessage(request);
            MessageResponse response = convertToResponse(message);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get messages for the current user with pagination.
     *
     * @param page Page number (0-based)
     * @param size Page size
     * @return Page of messages for the authenticated user
     */
    @GetMapping("/inbox")
    @Operation(summary = "Get user messages", 
               description = "Retrieve paginated messages for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<MessageResponse>> getUserMessages(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TacticalMessage> messages = messagingService.getMessagesForCurrentUser(pageable);
            Page<MessageResponse> responses = messages.map(this::convertToResponse);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get messages by classification level for authorized users.
     *
     * @param classification Message classification level
     * @param page Page number (0-based)
     * @param size Page size
     * @return Page of messages with specified classification
     */
    @GetMapping("/classification/{classification}")
    @Operation(summary = "Get messages by classification", 
               description = "Retrieve messages by security classification level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient clearance for classification level"),
        @ApiResponse(responseCode = "404", description = "Classification not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<Page<MessageResponse>> getMessagesByClassification(
            @Parameter(description = "Security classification level") @PathVariable String classification,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TacticalMessage> messages = messagingService.getMessagesByClassification(classification, pageable);
            Page<MessageResponse> responses = messages.map(this::convertToResponse);
            return ResponseEntity.ok(responses);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mark a message as read.
     *
     * @param messageId ID of the message to mark as read
     * @return Updated message details
     */
    @PutMapping("/{messageId}/read")
    @Operation(summary = "Mark message as read", 
               description = "Mark a specific message as read by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message marked as read"),
        @ApiResponse(responseCode = "403", description = "Access denied to message"),
        @ApiResponse(responseCode = "404", description = "Message not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MessageResponse> markMessageAsRead(
            @Parameter(description = "Message ID") @PathVariable Long messageId) {
        try {
            TacticalMessage message = messagingService.markMessageAsRead(messageId);
            MessageResponse response = convertToResponse(message);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get unread message count for the current user.
     *
     * @return Number of unread messages
     */
    @GetMapping("/unread-count")
    @Operation(summary = "Get unread message count", 
               description = "Get the count of unread messages for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unread count retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> getUnreadMessageCount() {
        try {
            long count = messagingService.getUnreadMessageCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Send emergency alert to all authorized users.
     *
     * @param alertMessage Emergency alert content
     * @param classification Alert classification level
     * @return Created alert message
     */
    @PostMapping("/emergency-alert")
    @Operation(summary = "Send emergency alert", 
               description = "Send emergency alert to all users with appropriate clearance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Emergency alert sent successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions to send alerts"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('COMMANDER')")
    public ResponseEntity<MessageResponse> sendEmergencyAlert(
            @Parameter(description = "Alert message content") @RequestParam String alertMessage,
            @Parameter(description = "Alert classification level") @RequestParam(defaultValue = "UNCLASSIFIED") String classification) {
        try {
            TacticalMessage alert = messagingService.sendEmergencyAlert(alertMessage, classification);
            MessageResponse response = convertToResponse(alert);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Convert TacticalMessage entity to MessageResponse DTO.
     *
     * @param message TacticalMessage entity
     * @return MessageResponse DTO
     */
    private MessageResponse convertToResponse(TacticalMessage message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setSenderName(message.getSender().getUsername());
        response.setMessageType(message.getMessageType());
        response.setClassification(message.getClassification());
        response.setSubject(message.getSubject());
        response.setContent(message.getContent());
        response.setCreatedAt(message.getCreatedAt());
        response.setReadAt(message.getReadAt());
        response.setDeliveryStatus(message.getDeliveryStatus());
        response.setPriorityLevel(message.getPriorityLevel());
        response.setThreadId(message.getThreadId());
        response.setEncrypted(message.isEncrypted());
        return response;
    }
}
