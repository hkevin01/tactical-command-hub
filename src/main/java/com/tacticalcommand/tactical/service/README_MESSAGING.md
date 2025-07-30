# Real-time Communication & Messaging Service

Spring Boot service for secure real-time messaging between command centers and tactical units.

## Features

### Secure Messaging Infrastructure
- End-to-end encryption using AES-256
- Message classification support (UNCLASSIFIED, CONFIDENTIAL, SECRET)
- Message retention and archival policies
- Group messaging capabilities
- Message delivery confirmation

### Real-time Communication
- WebSocket support for live messaging
- Server-Sent Events for status updates
- Push notification framework
- Connection resilience and reconnection logic
- Presence and activity tracking

### Message Types
- Tactical Updates
- Situation Reports (SITREP)
- Orders and Directives
- Alert Notifications
- Status Changes
- Emergency Communications

## Implementation

### WebSocket Configuration
```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TacticalMessageHandler(), "/ws/tactical")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
```

### Message Security
- JWT token validation for WebSocket connections
- Role-based message access control
- Message classification enforcement
- Audit logging for all communications

### Database Schema
```sql
CREATE TABLE tactical_messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT,
    group_id BIGINT,
    message_type VARCHAR(50) NOT NULL,
    classification VARCHAR(20) NOT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    encrypted_content BYTEA,
    delivery_status VARCHAR(20) DEFAULT 'SENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP
);
```

## Usage

### Send Message
```java
@PostMapping("/api/messages")
public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
    TacticalMessage message = messagingService.sendMessage(request);
    return ResponseEntity.ok(new MessageResponse(message));
}
```

### Real-time Updates
```javascript
const socket = new SockJS('/ws/tactical');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    stompClient.subscribe('/topic/messages', function(message) {
        displayMessage(JSON.parse(message.body));
    });
});
```

## Security Considerations

- All messages encrypted in transit and at rest
- Classification level enforcement
- User authorization for message access
- Comprehensive audit logging
- Message retention policies by classification

## Dependencies

- Spring WebSocket
- Spring Security
- Spring Data JPA
- Jackson for JSON processing
- SockJS for WebSocket fallback
