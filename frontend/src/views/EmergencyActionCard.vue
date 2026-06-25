<template>
  <article
    class="bento-card flex flex-col items-center justify-center text-center relative overflow-hidden"
    :class="{ 'opacity-70 pointer-events-none': submitting }"
  >
    <!-- Red pulse background -->
    <div class="absolute inset-0 bg-error/5 rounded-xl" />

    <!-- SOS Icon -->
    <button
      class="relative z-10 w-20 h-20 rounded-full bg-error text-on-error flex items-center justify-center shadow-lg hover:shadow-xl transition-ui transform hover:scale-105 active:scale-95 touch-target cursor-pointer border-0"
      :class="{ 'animate-pulse': !submitting }"
      :disabled="submitting"
      @click="$emit('sos')"
      aria-label="紧急呼叫"
    >
      <span v-if="submitting" class="material-symbols-outlined animate-spin" style="font-size: 36px;">progress_activity</span>
      <span v-else class="material-symbols-outlined" style="font-size: 36px;">warning</span>
    </button>

    <p class="relative z-10 font-label-md text-label-md text-error mt-3">
      {{ submitting ? '呼叫中...' : '一键紧急求助' }}
    </p>
    <p class="relative z-10 font-label-sm text-label-sm text-on-surface-variant mt-1">
      社区工作人员将立即响应
    </p>
  </article>
</template>

<script setup>
defineProps({
  submitting: { type: Boolean, default: false },
})

defineEmits(['sos'])
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
@keyframes spin {
  to { transform: rotate(360deg); }
}
.animate-spin {
  animation: spin 1s linear infinite;
}
</style>
