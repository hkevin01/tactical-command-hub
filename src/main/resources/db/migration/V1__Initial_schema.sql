-- Initial database schema for Tactical Command Hub
-- Creates tables for military units, missions, and related entities

-- Military Units table
CREATE TABLE military_units (
    id BIGSERIAL PRIMARY KEY,
    callsign VARCHAR(50) NOT NULL UNIQUE,
    unit_name VARCHAR(100) NOT NULL,
    unit_type VARCHAR(20) NOT NULL,
    domain VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    readiness_level VARCHAR(20) NOT NULL,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    altitude DECIMAL(8,2),
    heading DECIMAL(5,2),
    speed DECIMAL(7,2),
    personnel_count INTEGER,
    equipment VARCHAR(500),
    last_contact TIMESTAMP,
    communication_status VARCHAR(20),
    threat_level VARCHAR(10),
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- Unit Status History table
CREATE TABLE unit_status_history (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    previous_status VARCHAR(20) NOT NULL,
    new_status VARCHAR(20) NOT NULL,
    status_changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    change_reason VARCHAR(255),
    changed_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT fk_unit_status_history_unit FOREIGN KEY (unit_id) REFERENCES military_units(id) ON DELETE CASCADE
);

-- Missions table
CREATE TABLE missions (
    id BIGSERIAL PRIMARY KEY,
    mission_code VARCHAR(50) NOT NULL UNIQUE,
    mission_name VARCHAR(100) NOT NULL,
    mission_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(10) NOT NULL,
    objective VARCHAR(1000),
    description VARCHAR(2000),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    estimated_duration_hours INTEGER,
    target_latitude DECIMAL(10,8),
    target_longitude DECIMAL(11,8),
    target_location VARCHAR(200),
    commander VARCHAR(100),
    assigned_unit_id BIGINT,
    completion_percentage INTEGER DEFAULT 0,
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT fk_missions_assigned_unit FOREIGN KEY (assigned_unit_id) REFERENCES military_units(id) ON DELETE SET NULL
);

-- Mission Waypoints table
CREATE TABLE mission_waypoints (
    id BIGSERIAL PRIMARY KEY,
    mission_id BIGINT NOT NULL,
    sequence_number INTEGER NOT NULL,
    waypoint_name VARCHAR(100),
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    altitude DECIMAL(8,2),
    waypoint_type VARCHAR(20) NOT NULL,
    estimated_arrival_time TIMESTAMP,
    actual_arrival_time TIMESTAMP,
    is_reached BOOLEAN DEFAULT FALSE,
    description VARCHAR(500),
    action_required VARCHAR(300),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT fk_waypoints_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE,
    CONSTRAINT uk_waypoint_sequence UNIQUE (mission_id, sequence_number)
);

-- Mission Reports table
CREATE TABLE mission_reports (
    id BIGSERIAL PRIMARY KEY,
    mission_id BIGINT NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    report_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reporter VARCHAR(100) NOT NULL,
    reporter_rank VARCHAR(100),
    report_content VARCHAR(2000) NOT NULL,
    status_update VARCHAR(20),
    completion_percentage INTEGER,
    casualties_friendly INTEGER,
    casualties_enemy INTEGER,
    equipment_status VARCHAR(500),
    resource_consumption VARCHAR(500),
    threat_level VARCHAR(10),
    weather_conditions VARCHAR(200),
    next_checkpoint_eta TIMESTAMP,
    requires_support BOOLEAN DEFAULT FALSE,
    support_requested VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    
    CONSTRAINT fk_reports_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_unit_callsign ON military_units(callsign);
CREATE INDEX idx_unit_status ON military_units(status);
CREATE INDEX idx_unit_domain ON military_units(domain);
CREATE INDEX idx_unit_position ON military_units(latitude, longitude);
CREATE INDEX idx_unit_last_contact ON military_units(last_contact);
CREATE INDEX idx_unit_readiness ON military_units(readiness_level);
CREATE INDEX idx_unit_threat ON military_units(threat_level);

CREATE INDEX idx_status_history_unit ON unit_status_history(unit_id);
CREATE INDEX idx_status_history_timestamp ON unit_status_history(status_changed_at);

CREATE INDEX idx_mission_code ON missions(mission_code);
CREATE INDEX idx_mission_status ON missions(status);
CREATE INDEX idx_mission_priority ON missions(priority);
CREATE INDEX idx_mission_start_time ON missions(start_time);
CREATE INDEX idx_mission_assigned_unit ON missions(assigned_unit_id);
CREATE INDEX idx_mission_type ON missions(mission_type);

CREATE INDEX idx_waypoint_mission ON mission_waypoints(mission_id);
CREATE INDEX idx_waypoint_sequence ON mission_waypoints(mission_id, sequence_number);

CREATE INDEX idx_report_mission ON mission_reports(mission_id);
CREATE INDEX idx_report_timestamp ON mission_reports(report_timestamp);
CREATE INDEX idx_report_type ON mission_reports(report_type);

-- Add constraints for enum-like values
ALTER TABLE military_units ADD CONSTRAINT chk_unit_type 
    CHECK (unit_type IN ('INFANTRY', 'ARMOR', 'ARTILLERY', 'AIR_DEFENSE', 'AVIATION', 'NAVAL', 'CYBER', 'LOGISTICS', 'COMMAND', 'INTELLIGENCE'));

ALTER TABLE military_units ADD CONSTRAINT chk_domain 
    CHECK (domain IN ('LAND', 'AIR', 'SEA', 'CYBER', 'SPACE'));

ALTER TABLE military_units ADD CONSTRAINT chk_status 
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'DEPLOYED', 'STANDBY', 'MAINTENANCE', 'OFFLINE'));

ALTER TABLE military_units ADD CONSTRAINT chk_readiness 
    CHECK (readiness_level IN ('C1', 'C2', 'C3', 'C4'));

ALTER TABLE military_units ADD CONSTRAINT chk_comm_status 
    CHECK (communication_status IN ('OPERATIONAL', 'DEGRADED', 'OFFLINE', 'UNKNOWN'));

ALTER TABLE military_units ADD CONSTRAINT chk_threat_level 
    CHECK (threat_level IN ('LOW', 'MODERATE', 'HIGH', 'CRITICAL'));

ALTER TABLE missions ADD CONSTRAINT chk_mission_type 
    CHECK (mission_type IN ('RECONNAISSANCE', 'PATROL', 'ASSAULT', 'DEFENSE', 'LOGISTICS', 'TRAINING', 'SEARCH_AND_RESCUE', 'PEACEKEEPING', 'CYBER_OPERATION', 'INTELLIGENCE'));

ALTER TABLE missions ADD CONSTRAINT chk_mission_status 
    CHECK (status IN ('PLANNING', 'APPROVED', 'ACTIVE', 'SUSPENDED', 'COMPLETED', 'CANCELLED', 'ABORTED'));

ALTER TABLE missions ADD CONSTRAINT chk_mission_priority 
    CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'));

ALTER TABLE mission_waypoints ADD CONSTRAINT chk_waypoint_type 
    CHECK (waypoint_type IN ('START', 'CHECKPOINT', 'OBJECTIVE', 'RALLY_POINT', 'EXTRACTION', 'END'));

ALTER TABLE mission_reports ADD CONSTRAINT chk_report_type 
    CHECK (report_type IN ('SITREP', 'PROGRESS', 'CONTACT', 'CASUALTY', 'EQUIPMENT', 'RESUPPLY', 'WEATHER', 'COMPLETION'));

-- Create update timestamp triggers
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_military_units_updated_at BEFORE UPDATE ON military_units 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_unit_status_history_updated_at BEFORE UPDATE ON unit_status_history 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_missions_updated_at BEFORE UPDATE ON missions 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_mission_waypoints_updated_at BEFORE UPDATE ON mission_waypoints 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_mission_reports_updated_at BEFORE UPDATE ON mission_reports 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
