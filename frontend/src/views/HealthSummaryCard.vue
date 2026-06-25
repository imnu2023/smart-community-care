<template>
  <article
    class="bento-card flex flex-col justify-between relative overflow-hidden cursor-pointer"
    :class="warningLevel > 0 ? 'border-error/20 bg-error-container/5' : ''"
    @click="$emit('viewDetail')"
    role="button"
    tabindex="0"
    aria-label="查看健康详情"
    @keydown.enter="$emit('viewDetail')"
    @keydown.space.prevent="$emit('viewDetail')"
  >
    <!-- Header -->
    <div class="flex justify-between items-start">
      <div>
        <h3 class="font-label-md text-label-md text-on-surface-variant mb-0.5">健康监测</h3>
        <p class="font-headline-md text-headline-md text-on-surface">
          {{ statusText }}
        </p>
      </div>
      <!-- Warning Badge -->
      <div
        v-if="warningLevel > 0"
        class="flex items-center gap-1.5 bg-error-container text-on-error-container font-label-sm text-label-sm px-3 py-1 rounded-full"
      >
        <span class="w-2 h-2 rounded-full bg-error animate-pulse" />
        需关注
      </div>
      <div
        v-else
        class="flex items-center gap-1.5 bg-tertiary-fixed/40 text-on-tertiary-fixed-variant font-label-sm text-label-sm px-3 py-1 rounded-full"
      >
        <span class="w-2 h-2 rounded-full bg-tertiary" />
        正常
      </div>
    </div>

    <!-- Loading Skeleton -->
    <div v-if="loading" class="flex gap-6 mt-4">
      <div v-for="i in 3" :key="i" class="flex-1">
        <div class="skeleton-pulse h-4 w-16 rounded mb-2" />
        <div class="skeleton-pulse h-8 w-20 rounded" />
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="!healthData" class="mt-4 text-center py-4">
      <span class="material-symbols-outlined text-on-surface-variant text-2xl mb-1">monitor_heart</span>
      <p class="font-label-sm text-label-sm text-on-surface-variant">暂无健康数据</p>
    </div>

    <!-- Metrics -->
    <div v-else class="flex gap-4 md:gap-6 mt-4 z-10 flex-wrap">
      <div class="metric-item">
        <div class="font-label-sm text-label-sm text-on-surface-variant flex items-center gap-1">
          <span class="material-symbols-outlined text-base text-error">favorite</span>
          心率
        </div>
        <div class="font-headline-md text-headline-md text-on-surface whitespace-nowrap">
          {{ healthData.heartRate ?? '--' }}
          <span class="font-label-sm text-label-sm text-on-surface-variant font-normal" v-if="healthData.heartRate">bpm</span>
        </div>
      </div>
      <div class="metric-item">
        <div class="font-label-sm text-label-sm text-on-surface-variant">血压</div>
        <div class="font-headline-md text-headline-md text-on-surface whitespace-nowrap">
          <template v-if="healthData.bloodPressureHigh && healthData.bloodPressureLow">
            {{ healthData.bloodPressureHigh }}/{{ healthData.bloodPressureLow }}
          </template>
          <template v-else>--</template>
          <span class="font-label-sm text-label-sm text-on-surface-variant font-normal" v-if="healthData.bloodPressureHigh">mmHg</span>
        </div>
      </div>
      <div class="metric-item">
        <div class="font-label-sm text-label-sm text-on-surface-variant">血糖</div>
        <div class="font-headline-md text-headline-md text-on-surface whitespace-nowrap">
          {{ healthData.bloodSugar ?? '--' }}
          <span class="font-label-sm text-label-sm text-on-surface-variant font-normal" v-if="healthData.bloodSugar">mmol/L</span>
        </div>
      </div>
    </div>

    <!-- Bottom decoration wave (only when data present) -->
    <div
      v-if="healthData && !loading"
      class="absolute bottom-0 left-0 w-full h-1/3 opacity-10 pointer-events-none"
      :style="{
        background: warningLevel > 0
          ? `linear-gradient(180deg, transparent 0%, ${errorColor} 100%)`
          : `linear-gradient(180deg, transparent 0%, ${tertiaryColor} 100%)`,
        clipPath: 'polygon(0 100%, 0 55%, 20% 45%, 40% 65%, 60% 35%, 80% 70%, 100% 25%, 100% 100%)'
      }"
    />

    <!-- Footer action -->
    <div class="flex justify-end mt-3 z-10">
      <span class="font-label-sm text-label-sm text-primary flex items-center gap-0.5">
        查看详情
        <span class="material-symbols-outlined text-sm">arrow_forward</span>
      </span>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  healthData: { type: Object, default: null },
  loading: { type: Boolean, default: false },
})

defineEmits(['viewDetail'])

const warningLevel = computed(() => props.healthData?.warningLevel ?? 0)

const errorColor = '#ba1a1a'
const tertiaryColor = '#006825'

const statusText = computed(() => {
  if (props.loading) return '加载中...'
  if (!props.healthData) return '暂无数据'
  if (warningLevel.value === 0) return '指标正常'
  if (warningLevel.value <= 2) return '轻微异常'
  return '需要关注'
})
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
.bento-card:hover {
  box-shadow: theme('boxShadow.soft-care-hover');
}
.bento-card:focus-visible {
  outline: 2px solid theme('colors.primary');
  outline-offset: 2px;
}
.metric-item {
  min-width: 80px;
}
</style>
