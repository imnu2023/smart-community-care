---
name: ui
description: "通过 Stitch MCP 读取 Vue 3 高保真 UI，暂存预览后拆分为子组件。包含状态管理、防劣化与人机确认机制，用于从零生成新页面。"
user-invocable: true
---

# 任务目标：通过 Stitch MCP 读取高保真 UI 并进行工程化结构拆解

严格按照以下流程执行 Vue 3 (Tailwind + 项目现有设计 Token) 页面的读取、人工确认与重构。

---

## 阶段零：环境感知 (Context Awareness)

在操作任何文件之前，必须完成以下读取：

1. `frontend/src/router/index.js` — 了解现有路由结构、publicPaths、角色守卫
2. `frontend/tailwind.config.js` — 掌握已配置的设计 Token（色板、字号、间距、阴影），确保后续拆分不引用未定义的 Token
3. `frontend/src/stores/` 目录 — 了解已有 Store 的数据模式（当前为 Pinia Composition API 风格）
4. `frontend/src/views/` 目录 — 判断哪些视图为空壳占位、哪些已有内容
5. `frontend/src/components/` 目录 — 确认当前是否有已有组件需要复用

---

## 阶段一：通过 MCP 读取 Stitch 页面

1. 调用 `mcp__stitch__sp_projects` 获取 Stitch 项目列表
2. 选择合适的项目（优先选最近更新的），调用 `mcp__stitch__sp_screens` 列出该项目下的所有 screen
3. 调用 `mcp__stitch__sp_screen` 获取目标 screen 的完整 HTML 源码（`includeHtml: true`）
4. 如果返回的是纯 HTML（非 Vue SFC 格式），优先调用 `mcp__stitch__sp_to_vue` 转换。**若该调用超过 120 秒无响应或报错超时，立即放弃 MCP 转换**，改为直接在对话上下文中将 HTML 手工包裹为 Vue 3 SFC（`<template>` → 原 HTML，`<script setup>` → 空，`<style scoped>` → 原内联样式）。严禁反复重试 MCP 转换。
5. 将最终代码保存到 `frontend/src/views/__TEMP_PREVIEW__.vue`，无需暂停预览，直接进入拆分。

### 拆分策略
1. 读取 `__TEMP_PREVIEW__.vue`
2. 按以下颗粒度拆解：

| 拆分 | 不拆分 |
|------|--------|
| 布局模块 (Header / Sidebar / Footer) | 单个普通按钮 |
| 独立业务区块 (数据看板 / 表单 / 列表卡片) | 纯装饰性包裹元素 |
| 可复用的 UI 单元 (卡片 / 面板) | 单行文本或图标 |

3. 组件命名：PascalCase + 业务语义（如 `DashboardHeader.vue`、`HealthMonitorCard.vue`），存放到 `frontend/src/components/` 下按业务线分目录

### 样式处理
4. `<style scoped>` 拆分策略：
   - 仅被一个子组件使用的样式/动画 → 移入该子组件的 `<style scoped>`
   - 被多个子组件共用的样式（如 `.bento-card`）→ 保留在父组件 `<style scoped>` 中
   - `@keyframes` 动画 → 跟随触发该动画的 DOM 元素所在组件
5. 禁止修改任何 CSS 属性值、Tailwind class、颜色、间距、字号。视图层必须 100% 还原暂存文件，仅调整样式所在文件位置以满足作用域隔离。

### 依赖与状态
6. 确保每个子组件包含所需 import（`ref`、`computed`、`defineProps`、`defineEmits`、图标组件等）
7. 子组件通过 `defineProps` 接收数据，通过 `defineEmits` 向上触发事件。不在子组件内保留全局业务状态。

---

## 阶段二：视图组装与清理现场

### 自动确定目标视图文件
1. 根据阶段零读取的路由和 views 目录，按以下优先级自动判断目标文件：
   - 路由中已注册但内容为占位空壳的文件 → 优先覆写
   - 路由中 `/` 路径对应的视图 → 次优先
   - 若以上都不存在，在 `frontend/src/views/` 下新建 `{业务名}Page.vue`
2. **覆写前防呆检查**：对候选文件执行以下判断，任一条件不满足则**必须暂停并向我确认**：
   - 文件总行数 ≤ 20 行
   - `<script>` 块内无超过 3 行的逻辑代码（不含 import 和空行）
   - 文件中无 `defineProps`、`defineEmits`、`useXxxStore`、`watch`、`onMounted` 等业务关键字
   
   若文件不满足上述条件，说明它可能包含其他开发者已注入的逻辑，终端提问：
   > "⚠️ 目标文件 `{文件名}` 包含非空逻辑，覆写可能导致代码丢失。是否继续覆写？(输入 Y 强制覆写 / 输入 N 手动指定其他文件)"
3. 在目标视图文件中引入所有子组件并拼装
4. 在父组件中统一声明核心响应式状态（`ref`/`reactive`），正确绑定到子组件的 Props 和 Events
5. 确认目标视图依赖齐全且无编译错误后，**删除** `frontend/src/views/__TEMP_PREVIEW__.vue`
6. 输出 Markdown 树状图展示最终的页面-子组件结构