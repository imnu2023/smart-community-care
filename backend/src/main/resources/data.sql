INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('admin', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13800138000', 'admin@example.com', '管理员', 30, 'male', '管理办公室', '', 1, 0, '', '', NOW(), NOW());

INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('testuser', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13900139000', 'test@example.com', '张大爷', 65, 'male', '幸福社区1号楼101室', '', 2, 0, '李阿姨', '13888888888', NOW(), NOW());

INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('user2', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13700137000', 'user2@example.com', '王阿姨', 72, 'female', '幸福社区2号楼202室', '', 2, 0, '王先生', '13999999999', NOW(), NOW());

INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('user3', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13600136000', 'user3@example.com', '刘大爷', 68, 'male', '幸福社区3号楼301室', '', 2, 0, '刘阿姨', '13777777777', NOW(), NOW());

INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('家政服务', 'housekeeping', '专业保洁人员上门服务，包括打扫卫生、整理房间等', 80, '阳光家政公司', '13812345678', 'active', 1, NOW(), NOW());

INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('医疗护理', 'medical', '专业医护人员上门服务，包括测量血压、血糖、换药等', 150, '社区医院', '13887654321', 'active', 2, NOW(), NOW());

INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('生活配送', 'life', '日常用品配送服务，包括蔬菜、水果、药品等', 20, '社区配送中心', '13811112222', 'active', 3, NOW(), NOW());

INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('文化活动', 'culture', '组织各类文化活动，包括广场舞、书法、棋牌等', 50, '社区活动中心', '13833334444', 'active', 4, NOW(), NOW());

INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('康复理疗', 'medical', '专业康复理疗服务，帮助老人恢复身体机能', 120, '康复中心', '13855556666', 'active', 5, NOW(), NOW());

INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('心理咨询', 'medical', '专业心理咨询服务，关注老人心理健康', 100, '心理健康中心', '13877778888', 'active', 6, NOW(), NOW());

INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (2, '客厅灯光', 'light', 'dev-001', 'online', 100, '客厅', NOW(), NOW(), NOW());

INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (2, '卧室空调', 'aircon', 'dev-002', 'online', 26, '卧室', NOW(), NOW(), NOW());

INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (2, '窗帘控制器', 'curtain', 'dev-003', 'online', 50, '客厅', NOW(), NOW(), NOW());

INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (3, '主卧灯光', 'light', 'dev-004', 'online', 70, '主卧', NOW(), NOW(), NOW());

INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (3, '厨房空调', 'aircon', 'dev-005', 'offline', 24, '厨房', NOW(), NOW(), NOW());

INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (2, 72, 125, 80, 5.8, 36.5, '2026-06-08 08:00:00', NOW());

INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (2, 75, 130, 85, 6.2, 36.3, '2026-06-09 08:30:00', NOW());

INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (3, 68, 118, 78, 5.5, 36.6, '2026-06-08 09:00:00', NOW());

INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (3, 70, 122, 82, 5.9, 36.4, '2026-06-09 09:30:00', NOW());

INSERT INTO emergency_call (user_id, call_type, status, description, call_time, created_at) VALUES (2, 'medical', 'resolved', '突发心脏病，需要急救', '2026-06-05 14:25:00', '2026-06-05 14:25:00');

INSERT INTO emergency_call (user_id, call_type, status, description, call_time, created_at) VALUES (3, 'security', 'responding', '家中漏水，需要帮助', '2026-06-10 10:00:00', '2026-06-10 10:00:00');

INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260606001', 2, 1, 'completed', '2026-06-07 09:00:00', '幸福社区1号楼101室', 80, '2026-06-06 14:00:00', '2026-06-06 14:00:00');

INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260610002', 2, 2, 'pending', '2026-06-11 10:00:00', '幸福社区1号楼101室', 150, '2026-06-10 09:00:00', '2026-06-10 09:00:00');

INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260608003', 3, 3, 'completed', '2026-06-08 15:00:00', '幸福社区2号楼202室', 40, '2026-06-08 10:00:00', '2026-06-08 10:00:00');

INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260610004', 4, 4, 'pending', '2026-06-12 14:00:00', '幸福社区3号楼301室', 50, '2026-06-10 11:00:00', '2026-06-10 11:00:00');