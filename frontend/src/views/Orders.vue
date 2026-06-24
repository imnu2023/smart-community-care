<template>
  <div class="od-page">
    <header class="od-header">
      <button class="btn-back" @click="goBack"><AppIcon name="x" size="22" /></button>
      <div class="od-header-text">
        <h1 class="text-headline-lg">我的订单</h1>
        <p class="text-body-md">管理您的服务订单</p>
      </div>
      <div class="od-header-stats">
        <div class="od-hs"><span class="od-hs-num">{{ orderList.length }}</span><span class="od-hs-lbl">全部</span></div>
        <div class="od-hs"><span class="od-hs-num">{{ pendingCount }}</span><span class="od-hs-lbl">待处理</span></div>
        <div class="od-hs"><span class="od-hs-num">{{ completedCount }}</span><span class="od-hs-lbl">已完成</span></div>
      </div>
    </header>

    <!-- Tabs -->
    <div class="od-tabs">
      <button v-for="tab in tabs" :key="tab.value" class="od-tab" :class="{ 'od-tab--active': activeTab === tab.value }" @click="activeTab = tab.value">
        <AppIcon :name="tab.icon" size="16" /> {{ tab.label }}
      </button>
    </div>

    <!-- Order list -->
    <div class="od-list">
      <div v-for="order in filteredOrders" :key="order.id" class="od-card" @click="showOrderDetail(order)">
        <div class="od-card-top">
          <div>
            <div class="od-card-no">订单号: {{ order.orderNo }}</div>
            <div class="od-card-date">{{ formatDate(order.createdAt) }}</div>
          </div>
          <div style="display:flex;align-items:center;gap:var(--space-xs)">
            <span v-if="order.paymentStatus !== 'paid'" class="badge badge-warning" style="font-size:11px">未支付</span>
            <span class="badge" :class="statusBadge(order)">{{ getStatusText(order) }}</span>
          </div>
        </div>

        <div class="od-card-body">
          <div class="od-card-service">
            <span class="od-service-icon" :class="'od-service-icon--' + getServiceType(order.serviceId)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><path :d="getServiceIcon(order.serviceId)"/></svg>
            </span>
            <div>
              <div class="od-service-name">{{ getServiceName(order.serviceId) }}</div>
              <div class="od-service-type">{{ getServiceTypeName(order.serviceId) }}</div>
            </div>
          </div>
          <div class="od-card-details">
            <div class="od-detail"><span><AppIcon name="clock" size="14" /></span> {{ order.serviceTime }}</div>
            <div class="od-detail"><span><AppIcon name="map-pin" size="14" /></span> {{ order.address }}</div>
            <div class="od-detail"><span><AppIcon name="package" size="14" /></span> ¥{{ order.amount }}</div>
          </div>
        </div>

        <div class="od-card-actions" @click.stop>
          <button v-if="order.status === 'pending' && order.paymentStatus !== 'paid'" class="btn btn-primary btn-sm" @click="openPayModal(order)">立即支付</button>
          <!-- 取消：仅未支付 + pending 状态可取消 -->
          <button v-if="order.status === 'pending' && order.paymentStatus !== 'paid'" class="btn btn-ghost btn-sm" @click="cancelOrder(order)" style="color:var(--color-error)">取消订单</button>
          <button v-if="order.status === 'completed'" class="btn btn-ghost btn-sm" @click="openReviewModal(order)">评价服务</button>
        </div>
      </div>

      <div v-if="filteredOrders.length === 0" class="empty-state">
        <div class="empty-icon"><AppIcon name="clipboard" size="48" /></div>
        <h3>暂无{{ getTabLabel(activeTab) }}订单</h3>
      </div>
    </div>

    <!-- Detail modal -->
    <dialog v-if="showDetailModal" class="od-modal-overlay" @click="closeDetailModal">
      <div class="od-modal" @click.stop>
        <div class="od-modal-header"><h3>订单详情</h3><button class="btn btn-ghost btn-sm" @click="closeDetailModal">✕</button></div>
        <div v-if="selectedOrder" class="od-modal-body">
          <div class="od-info-grid">
            <div class="od-info-item"><span class="od-info-lbl">订单编号</span><span>{{ selectedOrder.orderNo }}</span></div>
            <div class="od-info-item"><span class="od-info-lbl">服务项目</span><span>{{ getServiceName(selectedOrder.serviceId) }}</span></div>
            <div class="od-info-item"><span class="od-info-lbl">服务时间</span><span>{{ selectedOrder.serviceTime }}</span></div>
            <div class="od-info-item"><span class="od-info-lbl">服务地址</span><span>{{ selectedOrder.address }}</span></div>
            <div class="od-info-item"><span class="od-info-lbl">订单金额</span><span class="od-price">¥{{ selectedOrder.amount }}</span></div>
            <div class="od-info-item"><span class="od-info-lbl">支付状态</span><span :class="selectedOrder.paymentStatus === 'paid' ? 'od-status-completed' : 'od-status-pending'">{{ selectedOrder.paymentStatus === 'paid' ? '已支付' : '未支付' }}</span></div>
            <div class="od-info-item"><span class="od-info-lbl">订单状态</span><span :class="'od-status-' + selectedOrder.status">{{ getStatusText(selectedOrder) }}</span></div>
          </div>
          <div v-if="selectedOrder.remark" class="od-section"><h4>备注</h4><p>{{ selectedOrder.remark }}</p></div>
        </div>
        <div class="od-modal-footer"><button class="btn btn-secondary" @click="closeDetailModal">关闭</button></div>
      </div>
    </dialog>

    <!-- Review modal -->
    <dialog v-if="showReviewModal" class="od-modal-overlay" @click="closeReviewModal">
      <div class="od-modal" @click.stop>
        <div class="od-modal-header"><h3>评价服务</h3><button class="btn btn-ghost btn-sm" @click="closeReviewModal">✕</button></div>
        <div class="od-modal-body">
          <div class="form-group"><label class="form-label">评分</label><div class="od-stars"><button v-for="s in 5" :key="s" class="od-star" :class="{ 'od-star--on': s <= reviewRating }" @click="reviewRating = s">★</button></div></div>
          <div class="form-group"><label class="form-label">评价内容</label><textarea class="form-input" v-model="reviewComment" rows="4" placeholder="请输入您的评价…" style="resize:vertical"></textarea></div>
        </div>
        <div class="od-modal-footer"><button class="btn btn-secondary" @click="closeReviewModal">取消</button><button class="btn btn-primary" @click="submitReview">提交评价</button></div>
      </div>
    </dialog>

    <!-- Cancel confirm -->
    <dialog v-if="showCancelConfirm" class="od-modal-overlay" @click="showCancelConfirm = false">
      <div class="od-modal od-modal--sm" @click.stop>
        <div style="text-align:center;font-size:40px;margin-bottom:var(--space-md)"><AppIcon name="bell" size="40" /></div>
        <h3 style="text-align:center">确认取消订单</h3>
        <p style="text-align:center;color:var(--color-on-surface-variant)">确定要取消该订单吗？</p>
        <div class="od-modal-footer" style="justify-content:center"><button class="btn btn-secondary" @click="showCancelConfirm = false">返回</button><button class="btn btn-danger" @click="confirmCancel">确认取消</button></div>
      </div>
    </dialog>

    <!-- Pay modal -->
    <dialog v-if="showPayModal" class="od-modal-overlay" @click="closePayModal">
      <div class="od-modal od-modal--sm" @click.stop>
        <div class="od-modal-header"><h3>订单支付</h3><button class="btn btn-ghost btn-sm" @click="closePayModal">✕</button></div>
        <div v-if="orderToPay" class="od-modal-body">
          <div class="od-wallet">钱包余额：<strong>¥{{ walletBalance }}</strong></div>
          <div class="sv-pay-row"><span>订单号</span><span>{{ orderToPay.orderNo }}</span></div>
          <div class="sv-pay-row"><span>服务项目</span><span>{{ getServiceName(orderToPay.serviceId) }}</span></div>
          <div class="sv-pay-row"><span>服务时间</span><span>{{ orderToPay.serviceTime }}</span></div>
          <div class="sv-pay-row sv-pay-row--total"><span>应付金额</span><span>¥{{ orderToPay.amount }}</span></div>
        </div>
        <div class="od-modal-footer"><button class="btn btn-secondary" @click="closePayModal">取消</button><button class="btn btn-primary" :disabled="paying || walletBalance < (orderToPay?.amount || 0)" @click="handlePay">{{ paying ? '支付中…' : walletBalance < (orderToPay?.amount || 0) ? '余额不足' : '确认支付' }}</button></div>
      </div>
    </dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderAPI, serviceAPI, paymentAPI, walletAPI } from '../api'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const goBack = () => router.push('/dashboard')

const currentUserId = ref(parseInt(localStorage.getItem('userId') || '1'))
const tabs = [
  { label: '全部', value: 'all', icon: 'dashboard' },
  { label: '进行中', value: 'active', icon: 'clock' },
  { label: '已完成', value: 'completed', icon: 'check' },
  { label: '已取消', value: 'cancelled', icon: 'x' }
]

const orderList = ref([])
const serviceList = ref([])
const activeTab = ref('all')
const showDetailModal = ref(false)
const showReviewModal = ref(false)
const showCancelConfirm = ref(false)
const showPayModal = ref(false)
const selectedOrder = ref(null)
const orderToCancel = ref(null)
const orderToPay = ref(null)
const reviewRating = ref(5)
const reviewComment = ref('')
const walletBalance = ref(0)
const paying = ref(false)

const pendingCount = computed(() => orderList.value.filter(o => o.status === 'pending' || o.status === 'confirmed').length)
const completedCount = computed(() => orderList.value.filter(o => o.status === 'completed').length)
const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orderList.value
  if (activeTab.value === 'active') return orderList.value.filter(o => o.status === 'pending' || o.status === 'confirmed')
  return orderList.value.filter(o => o.status === activeTab.value)
})

const loadOrders = async () => {
  try { const res = await orderAPI.getByUser(currentUserId.value); if (res?.code === 200) orderList.value = (res.data || []).map(o => ({ ...o, status: o.status || 'pending', paymentStatus: o.paymentStatus || 'unpaid' })) } catch (e) { orderList.value = [] }
}
const loadServices = async () => {
  try { const res = await serviceAPI.getAll(); if (res.code === 200) serviceList.value = res.data } catch (e) {}
}

const getServiceName = (id) => (serviceList.value.find(s => s.id === id) || {}).serviceName || '未知服务'
const getServiceTypeName = (id) => { const s = serviceList.value.find(x => x.id === id); return s ? ({ housekeeping: '家政服务', medical: '医疗护理', life: '生活服务', culture: '文化娱乐' }[s.serviceType] || s.serviceType) : '' }
const getServiceType = (id) => (serviceList.value.find(s => s.id === id) || {}).serviceType || 'default'
const getServiceIcon = (id) => { const s = serviceList.value.find(x => x.id === id); if (!s) return ''; return ({ housekeeping: 'M3 21l1.65-3.8a9 9 0 1 1 3.4 2.9L3 21', medical: 'M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0', life: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z', culture: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z' }[s.serviceType] || '') }
const getStatusText = (o) => {
  const s = typeof o === 'object' ? o.status : o
  if (s === 'pending') return o.paymentStatus === 'paid' ? '待处理' : '待支付'
  return ({ confirmed: '处理中', completed: '已完成', cancelled: '已取消' }[s] || s)
}
const statusBadge = (o) => {
  const s = typeof o === 'object' ? o.status : o
  if (s === 'pending') return o.paymentStatus === 'paid' ? 'badge-info' : 'badge-warning'
  return ({ confirmed: 'badge-info', completed: 'badge-success', cancelled: 'badge-neutral' }[s] || 'badge-neutral')
}
const getTabLabel = (v) => (tabs.find(t => t.value === v) || {}).label || ''
const formatDate = (d) => d ? new Date(d).toLocaleString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

const showOrderDetail = (o) => { selectedOrder.value = o; showDetailModal.value = true }
const closeDetailModal = () => { showDetailModal.value = false; selectedOrder.value = null }
const openReviewModal = (o) => { selectedOrder.value = o; reviewRating.value = 5; reviewComment.value = ''; showReviewModal.value = true }
const closeReviewModal = () => { showReviewModal.value = false }
const submitReview = () => { ElMessage.success('评价提交成功'); closeReviewModal() }
const cancelOrder = (o) => { orderToCancel.value = o; showCancelConfirm.value = true }
const confirmCancel = async () => { if (!orderToCancel.value) return; try { await orderAPI.cancel(orderToCancel.value.id); ElMessage.success('已取消'); loadOrders() } catch (e) { ElMessage.error('取消失败') }; showCancelConfirm.value = false }
const openPayModal = async (o) => { orderToPay.value = o; try { const res = await walletAPI.getBalance(parseInt(localStorage.getItem('userId'))); if (res.code === 200) walletBalance.value = res.data.balance } catch (e) { walletBalance.value = 0 }; showPayModal.value = true }
const closePayModal = () => { showPayModal.value = false; orderToPay.value = null }
const handlePay = async () => { if (!orderToPay.value || paying.value) return; paying.value = true; try { const res = await paymentAPI.pay(orderToPay.value.id, parseInt(localStorage.getItem('userId'))); if (res.code === 200 && res.data?.success !== false) { ElMessage.success('支付成功'); closePayModal(); loadOrders() } else { const msg = res.data?.message || ''; ElMessage.error(msg.includes('余额') ? msg : '余额不足，请充值后支付') } } catch (e) { ElMessage.error('支付失败，请稍后重试') } paying.value = false }

onMounted(() => { loadOrders(); loadServices() })
</script>

<style scoped>
.od-page { min-height: 100vh; background: var(--color-surface); padding: var(--space-lg); }
.od-header { display: flex; align-items: flex-start; gap: var(--space-md); margin-bottom: var(--space-xl); }
.od-header-text { flex: 1; }
.od-header-text h1 { margin: 0; }
.od-header-text p { margin: var(--space-xxs) 0 0; color: var(--color-on-surface-variant); }
.od-header-stats { display: flex; gap: var(--space-lg); }
.od-hs { text-align: center; }
.od-hs-num { display: block; font-size: var(--text-headline-md); font-weight: var(--weight-bold); color: var(--color-primary); }
.od-hs-lbl { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }

.od-tabs { display: flex; gap: var(--space-xs); margin-bottom: var(--space-lg); background: var(--color-surface-container-lowest); padding: var(--space-xs); border-radius: var(--radius-lg); border: 1px solid var(--color-outline-variant); }
.od-tab { flex: 1; padding: var(--space-sm); border: none; border-radius: var(--radius-md); background: transparent; cursor: pointer; font-size: var(--text-label-md); font-family: var(--font-family); color: var(--color-on-surface-variant); transition: all var(--duration-fast) var(--ease-out); min-height: var(--touch-min); }
.od-tab--active { background: var(--color-primary); color: var(--color-on-primary); font-weight: var(--weight-semibold); }

.od-list { max-width: 800px; }
.od-card { background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); padding: var(--space-lg); margin-bottom: var(--space-md); cursor: pointer; box-shadow: var(--shadow-card); transition: box-shadow var(--duration-fast) var(--ease-out), transform 0.2s var(--ease-out); }
.od-card:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }
.od-card-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--space-md); padding-bottom: var(--space-sm); border-bottom: 1px solid var(--color-outline-variant); }
.od-card-no { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.od-card-date { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.od-card-body { margin-bottom: var(--space-md); }
.od-card-service { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: var(--space-sm); }
.od-service-icon { width: 48px; height: 48px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; color: #fff; flex-shrink: 0; }
.od-service-icon--housekeeping { background: #06b6d4; }
.od-service-icon--medical { background: var(--color-error); }
.od-service-icon--life { background: var(--color-tertiary); }
.od-service-icon--culture { background: var(--color-primary); }
.od-service-name { font-size: var(--text-label-md); font-weight: var(--weight-semibold); }
.od-service-type { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.od-card-details { padding: var(--space-sm); background: var(--color-surface-container-low); border-radius: var(--radius-md); }
.od-detail { font-size: var(--text-label-md); color: var(--color-on-surface); margin-bottom: var(--space-xxs); }
.od-detail:last-child { margin-bottom: 0; }
.od-card-actions { display: flex; gap: var(--space-xs); justify-content: flex-end; padding-top: var(--space-sm); border-top: 1px solid var(--color-outline-variant); }

/* Modals */
.od-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); border: none; width: 100%; height: 100%; }
.od-modal { background: var(--color-surface-container-lowest); border-radius: var(--radius-2xl); width: 90%; max-width: 500px; }
.od-modal--sm { max-width: 380px; padding: var(--space-xl); }
.od-modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-lg) var(--space-xl); border-bottom: 1px solid var(--color-outline-variant); }
.od-modal-header h3 { margin: 0; font-size: var(--text-headline-md); }
.od-modal-body { padding: var(--space-xl); }
.od-modal-footer { display: flex; gap: var(--space-sm); justify-content: flex-end; padding-top: var(--space-lg); border-top: 1px solid var(--color-outline-variant); margin-top: var(--space-md); }
.od-info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-sm); }
.od-info-item { padding: var(--space-sm); background: var(--color-surface-container-low); border-radius: var(--radius-md); font-size: var(--text-label-md); }
.od-info-lbl { display: block; font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin-bottom: 2px; }
.od-price { color: var(--color-error); font-size: var(--text-headline-md); font-weight: var(--weight-bold); }
.od-status-pending { color: var(--color-secondary); }
.od-status-completed { color: var(--color-tertiary); }
.od-status-cancelled { color: var(--color-on-surface-variant); }
.od-section { margin-top: var(--space-md); }
.od-section h4 { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin: 0 0 var(--space-xxs); }
.od-section p { font-size: var(--text-body-md); color: var(--color-on-surface); margin: 0; }
.od-stars { display: flex; gap: var(--space-xs); }
.od-star { background: none; border: none; font-size: 28px; cursor: pointer; color: var(--color-outline-variant); transition: color var(--duration-fast); }
.od-star--on { color: #fbbf24; }
.od-wallet { padding: var(--space-sm) var(--space-md); background: var(--color-primary-fixed); border-radius: var(--radius-md); font-size: var(--text-body-md); margin-bottom: var(--space-md); }
.sv-pay-row { display: flex; justify-content: space-between; padding: var(--space-sm) 0; border-bottom: 1px solid var(--color-outline-variant); font-size: var(--text-body-md); }
.sv-pay-row--total { border-bottom: none; font-weight: var(--weight-bold); }
.sv-pay-row--total span:last-child { font-size: var(--text-headline-md); color: var(--color-primary); }

@media (max-width: 767px) {
  .od-page { padding: var(--space-md); }
  .od-header { flex-direction: column; }
  .od-header-stats { width: 100%; justify-content: space-around; }
  .od-tabs { flex-wrap: wrap; }
  .od-info-grid { grid-template-columns: 1fr; }
}
</style>
