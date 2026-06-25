<template>
  <article class="bento-card flex flex-col justify-between">
    <div>
      <div class="flex justify-between items-center mb-3">
        <h3 class="font-label-md text-label-md text-on-surface-variant">社区服务</h3>
        <span class="material-symbols-outlined text-on-surface-variant text-xl">home_repair_service</span>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="grid grid-cols-2 gap-2">
        <div v-for="i in 4" :key="i" class="skeleton-pulse h-16 rounded-lg" />
      </div>

      <!-- Empty -->
      <div v-else-if="services.length === 0" class="text-center py-4">
        <span class="material-symbols-outlined text-on-surface-variant text-2xl mb-1">storefront</span>
        <p class="font-label-sm text-label-sm text-on-surface-variant">暂无可用服务</p>
      </div>

      <!-- Grid -->
      <div v-else class="grid grid-cols-2 gap-2">
        <div
          v-for="svc in services"
          :key="svc.id"
          class="flex flex-col items-center gap-1 p-3 rounded-lg bg-surface-container hover:bg-surface-container-high transition-ui cursor-pointer"
        >
          <span class="material-symbols-outlined text-primary text-xl">{{ serviceIcon(svc.serviceType) }}</span>
          <span class="font-label-sm text-label-sm text-on-surface text-center truncate w-full">{{ svc.serviceName }}</span>
        </div>
      </div>
    </div>

    <button
      v-if="services.length > 0"
      class="flex items-center gap-1 font-label-sm text-label-sm text-primary mt-3 hover:underline cursor-pointer bg-transparent border-0 p-0"
      @click="$emit('viewAll')"
    >
      查看全部服务
      <span class="material-symbols-outlined text-sm">arrow_forward</span>
    </button>
  </article>
</template>

<script setup>
defineProps({
  services: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})

defineEmits(['viewAll'])

function serviceIcon(type) {
  const map = {
    housekeeping: 'cleaning_services',
    medical: 'medical_services',
    life: 'handyman',
    food: 'restaurant',
    beauty: 'content_cut',
    health: 'spa',
    shopping: 'shopping_bag',
    companion: 'diversity_3',
  }
  return map[type] || 'miscellaneous_services'
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
