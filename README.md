# 智慧社区养老平台

基于 Spring Boot + Vue 的智慧社区养老管理系统

## 技术栈

### 后端
- Java 8
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.5
- MySQL 8.0+

### 前端
- Vue 3
- Element Plus
- Vue Router

## 项目结构

```
smart-community-care/
├── backend/          # 后端服务
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   └── pom.xml
├── frontend/         # 前端应用
│   ├── src/
│   ├── index.html
│   └── package.json
└── README.md
```

## 快速开始

### 环境要求
- JDK 8+
- Node.js 16+
- MySQL 8.0+

### 后端启动

1. 配置数据库连接（修改 `backend/src/main/resources/application.yml`）

2. 创建数据库并导入初始化脚本：
```sql
CREATE DATABASE smart_community_care;
USE smart_community_care;
```

3. 运行后端服务：
```bash
cd backend
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

## API 接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/register` | POST | 用户注册 |
| `/api/emergency/call` | POST | 创建紧急呼叫 |
| `/api/emergency/unresolved` | GET | 获取待响应呼叫 |
| `/api/emergency/{id}/respond` | PUT | 响应呼叫 |
| `/api/devices` | GET | 获取设备列表 |

## 功能模块

- 健康监测
- 数据录入
- 智能家居
- 紧急呼叫
- 社区服务
- 订单管理
- 个人中心
