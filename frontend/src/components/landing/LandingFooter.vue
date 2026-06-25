<template>
  <footer class="bg-surface-container-low dark:bg-surface-container-lowest w-full py-12">
    <div class="flex flex-col md:flex-row justify-between items-center px-container-padding-mobile md:px-container-padding-desktop gap-gutter max-w-7xl mx-auto">
      <div class="flex flex-col items-center md:items-start gap-4">
        <div class="font-headline-md text-headline-md font-bold text-primary">智伴</div>
        <p class="font-label-sm text-label-sm text-on-surface-variant text-center md:text-left">
          © 2024 智伴 Smart Companion. All rights reserved.
        </p>
        <div class="flex items-center gap-2 text-tertiary font-label-sm text-label-sm">
          <span class="w-2 h-2 rounded-full bg-tertiary"></span>
          System Status: All Systems Operational.
        </div>
      </div>
      <nav class="flex gap-6 mt-6 md:mt-0">
        <a class="font-label-sm text-label-sm text-on-surface-variant hover:text-secondary transition-colors duration-300 cursor-pointer" @click.prevent="handleLink('privacy')">Privacy Policy</a>
        <a class="font-label-sm text-label-sm text-on-surface-variant hover:text-secondary transition-colors duration-300 cursor-pointer" @click.prevent="handleLink('terms')">Terms of Service</a>
        <a class="font-label-sm text-label-sm text-on-surface-variant hover:text-secondary transition-colors duration-300 cursor-pointer" @click.prevent="handleLink('support')">Contact Support</a>
      </nav>
    </div>
  </footer>
</template>

<script setup>
function handleLink(type) {
  const msg = {
    privacy: '隐私政策页面建设中',
    terms: '服务条款页面建设中',
    support: '请联系客服: support@zhiban.com'
  }
  window.alert(msg[type] || '页面建设中')
}
</script>
