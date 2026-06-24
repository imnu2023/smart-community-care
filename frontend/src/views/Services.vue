<template>
  <div class="sv-page">
    <header class="sv-header">
      <button class="btn-back" @click="goBack"><AppIcon name="x" size="22" /></button>
      <h2 class="text-headline-lg">社区服务</h2>
      <select v-model="selectedType" class="sv-filter">
        <option value="">全部服务</option>
        <option value="housekeeping">家政服务</option>
        <option value="medical">医疗护理</option>
        <option value="life">生活服务</option>
        <option value="culture">文化娱乐</option>
      </select>
    </header>

    <div class="sv-grid">
      <div v-for="service in filteredServices" :key="service.id" class="sv-card">
        <span class="sv-card-icon" :class="'sv-card-icon--' + service.serviceType">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="32" height="32">
            <path v-if="service.serviceType === 'housekeeping'" d="M3 21l1.65-3.8a9 9 0 1 1 3.4 2.9L3 21"/>
            <path v-else-if="service.serviceType === 'medical'" d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0"/>
            <path v-else-if="service.serviceType === 'life'" d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline v-else-if="service.serviceType === 'life'" points="9 22 9 12 15 12 15 22"/>
            <path v-else d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
        </span>
        <h3>{{ service.serviceName }}</h3>
        <p class="sv-desc">{{ service.description }}</p>
        <div class="sv-meta">
          <span class="sv-price">¥{{ service.price }}<small>/{{ getUnit(service.serviceType) }}</small></span>
          <span class="badge badge-neutral">{{ getServiceTypeName(service.serviceType) }}</span>
        </div>
        <div class="sv-contact">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/></svg>
          <span>{{ service.phone }}</span>
        </div>
        <button class="btn btn-primary btn-block" @click="bookService(service)">立即预约</button>
      </div>
    </div>

    <!-- Book modal -->
    <dialog v-if="showBookModal" class="sv-modal-overlay" @click="showBookModal = false">
      <div class="sv-modal" @click.stop>
        <div class="sv-modal-header"><h3>预约服务</h3><button class="btn btn-ghost btn-sm" @click="showBookModal = false">✕</button></div>
        <form @submit.prevent="submitBooking" class="sv-modal-body">
          <div class="form-group"><label class="form-label">服务项目</label><input class="form-input" :value="selectedService?.serviceName" disabled /></div>
          <div class="form-group"><label class="form-label">服务时间</label><input class="form-input" type="datetime-local" v-model="bookForm.serviceTime" :min="todayISO" required /></div>
          <div class="form-group"><label class="form-label">服务地址</label><input class="form-input" v-model="bookForm.address" placeholder="请输入服务地址" required /></div>
          <div class="form-group"><label class="form-label">备注</label><textarea class="form-input" v-model="bookForm.remark" placeholder="选填" rows="3" style="resize:vertical"></textarea></div>
          <div class="sv-modal-footer"><button type="button" class="btn btn-secondary" @click="showBookModal = false">取消</button><button type="submit" class="btn btn-primary">确认预约</button></div>
        </form>
      </div>
    </dialog>

    <!-- Result dialog: 预约成功后三选一 -->
    <dialog v-if="showResultModal" class="sv-modal-overlay" @click="showResultModal = false">
      <div class="sv-modal sv-modal--sm" @click.stop>
        <div class="sv-result-icon"><AppIcon name="check" :size="48" /></div>
        <h3 style="text-align:center;margin:0 0 var(--space-xs)">预约成功</h3>
        <p style="text-align:center;color:var(--color-on-surface-variant);font-size:var(--text-body-md);margin:0 0 var(--space-sm)">订单号：{{ pendingOrder?.orderNo }}</p>
        <div class="sv-pay-row sv-pay-row--total" style="justify-content:center;gap:var(--space-xs);border:none;padding-bottom:var(--space-lg)"><span>应付</span><span>¥{{ pendingOrder?.amount }}</span></div>
        <div class="sv-result-actions">
          <button class="btn btn-primary btn-block" @click="handlePayNow">立即支付</button>
          <button class="btn btn-secondary btn-block" @click="handlePayLater">稍后支付</button>
          <button class="btn btn-ghost btn-block" @click="handleCancelAfterBook" style="color:var(--color-on-surface-variant)">取消订单</button>
        </div>
      </div>
    </dialog>

    <!-- Pay modal (reused from result dialog or from Orders) -->
    <dialog v-if="showPayModal" class="sv-modal-overlay" @click="showPayModal = false">
      <div class="sv-modal sv-modal--sm" @click.stop>
        <div class="sv-modal-header"><h3>支付订单</h3><button class="btn btn-ghost btn-sm" @click="showPayModal = false">✕</button></div>
        <div class="sv-modal-body">
          <div class="sv-wallet-bar">钱包余额：<strong>¥{{ walletBalance.toFixed(2) }}</strong></div>
          <div class="sv-pay-row"><span>订单号</span><span>{{ pendingOrder?.orderNo }}</span></div>
          <div class="sv-pay-row"><span>服务项目</span><span>{{ pendingOrderServiceName }}</span></div>
          <div class="sv-pay-row"><span>服务时间</span><span>{{ pendingOrder?.serviceTime }}</span></div>
          <div class="sv-pay-row sv-pay-row--total"><span>应付金额</span><span>¥{{ pendingOrder?.amount }}</span></div>
        </div>
        <div class="sv-modal-footer">
          <button class="btn btn-secondary" @click="showPayModal = false">取消</button>
          <button class="btn btn-primary" :disabled="paying || walletBalance < (pendingOrder?.amount || 0)" @click="handlePay">
            {{ paying ? '支付中…' : walletBalance < (pendingOrder?.amount || 0) ? '余额不足' : '确认支付' }}
          </button>
        </div>
      </div>
    </dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { serviceAPI, orderAPI, paymentAPI, walletAPI } from '../api'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const goBack = () => router.push('/dashboard')

const serviceList = ref([])
const selectedType = ref('')
const showBookModal = ref(false)
const showResultModal = ref(false)
const showPayModal = ref(false)
const selectedService = ref(null)
const pendingOrder = ref(null)
const walletBalance = ref(0)
const paying = ref(false)

const bookForm = reactive({ serviceId: '', serviceTime: '', address: '', remark: '' })
const todayISO = new Date().toISOString().slice(0, 16)

const pendingOrderServiceName = computed(() => {
  if (!pendingOrder.value) return ''
  const s = serviceList.value.find(s => s.id === pendingOrder.value.serviceId)
  return s ? s.serviceName : ''
})

const loadServices = async () => {
  try { const res = await serviceAPI.getAll(); if (res.code === 200) serviceList.value = res.data } catch (e) { ElMessage.error('加载失败') }
}

const filteredServices = computed(() => selectedType.value ? serviceList.value.filter(s => s.serviceType === selectedType.value) : serviceList.value)
const getServiceTypeName = (t) => ({ housekeeping: '家政服务', medical: '医疗护理', life: '生活服务', culture: '文化娱乐' }[t] || t)
const getUnit = (t) => ({ housekeeping: '小时', medical: '次', life: '次', culture: '次' }[t] || '次')

const bookService = (s) => { selectedService.value = s; bookForm.serviceId = s.id; showBookModal.value = true }
const submitBooking = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const res = await orderAPI.create({ userId: parseInt(userId), serviceId: bookForm.serviceId, serviceTime: bookForm.serviceTime, address: bookForm.address, remark: bookForm.remark })
    if (res.code === 200) {
      pendingOrder.value = res.data
      showBookModal.value = false
      showResultModal.value = true  // Show the 3-choice result dialog instead of direct pay
    }
  } catch (e) { ElMessage.error('预约失败') }
}

const loadWalletBalance = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try { const res = await walletAPI.getBalance(parseInt(userId)); if (res.code === 200) walletBalance.value = res.data.balance || 0 } catch (e) { walletBalance.value = 0 }
}

const handlePayNow = async () => {
  showResultModal.value = false
  await loadWalletBalance()
  showPayModal.value = true
}

const handlePayLater = () => {
  showResultModal.value = false
  pendingOrder.value = null
  Object.keys(bookForm).forEach(k => bookForm[k] = '')
  ElMessage.success('订单已保存，可在「我的订单」中支付')
}

const handleCancelAfterBook = async () => {
  if (!pendingOrder.value) return
  try {
    await orderAPI.cancel(pendingOrder.value.id)
    showResultModal.value = false
    pendingOrder.value = null
    Object.keys(bookForm).forEach(k => bookForm[k] = '')
    ElMessage.success('订单已取消')
  } catch (e) { ElMessage.error('取消失败，请稍后重试') }
}

const handlePay = async () => {
  if (!pendingOrder.value || paying.value) return
  paying.value = true
  try {
    const userId = localStorage.getItem('userId')
    const res = await paymentAPI.pay(pendingOrder.value.id, parseInt(userId))
    if (res.code === 200 && res.data?.success !== false) {
      ElMessage.success('支付成功')
      showPayModal.value = false
      pendingOrder.value = null
      Object.keys(bookForm).forEach(k => bookForm[k] = '')
      loadServices()
    } else {
      const msg = res.data?.message || ''
      ElMessage.error(msg.includes('余额') ? msg : '余额不足，请充值后支付')
    }
  } catch (e) { ElMessage.error('支付失败，请稍后重试') }
  paying.value = false
}

onMounted(() => loadServices())
</script>

<style scoped>
.sv-page { min-height: 100vh; background: var(--color-surface); padding: var(--space-lg); }
.sv-header { display: flex; align-items: center; gap: var(--space-md); margin-bottom: var(--space-xl); }
.sv-header h2 { margin: 0; flex: 1; }
.sv-filter { padding: var(--space-sm) var(--space-md); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-md); font-size: var(--text-body-md); font-family: var(--font-family); color: var(--color-on-surface); background: var(--color-surface-container-lowest); min-height: var(--touch-min); cursor: pointer; }

.sv-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: var(--space-lg); }
.sv-card { background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); padding: var(--space-lg); text-align: center; box-shadow: var(--shadow-card); transition: box-shadow var(--duration-fast) var(--ease-out), transform var(--duration-fast) var(--ease-out); display: flex; flex-direction: column; gap: var(--space-sm); }
.sv-card:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }
.sv-card:nth-child(1) { animation: login-fade-up 0.45s var(--ease-out) 0.1s both; }
.sv-card:nth-child(2) { animation: login-fade-up 0.45s var(--ease-out) 0.15s both; }
.sv-card:nth-child(3) { animation: login-fade-up 0.45s var(--ease-out) 0.2s both; }
.sv-card:nth-child(4) { animation: login-fade-up 0.45s var(--ease-out) 0.25s both; }
.sv-card:nth-child(5) { animation: login-fade-up 0.45s var(--ease-out) 0.3s both; }
.sv-card:nth-child(6) { animation: login-fade-up 0.45s var(--ease-out) 0.35s both; }
@keyframes login-fade-up { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
.sv-card-icon { width: 64px; height: 64px; border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: center; color: #fff; margin: 0 auto; }
.sv-card-icon--housekeeping { background: #06b6d4; }
.sv-card-icon--medical { background: var(--color-error); }
.sv-card-icon--life { background: var(--color-tertiary); }
.sv-card-icon--culture { background: var(--color-primary); }
.sv-card h3 { font-size: var(--text-headline-md); margin: 0; }
.sv-desc { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: 0; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.sv-meta { display: flex; justify-content: space-between; align-items: center; }
.sv-price { font-size: var(--text-headline-md); font-weight: var(--weight-bold); color: var(--color-on-surface); }
.sv-price small { font-size: var(--text-label-sm); font-weight: var(--weight-regular); color: var(--color-on-surface-variant); }
.sv-contact { display: flex; align-items: center; justify-content: center; gap: var(--space-xs); font-size: var(--text-label-md); color: var(--color-on-surface-variant); }

/* Modals */
.sv-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); border: none; width: 100%; height: 100%; }
.sv-modal { background: var(--color-surface-container-lowest); border-radius: var(--radius-2xl); width: 90%; max-width: 480px; box-shadow: var(--shadow-soft-hover); }
.sv-modal--sm { max-width: 380px; }
.sv-modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-lg) var(--space-xl); border-bottom: 1px solid var(--color-outline-variant); }
.sv-modal-header h3 { margin: 0; font-size: var(--text-headline-md); }
.sv-modal-body { padding: var(--space-xl); }
.sv-modal-footer { display: flex; gap: var(--space-sm); justify-content: flex-end; padding-top: var(--space-lg); border-top: 1px solid var(--color-outline-variant); margin-top: var(--space-md); }
.sv-pay-row { display: flex; justify-content: space-between; padding: var(--space-sm) 0; border-bottom: 1px solid var(--color-outline-variant); font-size: var(--text-body-md); }
.sv-pay-row--total { border-bottom: none; font-weight: var(--weight-bold); }
.sv-pay-row--total span:last-child { font-size: var(--text-headline-md); color: var(--color-primary); }

.sv-wallet-bar { padding: var(--space-sm) var(--space-md); background: var(--color-primary-fixed); border-radius: var(--radius-md); font-size: var(--text-body-md); margin-bottom: var(--space-md); }
.sv-wallet-bar strong { color: var(--color-on-primary-fixed); }

.sv-result-icon { text-align: center; margin-bottom: var(--space-sm); color: var(--color-tertiary); }
.sv-result-actions { display: flex; flex-direction: column; gap: var(--space-sm); }
.sv-result-actions .btn-block { width: 100%; justify-content: center; }

@media (max-width: 767px) {
  .sv-page { padding: var(--space-md); }
  .sv-header { flex-wrap: wrap; }
  .sv-grid { grid-template-columns: 1fr; }
}
</style>
