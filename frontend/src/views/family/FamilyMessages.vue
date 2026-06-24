<template>
  <div class="fm-page">
    <!-- Header -->
    <header class="fm-hero">
      <div>
        <h1 class="fm-title">消息中心</h1>
        <p class="fm-desc" v-if="notifications.length > 0">
          {{ unreadCount > 0 ? `${unreadCount} 条未读通知` : '全部已读' }}
        </p>
      </div>
      <button v-if="unreadCount > 0" class="btn btn-secondary" @click="markAllRead">
        <AppIcon name="check" size="16" /> 全部已读
      </button>
    </header>

    <!-- Type filter tabs -->
    <nav class="fm-tabs">
      <button
        v-for="t in typeFilters"
        :key="t.key"
        class="fm-tab"
        :class="{ 'fm-tab--active': activeFilter === t.key }"
        @click="activeFilter = t.key"
      >
        <span class="fm-tab-dot" :class="`fm-tab-dot--${t.key}`"></span>
        {{ t.label }}
        <span v-if="typeCount(t.key) > 0" class="fm-tab-count">{{ typeCount(t.key) }}</span>
      </button>
    </nav>

    <!-- Loading -->
    <div v-if="loading" class="fm-skeleton">
      <div v-for="i in 5" :key="i" class="skeleton" style="height:88px;border-radius:var(--radius-lg);margin-bottom:var(--space-sm)"></div>
    </div>

    <!-- Empty -->
    <div v-else-if="filteredList.length === 0" class="fm-empty">
      <AppIcon name="message" size="48" class="fm-empty-icon" />
      <h3>{{ activeFilter === 'all' ? '暂无消息' : '没有此类通知' }}</h3>
      <p>系统通知和家人动态会显示在这里</p>
    </div>

    <!-- Notification list -->
    <div v-else class="fm-list">
      <div
        v-for="msg in filteredList"
        :key="msg.id"
        class="fm-item"
        :class="{ 'fm-item--unread': !msg.isRead }"
        @click="readMessage(msg)"
      >
        <!-- Type icon -->
        <span class="fm-item-icon" :class="`fm-item-icon--${msg.type}`">
          <AppIcon :name="iconForType(msg.type)" size="20" />
        </span>

        <!-- Content -->
        <div class="fm-item-body">
          <div class="fm-item-top">
            <span class="badge" :class="badgeClass(msg.type)">{{ typeLabel(msg.type) }}</span>
            <span class="fm-item-time">{{ formatTime(msg.createdAt) }}</span>
          </div>
          <p class="fm-item-text">{{ msg.content }}</p>
        </div>

        <!-- Actions -->
        <div class="fm-item-actions">
          <span v-if="!msg.isRead" class="fm-unread-dot"></span>
          <button class="fm-item-delete" @click.stop="deleteMessage(msg.id)" title="删除">
            <AppIcon name="x" size="14" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { messageAPI } from '../../api'
import AppIcon from '../../components/AppIcon.vue'

const familyId = ref(parseInt(localStorage.getItem('userId') || '0'))
const loading = ref(true)
const notifications = ref([])
const activeFilter = ref('all')
let pollTimer = null

const unreadCount = computed(() => notifications.value.filter(m => !m.isRead).length)

const typeFilters = [
  { key: 'all', label: '全部' },
  { key: 'health', label: '健康' },
  { key: 'emergency', label: '紧急' },
  { key: 'order', label: '订单' },
  { key: 'payment', label: '支付' },
  { key: 'activity', label: '活动' }
]

const typeConfig = {
  activity: { label: '活动通知', icon: 'calendar', badge: 'badge-info' },
  payment: { label: '支付通知', icon: 'star', badge: 'badge-warning' },
  health: { label: '健康提醒', icon: 'heart', badge: 'badge-success' },
  order: { label: '订单通知', icon: 'package', badge: 'badge-info' },
  emergency: { label: '紧急通知', icon: 'bell', badge: 'badge-danger' }
}

const typeLabel = (t) => typeConfig[t]?.label || '系统通知'
const iconForType = (t) => typeConfig[t]?.icon || 'bell'
const badgeClass = (t) => typeConfig[t]?.badge || 'badge-neutral'
const typeCount = (key) => key === 'all' ? notifications.value.length : notifications.value.filter(m => m.type === key).length
const filteredList = computed(() => activeFilter.value === 'all' ? notifications.value : notifications.value.filter(m => m.type === activeFilter.value))

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  let date
  if (Array.isArray(dateStr)) date = new Date(dateStr[0], dateStr[1] - 1, dateStr[2], dateStr[3] || 0, dateStr[4] || 0)
  else if (typeof dateStr === 'string') date = new Date(dateStr.replace('T', ' ').replace('Z', ''))
  else return ''
  if (isNaN(date.getTime())) return ''
  const diff = Date.now() - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const loadNotifications = async () => {
  try {
    const res = await messageAPI.getList(familyId.value)
    if (res?.code === 200) notifications.value = res.data || []
  } catch (e) { /* silent */ }
  loading.value = false
}

const readMessage = async (msg) => {
  if (!msg.isRead) {
    try { await messageAPI.markAsRead(msg.id); msg.isRead = true } catch (e) { /* silent */ }
  }
}

const deleteMessage = async (id) => {
  try {
    await messageAPI.delete(id)
    notifications.value = notifications.value.filter(m => m.id !== id)
    ElMessage.success('已删除')
  } catch (e) { ElMessage.error('删除失败') }
}

const markAllRead = async () => {
  try {
    await messageAPI.markAllAsRead(familyId.value)
    notifications.value.forEach(m => m.isRead = true)
    ElMessage.success('全部已读')
  } catch (e) { ElMessage.error('操作失败') }
}

onMounted(() => { loadNotifications(); pollTimer = setInterval(loadNotifications, 30000) })
onUnmounted(() => { if (pollTimer) clearInterval(pollTimer) })
</script>

<style scoped>
.fm-page { padding-bottom: var(--space-section); }

/* Header */
.fm-hero {
  display: flex; justify-content: space-between; align-items: flex-start;
  margin-bottom: var(--space-xl); flex-wrap: wrap; gap: var(--space-md);
}
.fm-title {
  font-size: clamp(28px, 4vw, 40px); font-weight: var(--weight-bold);
  letter-spacing: -0.025em; color: var(--color-on-surface);
  margin: 0 0 var(--space-xs); text-wrap: balance;
}
.fm-desc { font-size: var(--text-body-lg); color: var(--color-on-surface-variant); margin: 0; }

/* Filter tabs */
.fm-tabs {
  display: flex; gap: var(--space-xs); margin-bottom: var(--space-xl);
  flex-wrap: wrap;
}
.fm-tab {
  display: flex; align-items: center; gap: 6px;
  padding: 8px 18px; border-radius: var(--radius-full);
  border: 1px solid var(--color-outline-variant);
  background: var(--color-surface-container-lowest);
  font-size: var(--text-label-md); font-weight: var(--weight-medium);
  color: var(--color-on-surface-variant);
  font-family: inherit; cursor: pointer;
  transition: all 0.2s cubic-bezier(0.32, 0.72, 0, 1);
}
.fm-tab:hover { background: var(--color-surface-container-high); color: var(--color-on-surface); }
.fm-tab--active {
  background: var(--color-primary-fixed);
  border-color: var(--color-primary);
  color: var(--color-on-primary-fixed);
  font-weight: var(--weight-semibold);
}
.fm-tab-dot { width: 6px; height: 6px; border-radius: 50%; }
.fm-tab-dot--all { background: var(--color-on-surface-variant); }
.fm-tab-dot--health { background: var(--color-tertiary); }
.fm-tab-dot--emergency { background: var(--color-error); }
.fm-tab-dot--order { background: var(--color-primary); }
.fm-tab-dot--payment { background: var(--color-secondary); }
.fm-tab-dot--activity { background: var(--color-primary); }
.fm-tab-count {
  font-size: 11px; font-weight: var(--weight-bold);
  background: var(--color-surface-container-high);
  color: var(--color-on-surface-variant);
  padding: 1px 7px; border-radius: var(--radius-full); min-width: 20px; text-align: center;
}

/* Skeleton / Empty */
.fm-skeleton { max-width: 720px; }
.fm-empty { max-width: 480px; text-align: center; padding: var(--space-2xl); border: 1px dashed var(--color-outline-variant); border-radius: var(--radius-2xl); color: var(--color-on-surface-variant); }
.fm-empty-icon { margin-bottom: var(--space-lg); opacity: 0.35; }
.fm-empty h3 { margin: 0 0 var(--space-xs); color: var(--color-on-surface); font-size: var(--text-headline-md); }

/* Notification list */
.fm-list { display: flex; flex-direction: column; gap: var(--space-xs); max-width: 780px; }
.fm-item {
  display: flex; align-items: flex-start; gap: var(--space-md);
  padding: var(--space-md) var(--space-lg);
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-lg);
  cursor: pointer;
  box-shadow: var(--shadow-card);
  transition: all 0.25s cubic-bezier(0.32, 0.72, 0, 1);
}
.fm-item:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-1px); }
.fm-item--unread {
  background: var(--color-primary-fixed);
  border-color: var(--color-primary-fixed-dim);
}
.fm-item-icon {
  width: 44px; height: 44px; border-radius: var(--radius-md);
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  background: var(--color-surface-container); color: var(--color-on-surface-variant);
}
.fm-item-icon--emergency { background: var(--color-error-container); color: var(--color-error); }
.fm-item-icon--health { background: var(--color-tertiary-fixed); color: var(--color-tertiary); }
.fm-item-icon--order { background: var(--color-primary-fixed); color: var(--color-primary); }
.fm-item-icon--payment { background: var(--color-secondary-fixed); color: var(--color-secondary); }
.fm-item-icon--activity { background: var(--color-primary-fixed-dim); color: var(--color-on-primary-fixed-variant); }

.fm-item-body { flex: 1; min-width: 0; }
.fm-item-top { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: var(--space-xxs); }
.fm-item-time { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin-left: auto; }
.fm-item-text { font-size: var(--text-body-md); color: var(--color-on-surface); line-height: 1.5; margin: 0; }

.fm-item-actions {
  display: flex; flex-direction: column; align-items: center; gap: var(--space-xs);
  flex-shrink: 0;
}
.fm-unread-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--color-primary); }
.fm-item-delete {
  width: 28px; height: 28px; border-radius: var(--radius-sm); border: none;
  background: transparent; color: var(--color-on-surface-variant);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; opacity: 0; transition: all 0.2s;
  font-family: inherit;
}
.fm-item:hover .fm-item-delete { opacity: 1; }
.fm-item-delete:hover { background: var(--color-error-container); color: var(--color-error); }

@media (max-width: 767px) {
  .fm-item { padding: var(--space-md); }
  .fm-tabs { gap: 4px; }
  .fm-tab { padding: 6px 14px; font-size: var(--text-label-sm); }
}
</style>
