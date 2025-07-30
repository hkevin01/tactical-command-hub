-- V6: Create reporting and analytics tables
-- This migration creates the tables for operational reports and analytics metrics
-- Create operational_reports table
CREATE TABLE operational_reports (
  id BIGSERIAL PRIMARY KEY,
  report_number VARCHAR(50) NOT NULL UNIQUE,
  title VARCHAR(500) NOT NULL,
  report_type VARCHAR(50) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
  period_start TIMESTAMP NOT NULL,
  period_end TIMESTAMP NOT NULL,
  author_id BIGINT NOT NULL,
  executive_summary TEXT,
  key_findings TEXT,
  recommendations TEXT,
  metrics TEXT,
  appendices TEXT,
  classification VARCHAR(50) NOT NULL DEFAULT 'UNCLASSIFIED',
  reviewed_by BIGINT,
  reviewed_at TIMESTAMP,
  approved_by BIGINT,
  approved_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_operational_reports_author FOREIGN KEY (author_id) REFERENCES users(id),
  CONSTRAINT fk_operational_reports_reviewer FOREIGN KEY (reviewed_by) REFERENCES users(id),
  CONSTRAINT fk_operational_reports_approver FOREIGN KEY (approved_by) REFERENCES users(id)
);
-- Create report_categories table (for many-to-many relationship)
CREATE TABLE report_categories (
  operational_report_id BIGINT NOT NULL,
  categories VARCHAR(50) NOT NULL,
  CONSTRAINT fk_report_categories_report FOREIGN KEY (operational_report_id) REFERENCES operational_reports(id) ON DELETE CASCADE
);
-- Create analytics_metrics table
CREATE TABLE analytics_metrics (
  id BIGSERIAL PRIMARY KEY,
  metric_name VARCHAR(200) NOT NULL,
  metric_type VARCHAR(50) NOT NULL,
  category VARCHAR(50) NOT NULL,
  value DOUBLE PRECISION NOT NULL,
  unit VARCHAR(50),
  unit_id BIGINT,
  mission_id BIGINT,
  timestamp TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_analytics_metrics_unit FOREIGN KEY (unit_id) REFERENCES units(id),
  CONSTRAINT fk_analytics_metrics_mission FOREIGN KEY (mission_id) REFERENCES missions(id)
);
-- Create metric_dimensions table (for key-value pairs)
CREATE TABLE metric_dimensions (
  metric_id BIGINT NOT NULL,
  dimension_name VARCHAR(100) NOT NULL,
  dimension_value VARCHAR(500),
  CONSTRAINT fk_metric_dimensions_metric FOREIGN KEY (metric_id) REFERENCES analytics_metrics(id) ON DELETE CASCADE
);
-- Create metric_tags table (for tagging metrics)
CREATE TABLE metric_tags (
  metric_id BIGINT NOT NULL,
  tag_value VARCHAR(100) NOT NULL,
  CONSTRAINT fk_metric_tags_metric FOREIGN KEY (metric_id) REFERENCES analytics_metrics(id) ON DELETE CASCADE
);
-- Create indexes for operational_reports
CREATE INDEX idx_operational_report_period ON operational_reports(period_start, period_end);
CREATE INDEX idx_operational_report_type ON operational_reports(report_type);
CREATE INDEX idx_operational_report_status ON operational_reports(status);
CREATE INDEX idx_operational_report_author ON operational_reports(author_id);
CREATE INDEX idx_operational_report_created ON operational_reports(created_at);
CREATE INDEX idx_operational_report_classification ON operational_reports(classification);
-- Create indexes for analytics_metrics
CREATE INDEX idx_analytics_timestamp ON analytics_metrics(timestamp);
CREATE INDEX idx_analytics_type ON analytics_metrics(metric_type);
CREATE INDEX idx_analytics_category ON analytics_metrics(category);
CREATE INDEX idx_analytics_unit ON analytics_metrics(unit_id);
CREATE INDEX idx_analytics_mission ON analytics_metrics(mission_id);
CREATE INDEX idx_analytics_name ON analytics_metrics(metric_name);
CREATE INDEX idx_analytics_name_timestamp ON analytics_metrics(metric_name, timestamp);
CREATE INDEX idx_analytics_category_timestamp ON analytics_metrics(category, timestamp);
-- Create indexes for report_categories
CREATE INDEX idx_report_categories_report ON report_categories(operational_report_id);
CREATE INDEX idx_report_categories_category ON report_categories(categories);
-- Create indexes for metric_dimensions
CREATE INDEX idx_metric_dimensions_metric ON metric_dimensions(metric_id);
CREATE INDEX idx_metric_dimensions_name ON metric_dimensions(dimension_name);
-- Create indexes for metric_tags
CREATE INDEX idx_metric_tags_metric ON metric_tags(metric_id);
CREATE INDEX idx_metric_tags_value ON metric_tags(tag_value);
-- Add constraints for enum values
ALTER TABLE operational_reports
ADD CONSTRAINT chk_report_type CHECK (
    report_type IN (
      'DAILY_OPERATIONAL',
      'WEEKLY_SUMMARY',
      'MONTHLY_ANALYSIS',
      'QUARTERLY_REVIEW',
      'ANNUAL_ASSESSMENT',
      'INCIDENT_REPORT',
      'AFTER_ACTION_REVIEW',
      'INTELLIGENCE_SUMMARY',
      'MISSION_DEBRIEF',
      'SPECIAL_REPORT'
    )
  );
ALTER TABLE operational_reports
ADD CONSTRAINT chk_report_status CHECK (
    status IN (
      'DRAFT',
      'UNDER_REVIEW',
      'REVIEWED',
      'APPROVED',
      'PUBLISHED',
      'ARCHIVED'
    )
  );
ALTER TABLE operational_reports
ADD CONSTRAINT chk_report_classification CHECK (
    classification IN (
      'UNCLASSIFIED',
      'CONFIDENTIAL',
      'SECRET',
      'TOP_SECRET'
    )
  );
ALTER TABLE report_categories
ADD CONSTRAINT chk_report_category CHECK (
    categories IN (
      'OPERATIONS',
      'INTELLIGENCE',
      'LOGISTICS',
      'PERSONNEL',
      'COMMUNICATIONS',
      'SECURITY',
      'TRAINING',
      'EQUIPMENT',
      'WEATHER',
      'MEDICAL'
    )
  );
ALTER TABLE analytics_metrics
ADD CONSTRAINT chk_metric_type CHECK (
    metric_type IN ('COUNTER', 'GAUGE', 'HISTOGRAM', 'TIMER', 'RATE')
  );
ALTER TABLE analytics_metrics
ADD CONSTRAINT chk_metric_category CHECK (
    category IN (
      'OPERATIONAL',
      'PERFORMANCE',
      'RESOURCE',
      'COMMUNICATION',
      'INTELLIGENCE',
      'LOGISTICS',
      'PERSONNEL',
      'SECURITY',
      'TRAINING',
      'EQUIPMENT'
    )
  );
-- Add updated_at trigger for operational_reports
CREATE OR REPLACE FUNCTION update_operational_reports_updated_at() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';
CREATE TRIGGER trigger_operational_reports_updated_at BEFORE
UPDATE ON operational_reports FOR EACH ROW EXECUTE FUNCTION update_operational_reports_updated_at();
-- Insert sample data for testing
-- Sample operational reports
INSERT INTO operational_reports (
    report_number,
    title,
    report_type,
    period_start,
    period_end,
    author_id,
    executive_summary,
    key_findings,
    recommendations,
    classification
  )
VALUES (
    'OPREP-D-20241228-001',
    'Daily Operational Report - December 28, 2024',
    'DAILY_OPERATIONAL',
    '2024-12-28 00:00:00',
    '2024-12-28 23:59:59',
    1,
    'Daily operations proceeded according to plan with no major incidents.',
    'All units reported operational readiness at 95% or higher.',
    'Continue current operational tempo. Monitor equipment maintenance schedules.',
    'UNCLASSIFIED'
  ),
  (
    'INTSUM-20241228-001',
    'Weekly Intelligence Summary',
    'INTELLIGENCE_SUMMARY',
    '2024-12-21 00:00:00',
    '2024-12-28 23:59:59',
    2,
    'Weekly intelligence activities focused on threat assessment and monitoring.',
    'No immediate threats identified. Routine surveillance activities continue.',
    'Maintain current intelligence gathering protocols.',
    'CONFIDENTIAL'
  );
-- Sample analytics metrics
INSERT INTO analytics_metrics (
    metric_name,
    metric_type,
    category,
    value,
    unit,
    timestamp
  )
VALUES (
    'mission_success_rate',
    'GAUGE',
    'OPERATIONAL',
    95.5,
    'percent',
    CURRENT_TIMESTAMP
  ),
  (
    'message_delivery_rate',
    'GAUGE',
    'COMMUNICATION',
    98.2,
    'percent',
    CURRENT_TIMESTAMP
  ),
  (
    'resource_utilization',
    'GAUGE',
    'RESOURCE',
    87.3,
    'percent',
    CURRENT_TIMESTAMP
  ),
  (
    'response_time',
    'TIMER',
    'PERFORMANCE',
    2.5,
    'seconds',
    CURRENT_TIMESTAMP
  ),
  (
    'active_units',
    'COUNTER',
    'OPERATIONAL',
    12,
    'count',
    CURRENT_TIMESTAMP
  );
-- Add sample report categories
INSERT INTO report_categories (operational_report_id, categories)
VALUES (1, 'OPERATIONS'),
  (1, 'PERSONNEL'),
  (2, 'INTELLIGENCE'),
  (2, 'SECURITY');
-- Add sample metric tags
INSERT INTO metric_tags (metric_id, tag_value)
VALUES (1, 'critical'),
  (1, 'daily'),
  (2, 'communication'),
  (2, 'network'),
  (3, 'resource'),
  (3, 'capacity');
COMMENT ON TABLE operational_reports IS 'Stores operational reports for command analysis and record keeping';
COMMENT ON TABLE analytics_metrics IS 'Stores performance and operational metrics for analysis and monitoring';
COMMENT ON TABLE report_categories IS 'Many-to-many relationship table for report categories';
COMMENT ON TABLE metric_dimensions IS 'Key-value pairs for metric dimensions and metadata';
COMMENT ON TABLE metric_tags IS 'Tags for categorizing and searching metrics';