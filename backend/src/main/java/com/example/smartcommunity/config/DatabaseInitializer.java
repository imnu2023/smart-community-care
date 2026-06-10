package com.example.smartcommunity.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            fixForeignKeyConstraints(connection);
            fixDeviceTable(connection);
            insertTestData(connection);
        }
    }

    private void fixForeignKeyConstraints(Connection connection) throws SQLException {
        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE device DROP FOREIGN KEY device_ibfk_1");
            stmt.execute();
            System.out.println("Dropped device_ibfk_1 constraint");
        } catch (SQLException e) {
            System.out.println("Cannot drop foreign key device_ibfk_1: " + e.getMessage());
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE device ADD CONSTRAINT device_user_fk FOREIGN KEY (user_id) REFERENCES sys_user(id)");
            stmt.execute();
            System.out.println("Added device_user_fk constraint referencing sys_user");
        } catch (SQLException e) {
            System.out.println("Cannot add foreign key device_user_fk: " + e.getMessage());
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE health_data DROP FOREIGN KEY health_data_ibfk_1");
            stmt.execute();
            System.out.println("Dropped health_data_ibfk_1 constraint");
        } catch (SQLException e) {
            System.out.println("Cannot drop foreign key health_data_ibfk_1: " + e.getMessage());
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE health_data ADD CONSTRAINT health_data_user_fk FOREIGN KEY (user_id) REFERENCES sys_user(id)");
            stmt.execute();
            System.out.println("Added health_data_user_fk constraint referencing sys_user");
        } catch (SQLException e) {
            System.out.println("Cannot add foreign key health_data_user_fk: " + e.getMessage());
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE emergency_call DROP FOREIGN KEY emergency_call_ibfk_1");
            stmt.execute();
            System.out.println("Dropped emergency_call_ibfk_1 constraint");
        } catch (SQLException e) {
            System.out.println("Cannot drop foreign key emergency_call_ibfk_1: " + e.getMessage());
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE emergency_call ADD CONSTRAINT emergency_call_user_fk FOREIGN KEY (user_id) REFERENCES sys_user(id)");
            stmt.execute();
            System.out.println("Added emergency_call_user_fk constraint referencing sys_user");
        } catch (SQLException e) {
            System.out.println("Cannot add foreign key emergency_call_user_fk: " + e.getMessage());
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE service_order DROP FOREIGN KEY service_order_ibfk_1");
            stmt.execute();
            System.out.println("Dropped service_order_ibfk_1 constraint");
        } catch (SQLException e) {
            System.out.println("Cannot drop foreign key service_order_ibfk_1: " + e.getMessage());
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE service_order ADD CONSTRAINT service_order_user_fk FOREIGN KEY (user_id) REFERENCES sys_user(id)");
            stmt.execute();
            System.out.println("Added service_order_user_fk constraint referencing sys_user");
        } catch (SQLException e) {
            System.out.println("Cannot add foreign key service_order_user_fk: " + e.getMessage());
        }
    }

    private void fixDeviceTable(Connection connection) throws SQLException {
        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE device ADD COLUMN device_value DOUBLE");
            stmt.execute();
            System.out.println("Added device_value column to device table");
        } catch (SQLException e) {
            if (!e.getMessage().contains("Duplicate column name")) {
                throw e;
            }
            System.out.println("device_value column already exists");
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE device ADD COLUMN location VARCHAR(100)");
            stmt.execute();
            System.out.println("Added location column to device table");
        } catch (SQLException e) {
            if (!e.getMessage().contains("Duplicate column name")) {
                throw e;
            }
            System.out.println("location column already exists");
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE device ADD COLUMN last_online_time TIMESTAMP");
            stmt.execute();
            System.out.println("Added last_online_time column to device table");
        } catch (SQLException e) {
            if (!e.getMessage().contains("Duplicate column name")) {
                throw e;
            }
            System.out.println("last_online_time column already exists");
        }

        try {
            PreparedStatement stmt = connection.prepareStatement("ALTER TABLE device ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            stmt.execute();
            System.out.println("Added updated_at column to device table");
        } catch (SQLException e) {
            if (!e.getMessage().contains("Duplicate column name")) {
                throw e;
            }
            System.out.println("updated_at column already exists");
        }
    }

    private void insertTestData(Connection connection) throws SQLException {
        insertDevices(connection);
        insertHealthData(connection);
        insertEmergencyCalls(connection);
        insertCommunityServices(connection);
        insertServiceOrders(connection);
    }

    private void insertDevices(Connection connection) throws SQLException {
        PreparedStatement checkStmt = connection.prepareStatement("SELECT COUNT(*) FROM device");
        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        if (count == 0) {
            String sql = "INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), NOW())";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setLong(1, 2);
            stmt.setString(2, "Living Room Light");
            stmt.setString(3, "light");
            stmt.setString(4, "dev-001");
            stmt.setString(5, "online");
            stmt.setDouble(6, 100);
            stmt.setString(7, "Living Room");
            stmt.executeUpdate();

            stmt.setLong(1, 2);
            stmt.setString(2, "Bedroom AC");
            stmt.setString(3, "aircon");
            stmt.setString(4, "dev-002");
            stmt.setString(5, "online");
            stmt.setDouble(6, 26);
            stmt.setString(7, "Bedroom");
            stmt.executeUpdate();

            stmt.setLong(1, 2);
            stmt.setString(2, "Curtain Controller");
            stmt.setString(3, "curtain");
            stmt.setString(4, "dev-003");
            stmt.setString(5, "online");
            stmt.setDouble(6, 50);
            stmt.setString(7, "Living Room");
            stmt.executeUpdate();

            stmt.setLong(1, 3);
            stmt.setString(2, "Master Bedroom Light");
            stmt.setString(3, "light");
            stmt.setString(4, "dev-004");
            stmt.setString(5, "online");
            stmt.setDouble(6, 70);
            stmt.setString(7, "Master Bedroom");
            stmt.executeUpdate();

            stmt.setLong(1, 3);
            stmt.setString(2, "Kitchen AC");
            stmt.setString(3, "aircon");
            stmt.setString(4, "dev-005");
            stmt.setString(5, "offline");
            stmt.setDouble(6, 24);
            stmt.setString(7, "Kitchen");
            stmt.executeUpdate();

            System.out.println("Inserted 5 device records");
        } else {
            System.out.println("device table already has data, skipping insertion");
        }
    }

    private void insertHealthData(Connection connection) throws SQLException {
        PreparedStatement checkStmt = connection.prepareStatement("SELECT COUNT(*) FROM health_data");
        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        if (count == 0) {
            String sql = "INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setLong(1, 2);
            stmt.setInt(2, 72);
            stmt.setDouble(3, 125);
            stmt.setDouble(4, 80);
            stmt.setDouble(5, 5.8);
            stmt.setDouble(6, 36.5);
            stmt.setString(7, "2026-06-08 08:00:00");
            stmt.executeUpdate();

            stmt.setLong(1, 2);
            stmt.setInt(2, 75);
            stmt.setDouble(3, 130);
            stmt.setDouble(4, 85);
            stmt.setDouble(5, 6.2);
            stmt.setDouble(6, 36.3);
            stmt.setString(7, "2026-06-09 08:30:00");
            stmt.executeUpdate();

            stmt.setLong(1, 3);
            stmt.setInt(2, 68);
            stmt.setDouble(3, 118);
            stmt.setDouble(4, 78);
            stmt.setDouble(5, 5.5);
            stmt.setDouble(6, 36.6);
            stmt.setString(7, "2026-06-08 09:00:00");
            stmt.executeUpdate();

            stmt.setLong(1, 3);
            stmt.setInt(2, 70);
            stmt.setDouble(3, 122);
            stmt.setDouble(4, 82);
            stmt.setDouble(5, 5.9);
            stmt.setDouble(6, 36.4);
            stmt.setString(7, "2026-06-09 09:30:00");
            stmt.executeUpdate();

            System.out.println("Inserted 4 health_data records");
        } else {
            System.out.println("health_data table already has data, skipping insertion");
        }
    }

    private void insertEmergencyCalls(Connection connection) throws SQLException {
        PreparedStatement checkStmt = connection.prepareStatement("SELECT COUNT(*) FROM emergency_call");
        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        if (count == 0) {
            String sql = "INSERT INTO emergency_call (user_id, call_type, status, description, call_time, created_at) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setLong(1, 2);
            stmt.setString(2, "medical");
            stmt.setString(3, "completed");
            stmt.setString(4, "Heart attack, need emergency");
            stmt.setString(5, "2026-06-05 14:25:00");
            stmt.setString(6, "2026-06-05 14:25:00");
            stmt.executeUpdate();

            stmt.setLong(1, 3);
            stmt.setString(2, "security");
            stmt.setString(3, "processing");
            stmt.setString(4, "Water leak at home, need help");
            stmt.setString(5, "2026-06-10 10:00:00");
            stmt.setString(6, "2026-06-10 10:00:00");
            stmt.executeUpdate();

            System.out.println("Inserted 2 emergency_call records");
        } else {
            System.out.println("emergency_call table already has data, skipping insertion");
        }
    }

    private void insertCommunityServices(Connection connection) throws SQLException {
        PreparedStatement checkStmt = connection.prepareStatement("SELECT COUNT(*) FROM community_service");
        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        if (count == 0) {
            String sql = "INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, "Housekeeping");
            stmt.setString(2, "housekeeping");
            stmt.setString(3, "Professional cleaning service");
            stmt.setDouble(4, 80);
            stmt.setString(5, "Sunshine Cleaning");
            stmt.setString(6, "13812345678");
            stmt.setString(7, "active");
            stmt.setInt(8, 1);
            stmt.executeUpdate();

            stmt.setString(1, "Medical Care");
            stmt.setString(2, "medical");
            stmt.setString(3, "Professional medical service");
            stmt.setDouble(4, 150);
            stmt.setString(5, "Community Hospital");
            stmt.setString(6, "13887654321");
            stmt.setString(7, "active");
            stmt.setInt(8, 2);
            stmt.executeUpdate();

            stmt.setString(1, "Delivery");
            stmt.setString(2, "life");
            stmt.setString(3, "Daily supplies delivery");
            stmt.setDouble(4, 20);
            stmt.setString(5, "Community Delivery");
            stmt.setString(6, "13811112222");
            stmt.setString(7, "active");
            stmt.setInt(8, 3);
            stmt.executeUpdate();

            stmt.setString(1, "Cultural Activities");
            stmt.setString(2, "culture");
            stmt.setString(3, "Cultural activities");
            stmt.setDouble(4, 50);
            stmt.setString(5, "Community Center");
            stmt.setString(6, "13833334444");
            stmt.setString(7, "active");
            stmt.setInt(8, 4);
            stmt.executeUpdate();

            System.out.println("Inserted 4 community_service records");
        } else {
            System.out.println("community_service table already has data, skipping insertion");
        }
    }

    private void insertServiceOrders(Connection connection) throws SQLException {
        PreparedStatement checkStmt = connection.prepareStatement("SELECT COUNT(*) FROM service_order");
        ResultSet rs = checkStmt.executeQuery();
        rs.next();
        int count = rs.getInt(1);
        if (count == 0) {
            String sql = "INSERT INTO service_order (order_no, user_id, service_id, status, service_time, address, amount, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, "ORD20260606001");
            stmt.setLong(2, 2);
            stmt.setLong(3, 1);
            stmt.setString(4, "completed");
            stmt.setString(5, "2026-06-07 09:00:00");
            stmt.setString(6, "Community 1 Building 101");
            stmt.setDouble(7, 80);
            stmt.setString(8, "2026-06-06 14:00:00");
            stmt.setString(9, "2026-06-06 14:00:00");
            stmt.executeUpdate();

            stmt.setString(1, "ORD20260610002");
            stmt.setLong(2, 2);
            stmt.setLong(3, 2);
            stmt.setString(4, "pending");
            stmt.setString(5, "2026-06-11 10:00:00");
            stmt.setString(6, "Community 1 Building 101");
            stmt.setDouble(7, 150);
            stmt.setString(8, "2026-06-10 09:00:00");
            stmt.setString(9, "2026-06-10 09:00:00");
            stmt.executeUpdate();

            System.out.println("Inserted 2 service_order records");
        } else {
            System.out.println("service_order table already has data, skipping insertion");
        }
    }
}