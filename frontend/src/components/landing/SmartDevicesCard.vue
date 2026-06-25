<template>
  <div class="bento-card flex flex-col justify-between">
    <div class="flex justify-between items-center mb-4">
      <h3 class="font-label-sm text-label-sm text-on-surface-variant">Smart Devices</h3>
      <span class="material-symbols-outlined text-on-surface-variant">home_iot_device</span>
    </div>
    <div class="space-y-4">
      <div class="flex justify-between items-center">
        <span class="font-label-md text-label-md text-on-surface flex items-center gap-2">
          <span class="material-symbols-outlined text-primary" style="font-variation-settings: 'FILL' 1;">light</span> Living Room
        </span>
        <div class="w-12 h-6 bg-primary rounded-full relative cursor-pointer">
          <div class="w-5 h-5 bg-white rounded-full absolute right-0.5 top-0.5 shadow-sm"></div>
        </div>
      </div>
      <div class="flex justify-between items-center">
        <span class="font-label-md text-label-md text-on-surface flex items-center gap-2">
          <span class="material-symbols-outlined text-on-surface-variant">ac_unit</span> Bedroom AC
        </span>
        <div class="w-12 h-6 bg-surface-container-highest rounded-full relative cursor-pointer">
          <div class="w-5 h-5 bg-white rounded-full absolute left-0.5 top-0.5 shadow-sm"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 智能设备卡片 — 纯展示
</script>
