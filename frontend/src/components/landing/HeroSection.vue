<template>
  <section id="hero" class="max-w-7xl mx-auto px-container-padding-mobile md:px-container-padding-desktop py-16 md:py-24 grid md:grid-cols-2 gap-12 items-center scroll-mt-24">
    <div class="space-y-8">
      <div class="inline-flex items-center gap-2 bg-primary-fixed/30 text-primary px-4 py-2 rounded-full font-label-sm text-label-sm">
        <span class="w-2 h-2 rounded-full bg-primary pulse-dot"></span>
        Smart Community Hub Active
      </div>
      <h1 class="font-display-lg text-display-lg text-on-surface">跨越距离的守护，<br/><span class="text-primary">全天候智慧中枢</span></h1>
      <p class="font-body-lg text-body-lg text-on-surface-variant max-w-lg">Connecting IoT health tracking with community services and family care. A seamless, warm, and reliable ecosystem designed for the modern senior living experience.</p>
      <div class="flex flex-wrap gap-4 pt-4">
        <button
          class="bg-primary text-on-primary px-8 py-4 rounded-full font-label-md text-label-md min-h-[56px] hover:bg-primary/90 transition-colors shadow-lg shadow-primary/20 flex items-center gap-2"
          @click="scrollToDashboard"
        >
          Experience Demo
        </button>
        <button class="bg-surface-container-highest text-on-surface px-8 py-4 rounded-full font-label-md text-label-md min-h-[56px] hover:bg-surface-variant transition-colors flex items-center gap-2" @click="handleWatchVideo">
          <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">play_circle</span>
          Watch Video
        </button>
      </div>
    </div>
    <!-- Hero Visual -->
    <div class="relative h-[400px] md:h-[500px] w-full rounded-24 bg-gradient-to-br from-primary-fixed to-surface-container overflow-hidden shadow-soft-care">
      <!-- Floating Card 1 -->
      <div class="absolute top-10 right-10 bg-surface/90 backdrop-blur-md p-4 rounded-xl shadow-lg w-48 animate-[bounce_4s_infinite]">
        <div class="flex items-center gap-3 mb-2">
          <span class="material-symbols-outlined text-primary">favorite</span>
          <span class="font-label-sm text-label-sm text-on-surface-variant">Heart Rate</span>
        </div>
        <div class="font-headline-lg text-headline-lg text-on-surface">75 <span class="font-label-sm text-label-sm text-on-surface-variant">BPM</span></div>
      </div>
      <!-- Floating Card 2 -->
      <div class="absolute bottom-20 left-10 bg-surface/90 backdrop-blur-md p-4 rounded-xl shadow-lg w-56 animate-[bounce_5s_infinite_0.5s]">
        <div class="flex items-center gap-3 mb-2">
          <span class="material-symbols-outlined text-tertiary">shield</span>
          <span class="font-label-sm text-label-sm text-on-surface-variant">Secure Pay</span>
        </div>
        <div class="font-label-md text-label-md text-on-surface">Family Proxy Active</div>
      </div>
      <!-- Floating Card 3 -->
      <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-surface/90 backdrop-blur-md p-6 rounded-24 shadow-soft-care-hover animate-[pulse_3s_infinite_1s]">
        <span class="material-symbols-outlined text-secondary-container" style="font-size: 64px; font-variation-settings: 'FILL' 1;">lightbulb</span>
      </div>
    </div>
  </section>
</template>

<script setup>
function scrollToDashboard() {
  document.getElementById('dashboard')?.scrollIntoView({ behavior: 'smooth' })
}

function handleWatchVideo() {
  // 视频播放 → 待后续对接真实视频链接
  window.alert('演示视频即将上线，敬请期待！')
}
</script>
