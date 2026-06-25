<template>
  <div class="max-w-7xl mx-auto px-container-padding-mobile md:px-container-padding-desktop py-6 md:py-8">
    <!-- ========== Page Header ========== -->
    <div class="mb-6 md:mb-8">
      <h1 class="font-headline-lg text-headline-lg text-on-surface">
        {{ greeting }}
      </h1>
      <p class="font-body-md text-body-md text-on-surface-variant mt-1">
        {{ todayDate }}
      </p>
    </div>

    <!-- ========== Loading State ========== -->
    <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-bento-gap auto-rows-[180px]">
      <div v-for="n in 6" :key="n" class="skeleton-pulse rounded-xl" :class="n === 1 ? 'md:col-span-2 md:row-span-2' : ''" />
    </div>

    <!-- ========== Error State ========== -->
    <div v-else-if="loadError" class="flex flex-col items-center justify-center py-20 text-center">
      <span class="material-symbols-outlined text-on-surface-variant mb-4" style="font-size: 48px;">error_outline</span>
      <p class="font-body-lg text-body-lg text-on-surface mb-2">数据加载失败</p>
      <p class="font-body-md text-body-md text-on-surface-variant mb-6">{{ loadError }}</p>
      <button
        class="bg-primary text-on-primary font-label-md text-label-md px-6 py-3 rounded-md touch-target transition-ui hover:bg-primary-hover"
        @click="fetchAll"
      >
        重新加载
      </button>
    </div>

    <!-- ========== Bento Grid ========== -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-bento-gap auto-rows-[minmax(160px,auto)]">
      <!-- 1. Health Summary — spans 2 cols, 2 rows on desktop -->
      <HealthSummaryCard
        class="lg:col-span-2 lg:row-span-2"
        :health-data="healthData"
        :loading="healthLoading"
        @view-detail="$router.push('/elder/health')"
      />

      <!-- 2. SOS Emergency — always visible -->
      <EmergencyActionCard
        class="lg:col-span-1 lg:row-span-1"
        @sos="triggerSOS"
        :submitting="sosSubmitting"
      />

      <!-- 3. Wallet Balance -->
      <WalletPreviewCard
        class="lg:col-span-1 lg:row-span-1"
        :wallet="walletData"
        :loading="walletLoading"
      />

      <!-- 4. Upcoming Activities -->
      <ActivityPreviewCard
        class="lg:col-span-1 lg:row-span-1"
        :activities="upcomingActivities"
        :loading="activityLoading"
        @view-all="$router.push('/elder/services?tab=activities')"
      />

      <!-- 5. Quick Services -->
      <ServicePreviewCard
        class="lg:col-span-1 lg:row-span-1"
        :services="featuredServices"
        :loading="serviceLoading"
        @view-all="$router.push('/elder/services')"
      />

      <!-- 6. Messages Preview -->
      <MessagePreviewCard
        class="lg:col-span-2 lg:row-span-1"
        :messages="recentMessages"
        :loading="messageLoading"
        @view-all="$router.push('/elder/messages')"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user.js'
import { healthAPI, emergencyAPI, walletAPI, activityAPI, serviceAPI, messageAPI } from '../api/index.js'
import HealthSummaryCard from './HealthSummaryCard.vue'
import EmergencyActionCard from './EmergencyActionCard.vue'
import WalletPreviewCard from './WalletPreviewCard.vue'
import ActivityPreviewCard from './ActivityPreviewCard.vue'
import ServicePreviewCard from './ServicePreviewCard.vue'
import MessagePreviewCard from './MessagePreviewCard.vue'

const router = useRouter()
const userStore = useUserStore()

// ============ State ============
const loading = ref(true)
const loadError = ref('')
const healthLoading = ref(false)
const walletLoading = ref(false)
const activityLoading = ref(false)
const serviceLoading = ref(false)
const messageLoading = ref(false)
const sosSubmitting = ref(false)

const healthData = ref(null)
const walletData = ref(null)
const upcomingActivities = ref([])
const featuredServices = ref([])
const recentMessages = ref([])

// ============ Computed ============
const greeting = computed(() => {
  const hour = new Date().getHours()
  const name = userStore.name || '用户'
  if (hour < 9) return `早上好，${name}`
  if (hour < 12) return `上午好，${name}`
  if (hour < 14) return `中午好，${name}`
  if (hour < 18) return `下午好，${name}`
  return `晚上好，${name}`
})

const todayDate = computed(() => {
  const now = new Date()
  const weekMap = ['日', '一', '二', '三', '四', '五', '六']
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 星期${weekMap[now.getDay()]}`
})

// ============ Data Fetching ============
async function fetchAll() {
  loading.value = true
  loadError.value = ''

  const userId = userStore.userId
  if (!userId) {
    loadError.value = '用户信息未加载，请重新登录'
    loading.value = false
    return
  }

  try {
    // 并行拉取所有数据
    await Promise.allSettled([
      fetchHealth(userId),
      fetchWallet(userId),
      fetchActivities(userId),
      fetchServices(),
      fetchMessages(userId),
    ])
    loading.value = false
  } catch {
    loadError.value = '数据加载失败，请检查网络后重试'
    loading.value = false
  }
}

async function fetchHealth(userId) {
  healthLoading.value = true
  try {
    const res = await healthAPI.getLatest(userId)
    healthData.value = res.data
  } catch {
    healthData.value = null
  } finally {
    healthLoading.value = false
  }
}

async function fetchWallet(userId) {
  walletLoading.value = true
  try {
    const res = await walletAPI.getWallet(userId)
    walletData.value = res.data
  } catch {
    walletData.value = null
  } finally {
    walletLoading.value = false
  }
}

async function fetchActivities(userId) {
  activityLoading.value = true
  try {
    const [upcomingRes] = await Promise.all([
      activityAPI.getUpcoming(),
    ])
    upcomingActivities.value = (upcomingRes.data || []).slice(0, 3)
  } catch {
    upcomingActivities.value = []
  } finally {
    activityLoading.value = false
  }
}

async function fetchServices() {
  serviceLoading.value = true
  try {
    const res = await serviceAPI.getAll()
    featuredServices.value = (res.data || []).slice(0, 4)
  } catch {
    featuredServices.value = []
  } finally {
    serviceLoading.value = false
  }
}

async function fetchMessages(userId) {
  messageLoading.value = true
  try {
    const res = await messageAPI.getUnread(userId)
    recentMessages.value = (res.data || []).slice(0, 3)
  } catch {
    recentMessages.value = []
  } finally {
    messageLoading.value = false
  }
}

// ============ SOS ============
async function triggerSOS() {
  sosSubmitting.value = true
  try {
    await emergencyAPI.create({
      userId: userStore.userId,
      callType: '紧急求助',
      description: '老人请求紧急帮助',
    })
    alert('紧急呼叫已发出，社区工作人员将尽快响应！')
  } catch (e) {
    alert(e?.message || '呼叫失败，请直接拨打120或联系社区')
  } finally {
    sosSubmitting.value = false
  }
}

// ============ Init ============
onMounted(() => {
  fetchAll()
})
</script>
