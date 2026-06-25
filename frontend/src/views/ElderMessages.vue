<template>
  <div class="max-w-2xl mx-auto px-container-padding-mobile md:px-container-padding-desktop py-6 md:py-8">
    <!-- Back nav + actions -->
    <div class="flex items-center justify-between mb-6">
      <button class="flex items-center gap-1 font-label-md text-label-md text-on-surface-variant hover:text-on-surface transition-ui cursor-pointer bg-transparent border-0 p-0" @click="$router.push('/elder')">
        <span class="material-symbols-outlined text-lg">arrow_back</span>
        返回首页
      </button>
      <button
        v-if="messages.length > 0 && hasUnread"
        class="font-label-sm text-label-sm text-primary hover:underline cursor-pointer bg-transparent border-0"
        @click="markAllRead"
        :disabled="markingAll"
      >
        {{ markingAll ? '标记中...' : '全部已读' }}
      </button>
    </div>

    <h1 class="font-headline-lg text-headline-lg text-on-surface mb-6">消息中心</h1>

    <!-- ========== Loading ========== -->
    <div v-if="loading" class="space-y-3">
      <div v-for="i in 5" :key="i" class="skeleton-pulse h-20 rounded-xl" />
    </div>

    <!-- ========== Error ========== -->
    <div v-else-if="loadError" class="text-center py-16">
      <span class="material-symbols-outlined text-4xl text-on-surface-variant mb-3">error_outline</span>
      <p class="font-body-lg text-body-lg text-on-surface mb-2">加载失败</p>
      <button class="bg-primary text-on-primary font-label-md text-label-md px-6 py-3 rounded-md touch-target transition-ui hover:bg-primary-hover" @click="fetchMessages">重试</button>
    </div>

    <!-- ========== Empty ========== -->
    <div v-else-if="messages.length === 0" class="text-center py-20">
      <span class="material-symbols-outlined text-5xl text-on-surface-variant mb-4">mark_email_unread</span>
      <p class="font-body-lg text-body-lg text-on-surface mb-2">暂无消息</p>
      <p class="font-body-md text-body-md text-on-surface-variant">
        健康提醒、活动通知、支付消息会出现在这里
      </p>
    </div>

    <!-- ========== Message List ========== -->
    <div v-else class="space-y-2">
      <div
        v-for="msg in messages"
        :key="msg.id"
        class="bg-surface-container-lowest rounded-xl border border-outline-variant p-4 transition-ui hover:shadow-soft-care-hover cursor-pointer"
        :class="{ 'border-l-[3px] border-l-primary': !msg.isRead }"
        @click="markRead(msg)"
        role="button"
        tabindex="0"
        @keydown.enter="markRead(msg)"
      >
        <div class="flex items-start gap-3">
          <!-- Type icon -->
          <div
            class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0"
            :class="msgTypeClass(msg.type)"
          >
            <span class="material-symbols-outlined text-lg">{{ msgTypeIcon(msg.type) }}</span>
          </div>

          <div class="flex-1 min-w-0">
            <div class="flex justify-between items-start gap-2">
              <p class="font-label-md text-label-md text-on-surface" :class="{ 'font-semibold': !msg.isRead }">
                {{ typeLabel(msg.type) }}
              </p>
              <span class="font-label-sm text-label-sm text-on-surface-variant whitespace-nowrap flex-shrink-0">{{ timeText(msg.createdAt) }}</span>
            </div>
            <p class="font-body-md text-body-md text-on-surface-variant mt-1 line-clamp-3">{{ msg.content }}</p>
          </div>

          <!-- Unread dot -->
          <div v-if="!msg.isRead" class="w-2.5 h-2.5 rounded-full bg-primary flex-shrink-0 mt-2" />
        </div>

        <!-- Delete -->
        <div class="flex justify-end mt-2">
          <button
            class="font-label-sm text-label-sm text-on-surface-variant hover:text-error transition-ui cursor-pointer bg-transparent border-0 p-0"
            @click.stop="deleteMsg(msg.id)"
          >
            删除
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user.js'
import { messageAPI } from '../api/index.js'

const userStore = useUserStore()

// ============ State ============
const loading = ref(true)
const loadError = ref('')
const messages = ref([])
const markingAll = ref(false)

// ============ Computed ============
const hasUnread = computed(() => messages.value.some(m => !m.isRead))

// ============ Fetch ============
async function fetchMessages() {
  loading.value = true
  loadError.value = ''
  try {
    const res = await messageAPI.getList(userStore.userId)
    messages.value = res.data || []
    loading.value = false
  } catch {
    loadError.value = '加载失败，请检查网络'
    loading.value = false
  }
}

// ============ Actions ============
async function markRead(msg) {
  if (msg.isRead) return
  try {
    await messageAPI.markAsRead(msg.id)
    msg.isRead = true
  } catch {
    // 静默失败
  }
}

async function markAllRead() {
  markingAll.value = true
  try {
    await messageAPI.markAllAsRead(userStore.userId)
    messages.value.forEach(m => { m.isRead = true })
  } catch {
    alert('操作失败，请稍后重试')
  } finally {
    markingAll.value = false
  }
}

async function deleteMsg(id) {
  try {
    await messageAPI.delete(id)
    messages.value = messages.value.filter(m => m.id !== id)
  } catch {
    alert('删除失败')
  }
}

// ============ Helpers ============
function msgTypeIcon(type) {
  const map = { activity: 'event', payment: 'payments', health: 'favorite', order: 'receipt_long', emergency: 'warning' }
  return map[type] || 'notifications'
}

function msgTypeClass(type) {
  const map = { activity: 'bg-primary-fixed text-primary', payment: 'bg-secondary-fixed text-secondary', health: 'bg-tertiary-fixed text-tertiary', order: 'bg-surface-container-high text-on-surface-variant', emergency: 'bg-error-container text-error' }
  return map[type] || 'bg-surface-container-high text-on-surface-variant'
}

function typeLabel(type) {
  const map = { activity: '活动通知', payment: '支付通知', health: '健康提醒', order: '订单通知', emergency: '紧急通知' }
  return map[type] || '系统通知'
}

function timeText(dateArr) {
  if (!dateArr) return ''
  const d = new Date(dateArr[0], dateArr[1] - 1, dateArr[2], dateArr[3], dateArr[4] || 0, dateArr[5] || 0)
  const now = new Date()
  const diff = now - d
  const mins = Math.floor(diff / 60000)
  if (mins < 1) return '刚刚'
  if (mins < 60) return `${mins}分钟前`
  const hours = Math.floor(mins / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}天前`
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

onMounted(fetchMessages)
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
