<template>
  <article class="bento-card flex flex-col justify-between">
    <div>
      <div class="flex justify-between items-center mb-3">
        <h3 class="font-label-md text-label-md text-on-surface-variant">近期活动</h3>
        <span class="material-symbols-outlined text-on-surface-variant text-xl">event</span>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="space-y-3">
        <div v-for="i in 2" :key="i" class="flex gap-3">
          <div class="skeleton-pulse w-10 h-10 rounded-lg flex-shrink-0" />
          <div class="flex-1 space-y-1.5">
            <div class="skeleton-pulse h-4 w-full rounded" />
            <div class="skeleton-pulse h-3 w-2/3 rounded" />
          </div>
        </div>
      </div>

      <!-- Empty -->
      <div v-else-if="activities.length === 0" class="text-center py-4">
        <span class="material-symbols-outlined text-on-surface-variant text-2xl mb-1">celebration</span>
        <p class="font-label-sm text-label-sm text-on-surface-variant">暂无近期活动</p>
      </div>

      <!-- List -->
      <ul v-else class="space-y-3">
        <li v-for="act in activities" :key="act.id" class="flex items-start gap-3">
          <div class="w-10 h-10 rounded-lg flex items-center justify-center flex-shrink-0" :class="typeBadgeClass(act.type)">
            <span class="material-symbols-outlined text-lg">{{ typeIcon(act.type) }}</span>
          </div>
          <div class="min-w-0 flex-1">
            <p class="font-label-md text-label-md text-on-surface truncate">{{ act.title }}</p>
            <p class="font-label-sm text-label-sm text-on-surface-variant">
              {{ formatDate(act.startTime) }}
            </p>
          </div>
        </li>
      </ul>
    </div>

    <button
      v-if="activities.length > 0"
      class="flex items-center gap-1 font-label-sm text-label-sm text-primary mt-3 hover:underline cursor-pointer bg-transparent border-0 p-0"
      @click="$emit('viewAll')"
    >
      查看全部
      <span class="material-symbols-outlined text-sm">arrow_forward</span>
    </button>
  </article>
</template>

<script setup>
defineProps({
  activities: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})

defineEmits(['viewAll'])

function typeIcon(type) {
  const map = { culture: 'brush', sports: 'fitness_center', study: 'school', health: 'spa', social: 'groups' }
  return map[type] || 'event'
}

function typeBadgeClass(type) {
  const map = {
    culture: 'bg-primary-fixed text-primary',
    sports: 'bg-tertiary-fixed text-tertiary',
    study: 'bg-secondary-fixed text-secondary',
    health: 'bg-tertiary-fixed text-tertiary',
    social: 'bg-primary-fixed text-primary',
  }
  return map[type] || 'bg-surface-container-high text-on-surface-variant'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}月${d.getDate()}日 ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
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
</style>
