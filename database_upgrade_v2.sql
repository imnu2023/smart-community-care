-- ==========================================================
-- SmartSeniorCare 数据库 V2.0 完整升级脚本
-- 执行前必须备份数据库！
-- 包含: 精度修复 / 乐观锁 / 逻辑删除 / 唯一约束 / 索引 / CHECK约束 / 数据清洗
-- ==========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================================
-- 阶段〇：数据清洗（必须在加唯一约束前执行）
-- ==========================================================

-- 0.1 清理 wallet 重复数据（每用户保留 updated_at 最新、id 最大的那条）
DELETE w1 FROM wallet w1
INNER JOIN wallet w2
ON w1.user_id = w2.user_id
AND (
    w1.updated_at < w2.updated_at
    OR (w1.updated_at = w2.updated_at AND w1.id < w2.id)
);

-- 0.2 清理 activity_participant 重复报名（每对 activity+user 保留最早注册记录）
DELETE ap1 FROM activity_participant ap1
INNER JOIN activity_participant ap2
ON ap1.activity_id = ap2.activity_id
AND ap1.user_id = ap2.user_id
AND ap1.id > ap2.id;

-- 0.3 清理 user_relation 重复绑定（每对 elder+family 保留最早记录）
DELETE ur1 FROM user_relation ur1
INNER JOIN user_relation ur2
ON ur1.elder_id = ur2.elder_id
AND ur1.family_member_id = ur2.family_member_id
AND ur1.id > ur2.id;

-- 0.4 修正 activity.current_participants（根据实际报名去重后计数）
UPDATE activity a
SET a.current_participants = (
    SELECT COUNT(*) FROM activity_participant ap
    WHERE ap.activity_id = a.id
)
WHERE a.is_deleted = 0 OR a.is_deleted IS NULL;


-- ==========================================================
-- 阶段一：清理冗余表
-- ==========================================================
DROP TABLE IF EXISTS `user`;


-- ==========================================================
-- 阶段二：建立财务核心流水表
-- ==========================================================
DROP TABLE IF EXISTS `wallet_transaction_log`;
CREATE TABLE `wallet_transaction_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `transaction_type` tinyint NOT NULL COMMENT '变动类型: 1-充值, 2-消费, 3-退款',
  `amount` decimal(10,2) NOT NULL COMMENT '变动金额',
  `balance_after` decimal(10,2) NOT NULL COMMENT '变动后余额快照',
  `related_order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联服务订单号',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注说明',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE,
  INDEX `idx_order_no` (`related_order_no`) USING BTREE,
  INDEX `idx_user_time` (`user_id`, `created_at` DESC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '钱包资金流水表' ROW_FORMAT = Dynamic;


-- ==========================================================
-- 阶段三：表结构高阶改造
-- ==========================================================

-- 3.1 财务精度修复 (DOUBLE → DECIMAL)
ALTER TABLE `community_service`
  MODIFY COLUMN `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '服务价格';

ALTER TABLE `service_order`
  MODIFY COLUMN `amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单总金额';

-- 3.2 健康数据精度修复
ALTER TABLE `health_data`
  MODIFY COLUMN `blood_sugar` decimal(5, 2) NULL DEFAULT NULL COMMENT '血糖',
  MODIFY COLUMN `body_temperature` decimal(4, 2) NULL DEFAULT NULL COMMENT '体温',
  MODIFY COLUMN `calories` decimal(8, 2) NULL DEFAULT NULL COMMENT '消耗卡路里';

-- 3.3 并发安全 — 乐观锁 version 字段
-- 使用存储过程实现幂等添加（MySQL 8.0 不支持 ADD COLUMN IF NOT EXISTS）
DROP PROCEDURE IF EXISTS add_column_if_not_exists;
DELIMITER $$
CREATE PROCEDURE add_column_if_not_exists(
    IN tbl_name VARCHAR(128),
    IN col_name VARCHAR(128),
    IN col_def  TEXT
)
BEGIN
    DECLARE col_count INT DEFAULT 0;
    SELECT COUNT(*) INTO col_count
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tbl_name
      AND COLUMN_NAME = col_name;
    IF col_count = 0 THEN
        SET @stmt = CONCAT('ALTER TABLE `', tbl_name, '` ADD COLUMN ', col_def);
        PREPARE st FROM @stmt;
        EXECUTE st;
        DEALLOCATE PREPARE st;
    END IF;
END$$
DELIMITER ;

CALL add_column_if_not_exists('wallet',           'version',    '`version` int NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号''');
CALL add_column_if_not_exists('activity',         'version',    '`version` int NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号 (防止并发报名超卖)''');
CALL add_column_if_not_exists('service_order',    'version',    '`version` int NOT NULL DEFAULT 0 COMMENT ''乐观锁版本号 (防止并发修改订单状态)''');

CALL add_column_if_not_exists('service_order',    'is_deleted', '`is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除: 0-正常, 1-已删除''');
CALL add_column_if_not_exists('activity',         'is_deleted', '`is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除: 0-正常, 1-已删除''');
CALL add_column_if_not_exists('community_service','is_deleted', '`is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除: 0-正常, 1-已删除''');
CALL add_column_if_not_exists('sys_user',         'is_deleted', '`is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT ''逻辑删除: 0-正常, 1-已删除''');

DROP PROCEDURE IF EXISTS add_column_if_not_exists;

-- 3.5 清理 device 表冗余列（仅当列存在时删除）
DROP PROCEDURE IF EXISTS drop_column_if_exists;
DELIMITER $$
CREATE PROCEDURE drop_column_if_exists(
    IN tbl_name VARCHAR(128),
    IN col_name VARCHAR(128)
)
BEGIN
    DECLARE col_count INT DEFAULT 0;
    SELECT COUNT(*) INTO col_count
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tbl_name
      AND COLUMN_NAME = col_name;
    IF col_count > 0 THEN
        SET @stmt = CONCAT('ALTER TABLE `', tbl_name, '` DROP COLUMN `', col_name, '`');
        PREPARE st FROM @stmt;
        EXECUTE st;
        DEALLOCATE PREPARE st;
    END IF;
END$$
DELIMITER ;

CALL drop_column_if_exists('device', 'value');
DROP PROCEDURE IF EXISTS drop_column_if_exists;


-- ==========================================================
-- 阶段四：唯一约束（防止数据重复）
-- ==========================================================

-- 4.1 一人一钱包
ALTER TABLE `wallet`
  ADD UNIQUE INDEX `uk_wallet_user` (`user_id`);

-- 4.2 一活动一人只能报名一次
ALTER TABLE `activity_participant`
  ADD UNIQUE INDEX `uk_activity_user` (`activity_id`, `user_id`);

-- 4.3 一组亲属关系只能绑定一次
ALTER TABLE `user_relation`
  ADD UNIQUE INDEX `uk_elder_family` (`elder_id`, `family_member_id`);


-- ==========================================================
-- 阶段五：性能索引
-- ==========================================================

-- 5.1 健康数据时序查询（已有则跳过）
-- ALTER TABLE `health_data`
--   ADD INDEX IF NOT EXISTS `idx_user_measured` (`user_id`, `measured_at` DESC);

-- 5.2 紧急呼叫状态筛选
ALTER TABLE `emergency_call`
  ADD INDEX `idx_status_calltime` (`status`, `call_time` DESC);

-- 5.3 消息查询
ALTER TABLE `message`
  ADD INDEX `idx_receiver_unread` (`receiver_id`, `is_read`),
  ADD INDEX `idx_sender_receiver_time` (`sender_id`, `receiver_id`, `created_at` DESC);

-- 5.4 服务订单用户+状态查询
ALTER TABLE `service_order`
  ADD INDEX `idx_user_status` (`user_id`, `status`);

-- 5.5 活动时间筛选
ALTER TABLE `activity`
  ADD INDEX `idx_status_starttime` (`status`, `start_time`);


-- ==========================================================
-- 阶段六：CHECK 约束 (MySQL 8.0.26+)
-- ==========================================================

-- 6.1 钱包余额不能为负
ALTER TABLE `wallet`
  ADD CONSTRAINT `chk_balance_non_negative` CHECK (`balance` >= 0);

-- 6.2 活动人数合法性
ALTER TABLE `activity`
  ADD CONSTRAINT `chk_max_participants_positive` CHECK (`max_participants` > 0),
  ADD CONSTRAINT `chk_current_participants_non_negative` CHECK (`current_participants` >= 0);

-- 6.3 健康告警等级范围
ALTER TABLE `health_data`
  ADD CONSTRAINT `chk_warning_level_range` CHECK (`warning_level` BETWEEN 0 AND 5);

-- 6.4 订单金额非负
ALTER TABLE `service_order`
  ADD CONSTRAINT `chk_amount_non_negative` CHECK (`amount` >= 0);


-- ==========================================================
-- 阶段七：存量数据状态枚举统一
-- ==========================================================

-- 7.1 订单状态 (pending / confirmed / completed / cancelled)
UPDATE `service_order` SET `status` = 'completed'  WHERE `status` = '已完成';
UPDATE `service_order` SET `status` = 'pending'    WHERE `status` = '待服务';
UPDATE `service_order` SET `status` = 'confirmed'  WHERE `status` = '已确认';

-- 7.2 紧急呼叫状态 (pending / responding / resolved / cancelled)
-- 注意: Java 代码 EmergencyServiceImpl 使用 "responding" 和 "resolved"
UPDATE `emergency_call` SET `status` = 'resolved'   WHERE `status` = '已完成';
UPDATE `emergency_call` SET `status` = 'resolved'   WHERE `status` = '已解决';
UPDATE `emergency_call` SET `status` = 'resolved'   WHERE `status` = 'completed';
UPDATE `emergency_call` SET `status` = 'responding' WHERE `status` = '处理中';
UPDATE `emergency_call` SET `status` = 'cancelled'  WHERE `status` = '已取消';

-- 7.3 统一后台处理人命名
UPDATE `emergency_call` SET `handler` = 'admin' WHERE `handler` = '管理员';

-- 7.4 设备状态统一
UPDATE `device` SET `status` = 'offline' WHERE `status` = 'offline';
UPDATE `device` SET `status` = 'online'  WHERE `status` = 'active';


SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 升级完成！
-- 请核对以下事项：
-- 1. wallet 表每个 user_id 应只有一条记录
-- 2. activity_participant 每对 (activity_id, user_id) 应只有一条
-- 3. emergency_call.status 中没有中文值
-- 4. 所有金额字段均为 DECIMAL 类型
-- ==========================================================
