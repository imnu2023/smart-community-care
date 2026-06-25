<template>
  <article class="bento-card flex flex-col justify-between">
    <div>
      <div class="flex justify-between items-center mb-3">
        <h3 class="font-label-md text-label-md text-on-surface-variant">最新消息</h3>
        <span class="material-symbols-outlined text-on-surface-variant text-xl">notifications</span>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="space-y-3">
        <div v-for="i in 3" :key="i" class="flex gap-3">
          <div class="skeleton-pulse w-2 h-2 rounded-full mt-2 flex-shrink-0" />
          <div class="flex-1 space-y-1.5">
            <div class="skeleton-pulse h-4 w-full rounded" />
            <div class="skeleton-pulse h-3 w-1/3 rounded" />
          </div>
        </div>
      </div>

      <!-- Empty -->
      <div v-else-if="messages.length === 0" class="text-center py-6">
        <span class="material-symbols-outlined text-on-surface-variant text-3xl mb-2">mark_email_unread</span>
        <p class="font-label-md text-label-md text-on-surface">暂无新消息</p>
        <p class="font-label-sm text-label-sm text-on-surface-variant mt-0.5">有新的通知时会出现在这里</p>
      </div>

      <!-- List -->
      <ul v-else class="space-y-3">
        <li
          v-for="msg in messages"
          :key="msg.id"
          class="flex items-start gap-3"
        >
          <!-- Type icon -->
          <div class="w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5" :class="msgTypeClass(msg.type)">
            <span class="material-symbols-outlined text-base">{{ msgTypeIcon(msg.type) }}</span>
          </div>
          <div class="min-w-0 flex-1">
            <p class="font-label-md text-label-md text-on-surface line-clamp-2">{{ msg.content }}</p>
            <p class="font-label-sm text-label-sm text-on-surface-variant mt-0.5">{{ timeAgo(msg.createdAt) }}</p>
          </div>
          <!-- Unread dot -->
          <div v-if="!msg.isRead" class="w-2.5 h-2.5 rounded-full bg-primary flex-shrink-0 mt-2" />
        </li>
      </ul>
    </div>

    <button
      class="flex items-center gap-1 font-label-sm text-label-sm text-primary mt-3 hover:underline cursor-pointer bg-transparent border-0 p-0"
      @click="$emit('viewAll')"
    >
      查看全部消息
      <span class="material-symbols-outlined text-sm">arrow_forward</span>
    </button>
  </article>
</template>

<script setup>
defineProps({
  messages: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})

defineEmits(['viewAll'])

function msgTypeIcon(type) {
  const map = {
    activity: 'event',
    payment: 'payments',
    health: 'favorite',
    order: 'receipt_long',
    emergency: 'warning',
  }
  return map[type] || 'notifications'
}

function msgTypeClass(type) {
  const map = {
    activity: 'bg-primary-fixed text-primary',
    payment: 'bg-secondary-fixed text-secondary',
    health: 'bg-tertiary-fixed text-tertiary',
    order: 'bg-surface-container-high text-on-surface-variant',
    emergency: 'bg-error-container text-error',
  }
  return map[type] || 'bg-surface-container-high text-on-surface-variant'
}

function timeAgo(dateArr) {
  if (!dateArr) return ''
  // dateArr is [year, month, day, hour, minute, second]
  const d = new Date(dateArr[0], dateArr[1] - 1, dateArr[2], dateArr[3], dateArr[4], dateArr[5] || 0)
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
</script>

<style scoped>
.bento-card {
  background: theme('colors.surface-container-lowest');
  border: 1px solid theme('colors.outline-variant');
  border-radius: theme('borderRadius.xl');
  box-shadow: theme('boxShadow.soft-care');
  padding: 24px;
  transition: all 200ms ease-out;
}
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
