<template>
  <div class="emergency-page">
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
        </div>
        <h1>紧急呼叫中心</h1>
        <p>遇到紧急情况？立即点击下方按钮寻求帮助</p>
        <div class="location-badge">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0z"/>
            <circle cx="12" cy="10" r="3"/>
          </svg>
          <span>智慧社区 A栋 302室</span>
        </div>
      </div>
    </div>

    <div class="call-section">
      <div class="section-title">
        <span>紧急求助</span>
      </div>
      
      <div class="call-grid">
        <div class="call-card medical" @click="showConfirmModal('medical')">
          <div class="call-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
              <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
          </div>
          <h3>医疗急救</h3>
          <p>突发疾病、意外受伤等紧急医疗情况</p>
          <div class="call-btn">
            <span>立即呼叫</span>
          </div>
        </div>

        <div class="call-card fire" @click="showConfirmModal('fire')">
          <div class="call-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 12c2-2 6-2 8 0s2 6 0 8-6 2-8 0-2-6 0-8"/>
              <path d="M14 12c2-2 6-2 8 0s2 6 0 8-6 2-8 0-2-6 0-8"/>
            </svg>
          </div>
          <h3>火灾报警</h3>
          <p>发现火情、烟雾或其他消防安全隐患</p>
          <div class="call-btn">
            <span>立即呼叫</span>
          </div>
        </div>

        <div class="call-card security" @click="showConfirmModal('security')">
          <div class="call-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              <path d="M9 12l2 2 4-4"/>
            </svg>
          </div>
          <h3>安全求助</h3>
          <p>遭遇危险、需要安保人员协助</p>
          <div class="call-btn">
            <span>立即呼叫</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showCallConfirm" class="modal-overlay" @click="showCallConfirm = false">
      <div class="confirm-modal" @click.stop>
        <div class="modal-icon" :class="selectedCallType">
          <svg v-if="selectedCallType === 'medical'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
          <svg v-else-if="selectedCallType === 'fire'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 12c2-2 6-2 8 0s2 6 0 8-6 2-8 0-2-6 0-8"/>
            <path d="M14 12c2-2 6-2 8 0s2 6 0 8-6 2-8 0-2-6 0-8"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
            <path d="M9 12l2 2 4-4"/>
          </svg>
        </div>
        <h3>确认呼叫</h3>
        <p>确定要发起{{ getCallTypeName(selectedCallType) }}呼叫吗？</p>
        <p class="location-info">呼叫将发送至：社区服务中心</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showCallConfirm = false">取消</button>
          <button class="btn-confirm" :class="selectedCallType" @click="handleEmergencyCall(selectedCallType)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z"/>
              <path d="M9 5h6M9 12h6M9 19h6"/>
            </svg>
            确认呼叫
          </button>
        </div>
      </div>
    </div>

    <div v-if="isCalling" class="calling-overlay">
      <div class="calling-modal">
        <div class="calling-ring">
          <div class="ring-ring"></div>
          <div class="ring-ring delay-1"></div>
          <div class="ring-ring delay-2"></div>
          <div class="calling-icon-inner" :class="currentCallType">
            <svg v-if="currentCallType === 'medical'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
              <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
            <svg v-else-if="currentCallType === 'fire'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 12c2-2 6-2 8 0s2 6 0 8-6 2-8 0-2-6 0-8"/>
              <path d="M14 12c2-2 6-2 8 0s2 6 0 8-6 2-8 0-2-6 0-8"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              <path d="M9 12l2 2 4-4"/>
            </svg>
          </div>
        </div>
        <h3>{{ getCallTypeName(currentCallType) }}呼叫中...</h3>
        <p>正在连接紧急服务中心</p>
        <div class="calling-timer">{{ callingTime }}</div>
        <button class="btn-hangup" @click="cancelCall">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
          取消呼叫
        </button>
      </div>
    </div>

    <div class="history-section">
      <div class="section-header">
        <h3>呼叫历史</h3>
        <span class="badge">{{ callHistory.length }} 条记录</span>
      </div>

      <div v-if="callHistory.length > 0" class="history-timeline">
        <div v-for="(item, index) in callHistory" :key="item.id" class="timeline-item">
          <div class="timeline-dot" :class="item.callType"></div>
          <div class="timeline-content">
            <div class="timeline-header">
              <span class="type-tag" :class="item.callType">{{ getCallTypeName(item.callType) }}</span>
              <span class="status-tag" :class="item.status">{{ getStatusText(item.status) }}</span>
            </div>
            <div class="timeline-time">{{ item.callTime }}</div>
            <div class="timeline-detail">
              <span v-if="item.handler">处理人: {{ item.handler }}</span>
              <span v-if="item.resolvedTime">| {{ item.resolvedTime }} 已处理</span>
            </div>
          </div>
          <button v-if="item.status === 'pending'" class="timeline-action" @click="cancelEmergency(item.id)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
            </svg>
          </button>
        </div>
      </div>

      <div v-else class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
        <p>暂无呼叫记录</p>
        <p class="hint">遇到紧急情况时，请点击上方按钮进行呼叫</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { emergencyAPI } from '../api'
import { ElMessage } from 'element-plus'

const callHistory = ref([])
const showCallConfirm = ref(false)
const selectedCallType = ref('')
const isCalling = ref(false)
const currentCallType = ref('')
const callingTime = ref('00:00')
let timer = null

const loadCallHistory = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await emergencyAPI.getByUser(userId)
    if (response.code === 200) {
      callHistory.value = response.data || []
    }
  } catch (error) {
    console.error('加载呼叫记录失败:', error)
  }
}

const showConfirmModal = (type) => {
  selectedCallType.value = type
  showCallConfirm.value = true
}

const handleEmergencyCall = async (callType) => {
  showCallConfirm.value = false
  
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await emergencyAPI.create({
      userId: parseInt(userId),
      callType: callType,
      description: '紧急呼叫'
    })
    
    if (response.code === 200) {
      isCalling.value = true
      currentCallType.value = callType
      callingTime.value = '00:00'
      startTimer()
      ElMessage.success('呼叫已发送，工作人员将尽快响应')
      loadCallHistory()
    }
  } catch (error) {
    ElMessage.error('呼叫发送失败')
  }
}

const startTimer = () => {
  let seconds = 0
  timer = setInterval(() => {
    seconds++
    const mins = Math.floor(seconds / 60).toString().padStart(2, '0')
    const secs = (seconds % 60).toString().padStart(2, '0')
    callingTime.value = `${mins}:${secs}`
  }, 1000)
}

const cancelCall = () => {
  isCalling.value = false
  currentCallType.value = ''
  callingTime.value = '00:00'
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  ElMessage.info('呼叫已取消')
}

const cancelEmergency = async (emergencyId) => {
  try {
    const response = await emergencyAPI.cancel(emergencyId)
    if (response.code === 200) {
      ElMessage.success('已取消呼叫')
      loadCallHistory()
    }
  } catch (error) {
    ElMessage.error('取消失败')
  }
}

const getCallTypeName = (type) => {
  const types = { medical: '医疗急救', fire: '火灾报警', security: '安全求助' }
  return types[type] || type
}

const getStatusText = (status) => {
  if (status === 'pending') return '待响应'
  if (status === 'responding') return '处理中'
  if (status === 'resolved') return '已处理'
  return '已取消'
}

onMounted(() => {
  loadCallHistory()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.emergency-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7fa 0%, #e8eaf0 100%);
  padding-bottom: 40px;
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 24px;
  text-align: center;
  color: white;
}

.hero-content {
  max-width: 600px;
  margin: 0 auto;
}

.hero-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-icon svg {
  width: 40px;
  height: 40px;
}

.hero-content h1 {
  font-size: 32px;
  margin: 0 0 12px 0;
  font-weight: 600;
}

.hero-content p {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 20px 0;
}

.location-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 25px;
  font-size: 14px;
}

.location-badge svg {
  width: 16px;
  height: 16px;
}

.call-section {
  padding: 32px 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 24px;
  text-align: center;
}

.call-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.call-card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.call-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.call-card.medical {
  border-top: 4px solid #ef4444;
}

.call-card.fire {
  border-top: 4px solid #f59e0b;
}

.call-card.security {
  border-top: 4px solid #3b82f6;
}

.call-icon {
  width: 70px;
  height: 70px;
  margin: 0 auto 20px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.call-card.medical .call-icon {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.call-card.fire .call-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.call-card.security .call-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.call-icon svg {
  width: 32px;
  height: 32px;
}

.call-card h3 {
  font-size: 20px;
  margin: 0 0 10px 0;
  color: #1e293b;
}

.call-card p {
  color: #64748b;
  font-size: 14px;
  margin: 0 0 20px 0;
  line-height: 1.5;
}

.call-btn {
  display: inline-block;
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 30px;
  font-weight: 600;
  transition: all 0.3s;
}

.call-card:hover .call-btn {
  transform: scale(1.05);
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

.confirm-modal {
  background: white;
  border-radius: 24px;
  padding: 40px;
  max-width: 400px;
  width: 90%;
  text-align: center;
  animation: scaleIn 0.3s;
}

@keyframes scaleIn {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.modal-icon {
  width: 70px;
  height: 70px;
  margin: 0 auto 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.modal-icon.medical {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.modal-icon.fire {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.modal-icon.security {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.modal-icon svg {
  width: 32px;
  height: 32px;
}

.confirm-modal h3 {
  font-size: 22px;
  margin: 0 0 12px 0;
  color: #1e293b;
}

.confirm-modal p {
  margin: 0 0 8px 0;
  color: #64748b;
}

.location-info {
  font-size: 14px !important;
  color: #94a3b8 !important;
}

.modal-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 28px;
}

.btn-cancel {
  padding: 12px 32px;
  background: #f1f5f9;
  border: none;
  border-radius: 12px;
  color: #64748b;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  background: #e2e8f0;
}

.btn-confirm {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  border: none;
  border-radius: 12px;
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-confirm.medical {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.btn-confirm.fire {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.btn-confirm.security {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.btn-confirm svg {
  width: 18px;
  height: 18px;
}

.calling-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.calling-modal {
  text-align: center;
  color: white;
}

.calling-ring {
  position: relative;
  width: 150px;
  height: 150px;
  margin: 0 auto 30px;
}

.ring-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: ring 1.5s infinite;
}

.ring-ring.delay-1 {
  animation-delay: 0.5s;
}

.ring-ring.delay-2 {
  animation-delay: 1s;
}

@keyframes ring {
  0% { transform: translate(-50%, -50%) scale(1); opacity: 1; }
  100% { transform: translate(-50%, -50%) scale(1.5); opacity: 0; }
}

.calling-icon-inner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.calling-icon-inner.medical {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.calling-icon-inner.fire {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.calling-icon-inner.security {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.calling-icon-inner svg {
  width: 32px;
  height: 32px;
}

.calling-modal h3 {
  font-size: 24px;
  margin: 0 0 12px 0;
}

.calling-modal p {
  margin: 0 0 20px 0;
  opacity: 0.8;
}

.calling-timer {
  font-size: 48px;
  font-weight: bold;
  font-family: monospace;
  margin-bottom: 30px;
}

.btn-hangup {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 40px;
  background: #ef4444;
  border: none;
  border-radius: 50px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  margin: 0 auto;
}

.btn-hangup:hover {
  background: #dc2626;
  transform: scale(1.05);
}

.btn-hangup svg {
  width: 20px;
  height: 20px;
}

.history-section {
  padding: 0 24px;
  max-width: 800px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h3 {
  font-size: 18px;
  margin: 0;
  color: #1e293b;
}

.badge {
  padding: 6px 16px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.history-timeline {
  position: relative;
}

.history-timeline::before {
  content: '';
  position: absolute;
  left: 19px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e2e8f0;
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 20px;
  padding: 20px 0;
}

.timeline-dot {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  z-index: 1;
}

.timeline-dot.medical {
  background: rgba(239, 68, 68, 0.1);
  border: 3px solid #ef4444;
}

.timeline-dot.fire {
  background: rgba(245, 158, 11, 0.1);
  border: 3px solid #f59e0b;
}

.timeline-dot.security {
  background: rgba(59, 130, 246, 0.1);
  border: 3px solid #3b82f6;
}

.timeline-content {
  flex: 1;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

.timeline-header {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.type-tag {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.type-tag.medical {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.type-tag.fire {
  background: rgba(245, 158, 11, 0.1);
  color: #d97706;
}

.type-tag.security {
  background: rgba(59, 130, 246, 0.1);
  color: #2563eb;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.pending {
  background: rgba(139, 92, 246, 0.1);
  color: #7c3aed;
}

.status-tag.responding {
  background: rgba(245, 158, 11, 0.1);
  color: #d97706;
}

.status-tag.resolved {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.status-tag.cancelled {
  background: rgba(148, 163, 184, 0.1);
  color: #64748b;
}

.timeline-time {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 5px;
}

.timeline-detail {
  font-size: 13px;
  color: #94a3b8;
}

.timeline-action {
  padding: 10px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s;
}

.timeline-action:hover {
  background: #fef2f2;
  border-color: #ef4444;
  color: #dc2626;
}

.timeline-action svg {
  width: 18px;
  height: 18px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  color: #cbd5e1;
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0 0 8px 0;
  color: #64748b;
}

.empty-state .hint {
  font-size: 14px;
  color: #94a3b8;
}
</style>