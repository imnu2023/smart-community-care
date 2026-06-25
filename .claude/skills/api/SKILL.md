---
name: api
description: "将静态 UI 与业务逻辑绑定：替换假数据、绑定事件、连通 API、补充 Loading/Error 状态。用于给已拆解的组件注入真实逻辑。"
user-invocable: true
argument-hint: "[组件文件路径]"
---

# 任务目标：点对点业务逻辑注入
请读取我指定的 Component 文件和对应的 Store 文件。现在我们要将静态 UI 与真实的业务逻辑绑定：

1. **变量替换**：将模板中写死的假数据（如数字、名字、状态文案）替换为真实的响应式变量（ref/reactive 或 store 中的 state）。
2. **事件绑定**：为按钮、表单提交等交互元素绑定真实的点击/变更事件。
3. **接口连通**：如果需要请求接口，请统一调用 Store 中封装好的 Action，或直接引入 `src/api/` 中的方法。
4. **状态降级**：补充加载中（Loading）和失败（Error）时的基础防御性交互（如按钮的 loading 状态或错误提示）。
