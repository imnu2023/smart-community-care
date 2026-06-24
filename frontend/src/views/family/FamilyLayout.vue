<template>
  <div class="fl-shell">
    <!-- ================================================================
         TOP NAVIGATION — warm, premium, human
         ================================================================ -->
    <header class="fl-topbar">
      <div class="fl-topbar-inner">
        <!-- Brand -->
        <router-link to="/family" class="fl-brand">
          <span class="fl-brand-mark">
            <AppIcon name="people" size="22" />
          </span>
          <div class="fl-brand-text">
            <span class="fl-brand-title">关怀中心</span>
            <span class="fl-brand-sub">Family Care</span>
          </div>
        </router-link>

        <!-- Nav links -->
        <nav class="fl-nav">
          <router-link to="/family" class="fl-nav-item" :class="{ 'fl-nav-item--active': $route.path === '/family' }">
            <AppIcon name="dashboard" size="16" />
            <span>概览</span>
          </router-link>
          <router-link to="/family/orders" class="fl-nav-item" :class="{ 'fl-nav-item--active': $route.path === '/family/orders' }">
            <AppIcon name="package" size="16" />
            <span>代付</span>
            <span v-if="pendingCount > 0" class="fl-nav-badge">{{ pendingCount }}</span>
          </router-link>
          <router-link to="/family/activities" class="fl-nav-item" :class="{ 'fl-nav-item--active': $route.path === '/family/activities' }">
            <AppIcon name="calendar" size="16" />
            <span>动态</span>
          </router-link>
          <router-link to="/family/services" class="fl-nav-item" :class="{ 'fl-nav-item--active': $route.path === '/family/services' }">
            <AppIcon name="tool" size="16" />
            <span>服务</span>
          </router-link>
          <router-link to="/family/messages" class="fl-nav-item" :class="{ 'fl-nav-item--active': $route.path === '/family/messages' }">
            <AppIcon name="message" size="16" />
            <span>消息</span>
            <span v-if="unreadCount > 0" class="fl-nav-badge">{{ unreadCount }}</span>
          </router-link>
        </nav>

        <!-- Right: user -->
        <div class="fl-user">
          <div class="fl-user-card">
            <span class="fl-user-avatar">{{ userName.charAt(0) }}</span>
            <span class="fl-user-name">{{ userName }}</span>
          </div>
          <button class="fl-logout" @click="handleLogout" title="退出登录">
            <AppIcon name="logout" size="18" />
          </button>
        </div>
      </div>
    </header>

    <!-- ================================================================
         MAIN CONTENT
         ================================================================ -->
    <main class="fl-main">
      <router-view v-slot="{ Component }">
        <transition name="fl-page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { messageAPI, paymentAPI } from '../../api'
import AppIcon from '../../components/AppIcon.vue'

const router = useRouter()
const userName = ref(localStorage.getItem('name') || '家属')
const familyId = ref(parseInt(localStorage.getItem('userId') || '0'))
const unreadCount = ref(0)
const pendingCount = ref(0)

const loadCounts = async () => {
  try {
    const [msgRes, orderRes] = await Promise.all([
      messageAPI.countUnread(familyId.value),
      paymentAPI.getElderOrders(familyId.value)
    ])
    if (msgRes?.code === 200) unreadCount.value = msgRes.data?.count || 0
    if (orderRes?.code === 200 && orderRes.data) {
      pendingCount.value = orderRes.data.filter(o => o.status === 'pending' && o.paymentStatus !== 'paid').length
    }
  } catch (e) { /* silent */ }
}

const handleLogout = () => {
  localStorage.clear()
  router.push('/login')
  ElMessage.success('已退出')
}

onMounted(() => loadCounts())
</script>

<style scoped>
/* ============================================================
   SHELL
   ============================================================ */
.fl-shell {
  min-height: 100vh;
  background:
    radial-gradient(ellipse 80% 50% at 20% 0%, rgba(0, 80, 203, 0.018) 0%, transparent 60%),
    radial-gradient(ellipse 60% 40% at 85% 100%, rgba(0, 104, 37, 0.012) 0%, transparent 50%),
    var(--color-surface);
}

/* ---- Topbar: taller, softer, more premium ---- */
.fl-topbar {
  position: sticky; top: 0; z-index: var(--z-sticky);
  background: rgba(253, 253, 252, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0, 47, 112, 0.06);
}
.fl-topbar-inner {
  max-width: var(--content-max-width);
  margin: 0 auto;
  display: flex; align-items: center;
  height: 72px;
  padding: 0 var(--space-xl);
  gap: var(--space-2xl);
}

/* Brand */
.fl-brand {
  display: flex; align-items: center; gap: var(--space-sm);
  text-decoration: none; flex-shrink: 0;
}
.fl-brand-mark {
  width: 40px; height: 40px;
  border-radius: var(--radius-md);
  background: linear-gradient(135deg, #0050cb 0%, #003fa4 100%);
  color: #fff;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 12px rgba(0, 80, 203, 0.18);
}
.fl-brand-text { display: flex; flex-direction: column; line-height: 1.2; }
.fl-brand-title {
  font-size: var(--text-body-md);
  font-weight: var(--weight-bold);
  color: var(--color-on-surface);
  letter-spacing: -0.01em;
}
.fl-brand-sub {
  font-size: 10px;
  font-weight: var(--weight-medium);
  color: var(--color-on-surface-variant);
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

/* Nav */
.fl-nav { display: flex; gap: 4px; flex: 1; }
.fl-nav-item {
  display: flex; align-items: center; gap: var(--space-xs);
  padding: 10px 18px;
  border-radius: var(--radius-lg);
  text-decoration: none;
  font-size: var(--text-label-md);
  font-weight: var(--weight-medium);
  color: var(--color-on-surface-variant);
  transition: all 0.2s cubic-bezier(0.32, 0.72, 0, 1);
  position: relative;
}
.fl-nav-item:hover {
  background: rgba(0, 80, 203, 0.04);
  color: var(--color-primary);
}
.fl-nav-item:active { transform: scale(0.97); }
.fl-nav-item--active {
  background: rgba(0, 80, 203, 0.08);
  color: var(--color-primary);
  font-weight: var(--weight-semibold);
}
.fl-nav-item--active::after {
  content: '';
  position: absolute; bottom: 2px; left: 50%; transform: translateX(-50%);
  width: 20px; height: 3px;
  border-radius: 3px;
  background: var(--color-primary);
}
.fl-nav-badge {
  min-width: 18px; height: 18px;
  border-radius: var(--radius-full);
  background: var(--color-error);
  color: #fff;
  font-size: 11px; font-weight: var(--weight-bold);
  display: flex; align-items: center; justify-content: center;
  padding: 0 5px;
}

/* User area */
.fl-user { display: flex; align-items: center; gap: var(--space-sm); flex-shrink: 0; }
.fl-user-card {
  display: flex; align-items: center; gap: var(--space-sm);
  padding: 6px 14px 6px 6px;
  border-radius: var(--radius-full);
  background: var(--color-surface-container);
  transition: background 0.2s var(--ease-out);
}
.fl-user-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: var(--color-secondary-fixed);
  color: var(--color-on-secondary-fixed);
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: var(--weight-bold);
}
.fl-user-name { font-size: var(--text-label-sm); color: var(--color-on-surface); font-weight: var(--weight-medium); }
.fl-logout {
  width: 40px; height: 40px; border-radius: var(--radius-lg);
  border: 1px solid var(--color-outline-variant);
  background: var(--color-surface-container-lowest);
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  color: var(--color-on-surface-variant);
  transition: all 0.2s var(--ease-out);
}
.fl-logout:hover { background: var(--color-error-container); color: var(--color-error); border-color: transparent; }

/* ---- Main ---- */
.fl-main {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: var(--space-2xl) var(--space-xl) var(--space-section);
  min-height: calc(100vh - 72px);
}

/* Page transitions */
.fl-page-enter-active { transition: opacity 0.3s cubic-bezier(0.32,0.72,0,1), transform 0.35s cubic-bezier(0.32,0.72,0,1); }
.fl-page-leave-active { transition: opacity 0.2s cubic-bezier(0.32,0.72,0,1), transform 0.2s cubic-bezier(0.32,0.72,0,1); }
.fl-page-enter-from { opacity: 0; transform: translateY(12px); }
.fl-page-leave-to { opacity: 0; transform: translateY(-6px); }

/* ---- Responsive ---- */
@media (max-width: 767px) {
  .fl-topbar-inner { padding: 0 var(--space-md); gap: var(--space-sm); height: 64px; }
  .fl-brand-sub { display: none; }
  .fl-nav-item span { display: none; }
  .fl-nav-item { padding: 10px 14px; }
  .fl-user-name { display: none; }
  .fl-user-card { padding: 6px; }
  .fl-main { padding: var(--space-lg) var(--space-md); }
}
</style>
