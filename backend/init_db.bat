@echo off
set MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 8.0\bin
set PATH=%MYSQL_HOME%;%PATH%

echo 正在连接数据库...
mysql -u root -p123456 smart_community <<EOF

ALTER TABLE device ADD COLUMN device_value DOUBLE;
ALTER TABLE device ADD COLUMN location VARCHAR(100);
ALTER TABLE device ADD COLUMN last_online_time TIMESTAMP;
ALTER TABLE device ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

INSERT IGNORE INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (2, 'Living Room Light', 'light', 'dev-001', 'online', 100, 'Living Room', NOW(), NOW(), NOW());
INSERT IGNORE INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (2, 'Bedroom AC', 'aircon', 'dev-002', 'online', 26, 'Bedroom', NOW(), NOW(), NOW());
INSERT IGNORE INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (2, 'Curtain Controller', 'curtain', 'dev-003', 'online', 50, 'Living Room', NOW(), NOW(), NOW());
INSERT IGNORE INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (3, 'Master Bedroom Light', 'light', 'dev-004', 'online', 70, 'Master Bedroom', NOW(), NOW(), NOW());
INSERT IGNORE INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (3, 'Kitchen AC', 'aircon', 'dev-005', 'offline', 24, 'Kitchen', NOW(), NOW(), NOW());

INSERT IGNORE INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (2, 72, 125, 80, 5.8, 36.5, '2026-06-08 08:00:00', NOW());
INSERT IGNORE INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (2, 75, 130, 85, 6.2, 36.3, '2026-06-09 08:30:00', NOW());
INSERT IGNORE INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (3, 68, 118, 78, 5.5, 36.6, '2026-06-08 09:00:00', NOW());
INSERT IGNORE INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (3, 70, 122, 82, 5.9, 36.4, '2026-06-09 09:30:00', NOW());

INSERT IGNORE INTO emergency_call (user_id, call_type, status, description, call_time, created_at) VALUES (2, 'medical', 'completed', 'Heart attack, need emergency', '2026-06-05 14:25:00', '2026-06-05 14:25:00');
INSERT IGNORE INTO emergency_call (user_id, call_type, status, description, call_time, created_at) VALUES (3, 'security', 'processing', 'Water leak at home, need help', '2026-06-10 10:00:00', '2026-06-10 10:00:00');

INSERT IGNORE INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('Housekeeping', 'housekeeping', 'Professional cleaning service', 80, 'Sunshine Cleaning', '13812345678', 'active', 1, NOW(), NOW());
INSERT IGNORE INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('Medical Care', 'medical', 'Professional medical service', 150, 'Community Hospital', '13887654321', 'active', 2, NOW(), NOW());
INSERT IGNORE INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('Delivery', 'life', 'Daily supplies delivery', 20, 'Community Delivery', '13811112222', 'active', 3, NOW(), NOW());
INSERT IGNORE INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('Cultural Activities', 'culture', 'Cultural activities', 50, 'Community Center', '13833334444', 'active', 4, NOW(), NOW());

INSERT IGNORE INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260606001', 2, 1, 'completed', '2026-06-07 09:00:00', 'Community 1 Building 101', 80, '2026-06-06 14:00:00', '2026-06-06 14:00:00');
INSERT IGNORE INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260610002', 2, 2, 'pending', '2026-06-11 10:00:00', 'Community 1 Building 101', 150, '2026-06-10 09:00:00', '2026-06-10 09:00:00');

SELECT 'Data insertion completed successfully' AS result;
EOF

echo 数据库初始化完成！
pause