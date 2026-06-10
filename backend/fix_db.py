import mysql.connector
from mysql.connector import Error

def check_and_fix_database():
    try:
        connection = mysql.connector.connect(
            host='localhost',
            database='smart_community',
            user='root',
            password='123456'
        )
        
        if connection.is_connected():
            cursor = connection.cursor()
            
            cursor.execute("DESCRIBE device;")
            device_columns = [col[0] for col in cursor.fetchall()]
            print("当前 device 表列:", device_columns)
            
            if 'device_value' not in device_columns:
                print("添加缺失的 device_value 列...")
                cursor.execute("ALTER TABLE device ADD COLUMN device_value DOUBLE;")
                connection.commit()
            
            cursor.execute("DESCRIBE emergency_call;")
            emergency_columns = [col[0] for col in cursor.fetchall()]
            print("当前 emergency_call 表列:", emergency_columns)
            
            cursor.execute("SELECT COUNT(*) FROM emergency_call;")
            count = cursor.fetchone()[0]
            print(f"emergency_call 表当前记录数: {count}")
            
            if count == 0:
                print("插入测试数据...")
                emergency_sql = """
                INSERT INTO emergency_call (user_id, call_type, status, description, call_time, created_at) 
                VALUES (%s, %s, %s, %s, %s, %s)
                """
                emergencies = [
                    (2, 'medical', 'completed', 'Heart attack, need emergency', '2026-06-05 14:25:00', '2026-06-05 14:25:00'),
                    (3, 'security', 'processing', 'Water leak at home, need help', '2026-06-10 10:00:00', '2026-06-10 10:00:00')
                ]
                cursor.executemany(emergency_sql, emergencies)
                connection.commit()
                print(f"插入 {cursor.rowcount} 条紧急呼叫记录")
            
            cursor.execute("SELECT COUNT(*) FROM device;")
            device_count = cursor.fetchone()[0]
            print(f"device 表当前记录数: {device_count}")
            
            if device_count == 0:
                print("插入设备数据...")
                device_sql = """
                INSERT INTO device (user_id, device_name, device_type, device_id, status, device_value, location, last_online_time, created_at, updated_at) 
                VALUES (%s, %s, %s, %s, %s, %s, %s, NOW(), NOW(), NOW())
                """
                devices = [
                    (2, 'Living Room Light', 'light', 'dev-001', 'online', 100, 'Living Room'),
                    (2, 'Bedroom AC', 'aircon', 'dev-002', 'online', 26, 'Bedroom'),
                    (2, 'Curtain Controller', 'curtain', 'dev-003', 'online', 50, 'Living Room'),
                    (3, 'Master Bedroom Light', 'light', 'dev-004', 'online', 70, 'Master Bedroom'),
                    (3, 'Kitchen AC', 'aircon', 'dev-005', 'offline', 24, 'Kitchen')
                ]
                cursor.executemany(device_sql, devices)
                connection.commit()
                print(f"插入 {cursor.rowcount} 条设备数据")
            
            cursor.execute("SELECT COUNT(*) FROM health_data;")
            health_count = cursor.fetchone()[0]
            print(f"health_data 表当前记录数: {health_count}")
            
            if health_count == 0:
                print("插入健康数据...")
                health_sql = """
                INSERT INTO health_data (user_id, heart_rate, blood_pressure_high, blood_pressure_low, blood_sugar, body_temperature, measured_at, created_at) 
                VALUES (%s, %s, %s, %s, %s, %s, %s, NOW())
                """
                health_data = [
                    (2, 72, 125, 80, 5.8, 36.5, '2026-06-08 08:00:00'),
                    (2, 75, 130, 85, 6.2, 36.3, '2026-06-09 08:30:00'),
                    (3, 68, 118, 78, 5.5, 36.6, '2026-06-08 09:00:00'),
                    (3, 70, 122, 82, 5.9, 36.4, '2026-06-09 09:30:00')
                ]
                cursor.executemany(health_sql, health_data)
                connection.commit()
                print(f"插入 {cursor.rowcount} 条健康数据")
            
            print("\n数据库修复完成！")
            
    except Error as e:
        print(f"数据库错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("数据库连接已关闭")

if __name__ == "__main__":
    check_and_fix_database()