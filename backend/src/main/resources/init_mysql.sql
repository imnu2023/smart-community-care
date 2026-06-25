CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    name VARCHAR(50),
    age INT,
    gender VARCHAR(10),
    address VARCHAR(255),
    avatar VARCHAR(255),
    role INT DEFAULT 2,
    health_status INT DEFAULT 0,
    emergency_contact VARCHAR(50),
    emergency_phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS health_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    heart_rate INT,
    blood_pressure_high INT,
    blood_pressure_low INT,
    blood_sugar DOUBLE,
    body_temperature DOUBLE,
    sleep_hours INT,
    steps INT,
    date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50),
    device_code VARCHAR(50) UNIQUE,
    status VARCHAR(20) DEFAULT 'offline',
    location VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS device_control (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,
    control_type VARCHAR(50) NOT NULL,
    control_value VARCHAR(255),
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES device(id)
);

CREATE TABLE IF NOT EXISTS emergency_call (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    call_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pending',
    response_time TIMESTAMP,
    responder_id BIGINT,
    location VARCHAR(255),
    description TEXT,
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (responder_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS community_service (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    service_type VARCHAR(50),
    description TEXT,
    price DOUBLE,
    provider VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'active',
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS service_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    service_time TIMESTAMP NOT NULL,
    address VARCHAR(255),
    remark TEXT,
    amount DOUBLE,
    payment_status VARCHAR(20) DEFAULT 'unpaid',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (service_id) REFERENCES community_service(id)
);

INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('admin', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13800138000', 'admin@example.com', '管理员', 30, 'male', '管理办公室', '', 1, 0, '', '', NOW(), NOW());
INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('elder1', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13900139000', 'elder1@example.com', '张大爷', 65, 'male', '幸福社区1号楼101室', '', 3, 0, '张小红', '13888888888', NOW(), NOW());
INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('family1', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13888888888', 'family1@example.com', '张小红', 35, 'female', '北京市朝阳区', '', 4, 0, '', '', NOW(), NOW());
INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('elder2', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13700137000', 'elder2@example.com', '王阿姨', 72, 'female', '幸福社区2号楼202室', '', 3, 0, '王小强', '13999999999', NOW(), NOW());
INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('family2', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13999999999', 'family2@example.com', '王小强', 38, 'male', '北京市海淀区', '', 4, 0, '', '', NOW(), NOW());
INSERT INTO sys_user (username, password, phone, email, name, age, gender, address, avatar, role, health_status, emergency_contact, emergency_phone, created_at, updated_at) VALUES ('user3', '$2a$10$Ga00Sp5uYbZ7sNZw6llA8Ox6xKQ6tpfNE/KAlSYWYMzWHygA7Qhbm', '13600136000', 'user3@example.com', '刘大爷', 68, 'male', '幸福社区3号楼301室', '', 2, 0, '刘阿姨', '13777777777', NOW(), NOW());

INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('家政服务', 'housekeeping', '专业保洁人员上门服务，包括打扫卫生、整理房间等', 80, '阳光家政公司', '13812345678', 'active', 1, NOW(), NOW());
INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('送餐服务', 'food', '营养均衡的餐食配送服务，适合老年人用餐需求', 30, '社区食堂', '13887654321', 'active', 2, NOW(), NOW());
INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES ('理发服务', 'beauty', '专业理发师上门理发服务', 50, '社区理发店', '13811112222', 'active', 3, NOW(), NOW());

INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260610001', 2, 1, 'completed', '2026-06-08 09:00:00', '幸福社区1号楼101室', 80, '2026-06-07 10:00:00', '2026-06-08 10:00:00');
INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260610002', 2, 2, 'pending', '2026-06-11 12:00:00', '幸福社区1号楼101室', 30, '2026-06-10 09:00:00', '2026-06-10 09:00:00');
INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260610003', 4, 3, 'pending', '2026-06-12 09:00:00', '幸福社区2号楼202室', 50, '2026-06-10 10:00:00', '2026-06-10 10:00:00');
INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES ('ORD20260610004', 5, 2, 'pending', '2026-06-12 14:00:00', '北京市海淀区', 30, '2026-06-10 11:00:00', '2026-06-10 11:00:00');

INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, sleep_hours, steps, date, created_at) VALUES (2, 72, 125, 80, 5.8, 36.5, 7, 3500, '2026-06-11', NOW());
INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, sleep_hours, steps, date, created_at) VALUES (2, 75, 130, 85, 6.1, 36.3, 6, 4200, '2026-06-10', NOW());

CREATE TABLE IF NOT EXISTS wallet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS user_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    elder_id BIGINT NOT NULL,
    family_member_id BIGINT NOT NULL,
    relation_type VARCHAR(50),
    status INT DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (elder_id) REFERENCES sys_user(id),
    FOREIGN KEY (family_member_id) REFERENCES sys_user(id)
);

INSERT INTO wallet (user_id, balance, created_at, updated_at) VALUES (2, 200.00, NOW(), NOW());
INSERT INTO wallet (user_id, balance, created_at, updated_at) VALUES (3, 500.00, NOW(), NOW());
INSERT INTO wallet (user_id, balance, created_at, updated_at) VALUES (4, 100.00, NOW(), NOW());
INSERT INTO wallet (user_id, balance, created_at, updated_at) VALUES (5, 800.00, NOW(), NOW());

INSERT INTO user_relation (elder_id, family_member_id, relation_type, status, created_at) VALUES (2, 3, '女儿', 1, NOW());
INSERT INTO user_relation (elder_id, family_member_id, relation_type, status, created_at) VALUES (4, 5, '儿子', 1, NOW());
INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, sleep_hours, steps, date, created_at) VALUES (2, 70, 128, 82, 5.9, 36.6, 8, 2800, '2026-06-09', NOW());
INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, sleep_hours, steps, date, created_at) VALUES (2, 78, 135, 88, 6.3, 36.4, 5, 5100, '2026-06-08', NOW());
INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, sleep_hours, steps, date, created_at) VALUES (2, 73, 126, 81, 5.7, 36.5, 7, 3800, '2026-06-07', NOW());

INSERT INTO device (user_id, device_name, device_type, device_code, status, location, created_at, updated_at) VALUES (2, '智能血压计', 'blood_pressure', 'DEV001', 'online', '卧室', NOW(), NOW());
INSERT INTO device (user_id, device_name, device_type, device_code, status, location, created_at, updated_at) VALUES (2, '智能心率手环', 'heart_rate', 'DEV002', 'online', '客厅', NOW(), NOW());
INSERT INTO device (user_id, device_name, device_type, device_code, status, location, created_at, updated_at) VALUES (2, '智能温湿度传感器', 'environment', 'DEV003', 'online', '卧室', NOW(), NOW());

CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(20) DEFAULT 'text',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES sys_user(id),
    FOREIGN KEY (receiver_id) REFERENCES sys_user(id)
);

CREATE TABLE IF NOT EXISTS activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    location VARCHAR(200),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    max_participants INT DEFAULT 20,
    current_participants INT DEFAULT 0,
    organizer VARCHAR(100),
    status VARCHAR(20) DEFAULT 'active',
    cover_image VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS activity_participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'confirmed',
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activity_id) REFERENCES activity(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

INSERT INTO message (sender_id, receiver_id, content, type, is_read, created_at) VALUES (3, 2, '爸爸，今天天气不错，记得出去走走', 'text', TRUE, '2026-06-10 09:00:00');
INSERT INTO message (sender_id, receiver_id, content, type, is_read, created_at) VALUES (2, 3, '好的，我下午出去', 'text', TRUE, '2026-06-10 09:05:00');
INSERT INTO message (sender_id, receiver_id, content, type, is_read, created_at) VALUES (3, 2, '记得带伞，可能会下雨', 'text', FALSE, '2026-06-10 09:15:00');
INSERT INTO message (sender_id, receiver_id, content, type, is_read, created_at) VALUES (1, 2, '您好，下周周三下午两点在社区活动中心有健康讲座，请提前到大厅签到', 'text', FALSE, '2026-06-09 14:00:00');
INSERT INTO message (sender_id, receiver_id, content, type, is_read, created_at) VALUES (5, 4, '妈妈，最近身体怎么样？按时吃药了吗？', 'text', TRUE, '2026-06-10 11:00:00');

INSERT INTO activity (title, description, type, location, start_time, end_time, max_participants, current_participants, organizer, status, created_at) VALUES ('书法艺术交流', '邀请社区书法爱好者一起交流书法技巧，分享作品，共同提高书法水平。', 'culture', '社区活动中心三楼', '2026-06-25 09:00:00', '2026-06-25 11:30:00', 20, 15, '李老师', 'active', NOW());
INSERT INTO activity (title, description, type, location, start_time, end_time, max_participants, current_participants, organizer, status, created_at) VALUES ('广场舞健身活动', '每天早晨的广场舞健身活动，强身健体，愉悦心情。', 'sports', '社区广场', '2026-06-25 07:00:00', '2026-06-25 09:00:00', 50, 42, '健身管理办公室', 'active', NOW());
INSERT INTO activity (title, description, type, location, start_time, end_time, max_participants, current_participants, organizer, status, created_at) VALUES ('智能手机使用培训', '帮助老年人学习智能手机的基本操作，包括微信使用、视频通话、健康码等。', 'study', '社区活动中心二楼', '2026-06-25 14:00:00', '2026-06-25 16:00:00', 30, 28, '社区志愿者', 'active', NOW());
INSERT INTO activity (title, description, type, location, start_time, end_time, max_participants, current_participants, organizer, status, created_at) VALUES ('健康养生讲座', '邀请专业医生讲解老年人健康养生知识，解答健康疑问。', 'health', '社区活动中心一楼大厅', '2026-06-27 14:30:00', '2026-06-27 16:30:00', 80, 65, '社区卫生服务中心', 'active', NOW());
INSERT INTO activity (title, description, type, location, start_time, end_time, max_participants, current_participants, organizer, status, created_at) VALUES ('社区生日聚会', '为当月过生日的居民举办生日聚会，增进邻里感情。', 'social', '社区活动中心', '2026-06-26 10:00:00', '2026-06-26 12:00:00', 40, 30, '社区管理办公室', 'active', NOW());

INSERT INTO activity_participant (activity_id, user_id, status, registered_at) VALUES (1, 2, 'confirmed', NOW());
INSERT INTO activity_participant (activity_id, user_id, status, registered_at) VALUES (3, 2, 'confirmed', NOW());
INSERT INTO activity_participant (activity_id, user_id, status, registered_at) VALUES (5, 2, 'confirmed', NOW());
INSERT INTO activity_participant (activity_id, user_id, status, registered_at) VALUES (1, 4, 'confirmed', NOW());
INSERT INTO activity_participant (activity_id, user_id, status, registered_at) VALUES (4, 4, 'confirmed', NOW());
INSERT INTO activity_participant (activity_id, user_id, status, registered_at) VALUES (2, 2, 'confirmed', NOW());

CREATE TABLE IF NOT EXISTS user_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    elder_id BIGINT NOT NULL,
    family_member_id BIGINT NOT NULL,
    relation_type VARCHAR(50) DEFAULT 'child',
    status INT DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (elder_id) REFERENCES sys_user(id),
    FOREIGN KEY (family_member_id) REFERENCES sys_user(id)
);

INSERT INTO user_relation (elder_id, family_member_id, relation_type, status, created_at) VALUES (2, 3, 'child', 1, NOW());
INSERT INTO user_relation (elder_id, family_member_id, relation_type, status, created_at) VALUES (4, 5, 'child', 1, NOW());
