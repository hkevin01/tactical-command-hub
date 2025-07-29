-- User authentication and authorization tables
-- Creates tables for users, roles, and user-role associations

-- Roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    rank VARCHAR(50),
    unit_assignment VARCHAR(100),
    clearance_level VARCHAR(50),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- User-Role association table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_enabled ON users(enabled);
CREATE INDEX idx_users_unit_assignment ON users(unit_assignment);
CREATE INDEX idx_users_clearance_level ON users(clearance_level);
CREATE INDEX idx_users_rank ON users(rank);

CREATE INDEX idx_roles_name ON roles(name);

CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);

-- Add constraints for role names
ALTER TABLE roles ADD CONSTRAINT chk_role_name 
    CHECK (name IN ('ROLE_COMMANDER', 'ROLE_OPERATOR', 'ROLE_ANALYST', 'ROLE_VIEWER'));

-- Create update timestamp triggers for user and role tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_roles_updated_at BEFORE UPDATE ON roles 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert default roles
INSERT INTO roles (name, description, created_by) VALUES
('ROLE_COMMANDER', 'Full system access with mission command authority', 'SYSTEM'),
('ROLE_OPERATOR', 'Operational access for mission planning and execution', 'SYSTEM'),
('ROLE_ANALYST', 'Intelligence analysis and reporting capabilities', 'SYSTEM'),
('ROLE_VIEWER', 'Read-only access for monitoring and basic reporting', 'SYSTEM');

-- Create default admin user (password: admin123 - BCrypt encoded)
-- Password should be changed on first login in production
INSERT INTO users (username, full_name, email, password, rank, unit_assignment, clearance_level, created_by) VALUES
('admin', 'System Administrator', 'admin@tactical-command.mil', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKyMzqLzFaTww7C', 'COL', 'COMMAND', 'TOP_SECRET', 'SYSTEM'),
('commander1', 'John Smith', 'j.smith@tactical-command.mil', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKyMzqLzFaTww7C', 'LTC', '1st Infantry Division', 'SECRET', 'SYSTEM'),
('operator1', 'Sarah Johnson', 's.johnson@tactical-command.mil', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKyMzqLzFaTww7C', 'MAJ', '2nd Armored Brigade', 'SECRET', 'SYSTEM'),
('analyst1', 'Michael Brown', 'm.brown@tactical-command.mil', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKyMzqLzFaTww7C', 'CPT', 'Intelligence Battalion', 'TOP_SECRET', 'SYSTEM'),
('viewer1', 'Emily Davis', 'e.davis@tactical-command.mil', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKyMzqLzFaTww7C', 'LT', 'Logistics Support', 'CONFIDENTIAL', 'SYSTEM');

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES
-- Admin user gets COMMANDER role
(1, 1),
-- Commander user gets COMMANDER role
(2, 1),
-- Operator user gets OPERATOR role
(3, 2),
-- Analyst user gets ANALYST role
(4, 3),
-- Viewer user gets VIEWER role
(5, 4);

-- Additional role assignments (some users can have multiple roles)
INSERT INTO user_roles (user_id, role_id) VALUES
-- Commander also has operator capabilities
(2, 2),
-- Analyst also has viewer capabilities
(4, 4);
