# 智慧社区养老系统 — 后端 API 接口文档

> 基础地址: `http://localhost:8081`  
> 统一响应格式: `ApiResponse<T>`  
> Content-Type: `application/json`  
> 最后更新: 2026-06-22

---

## 通用响应格式

```json
{
  "code": 200,
  "message": "success",
  "data":  {},
  "timestamp": 1719030000000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 200-成功, 400-业务错误, 500-服务器错误 |
| message | string | 提示信息 |
| data | any | 响应数据（可为对象、数组、null） |
| timestamp | long | 时间戳 |

> **注意**: `/api/relations/**` 接口使用 `ResponseEntity<Map>` 格式而非 `ApiResponse`，详见对应章节。

---

## 角色枚举

| role | 含义 |
|------|------|
| 1 | 管理员 |
| 2 | 老人（旧版） |
| 3 | 老人 |
| 4 | 家属 |

---

## 1. 认证 /api/auth

### 1.1 密码哈希（调试用）
```
GET /api/auth/hash/{password}
```
**路径参数**: `password` — 明文密码  
**响应**: `String` — BCrypt 哈希值

---

### 1.2 登录
```
POST /api/auth/login
```
**请求体**:
```json
{
  "username": "admin",
  "password": "123456"
}
```
**响应** `LoginResponse`:
```json
{
  "token": "eyJ...",
  "userId": 1,
  "username": "admin",
  "name": "管理员",
  "role": 1,
  "phone": "13800138000"
}
```

---

### 1.3 注册
```
POST /api/auth/register
```
**请求体**:
```json
{
  "username": "newuser",
  "password": "123456",
  "name": "张三",
  "phone": "13800001111",
  "email": "zhangsan@example.com",
  "age": 65,
  "gender": "male",
  "address": "幸福社区1号楼",
  "role": 3,
  "emergencyContact": "张四",
  "emergencyPhone": "13800002222"
}
```
**响应**: `ApiResponse<Void>` — `"注册成功"`

---

## 2. 用户 /api/users

### 2.1 获取所有用户
```
GET /api/users
```
**响应**: `List<User>`

### 2.2 获取用户详情
```
GET /api/users/{id}
```
**响应**: `User`

### 2.3 更新用户
```
PUT /api/users/{id}
```
**请求体**: `User` 对象（只传需更新的字段）

### 2.4 删除用户（逻辑删除）
```
DELETE /api/users/{id}
```

---

## 3. 亲属关系 /api/relations

> ⚠️ 该模块使用 `ResponseEntity<Map>` 格式，非标准 `ApiResponse`！

```json
// 成功
{ "code": 200, "message": "绑定成功", "data": {...} }
// 失败
{ "code": 500, "message": "绑定失败: ..." }
```

### 3.1 绑定亲属关系
```
POST /api/relations/bind
```
**请求体**:
```json
{
  "elderId": 14,
  "familyMemberId": 15,
  "relationType": "child"
}
```

### 3.2 解绑
```
DELETE /api/relations/{id}
```

### 3.3 查询老人的所有家属
```
GET /api/relations/elder/{elderId}
```
**响应 data** 含 `elderName`, `elderPhone`, `familyMemberName`, `familyMemberPhone`

### 3.4 查询家属的所有老人
```
GET /api/relations/family/{familyMemberId}
```
**响应 data** 同上格式

### 3.5 判断关系是否存在
```
GET /api/relations/exists?elderId=14&familyMemberId=15
```
**响应**: `{ "data": true }` 或 `{ "data": false }`

### 3.6 清理重复关系
```
DELETE /api/relations/clean-duplicates
```

---

## 4. 钱包 /api/wallet

### 4.1 获取钱包
```
GET /api/wallet/{userId}
```
**响应** `Wallet`:
```json
{
  "id": 97,
  "userId": 14,
  "balance": 9960.00,
  "version": 3,
  "createdAt": "2026-06-15T10:10:05",
  "updatedAt": "2026-06-17T10:55:26"
}
```

### 4.2 查询余额
```
GET /api/wallet/balance/{userId}
```
**响应**:
```json
{ "userId": 14, "balance": 9960.00 }
```

### 4.3 充值
```
POST /api/wallet/recharge
```
**请求体**:
```json
{ "userId": 14, "amount": 100.00 }
```
**响应**:
```json
{ "success": true, "balance": 10060.00 }
```

### 4.4 扣款（无订单关联）
```
POST /api/wallet/pay
```
**请求体**:
```json
{ "userId": 14, "amount": 50.00 }
```
**响应**:
```json
// 成功
{ "success": true, "balance": 10010.00 }
// 余额不足
{ "success": false, "message": "余额不足" }
```
> 金额校验: 必须 > 0，否则抛异常；乐观锁冲突时抛异常并回滚事务。

---

## 5. 支付 /api/payments

### 5.1 本人支付
```
POST /api/payments/pay
```
**请求体**:
```json
{
  "orderId": 173,
  "payerId": 14
}
```
**响应**:
```json
// 成功
{ "success": true, "orderId": 173, "amount": 80.00, "balance": 9880.00 }
// 失败
{ "success": false, "message": "余额不足" }
```
> 自动写入 `wallet_transaction_log` 流水，关联订单号

### 5.2 家属代付
```
POST /api/payments/family-pay
```
**请求体**:
```json
{
  "orderId": 173,
  "familyId": 15
}
```
> 前提：familyId 与订单所属老人存在亲属关系

### 5.3 确认支付完成
```
POST /api/payments/confirm/{orderId}
```
> 将订单状态从 `pending` 改为 `completed`

### 5.4 查询待确认订单
```
GET /api/payments/orders/need-confirm
```
> 条件: `paymentStatus=paid` 且 `status=pending`

### 5.5 查询家属关联老人的订单
```
GET /api/payments/elder-orders/{familyId}
```
> 返回值仅包含第一个关联老人的订单（已知限制）

---

## 6. 社区服务 /api/services

### 6.1 获取所有服务
```
GET /api/services
```
**响应**: `List<CommunityService>`

### 6.2 获取服务详情
```
GET /api/services/{id}
```

### 6.3 按类型筛选
```
GET /api/services/type/{type}
```
> type: `housekeeping` / `medical` / `life` / `food` / `beauty` 等

### 6.4 新增服务
```
POST /api/services
```
**请求体**: `CommunityService` 对象
```json
{
  "serviceName": "理发服务",
  "serviceType": "beauty",
  "description": "专业理发师上门",
  "price": 50.00,
  "provider": "时尚理发店",
  "phone": "13822223333",
  "sortOrder": 4
}
```

### 6.5 更新服务
```
PUT /api/services/{id}
```

### 6.6 删除服务（逻辑删除）
```
DELETE /api/services/{id}
```

### 6.7 清理/修复 工具接口
```
DELETE /api/services/clean          → 清理重复
DELETE /api/services/clean-all      → 清理全部乱码
POST   /api/services/fix-garbled     → 修复乱码
POST   /api/services/fix-all-garbled → 修复全部乱码
```

---

## 7. 服务订单 /api/orders

### 订单状态枚举
| status | 含义 |
|--------|------|
| pending | 待处理 |
| confirmed | 已确认 |
| completed | 已完成 |
| cancelled | 已取消 |

### 支付状态
| paymentStatus | 含义 |
|--------------|------|
| unpaid | 未支付 |
| paid | 已支付 |

### 7.1 获取所有订单
```
GET /api/orders
```

### 7.2 创建订单
```
POST /api/orders
```
**请求体**:
```json
{
  "userId": 14,
  "serviceId": 1,
  "serviceTime": "2026-06-25 10:00:00",
  "address": "幸福社区1号楼101室",
  "remark": "需要下午服务"
}
```

### 7.3 获取订单详情
```
GET /api/orders/{id}
```

### 7.4 按用户查询
```
GET /api/orders/user/{userId}
```

### 7.5 按状态查询
```
GET /api/orders/status/{status}
```
> status: `pending` / `confirmed` / `completed` / `cancelled`

### 7.6 更新订单状态
```
PUT /api/orders/{orderId}/status
```
**请求体**:
```json
{ "status": "confirmed" }
```

### 7.7 取消订单
```
PUT /api/orders/{orderId}/cancel
```

### 7.8 修复地址乱码
```
POST /api/orders/fix-address
```

---

## 8. 活动 /api/activities

### 8.1 创建活动
```
POST /api/activities
```
**请求体**: `Activity` 对象
```json
{
  "title": "书法艺术交流",
  "description": "邀请社区书法爱好者一起交流",
  "type": "culture",
  "location": "社区活动中心三楼",
  "startTime": "2026-06-20T09:00:00",
  "endTime": "2026-06-20T11:30:00",
  "maxParticipants": 20,
  "organizer": "书画社"
}
```

### 8.2 获取所有活动
```
GET /api/activities
```

### 8.3 获取进行中活动
```
GET /api/activities/active
```

### 8.4 获取即将开始的活动
```
GET /api/activities/upcoming
```

### 8.5 按类型筛选
```
GET /api/activities/type/{type}
```
> type: `culture` / `sports` / `study` / `health` / `social`

### 8.6 获取用户报名的活动
```
GET /api/activities/participant/{userId}
```

### 8.7 获取活动详情
```
GET /api/activities/{id}
```

### 8.8 更新活动
```
PUT /api/activities/{id}
```

### 8.9 删除活动（逻辑删除）
```
DELETE /api/activities/{id}
```

### 8.10 报名活动
```
POST /api/activities/{activityId}/register?userId=14
```
> 乐观锁防超卖 + 唯一约束防重复报名

### 8.11 取消报名
```
DELETE /api/activities/{activityId}/register?userId=14
```

### 8.12 获取活动参与者列表
```
GET /api/activities/{activityId}/participants
```

### 8.13 判断是否已报名
```
GET /api/activities/{activityId}/participant/{userId}
```
**响应**: `Boolean`

---

## 9. 紧急呼叫 /api/emergency

### 状态枚举
| status | 含义 |
|--------|------|
| pending | 待处理 |
| responding | 响应中 |
| resolved | 已解决 |
| cancelled | 已取消 |

### 9.1 发起紧急呼叫
```
POST /api/emergency/call
```
**请求体**:
```json
{
  "userId": 14,
  "callType": "医疗",
  "latitude": 39.9042,
  "longitude": 116.4074,
  "description": "老人摔倒，需要帮助"
}
```

### 9.2 获取呼叫详情
```
GET /api/emergency/{id}
```

### 9.3 查询用户的呼叫记录
```
GET /api/emergency/user/{userId}
```

### 9.4 查询未解决的呼叫
```
GET /api/emergency/unresolved
```
> SQL: `WHERE status != 'resolved'`

### 9.5 响应呼叫
```
PUT /api/emergency/{callId}/respond?handler=admin
```
> 状态 → `responding`，记录 `handler` 和 `responseTime`

### 9.6 解决呼叫
```
PUT /api/emergency/{callId}/resolve
```
> 状态 → `resolved`，记录 `resolvedTime`

### 9.7 取消呼叫
```
PUT /api/emergency/{callId}/cancel
```
> 状态 → `cancelled`

---

## 10. 健康数据 /api/health

### 10.1 健康检查
```
GET /api/health
```
**响应**: `"Health service is running"`

### 10.2 保存健康数据
```
POST /api/health
```
**请求体**: `HealthData` 对象
```json
{
  "userId": 14,
  "heartRate": 75,
  "bloodPressureHigh": 130,
  "bloodPressureLow": 85,
  "bloodSugar": 5.8,
  "bodyTemperature": 36.5,
  "sleepHours": 7,
  "steps": 3500,
  "measuredAt": "2026-06-22T08:00:00"
}
```

### 10.3 获取近7天数据
```
GET /api/health/weekly/{userId}
```

### 10.4 获取告警数据
```
GET /api/health/warning/{userId}
```
> 条件: `warning_level > 0`

### 10.5 获取最新数据
```
GET /api/health/latest/{userId}
```

### 10.6 健康分析
```
GET /api/health/analyze/{userId}
```
**响应**: `String` — 健康分析报告文本

### 10.7 生成健康报告
```
POST /api/health/report/{userId}
```
> 生成报告并写入 `health_report` 字段

---

## 11. 消息 /api/messages

### 11.1 发送消息
```
POST /api/messages
```
**请求体**:
```json
{
  "senderId": 14,
  "receiverId": 15,
  "content": "吃药了吗",
  "type": "text"
}
```
> type 可选: `text`（默认）

### 11.2 获取接收消息
```
GET /api/messages/receiver/{receiverId}
```

### 11.3 获取未读消息
```
GET /api/messages/unread/{receiverId}
```

### 11.4 未读消息数
```
GET /api/messages/unread/count/{receiverId}
```
**响应**: `{ "count": 3 }`

### 11.5 标记单条已读
```
PUT /api/messages/{messageId}/read
```

### 11.6 全部标记已读
```
PUT /api/messages/receiver/{receiverId}/read
```

### 11.7 获取对话记录
```
GET /api/messages/conversation?userId=14&otherId=15
```
> 返回两人之间的所有消息（双向）

### 11.8 清空消息表
```
DELETE /api/messages/clear
```

---

## 12. 设备 /api/devices

### 12.1 获取所有设备
```
GET /api/devices
```

### 12.2 添加设备
```
POST /api/devices
```
**请求体**: `Device` 对象
```json
{
  "userId": 14,
  "deviceName": "客厅灯光",
  "deviceType": "light",
  "deviceId": "dev-006",
  "location": "客厅"
}
```

### 12.3 更新设备
```
PUT /api/devices/{id}
```

### 12.4 删除设备
```
DELETE /api/devices/{id}
```

### 12.5 查询用户设备
```
GET /api/devices/user/{userId}
```

### 12.6 获取设备详情
```
GET /api/devices/{id}
```

### 12.7 控制设备（开关等）
```
POST /api/devices/{deviceId}/control
```
**请求体**:
```json
{ "action": "on" }
```
> action: `"on"` / `"off"`  
> 设备状态: `"online"` / `"offline"`

---

## 13. 文件上传 /api/upload

> 阿里云 OSS 存储，返回文件 URL

### 13.1 上传健康报告
```
POST /api/upload/health-report
Content-Type: multipart/form-data
```
**参数**: `file` — 文件  
**响应**: `String` — OSS URL

### 13.2 上传头像
```
POST /api/upload/avatar
Content-Type: multipart/form-data
```
**参数**: `file` — 图片（仅限 jpg/jpeg/png/gif）  
**响应**: `String` — OSS URL

### 13.3 批量上传报告
```
POST /api/upload/health-reports
Content-Type: multipart/form-data
```
**参数**: `files` — 多文件  
**响应**: 逗号分隔的 URL 列表

---

## 14. 数据修复 /api/fix

> 管理工具接口，修复乱码数据

### 14.1 修复订单乱码
```
POST /api/fix/orders
```
> 将包含 `?`、`Community`、`1` 的地址替换为中文

### 14.2 修复设备乱码
```
POST /api/fix/devices
```
> 将包含 `?` 的设备名/位置替换为中文

### 14.3 修复全部
```
POST /api/fix/all
```

---

## 附录 A: 完整实体字段速查

### User（sys_user）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| username | String | 用户名（唯一） |
| password | String | BCrypt 加密 |
| phone | String | 手机号 |
| email | String | 邮箱 |
| name | String | 姓名 |
| age | Integer | 年龄 |
| gender | String | 性别 |
| address | String | 地址 |
| avatar | String | 头像 URL |
| role | Integer | 1-管理 2-老人(旧) 3-老人 4-家属 |
| healthStatus | Integer | 健康状态 |
| emergencyContact | String | 紧急联系人 |
| emergencyPhone | String | 紧急联系电话 |
| isDeleted | Integer | 逻辑删除 (0/1) |

### Wallet（wallet）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| userId | Long | 用户ID（唯一约束） |
| balance | BigDecimal | 余额 DECIMAL(10,2) |
| version | Integer | 乐观锁版本号 |

### ServiceOrder（service_order）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| orderNo | String | 订单号（唯一） |
| userId | Long | 用户ID |
| serviceId | Long | 服务ID |
| status | String | pending/confirmed/completed/cancelled |
| serviceTime | LocalDateTime | 预约时间 |
| address | String | 服务地址 |
| remark | String | 备注 |
| amount | BigDecimal | 金额 |
| paymentStatus | String | unpaid/paid |
| version | Integer | 乐观锁 |
| isDeleted | Integer | 逻辑删除 |

### Activity（activity）
| 字段 | 类型 | 说明 |
|------|------|------|
| maxParticipants | Integer | 最大人数 |
| currentParticipants | Integer | 当前报名人数 |
| version | Integer | 乐观锁（防超卖） |
| isDeleted | Integer | 逻辑删除 |

### EmergencyCall（emergency_call）
| 字段 | 类型 | 说明 |
|------|------|------|
| status | String | pending/responding/resolved/cancelled |
| callType | String | 呼叫类型 |
| handler | String | 处理人 |
| latitude/longitude | Double | 位置 |

---

## 附录 B: 接口一览表

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| Auth | POST | /api/auth/login | 登录 |
| Auth | POST | /api/auth/register | 注册 |
| User | GET | /api/users | 用户列表 |
| User | GET/PUT/DELETE | /api/users/{id} | 用户CRUD |
| Relation | POST | /api/relations/bind | 绑定亲属 |
| Relation | GET | /api/relations/elder/{id} | 老人→家属 |
| Relation | GET | /api/relations/family/{id} | 家属→老人 |
| Relation | GET | /api/relations/exists | 判断关系 |
| Wallet | GET | /api/wallet/{userId} | 获取钱包 |
| Wallet | GET | /api/wallet/balance/{userId} | 查询余额 |
| Wallet | POST | /api/wallet/recharge | 充值 |
| Wallet | POST | /api/wallet/pay | 扣款 |
| Payment | POST | /api/payments/pay | 本人支付 |
| Payment | POST | /api/payments/family-pay | 家属代付 |
| Payment | POST | /api/payments/confirm/{id} | 确认支付 |
| Payment | GET | /api/payments/orders/need-confirm | 待确认订单 |
| Service | GET/POST | /api/services | 服务列表/新增 |
| Service | PUT/DELETE | /api/services/{id} | 服务修改/删除 |
| Service | GET | /api/services/type/{type} | 按类型筛选 |
| Order | GET/POST | /api/orders | 订单列表/创建 |
| Order | GET | /api/orders/user/{userId} | 用户订单 |
| Order | GET | /api/orders/status/{status} | 按状态筛选 |
| Order | PUT | /api/orders/{id}/status | 更新状态 |
| Order | PUT | /api/orders/{id}/cancel | 取消订单 |
| Activity | GET/POST | /api/activities | 活动列表/创建 |
| Activity | GET | /api/activities/active | 进行中活动 |
| Activity | GET | /api/activities/upcoming | 即将开始 |
| Activity | POST | /api/activities/{id}/register | 报名 |
| Activity | DELETE | /api/activities/{id}/register | 取消报名 |
| Emergency | POST | /api/emergency/call | 发起呼叫 |
| Emergency | GET | /api/emergency/unresolved | 未解决呼叫 |
| Emergency | PUT | /api/emergency/{id}/respond | 响应 |
| Emergency | PUT | /api/emergency/{id}/resolve | 解决 |
| Health | POST | /api/health | 保存数据 |
| Health | GET | /api/health/weekly/{userId} | 周报 |
| Health | GET | /api/health/latest/{userId} | 最新数据 |
| Health | GET | /api/health/analyze/{userId} | 分析报告 |
| Message | POST | /api/messages | 发送消息 |
| Message | GET | /api/messages/receiver/{id} | 接收消息 |
| Message | GET | /api/messages/unread/{id} | 未读消息 |
| Message | GET | /api/messages/conversation | 对话记录 |
| Device | GET | /api/devices | 设备列表 |
| Device | POST | /api/devices/{id}/control | 控制设备 |
| Upload | POST | /api/upload/avatar | 上传头像 |
| Upload | POST | /api/upload/health-report | 上传报告 |
