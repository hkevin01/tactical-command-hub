-- Sample data for testing and demonstration
-- This migration adds sample military units, missions, and related data

-- Insert sample military units
INSERT INTO military_units (callsign, unit_name, unit_type, domain, status, readiness_level, latitude, longitude, altitude, heading, speed, personnel_count, equipment, last_contact, communication_status, threat_level, notes) VALUES
('ALPHA-1', '1st Infantry Division', 'INFANTRY', 'LAND', 'ACTIVE', 'C1', 39.048667, -76.886944, 45.0, 180.0, 0.0, 250, 'M4A1 Rifles, M240B Machine Guns, Night Vision', CURRENT_TIMESTAMP - INTERVAL '10 minutes', 'OPERATIONAL', 'LOW', 'Ready for deployment'),
('BRAVO-2', '2nd Armored Brigade', 'ARMOR', 'LAND', 'ACTIVE', 'C1', 39.055000, -76.890000, 42.0, 90.0, 25.5, 180, 'M1A2 Abrams Tanks, M2 Bradley IFVs', CURRENT_TIMESTAMP - INTERVAL '5 minutes', 'OPERATIONAL', 'MODERATE', 'Conducting training exercises'),
('CHARLIE-6', '6th Artillery Battalion', 'ARTILLERY', 'LAND', 'STANDBY', 'C2', 39.040000, -76.880000, 38.0, 270.0, 0.0, 120, 'M777 Howitzers, HIMARS', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'OPERATIONAL', 'LOW', 'Awaiting fire mission orders'),
('DELTA-4', '4th Air Defense Battery', 'AIR_DEFENSE', 'LAND', 'ACTIVE', 'C1', 39.060000, -76.875000, 50.0, 45.0, 0.0, 85, 'Patriot Missile Systems, Stinger Missiles', CURRENT_TIMESTAMP - INTERVAL '2 minutes', 'OPERATIONAL', 'HIGH', 'Monitoring airspace'),
('EAGLE-1', '1st Aviation Squadron', 'AVIATION', 'AIR', 'DEPLOYED', 'C1', 39.070000, -76.885000, 1500.0, 315.0, 180.0, 12, 'AH-64 Apache Helicopters', CURRENT_TIMESTAMP - INTERVAL '1 minute', 'OPERATIONAL', 'MODERATE', 'On reconnaissance patrol'),
('FOXTROT-3', '3rd Naval Group', 'NAVAL', 'SEA', 'ACTIVE', 'C1', 38.950000, -76.480000, -5.0, 0.0, 15.0, 300, 'Destroyer, Patrol Boats', CURRENT_TIMESTAMP - INTERVAL '15 minutes', 'OPERATIONAL', 'LOW', 'Coastal patrol mission'),
('GHOST-7', '7th Cyber Operations', 'CYBER', 'CYBER', 'ACTIVE', 'C1', 39.045000, -76.888000, 25.0, 0.0, 0.0, 45, 'Advanced Computing Systems, Network Equipment', CURRENT_TIMESTAMP - INTERVAL '3 minutes', 'OPERATIONAL', 'CRITICAL', 'Monitoring network threats'),
('HOTEL-9', '9th Logistics Support', 'LOGISTICS', 'LAND', 'ACTIVE', 'C2', 39.035000, -76.895000, 40.0, 180.0, 35.0, 200, 'Supply Trucks, Fuel Tankers, Medical Equipment', CURRENT_TIMESTAMP - INTERVAL '20 minutes', 'OPERATIONAL', 'LOW', 'Resupply convoy en route');

-- Insert sample unit status history
INSERT INTO unit_status_history (unit_id, previous_status, new_status, status_changed_at, change_reason, changed_by) VALUES
(1, 'STANDBY', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '2 hours', 'Deployment order received', 'CMDR.SMITH'),
(2, 'MAINTENANCE', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '4 hours', 'Maintenance completed', 'SGT.JOHNSON'),
(3, 'ACTIVE', 'STANDBY', CURRENT_TIMESTAMP - INTERVAL '1 hour', 'Training exercise completed', 'LT.BROWN'),
(5, 'STANDBY', 'DEPLOYED', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'Reconnaissance mission assigned', 'MAJ.WILSON');

-- Insert sample missions
INSERT INTO missions (mission_code, mission_name, mission_type, status, priority, objective, description, start_time, end_time, estimated_duration_hours, target_latitude, target_longitude, target_location, commander, assigned_unit_id, completion_percentage, notes) VALUES
('OP-FALCON-001', 'Operation Falcon Strike', 'RECONNAISSANCE', 'ACTIVE', 'HIGH', 'Conduct area reconnaissance and threat assessment', 'Multi-unit reconnaissance operation to assess enemy positions in sector 7-G. Primary objective is intelligence gathering with secondary objective of establishing forward observation posts.', CURRENT_TIMESTAMP - INTERVAL '2 hours', NULL, 8, 39.080000, -76.900000, 'Grid Square 7-G, Northern Sector', 'MAJ.WILSON', 5, 35, 'Weather conditions favorable'),
('OP-SHIELD-002', 'Operation Shield Wall', 'DEFENSE', 'PLANNING', 'CRITICAL', 'Establish defensive perimeter around critical infrastructure', 'Coordinate multi-unit defensive operation to protect critical supply depot and communication hub. Includes establishment of checkpoints and patrol routes.', CURRENT_TIMESTAMP + INTERVAL '6 hours', NULL, 24, 39.050000, -76.890000, 'Supply Depot Alpha', 'COL.ANDERSON', 1, 0, 'Awaiting final approval'),
('OP-THUNDER-003', 'Operation Thunder Run', 'ASSAULT', 'COMPLETED', 'MEDIUM', 'Clear enemy positions from designated area', 'Combined arms assault to neutralize enemy stronghold. Successfully completed with minimal casualties and all objectives achieved.', CURRENT_TIMESTAMP - INTERVAL '12 hours', CURRENT_TIMESTAMP - INTERVAL '4 hours', 8, 39.025000, -76.910000, 'Hill 442', 'LT.COL.MARTINEZ', 2, 100, 'Mission successful'),
('OP-GUARDIAN-004', 'Operation Guardian Watch', 'PATROL', 'ACTIVE', 'MEDIUM', 'Maintain security patrol along border sector', 'Regular security patrol to monitor and secure border area. Includes checkpoint inspections and civilian interaction protocols.', CURRENT_TIMESTAMP - INTERVAL '1 hour', NULL, 12, 39.100000, -76.850000, 'Border Sector Charlie', 'CAPT.DAVIS', 6, 15, 'No significant contacts reported');

-- Insert sample mission waypoints
INSERT INTO mission_waypoints (mission_id, sequence_number, waypoint_name, latitude, longitude, altitude, waypoint_type, estimated_arrival_time, actual_arrival_time, is_reached, description, action_required) VALUES
-- Waypoints for Operation Falcon Strike
(1, 1, 'LZ Alpha', 39.070000, -76.885000, 1500.0, 'START', CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '2 hours', true, 'Initial launch point', 'Conduct pre-flight checks'),
(1, 2, 'Checkpoint Bravo', 39.075000, -76.892000, 800.0, 'CHECKPOINT', CURRENT_TIMESTAMP - INTERVAL '90 minutes', CURRENT_TIMESTAMP - INTERVAL '90 minutes', true, 'First navigation checkpoint', 'Report position and status'),
(1, 3, 'Observation Point Charlie', 39.080000, -76.900000, 500.0, 'OBJECTIVE', CURRENT_TIMESTAMP - INTERVAL '60 minutes', NULL, false, 'Primary observation target', 'Conduct visual reconnaissance'),
(1, 4, 'Rally Point Delta', 39.082000, -76.905000, 300.0, 'RALLY_POINT', CURRENT_TIMESTAMP - INTERVAL '30 minutes', NULL, false, 'Secondary rally point if compromised', 'Regroup and assess'),
(1, 5, 'LZ Alpha', 39.070000, -76.885000, 1500.0, 'END', CURRENT_TIMESTAMP, NULL, false, 'Return to base', 'Mission debrief'),

-- Waypoints for Operation Guardian Watch
(4, 1, 'Base Departure', 38.950000, -76.480000, -5.0, 'START', CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '1 hour', true, 'Naval base departure point', 'Final equipment check'),
(4, 2, 'Patrol Point Alpha', 39.000000, -76.450000, -3.0, 'CHECKPOINT', CURRENT_TIMESTAMP - INTERVAL '40 minutes', CURRENT_TIMESTAMP - INTERVAL '40 minutes', true, 'First patrol checkpoint', 'Visual inspection of area'),
(4, 3, 'Border Marker Charlie', 39.100000, -76.850000, 0.0, 'OBJECTIVE', CURRENT_TIMESTAMP - INTERVAL '20 minutes', NULL, false, 'Primary patrol objective', 'Document any unusual activity'),
(4, 4, 'Patrol Point Bravo', 39.080000, -76.830000, -2.0, 'CHECKPOINT', CURRENT_TIMESTAMP, NULL, false, 'Second patrol checkpoint', 'Status report to command');

-- Insert sample mission reports
INSERT INTO mission_reports (mission_id, report_type, report_timestamp, reporter, reporter_rank, report_content, status_update, completion_percentage, casualties_friendly, casualties_enemy, equipment_status, resource_consumption, threat_level, weather_conditions, next_checkpoint_eta, requires_support, support_requested) VALUES
-- Reports for Operation Falcon Strike
(1, 'SITREP', CURRENT_TIMESTAMP - INTERVAL '90 minutes', 'CPT.RODRIGUEZ', 'Captain', 'Aircraft launched successfully. Weather conditions are favorable with clear visibility. All systems operational. Proceeding to first checkpoint on schedule.', 'ACTIVE', 10, 0, 0, 'All systems green', 'Fuel: 85%, Ammunition: 100%', 'LOW', 'Clear skies, visibility 10+ miles, winds 5-10 knots', CURRENT_TIMESTAMP - INTERVAL '60 minutes', false, NULL),
(1, 'PROGRESS', CURRENT_TIMESTAMP - INTERVAL '60 minutes', 'CPT.RODRIGUEZ', 'Captain', 'Checkpoint Bravo reached on time. Visual reconnaissance of target area initiated. No hostile contacts observed. Continuing mission as planned.', 'ACTIVE', 35, 0, 0, 'All systems operational', 'Fuel: 70%, Ammunition: 100%', 'LOW', 'Partly cloudy, visibility good', CURRENT_TIMESTAMP - INTERVAL '30 minutes', false, NULL),
(1, 'CONTACT', CURRENT_TIMESTAMP - INTERVAL '30 minutes', 'CPT.RODRIGUEZ', 'Captain', 'Unidentified vehicles spotted in target area. Conducting detailed observation. No immediate threat detected. Vehicles appear to be civilian contractors.', 'ACTIVE', 50, 0, 0, 'Surveillance equipment functioning', 'Fuel: 60%, Ammunition: 100%', 'MODERATE', 'Hazy conditions developing', CURRENT_TIMESTAMP, false, NULL),

-- Reports for Operation Thunder Run (completed mission)
(3, 'COMPLETION', CURRENT_TIMESTAMP - INTERVAL '4 hours', 'LT.COL.MARTINEZ', 'Lieutenant Colonel', 'Mission completed successfully. All objectives achieved with minimal resistance. Enemy positions neutralized. Area secured and cleared for follow-on operations.', 'COMPLETED', 100, 1, 8, 'Minor damage to one vehicle, all others operational', 'Ammunition: 65%, Fuel: 40%', 'LOW', 'Overcast, no impact on operations', NULL, false, NULL),

-- Reports for Operation Guardian Watch
(4, 'SITREP', CURRENT_TIMESTAMP - INTERVAL '40 minutes', 'LCDR.THOMPSON', 'Lieutenant Commander', 'Patrol commenced on schedule. Proceeding along designated route. Sea state calm, visibility excellent. All crew members alert and ready.', 'ACTIVE', 20, 0, 0, 'All systems operational', 'Fuel: 90%, Provisions: 100%', 'LOW', 'Clear weather, calm seas', CURRENT_TIMESTAMP - INTERVAL '20 minutes', false, NULL),
(4, 'PROGRESS', CURRENT_TIMESTAMP - INTERVAL '20 minutes', 'LCDR.THOMPSON', 'Lieutenant Commander', 'First checkpoint cleared without incident. Conducting visual sweep of coastal area. No suspicious activity detected. Morale remains high.', 'ACTIVE', 40, 0, 0, 'Radar and communications optimal', 'Fuel: 80%, Provisions: 95%', 'LOW', 'Light breeze, excellent visibility', CURRENT_TIMESTAMP, false, NULL);
