<template>
  <div class="eh-page">
    <!-- Back + Elder identity -->
    <header class="eh-hero">
      <button class="btn btn-ghost" @click="$router.push('/family')">
        <AppIcon name="chevron-right" size="18" class="eh-back-icon" /> 返回首页
      </button>

      <div class="eh-hero-body" v-if="!pageLoading">
        <div class="eh-elder-id">
          <div class="eh-elder-avatar" :class="avatarClass">
            {{ elderName.charAt(0) }}
          </div>
          <div>
            <h1 class="eh-elder-name">{{ elderName }}</h1>
            <p class="eh-elder-date">{{ currentDate }}</p>
          </div>
        </div>

        <div class="eh-score" v-if="healthData" :class="scoreClass">
          <span class="eh-score-num tabular-nums">{{ healthScore }}</span>
          <span class="eh-score-lbl">健康分</span>
        </div>
      </div>
    </header>

    <!-- Loading -->
    <div v-if="pageLoading" class="eh-skeleton-grid">
      <div v-for="i in 6" :key="i" class="skeleton" style="height:160px;border-radius:var(--radius-xl)"></div>
    </div>

    <!-- Empty -->
    <div v-else-if="!healthData" class="eh-empty">
      <AppIcon name="clipboard" size="48" class="eh-empty-icon" />
      <h2>暂无健康数据</h2>
      <p>{{ elderName }}还没有录入健康数据。设备连接后，数据将自动同步。</p>
    </div>

    <!-- Data -->
    <template v-else>
      <!-- Vitals grid (3x2) -->
      <section class="eh-section">
        <h2 class="eh-section-title">生命体征</h2>
        <div class="eh-vitals-grid">
          <div
            v-for="v in vitalItems"
            :key="v.label"
            class="eh-vital-card"
            :style="{ '--card-accent': v.accent }"
          >
            <div class="eh-vital-inner">
              <span class="eh-vital-icon" :style="{ color: v.accent }">
                <AppIcon :name="v.icon" size="22" />
              </span>
              <div class="eh-vital-main">
                <span class="eh-vital-value tabular-nums">{{ v.value }}<small>{{ v.unit }}</small></span>
                <span class="eh-vital-label">{{ v.label }}</span>
              </div>
              <span class="eh-vital-status" :class="v.statusClass">{{ v.statusText }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Weekly chart + AI analysis -->
      <section class="eh-section">
        <div class="eh-grid-2">
          <!-- Weekly chart -->
          <div class="eh-panel">
            <h3 class="eh-panel-title">近7天心率趋势</h3>
            <div class="eh-chart" v-if="weeklyData.length > 0">
              <div class="eh-chart-bars">
                <div v-for="(d, i) in weeklyData" :key="i" class="eh-chart-col">
                  <div
                    class="eh-chart-bar"
                    :style="{ height: d.heartRate ? `${Math.min(100, (d.heartRate / 120) * 100)}%` : '0%' }"
                  ></div>
                  <span class="eh-chart-day">{{ d.day }}</span>
                </div>
              </div>
              <p class="eh-chart-hint">单位: bpm</p>
            </div>
            <div v-else class="eh-chart-empty">
              <AppIcon name="pulse" size="28" />
              <p>暂无趋势数据</p>
            </div>
          </div>

          <!-- AI analysis -->
          <div class="eh-panel">
            <h3 class="eh-panel-title">AI 健康分析</h3>
            <div class="eh-analysis" v-if="aiReport">
              <p class="eh-analysis-text">{{ aiReport }}</p>
            </div>
            <div v-else class="eh-analysis-empty">
              <AppIcon name="pulse" size="32" />
              <p>点击按钮生成AI健康分析报告</p>
              <button class="btn btn-primary btn-sm" @click="generateReport" :disabled="generating">
                {{ generating ? '生成中…' : '生成报告' }}
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Warning records -->
      <section class="eh-section" v-if="warnings.length > 0">
        <h2 class="eh-section-title">
          <AppIcon name="bell" size="18" class="eh-section-title-icon--warn" />
          异常记录 ({{ warnings.length }})
        </h2>
        <div class="eh-warnings">
          <div v-for="w in warnings" :key="w.id" class="eh-warn-item">
            <span class="eh-warn-dot"></span>
            <div class="eh-warn-body">
              <span class="eh-warn-date">{{ formatDate(w.measuredAt) }}</span>
              <span class="eh-warn-text">多项指标异常，需要关注</span>
            </div>
            <span class="badge badge-warning">关注</span>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { healthAPI } from '../../api'
import AppIcon from '../../components/AppIcon.vue'

const elderId = ref(parseInt(localStorage.getItem('viewElderId') || '0'))
const elderName = ref(localStorage.getItem('viewElderName') || '老人')
const currentDate = computed(() => new Date().toLocaleDateString('zh-CN', { month: 'long', day: 'numeric', weekday: 'long' }))

const pageLoading = ref(true)
const healthData = ref(null)
const weeklyData = ref([])
const warnings = ref([])
const aiReport = ref('')
const generating = ref(false)

const healthScore = computed(() => {
  if (!healthData.value) return '--'
  const d = healthData.value
  let score = 65; let f = 0
  if (d.heartRate) { f++; if (d.heartRate >= 60 && d.heartRate <= 80) score += 10; else if (d.heartRate >= 50 && d.heartRate <= 100) score += 5; else score -= 5 }
  if (d.bloodPressureHigh && d.bloodPressureLow) { f++; if (d.bloodPressureHigh < 120 && d.bloodPressureLow < 80) score += 12; else if (d.bloodPressureHigh < 130 && d.bloodPressureLow < 85) score += 7; }
  if (d.bloodSugar) { f++; const s = parseFloat(d.bloodSugar); if (s >= 3.9 && s <= 5.6) score += 10; else if (s >= 3.5 && s <= 6.9) score += 5; }
  if (d.sleepHours) { f++; const s = parseFloat(d.sleepHours); if (s >= 7 && s <= 8) score += 8; else if (s >= 6 && s <= 9) score += 4; }
  return Math.max(10, Math.min(100, f > 0 ? score : 60))
})

const scoreClass = computed(() => {
  if (healthScore.value >= 80) return 'eh-score--good'
  if (healthScore.value >= 60) return 'eh-score--fair'
  return 'eh-score--poor'
})

const avatarClass = computed(() => {
  if (healthScore.value >= 80) return 'eh-avatar--good'
  if (healthScore.value >= 60) return 'eh-avatar--fair'
  return 'eh-avatar--poor'
})

// Vital helpers
const vitalStatus = (v, ok) => {
  if (v == null) return { text: '--', cls: '' }
  return ok ? { text: '正常', cls: 'eh-status--good' } : { text: '偏高', cls: 'eh-status--warn' }
}

const d = () => healthData.value || {}
const hrOk = computed(() => d().heartRate >= 60 && d().heartRate <= 80)
const bpOk = computed(() => d().bloodPressureHigh && d().bloodPressureLow && d().bloodPressureHigh < 130 && d().bloodPressureLow < 85)
const sugarOk = computed(() => d().bloodSugar && parseFloat(d().bloodSugar) >= 3.9 && parseFloat(d().bloodSugar) <= 5.6)
const sleepOk = computed(() => d().sleepHours && d().sleepHours >= 7 && d().sleepHours <= 8)

const vitalItems = computed(() => [
  { icon: 'pulse', label: '心率', value: d().heartRate ?? '--', unit: 'bpm', accent: 'var(--color-error)', statusText: vitalStatus(d().heartRate, hrOk.value).text, statusClass: vitalStatus(d().heartRate, hrOk.value).cls },
  { icon: 'droplet', label: '血压', value: d().bloodPressureHigh ? `${d().bloodPressureHigh}/${d().bloodPressureLow}` : '--', unit: 'mmHg', accent: 'var(--color-primary)', statusText: vitalStatus(d().bloodPressureHigh, bpOk.value).text, statusClass: vitalStatus(d().bloodPressureHigh, bpOk.value).cls },
  { icon: 'droplet', label: '血糖', value: d().bloodSugar ?? '--', unit: 'mmol/L', accent: 'var(--color-secondary)', statusText: vitalStatus(d().bloodSugar, sugarOk.value).text, statusClass: vitalStatus(d().bloodSugar, sugarOk.value).cls },
  { icon: 'moon', label: '睡眠', value: d().sleepHours ?? '--', unit: '小时', accent: 'var(--color-primary)', statusText: vitalStatus(d().sleepHours, sleepOk.value).text, statusClass: vitalStatus(d().sleepHours, sleepOk.value).cls },
  { icon: 'thermometer', label: '体温', value: d().bodyTemperature ?? '--', unit: '°C', accent: 'var(--color-secondary)', statusText: '--', statusClass: '' },
  { icon: 'foot', label: '步数', value: (d().steps ?? '--'), unit: '步', accent: 'var(--color-tertiary)', statusText: '--', statusClass: '' }
])

const formatDate = (s) => s ? new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }) : ''

const generateReport = async () => {
  generating.value = true
  aiReport.value = ''
  const token = localStorage.getItem('token')
  try {
    const response = await fetch(`/api/health/analyze/stream/${elderId.value}`, {
      headers: { 'Authorization': token ? `Bearer ${token}` : '' }
    })
    if (!response.ok) throw new Error('HTTP ' + response.status)
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      const text = decoder.decode(value, { stream: true })
      const lines = text.split('\n').filter(l => l.startsWith('data:'))
      for (const line of lines) {
        const content = line.slice(5).trim()
        if (content && content !== '[DONE]') aiReport.value += content
      }
    }
  } catch (e) {
    if (!aiReport.value) ElMessage.error('生成失败')
  }
  generating.value = false
}

onMounted(async () => {
  try {
    const [latestRes, weeklyRes, warnRes] = await Promise.all([
      healthAPI.getLatest(elderId.value),
      healthAPI.getWeekly(elderId.value),
      healthAPI.getWarning(elderId.value)
    ])
    if (latestRes?.code === 200) healthData.value = latestRes.data
    if (weeklyRes?.code === 200 && weeklyRes.data) {
      weeklyData.value = weeklyRes.data.slice(-7).map(d => ({
        day: new Date(d.measuredAt).toLocaleDateString('zh-CN', { weekday: 'short' }),
        heartRate: d.heartRate
      }))
    }
    if (warnRes?.code === 200) warnings.value = warnRes.data || []
  } catch (e) { ElMessage.error('加载失败') }
  pageLoading.value = false
})
</script>

<style scoped>
.eh-page { padding-bottom: var(--space-section); }

/* Header */
.eh-hero { margin-bottom: var(--space-2xl); }
.eh-back-icon { transform: rotate(180deg); }
.eh-hero-body { display: flex; justify-content: space-between; align-items: center; margin-top: var(--space-lg); gap: var(--space-lg); flex-wrap: wrap; }
.eh-elder-id { display: flex; align-items: center; gap: var(--space-md); }
.eh-elder-avatar {
  width: 64px; height: 64px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 26px; font-weight: var(--weight-bold);
}
.eh-avatar--good { background: var(--color-tertiary-fixed); color: var(--color-tertiary); }
.eh-avatar--fair { background: var(--color-secondary-fixed); color: var(--color-secondary); }
.eh-avatar--poor { background: var(--color-error-container); color: var(--color-error); }
.eh-elder-name { font-size: clamp(28px, 4vw, 40px); font-weight: var(--weight-bold); letter-spacing: -0.025em; margin: 0; }
.eh-elder-date { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: var(--space-xxs) 0 0; }

/* Score ring */
.eh-score {
  width: 80px; height: 80px; border-radius: 50%;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  transition: box-shadow 0.4s var(--ease-out);
}
.eh-score--good { box-shadow: 0 0 0 5px var(--color-tertiary), 0 4px 20px rgba(0, 104, 37, 0.12); }
.eh-score--fair { box-shadow: 0 0 0 5px var(--color-secondary), 0 4px 20px rgba(165, 59, 24, 0.12); }
.eh-score--poor { box-shadow: 0 0 0 5px var(--color-error), 0 4px 20px rgba(186, 26, 26, 0.14); }
.eh-score-num { font-size: 28px; font-weight: var(--weight-bold); color: var(--color-on-surface); line-height: 1; }
.eh-score-lbl { font-size: 10px; color: var(--color-on-surface-variant); }

/* Skeleton / Empty */
.eh-skeleton-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--space-lg); }
@media (max-width: 767px) { .eh-skeleton-grid { grid-template-columns: 1fr; } }
.eh-empty { max-width: 480px; text-align: center; padding: var(--space-2xl); border: 1px dashed var(--color-outline-variant); border-radius: var(--radius-2xl); color: var(--color-on-surface-variant); margin-top: var(--space-xl); }
.eh-empty-icon { margin-bottom: var(--space-lg); opacity: 0.35; }
.eh-empty h2 { margin: 0 0 var(--space-xs); color: var(--color-on-surface); font-size: var(--text-headline-md); }

/* Sections */
.eh-section { max-width: 960px; margin-bottom: var(--space-2xl); }
.eh-section-title {
  font-size: var(--text-headline-md); font-weight: var(--weight-semibold);
  color: var(--color-on-surface); margin: 0 0 var(--space-md);
  display: flex; align-items: center; gap: var(--space-sm); letter-spacing: -0.01em;
}
.eh-section-title-icon--warn { color: var(--color-secondary); }

/* Vitals grid (3x2) */
.eh-vitals-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: var(--space-md); }
@media (max-width: 767px) { .eh-vitals-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 480px) { .eh-vitals-grid { grid-template-columns: 1fr; } }

.eh-vital-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  position: relative; overflow: hidden;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}
.eh-vital-card::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 2px;
  background: transparent;
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  transition: background-color 0.3s ease;
}
.eh-vital-card:hover::before { background: var(--card-accent, var(--color-primary)); }
.eh-vital-card:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }

.eh-vital-inner { padding: var(--space-lg); display: flex; flex-direction: column; gap: var(--space-sm); }
.eh-vital-main { flex: 1; }
.eh-vital-icon { margin-bottom: var(--space-xxs); }
.eh-vital-value { font-size: var(--text-headline-lg); font-weight: var(--weight-bold); line-height: 1.2; display: block; }
.eh-vital-value small { font-size: var(--text-label-sm); font-weight: var(--weight-regular); color: var(--color-on-surface-variant); margin-left: 3px; }
.eh-vital-label { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.eh-vital-status { font-size: 11px; font-weight: var(--weight-semibold); align-self: flex-end; }
.eh-status--good { color: var(--color-tertiary); }
.eh-status--warn { color: var(--color-secondary); }
.eh-status--bad { color: var(--color-error); }

/* Grid 2 */
.eh-grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-lg); }
@media (max-width: 767px) { .eh-grid-2 { grid-template-columns: 1fr; } }

.eh-panel {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  padding: var(--space-lg);
  box-shadow: var(--shadow-card);
}
.eh-panel-title { font-size: var(--text-label-md); font-weight: var(--weight-semibold); margin: 0 0 var(--space-md); }

/* Chart */
.eh-chart { padding-top: var(--space-sm); }
.eh-chart-bars { display: flex; align-items: flex-end; gap: var(--space-sm); height: 120px; padding-bottom: var(--space-lg); }
.eh-chart-col { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: flex-end; gap: var(--space-xxs); height: 100%; }
.eh-chart-bar {
  width: 100%; max-width: 32px; min-height: 4px;
  background: linear-gradient(180deg, var(--color-primary) 0%, var(--color-primary-fixed-dim) 100%);
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
  transition: height 0.6s cubic-bezier(0.32, 0.72, 0, 1);
}
.eh-chart-day { font-size: 11px; color: var(--color-on-surface-variant); }
.eh-chart-hint { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); text-align: center; margin: 0; }
.eh-chart-empty { text-align: center; padding: var(--space-lg); color: var(--color-on-surface-variant); }

/* Analysis */
.eh-analysis-text { font-size: var(--text-body-md); line-height: 1.8; color: var(--color-on-surface); }
.eh-analysis-empty { text-align: center; padding: var(--space-lg); color: var(--color-on-surface-variant); }
.eh-analysis-empty p { margin: var(--space-sm) 0 var(--space-md); }

/* Warnings */
.eh-warnings { display: flex; flex-direction: column; gap: var(--space-xs); }
.eh-warn-item {
  display: flex; align-items: center; gap: var(--space-sm);
  padding: var(--space-sm) var(--space-md);
  background: var(--color-error-container);
  border-radius: var(--radius-md);
}
.eh-warn-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--color-error); flex-shrink: 0; }
.eh-warn-body { flex: 1; display: flex; gap: var(--space-sm); font-size: var(--text-label-md); }
.eh-warn-date { color: var(--color-on-surface-variant); white-space: nowrap; }

@media (max-width: 767px) {
  .eh-hero-body { flex-direction: column; align-items: flex-start; }
}
</style>
