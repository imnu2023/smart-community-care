<template>
  <div class="notification-center">
    <div class="page-header">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5m0 0l7-7m-7 7l7 7"/>
        </svg>
        <span>返回</span>
      </button>
      <h1>消息中心</h1>
      <button class="mark-all-read" @click="markAllRead" v-if="unreadCount > 0">
        全部已读 ({{ unreadCount }})
      </button>
    </div>

    <div class="notification-list" v-if="notifications.length > 0">
      <div
        v-for="msg in notifications"
        :key="msg.id"
        :class="['notification-card', { unread: !msg.isRead }]"
        @click="readMessage(msg)"
      >
        <div class="card-icon" :class="typeClass(msg.type)">
          {{ typeIcon(msg.type) }}
        </div>
        <div class="card-body">
          <div class="card-title">
            <span class="card-type">{{ typeLabel(msg.type) }}</span>
            <span class="card-time">{{ formatTime(msg.createdAt) }}</span>
          </div>
          <div class="card-content">{{ msg.content }}</div>
        </div>
        <button class="card-delete" @click.stop="deleteMessage(msg.id)" title="删除">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
            <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>
    </div>

    <div class="empty-state" v-else>
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
        <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
      </svg>
      <p>暂无通知消息</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { messageAPI } from '../api'

const router = useRouter()

const goBack = () => router.push('/dashboard')

const notifications = ref([])
let pollTimer = null

const currentUserId = computed(() => parseInt(localStorage.getItem('userId')) || 0)

const unreadCount = computed(() => notifications.value.filter(m => !m.isRead).length)

const typeConfig = {
  activity:  { label: '活动通知', icon: '📅', cls: 'type-activity' },
  payment:   { label: '支付通知', icon: '💰', cls: 'type-payment' },
  health:    { label: '健康提醒', icon: '❤️', cls: 'type-health' },
  order:     { label: '订单通知', icon: '📋', cls: 'type-order' },
  emergency: { label: '紧急通知', icon: '🚨', cls: 'type-emergency' }
}

const typeLabel = (type) => typeConfig[type]?.label || '系统通知'
const typeIcon = (type) => typeConfig[type]?.icon || '🔔'
const typeClass = (type) => typeConfig[type]?.cls || ''

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  // Java LocalDateTime 在 JSON 中序列化为数组 [2026,6,17,15,11,3]
  let date
  if (Array.isArray(dateStr)) {
    date = new Date(dateStr[0], dateStr[1] - 1, dateStr[2], dateStr[3] || 0, dateStr[4] || 0, dateStr[5] || 0)
  } else if (typeof dateStr === 'string') {
    date = new Date(dateStr.replace('T', ' ').replace('Z', ''))
  } else {
    return ''
  }
  if (isNaN(date.getTime())) return ''
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 86400000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return date.toLocaleDateString('zh-CN')
}

const loadNotifications = async () => {
  if (!currentUserId.value) return
  try {
    const res = await messageAPI.getList(currentUserId.value)
    if (res?.data) notifications.value = res.data
  } catch (e) {
    console.error('加载通知失败:', e)
  }
}

const readMessage = async (msg) => {
  if (!msg.isRead) {
    try {
      await messageAPI.markAsRead(msg.id)
      msg.isRead = true
    } catch (e) { console.error(e) }
  }
}

const deleteMessage = async (id) => {
  try {
    await messageAPI.delete(id)
    notifications.value = notifications.value.filter(m => m.id !== id)
  } catch (e) { console.error(e) }
}

const markAllRead = async () => {
  try {
    await messageAPI.markAllAsRead(currentUserId.value)
    notifications.value.forEach(m => { m.isRead = true })
  } catch (e) { console.error(e) }
}

const poll = async () => {
  if (!currentUserId.value) return
  try {
    const res = await messageAPI.countUnread(currentUserId.value)
    const count = res?.data?.count || 0
    if (count !== unreadCount.value) await loadNotifications()
  } catch (e) { /* silent */ }
}

onMounted(() => {
  loadNotifications()
  pollTimer = setInterval(poll, 5000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped>
.notification-center {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  max-width: 720px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.back-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.back-btn svg { width: 18px; height: 18px; }

.page-header h1 {
  flex: 1;
  margin: 0;
  font-size: 24px;
  color: #1e293b;
  font-weight: 700;
}

.mark-all-read {
  padding: 8px 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 20px;
  color: white;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
  white-space: nowrap;
}

.mark-all-read:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102,126,234,0.4);
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification-card {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 16px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: all 0.25s;
  position: relative;
}

.notification-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); }

.notification-card.unread {
  background: linear-gradient(135deg, rgba(102,126,234,0.04) 0%, rgba(118,75,162,0.04) 100%);
  border-left: 3px solid #667eea;
  padding-left: 13px;
}

.card-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
  background: #f1f5f9;
}

.type-activity  { background: #e0f2fe; }
.type-payment   { background: #fef3c7; }
.type-health    { background: #fce7f3; }
.type-order     { background: #e0e7ff; }
.type-emergency { background: #fee2e2; }

.card-body { flex: 1; min-width: 0; }

.card-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.card-type {
  font-size: 13px;
  font-weight: 600;
  color: #667eea;
}

.card-time {
  font-size: 12px;
  color: #94a3b8;
  flex-shrink: 0;
  margin-left: 12px;
}

.card-content {
  font-size: 15px;
  color: #334155;
  line-height: 1.5;
  word-break: break-all;
}

.card-delete {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 28px;
  height: 28px;
  border: none;
  background: none;
  color: #94a3b8;
  cursor: pointer;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.2s;
}

.notification-card:hover .card-delete { opacity: 1; }
.card-delete:hover { color: #ef4444; background: #fef2f2; }

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #94a3b8;
}

.empty-state svg { width: 64px; height: 64px; margin-bottom: 16px; }
.empty-state p { font-size: 16px; }

@media (max-width: 768px) {
  .notification-center { padding: 16px; }
  .page-header h1 { font-size: 20px; }
}
</style>
