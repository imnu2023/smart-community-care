<template>
  <div class="max-w-5xl mx-auto px-container-padding-mobile md:px-container-padding-desktop py-6 md:py-8">
    <!-- Back nav -->
    <button class="flex items-center gap-1 font-label-md text-label-md text-on-surface-variant hover:text-on-surface transition-ui mb-6 cursor-pointer bg-transparent border-0 p-0" @click="$router.push('/elder')">
      <span class="material-symbols-outlined text-lg">arrow_back</span>
      返回首页
    </button>

    <h1 class="font-headline-lg text-headline-lg text-on-surface mb-6">社区服务</h1>

    <!-- ========== Tab Bar ========== -->
    <div class="flex gap-1 bg-surface-container rounded-lg p-1 mb-6 w-fit">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        class="font-label-md text-label-md px-5 py-2.5 rounded-md transition-ui touch-target cursor-pointer border-0"
        :class="activeTab === tab.key
          ? 'bg-surface-container-lowest text-on-surface shadow-soft-care'
          : 'text-on-surface-variant hover:text-on-surface bg-transparent'"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- ========== Loading ========== -->
    <div v-if="loading" class="space-y-4">
      <div v-for="i in 4" :key="i" class="skeleton-pulse h-24 rounded-xl" />
    </div>

    <!-- ========== Error ========== -->
    <div v-else-if="loadError" class="text-center py-16">
      <span class="material-symbols-outlined text-4xl text-on-surface-variant mb-3">error_outline</span>
      <p class="font-body-lg text-body-lg text-on-surface mb-2">加载失败</p>
      <button class="bg-primary text-on-primary font-label-md text-label-md px-6 py-3 rounded-md touch-target transition-ui hover:bg-primary-hover" @click="fetchData">重试</button>
    </div>

    <!-- ========== Service Catalog Tab ========== -->
    <template v-else-if="activeTab === 'services'">
      <!-- Type filter pills -->
      <div class="flex flex-wrap gap-2 mb-6">
        <button
          v-for="type in serviceTypes"
          :key="type.key"
          class="font-label-sm text-label-sm px-4 py-2 rounded-full transition-ui touch-target cursor-pointer border"
          :class="selectedType === type.key
            ? 'bg-primary text-on-primary border-primary'
            : 'bg-surface-container-lowest text-on-surface-variant border-outline-variant hover:border-primary'"
          @click="selectedType = selectedType === type.key ? '' : type.key"
        >
          <span class="material-symbols-outlined text-sm align-middle mr-1">{{ type.icon }}</span>
          {{ type.label }}
        </button>
      </div>

      <!-- Empty -->
      <div v-if="filteredServices.length === 0" class="text-center py-16">
        <span class="material-symbols-outlined text-4xl text-on-surface-variant mb-3">storefront</span>
        <p class="font-body-md text-body-md text-on-surface mb-1">暂无此类服务</p>
        <p class="font-label-md text-label-md text-on-surface-variant">试试其他分类，或联系管理员添加服务</p>
      </div>

      <!-- Service Grid -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div
          v-for="svc in filteredServices"
          :key="svc.id"
          class="bg-surface-container-lowest rounded-xl border border-outline-variant p-5 hover:shadow-soft-care-hover transition-ui cursor-pointer"
          @click="openBooking(svc)"
          role="button"
          tabindex="0"
          @keydown.enter="openBooking(svc)"
        >
          <div class="flex justify-between items-start">
            <div class="flex-1 min-w-0">
              <h3 class="font-label-md text-label-md text-on-surface truncate">{{ svc.serviceName }}</h3>
              <p class="font-label-sm text-label-sm text-on-surface-variant mt-1 line-clamp-2">{{ svc.description || '暂无描述' }}</p>
              <p class="font-label-sm text-label-sm text-on-surface-variant mt-2">
                <span class="material-symbols-outlined text-sm align-middle mr-0.5">store</span>
                {{ svc.provider || '社区服务中心' }}
              </p>
            </div>
            <div class="text-right ml-4 flex-shrink-0">
              <div class="font-headline-md text-headline-md text-primary whitespace-nowrap">¥{{ svc.price }}</div>
              <button
                class="mt-2 bg-primary text-on-primary font-label-sm text-label-sm px-4 py-2 rounded-md touch-target transition-ui hover:bg-primary-hover cursor-pointer border-0"
                @click.stop="openBooking(svc)"
              >
                预约
              </button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ========== My Orders Tab ========== -->
    <template v-else-if="activeTab === 'orders'">
      <!-- Empty -->
      <div v-if="myOrders.length === 0" class="text-center py-16">
        <span class="material-symbols-outlined text-4xl text-on-surface-variant mb-3">receipt_long</span>
        <p class="font-body-md text-body-md text-on-surface mb-1">暂无订单</p>
        <p class="font-label-md text-label-md text-on-surface-variant">预约服务后订单会出现在这里</p>
      </div>

      <!-- Order list -->
      <div v-else class="space-y-3">
        <div
          v-for="order in myOrders"
          :key="order.id"
          class="bg-surface-container-lowest rounded-xl border border-outline-variant p-5"
        >
          <div class="flex justify-between items-start">
            <div>
              <div class="flex items-center gap-2 mb-1">
                <span class="font-label-md text-label-md text-on-surface">订单 #{{ (order.orderNo || '').slice(-6) || order.id }}</span>
                <span
                  class="font-label-sm text-label-sm px-2 py-0.5 rounded-full"
                  :class="statusClass(order.status)"
                >{{ statusLabel(order.status) }}</span>
              </div>
              <p class="font-label-md text-label-md text-on-surface-variant">
                {{ order.serviceName || '服务' }}
                <span v-if="order.serviceTime" class="ml-2">· {{ formatDateTime(order.serviceTime) }}</span>
              </p>
              <p class="font-label-sm text-label-sm text-on-surface-variant mt-1">
                {{ order.address || '社区服务中心' }}
              </p>
            </div>
            <div class="text-right ml-4 flex-shrink-0">
              <div class="font-label-md text-label-md text-on-surface">¥{{ order.amount || 0 }}</div>
              <p class="font-label-sm text-label-sm"
                :class="order.paymentStatus === 'paid' ? 'text-tertiary' : 'text-error'"
              >
                {{ order.paymentStatus === 'paid' ? '已支付' : '待支付' }}
              </p>
            </div>
          </div>
          <!-- Cancel button -->
          <button
            v-if="order.status === 'pending'"
            class="mt-3 font-label-sm text-label-sm text-error hover:underline cursor-pointer bg-transparent border-0 p-0"
            @click="cancelOrder(order.id)"
          >
            取消订单
          </button>
        </div>
      </div>
    </template>

    <!-- ========== Booking Modal ========== -->
    <Teleport to="body">
      <Transition name="modal">
        <div
          v-if="bookingSvc"
          class="fixed inset-0 z-50 flex items-center justify-center p-4"
          @click.self="bookingSvc = null"
        >
          <div class="absolute inset-0 bg-black/50" />
          <div class="relative bg-surface-container-lowest rounded-2xl p-6 w-full max-w-md shadow-2xl">
            <h2 class="font-headline-md text-headline-md text-on-surface mb-4">预约服务</h2>

            <div class="mb-4">
              <p class="font-label-md text-label-md text-on-surface">{{ bookingSvc.serviceName }}</p>
              <p class="font-label-sm text-label-sm text-on-surface-variant mt-0.5">{{ bookingSvc.provider }} · ¥{{ bookingSvc.price }}</p>
            </div>

            <!-- Date -->
            <label class="block mb-3">
              <span class="font-label-md text-label-md text-on-surface block mb-1">预约时间</span>
              <input
                v-model="bookingForm.serviceTime"
                type="datetime-local"
                class="w-full bg-surface-container-lowest border border-outline-variant rounded-md px-4 py-3 font-body-md text-body-md text-on-surface focus:border-2 focus:border-primary focus:outline-none transition-ui"
                style="min-height: 56px;"
              />
            </label>

            <!-- Address -->
            <label class="block mb-3">
              <span class="font-label-md text-label-md text-on-surface block mb-1">服务地址</span>
              <input
                v-model="bookingForm.address"
                type="text"
                placeholder="如：幸福社区1号楼101室"
                class="w-full bg-surface-container-lowest border border-outline-variant rounded-md px-4 py-3 font-body-md text-body-md text-on-surface placeholder:text-on-surface-variant focus:border-2 focus:border-primary focus:outline-none transition-ui"
                style="min-height: 56px;"
              />
            </label>

            <!-- Remark -->
            <label class="block mb-5">
              <span class="font-label-md text-label-md text-on-surface block mb-1">备注（选填）</span>
              <input
                v-model="bookingForm.remark"
                type="text"
                placeholder="特殊需求..."
                class="w-full bg-surface-container-lowest border border-outline-variant rounded-md px-4 py-3 font-body-md text-body-md text-on-surface placeholder:text-on-surface-variant focus:border-2 focus:border-primary focus:outline-none transition-ui"
                style="min-height: 56px;"
              />
            </label>

            <!-- Error -->
            <p v-if="bookingError" class="font-label-sm text-label-sm text-error mb-3">{{ bookingError }}</p>

            <!-- Actions -->
            <div class="flex gap-3 justify-end">
              <button
                class="bg-surface-container-high text-on-surface font-label-md text-label-md px-5 py-3 rounded-md touch-target transition-ui hover:bg-surface-variant cursor-pointer border-0"
                @click="bookingSvc = null"
              >
                取消
              </button>
              <button
                class="bg-primary text-on-primary font-label-md text-label-md px-5 py-3 rounded-md touch-target transition-ui hover:bg-primary-hover cursor-pointer border-0 disabled:opacity-50"
                :disabled="bookingSubmitting"
                @click="submitBooking"
              >
                <span v-if="bookingSubmitting" class="flex items-center gap-1">
                  <span class="material-symbols-outlined text-sm animate-spin">progress_activity</span>
                  提交中
                </span>
                <span v-else>确认预约</span>
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user.js'
import { serviceAPI, orderAPI } from '../api/index.js'

const userStore = useUserStore()

// ============ Tabs ============
const activeTab = ref('services')
const tabs = [
  { key: 'services', label: '服务目录' },
  { key: 'orders', label: '我的订单' },
]

// ============ State ============
const loading = ref(true)
const loadError = ref('')
const services = ref([])
const myOrders = ref([])
const selectedType = ref('')
const bookingSvc = ref(null)
const bookingSubmitting = ref(false)
const bookingError = ref('')
const bookingForm = ref({ serviceTime: '', address: '', remark: '' })

// ============ Service Types ============
const serviceTypes = [
  { key: 'medical', label: '医疗', icon: 'medical_services' },
  { key: 'housekeeping', label: '家政', icon: 'cleaning_services' },
  { key: 'food', label: '餐饮', icon: 'restaurant' },
  { key: 'shopping', label: '代购', icon: 'shopping_bag' },
  { key: 'companion', label: '陪护', icon: 'diversity_3' },
  { key: 'beauty', label: '美发', icon: 'content_cut' },
  { key: 'health', label: '养生', icon: 'spa' },
  { key: 'life', label: '生活', icon: 'handyman' },
]

// ============ Computed ============
const filteredServices = computed(() => {
  if (!selectedType.value) return services.value
  return services.value.filter(s => s.serviceType === selectedType.value)
})

// ============ Fetch ============
async function fetchData() {
  loading.value = true
  loadError.value = ''
  try {
    const [svcRes, orderRes] = await Promise.all([
      serviceAPI.getAll(),
      orderAPI.getByUser(userStore.userId),
    ])
    services.value = svcRes.data || []
    myOrders.value = orderRes.data || []
    loading.value = false
  } catch {
    loadError.value = '加载失败，请检查网络'
    loading.value = false
  }
}

// ============ Booking ============
function openBooking(svc) {
  bookingSvc.value = svc
  bookingForm.value = { serviceTime: '', address: userStore.phone ? '请填写您的地址' : '', remark: '' }
  bookingError.value = ''
}

async function submitBooking() {
  if (!bookingForm.value.serviceTime) {
    bookingError.value = '请选择预约时间'
    return
  }
  if (!bookingForm.value.address.trim()) {
    bookingError.value = '请填写服务地址'
    return
  }

  bookingSubmitting.value = true
  bookingError.value = ''
  try {
    await orderAPI.create({
      userId: userStore.userId,
      serviceId: bookingSvc.value.id,
      serviceTime: bookingForm.value.serviceTime.replace('T', ' ') + ':00',
      address: bookingForm.value.address.trim(),
      remark: bookingForm.value.remark.trim(),
    })
    bookingSvc.value = null
    // Refresh orders
    const orderRes = await orderAPI.getByUser(userStore.userId)
    myOrders.value = orderRes.data || []
    alert('预约成功！')
  } catch (e) {
    bookingError.value = e?.message || '预约失败，请稍后重试'
  } finally {
    bookingSubmitting.value = false
  }
}

// ============ Cancel ============
async function cancelOrder(orderId) {
  if (!confirm('确定要取消这个订单吗？')) return
  try {
    await orderAPI.cancel(orderId)
    const orderRes = await orderAPI.getByUser(userStore.userId)
    myOrders.value = orderRes.data || []
  } catch (e) {
    alert(e?.message || '取消失败')
  }
}

// ============ Helpers ============
function statusClass(status) {
  const map = { pending: 'bg-surface-container-high text-on-surface-variant', confirmed: 'bg-primary-fixed text-primary', completed: 'bg-tertiary-fixed text-tertiary', cancelled: 'bg-error-container text-error' }
  return map[status] || ''
}

function statusLabel(status) {
  const map = { pending: '待确认', confirmed: '已确认', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}

function formatDateTime(str) {
  if (!str) return ''
  return str.replace('T', ' ').slice(0, 16)
}

onMounted(fetchData)
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 200ms ease-out;
}
.modal-enter-active > div:last-child,
.modal-leave-active > div:last-child {
  transition: transform 200ms ease-out;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-from > div:last-child {
  transform: scale(0.95) translateY(8px);
}
.modal-leave-to > div:last-child {
  transform: scale(0.95) translateY(8px);
}
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.animate-spin {
  animation: spin 1s linear infinite;
}
</style>
