<template>
  <div class="fp-page">
    <header class="fp-header">
      <h1 class="text-headline-lg">家属代付</h1>
      <p class="text-body-md">为您关联的老人支付订单</p>
    </header>

    <!-- Elder selector -->
    <div v-if="elderList.length > 0" class="fp-elders">
      <div class="fp-elders-label text-label-md">选择老人</div>
      <div class="fp-elders-grid">
        <button v-for="elder in elderList" :key="elder.id" class="fp-elder" :class="{ 'fp-elder--active': selectedElderId === elder.id }" @click="selectElder(elder.id)">
          <span class="fp-elder-avatar">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </span>
          <div>
            <div class="fp-elder-name">{{ elder.name }}</div>
            <div class="fp-elder-phone">{{ elder.phone }}</div>
          </div>
        </button>
      </div>
    </div>

    <!-- Orders -->
    <div v-if="selectedElderId && orderList.length > 0" class="fp-orders">
      <h3 class="text-headline-md" style="margin-bottom:var(--space-md)">待支付订单</h3>
      <div class="fp-order-list">
        <div v-for="order in pendingOrders" :key="order.id" class="card fp-order">
          <div class="fp-order-top">
            <div><span class="fp-order-no">订单号: {{ order.orderNo }}</span><span class="fp-order-date">{{ formatDate(order.createdAt) }}</span></div>
            <span class="badge badge-warning">待支付</span>
          </div>
          <div class="fp-order-body">
            <div class="fp-order-service">
              <span class="fp-service-icon" :class="'fp-service-icon--' + getServiceType(order.serviceId)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><path :d="getServiceIcon(order.serviceId)"/></svg>
              </span>
              <div><strong>{{ getServiceName(order.serviceId) }}</strong><div class="fp-service-type">{{ getServiceTypeName(order.serviceId) }}</div></div>
            </div>
            <div class="fp-order-details">
              <div><AppIcon name="clock" size="14" /> {{ order.serviceTime }}</div>
              <div><AppIcon name="map-pin" size="14" /> {{ order.address }}</div>
              <div><AppIcon name="package" size="14" /> ¥{{ order.amount }}</div>
            </div>
          </div>
          <div class="fp-order-actions">
            <button class="btn btn-primary" @click="openPayModal(order)">代付 ¥{{ order.amount }}</button>
          </div>
        </div>
      </div>
      <div v-if="pendingOrders.length === 0" class="empty-state"><div class="empty-icon"><AppIcon name="clipboard" size="48" /></div><h3>暂无待支付订单</h3></div>
    </div>

    <div v-if="!selectedElderId && elderList.length > 0" class="empty-state"><div class="empty-icon"><AppIcon name="user" size="48" /></div><h3>请选择一位老人查看订单</h3></div>
    <div v-if="elderList.length === 0" class="empty-state"><div class="empty-icon"><AppIcon name="user" size="48" /></div><h3>暂无关联的老人</h3></div>

    <!-- Pay modal -->
    <dialog v-if="showPayModal" class="fp-modal-overlay" @click="closePayModal">
      <div class="fp-modal" @click.stop>
        <div class="fp-modal-header"><h3>确认代付</h3><button class="btn btn-ghost btn-sm" @click="closePayModal">✕</button></div>
        <div v-if="orderToPay" class="fp-modal-body">
          <div class="fp-pay-info">
            <div class="fp-pay-row"><span>订单号</span><span>{{ orderToPay.orderNo }}</span></div>
            <div class="fp-pay-row"><span>服务项目</span><span>{{ getServiceName(orderToPay.serviceId) }}</span></div>
            <div class="fp-pay-row"><span>服务时间</span><span>{{ orderToPay.serviceTime }}</span></div>
            <div class="fp-pay-row"><span>服务地址</span><span>{{ orderToPay.address }}</span></div>
            <div class="fp-pay-row fp-pay-row--total"><span>代付金额</span><span>¥{{ orderToPay.amount }}</span></div>
            <div class="fp-pay-row"><span>钱包余额</span><span :class="{ 'fp-balance-warn': walletBalance < orderToPay.amount }">¥{{ walletBalance }}</span></div>
          </div>
        </div>
        <div class="fp-modal-footer">
          <button class="btn btn-secondary" @click="closePayModal">取消</button>
          <button class="btn btn-primary" :disabled="paying || walletBalance < (orderToPay?.amount || 0)" @click="handleFamilyPay">{{ paying ? '支付中…' : walletBalance < (orderToPay?.amount || 0) ? '余额不足' : '确认代付' }}</button>
        </div>
      </div>
    </dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { relationAPI, orderAPI, serviceAPI, paymentAPI, walletAPI } from '../api'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'

const elderList = ref([])
const serviceList = ref([])
const orderList = ref([])
const selectedElderId = ref(null)
const showPayModal = ref(false)
const orderToPay = ref(null)
const walletBalance = ref(0)
const paying = ref(false)

const pendingOrders = computed(() => orderList.value.filter(o => o.status === 'pending' && o.paymentStatus !== 'paid'))

const loadElders = async () => {
  const userId = localStorage.getItem('userId'); if (!userId) return
  try { const res = await relationAPI.getByFamilyMember(parseInt(userId)); if (res.code === 200 && res.data) { elderList.value = res.data.map(r => ({ id: r.elderId, name: r.elderName, phone: r.elderPhone })); if (elderList.value.length > 0) { selectedElderId.value = elderList.value[0].id; loadOrders() } } } catch (e) { ElMessage.error('加载失败') }
}
const loadServices = async () => { try { const res = await serviceAPI.getAll(); if (res.code === 200) serviceList.value = res.data } catch (e) {} }
const loadOrders = async () => { if (!selectedElderId.value) return; try { const res = await orderAPI.getByUser(selectedElderId.value); if (res.code === 200) orderList.value = res.data } catch (e) {} }
const loadWalletBalance = async () => { const userId = localStorage.getItem('userId'); if (!userId) return; try { const res = await walletAPI.getBalance(parseInt(userId)); if (res.code === 200) walletBalance.value = res.data.balance } catch (e) { walletBalance.value = 0 } }

const selectElder = (id) => { selectedElderId.value = id; loadOrders() }
const getServiceName = (id) => (serviceList.value.find(s => s.id === id) || {}).serviceName || '未知'
const getServiceTypeName = (id) => { const s = serviceList.value.find(x => x.id === id); return s ? ({ housekeeping: '家政服务', medical: '医疗护理', life: '生活服务', culture: '文化娱乐' }[s.serviceType] || '') : '' }
const getServiceType = (id) => (serviceList.value.find(s => s.id === id) || {}).serviceType || 'default'
const getServiceIcon = (id) => { const s = serviceList.value.find(x => x.id === id); return s ? ({ housekeeping: 'M3 21l1.65-3.8a9 9 0 1 1 3.4 2.9L3 21', medical: 'M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0', life: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z', culture: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z' }[s.serviceType] || '') : '' }
const formatDate = (d) => d ? new Date(d).toLocaleString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

const openPayModal = async (o) => { orderToPay.value = o; await loadWalletBalance(); showPayModal.value = true }
const closePayModal = () => { showPayModal.value = false; orderToPay.value = null }
const handleFamilyPay = async () => { if (!orderToPay.value || paying.value) return; paying.value = true; try { const res = await paymentAPI.familyPay(orderToPay.value.id, parseInt(localStorage.getItem('userId'))); if (res.data?.success !== false) { ElMessage.success('代付成功'); closePayModal(); loadOrders(); loadWalletBalance() } else { const msg = res.data?.message || ''; ElMessage.error(msg.includes('余额') ? msg : '余额不足，请充值后支付') } } catch (e) { ElMessage.error('代付失败，请稍后重试') } paying.value = false }

onMounted(() => { loadElders(); loadServices(); loadWalletBalance() })
</script>

<style scoped>
.fp-page { min-height: 100vh; background: var(--color-surface); padding: var(--space-lg); }
.fp-header { margin-bottom: var(--space-xl); }
.fp-header h1 { margin: 0; }
.fp-header p { margin: var(--space-xxs) 0 0; color: var(--color-on-surface-variant); }

.fp-elders { margin-bottom: var(--space-xl); }
.fp-elders-label { color: var(--color-on-surface-variant); margin-bottom: var(--space-sm); }
.fp-elders-grid { display: flex; gap: var(--space-md); flex-wrap: wrap; }
.fp-elder { display: flex; align-items: center; gap: var(--space-sm); padding: var(--space-md); background: var(--color-surface-container-lowest); border: 2px solid var(--color-outline-variant); border-radius: var(--radius-lg); cursor: pointer; font-family: inherit; transition: all var(--duration-fast) var(--ease-out); min-width: 200px; }
.fp-elder:hover { border-color: var(--color-primary-fixed-dim); }
.fp-elder--active { border-color: var(--color-primary); background: var(--color-primary-fixed); }
.fp-elder-avatar { width: 44px; height: 44px; border-radius: 50%; background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); display: flex; align-items: center; justify-content: center; }
.fp-elder--active .fp-elder-avatar { background: var(--color-primary); color: var(--color-on-primary); }
.fp-elder-name { font-size: var(--text-label-md); font-weight: var(--weight-semibold); }
.fp-elder-phone { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }

.fp-orders { max-width: 640px; }
.fp-order-list { display: flex; flex-direction: column; gap: var(--space-md); }
.fp-order { padding: var(--space-md); box-shadow: var(--shadow-card); transition: box-shadow var(--duration-fast) var(--ease-out), transform 0.2s var(--ease-out); }
.fp-order:hover { transform: translateY(-2px); box-shadow: var(--shadow-soft-hover); }
.fp-order-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--space-sm); padding-bottom: var(--space-sm); border-bottom: 1px solid var(--color-outline-variant); }
.fp-order-no { display: block; font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fp-order-date { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fp-order-body { margin-bottom: var(--space-sm); }
.fp-order-service { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: var(--space-sm); }
.fp-service-icon { width: 44px; height: 44px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; color: #fff; flex-shrink: 0; }
.fp-service-icon--housekeeping { background: #06b6d4; }
.fp-service-icon--medical { background: var(--color-error); }
.fp-service-icon--life { background: var(--color-tertiary); }
.fp-service-icon--culture { background: var(--color-primary); }
.fp-service-type { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fp-order-details { padding: var(--space-sm); background: var(--color-surface-container-low); border-radius: var(--radius-md); font-size: var(--text-label-md); display: flex; flex-direction: column; gap: var(--space-xxs); }
.fp-order-actions { display: flex; justify-content: flex-end; padding-top: var(--space-sm); border-top: 1px solid var(--color-outline-variant); }

.fp-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); border: none; width: 100%; height: 100%; }
.fp-modal { background: var(--color-surface-container-lowest); border-radius: var(--radius-2xl); width: 90%; max-width: 440px; }
.fp-modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-lg) var(--space-xl); border-bottom: 1px solid var(--color-outline-variant); }
.fp-modal-header h3 { margin: 0; font-size: var(--text-headline-md); }
.fp-modal-body { padding: var(--space-xl); }
.fp-modal-footer { display: flex; gap: var(--space-sm); justify-content: flex-end; padding-top: var(--space-lg); border-top: 1px solid var(--color-outline-variant); margin-top: var(--space-md); }
.fp-pay-info { display: flex; flex-direction: column; gap: var(--space-sm); }
.fp-pay-row { display: flex; justify-content: space-between; padding: var(--space-xs) 0; font-size: var(--text-body-md); border-bottom: 1px solid var(--color-outline-variant); }
.fp-pay-row--total { border-bottom: none; font-weight: var(--weight-bold); font-size: var(--text-headline-md); color: var(--color-primary); }
.fp-balance-warn { color: var(--color-error) !important; }

@media (max-width: 767px) { .fp-page { padding: var(--space-md); } .fp-elders-grid { flex-direction: column; } }
</style>
