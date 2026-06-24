<template>
  <router-view v-slot="{ Component, route }">
    <transition name="page" mode="out-in">
      <component :is="Component" :key="route.path" />
    </transition>
  </router-view>
</template>

<script setup>
// 智伴智慧社区养老 — 温暖可信 · 亲和关怀 · 安全可靠
</script>
