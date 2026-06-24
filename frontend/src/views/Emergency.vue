<template>
  <div class="em-page" :class="{ 'em-page--calling': isCalling }">
    <!-- Close / Back button -->
    <button class="em-close" @click="goBack" v-if="!isCalling">
      <AppIcon name="x" size="22" />
    </button>

    <!-- ================================================================
         MAIN SOS VIEW (not calling)
         ================================================================ -->
    <main v-if="!isCalling" class="em-main">
      <!-- Header -->
      <div class="em-hero-text">
        <h1 class="em-title">紧急救援</h1>
        <p class="em-subtitle">如果您需要帮助，请点击下方按钮</p>
      </div>

      <!-- Huge SOS Button with ripple rings -->
      <div class="em-sos-wrap">
        <div class="em-sos-ripple em-sos-ripple--1"></div>
        <div class="em-sos-ripple em-sos-ripple--2"></div>
        <div class="em-sos-ripple em-sos-ripple--3"></div>
        <button class="em-sos-btn" @click="showConfirmModal('medical')">
          <AppIcon name="bell" size="52" class="em-sos-icon" />
          <span class="em-sos-label">SOS</span>
          <span class="em-sos-hint">一键呼叫</span>
        </button>
      </div>

      <!-- Category pills -->
      <div class="em-categories">
        <button class="em-cat" @click="showConfirmModal('medical')">
          <span class="em-cat-icon em-cat-icon--medical">
            <AppIcon name="heart" size="28" />
          </span>
          <span class="em-cat-label">医疗</span>
        </button>
        <button class="em-cat" @click="showConfirmModal('fire')">
          <span class="em-cat-icon em-cat-icon--fire">
            <AppIcon name="bell" size="28" />
          </span>
          <span class="em-cat-label">火灾</span>
        </button>
        <button class="em-cat" @click="showConfirmModal('security')">
          <span class="em-cat-icon em-cat-icon--security">
            <AppIcon name="lock" size="28" />
          </span>
          <span class="em-cat-label">安全</span>
        </button>
      </div>

      <!-- Call history (collapsed at bottom) -->
      <section v-if="callHistory.length > 0" class="em-history">
        <details>
          <summary class="em-history-toggle">
            <AppIcon name="clock" size="14" />
            呼叫记录 ({{ callHistory.length }})
          </summary>
          <div class="em-history-list">
            <div v-for="item in callHistory.slice(0, 10)" :key="item.id" class="em-history-item">
              <span class="em-history-dot" :class="`em-history-dot--${item.callType}`"></span>
              <span class="em-history-type">{{ getCallTypeName(item.callType) }}</span>
              <span class="em-history-status" :class="`em-history-status--${item.status}`">
                {{ getStatusText(item.status) }}
              </span>
              <span class="em-history-time">{{ formatTime(item.callTime) }}</span>
            </div>
          </div>
        </details>
      </section>
    </main>

    <!-- ================================================================
         CONFIRM MODAL
         ================================================================ -->
    <Teleport to="body">
      <transition name="em-modal">
        <div v-if="showCallConfirm" class="em-overlay" @click.self="showCallConfirm = false">
          <div class="em-modal">
            <div class="em-modal-icon" :class="`em-modal-icon--${selectedCallType}`">
              <AppIcon name="bell" size="30" />
            </div>
            <h3>确认呼叫</h3>
            <p>确定要发起<strong>{{ getCallTypeName(selectedCallType) }}</strong>呼叫吗？</p>
            <p class="em-modal-hint">呼叫将立即发送至社区服务中心</p>
            <div class="em-modal-actions">
              <button class="btn btn-secondary" @click="showCallConfirm = false">取消</button>
              <button class="btn btn-danger btn-lg" @click="handleEmergencyCall(selectedCallType)">
                确认呼叫
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- ================================================================
         CALLING OVERLAY
         ================================================================ -->
    <div v-if="isCalling" class="em-calling">
      <div class="em-calling-core">
        <div class="em-calling-rings">
          <div class="em-calling-ring em-calling-ring--1"></div>
          <div class="em-calling-ring em-calling-ring--2"></div>
          <div class="em-calling-ring em-calling-ring--3"></div>
          <div class="em-calling-icon">
            <AppIcon name="bell" size="36" />
          </div>
        </div>
        <h3>{{ getCallTypeName(currentCallType) }}呼叫中…</h3>
        <p>正在连接紧急服务中心</p>
        <div class="em-calling-timer tabular-nums">{{ callingTime }}</div>
        <button class="btn btn-danger" @click="cancelCall">取消呼叫</button>
      </div>
    </div>

    <!-- Background blur orbs -->
    <div class="em-orb em-orb--left"></div>
    <div class="em-orb em-orb--right"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { emergencyAPI } from '../api'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const goBack = () => router.push('/dashboard')

const callHistory = ref([])
const showCallConfirm = ref(false)
const selectedCallType = ref('')
const isCalling = ref(false)
const currentCallType = ref('')
const callingTime = ref('00:00')
let timer = null
let pulseInterval = null

const loadCallHistory = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const response = await emergencyAPI.getByUser(userId)
    if (response.code === 200) callHistory.value = (response.data || []).slice(-10).reverse()
  } catch (error) { /* silent */ }
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
    const callTypeMap = { medical: '医疗', fire: '火灾', security: '安全' }
    const response = await emergencyAPI.create({
      userId: parseInt(userId),
      callType: callTypeMap[callType] || callType,
      description: `${callTypeMap[callType] || callType}紧急呼叫`
    })
    if (response.code === 200) {
      isCalling.value = true
      currentCallType.value = callType
      startTimer()
      ElMessage.success('呼叫已发送')
      loadCallHistory()
    }
  } catch (error) { ElMessage.error('呼叫发送失败') }
}

const startTimer = () => {
  let seconds = 0
  timer = setInterval(() => {
    seconds++
    callingTime.value = `${String(Math.floor(seconds / 60)).padStart(2, '0')}:${String(seconds % 60).padStart(2, '0')}`
  }, 1000)
  // Pulse the calling screen background
  pulseInterval = setInterval(() => {
    document.querySelector('.em-page')?.classList.toggle('em-page--pulse')
  }, 2000)
}

const cancelCall = () => {
  isCalling.value = false
  if (timer) { clearInterval(timer); timer = null }
  if (pulseInterval) { clearInterval(pulseInterval); pulseInterval = null }
  document.querySelector('.em-page')?.classList.remove('em-page--pulse')
  ElMessage.info('呼叫已取消')
}

const getCallTypeName = (t) => ({ medical: '医疗急救', fire: '火灾报警', security: '安全求助' }[t] || t)
const getStatusText = (s) => ({ pending: '待响应', responding: '处理中', resolved: '已处理', cancelled: '已取消' }[s] || s)
const formatTime = (s) => {
  if (!s) return ''
  try { return new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) } catch (e) { return '' }
}

onMounted(() => loadCallHistory())
onUnmounted(() => {
  if (timer) clearInterval(timer)
  if (pulseInterval) clearInterval(pulseInterval)
})
</script>

<style scoped>
/* ============================================================
   PAGE — fullscreen centered
   ============================================================ */
.em-page {
  min-height: 100vh;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  background: var(--color-surface-container-lowest);
  position: relative; overflow: hidden;
  transition: background-color 1s;
  padding: var(--space-lg);
}
.em-page--pulse { background: var(--color-error-container); }

/* Background orbs */
.em-orb {
  position: fixed; border-radius: 50%; pointer-events: none; z-index: 0;
  filter: blur(80px); opacity: 0.15;
}
.em-orb--left {
  top: -20%; left: -10%; width: 50vw; height: 50vw;
  background: var(--color-error);
}
.em-orb--right {
  bottom: -10%; right: -10%; width: 40vw; height: 40vw;
  background: var(--color-secondary);
}

/* Close button */
.em-close {
  position: absolute; top: var(--space-lg); left: var(--space-lg); z-index: 10;
  width: 48px; height: 48px; border-radius: 50%;
  background: rgba(253, 253, 252, 0.8); backdrop-filter: blur(8px);
  border: 1px solid var(--color-outline-variant);
  display: flex; align-items: center; justify-content: center;
  color: var(--color-on-surface-variant); cursor: pointer;
  transition: all 0.2s;
}
.em-close:hover { background: var(--color-surface-container-high); color: var(--color-on-surface); }
.em-close:active { transform: scale(0.95); }

/* ============================================================
   MAIN CONTENT
   ============================================================ */
.em-main {
  position: relative; z-index: 1;
  max-width: 640px; width: 100%;
  display: flex; flex-direction: column; align-items: center;
  gap: var(--space-2xl);
}

/* Hero text */
.em-hero-text { text-align: center; }
.em-title {
  font-size: clamp(36px, 8vw, 56px);
  font-weight: var(--weight-bold);
  letter-spacing: -0.03em;
  color: var(--color-on-surface);
  margin: 0;
}
.em-subtitle {
  font-size: var(--text-body-lg);
  color: var(--color-on-surface-variant);
  margin: var(--space-sm) 0 0;
}

/* ============================================================
   SOS BUTTON — huge, pulsing red
   ============================================================ */
.em-sos-wrap {
  position: relative; width: 240px; height: 240px;
  display: flex; align-items: center; justify-content: center;
}
@media (min-width: 768px) { .em-sos-wrap { width: 280px; height: 280px; } }

/* Ripple rings */
.em-sos-ripple {
  position: absolute; inset: 0; border-radius: 50%;
  background: var(--color-error);
  opacity: 0.15;
  animation: em-sos-pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
.em-sos-ripple--2 { animation-delay: 0.5s; }
.em-sos-ripple--3 { animation-delay: 1s; }

@keyframes em-sos-pulse {
  0% { transform: scale(0.85); opacity: 0.25; }
  100% { transform: scale(1.15); opacity: 0; }
}

/* SOS button core */
.em-sos-btn {
  position: relative; z-index: 1;
  width: 180px; height: 180px;
  border-radius: 50%;
  border: 4px solid var(--color-surface-container-lowest);
  background: var(--color-error);
  color: var(--color-on-error);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  cursor: pointer; font-family: inherit;
  box-shadow: 0 10px 40px rgba(186, 26, 26, 0.5);
  transition: transform 0.3s cubic-bezier(0.32, 0.72, 0, 1), box-shadow 0.3s;
}
@media (min-width: 768px) { .em-sos-btn { width: 200px; height: 200px; } }

.em-sos-btn:hover { transform: scale(1.05); box-shadow: 0 12px 50px rgba(186, 26, 26, 0.6); }
.em-sos-btn:active { transform: scale(0.95); box-shadow: 0 4px 16px rgba(186, 26, 26, 0.5); }

.em-sos-icon { transition: transform 0.3s; }
.em-sos-btn:hover .em-sos-icon { transform: scale(1.1); }

.em-sos-label {
  font-size: clamp(24px, 5vw, 32px);
  font-weight: var(--weight-bold);
  letter-spacing: 0.1em;
}
.em-sos-hint {
  font-size: var(--text-label-sm); opacity: 0.85;
  margin-top: 2px;
}

/* ============================================================
   CATEGORY PILLS
   ============================================================ */
.em-categories {
  display: flex; gap: var(--space-lg);
}
.em-cat {
  display: flex; flex-direction: column; align-items: center; gap: var(--space-sm);
  background: none; border: none; cursor: pointer; font-family: inherit;
  transition: transform 0.2s;
}
.em-cat:hover { transform: translateY(-3px); }
.em-cat:active { transform: scale(0.95); }

.em-cat-icon {
  width: 64px; height: 64px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: box-shadow 0.2s;
}
.em-cat-icon--medical { background: var(--color-tertiary-fixed); color: var(--color-tertiary); }
.em-cat-icon--fire { background: var(--color-error-container); color: var(--color-error); }
.em-cat-icon--security { background: var(--color-secondary-fixed); color: var(--color-secondary); }

.em-cat-label { font-size: var(--text-label-md); font-weight: var(--weight-medium); color: var(--color-on-surface); }

/* ============================================================
   HISTORY (collapsed)
   ============================================================ */
.em-history { width: 100%; max-width: 480px; }
.em-history-toggle {
  display: flex; align-items: center; gap: var(--space-xs);
  font-size: var(--text-label-sm); color: var(--color-on-surface-variant);
  cursor: pointer; padding: var(--space-xs) 0; list-style: none;
}
.em-history-toggle::-webkit-details-marker { display: none; }

.em-history-list {
  display: flex; flex-direction: column; gap: var(--space-xs);
  margin-top: var(--space-sm);
  max-height: 240px; overflow-y: auto;
}
.em-history-item {
  display: flex; align-items: center; gap: var(--space-sm);
  padding: var(--space-xs) var(--space-sm);
  font-size: var(--text-label-sm);
  background: var(--color-surface-container-low);
  border-radius: var(--radius-sm);
}
.em-history-dot {
  width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0;
}
.em-history-dot--medical { background: var(--color-error); }
.em-history-dot--fire { background: var(--color-secondary); }
.em-history-dot--security { background: var(--color-primary); }
.em-history-type { flex: 1; font-weight: var(--weight-medium); }
.em-history-status { font-size: 11px; }
.em-history-status--pending { color: var(--color-secondary); }
.em-history-status--responding { color: var(--color-primary); }
.em-history-status--resolved { color: var(--color-tertiary); }
.em-history-status--cancelled { color: var(--color-on-surface-variant); }
.em-history-time { color: var(--color-on-surface-variant); font-size: 11px; }

/* ============================================================
   CONFIRM MODAL
   ============================================================ */
.em-overlay {
  position: fixed; inset: 0; z-index: var(--z-modal);
  background: rgba(0, 0, 0, 0.4); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
}
.em-modal {
  background: var(--color-surface-container-lowest);
  border-radius: var(--radius-2xl);
  padding: var(--space-2xl);
  max-width: 400px; width: 90%;
  text-align: center;
  box-shadow: 0 24px 64px rgba(0, 0, 0, 0.15);
}
.em-modal-icon {
  width: 64px; height: 64px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto var(--space-md); color: #fff;
}
.em-modal-icon--medical { background: var(--color-error); }
.em-modal-icon--fire { background: var(--color-secondary); }
.em-modal-icon--security { background: var(--color-primary); }
.em-modal h3 { font-size: var(--text-headline-md); margin: 0 0 var(--space-xs); color: var(--color-on-surface); }
.em-modal p { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: 0 0 var(--space-xxs); }
.em-modal-hint { font-size: var(--text-label-sm) !important; margin-top: var(--space-xs); }
.em-modal-actions { display: flex; gap: var(--space-sm); justify-content: center; margin-top: var(--space-xl); }
.em-modal-enter-active { transition: opacity 0.25s ease-out; }
.em-modal-leave-active { transition: opacity 0.15s ease-in; }
.em-modal-enter-from, .em-modal-leave-to { opacity: 0; }

/* ============================================================
   CALLING OVERLAY
   ============================================================ */
.em-calling {
  position: fixed; inset: 0; z-index: 2000;
  background: rgba(0, 0, 0, 0.75); backdrop-filter: blur(12px);
  display: flex; align-items: center; justify-content: center;
}
.em-calling-core { text-align: center; color: #fff; }
.em-calling-rings {
  position: relative; width: 160px; height: 160px;
  margin: 0 auto var(--space-xl);
}
.em-calling-ring {
  position: absolute; inset: 0; border: 2px solid rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  animation: em-ring-pulse 1.5s infinite;
}
.em-calling-ring--2 { animation-delay: 0.5s; }
.em-calling-ring--3 { animation-delay: 1s; }
@keyframes em-ring-pulse {
  0% { transform: scale(0.9); opacity: 1; }
  100% { transform: scale(1.4); opacity: 0; }
}
.em-calling-icon {
  position: absolute; inset: 50%; transform: translate(-50%, -50%);
  width: 72px; height: 72px; border-radius: 50%;
  background: var(--color-error);
  display: flex; align-items: center; justify-content: center;
}
.em-calling-core h3 { font-size: var(--text-headline-md); margin: 0 0 var(--space-xs); }
.em-calling-core p { font-size: var(--text-body-md); opacity: 0.8; margin: 0 0 var(--space-md); }
.em-calling-timer { font-size: 48px; font-family: var(--font-mono); font-weight: var(--weight-bold); margin-bottom: var(--space-xl); }

/* ============================================================
   ACCESSIBILITY
   ============================================================ */
@media (prefers-reduced-motion: reduce) {
  .em-sos-ripple { animation: none; opacity: 0.08; }
  .em-calling-ring { animation: none; opacity: 0.3; }
  .em-page--pulse { background: var(--color-surface-container-lowest); }
}
@media (max-width: 480px) {
  .em-sos-wrap { width: 200px; height: 200px; }
  .em-sos-btn { width: 150px; height: 150px; }
  .em-categories { gap: var(--space-md); }
  .em-cat-icon { width: 56px; height: 56px; }
}
</style>
