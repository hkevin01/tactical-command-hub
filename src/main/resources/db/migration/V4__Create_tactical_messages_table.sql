-- Create tactical_messages table for secure messaging functionality
-- Migration: V4__Create_tactical_messages_table.sql
CREATE TABLE tactical_messages (
  id BIGSERIAL PRIMARY KEY,
  sender_id BIGINT NOT NULL,
  recipient_id BIGINT,
  message_type VARCHAR(50) NOT NULL,
  classification VARCHAR(20) NOT NULL DEFAULT 'UNCLASSIFIED',
  subject VARCHAR(255),
  content TEXT,
  encrypted_content BYTEA,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  read_at TIMESTAMP WITHOUT TIME ZONE,
  delivery_status VARCHAR(20),
  priority_level INTEGER DEFAULT 3,
  group_id BIGINT,
  thread_id VARCHAR(100),
  expires_at TIMESTAMP WITHOUT TIME ZONE,
  -- Foreign key constraints
  CONSTRAINT fk_tactical_messages_sender FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_tactical_messages_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
  -- Constraints
  CONSTRAINT chk_priority_level CHECK (
    priority_level BETWEEN 1 AND 3
  ),
  CONSTRAINT chk_classification CHECK (
    classification IN (
      'UNCLASSIFIED',
      'CONFIDENTIAL',
      'SECRET',
      'TOP_SECRET'
    )
  ),
  CONSTRAINT chk_message_type CHECK (
    message_type IN (
      'TEXT',
      'ALERT',
      'REPORT',
      'ORDER',
      'REQUEST',
      'EMERGENCY_ALERT',
      'STATUS_UPDATE'
    )
  ),
  CONSTRAINT chk_delivery_status CHECK (
    delivery_status IN (
      'SENT',
      'DELIVERED',
      'READ',
      'FAILED',
      'BROADCAST'
    )
  )
);
-- Create indexes for performance
CREATE INDEX idx_tactical_messages_sender_id ON tactical_messages(sender_id);
CREATE INDEX idx_tactical_messages_recipient_id ON tactical_messages(recipient_id);
CREATE INDEX idx_tactical_messages_created_at ON tactical_messages(created_at);
CREATE INDEX idx_tactical_messages_classification ON tactical_messages(classification);
CREATE INDEX idx_tactical_messages_message_type ON tactical_messages(message_type);
CREATE INDEX idx_tactical_messages_priority_level ON tactical_messages(priority_level);
CREATE INDEX idx_tactical_messages_thread_id ON tactical_messages(thread_id);
CREATE INDEX idx_tactical_messages_group_id ON tactical_messages(group_id);
CREATE INDEX idx_tactical_messages_read_at ON tactical_messages(read_at);
CREATE INDEX idx_tactical_messages_delivery_status ON tactical_messages(delivery_status);
-- Composite indexes for common queries
CREATE INDEX idx_tactical_messages_recipient_read ON tactical_messages(recipient_id, read_at);
CREATE INDEX idx_tactical_messages_sender_created ON tactical_messages(sender_id, created_at);
CREATE INDEX idx_tactical_messages_class_created ON tactical_messages(classification, created_at);
-- Comments for documentation
COMMENT ON TABLE tactical_messages IS 'Secure tactical messages between command centers and units';
COMMENT ON COLUMN tactical_messages.sender_id IS 'User who sent the message';
COMMENT ON COLUMN tactical_messages.recipient_id IS 'User who receives the message (null for broadcast)';
COMMENT ON COLUMN tactical_messages.message_type IS 'Type of message (TEXT, ALERT, REPORT, etc.)';
COMMENT ON COLUMN tactical_messages.classification IS 'Security classification level';
COMMENT ON COLUMN tactical_messages.subject IS 'Message subject line';
COMMENT ON COLUMN tactical_messages.content IS 'Plain text message content';
COMMENT ON COLUMN tactical_messages.encrypted_content IS 'Encrypted message content for classified messages';
COMMENT ON COLUMN tactical_messages.created_at IS 'Timestamp when message was created';
COMMENT ON COLUMN tactical_messages.read_at IS 'Timestamp when message was read by recipient';
COMMENT ON COLUMN tactical_messages.delivery_status IS 'Current delivery status of the message';
COMMENT ON COLUMN tactical_messages.priority_level IS 'Message priority (1=HIGH, 2=MEDIUM, 3=LOW)';
COMMENT ON COLUMN tactical_messages.group_id IS 'Target group for group messages';
COMMENT ON COLUMN tactical_messages.thread_id IS 'Thread identifier for conversation tracking';
COMMENT ON COLUMN tactical_messages.expires_at IS 'Expiration timestamp for temporary messages';