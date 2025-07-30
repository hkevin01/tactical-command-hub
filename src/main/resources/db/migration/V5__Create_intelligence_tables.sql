-- Create intelligence and threat analysis tables
-- Migration: V5__Create_intelligence_tables.sql
-- Intelligence Reports table
CREATE TABLE intelligence_reports (
  id BIGSERIAL PRIMARY KEY,
  report_number VARCHAR(50) UNIQUE NOT NULL,
  title VARCHAR(255) NOT NULL,
  classification VARCHAR(20) NOT NULL DEFAULT 'UNCLASSIFIED',
  threat_level VARCHAR(20),
  confidence_level VARCHAR(20),
  source_reliability VARCHAR(10),
  content TEXT,
  summary TEXT,
  geographic_area VARCHAR(255),
  latitude DECIMAL(10, 8),
  longitude DECIMAL(11, 8),
  radius_km DECIMAL(8, 2),
  threat_type VARCHAR(50),
  target_type VARCHAR(50),
  time_sensitivity VARCHAR(20),
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITHOUT TIME ZONE,
  expires_at TIMESTAMP WITHOUT TIME ZONE,
  dissemination_controls VARCHAR(100),
  author_id BIGINT NOT NULL,
  reviewed_by_id BIGINT,
  review_status VARCHAR(20) DEFAULT 'PENDING',
  source_id VARCHAR(100),
  source_type VARCHAR(50),
  -- Foreign key constraints
  CONSTRAINT fk_intelligence_reports_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_intelligence_reports_reviewer FOREIGN KEY (reviewed_by_id) REFERENCES users(id) ON DELETE
  SET NULL,
    -- Constraints
    CONSTRAINT chk_classification_intelligence CHECK (
      classification IN (
        'UNCLASSIFIED',
        'CONFIDENTIAL',
        'SECRET',
        'TOP_SECRET'
      )
    ),
    CONSTRAINT chk_threat_level CHECK (
      threat_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')
    ),
    CONSTRAINT chk_confidence_level CHECK (
      confidence_level IN ('LOW', 'MEDIUM', 'HIGH', 'CONFIRMED')
    ),
    CONSTRAINT chk_source_reliability CHECK (
      source_reliability IN ('A', 'B', 'C', 'D', 'E', 'F')
    ),
    CONSTRAINT chk_threat_type CHECK (
      threat_type IN (
        'KINETIC',
        'CYBER',
        'NBC',
        'CONVENTIONAL',
        'HYBRID'
      )
    ),
    CONSTRAINT chk_target_type CHECK (
      target_type IN (
        'PERSONNEL',
        'FACILITY',
        'EQUIPMENT',
        'COMMUNICATION',
        'INFRASTRUCTURE'
      )
    ),
    CONSTRAINT chk_time_sensitivity CHECK (
      time_sensitivity IN ('IMMEDIATE', 'PRIORITY', 'ROUTINE')
    ),
    CONSTRAINT chk_review_status CHECK (
      review_status IN ('PENDING', 'APPROVED', 'REJECTED')
    ),
    CONSTRAINT chk_source_type CHECK (
      source_type IN (
        'HUMINT',
        'SIGINT',
        'OSINT',
        'IMINT',
        'GEOINT',
        'MASINT'
      )
    )
);
-- Intelligence Tags table
CREATE TABLE intelligence_tags (
  id BIGSERIAL PRIMARY KEY,
  intelligence_report_id BIGINT NOT NULL,
  tag_name VARCHAR(100) NOT NULL,
  tag_category VARCHAR(50),
  description VARCHAR(255),
  -- Foreign key constraints
  CONSTRAINT fk_intelligence_tags_report FOREIGN KEY (intelligence_report_id) REFERENCES intelligence_reports(id) ON DELETE CASCADE,
  -- Constraints
  CONSTRAINT chk_tag_category CHECK (
    tag_category IN (
      'LOCATION',
      'THREAT',
      'ASSET',
      'OPERATION',
      'ACTOR',
      'METHOD'
    )
  )
);
-- Threat Correlations table
CREATE TABLE threat_correlations (
  id BIGSERIAL PRIMARY KEY,
  intelligence_report_id BIGINT NOT NULL,
  correlated_report_id BIGINT NOT NULL,
  correlation_type VARCHAR(50) NOT NULL,
  correlation_strength VARCHAR(20),
  confidence_score DECIMAL(3, 2),
  description TEXT,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by_id BIGINT NOT NULL,
  validation_status VARCHAR(20) DEFAULT 'PENDING',
  validated_by_id BIGINT,
  validated_at TIMESTAMP WITHOUT TIME ZONE,
  -- Foreign key constraints
  CONSTRAINT fk_threat_correlations_primary_report FOREIGN KEY (intelligence_report_id) REFERENCES intelligence_reports(id) ON DELETE CASCADE,
  CONSTRAINT fk_threat_correlations_correlated_report FOREIGN KEY (correlated_report_id) REFERENCES intelligence_reports(id) ON DELETE CASCADE,
  CONSTRAINT fk_threat_correlations_creator FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_threat_correlations_validator FOREIGN KEY (validated_by_id) REFERENCES users(id) ON DELETE
  SET NULL,
    -- Constraints
    CONSTRAINT chk_correlation_type CHECK (
      correlation_type IN (
        'LOCATION',
        'TIME',
        'THREAT_ACTOR',
        'METHOD',
        'TARGET',
        'TECHNIQUE'
      )
    ),
    CONSTRAINT chk_correlation_strength CHECK (
      correlation_strength IN ('WEAK', 'MODERATE', 'STRONG', 'CONFIRMED')
    ),
    CONSTRAINT chk_confidence_score CHECK (
      confidence_score BETWEEN 0.0 AND 1.0
    ),
    CONSTRAINT chk_validation_status CHECK (
      validation_status IN ('PENDING', 'VALIDATED', 'REJECTED')
    ),
    CONSTRAINT chk_different_reports CHECK (intelligence_report_id != correlated_report_id)
);
-- Create indexes for performance
CREATE INDEX idx_intelligence_reports_author_id ON intelligence_reports(author_id);
CREATE INDEX idx_intelligence_reports_classification ON intelligence_reports(classification);
CREATE INDEX idx_intelligence_reports_threat_level ON intelligence_reports(threat_level);
CREATE INDEX idx_intelligence_reports_created_at ON intelligence_reports(created_at);
CREATE INDEX idx_intelligence_reports_review_status ON intelligence_reports(review_status);
CREATE INDEX idx_intelligence_reports_time_sensitivity ON intelligence_reports(time_sensitivity);
CREATE INDEX idx_intelligence_reports_geographic_area ON intelligence_reports(geographic_area);
CREATE INDEX idx_intelligence_reports_threat_type ON intelligence_reports(threat_type);
CREATE INDEX idx_intelligence_reports_source_type ON intelligence_reports(source_type);
CREATE INDEX idx_intelligence_reports_expires_at ON intelligence_reports(expires_at);
CREATE INDEX idx_intelligence_reports_report_number ON intelligence_reports(report_number);
-- Geospatial index for location-based queries
CREATE INDEX idx_intelligence_reports_location ON intelligence_reports(latitude, longitude);
-- Composite indexes for common queries
CREATE INDEX idx_intelligence_reports_class_threat ON intelligence_reports(classification, threat_level);
CREATE INDEX idx_intelligence_reports_status_created ON intelligence_reports(review_status, created_at);
CREATE INDEX idx_intelligence_reports_author_created ON intelligence_reports(author_id, created_at);
-- Intelligence tags indexes
CREATE INDEX idx_intelligence_tags_report_id ON intelligence_tags(intelligence_report_id);
CREATE INDEX idx_intelligence_tags_name ON intelligence_tags(tag_name);
CREATE INDEX idx_intelligence_tags_category ON intelligence_tags(tag_category);
-- Threat correlations indexes
CREATE INDEX idx_threat_correlations_primary_report ON threat_correlations(intelligence_report_id);
CREATE INDEX idx_threat_correlations_correlated_report ON threat_correlations(correlated_report_id);
CREATE INDEX idx_threat_correlations_type ON threat_correlations(correlation_type);
CREATE INDEX idx_threat_correlations_strength ON threat_correlations(correlation_strength);
CREATE INDEX idx_threat_correlations_created_by ON threat_correlations(created_by_id);
CREATE INDEX idx_threat_correlations_validation_status ON threat_correlations(validation_status);
-- Comments for documentation
COMMENT ON TABLE intelligence_reports IS 'Intelligence reports with threat analysis and classification';
COMMENT ON COLUMN intelligence_reports.report_number IS 'Unique report identifier (auto-generated)';
COMMENT ON COLUMN intelligence_reports.classification IS 'Security classification level';
COMMENT ON COLUMN intelligence_reports.threat_level IS 'Assessed threat severity level';
COMMENT ON COLUMN intelligence_reports.confidence_level IS 'Confidence in threat assessment';
COMMENT ON COLUMN intelligence_reports.source_reliability IS 'Source reliability rating (A-F scale)';
COMMENT ON COLUMN intelligence_reports.geographic_area IS 'Geographic region or area of interest';
COMMENT ON COLUMN intelligence_reports.threat_type IS 'Type of threat identified';
COMMENT ON COLUMN intelligence_reports.target_type IS 'Type of target threatened';
COMMENT ON COLUMN intelligence_reports.time_sensitivity IS 'Urgency level for processing';
COMMENT ON COLUMN intelligence_reports.dissemination_controls IS 'Distribution and sharing controls';
COMMENT ON COLUMN intelligence_reports.source_type IS 'Intelligence discipline (HUMINT, SIGINT, etc.)';
COMMENT ON TABLE intelligence_tags IS 'Tags for categorizing and searching intelligence reports';
COMMENT ON TABLE threat_correlations IS 'Correlations between related intelligence reports and threats';
COMMENT ON COLUMN threat_correlations.correlation_type IS 'Type of relationship between reports';
COMMENT ON COLUMN threat_correlations.correlation_strength IS 'Strength of the correlation';
COMMENT ON COLUMN threat_correlations.confidence_score IS 'Numeric confidence score (0.0-1.0)';
COMMENT ON COLUMN threat_correlations.validation_status IS 'Validation status of the correlation';