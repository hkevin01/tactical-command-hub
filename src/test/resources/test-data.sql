-- Test Data for Military Units
INSERT INTO military_units (
    id,
    call_sign,
    name,
    type,
    domain,
    status,
    latitude,
    longitude,
    altitude,
    heading,
    personnel_count,
    created_at,
    updated_at,
    version
  )
VALUES (
    1,
    'ALPHA-1',
    '1st Infantry Battalion Alpha',
    'INFANTRY',
    'LAND',
    'OPERATIONAL',
    34.0522,
    -118.2437,
    100.0,
    45.0,
    150,
    NOW(),
    NOW(),
    0
  ),
  (
    2,
    'BRAVO-2',
    '2nd Armored Division Bravo',
    'ARMOR',
    'LAND',
    'OPERATIONAL',
    34.0622,
    -118.2337,
    120.0,
    90.0,
    200,
    NOW(),
    NOW(),
    0
  ),
  (
    3,
    'CHARLIE-3',
    '3rd Aviation Regiment Charlie',
    'AVIATION',
    'AIR',
    'READY',
    34.0722,
    -118.2137,
    500.0,
    180.0,
    75,
    NOW(),
    NOW(),
    0
  ),
  (
    4,
    'DELTA-4',
    '4th Special Operations Delta',
    'SPECIAL_FORCES',
    'LAND',
    'STANDBY',
    34.0822,
    -118.1937,
    150.0,
    270.0,
    50,
    NOW(),
    NOW(),
    0
  ),
  (
    5,
    'ECHO-5',
    '5th Naval Command Echo',
    'NAVAL',
    'SEA',
    'OPERATIONAL',
    33.9522,
    -118.3437,
    0.0,
    0.0,
    300,
    NOW(),
    NOW(),
    0
  );
-- Test Data for Test Users
INSERT INTO users (
    id,
    username,
    email,
    password,
    first_name,
    last_name,
    rank,
    clearance_level,
    unit_id,
    enabled,
    created_at,
    updated_at,
    version
  )
VALUES (
    1,
    'test.commander',
    'commander@test.mil',
    '$2a$10$dXJ3SW6G7P9LVvVEgkUaQOyf2M8k6TZZ8sP8KgZK1Y2bvUTbCbvIK',
    'John',
    'Commander',
    'COLONEL',
    'SECRET',
    1,
    true,
    NOW(),
    NOW(),
    0
  ),
  (
    2,
    'test.operator',
    'operator@test.mil',
    '$2a$10$dXJ3SW6G7P9LVvVEgkUaQOyf2M8k6TZZ8sP8KgZK1Y2bvUTbCbvIK',
    'Jane',
    'Operator',
    'CAPTAIN',
    'CONFIDENTIAL',
    2,
    true,
    NOW(),
    NOW(),
    0
  ),
  (
    3,
    'test.analyst',
    'analyst@test.mil',
    '$2a$10$dXJ3SW6G7P9LVvVEgkUaQOyf2M8k6TZZ8sP8KgZK1Y2bvUTbCbvIK',
    'Bob',
    'Analyst',
    'LIEUTENANT',
    'UNCLASSIFIED',
    3,
    true,
    NOW(),
    NOW(),
    0
  );
-- Test Data for Roles
INSERT INTO roles (
    id,
    name,
    description,
    created_at,
    updated_at,
    version
  )
VALUES (
    1,
    'USER',
    'Standard user with basic access',
    NOW(),
    NOW(),
    0
  ),
  (
    2,
    'COMMANDER',
    'Command authority with full access',
    NOW(),
    NOW(),
    0
  ),
  (
    3,
    'ADMIN',
    'System administrator with all privileges',
    NOW(),
    NOW(),
    0
  );
-- Test Data for User-Role Associations
INSERT INTO user_roles (user_id, role_id)
VALUES (1, 2),
  -- Commander role
  (1, 1),
  -- User role
  (2, 1),
  -- Operator has User role
  (3, 1);
-- Analyst has User role
-- Test Data for Missions
INSERT INTO missions (
    id,
    mission_code,
    name,
    description,
    priority,
    status,
    start_latitude,
    start_longitude,
    end_latitude,
    end_longitude,
    start_time,
    end_time,
    estimated_duration,
    classification,
    created_at,
    updated_at,
    version
  )
VALUES (
    1,
    'TEST-MISSION-2025',
    'Test Mission Alpha',
    'Integration test mission for unit testing',
    'HIGH',
    'PLANNING',
    34.0522,
    -118.2437,
    34.0822,
    -118.1937,
    '2025-01-28 06:00:00',
    '2025-01-28 18:00:00',
    720,
    'UNCLASSIFIED',
    NOW(),
    NOW(),
    0
  ),
  (
    2,
    'TEST-PATROL-2025',
    'Test Patrol Bravo',
    'Security patrol test mission',
    'MEDIUM',
    'ACTIVE',
    34.0422,
    -118.2337,
    34.0422,
    -118.2337,
    '2025-01-27 08:00:00',
    '2025-01-27 16:00:00',
    480,
    'CONFIDENTIAL',
    NOW(),
    NOW(),
    0
  );
-- Test Data for Mission Unit Assignments
INSERT INTO mission_unit_assignments (mission_id, unit_id)
VALUES (1, 1),
  -- ALPHA-1 assigned to TEST-MISSION-2025
  (1, 2),
  -- BRAVO-2 assigned to TEST-MISSION-2025
  (2, 3),
  -- CHARLIE-3 assigned to TEST-PATROL-2025
  (2, 4);
-- DELTA-4 assigned to TEST-PATROL-2025
-- Test Data for Mission Waypoints
INSERT INTO mission_waypoints (
    id,
    mission_id,
    sequence_number,
    latitude,
    longitude,
    description,
    estimated_arrival_time,
    created_at,
    updated_at,
    version
  )
VALUES (
    1,
    1,
    1,
    34.0622,
    -118.2337,
    'Initial staging area',
    '2025-01-28 06:30:00',
    NOW(),
    NOW(),
    0
  ),
  (
    2,
    1,
    2,
    34.0722,
    -118.2037,
    'Primary objective area',
    '2025-01-28 10:00:00',
    NOW(),
    NOW(),
    0
  ),
  (
    3,
    2,
    1,
    34.0522,
    -118.2437,
    'Patrol checkpoint Alpha',
    '2025-01-27 09:00:00',
    NOW(),
    NOW(),
    0
  ),
  (
    4,
    2,
    2,
    34.0322,
    -118.2237,
    'Patrol checkpoint Bravo',
    '2025-01-27 12:00:00',
    NOW(),
    NOW(),
    0
  );
-- Test Data for Tactical Events
INSERT INTO tactical_events (
    id,
    event_type,
    title,
    description,
    severity,
    status,
    latitude,
    longitude,
    unit_id,
    mission_id,
    occurred_at,
    created_at,
    updated_at,
    version
  )
VALUES (
    1,
    'UNIT_MOVEMENT',
    'Unit Position Update',
    'ALPHA-1 moved to new coordinates',
    'INFO',
    'ACTIVE',
    34.0522,
    -118.2437,
    1,
    1,
    NOW(),
    NOW(),
    NOW(),
    0
  ),
  (
    2,
    'MISSION_STATUS',
    'Mission Started',
    'TEST-PATROL-2025 mission started',
    'INFO',
    'RESOLVED',
    34.0422,
    -118.2337,
    3,
    2,
    NOW(),
    NOW(),
    NOW(),
    0
  ),
  (
    3,
    'SECURITY_ALERT',
    'Test Alert',
    'Simulated security alert for testing',
    'WARNING',
    'ACKNOWLEDGED',
    34.0722,
    -118.2137,
    4,
    NULL,
    NOW(),
    NOW(),
    NOW(),
    0
  );
-- Test Data for Unit Status History
INSERT INTO unit_status_history (
    id,
    unit_id,
    status,
    changed_at,
    changed_by,
    notes,
    created_at,
    updated_at,
    version
  )
VALUES (
    1,
    1,
    'OPERATIONAL',
    NOW(),
    'test.commander',
    'Unit deployed for test mission',
    NOW(),
    NOW(),
    0
  ),
  (
    2,
    2,
    'OPERATIONAL',
    NOW(),
    'test.commander',
    'Armored unit ready for deployment',
    NOW(),
    NOW(),
    0
  ),
  (
    3,
    3,
    'READY',
    NOW(),
    'test.operator',
    'Aviation unit on standby',
    NOW(),
    NOW(),
    0
  ),
  (
    4,
    4,
    'STANDBY',
    NOW(),
    'test.operator',
    'Special forces unit in reserve',
    NOW(),
    NOW(),
    0
  ),
  (
    5,
    5,
    'OPERATIONAL',
    NOW(),
    'test.commander',
    'Naval unit active in AOR',
    NOW(),
    NOW(),
    0
  );