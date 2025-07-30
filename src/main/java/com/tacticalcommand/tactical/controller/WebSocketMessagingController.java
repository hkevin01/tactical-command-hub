package com.tacticalcommand.tactical.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.tacticalcommand.tactical.dto.MessageRequest;
import com.tacticalcommand.tactical.dto.MessageResponse;
import com.tacticalcommand.tactical.service.MessagingService;

/**
 * WebSocket controller for real-time tactical messaging.
 * Handles WebSocket connections and real-time message broadcasting.
 */
@Controller
public class WebSocketMessagingController {

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Handle real-time message sending via WebSocket.
     *
     * @param messageRequest Message details
     * @param principal Authenticated user principal
     * @return Message response for confirmation
     */
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public MessageResponse sendMessage(@Payload MessageRequest messageRequest, Principal principal) {
        try {
            // Send message through the messaging service
            var message = messagingService.sendMessage(messageRequest);
            
            // Convert to response DTO
            MessageResponse response = new MessageResponse();
            response.setId(message.getId());
            response.setSenderName(message.getSender().getUsername());
            response.setMessageType(message.getMessageType());
            response.setClassification(message.getClassification());
            response.setSubject(message.getSubject());
            response.setContent(message.getContent());
            response.setCreatedAt(message.getCreatedAt());
            response.setDeliveryStatus(message.getDeliveryStatus());
            response.setPriorityLevel(message.getPriorityLevel());
            response.setThreadId(message.getThreadId());
            
            return response;
        } catch (Exception e) {
            // Create error response
            MessageResponse errorResponse = new MessageResponse();
            errorResponse.setContent("Error sending message: " + e.getMessage());
            errorResponse.setDeliveryStatus("FAILED");
            return errorResponse;
        }
    }

    /**
     * Handle user joining a conversation thread.
     *
     * @param threadId Thread identifier
     * @param headerAccessor WebSocket header accessor
     * @param principal Authenticated user principal
     */
    @MessageMapping("/join")
    public void joinThread(@Payload String threadId, 
                          SimpMessageHeaderAccessor headerAccessor, 
                          Principal principal) {
        try {
            // Add user to thread session with null check
            var sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                sessionAttributes.put("threadId", threadId);
                sessionAttributes.put("username", principal.getName());
            }
            
            // Notify thread participants
            messagingTemplate.convertAndSend("/topic/thread/" + threadId + "/users", 
                principal.getName() + " joined the conversation");
        } catch (Exception e) {
            // Log error but don't fail the connection
            System.err.println("Error joining thread: " + e.getMessage());
        }
    }

    /**
     * Handle user leaving a conversation thread.
     *
     * @param threadId Thread identifier
     * @param headerAccessor WebSocket header accessor
     * @param principal Authenticated user principal
     */
    @MessageMapping("/leave")
    public void leaveThread(@Payload String threadId, 
                           SimpMessageHeaderAccessor headerAccessor, 
                           Principal principal) {
        try {
            // Remove user from thread session with null check
            var sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                sessionAttributes.remove("threadId");
            }
            
            // Notify thread participants
            messagingTemplate.convertAndSend("/topic/thread/" + threadId + "/users", 
                principal.getName() + " left the conversation");
        } catch (Exception e) {
            // Log error but don't fail the connection
            System.err.println("Error leaving thread: " + e.getMessage());
        }
    }

    /**
     * Handle typing indicator for real-time feedback.
     *
     * @param threadId Thread identifier where user is typing
     * @param principal Authenticated user principal
     */
    @MessageMapping("/typing")
    public void handleTyping(@Payload String threadId, Principal principal) {
        try {
            // Send typing indicator to thread participants
            messagingTemplate.convertAndSend("/topic/thread/" + threadId + "/typing", 
                principal.getName() + " is typing...");
        } catch (Exception e) {
            // Log error but don't fail the connection
            System.err.println("Error handling typing indicator: " + e.getMessage());
        }
    }

    /**
     * Handle emergency alert broadcasting.
     *
     * @param alertMessage Emergency alert content
     * @param principal Authenticated user principal (must be COMMANDER)
     */
    @MessageMapping("/emergency")
    @SendTo("/topic/emergency")
    public MessageResponse broadcastEmergencyAlert(@Payload String alertMessage, Principal principal) {
        try {
            // Verify user has commander role (additional check)
            Authentication auth = (Authentication) principal;
            boolean isCommander = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COMMANDER"));
            
            if (!isCommander) {
                MessageResponse errorResponse = new MessageResponse();
                errorResponse.setContent("Access denied: Commander role required");
                errorResponse.setDeliveryStatus("FAILED");
                return errorResponse;
            }
            
            // Send emergency alert
            var alert = messagingService.sendEmergencyAlert(alertMessage, "UNCLASSIFIED");
            
            // Convert to response DTO
            MessageResponse response = new MessageResponse();
            response.setId(alert.getId());
            response.setSenderName(alert.getSender().getUsername());
            response.setMessageType(alert.getMessageType());
            response.setClassification(alert.getClassification());
            response.setSubject(alert.getSubject());
            response.setContent(alert.getContent());
            response.setCreatedAt(alert.getCreatedAt());
            response.setDeliveryStatus(alert.getDeliveryStatus());
            response.setPriorityLevel(1); // Emergency alerts are always high priority
            
            return response;
        } catch (Exception e) {
            // Create error response
            MessageResponse errorResponse = new MessageResponse();
            errorResponse.setContent("Error broadcasting emergency alert: " + e.getMessage());
            errorResponse.setDeliveryStatus("FAILED");
            return errorResponse;
        }
    }
}
