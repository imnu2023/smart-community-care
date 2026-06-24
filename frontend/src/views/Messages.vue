<template>
  <div class="msg-page">
    <header class="msg-header">
      <button class="btn-back" @click="goBack"><AppIcon name="x" size="22" /></button>
      <h1 class="text-headline-lg">消息中心</h1>
      <button v-if="unreadCount > 0" class="btn btn-ghost btn-sm" @click="markAllRead">全部已读 ({{ unreadCount }})</button>
    </header>

    <div v-if="notifications.length > 0" class="msg-list">
      <div v-for="msg in notifications" :key="msg.id" class="msg-item" :class="{ 'msg-item--unread': !msg.isRead }" @click="readMessage(msg)">
        <span class="msg-icon" :class="typeClass(msg.type)"><AppIcon :name="typeIcon(msg.type)" :size="22" /></span>
        <div class="msg-body">
          <div class="msg-top">
            <span class="badge" :class="typeBadge(msg.type)">{{ typeLabel(msg.type) }}</span>
            <span class="msg-time">{{ formatTime(msg.createdAt) }}</span>
          </div>
          <div class="msg-content">{{ msg.content }}</div>
        </div>
        <button class="btn btn-ghost btn-sm msg-delete" @click.stop="deleteMessage(msg.id)" title="删除">✕</button>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon"><AppIcon name="bell" :size="48" /></div>
      <h3>暂无消息</h3>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { messageAPI } from '../api'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const goBack = () => router.push('/dashboard')

const notifications = ref([])
let pollTimer = null

const currentUserId = computed(() => parseInt(localStorage.getItem('userId')) || 0)
const unreadCount = computed(() => notifications.value.filter(m => !m.isRead).length)

const typeConfig = {
  activity: { label: '活动通知', icon: 'calendar', cls: 'msg-icon--activity', badge: 'badge-info' },
  payment: { label: '支付通知', icon: 'star', cls: 'msg-icon--payment', badge: 'badge-warning' },
  health: { label: '健康提醒', icon: 'heart', cls: 'msg-icon--health', badge: 'badge-success' },
  order: { label: '订单通知', icon: 'clipboard', cls: 'msg-icon--order', badge: 'badge-info' },
  emergency: { label: '紧急通知', icon: 'bell', cls: 'msg-icon--emergency', badge: 'badge-danger' }
}

const typeLabel = (t) => typeConfig[t]?.label || '系统通知'
const typeIcon = (t) => typeConfig[t]?.icon || 'bell'
const typeClass = (t) => typeConfig[t]?.cls || ''
const typeBadge = (t) => typeConfig[t]?.badge || 'badge-neutral'

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  let date
  if (Array.isArray(dateStr)) date = new Date(dateStr[0], dateStr[1] - 1, dateStr[2], dateStr[3] || 0, dateStr[4] || 0, dateStr[5] || 0)
  else if (typeof dateStr === 'string') date = new Date(dateStr.replace('T', ' ').replace('Z', ''))
  else return ''
  if (isNaN(date.getTime())) return ''
  const now = new Date(), diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return date.toLocaleDateString('zh-CN')
}

const loadNotifications = async () => {
  try { const res = await messageAPI.getList(currentUserId.value); if (res?.code === 200) notifications.value = res.data || [] } catch (e) {}
}
const readMessage = async (msg) => { if (!msg.isRead) { try { await messageAPI.markAsRead(msg.id); msg.isRead = true } catch (e) {} } }
const deleteMessage = async (id) => { try { await messageAPI.delete(id); notifications.value = notifications.value.filter(m => m.id !== id) } catch (e) {} }
const markAllRead = async () => { try { await messageAPI.markAllAsRead(currentUserId.value); notifications.value.forEach(m => m.isRead = true) } catch (e) {} }

onMounted(() => { loadNotifications(); pollTimer = setInterval(loadNotifications, 30000) })
onUnmounted(() => { if (pollTimer) clearInterval(pollTimer) })
</script>

<style scoped>
.msg-page { min-height: 100vh; background: var(--color-surface); padding: var(--space-lg); }
.msg-header { display: flex; align-items: center; gap: var(--space-md); margin-bottom: var(--space-xl); }
.msg-header h1 { margin: 0; flex: 1; }

.msg-list { display: flex; flex-direction: column; gap: var(--space-sm); max-width: 720px; }
.msg-item { display: flex; align-items: flex-start; gap: var(--space-sm); padding: var(--space-md); background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-lg); cursor: pointer; box-shadow: var(--shadow-card); transition: box-shadow var(--duration-fast) var(--ease-out), transform var(--duration-fast) var(--ease-out); }
.msg-item:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-1px); }
.msg-item--unread { border-color: var(--color-primary-fixed-dim); background: var(--color-primary-fixed); }
.msg-icon { width: 44px; height: 44px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; flex-shrink: 0; background: var(--color-surface-container); color: var(--color-on-surface-variant); }
.msg-icon--emergency { background: var(--color-error-container); }
.msg-icon--health { background: var(--color-tertiary-fixed); }
.msg-body { flex: 1; min-width: 0; }
.msg-top { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: var(--space-xxs); }
.msg-time { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin-left: auto; }
.msg-content { font-size: var(--text-body-md); color: var(--color-on-surface); line-height: 1.5; }
.msg-delete { flex-shrink: 0; opacity: 0; transition: opacity var(--duration-fast); }
.msg-item:hover .msg-delete { opacity: 1; }

@media (max-width: 767px) {
  .msg-page { padding: var(--space-md); }
  .msg-header { flex-wrap: wrap; }
}
</style>
