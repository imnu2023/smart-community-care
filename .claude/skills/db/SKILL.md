---
name: db
description: "建立前端数据契约：创建 Pinia Store、配置 Vue Router 权限守卫、统一 API 错误拦截。用于为新模块搭建数据层基建。"
user-invocable: true
argument-hint: "[业务模块名]"
---

# 任务目标：建立前端数据契约（Pinia & Router）
请读取根目录下的 `API_DOCUMENTATION.md` 以及当前指定的业务模块，完成以下基建：

1. **状态管理 (Pinia)**：在 `src/stores/` 下创建或更新对应的 Store 文件。只需编写当前模块涉及的 State 和 Action（包含 API 调用逻辑），不要写多余的接口。
2. **路由与权限 (Vue Router)**：检查 `src/router/index.js`。如果当前模块需要权限拦截，请确保全局前置守卫（beforeEach）已正确处理 Token 校验和 role（1=管理员, 2=老人, 3=家属）的端侧分发。
3. **错误处理**：确保所有的 API 调用都通过统一的 `request.js` 拦截器，并妥善处理异常情况。
