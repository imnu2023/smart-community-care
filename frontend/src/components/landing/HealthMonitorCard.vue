<template>
  <div class="bento-card md:col-span-2 row-span-1 md:row-span-2 flex flex-col justify-between relative overflow-hidden">
    <div class="flex justify-between items-start">
      <div>
        <h3 class="font-label-sm text-label-sm text-on-surface-variant mb-1">Health Monitoring</h3>
        <div class="font-headline-lg text-headline-lg text-on-surface">Stable</div>
      </div>
      <div class="flex items-center gap-2 bg-error-container/50 px-3 py-1 rounded-full">
        <div class="w-2.5 h-2.5 rounded-full bg-error pulse-dot"></div>
        <span class="font-label-sm text-label-sm text-on-error-container">Mild Warning</span>
      </div>
    </div>
    <div class="flex gap-6 mt-4 z-10">
      <div>
        <div class="font-label-sm text-label-sm text-on-surface-variant">Heart Rate</div>
        <div class="font-headline-md text-headline-md text-primary">75 <span class="text-sm font-normal">bpm</span></div>
      </div>
      <div>
        <div class="font-label-sm text-label-sm text-on-surface-variant">Blood Pressure</div>
        <div class="font-headline-md text-headline-md text-primary">130/85 <span class="text-sm font-normal">mmHg</span></div>
      </div>
    </div>
    <div class="absolute bottom-0 left-0 w-full h-1/2 opacity-20 pointer-events-none" style="background: linear-gradient(180deg, transparent 0%, theme('colors.primary-fixed') 100%); clip-path: polygon(0 100%, 0 60%, 20% 50%, 40% 70%, 60% 40%, 80% 80%, 100% 30%, 100% 100%);"></div>
  </div>
</template>

<script setup>
// 健康监测卡片 — 纯展示
</script>
