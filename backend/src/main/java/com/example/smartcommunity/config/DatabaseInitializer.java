package com.example.smartcommunity.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                initializeDataIfNeeded();
                fixDatabaseData();
            } catch (Exception e) {
                System.err.println("Error initializing database: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void initializeDataIfNeeded() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            
            try (Statement charsetStmt = connection.createStatement()) {
                charsetStmt.execute("SET NAMES utf8mb4");
                charsetStmt.execute("SET CHARACTER SET utf8mb4");
            }
            
            if (!isDataInitialized(connection)) {
                executeSqlScript("init_mysql.sql", connection);
            } else {
                System.out.println("Database already initialized, skipping data insertion");
            }
            
            connection.commit();
        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void fixDatabaseData() {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            
            try (Statement charsetStmt = connection.createStatement()) {
                charsetStmt.execute("SET NAMES utf8mb4");
                charsetStmt.execute("SET CHARACTER SET utf8mb4");
            }
            
            fixDeviceTable(connection);
            fixEmergencyCallTable(connection);
            fixServiceOrderTable(connection);
            fixMessageTable(connection);
            fillDeviceControlTable(connection);
            fillWalletTable(connection);
            
            connection.commit();
            System.out.println("Database data fixed successfully");
        } catch (Exception e) {
            System.out.println("Error fixing database data: " + e.getMessage());
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
                pstmt.executeUpdate();
            }
            System.out.println("Fixed device table");
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
                pstmt.executeUpdate();
            }
            System.out.println("Fixed emergency_call table");
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
                pstmt.executeUpdate();
            }
            System.out.println("Fixed service_order table");
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
            {"那我们周六去看", "20"},
            {"社区通知：明天上午九点在活动中心开会", "31"},
            {"收到，准时参加", "32"},
            {"今天感觉身体怎么样", "33"},
            {"挺好的，谢谢关心", "34"},
            {"记得按时吃药", "35"},
            {"好的，我会记得的", "36"},
            {"明天社区有义诊活动", "37"},
            {"太好了，我要去看看", "38"},
            {"家里的药快用完了", "39"},
            {"我帮你去药店买", "40"},
            {"谢谢女儿", "41"},
            {"不用客气", "42"},
            {"今天做了什么好吃的", "43"},
            {"做了你爱吃的红烧肉", "44"},
            {"太棒了", "45"},
            {"晚上早点回家", "46"},
            {"好的", "47"},
            {"天气变凉了，多穿点衣服", "48"},
            {"知道了", "49"},
            {"周末一起去超市采购", "50"},
            {"好主意", "51"}
        };
        
        try (PreparedStatement pstmt = connection.prepareStatement(
            "UPDATE message SET content = ? WHERE id = ?")) {
            for (String[] update : updates) {
                pstmt.setString(1, update[0]);
                pstmt.setString(2, update[1]);
                pstmt.executeUpdate();
            }
            System.out.println("Fixed message table");
        }
    }
    
    private void fillDeviceControlTable(Connection connection) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM device_control");
            if (rs.next() && rs.getInt(1) == 0) {
                ResultSet deviceRs = stmt.executeQuery("SELECT id, device_type FROM device LIMIT 5");
                while (deviceRs.next()) {
                    long deviceId = deviceRs.getLong("id");
                    String deviceType = deviceRs.getString("device_type");
                    String controlValue = "light".equals(deviceType) ? "on" : "26";
                    
                    try (PreparedStatement pstmt = connection.prepareStatement(
                        "INSERT INTO device_control (device_id, control_type, control_value, status, created_at) VALUES (?, ?, ?, ?, NOW())")) {
                        pstmt.setLong(1, deviceId);
                        pstmt.setString(2, deviceType);
                        pstmt.setString(3, controlValue);
                        pstmt.setString(4, "executed");
                        pstmt.executeUpdate();
                    }
                }
                System.out.println("Filled device_control table");
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
                    "INSERT INTO wallet (user_id, balance, version, created_at, updated_at) VALUES (?, ?, 0, NOW(), NOW())")) {
                    for (String[] insert : inserts) {
                        pstmt.setString(1, insert[0]);
                        pstmt.setString(2, insert[1]);
                        pstmt.executeUpdate();
                    }
                    System.out.println("Filled wallet table");
                }
            }
        }
    }

    private boolean isDataInitialized(Connection connection) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM sys_user WHERE username = 'admin'");
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void executeSqlScript(String scriptName, Connection connection) {
        try (InputStream is = getClass().getResourceAsStream("/" + scriptName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            
            StringBuilder sql = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sql.append(line);
                if (line.endsWith(";")) {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(sql.toString());
                        System.out.println("Executed SQL: " + sql.substring(0, Math.min(sql.length(), 100)) + "...");
                    } catch (Exception e) {
                        System.out.println("SQL execution error: " + e.getMessage());
                    }
                    sql = new StringBuilder();
                }
            }
            System.out.println("Successfully executed SQL script: " + scriptName);
            
        } catch (Exception e) {
            System.out.println("Error executing SQL script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void insertInitialData(Connection connection) {
        try {
            insertServices(connection);
            insertActivities(connection);
            System.out.println("Successfully inserted initial data");
        } catch (Exception e) {
            System.out.println("Error inserting initial data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void insertServices(Connection connection) throws Exception {
        String[] serviceNames = {"家政服务", "医疗服务", "生活服务", "餐饮服务", "美容服务"};
        String[] serviceTypes = {"housekeeping", "medical", "life", "food", "beauty"};
        String[] descriptions = {
            "为社区居民提供上门家政服务，包括保洁、做饭、洗衣等",
            "为社区居民提供医疗服务，包括体检、问诊、康复等",
            "日常生活便利服务，包括代购、送水、送餐等",
            "为老年人提供营养餐饮服务",
            "为社区居民提供美容服务"
        };
        String[] providers = {"阳光家政服务中心", "社区医院", "社区服务中心", "社区食堂", "社区美容院"};
        java.math.BigDecimal[] prices = {new java.math.BigDecimal("80.00"), new java.math.BigDecimal("150.00"), new java.math.BigDecimal("20.00"), new java.math.BigDecimal("30.00"), new java.math.BigDecimal("50.00")};
        String[] phones = {"13812345678", "13887654321", "13811112222", "13833334444", "13855556666"};
        
        try (PreparedStatement pstmt = connection.prepareStatement(
            "INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, 'active', ?, NOW(), NOW())")) {
            
            for (int i = 0; i < serviceNames.length; i++) {
                pstmt.setString(1, serviceNames[i]);
                pstmt.setString(2, serviceTypes[i]);
                pstmt.setString(3, descriptions[i]);
                pstmt.setBigDecimal(4, prices[i]);
                pstmt.setString(5, providers[i]);
                pstmt.setString(6, phones[i]);
                pstmt.setInt(7, i + 1);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
    
    private void insertActivities(Connection connection) throws Exception {
        String[] titles = {"书法艺术交流", "广场舞健身活动", "智能手机使用培训", "健康养生讲座", "社区生日聚会"};
        String[] descriptions = {
            "邀请社区书法爱好者一起交流书法技艺，互学互鉴，共同提高书法水平",
            "每天早晨的广场舞健身活动，强健体魄，陶冶情操",
            "帮助老年人学习智能手机的使用，包括微信使用、视频通话、健康监测",
            "邀请专业医生讲解老年人健康养生知识，解答健康疑问",
            "为当月过生日的居民举办生日聚会，共度欢乐时光"
        };
        String[] types = {"culture", "sports", "study", "health", "social"};
        String[] locations = {"社区活动中心三楼", "社区广场", "社区活动中心二楼", "社区活动中心三楼大厅", "社区活动中心"};
        String[] startTimes = {"2026-06-20 09:00:00", "2026-06-18 07:00:00", "2026-06-22 14:00:00", "2026-06-28 14:30:00", "2026-06-25 10:00:00"};
        String[] endTimes = {"2026-06-20 11:30:00", "2026-06-18 09:00:00", "2026-06-22 16:00:00", "2026-06-28 16:30:00", "2026-06-25 12:00:00"};
        int[] maxParticipants = {20, 50, 30, 80, 40};
        int[] currentParticipants = {15, 42, 28, 66, 31};
        String[] organizers = {"书画社", "健身管理办公室", "社区志愿者", "社区服务中心", "社区管理办公室"};
        
        try (PreparedStatement pstmt = connection.prepareStatement(
            "INSERT INTO activity (title, description, type, location, start_time, end_time, max_participants, current_participants, organizer, status, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'active', NOW())")) {
            
            for (int i = 0; i < titles.length; i++) {
                pstmt.setString(1, titles[i]);
                pstmt.setString(2, descriptions[i]);
                pstmt.setString(3, types[i]);
                pstmt.setString(4, locations[i]);
                pstmt.setString(5, startTimes[i]);
                pstmt.setString(6, endTimes[i]);
                pstmt.setInt(7, maxParticipants[i]);
                pstmt.setInt(8, currentParticipants[i]);
                pstmt.setString(9, organizers[i]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
}