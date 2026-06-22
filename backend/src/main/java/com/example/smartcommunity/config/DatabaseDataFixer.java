package com.example.smartcommunity.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class DatabaseDataFixer implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseDataFixer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        fixDatabaseData();
    }

    private void fixDatabaseData() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            
            try (Statement charsetStmt = connection.createStatement()) {
                charsetStmt.execute("SET NAMES utf8mb4");
                charsetStmt.execute("SET CHARACTER SET utf8mb4");
            }
            
            System.out.println("=== 修复 device 表 ===");
            fixDeviceTable(connection);
            
            System.out.println("=== 修复 emergency_call 表 ===");
            fixEmergencyCallTable(connection);
            
            System.out.println("=== 修复 service_order 表 ===");
            fixServiceOrderTable(connection);
            
            System.out.println("=== 修复 message 表 ===");
            fixMessageTable(connection);
            
            System.out.println("=== 补充 device_control 表 ===");
            fillDeviceControlTable(connection);
            
            System.out.println("=== 补充 wallet 表 ===");
            fillWalletTable(connection);
            
            connection.commit();
            System.out.println("\n=== 数据库数据修复完成！===");
            
        } catch (Exception e) {
            System.err.println("数据库修复失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fixDeviceTable(Connection connection) throws Exception {
        String[][] updates = {
            {"客厅灯光", "客厅", "dev-001"},
            {"卧室空调", "卧室", "dev-002"},
            {"窗帘控制器", "客厅", "dev-003"},
            {"主卧灯光", "主卧", "dev-004"},
            {"厨房空调", "厨房", "dev-005"},
            {"客厅灯光", "客厅", "test-dev-001"},
            {"客厅空调", "客厅", "unique-dev-001"}
        };
        
        try (PreparedStatement pstmt = connection.prepareStatement(
            "UPDATE device SET device_name = ?, location = ? WHERE device_id = ?")) {
            for (String[] update : updates) {
                pstmt.setString(1, update[0]);
                pstmt.setString(2, update[1]);
                pstmt.setString(3, update[2]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("  已修复设备数据");
        }
    }

    private void fixEmergencyCallTable(Connection connection) throws Exception {
        // 使用英文规范枚举（与 Java 代码 EmergencyServiceImpl 保持一致）
        String[][] updates = {
            {"医疗", "resolved",   "突发心脏病，需要紧急救治", "29"},
            {"安全", "responding", "家中漏水，需要帮助",       "30"},
            {"医疗", "resolved",   "紧急呼叫测试",             "31"},
            {"医疗", "resolved",   "紧急呼叫测试",             "32"},
            {"火灾", "cancelled",  "火灾报警测试",             "33"}
        };

        try (PreparedStatement pstmt = connection.prepareStatement(
            "UPDATE emergency_call SET call_type = ?, status = ?, description = ? WHERE id = ?")) {
            for (String[] update : updates) {
                pstmt.setString(1, update[0]);
                pstmt.setString(2, update[1]);
                pstmt.setString(3, update[2]);
                pstmt.setString(4, update[3]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("  已修复紧急呼叫数据");
        }
    }

    private void fixServiceOrderTable(Connection connection) throws Exception {
        String[][] updates = {
            {"completed", "幸福社区1号楼101室", "ORD20260606001"},
            {"pending",   "幸福社区1号楼101室", "ORD20260610002"}
        };
        
        try (PreparedStatement pstmt = connection.prepareStatement(
            "UPDATE service_order SET status = ?, address = ? WHERE order_no = ?")) {
            for (String[] update : updates) {
                pstmt.setString(1, update[0]);
                pstmt.setString(2, update[1]);
                pstmt.setString(3, update[2]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("  已修复服务订单数据");
        }
    }

    private void fixMessageTable(Connection connection) throws Exception {
        String[][] updates = {
            {"爸爸，今天天气不错，记得出门散步", "1"},
            {"好的，我下午出去", "2"},
            {"记得带伞，可能会下雨", "3"},
            {"你好，下周周三下午两点有社区活动", "4"},
            {"爸妈，今天晚上我回家吃饭", "5"},
            {"好的，我们准备晚饭", "6"},
            {"周末有空一起去公园吗", "7"},
            {"可以啊，周六上午怎么样", "8"},
            {"好的，周六见", "9"},
            {"我们出门了", "11"},
            {"我到家了", "12"},
            {"明天有空来家里坐坐吗", "13"},
            {"好的，明天下午三点", "14"},
            {"记得带些水果", "15"},
            {"好的，知道了", "16"},
            {"周末去看电影吗", "17"},
            {"好的，想看什么电影", "18"},
            {"最近有部新上映的不错", "19"},
            {"那我们周六去看", "20"}
        };
        
        try (PreparedStatement pstmt = connection.prepareStatement(
            "UPDATE message SET content = ? WHERE id = ?")) {
            for (String[] update : updates) {
                pstmt.setString(1, update[0]);
                pstmt.setString(2, update[1]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("  已修复消息数据");
        }
    }

    private void fillDeviceControlTable(Connection connection) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM device_control");
            if (rs.next() && rs.getInt(1) == 0) {
                String[][] inserts = {
                    {"6", "light", "on", "executed"},
                    {"7", "aircon", "26", "executed"},
                    {"8", "curtain", "open", "executed"},
                    {"11", "light", "on", "executed"},
                    {"15", "aircon", "24", "executed"}
                };
                
                try (PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO device_control (device_id, control_type, control_value, status, created_at) VALUES (?, ?, ?, ?, NOW())")) {
                    for (String[] insert : inserts) {
                        pstmt.setString(1, insert[0]);
                        pstmt.setString(2, insert[1]);
                        pstmt.setString(3, insert[2]);
                        pstmt.setString(4, insert[3]);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                    System.out.println("  已插入设备控制数据");
                }
            } else {
                System.out.println("  device_control 表已有数据，跳过");
            }
        }
    }

    private void fillWalletTable(Connection connection) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM wallet");
            if (rs.next() && rs.getInt(1) == 0) {
                String[][] inserts = {
                    {"2", "500.00"},
                    {"3", "800.00"},
                    {"14", "1200.00"},
                    {"15", "300.00"}
                };
                
                try (PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO wallet (user_id, balance, created_at, updated_at) VALUES (?, ?, NOW(), NOW())")) {
                    for (String[] insert : inserts) {
                        pstmt.setString(1, insert[0]);
                        pstmt.setString(2, insert[1]);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                    System.out.println("  已插入钱包数据");
                }
            } else {
                System.out.println("  wallet 表已有数据，跳过");
            }
        }
    }
}