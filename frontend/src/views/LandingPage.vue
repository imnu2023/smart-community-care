<template>
  <div class="bg-background text-on-background antialiased selection:bg-primary-container selection:text-on-primary-container min-h-screen flex flex-col">
    <LandingHeader />

    <main class="flex-grow pt-28 pb-20">
      <HeroSection />

      <!-- Bento UI Grid -->
      <section id="dashboard" class="max-w-7xl mx-auto px-container-padding-mobile md:px-container-padding-desktop py-12 scroll-mt-24">
        <h2 class="font-headline-md text-headline-md text-on-surface mb-8">Live Dashboard Overview</h2>
        <div class="grid grid-cols-1 md:grid-cols-4 gap-bento-gap auto-rows-[200px]">
          <HealthMonitorCard />
          <EmergencySOSCard />
          <FamilyPayCard />
          <SmartDevicesCard />
        </div>
      </section>
    </main>

    <LandingFooter />
  </div>
</template>

<script setup>
import LandingHeader from '../components/landing/LandingHeader.vue'
import HeroSection from '../components/landing/HeroSection.vue'
import HealthMonitorCard from '../components/landing/HealthMonitorCard.vue'
import EmergencySOSCard from '../components/landing/EmergencySOSCard.vue'
import FamilyPayCard from '../components/landing/FamilyPayCard.vue'
import SmartDevicesCard from '../components/landing/SmartDevicesCard.vue'
import LandingFooter from '../components/landing/LandingFooter.vue'
</script>

<style scoped>
/* 多子组件共用的样式保留在父组件 */
.bento-card {
  background: theme('colors.surface-container-lowest');
  border-radius: theme('borderRadius.24');
  box-shadow: theme('boxShadow.soft-care');
  transition: all 0.3s ease-in-out;
  padding: 24px;
}
.bento-card:hover {
  box-shadow: theme('boxShadow.soft-care-hover');
  transform: scale(1.01);
}
.pulse-dot {
  animation: pulse 1.5s infinite;
}
@keyframes pulse {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(186, 26, 26, 0.7);
  }
  70% {
    transform: scale(1);
    box-shadow: 0 0 0 10px rgba(186, 26, 26, 0);
  }
  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(186, 26, 26, 0);
  }
}
</style>
