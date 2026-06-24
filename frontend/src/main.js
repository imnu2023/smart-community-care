import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'

// 设计系统：Element 默认 → 主题覆盖 → 全局 token + 布局
import 'element-plus/dist/index.css'
import './style/element-override.css'
import './style/global.css'

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')