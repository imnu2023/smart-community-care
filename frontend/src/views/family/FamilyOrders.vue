<template>
  <div class="fo-page">
    <header class="fo-hero">
      <p class="fo-greeting">订单管理</p>
      <h1 class="fo-hero-title">为家人支付</h1>
      <p class="fo-hero-desc" v-if="orders.length > 0">共 {{ pendingList.length }} 笔待付，合计 ¥{{ totalPending.toFixed(2) }}</p>
    </header>

    <!-- Loading -->
    <div v-if="loading" class="fo-skeleton">
      <div v-for="i in 3" :key="i" class="skeleton" style="height:140px;border-radius:var(--radius-xl);margin-bottom:var(--space-md)"></div>
    </div>

    <!-- Empty -->
    <div v-else-if="orders.length === 0" class="fo-empty">
      <div class="fo-empty-inner">
        <AppIcon name="package" size="40" class="fo-empty-icon" />
        <h2>暂无订单</h2>
        <p>家人没有未完成的订单，一切安好。</p>
      </div>
    </div>

    <!-- Order list -->
    <div v-else class="fo-list">
      <article
        v-for="(order, i) in orders"
        :key="order.id"
        class="fo-card"
        :class="{ 'fo-card--urgent': order.status === 'pending' && order.paymentStatus !== 'paid' }"
        :style="{ animationDelay: `${0.06 * i}s` }"
      >
        <div class="fo-card-top">
          <!-- Elder info -->
          <div class="fo-elder">
            <span class="fo-elder-avatar">{{ getElderName(order.userId).charAt(0) }}</span>
            <div>
              <strong>{{ getElderName(order.userId) }}</strong>
              <span class="fo-elder-phone">{{ getElderPhone(order.userId) }}</span>
            </div>
          </div>
          <!-- Status + amount -->
          <div class="fo-card-meta">
            <span v-if="order.paymentStatus !== 'paid'" class="fo-badge fo-badge--unpaid">未支付</span>
            <span class="fo-badge" :class="badgeClass(order)">{{ statusText(order) }}</span>
            <span class="fo-card-amount">¥{{ order.amount }}</span>
          </div>
        </div>

        <div class="fo-card-body">
          <div class="fo-service">
            <span class="fo-service-icon">
              <AppIcon name="tool" size="18" />
            </span>
            <div>
              <div class="fo-service-name">{{ order.serviceName || '社区服务' }}</div>
              <div class="fo-service-no">{{ order.orderNo }}</div>
            </div>
          </div>
          <div class="fo-meta-row">
            <span><AppIcon name="clock" size="13" />{{ formatServiceTime(order.serviceTime) }}</span>
            <span><AppIcon name="map-pin" size="13" />{{ order.address || '--' }}</span>
          </div>
        </div>

        <div class="fo-card-foot">
          <button
            v-if="order.status === 'pending' && order.paymentStatus !== 'paid'"
            class="fo-pay-btn"
            @click="openPayModal(order)"
          >
            代付 ¥{{ order.amount }}
          </button>
          <span v-else class="fo-done-text">
            <AppIcon name="check" size="14" />
            {{ order.paymentStatus === 'paid' ? '已支付' : statusText(order) }}
          </span>
        </div>
      </article>
    </div>

    <!-- Pay Modal -->
    <transition name="fo-modal">
      <div v-if="showPayModal" class="fo-overlay" @click.self="closePayModal">
        <div class="fo-modal-card">
          <div class="fo-modal-head">
            <h3>确认代付</h3>
            <button class="btn btn-ghost btn-sm" @click="closePayModal"><AppIcon name="x" size="18" /></button>
          </div>
          <div class="fo-modal-body" v-if="payTarget">
            <div class="fo-modal-elder">
              <span class="fo-modal-avatar">{{ getElderName(payTarget.userId).charAt(0) }}</span>
              <div>
                <strong>{{ getElderName(payTarget.userId) }}</strong>
                <p>{{ payTarget.serviceName }}</p>
              </div>
              <span class="fo-modal-amount">¥{{ payTarget.amount }}</span>
            </div>
            <dl class="fo-modal-dl">
              <div><dt>时间</dt><dd>{{ formatServiceTime(payTarget.serviceTime) }}</dd></div>
              <div><dt>地址</dt><dd>{{ payTarget.address }}</dd></div>
              <div><dt>余额</dt><dd :class="{ 'fo-text-warn': walletBalance < payTarget.amount }">¥{{ walletBalance.toFixed(2) }}</dd></div>
            </dl>
          </div>
          <div class="fo-modal-foot">
            <button class="btn btn-secondary" @click="closePayModal">取消</button>
            <button class="fo-pay-btn" :disabled="paying || walletBalance < (payTarget?.amount || 0)" @click="handlePay">
              {{ paying ? '支付中…' : walletBalance < (payTarget?.amount || 0) ? '余额不足' : '确认代付' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { relationAPI, paymentAPI, walletAPI } from '../../api'
import AppIcon from '../../components/AppIcon.vue'

const familyId = ref(parseInt(localStorage.getItem('userId') || '0'))
const loading = ref(true)
const orders = ref([])
const eldersMap = ref({})
const walletBalance = ref(0)
const showPayModal = ref(false)
const payTarget = ref(null)
const paying = ref(false)

const pendingList = computed(() => orders.value.filter(o => o.status === 'pending' && o.paymentStatus !== 'paid'))
const totalPending = computed(() => pendingList.value.reduce((s, o) => s + (parseFloat(o.amount) || 0), 0))
const getElderName = (id) => eldersMap.value[id]?.name || '老人'
const getElderPhone = (id) => eldersMap.value[id]?.phone || ''
const statusText = (o) => ({ pending: '待处理', confirmed: '处理中', completed: '已完成', cancelled: '已取消' }[o.status] || o.status)
const badgeClass = (o) => ({ pending: 'fo-badge--pending', confirmed: 'fo-badge--info', completed: 'fo-badge--done', cancelled: 'fo-badge--neutral' }[o.status] || '')
const formatServiceTime = (t) => {
  if (!t) return '--'
  try {
    return new Date(t.replace('T', ' ') + (t.includes(':') && !t.includes(':', t.indexOf(':') + 1) ? ':00' : '')).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  } catch (e) {
    return t
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const relRes = await relationAPI.getByFamilyMember(familyId.value)
    if (relRes?.code === 200 && relRes.data) {
      relRes.data.forEach(r => { eldersMap.value[r.elderId] = { name: r.elderName || '老人', phone: r.elderPhone || '' } })
    }
    const oRes = await paymentAPI.getElderOrders(familyId.value)
    if (oRes?.code === 200 && oRes.data) {
      orders.value = oRes.data.sort((a, b) => {
        if (a.status === 'pending' && a.paymentStatus !== 'paid') return -1
        if (b.status === 'pending' && b.paymentStatus !== 'paid') return 1
        return new Date(b.createdAt) - new Date(a.createdAt)
      })
    }
    const wRes = await walletAPI.getBalance(familyId.value)
    if (wRes?.code === 200) walletBalance.value = wRes.data?.balance || 0
  } catch (e) { ElMessage.error('加载失败') }
  loading.value = false
}

const openPayModal = (o) => { payTarget.value = o; showPayModal.value = true }
const closePayModal = () => { showPayModal.value = false; payTarget.value = null }
const handlePay = async () => {
  if (!payTarget.value || paying.value) return; paying.value = true
  try {
    const res = await paymentAPI.familyPay(payTarget.value.id, familyId.value)
    if (res?.code === 200 && res.data?.success !== false) { ElMessage.success('代付成功'); closePayModal(); loadData() }
    else ElMessage.error(res?.data?.message || '余额不足')
  } catch (e) { ElMessage.error('支付失败') }
  paying.value = false
}

onMounted(() => loadData())
</script>

<style scoped>
.fo-page { padding-bottom: var(--space-section); }

/* Hero */
.fo-hero { margin-bottom: var(--space-2xl); }
.fo-greeting { font-size: var(--text-body-lg); color: var(--color-on-surface-variant); margin: 0 0 var(--space-xs); }
.fo-hero-title { font-size: clamp(28px, 4vw, 40px); font-weight: var(--weight-bold); letter-spacing: -0.025em; margin: 0 0 var(--space-sm); }
.fo-hero-desc { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: 0; }

/* Skeleton / Empty */
.fo-skeleton { max-width: 640px; }
.fo-empty { max-width: 480px; }
.fo-empty-inner { text-align: center; padding: var(--space-2xl); border: 1px dashed var(--color-outline-variant); border-radius: var(--radius-2xl); color: var(--color-on-surface-variant); }
.fo-empty-icon { margin-bottom: var(--space-lg); opacity: 0.3; }
.fo-empty-inner h2 { margin: 0 0 var(--space-xs); color: var(--color-on-surface); }

/* Cards */
.fo-list { display: flex; flex-direction: column; gap: var(--space-md); max-width: 680px; }
.fo-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  padding: var(--space-lg);
  box-shadow: var(--shadow-card);
  opacity: 0;
  animation: fo-in 0.5s cubic-bezier(0.32,0.72,0,1) forwards;
  transition: box-shadow 0.3s cubic-bezier(0.32,0.72,0,1), transform 0.2s cubic-bezier(0.32,0.72,0,1);
}
.fo-card:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }
.fo-card--urgent { border-left: 3px solid var(--color-error); }
@keyframes fo-in { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }

.fo-card-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--space-md); }
.fo-elder { display: flex; align-items: center; gap: var(--space-sm); }
.fo-elder-avatar { width: 36px; height: 36px; border-radius: 50%; background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); display: flex; align-items: center; justify-content: center; font-weight: var(--weight-bold); font-size: 15px; }
.fo-elder strong { display: block; font-size: var(--text-label-md); }
.fo-elder-phone { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fo-card-meta { display: flex; align-items: center; gap: var(--space-xs); }
.fo-badge { padding: 3px 10px; border-radius: var(--radius-full); font-size: 11px; font-weight: var(--weight-semibold); }
.fo-badge--unpaid { background: #fde8e8; color: #ba1a1a; }
.fo-badge--pending { background: #fef6e8; color: #a53b18; }
.fo-badge--info { background: #d2e0fc; color: #003fa4; }
.fo-badge--done { background: #e6f5ec; color: #006825; }
.fo-badge--neutral { background: var(--color-surface-container-high); color: var(--color-on-surface-variant); }
.fo-card-amount { font-size: var(--text-headline-md); font-weight: var(--weight-bold); color: var(--color-on-surface); margin-left: var(--space-sm); }

.fo-card-body { margin-bottom: var(--space-md); }
.fo-service { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: var(--space-sm); }
.fo-service-icon { width: 36px; height: 36px; border-radius: var(--radius-sm); background: var(--color-surface-container); display: flex; align-items: center; justify-content: center; color: var(--color-on-surface-variant); }
.fo-service-name { font-size: var(--text-label-md); font-weight: var(--weight-semibold); }
.fo-service-no { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fo-meta-row { display: flex; gap: var(--space-lg); padding: var(--space-sm); background: var(--color-surface-container-low); border-radius: var(--radius-md); font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fo-meta-row span { display: flex; align-items: center; gap: 4px; }

.fo-card-foot { display: flex; justify-content: flex-end; padding-top: var(--space-sm); border-top: 1px solid var(--color-outline-variant); }
.fo-done-text { display: flex; align-items: center; gap: var(--space-xs); font-size: var(--text-label-md); color: var(--color-tertiary); font-weight: var(--weight-medium); }

/* Pay button */
.fo-pay-btn {
  padding: 9px 22px; border-radius: var(--radius-full); border: none;
  background: var(--color-primary); color: var(--color-on-primary);
  font-size: var(--text-label-md); font-weight: var(--weight-semibold);
  font-family: inherit; cursor: pointer;
  transition: all 0.2s cubic-bezier(0.32,0.72,0,1);
}
.fo-pay-btn:hover:not(:disabled) { background: var(--color-primary-hover); transform: translateY(-1px); }
.fo-pay-btn:active:not(:disabled) { transform: scale(0.97); }
.fo-pay-btn:disabled { opacity: 0.45; cursor: not-allowed; }

/* Modal */
.fo-overlay { position: fixed; inset: 0; z-index: var(--z-modal); background: rgba(0,0,0,0.25); backdrop-filter: blur(6px); display: flex; align-items: center; justify-content: center; }
.fo-modal-card { width: 90%; max-width: 420px; background: var(--color-surface-container-lowest); border-radius: var(--radius-2xl); box-shadow: 0 24px 64px rgba(0,47,112,0.12); overflow: hidden; }
.fo-modal-head { display: flex; justify-content: space-between; align-items: center; padding: var(--space-lg) var(--space-xl); border-bottom: 1px solid var(--color-outline-variant); }
.fo-modal-head h3 { margin: 0; }
.fo-modal-body { padding: var(--space-xl); }
.fo-modal-foot { display: flex; gap: var(--space-sm); justify-content: flex-end; padding: var(--space-lg) var(--space-xl); border-top: 1px solid var(--color-outline-variant); }
.fo-modal-elder { display: flex; align-items: center; gap: var(--space-md); margin-bottom: var(--space-lg); padding: var(--space-md); background: var(--color-surface-container-low); border-radius: var(--radius-lg); }
.fo-modal-elder strong { display: block; }
.fo-modal-elder p { margin: 2px 0 0; font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fo-modal-avatar { width: 44px; height: 44px; border-radius: 50%; background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); display: flex; align-items: center; justify-content: center; font-size: 18px; font-weight: var(--weight-bold); flex-shrink: 0; }
.fo-modal-amount { font-size: var(--text-headline-md); font-weight: var(--weight-bold); color: var(--color-primary); margin-left: auto; }
.fo-modal-dl { display: flex; flex-direction: column; gap: var(--space-sm); }
.fo-modal-dl div { display: flex; justify-content: space-between; padding: var(--space-xs) 0; border-bottom: 1px solid var(--color-outline-variant); font-size: var(--text-body-md); }
.fo-modal-dl div:last-child { border-bottom: none; }
.fo-modal-dl dt { color: var(--color-on-surface-variant); }
.fo-text-warn { color: var(--color-error) !important; }
.fo-modal-enter-active { transition: opacity 0.3s var(--ease-out); }
.fo-modal-leave-active { transition: opacity 0.2s var(--ease-out); }
.fo-modal-enter-from, .fo-modal-leave-to { opacity: 0; }

@media (max-width: 767px) {
  .fo-card-meta { flex-wrap: wrap; }
  .fo-modal-card { max-width: 100%; border-radius: var(--radius-xl) var(--radius-xl) 0 0; align-self: flex-end; }
}
</style>
