<template>
  <div class="bento-card md:row-span-2 flex flex-col items-center justify-center text-center bg-secondary-fixed/30 border border-secondary/10">
    <h3 class="font-label-sm text-label-sm text-on-secondary-container mb-8 w-full text-left">Emergency Assistance</h3>
    <button class="relative w-32 h-32 rounded-full bg-secondary-container flex items-center justify-center text-on-secondary shadow-lg shadow-secondary-container/40 hover:scale-105 transition-transform">
      <div class="ripple"></div>
      <span class="material-symbols-outlined text-[48px]" style="font-variation-settings: 'FILL' 1;">sos</span>
    </button>
    <p class="font-label-md text-label-md text-on-secondary-container mt-6">Press &amp; Hold for 3s</p>
  </div>
</template>

<script setup>
// 紧急呼救卡片 — 纯展示
</script>

<style scoped>
.ripple {
  position: absolute;
  border-radius: 50%;
  transform: scale(0);
  animation: ripple-effect 2s linear infinite;
  background-color: rgba(255, 125, 84, 0.3);
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
}
@keyframes ripple-effect {
  to {
    transform: scale(1.5);
    opacity: 0;
  }
}
</style>
