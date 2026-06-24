<template>
  <div class="ad-layout">
    <aside class="ad-sidebar">
      <div class="ad-sidebar-header">
        <div class="ad-logo">
          <span class="ad-logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
          </span>
          <span class="ad-logo-text">智慧养老管理</span>
        </div>
      </div>
      <nav class="ad-nav">
        <button v-for="item in menuItems" :key="item.path" class="ad-nav-item" :class="{ 'ad-nav-item--active': currentPath === item.path }" @click="handleNavClick(item.path)">
          <span class="ad-nav-icon"><AppIcon :name="item.icon" size="18" /></span>
          <span>{{ item.name }}</span>
        </button>
      </nav>
      <div class="ad-sidebar-footer">
        <div class="ad-user">
          <span class="ad-user-avatar">{{ userName.charAt(0) }}</span>
          <div><div class="ad-user-name">{{ userName }}</div><div class="ad-user-role">管理员</div></div>
        </div>
        <button class="btn btn-ghost btn-sm ad-logout" @click="handleLogout" style="color:var(--color-error)">退出登录</button>
      </div>
    </aside>
    <main class="ad-main">
      <header class="ad-topbar">
        <div class="ad-breadcrumb"><span>管理后台</span><span style="margin:0 var(--space-xs);color:var(--color-outline)">/</span><span>{{ currentMenuName }}</span></div>
      </header>
      <div class="ad-content"><router-view /></div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppIcon from '../../components/AppIcon.vue'

const route = useRoute(); const router = useRouter()
const userName = ref('')
const menuItems = [
  { name: '用户管理', path: '/admin/users', icon: 'user' },
  { name: '设备管理', path: '/admin/devices', icon: 'home' },
  { name: '紧急呼叫', path: '/admin/emergencies', icon: 'bell' },
  { name: '服务管理', path: '/admin/services', icon: 'tool' },
  { name: '订单管理', path: '/admin/orders', icon: 'package' },
  { name: '关系管理', path: '/admin/relations', icon: 'people' }
]
const currentPath = computed(() => route.path)
const currentMenuName = computed(() => (menuItems.find(i => i.path === currentPath.value) || {}).name || '管理后台')
const handleNavClick = (p) => router.push(p)
const handleLogout = () => { localStorage.clear(); router.push('/login'); ElMessage.success('已退出') }
onMounted(() => { userName.value = localStorage.getItem('name') || '管理员' })
</script>

<style scoped>
.ad-layout {
  display: flex; height: 100vh;
  background:
    radial-gradient(ellipse 50% 40% at 80% 20%, rgba(0, 80, 203, 0.02) 0%, transparent 50%),
    var(--color-surface);
}
.ad-sidebar {
  width: 240px;
  background: linear-gradient(180deg, #f9fafc 0%, var(--color-surface-container-low) 100%);
  border-right: 1px solid var(--color-outline-variant);
  display: flex; flex-direction: column;
}
.ad-sidebar-header { padding: var(--space-lg); border-bottom: 1px solid var(--color-outline-variant); }
.ad-logo { display: flex; align-items: center; gap: var(--space-sm); }
.ad-logo-icon {
  width: 36px; height: 36px;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-hover) 100%);
  color: var(--color-on-primary);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 80, 203, 0.15);
}
.ad-logo-text { font-size: var(--text-label-md); font-weight: var(--weight-semibold); color: var(--color-on-surface); }
.ad-nav { flex: 1; padding: var(--space-sm); }
.ad-nav-item {
  display: flex; align-items: center; gap: var(--space-sm);
  width: 100%; padding: var(--space-sm) var(--space-md);
  margin-bottom: 2px; border: none; border-radius: var(--radius-md);
  background: transparent; cursor: pointer;
  font-family: inherit; font-size: var(--text-label-md);
  color: var(--color-on-surface-variant);
  transition: all 0.2s var(--ease-out);
  min-height: var(--touch-min);
}
.ad-nav-item:hover { background: var(--color-surface-container-highest); color: var(--color-on-surface); }
.ad-nav-item:active { transform: scale(0.98); }
.ad-nav-item--active { background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); font-weight: var(--weight-semibold); }
.ad-nav-icon { display: flex; align-items: center; justify-content: center; width: 24px; flex-shrink: 0; }
.ad-sidebar-footer { padding: var(--space-md); border-top: 1px solid var(--color-outline-variant); }
.ad-user { display: flex; align-items: center; gap: var(--space-sm); padding: var(--space-sm); background: var(--color-surface-container); border-radius: var(--radius-md); margin-bottom: var(--space-sm); }
.ad-user-avatar { width: 36px; height: 36px; border-radius: 50%; background: var(--color-secondary-fixed); color: var(--color-on-secondary-fixed); display: flex; align-items: center; justify-content: center; font-weight: var(--weight-semibold); font-size: var(--text-label-md); }
.ad-user-name { font-size: var(--text-label-md); font-weight: var(--weight-semibold); }
.ad-user-role { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.ad-logout { width: 100%; }
.ad-main { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.ad-topbar {
  height: 56px;
  background: var(--color-surface-container-lowest);
  border-bottom: 1px solid var(--color-outline-variant);
  box-shadow: 0 1px 3px rgba(0, 47, 112, 0.04);
  display: flex; align-items: center;
  padding: 0 var(--space-xl);
}
.ad-breadcrumb { font-size: var(--text-label-md); color: var(--color-on-surface-variant); }
.ad-content { flex: 1; overflow: auto; padding: var(--space-lg); }
</style>
