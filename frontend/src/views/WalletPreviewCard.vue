<template>
  <article class="bento-card flex flex-col justify-between">
    <div>
      <h3 class="font-label-md text-label-md text-on-surface-variant mb-2">我的钱包</h3>

      <!-- Loading -->
      <div v-if="loading" class="space-y-2">
        <div class="skeleton-pulse h-8 w-28 rounded" />
        <div class="skeleton-pulse h-4 w-20 rounded" />
      </div>

      <!-- Empty -->
      <div v-else-if="!wallet" class="text-center py-2">
        <p class="font-label-sm text-label-sm text-on-surface-variant">钱包未开通</p>
      </div>

      <!-- Balance -->
      <div v-else>
        <div class="font-headline-md text-headline-md text-on-surface">
          ¥{{ formatBalance(wallet.balance) }}
        </div>
        <p class="font-label-sm text-label-sm text-on-surface-variant mt-0.5">可用余额</p>
      </div>
    </div>

    <!-- Quick action: recharge hint -->
    <p v-if="wallet && wallet.balance < 50" class="font-label-sm text-label-sm text-error mt-2">
      余额较低，可联系家属充值
    </p>
  </article>
</template>

<script setup>
defineProps({
  wallet: { type: Object, default: null },
  loading: { type: Boolean, default: false },
})

function formatBalance(val) {
  if (val == null) return '0.00'
  return Number(val).toFixed(2)
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
