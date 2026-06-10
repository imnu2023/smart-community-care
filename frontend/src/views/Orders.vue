<template>
  <div class="orders-page">
    <div class="page-header">
      <div class="header-text">
        <h1>我的订单</h1>
        <p>管理您的服务订单</p>
      </div>
      <div class="stats-summary">
        <div class="stat-item">
          <span class="stat-value">{{ orderList.length }}</span>
          <span class="stat-label">全部订单</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ pendingCount }}</span>
          <span class="stat-label">待处理</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ completedCount }}</span>
          <span class="stat-label">已完成</span>
        </div>
      </div>
    </div>

    <div class="tabs-container">
      <button 
        v-for="tab in tabs" 
        :key="tab.value" 
        :class="['tab-btn', { active: activeTab === tab.value }]"
        @click="activeTab = tab.value"
      >
        <span class="tab-icon">{{ tab.icon }}</span>
        <span class="tab-label">{{ tab.label }}</span>
        <span v-if="tab.count > 0" class="tab-badge">{{ tab.count }}</span>
      </button>
    </div>

    <div class="order-list">
      <div 
        v-for="order in filteredOrders" 
        :key="order.id" 
        class="order-card"
        @click="showOrderDetail(order)"
      >
        <div class="card-header">
          <div class="order-info">
            <span class="order-no">订单号: {{ order.orderNo }}</span>
            <span class="order-date">{{ formatDate(order.createdAt) }}</span>
          </div>
          <span :class="['status-tag', order.status]">{{ getStatusText(order.status) }}</span>
        </div>
        
        <div class="card-body">
          <div class="service-card">
            <div class="service-icon" :class="getServiceType(order.serviceId)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path :d="getServiceIcon(order.serviceId)"/>
              </svg>
            </div>
            <div class="service-info">
              <h4>{{ getServiceName(order.serviceId) }}</h4>
              <p>{{ getServiceTypeName(order.serviceId) }}</p>
            </div>
          </div>
          
          <div class="order-details">
            <div class="detail-row">
              <span class="detail-icon">🕐</span>
              <span class="detail-text">{{ order.serviceTime }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-icon">📍</span>
              <span class="detail-text">{{ order.address }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-icon">💰</span>
              <span class="detail-text">¥{{ order.amount }}</span>
            </div>
          </div>
        </div>
        
        <div class="card-footer">
          <div class="order-actions">
            <button 
              v-if="order.status === 'pending'" 
              class="action-btn cancel" 
              @click.stop="cancelOrder(order)"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
              取消订单
            </button>
            <button 
              v-if="order.status === 'completed'" 
              class="action-btn primary" 
              @click.stop="openReviewModal(order)"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
              </svg>
              评价服务
            </button>
          </div>
        </div>
      </div>
      
      <div v-if="filteredOrders.length === 0" class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 7h-9M14 17H5m15-4h-9"/>
          <circle cx="17" cy="7" r="2"/>
          <circle cx="7" cy="17" r="2"/>
          <circle cx="17" cy="17" r="2"/>
        </svg>
        <p>暂无{{ getTabLabel(activeTab) }}订单</p>
      </div>
    </div>

    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content detail-modal" @click.stop>
        <div class="modal-header">
          <h3>订单详情</h3>
          <button class="close-btn" @click="closeDetailModal">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div v-if="selectedOrder" class="modal-body">
          <div class="detail-section">
            <h4>服务信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">订单编号</span>
                <span class="info-value">{{ selectedOrder.orderNo }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务项目</span>
                <span class="info-value">{{ getServiceName(selectedOrder.serviceId) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务时间</span>
                <span class="info-value">{{ selectedOrder.serviceTime }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务地址</span>
                <span class="info-value">{{ selectedOrder.address }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">订单金额</span>
                <span class="info-value highlight">¥{{ selectedOrder.amount }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">订单状态</span>
                <span :class="['info-value', 'status-' + selectedOrder.status]">{{ getStatusText(selectedOrder.status) }}</span>
              </div>
            </div>
          </div>
          <div v-if="selectedOrder.remark" class="detail-section">
            <h4>备注信息</h4>
            <p>{{ selectedOrder.remark }}</p>
          </div>
          <div class="detail-section">
            <h4>服务联系方式</h4>
            <div class="contact-info">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
              </svg>
              <span>{{ getServicePhone(selectedOrder.serviceId) }}</span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeDetailModal">关闭</button>
        </div>
      </div>
    </div>

    <div v-if="showReviewModal" class="modal-overlay" @click="closeReviewModal">
      <div class="modal-content review-modal" @click.stop>
        <div class="modal-header">
          <h3>评价服务</h3>
          <button class="close-btn" @click="closeReviewModal">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="review-form">
            <div class="rating-section">
              <label>评分</label>
              <div class="rating-stars">
                <button 
                  v-for="star in 5" 
                  :key="star"
                  :class="['star', { active: star <= reviewRating }]"
                  @click="reviewRating = star"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                  </svg>
                </button>
              </div>
            </div>
            <div class="comment-section">
              <label>评价内容</label>
              <textarea 
                v-model="reviewComment" 
                placeholder="请输入您的评价..."
                rows="4"
              ></textarea>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-secondary" @click="closeReviewModal">取消</button>
          <button class="btn-primary" @click="submitReview">提交评价</button>
        </div>
      </div>
    </div>

    <div v-if="showCancelConfirm" class="modal-overlay" @click="showCancelConfirm = false">
      <div class="modal-content confirm-modal" @click.stop>
        <div class="confirm-icon warning">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
          </svg>
        </div>
        <h3>确认取消订单</h3>
        <p>确定要取消该订单吗？</p>
        <div class="modal-actions">
          <button class="btn-secondary" @click="showCancelConfirm = false">取消</button>
          <button class="btn-danger" @click="confirmCancel">确认取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { orderAPI, serviceAPI } from '../api'
import { ElMessage } from 'element-plus'

const tabs = [
  { label: '全部', value: 'all', icon: '📋', count: 0 },
  { label: '待处理', value: 'pending', icon: '⏳', count: 0 },
  { label: '已完成', value: 'completed', icon: '✅', count: 0 },
  { label: '已取消', value: 'cancelled', icon: '❌', count: 0 }
]

const orderList = ref([])
const serviceList = ref([])
const activeTab = ref('all')
const showDetailModal = ref(false)
const showReviewModal = ref(false)
const showCancelConfirm = ref(false)
const selectedOrder = ref(null)
const orderToCancel = ref(null)
const reviewRating = ref(5)
const reviewComment = ref('')

const pendingCount = computed(() => orderList.value.filter(o => o.status === 'pending').length)
const completedCount = computed(() => orderList.value.filter(o => o.status === 'completed').length)
const cancelledCount = computed(() => orderList.value.filter(o => o.status === 'cancelled').length)

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orderList.value
  return orderList.value.filter(o => o.status === activeTab.value)
})

const loadOrders = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await orderAPI.getByUser(userId)
    if (response.code === 200) {
      orderList.value = response.data
      updateTabCounts()
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
}

const loadServices = async () => {
  try {
    const response = await serviceAPI.getAll()
    if (response.code === 200) {
      serviceList.value = response.data
    }
  } catch (error) {
    console.error('加载服务列表失败')
  }
}

const updateTabCounts = () => {
  tabs[0].count = orderList.value.length
  tabs[1].count = pendingCount.value
  tabs[2].count = completedCount.value
  tabs[3].count = cancelledCount.value
}

const getServiceName = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  return service ? service.serviceName : '未知服务'
}

const getServiceTypeName = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  if (!service) return ''
  const types = {
    housekeeping: '家政服务',
    medical: '医疗护理',
    life: '生活服务',
    culture: '文化娱乐'
  }
  return types[service.serviceType] || service.serviceType
}

const getServiceType = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  return service ? service.serviceType : 'default'
}

const getServiceIcon = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  if (!service) return 'M20 7h-9M14 17H5m15-4h-9'
  
  const icons = {
    housekeeping: 'M3 21l1.65-3.8a9 9 0 1 1 3.4 2.9L3 21M16 11.3A6 6 0 1 1 10.7 6',
    medical: 'M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0',
    life: 'M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z',
    culture: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z'
  }
  return icons[service.serviceType] || icons.life
}

const getServicePhone = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  return service ? service.phone : '-'
}

const getStatusText = (status) => {
  if (status === 'pending') return '待处理'
  if (status === 'completed') return '已完成'
  return '已取消'
}

const getTabLabel = (tabValue) => {
  const tab = tabs.find(t => t.value === tabValue)
  return tab ? tab.label : ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { 
    month: 'short', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const showOrderDetail = (order) => {
  selectedOrder.value = order
  showDetailModal.value = true
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedOrder.value = null
}

const openReviewModal = (order) => {
    selectedOrder.value = order
    reviewRating.value = 5
    reviewComment.value = ''
    showReviewModal.value = true
  }

const closeReviewModal = () => {
  showReviewModal.value = false
  selectedOrder.value = null
}

const submitReview = () => {
  ElMessage.success('评价提交成功')
  closeReviewModal()
}

const cancelOrder = (order) => {
  orderToCancel.value = order
  showCancelConfirm.value = true
}

const confirmCancel = async () => {
  if (!orderToCancel.value) return
  
  try {
    const response = await orderAPI.cancel(orderToCancel.value.id)
    if (response.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    }
  } catch (error) {
    ElMessage.error('取消失败')
  }
  
  showCancelConfirm.value = false
  orderToCancel.value = null
}

onMounted(() => {
  loadOrders()
  loadServices()
})
</script>

<style scoped>
.orders-page {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 28px;
}

.header-text h1 {
  margin: 0;
  font-size: 28px;
  color: #1e293b;
  font-weight: 700;
}

.header-text p {
  margin: 8px 0 0 0;
  color: #64748b;
  font-size: 14px;
}

.stats-summary {
  display: flex;
  gap: 24px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
}

.tabs-container {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  background: white;
  padding: 8px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 16px;
  border: none;
  border-radius: 10px;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  color: #64748b;
  transition: all 0.3s;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.tab-icon {
  font-size: 18px;
}

.tab-label {
  font-weight: 500;
}

.tab-badge {
  padding: 2px 8px;
  background: rgba(239, 68, 68, 0.9);
  color: white;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
}

.order-list {
  max-width: 800px;
}

.order-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-no {
  font-size: 13px;
  color: #64748b;
}

.order-date {
  font-size: 12px;
  color: #94a3b8;
}

.status-tag {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.status-tag.pending {
  background: rgba(251, 191, 36, 0.1);
  color: #f59e0b;
}

.status-tag.completed {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.status-tag.cancelled {
  background: rgba(148, 163, 184, 0.1);
  color: #64748b;
}

.card-body {
  margin-bottom: 16px;
}

.service-card {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.service-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.service-icon svg {
  width: 24px;
  height: 24px;
}

.service-icon.housekeeping {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
}

.service-icon.medical {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
}

.service-icon.life {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
}

.service-icon.culture {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.service-icon.default {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.service-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #1e293b;
}

.service-info p {
  margin: 0;
  font-size: 13px;
  color: #64748b;
}

.order-details {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-icon {
  font-size: 16px;
}

.detail-text {
  font-size: 14px;
  color: #334155;
}

.card-footer {
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.order-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.cancel {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.action-btn.cancel:hover {
  background: rgba(239, 68, 68, 0.2);
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.action-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #94a3b8;
}

.empty-state svg {
  width: 56px;
  height: 56px;
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  animation: slideUp 0.3s;
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f1f5f9;
  border-radius: 8px;
  cursor: pointer;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn svg {
  width: 16px;
  height: 16px;
}

.modal-body {
  padding: 24px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.info-item {
  padding: 12px;
  background: #f8fafc;
  border-radius: 10px;
}

.info-label {
  display: block;
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

.info-value.highlight {
  color: #ef4444;
  font-size: 18px;
}

.info-value.status-pending {
  color: #f59e0b;
}

.info-value.status-completed {
  color: #22c55e;
}

.info-value.status-cancelled {
  color: #64748b;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.contact-info svg {
  width: 24px;
  height: 24px;
  color: #667eea;
}

.contact-info span {
  font-size: 16px;
  color: #1e293b;
  font-weight: 500;
}

.review-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.rating-section label,
.comment-section label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  margin-bottom: 12px;
}

.rating-stars {
  display: flex;
  gap: 8px;
}

.star {
  background: transparent;
  border: none;
  cursor: pointer;
  color: #e2e8f0;
  transition: all 0.2s;
}

.star svg {
  width: 32px;
  height: 32px;
}

.star.active,
.star:hover {
  color: #fbbf24;
}

.comment-section textarea {
  width: 100%;
  padding: 14px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  resize: none;
  box-sizing: border-box;
}

.comment-section textarea:focus {
  outline: none;
  border-color: #667eea;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e2e8f0;
}

.btn-secondary,
.btn-primary,
.btn-danger {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-secondary {
  background: #f1f5f9;
  color: #64748b;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.btn-danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}

.confirm-modal {
  text-align: center;
}

.confirm-icon {
  width: 64px;
  height: 64px;
  margin: 24px auto 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.confirm-icon.warning {
  background: rgba(251, 191, 36, 0.1);
  color: #f59e0b;
}

.confirm-icon svg {
  width: 32px;
  height: 32px;
}

.confirm-modal h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #1e293b;
}

.confirm-modal p {
  margin: 0 0 24px 0;
  color: #64748b;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .stats-summary {
    width: 100%;
    justify-content: space-between;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>