<template>
  <div class="fd-page">
    <!-- ================================================================
         HEADER — editorial greeting + status badge
         ================================================================ -->
    <header class="fd-hero">
      <div class="fd-hero-left">
        <h1 class="fd-hero-title">{{ greetingText }}，{{ userName }}</h1>
        <p class="fd-hero-desc" v-if="elders.length > 0">
          {{ elders[0].elderName }}今日情况概览{{ elders.length > 1 ? `，还有${elders.length - 1}位家人` : '' }}
        </p>
        <p class="fd-hero-desc" v-else>加载家人健康数据中…</p>
      </div>
      <div class="fd-status-badge" v-if="!pageLoading && elders.length > 0">
        <span class="fd-status-icon">
          <AppIcon :name="statusIcon" size="22" />
        </span>
        <div>
          <span class="fd-status-label">目前状态</span>
          <span class="fd-status-text" :class="statusColorClass">{{ statusText }}</span>
        </div>
      </div>
    </header>

    <!-- ================================================================
         LOADING SKELETON
         ================================================================ -->
    <div v-if="pageLoading" class="fd-bento">
      <div class="fd-bento-main skeleton" style="height:340px;border-radius:var(--radius-xl)"></div>
      <div class="fd-bento-side skeleton" style="height:340px;border-radius:var(--radius-xl)"></div>
      <div class="fd-bento-main skeleton" style="height:320px;border-radius:var(--radius-xl)"></div>
      <div class="fd-bento-side skeleton" style="height:320px;border-radius:var(--radius-xl)"></div>
    </div>

    <!-- ================================================================
         EMPTY STATE
         ================================================================ -->
    <div v-else-if="elders.length === 0" class="fd-empty">
      <span class="fd-empty-icon"><AppIcon name="people" size="48" /></span>
      <h2>暂未绑定家人</h2>
      <p>联系社区管理员将您的账号与家人账号绑定，即可查看健康数据、代付订单。</p>
      <button class="btn btn-primary" @click="refreshAll">
        <AppIcon name="refresh" size="16" /> 刷新检查
      </button>
    </div>

    <!-- ================================================================
         BENTO GRID — asymmetric 12-col layout
         ================================================================ -->
    <div v-else class="fd-bento">
      <!-- Health Vitals (8 cols) -->
      <section class="fd-bento-main">
        <div class="fd-section-head">
          <h2 class="fd-section-title">
            <AppIcon name="pulse" size="20" class="fd-section-title-icon" />
            健康指标
          </h2>
          <button class="fd-link" @click="viewHealth(firstElder)">
            查看完整报告 <AppIcon name="chevron-right" size="14" />
          </button>
        </div>

        <div class="fd-vitals-grid">
          <article
            v-for="vital in vitalCards"
            :key="vital.key"
            class="fd-vital-card"
            :style="{ '--card-accent': vital.accent }"
          >
            <div class="fd-vital-top">
              <span class="fd-vital-icon-box" :style="{ background: vital.iconBg, color: vital.iconColor }">
                <AppIcon :name="vital.icon" size="18" />
              </span>
              <span class="fd-vital-tag">{{ vital.updated }}</span>
            </div>
            <span class="fd-vital-label">{{ vital.label }}</span>
            <div class="fd-vital-value-row">
              <span class="fd-vital-value tabular-nums">{{ vital.value }}</span>
              <span class="fd-vital-unit">{{ vital.unit }}</span>
            </div>
            <div v-if="vital.progress !== null" class="fd-vital-progress-bar">
              <div class="fd-vital-progress-fill" :style="{ width: vital.progress + '%', background: vital.accent }"></div>
            </div>
            <span class="fd-vital-status" :class="vital.statusClass">
              <AppIcon name="check" size="13" v-if="vital.statusClass === 'fd-status-ok'" />
              {{ vital.status }}
            </span>
          </article>
        </div>
      </section>

      <!-- Upcoming Services (4 cols) -->
      <section class="fd-bento-side">
        <div class="fd-section-head">
          <h2 class="fd-section-title">
            <AppIcon name="clock" size="20" class="fd-section-title-icon fd-section-title-icon--tertiary" />
            即将到来的服务
          </h2>
        </div>

        <div class="fd-services-card">
          <template v-if="upcomingServices.length > 0">
            <div
              v-for="(svc, i) in upcomingServices"
              :key="i"
              class="fd-service-item"
            >
              <span class="fd-service-time">{{ svc.time }}</span>
              <div class="fd-service-info">
                <h3>{{ svc.name }}</h3>
                <p><AppIcon name="map-pin" size="12" />{{ svc.detail }}</p>
              </div>
            </div>
          </template>
          <div v-else class="fd-services-empty">
            <AppIcon name="calendar" size="28" />
            <p>暂无预约服务</p>
          </div>
          <router-link to="/family/services" class="fd-service-add-btn">
            <AppIcon name="star" size="16" /> 预约新服务
          </router-link>
        </div>
      </section>

      <!-- Activity Timeline (8 cols) -->
      <section class="fd-bento-main">
        <h2 class="fd-section-title">
          <AppIcon name="calendar" size="20" class="fd-section-title-icon fd-section-title-icon--secondary" />
          今日动态
        </h2>
        <div class="fd-timeline-card">
          <div class="fd-timeline" v-if="recentActivities.length > 0">
            <div
              v-for="(item, i) in recentActivities"
              :key="i"
              class="fd-timeline-item"
              :style="{ animationDelay: `${0.05 * i}s` }"
            >
              <span class="fd-timeline-dot" :class="`fd-timeline-dot--${item.type}`">
                <AppIcon :name="item.icon" size="12" />
              </span>
              <span class="fd-timeline-time">{{ item.time }}</span>
              <div class="fd-timeline-body">{{ item.text }}</div>
            </div>
          </div>
          <div v-else class="fd-timeline-empty">
            <AppIcon name="clock" size="32" />
            <p>今天还没有新的动态</p>
          </div>
        </div>
      </section>

      <!-- Quick Actions (4 cols) -->
      <section class="fd-bento-side">
        <h2 class="fd-section-title">
          <AppIcon name="star" size="20" class="fd-section-title-icon fd-section-title-icon--secondary" />
          快捷操作
        </h2>
        <div class="fd-actions">
          <div class="fd-actions-grid">
            <router-link to="/family/services" class="fd-action-btn">
              <AppIcon name="star" size="24" />
              <span>预约新服务</span>
            </router-link>
            <button class="fd-action-btn" @click="viewHealth(firstElder)">
              <AppIcon name="clipboard" size="24" />
              <span>完整健康报告</span>
            </button>
          </div>
        </div>
      </section>
    </div>

    <!-- ================================================================
         PAY MODAL
         ================================================================ -->
    <Teleport to="body">
      <transition name="fd-modal">
        <div v-if="showPayModal" class="fd-overlay" @click.self="closePayModal">
          <div class="fd-modal-card">
            <div class="fd-modal-head">
              <h3>为家人代付</h3>
              <button class="btn btn-ghost btn-sm" @click="closePayModal"><AppIcon name="x" size="18" /></button>
            </div>
            <div class="fd-modal-body" v-if="payTarget">
              <div class="fd-modal-elder">
                <span class="fd-modal-avatar">{{ payTarget.elderName?.charAt(0) }}</span>
                <div>
                  <strong>{{ payTarget.elderName }}</strong>
                  <p>{{ payTarget.serviceName }}</p>
                </div>
                <span class="fd-modal-amount">¥{{ payTarget.amount }}</span>
              </div>
              <dl class="fd-modal-dl">
                <div><dt>服务时间</dt><dd>{{ payTarget.serviceTime }}</dd></div>
                <div><dt>地址</dt><dd>{{ payTarget.address }}</dd></div>
                <div><dt>余额</dt><dd :class="{ 'fd-text-warn': walletBalance < payTarget.amount }">¥{{ walletBalance.toFixed(2) }}</dd></div>
              </dl>
            </div>
            <div class="fd-modal-foot">
              <button class="btn btn-secondary" @click="closePayModal">取消</button>
              <button class="btn btn-primary" :disabled="paying || walletBalance < (payTarget?.amount || 0)" @click="handlePay">
                {{ paying ? '支付中…' : walletBalance < (payTarget?.amount || 0) ? '余额不足' : '确认代付' }}
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { relationAPI, healthAPI, orderAPI, paymentAPI, walletAPI } from '../../api'
import AppIcon from '../../components/AppIcon.vue'

const router = useRouter()

const pageLoading = ref(true)
const elders = ref([])
const walletBalance = ref(0)
const familyId = ref(parseInt(localStorage.getItem('userId') || '0'))
const userName = ref(localStorage.getItem('name') || '家属')
const showPayModal = ref(false)
const payTarget = ref(null)
const paying = ref(false)

// Computed
const firstElder = computed(() => elders.value[0] || {})
const greetingText = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 9) return '早上好'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const hasWarnings = (h) => h && (h.warningLevel || 0) > 0
const statusIcon = computed(() => elders.value.some(e => hasWarnings(e.health)) ? 'bell' : 'check')
const statusText = computed(() => elders.value.some(e => hasWarnings(e.health)) ? '有健康预警' : '在家，一切正常')
const statusColorClass = computed(() => elders.value.some(e => hasWarnings(e.health)) ? 'fd-status-warn' : 'fd-status-ok')

const vitalCards = computed(() => {
  const h = firstElder.value?.health
  if (!h) {
    return [
      { key: 'hr', icon: 'pulse', accent: 'var(--color-error)', iconBg: 'var(--color-error-container)', iconColor: 'var(--color-error)', label: '心率', value: '--', unit: 'bpm', updated: '暂无', progress: null, status: '无数据', statusClass: '' },
      { key: 'bp', icon: 'droplet', accent: 'var(--color-primary)', iconBg: 'var(--color-primary-fixed)', iconColor: 'var(--color-primary)', label: '血压', value: '--', unit: 'mmHg', updated: '暂无', progress: null, status: '无数据', statusClass: '' },
      { key: 'steps', icon: 'foot', accent: 'var(--color-tertiary)', iconBg: 'var(--color-tertiary-fixed)', iconColor: 'var(--color-tertiary)', label: '步数', value: '--', unit: '步', updated: '暂无', progress: 0, status: '无数据', statusClass: '' }
    ]
  }
  const hrOk = h.heartRate >= 60 && h.heartRate <= 80
  const bpOk = h.bloodPressureHigh && h.bloodPressureLow && h.bloodPressureHigh < 130 && h.bloodPressureLow < 85
  return [
    { key: 'hr', icon: 'pulse', accent: hrOk ? 'var(--color-tertiary)' : 'var(--color-error)', iconBg: hrOk ? 'var(--color-tertiary-fixed)' : 'var(--color-error-container)', iconColor: hrOk ? 'var(--color-tertiary)' : 'var(--color-error)', label: '心率', value: h.heartRate ?? '--', unit: 'bpm', updated: '刚刚更新', progress: null, status: hrOk ? '稳定' : '偏高', statusClass: hrOk ? 'fd-status-ok' : 'fd-status-warn' },
    { key: 'bp', icon: 'droplet', accent: 'var(--color-primary)', iconBg: 'var(--color-primary-fixed)', iconColor: 'var(--color-primary)', label: '血压', value: h.bloodPressureHigh ? `${h.bloodPressureHigh}/${h.bloodPressureLow}` : '--', unit: 'mmHg', updated: '1小时前', progress: null, status: bpOk ? '正常' : '偏高', statusClass: bpOk ? 'fd-status-ok' : 'fd-status-warn' },
    { key: 'steps', icon: 'foot', accent: 'var(--color-tertiary)', iconBg: 'var(--color-tertiary-fixed)', iconColor: 'var(--color-tertiary)', label: '步数', value: (h.steps || 0).toLocaleString(), unit: '步', updated: '今日累计', progress: Math.min(100, Math.round((h.steps || 0) / 6000 * 100)), status: `目标完成 ${Math.min(100, Math.round((h.steps || 0) / 6000 * 100))}%`, statusClass: '' }
  ]
})

const upcomingServices = computed(() => {
  const pending = []
  elders.value.forEach(e => {
    if (e.latestOrder && e.latestOrder.status === 'pending' && e.latestOrder.paymentStatus !== 'paid') {
      pending.push({
        time: formatTimeShort(e.latestOrder.serviceTime),
        name: e.latestOrder.serviceName || '社区服务',
        detail: `${e.elderName} · ¥${e.latestOrder.amount || 0}`
      })
    }
  })
  return pending.slice(0, 3)
})

const recentActivities = computed(() => {
  const items = []
  elders.value.forEach(e => {
    if (e.health) items.push({ type: 'health', icon: 'pulse', time: '今日', text: `${e.elderName}的健康数据已更新` })
    if (e.recentEmergency) items.push({ type: 'emergency', icon: 'bell', time: formatTimeShort(e.recentEmergency.callTime), text: `紧急呼叫 — ${e.recentEmergency.callType}` })
    if (e.latestOrder) items.push({ type: 'order', icon: 'package', time: formatTimeShort(e.latestOrder.serviceTime), text: `${e.elderName}有待付订单 — ${e.latestOrder.serviceName}` })
  })
  return items.slice(0, 5)
})

// Data loading
const loadElders = async () => {
  try {
    const res = await relationAPI.getByFamilyMember(familyId.value)
    if (res.code === 200 && res.data) {
      elders.value = res.data.map(r => ({
        elderId: r.elderId, elderName: r.elderName || '老人',
        elderPhone: r.elderPhone || '', relationType: r.relationType || '家属',
        health: null, pendingOrders: 0, latestOrder: null, recentEmergency: null
      }))
      await Promise.all(elders.value.map(e => loadElderData(e)))
    }
  } catch (e) { /* silent */ }
  pageLoading.value = false
}

const loadElderData = async (elder) => {
  try {
    const hRes = await healthAPI.getLatest(elder.elderId)
    if (hRes?.code === 200 && hRes.data) elder.health = hRes.data
    const oRes = await orderAPI.getByUser(elder.elderId)
    if (oRes?.code === 200 && oRes.data) {
      const pending = oRes.data.filter(o => o.status === 'pending' && o.paymentStatus !== 'paid')
      elder.pendingOrders = pending.length
      if (pending.length > 0) elder.latestOrder = pending[0]
    }
  } catch (e) { /* silent */ }
}

const loadWallet = async () => {
  try { const res = await walletAPI.getBalance(familyId.value); if (res?.code === 200) walletBalance.value = res.data.balance || 0 } catch (e) { walletBalance.value = 0 }
}

const refreshAll = async () => { pageLoading.value = true; await loadElders(); await loadWallet(); pageLoading.value = false; ElMessage.success('已刷新') }

// Helpers
const formatTimeShort = (t) => {
  if (!t) return ''
  const d = new Date(t)
  if (isNaN(d.getTime())) return ''
  return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// Actions
const viewHealth = (e) => {
  if (!e.elderId) return
  localStorage.setItem('viewElderId', e.elderId); localStorage.setItem('viewElderName', e.elderName); router.push('/family/elder-health')
}
const closePayModal = () => { showPayModal.value = false; payTarget.value = null }
const handlePay = async () => {
  if (!payTarget.value || paying.value) return; paying.value = true
  try {
    const res = await paymentAPI.familyPay(payTarget.value.orderId, familyId.value)
    if (res?.code === 200 && res.data?.success !== false) { ElMessage.success('代付成功'); closePayModal(); refreshAll() }
    else ElMessage.error(res?.data?.message || '余额不足')
  } catch (e) { ElMessage.error('支付失败') }
  paying.value = false
}

onMounted(() => { loadElders(); loadWallet() })
</script>

<style scoped>
/* ============================================================
   PAGE
   ============================================================ */
.fd-page { padding-bottom: var(--space-section); }

/* ============================================================
   HERO — editorial, no metric template
   ============================================================ */
.fd-hero {
  display: flex; justify-content: space-between; align-items: flex-start;
  gap: var(--space-xl); margin-bottom: var(--space-2xl); flex-wrap: wrap;
}
.fd-hero-left { flex: 1; min-width: 280px; }
.fd-hero-title {
  font-size: clamp(28px, 4vw, 40px);
  font-weight: var(--weight-bold);
  letter-spacing: -0.025em;
  line-height: 1.2;
  color: var(--color-on-surface);
  margin: 0 0 var(--space-sm);
  text-wrap: balance;
}
.fd-hero-desc {
  font-size: var(--text-body-lg);
  color: var(--color-on-surface-variant);
  margin: 0;
}

/* Status badge */
.fd-status-badge {
  display: flex; align-items: center; gap: var(--space-md);
  padding: var(--space-md) var(--space-lg);
  background: var(--color-tertiary-fixed);
  border: 1px solid var(--color-tertiary-fixed-dim);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  flex-shrink: 0;
}
.fd-status-icon {
  width: 44px; height: 44px; border-radius: var(--radius-md);
  background: var(--color-tertiary);
  color: var(--color-on-tertiary);
  display: flex; align-items: center; justify-content: center;
}
.fd-status-label { display: block; font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fd-status-text { font-size: var(--text-headline-md); font-weight: var(--weight-semibold); }
.fd-status-ok { color: var(--color-tertiary); }
.fd-status-warn { color: var(--color-error); }

/* ============================================================
   BENTO GRID — 12-col asymmetric
   ============================================================ */
.fd-bento {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: var(--space-lg);
}
.fd-bento-main { grid-column: span 8; }
.fd-bento-side { grid-column: span 4; }

@media (max-width: 1023px) {
  .fd-bento-main, .fd-bento-side { grid-column: span 12; }
}

/* ============================================================
   SECTION HEAD
   ============================================================ */
.fd-section-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-md); }
.fd-section-title {
  font-size: var(--text-headline-md); font-weight: var(--weight-semibold);
  color: var(--color-on-surface); margin: 0 0 var(--space-md);
  display: flex; align-items: center; gap: var(--space-sm);
  letter-spacing: -0.01em;
}
.fd-section-title-icon { color: var(--color-primary); }
.fd-section-title-icon--tertiary { color: var(--color-secondary); }
.fd-section-title-icon--secondary { color: var(--color-on-surface-variant); }
.fd-link {
  font-size: var(--text-label-md); font-weight: var(--weight-medium);
  color: var(--color-primary); background: none; border: none;
  cursor: pointer; display: flex; align-items: center; gap: 4px;
  font-family: inherit; transition: color 0.2s;
}
.fd-link:hover { color: var(--color-primary-hover); }

/* ============================================================
   VITAL CARDS — asymmetric accent strip on hover
   ============================================================ */
.fd-vitals-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--space-md); }
@media (max-width: 767px) { .fd-vitals-grid { grid-template-columns: 1fr; } }

.fd-vital-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  padding: var(--space-lg);
  box-shadow: var(--shadow-card);
  position: relative; overflow: hidden;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  display: flex; flex-direction: column; gap: var(--space-sm);
}
.fd-vital-card::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 2px;
  background: transparent;
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  transition: background-color 0.3s ease;
}
.fd-vital-card:hover::before { background: var(--card-accent, var(--color-primary)); }
.fd-vital-card:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }

.fd-vital-top { display: flex; justify-content: space-between; align-items: flex-start; }
.fd-vital-icon-box {
  width: 36px; height: 36px; border-radius: var(--radius-md);
  display: flex; align-items: center; justify-content: center;
}
.fd-vital-tag {
  font-size: 11px; color: var(--color-on-surface-variant);
  background: var(--color-surface-container); padding: 2px 8px;
  border-radius: var(--radius-sm);
}
.fd-vital-label { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); font-weight: var(--weight-medium); }
.fd-vital-value-row { display: flex; align-items: baseline; gap: var(--space-xs); }
.fd-vital-value { font-size: var(--text-display-lg); font-weight: var(--weight-bold); color: var(--color-on-surface); line-height: 1.1; }
.fd-vital-unit { font-size: var(--text-body-md); color: var(--color-on-surface-variant); }
.fd-vital-progress-bar {
  width: 100%; height: 6px; border-radius: 3px;
  background: var(--color-surface-container); overflow: hidden;
}
.fd-vital-progress-fill { height: 100%; border-radius: 3px; transition: width 0.6s cubic-bezier(0.32, 0.72, 0, 1); }
.fd-vital-status {
  font-size: var(--text-label-sm); display: flex; align-items: center; gap: 4px;
  color: var(--color-on-surface-variant);
}
.fd-status-ok { color: var(--color-tertiary); }
.fd-status-warn { color: var(--color-error); }

/* ============================================================
   SERVICES CARD
   ============================================================ */
.fd-services-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}
.fd-service-item {
  display: flex; gap: var(--space-md); align-items: flex-start;
  padding: var(--space-md); transition: background 0.2s;
}
.fd-service-item:hover { background: var(--color-surface-container-low); }
.fd-service-item + .fd-service-item { border-top: 1px solid var(--color-outline-variant); }
.fd-service-time {
  font-size: var(--text-label-sm); font-weight: var(--weight-bold);
  color: var(--color-secondary); background: var(--color-secondary-fixed);
  padding: var(--space-xs) var(--space-sm); border-radius: var(--radius-md);
  min-width: 52px; text-align: center; flex-shrink: 0;
}
.fd-service-info { flex: 1; min-width: 0; }
.fd-service-info h3 { font-size: var(--text-label-md); font-weight: var(--weight-semibold); margin: 0 0 2px; }
.fd-service-info p { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin: 0; display: flex; align-items: center; gap: 4px; }
.fd-services-empty { text-align: center; padding: var(--space-xl); color: var(--color-on-surface-variant); }
.fd-services-empty p { margin-top: var(--space-sm); }
.fd-service-add-btn {
  display: flex; align-items: center; justify-content: center; gap: var(--space-xs);
  padding: var(--space-sm); margin: var(--space-sm); border-radius: var(--radius-md);
  border: 1px solid var(--color-outline-variant);
  font-size: var(--text-label-md); font-weight: var(--weight-medium);
  color: var(--color-primary); text-decoration: none;
  font-family: inherit; cursor: pointer; transition: all 0.2s;
}
.fd-service-add-btn:hover { background: var(--color-primary-fixed); }

/* ============================================================
   TIMELINE
   ============================================================ */
.fd-timeline-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  padding: var(--space-lg);
  box-shadow: var(--shadow-card);
}
.fd-timeline {
  position: relative; padding-left: var(--space-xl);
  border-left: 2px solid var(--color-outline-variant);
  margin-left: var(--space-sm);
  display: flex; flex-direction: column; gap: var(--space-xl);
}
.fd-timeline-item {
  position: relative;
  opacity: 0;
  animation: fd-fade-up 0.45s cubic-bezier(0.32, 0.72, 0, 1) forwards;
}
@keyframes fd-fade-up { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

.fd-timeline-dot {
  position: absolute; left: calc(-1 * var(--space-xl) - 11px);
  width: 22px; height: 22px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 3px solid var(--color-surface-container-lowest);
  color: var(--color-on-primary);
}
.fd-timeline-dot--health { background: var(--color-tertiary); }
.fd-timeline-dot--order { background: var(--color-primary); }
.fd-timeline-dot--emergency { background: var(--color-error); }
.fd-timeline-time { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); display: block; margin-bottom: 4px; }
.fd-timeline-body {
  background: var(--color-surface-container-low); padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md); font-size: var(--text-body-md);
}
.fd-timeline-empty { text-align: center; padding: var(--space-xl); color: var(--color-on-surface-variant); }
.fd-timeline-empty p { margin-top: var(--space-sm); }

/* ============================================================
   QUICK ACTIONS
   ============================================================ */
.fd-actions { display: flex; flex-direction: column; gap: var(--space-md); }

.fd-actions-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-md); }
.fd-action-btn {
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: var(--space-sm);
  padding: var(--space-lg) var(--space-sm); border-radius: var(--radius-xl);
  border: 1px solid var(--color-outline-variant);
  background: var(--color-surface-container-lowest);
  font-size: var(--text-label-sm); font-weight: var(--weight-medium);
  color: var(--color-primary); text-decoration: none;
  font-family: inherit; cursor: pointer;
  box-shadow: var(--shadow-card);
  transition: all 0.25s cubic-bezier(0.32, 0.72, 0, 1);
}
.fd-action-btn:hover { background: var(--color-primary-fixed); border-color: var(--color-primary); transform: translateY(-2px); }
.fd-action-btn:active { transform: translateY(1px); }

/* ============================================================
   EMPTY
   ============================================================ */
.fd-empty { max-width: 480px; margin-top: var(--space-xl); text-align: center; padding: var(--space-2xl); border: 1px dashed var(--color-outline-variant); border-radius: var(--radius-2xl); color: var(--color-on-surface-variant); }
.fd-empty-icon { margin-bottom: var(--space-lg); opacity: 0.35; display: block; }
.fd-empty h2 { margin: 0 0 var(--space-sm); color: var(--color-on-surface); font-size: var(--text-headline-md); }
.fd-empty p { margin: 0 auto var(--space-xl); max-width: 42ch; font-size: var(--text-body-md); line-height: 1.7; }

/* ============================================================
   MODAL
   ============================================================ */
.fd-overlay {
  position: fixed; inset: 0; z-index: var(--z-modal);
  background: rgba(0, 0, 0, 0.25); backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center;
}
.fd-modal-card {
  width: 90%; max-width: 420px;
  background: var(--color-surface-container-lowest);
  border-radius: var(--radius-2xl);
  box-shadow: 0 24px 64px rgba(0, 47, 112, 0.12);
  overflow: hidden;
}
.fd-modal-head { display: flex; justify-content: space-between; align-items: center; padding: var(--space-lg) var(--space-xl); border-bottom: 1px solid var(--color-outline-variant); }
.fd-modal-head h3 { margin: 0; font-size: var(--text-headline-md); }
.fd-modal-body { padding: var(--space-xl); }
.fd-modal-foot { display: flex; gap: var(--space-sm); justify-content: flex-end; padding: var(--space-lg) var(--space-xl); border-top: 1px solid var(--color-outline-variant); }
.fd-modal-elder { display: flex; align-items: center; gap: var(--space-md); margin-bottom: var(--space-lg); padding: var(--space-md); background: var(--color-surface-container-low); border-radius: var(--radius-lg); }
.fd-modal-elder strong { display: block; }
.fd-modal-elder p { margin: 2px 0 0; font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fd-modal-avatar { width: 44px; height: 44px; border-radius: 50%; background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); display: flex; align-items: center; justify-content: center; font-size: 18px; font-weight: var(--weight-bold); flex-shrink: 0; }
.fd-modal-amount { font-size: var(--text-headline-md); font-weight: var(--weight-bold); color: var(--color-primary); margin-left: auto; }
.fd-modal-dl { display: flex; flex-direction: column; gap: var(--space-sm); }
.fd-modal-dl div { display: flex; justify-content: space-between; padding: var(--space-xs) 0; border-bottom: 1px solid var(--color-outline-variant); font-size: var(--text-body-md); }
.fd-modal-dl div:last-child { border-bottom: none; }
.fd-modal-dl dt { color: var(--color-on-surface-variant); }
.fd-modal-dl dd { font-weight: var(--weight-medium); }
.fd-text-warn { color: var(--color-error) !important; }

.fd-modal-enter-active { transition: opacity 0.3s var(--ease-out); }
.fd-modal-leave-active { transition: opacity 0.2s var(--ease-out); }
.fd-modal-enter-from, .fd-modal-leave-to { opacity: 0; }

/* ============================================================
   RESPONSIVE
   ============================================================ */
@media (max-width: 767px) {
  .fd-hero { flex-direction: column; }
  .fd-vitals-grid { grid-template-columns: 1fr; }
  .fd-modal-card { max-width: 100%; border-radius: var(--radius-xl) var(--radius-xl) 0 0; align-self: flex-end; }
}
</style>
