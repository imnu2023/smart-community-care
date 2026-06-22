INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (1, '客厅灯光', 'light', 'dev-001', 'online', 100, '客厅', NOW(), NOW(), NOW());

INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (1, '卧室空调', 'aircon', 'dev-002', 'online', 26, '卧室', NOW(), NOW(), NOW());

INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (1, '窗帘控制器', 'curtain', 'dev-003', 'online', 50, '客厅', NOW(), NOW(), NOW());

INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (2, '主卧灯光', 'light', 'dev-004', 'online', 70, '主卧', NOW(), NOW(), NOW());

INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (2, '厨房空调', 'aircon', 'dev-005', 'offline', 24, '厨房', NOW(), NOW(), NOW());

INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (1, 72, 125, 80, 5.8, 36.5, '2026-06-08 08:00:00', NOW());

INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (1, 75, 130, 85, 6.2, 36.3, '2026-06-09 08:30:00', NOW());

INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (2, 68, 118, 78, 5.5, 36.6, '2026-06-08 09:00:00', NOW());

INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (2, 70, 122, 82, 5.9, 36.4, '2026-06-09 09:30:00', NOW());

INSERT INTO emergency_call (user_id, call_type, status, description, call_time, created_at) VALUES (1, 'medical', 'resolved', '突发心脏病，需要急救', '2026-06-05 14:25:00', '2026-06-05 14:25:00');

INSERT INTO emergency_call (user_id, call_type, status, description, call_time, created_at) VALUES (2, 'security', 'responding', '家中漏水，需要帮助', '2026-06-10 10:00:00', '2026-06-10 10:00:00');

INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260606001', 1, 1, 'completed', '2026-06-07 09:00:00', '幸福社区1号楼101室', 80, '2026-06-06 14:00:00', '2026-06-06 14:00:00');

INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260610002', 1, 2, 'pending', '2026-06-11 10:00:00', '幸福社区1号楼101室', 150, '2026-06-10 09:00:00', '2026-06-10 09:00:00');

INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260608003', 2, 3, 'completed', '2026-06-08 15:00:00', '幸福社区2号楼202室', 40, '2026-06-08 10:00:00', '2026-06-08 10:00:00');

INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260610004', 3, 4, 'pending', '2026-06-12 14:00:00', '幸福社区3号楼301室', 50, '2026-06-10 11:00:00', '2026-06-10 11:00:00');