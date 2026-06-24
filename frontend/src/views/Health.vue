<template>
  <div class="health-page">
    <!-- Header -->
    <header class="health-header">
      <button class="btn-back" @click="goBack"><AppIcon name="x" size="22" /></button>
      <div class="health-header-text">
        <h1 class="text-headline-lg">健康监测</h1>
        <p class="text-body-md">实时追踪您的健康数据</p>
      </div>
      <button class="btn btn-ghost" @click="refreshData" :disabled="loading">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18" :class="{ 'health-spin': loading }">
          <polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/>
          <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
        </svg>
        {{ loading ? '刷新中…' : '刷新数据' }}
      </button>
    </header>

    <!-- Loading skeleton -->
    <template v-if="loading && !hasData">
      <div class="health-skeleton-ring"></div>
      <div class="grid grid-4"><div v-for="i in 4" :key="i" class="skeleton" style="height:140px;border-radius:var(--radius-xl)"></div></div>
    </template>

    <!-- Empty state: no data ever recorded -->
    <div v-else-if="!hasData && !loading" class="health-empty">
      <div class="health-empty-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="56" height="56">
          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
        </svg>
      </div>
      <h2 class="text-headline-md">还没有健康数据</h2>
      <p class="text-body-lg">去<a href="/health-input" class="health-empty-link">数据录入</a>页面记录您的第一条健康数据，<br/>我们将为您生成专属健康分析。</p>
    </div>

    <!-- Data view -->
    <template v-else>
      <!-- Hero: Health score ring -->
      <section class="health-hero">
        <div class="health-hero-ring">
          <svg viewBox="0 0 160 160">
            <defs>
              <linearGradient id="scoreGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                <stop offset="0%" stop-color="var(--color-tertiary)" />
                <stop offset="40%" stop-color="var(--color-tertiary-fixed-dim)" />
                <stop offset="70%" stop-color="var(--color-secondary-fixed-dim)" />
                <stop offset="100%" stop-color="var(--color-error)" />
              </linearGradient>
            </defs>
            <!-- Track -->
            <circle cx="80" cy="80" r="68" fill="none" stroke="var(--color-surface-container-high)" stroke-width="12"/>
            <!-- Progress arc (rotated: 0 starts from top) -->
            <circle
              cx="80" cy="80" r="68"
              fill="none"
              stroke="url(#scoreGradient)"
              stroke-width="12"
              stroke-linecap="round"
              :stroke-dasharray="ringCircumference"
              :stroke-dashoffset="scoreOffset"
              transform="rotate(-90 80 80)"
              class="health-ring-arc"
            />
          </svg>
          <div class="health-hero-score">
            <span class="health-hero-num" :class="scoreLevelClass">{{ hasData ? healthScore : '--' }}</span>
            <span class="health-hero-label">健康评分</span>
          </div>
        </div>
        <div class="health-hero-verdict">
          <p class="health-hero-verdict-text">{{ scoreVerdict }}</p>
          <div class="health-hero-meta">
            <span>更新于 {{ lastUpdated }}</span>
            <span class="health-status-dot" :class="scoreLevelClass"></span>
          </div>
        </div>
      </section>

      <!-- Vital signs grid -->
      <section class="health-section">
        <h3 class="health-section-title">生命体征</h3>
        <div class="health-vitals">
          <div v-for="vital in vitals" :key="vital.label" class="health-vital" :class="'health-vital--' + vital.tone">
            <div class="health-vital-ring" :class="'health-vital-ring--' + vital.tone">
              <AppIcon :name="vital.icon" :size="24" class="health-vital-icon" />
            </div>
            <div class="health-vital-body">
              <div class="health-vital-value">
                {{ vital.value }}<small>{{ vital.unit }}</small>
              </div>
              <div class="health-vital-label">{{ vital.label }}</div>
              <div class="health-vital-range">{{ vital.range }}</div>
            </div>
            <div v-if="vital.trend" class="health-vital-trend" :class="'health-vital-trend--' + vital.trendDir">
              <span>{{ vital.trend }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Two-column: Analysis + Tips -->
      <section class="health-section">
        <div class="health-two-col">
          <!-- AI Analysis -->
          <div class="card">
            <div class="card-header">
              <h3 class="card-title">AI 健康分析</h3>
              <button class="btn btn-primary btn-sm" @click="generateReport" :disabled="generating">
                {{ generating ? '分析中…' : (reportGenerated ? '重新生成' : '生成报告') }}
              </button>
            </div>
            <div class="health-analysis-body">
              <div v-if="!reportGenerated && !generating" class="health-analysis-empty">
                <AppIcon name="pulse" :size="36" class="health-analysis-empty-icon" />
                <p>AI 将根据您的健康数据生成个性化分析与建议</p>
              </div>
              <div v-else-if="generating" class="health-analysis-loading">
                <span class="health-typing-dot"></span>
                <span class="health-typing-dot"></span>
                <span class="health-typing-dot"></span>
              </div>
              <div v-else class="health-analysis-result">
                <div class="health-analysis-meta">
                  <span class="badge badge-neutral">{{ reportDate }}</span>
                  <span class="badge badge-success">分析完成</span>
                </div>
                <div class="health-analysis-text">{{ healthReport }}</div>
              </div>
            </div>
          </div>

          <!-- Personalized tips -->
          <div class="card">
            <div class="card-header"><h3 class="card-title">个性化建议</h3></div>
            <div class="health-tips-list">
              <div v-for="tip in activeTips" :key="tip.label" class="health-tip-item">
                <AppIcon :name="tip.icon" :size="18" class="health-tip-icon" />
                <div class="health-tip-body">
                  <div class="health-tip-label">{{ tip.label }}</div>
                  <div class="health-tip-text">{{ tip.text }}</div>
                </div>
                <span v-if="tip.alert" class="health-tip-alert" :class="'health-tip-alert--' + tip.alert">●</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Upload -->
      <section class="health-section">
        <div class="card">
          <div class="card-header">
            <div>
              <h3 class="card-title">上传健康报告</h3>
              <p class="health-upload-hint">支持 JPG、PNG、PDF，单文件不超过 10MB</p>
            </div>
          </div>

          <div class="health-upload-zone" @click="selectFiles" @dragover.prevent @drop.prevent="handleDrop" :class="{ 'health-upload-zone--over': dragOver }" @dragenter.prevent="dragOver = true" @dragleave="dragOver = false">
            <input ref="fileInput" type="file" multiple accept="image/jpeg,image/png,application/pdf" @change="handleFileSelect" hidden />
            <span class="health-upload-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="36" height="36"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
            </span>
            <p>点击或拖拽文件到此处</p>
          </div>

          <div v-if="uploadedFiles.length" class="health-files">
            <div v-for="(file, i) in uploadedFiles" :key="i" class="health-file-row">
              <AppIcon :name="file.type.startsWith('image') ? 'palette' : 'clipboard'" :size="20" class="health-file-icon" />
              <div class="health-file-info">
                <span class="health-file-name">{{ file.name }}</span>
                <span class="health-file-size">{{ formatFileSize(file.size) }}</span>
              </div>
              <span class="badge" :class="fileStatusBadge(file.status)">{{ fileStatusText(file.status) }}</span>
              <button v-if="file.status !== 'uploading'" class="btn btn-ghost btn-sm" @click.stop="removeFile(i)"><AppIcon name="x" :size="14" /></button>
            </div>
          </div>

          <div v-if="uploadedFiles.length" class="health-upload-actions">
            <button class="btn btn-primary" @click="uploadAllFiles" :disabled="isUploading">
              {{ isUploading ? '上传中…' : '上传全部' }}
            </button>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { healthAPI } from '../api'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const goBack = () => router.push('/dashboard')

const latestData = ref(null)
const loading = ref(true)
const generating = ref(false)
const dragOver = ref(false)
const fileInput = ref(null)
const uploadedFiles = ref([])
const isUploading = ref(false)

const ringCircumference = 2 * Math.PI * 68

const hasData = computed(() => {
  const d = latestData.value
  if (!d) return false
  return !!(d.heartRate || d.bloodPressureHigh || d.bloodSugar || d.sleepHours)
})

const healthScore = computed(() => {
  const d = latestData.value
  if (!d || !hasData.value) return 0
  let score = 65
  let factors = 0
  if (d.heartRate) {
    factors++
    if (d.heartRate >= 60 && d.heartRate <= 80) score += 10
    else if (d.heartRate >= 50 && d.heartRate <= 100) score += 5
    else score -= 5
  }
  if (d.bloodPressureHigh && d.bloodPressureLow) {
    factors++
    const sys = d.bloodPressureHigh, dia = d.bloodPressureLow
    if (sys < 120 && dia < 80) score += 12
    else if (sys < 130 && dia < 85) score += 8
    else if (sys < 140 && dia < 90) score += 4
    else score -= 4
  }
  if (d.bloodSugar) {
    factors++
    const s = parseFloat(d.bloodSugar)
    if (s >= 3.9 && s <= 5.6) score += 10
    else if (s >= 3.5 && s <= 6.9) score += 5
    else score -= 5
  }
  if (d.sleepHours) {
    factors++
    const s = parseFloat(d.sleepHours)
    if (s >= 7 && s <= 8) score += 8
    else if (s >= 6 && s <= 9) score += 4
    else score -= 4
  }
  if (d.steps) {
    factors++
    if (d.steps >= 5000) score += 5
    else if (d.steps >= 2000) score += 2
  }
  if (d.bodyTemperature) {
    factors++
    const t = parseFloat(d.bodyTemperature)
    if (t >= 36.0 && t <= 37.2) score += 5
    else if (t >= 35.5 && t <= 38.0) score += 2
    else score -= 3
  }
  return Math.max(10, Math.min(100, factors > 0 ? score : 60))
})

const scoreOffset = computed(() => ringCircumference - (healthScore.value / 100) * ringCircumference)

const scoreLevelClass = computed(() => {
  const s = healthScore.value
  if (s >= 80) return 'health-level--good'
  if (s >= 60) return 'health-level--fair'
  return 'health-level--poor'
})

const scoreVerdict = computed(() => {
  const s = healthScore.value
  if (s >= 85) return '您的健康状况良好，各项指标均在理想范围'
  if (s >= 70) return '整体状况不错，部分指标可进一步优化'
  if (s >= 55) return '请注意关注异常指标，建议咨询医生'
  return '建议尽快进行健康检查，关注关键指标'
})

const lastUpdated = computed(() => {
  if (!latestData.value?.measuredAt) return '暂无'
  return new Date(latestData.value.measuredAt).toLocaleDateString('zh-CN', { month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' })
})

const vitals = computed(() => {
  const d = latestData.value || {}
  return [
    {
      icon: 'heart', label: '心率', value: d.heartRate ?? '--', unit: 'bpm',
      range: '60-100', tone: 'rose',
      trend: d.heartRate ? (d.heartRate <= 80 ? '理想' : '关注') : '',
      trendDir: d.heartRate && d.heartRate <= 80 ? 'up' : 'down'
    },
    {
      icon: 'droplet', label: '血压',
      value: d.bloodPressureHigh ? `${d.bloodPressureHigh}/${d.bloodPressureLow}` : '--',
      unit: 'mmHg', range: '<130/80', tone: 'blue',
      trend: d.bloodPressureHigh ? (d.bloodPressureHigh < 120 && d.bloodPressureLow < 80 ? '理想' : '关注') : '',
      trendDir: d.bloodPressureHigh && d.bloodPressureHigh < 120 ? 'up' : 'down'
    },
    {
      icon: 'pill', label: '血糖', value: d.bloodSugar ?? '--', unit: 'mmol/L',
      range: '3.9-6.1', tone: 'amber',
      trend: d.bloodSugar ? (parseFloat(d.bloodSugar) <= 6.1 ? '正常' : '偏高') : '',
      trendDir: d.bloodSugar && parseFloat(d.bloodSugar) <= 6.1 ? 'up' : 'down'
    },
    {
      icon: 'moon', label: '睡眠', value: d.sleepHours ?? '--', unit: '小时',
      range: '7-9h', tone: 'violet',
      trend: d.sleepHours ? (d.sleepHours >= 7 ? '充足' : '不足') : '',
      trendDir: d.sleepHours && d.sleepHours >= 7 ? 'up' : 'down'
    }
  ]
})

const activeTips = computed(() => {
  const d = latestData.value || {}
  const tips = []
  // Dynamic tips based on actual data
  if (!d.heartRate) {
    tips.push({ icon: 'heart', label: '记录心率', text: '建议录入心率数据以追踪心脏健康状况', alert: 'warning' })
  } else if (d.heartRate > 100) {
    tips.push({ icon: 'heart', label: '心率偏高', text: `当前心率 ${d.heartRate} bpm，建议休息后复测，持续偏高请就医`, alert: 'danger' })
  } else if (d.heartRate < 55) {
    tips.push({ icon: 'heart', label: '心率偏低', text: `当前心率 ${d.heartRate} bpm，如伴随头晕乏力请就医`, alert: 'warning' })
  } else {
    tips.push({ icon: 'heart', label: '心率正常', text: '心率处于健康范围，保持规律运动有助于维持良好状态' })
  }
  if (d.bloodPressureHigh && (d.bloodPressureHigh >= 130 || d.bloodPressureLow >= 85)) {
    tips.push({ icon: 'droplet', label: '血压关注', text: '建议低盐饮食、控制体重、定期监测血压变化', alert: 'warning' })
  } else {
    tips.push({ icon: 'droplet', label: '血压管理', text: '每日限盐 5g 以内，多吃深色蔬菜，保持心情舒畅' })
  }
  if (d.bloodSugar && parseFloat(d.bloodSugar) > 6.1) {
    tips.push({ icon: 'pill', label: '血糖偏高', text: '控制碳水化合物摄入，餐后适当散步，定期复查糖化血红蛋白', alert: 'warning' })
  } else {
    tips.push({ icon: 'pill', label: '血糖建议', text: '主食粗细搭配，少食多餐，避免高糖饮料和精细碳水' })
  }
  if (d.sleepHours && d.sleepHours < 6) {
    tips.push({ icon: 'moon', label: '睡眠不足', text: '睡前远离手机、调暗灯光、保持卧室凉爽安静', alert: 'danger' })
  } else {
    tips.push({ icon: 'moon', label: '睡眠建议', text: '固定入睡和起床时间，午后避免咖啡因摄入' })
  }
  if (d.steps && d.steps < 3000) {
    tips.push({ icon: 'foot', label: '活动不足', text: '今日步数偏少，建议餐后散步 20 分钟或做轻度家务', alert: 'warning' })
  } else if (d.steps && d.steps >= 5000) {
    tips.push({ icon: 'foot', label: '活动良好', text: `今日 ${d.steps} 步，达到了推荐活动量，继续保持` })
  }
  return tips.slice(0, 5)
})

const healthReport = ref('')
const reportGenerated = ref(false)
const reportDate = ref('')

const loadData = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) { loading.value = false; return }
  loading.value = true
  try {
    const res = await healthAPI.getLatest(userId)
    if (res.code === 200) latestData.value = res.data
  } catch (e) { /* silent */ }
  loading.value = false
}

const generateReport = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  generating.value = true
  healthReport.value = ''
  reportGenerated.value = true
  reportDate.value = new Date().toLocaleDateString('zh-CN')
  const token = localStorage.getItem('token')
  try {
    const response = await fetch(`/api/health/analyze/stream/${userId}`, {
      headers: { 'Authorization': token ? `Bearer ${token}` : '' }
    })
    if (!response.ok) throw new Error('HTTP ' + response.status)
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let leftover = ''  // 跨 chunk 的残行缓冲
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      const text = leftover + decoder.decode(value, { stream: true })
      const lines = text.split('\n')
      // 最后一行可能不完整，留给下一轮
      leftover = lines.pop() || ''
      for (const line of lines) {
        if (!line.startsWith('data:')) continue
        const content = line.slice(5).trim()
        if (!content || content === '[DONE]') continue
        if (content.startsWith('[ERROR]')) {
          const errMsg = content.slice(7).trim()
          ElMessage.error('AI分析失败：' + errMsg)
          reportGenerated.value = false
          continue
        }
        healthReport.value += content
      }
    }
  } catch (e) {
    if (!healthReport.value) { ElMessage.error('生成报告失败'); reportGenerated.value = false }
  }
  generating.value = false
}

const refreshData = async () => { await loadData(); ElMessage.success('数据已刷新') }

// Upload
const selectFiles = () => fileInput.value?.click()
const handleFileSelect = (e) => { addFiles(Array.from(e.target.files)); e.target.value = '' }
const handleDrop = (e) => { dragOver.value = false; addFiles(Array.from(e.dataTransfer.files)) }
const addFiles = (files) => {
  files.forEach(f => {
    if (f.size > 10 * 1024 * 1024) { ElMessage.error(`${f.name} 超过 10MB`); return }
    uploadedFiles.value.push({ file: f, name: f.name, size: f.size, type: f.type, status: 'pending', preview: f.type.startsWith('image') ? URL.createObjectURL(f) : null })
  })
}
const removeFile = (i) => {
  if (uploadedFiles.value[i].preview) URL.revokeObjectURL(uploadedFiles.value[i].preview)
  uploadedFiles.value.splice(i, 1)
}
const formatFileSize = (b) => b < 1024 ? `${b} B` : b < 1024 * 1024 ? `${(b / 1024).toFixed(1)} KB` : `${(b / (1024 * 1024)).toFixed(1)} MB`
const fileStatusBadge = (s) => ({ pending: 'badge-warning', uploading: 'badge-info', success: 'badge-success', error: 'badge-danger' }[s] || 'badge-neutral')
const fileStatusText = (s) => ({ pending: '待上传', uploading: '上传中', success: '成功', error: '失败' }[s] || s)
const uploadAllFiles = async () => {
  isUploading.value = true
  const pending = uploadedFiles.value.filter(f => f.status === 'pending')
  for (const f of pending) {
    f.status = 'uploading'
    try {
      const fd = new FormData(); fd.append('file', f.file)
      const res = await axios.post('/api/upload/health-report', fd)
      f.status = res.data?.code === 200 ? 'success' : 'error'
    } catch (e) { f.status = 'error' }
  }
  ElMessage.success(`上传完成：${uploadedFiles.value.filter(f => f.status === 'success').length}/${pending.length}`)
  isUploading.value = false
}

onMounted(() => loadData())
</script>

<style scoped>
.health-page { min-height: 100vh; background: var(--color-surface); padding: var(--space-lg); }

/* Header */
.health-header { display: flex; align-items: center; gap: var(--space-md); margin-bottom: var(--space-xl); }
.health-header-text { flex: 1; }
.health-header-text h1 { margin: 0; }
.health-header-text p { margin: var(--space-xxs) 0 0; color: var(--color-on-surface-variant); }
.health-spin { animation: health-spin 1s linear infinite; }
@keyframes health-spin { to { transform: rotate(360deg); } }

/* Empty state */
.health-empty { text-align: center; padding: var(--space-section-lg) var(--space-lg); }
.health-empty-icon { width: 96px; height: 96px; border-radius: 50%; background: var(--color-primary-fixed); color: var(--color-primary); display: flex; align-items: center; justify-content: center; margin: 0 auto var(--space-lg); }
.health-empty h2 { margin-bottom: var(--space-sm); }
.health-empty p { color: var(--color-on-surface-variant); max-width: 36ch; margin: 0 auto; }
.health-empty-link { font-weight: var(--weight-semibold); }

/* Skeleton */
.health-skeleton-ring { width: 160px; height: 160px; border-radius: 50%; margin: 0 auto var(--space-xl); }

/* Hero score */
.health-hero { display: flex; align-items: center; gap: var(--space-xl); padding: var(--space-xl); background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); margin-bottom: var(--space-xl); box-shadow: var(--shadow-card); }
.health-hero:hover { transform: none; }
.health-hero-ring { position: relative; width: 160px; height: 160px; flex-shrink: 0; }
.health-hero-ring svg { width: 100%; height: 100%; display: block; }
.health-ring-arc { transition: stroke-dashoffset 1s var(--ease-out); }
.health-hero-score { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); text-align: center; display: flex; flex-direction: column; }
.health-hero-num { font-size: 44px; font-weight: var(--weight-bold); line-height: 1; color: var(--color-on-surface); }
.health-hero-num.health-level--good { color: var(--color-tertiary); }
.health-hero-num.health-level--fair { color: var(--color-secondary); }
.health-hero-num.health-level--poor { color: var(--color-error); }
.health-hero-label { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin-top: 2px; }
.health-hero-verdict { flex: 1; }
.health-hero-verdict-text { font-size: var(--text-body-lg); color: var(--color-on-surface); margin: 0 0 var(--space-sm); line-height: 1.6; }
.health-hero-meta { display: flex; align-items: center; gap: var(--space-sm); font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.health-status-dot { width: 8px; height: 8px; border-radius: 50%; }
.health-status-dot.health-level--good { background: var(--color-tertiary); }
.health-status-dot.health-level--fair { background: var(--color-secondary); }
.health-status-dot.health-level--poor { background: var(--color-error); }

/* Sections */
.health-section { margin-bottom: var(--space-xl); }
.health-section-title { font-size: var(--text-headline-md); font-weight: var(--weight-semibold); margin: 0 0 var(--space-md); }

/* Vitals */
.health-vitals { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: var(--space-md); }
.health-vital { display: flex; align-items: center; gap: var(--space-md); padding: var(--space-lg); background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); box-shadow: var(--shadow-card); transition: box-shadow var(--duration-fast) var(--ease-out), transform 0.2s var(--ease-out); }
.health-vital:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }
.health-vital-ring { width: 56px; height: 56px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.health-vital-ring--rose { background: #fce4ec; }
.health-vital-ring--blue { background: #e3f2fd; }
.health-vital-ring--amber { background: #fff8e1; }
.health-vital-ring--violet { background: #f3e5f5; }
.health-vital-icon { font-size: 24px; }
.health-vital-body { flex: 1; }
.health-vital-value { font-size: 24px; font-weight: var(--weight-bold); color: var(--color-on-surface); }
.health-vital-value small { font-size: var(--text-label-sm); font-weight: var(--weight-regular); color: var(--color-on-surface-variant); margin-left: 2px; }
.health-vital-label { font-size: var(--text-label-sm); color: var(--color-on-surface); font-weight: var(--weight-medium); }
.health-vital-range { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.health-vital-trend { font-size: var(--text-label-sm); font-weight: var(--weight-semibold); padding: 2px var(--space-xs); border-radius: var(--radius-sm); }
.health-vital-trend--up { color: var(--color-tertiary); background: var(--color-tertiary-fixed); }
.health-vital-trend--down { color: var(--color-secondary); background: var(--color-secondary-fixed); }

/* Two-col */
.health-two-col { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-lg); }

/* Analysis */
.health-analysis-body { min-height: 120px; }
.health-analysis-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: var(--space-xl); color: var(--color-on-surface-variant); text-align: center; }
.health-analysis-empty-icon { margin-bottom: var(--space-sm); }
.health-analysis-empty p { font-size: var(--text-body-md); margin: 0; }
.health-analysis-loading { display: flex; align-items: center; justify-content: center; gap: 6px; padding: var(--space-xl); }
.health-typing-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--color-primary); animation: health-dot-bounce 1.4s infinite ease-in-out both; }
.health-typing-dot:nth-child(1) { animation-delay: -0.32s; }
.health-typing-dot:nth-child(2) { animation-delay: -0.16s; }
@keyframes health-dot-bounce { 0%,80%,100% { transform: scale(0.6); opacity: 0.4; } 40% { transform: scale(1); opacity: 1; } }
.health-analysis-meta { display: flex; gap: var(--space-xs); margin-bottom: var(--space-md); }
.health-analysis-text { font-size: var(--text-body-md); line-height: 1.8; color: var(--color-on-surface); white-space: pre-wrap; padding: var(--space-md); background: var(--color-surface-container-low); border-radius: var(--radius-md); }

/* Tips */
.health-tips-list { display: flex; flex-direction: column; gap: var(--space-sm); }
.health-tip-item { display: flex; align-items: center; gap: var(--space-sm); padding: var(--space-sm) var(--space-md); background: var(--color-surface-container-low); border-radius: var(--radius-md); transition: background var(--duration-fast) var(--ease-out); }
.health-tip-item:hover { background: var(--color-surface-container); }
.health-tip-icon { width: 36px; height: 36px; border-radius: var(--radius-sm); background: var(--color-surface-container-lowest); display: flex; align-items: center; justify-content: center; font-size: 18px; flex-shrink: 0; }
.health-tip-body { flex: 1; min-width: 0; }
.health-tip-label { font-size: var(--text-label-sm); font-weight: var(--weight-semibold); color: var(--color-on-surface); }
.health-tip-text { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin-top: 1px; line-height: 1.4; }
.health-tip-alert { font-size: 10px; flex-shrink: 0; }
.health-tip-alert--warning { color: var(--color-secondary); }
.health-tip-alert--danger { color: var(--color-error); }

/* Upload */
.health-upload-hint { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin: var(--space-xxs) 0 0; }
.health-upload-zone { border: 2px dashed var(--color-outline-variant); border-radius: var(--radius-md); padding: var(--space-xl); text-align: center; cursor: pointer; transition: all var(--duration-fast) var(--ease-out); margin-top: var(--space-md); }
.health-upload-zone:hover, .health-upload-zone--over { border-color: var(--color-primary); background: var(--color-primary-fixed); }
.health-upload-icon { display: block; margin-bottom: var(--space-sm); color: var(--color-on-surface-variant); }
.health-upload-zone:hover .health-upload-icon { color: var(--color-primary); }
.health-upload-zone p { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: 0; }
.health-files { display: flex; flex-direction: column; gap: var(--space-sm); margin-top: var(--space-md); }
.health-file-row { display: flex; align-items: center; gap: var(--space-sm); padding: var(--space-sm); background: var(--color-surface-container-low); border-radius: var(--radius-md); }
.health-file-icon { font-size: 20px; flex-shrink: 0; }
.health-file-info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.health-file-name { font-size: var(--text-label-md); font-weight: var(--weight-medium); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.health-file-size { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.health-upload-actions { display: flex; justify-content: flex-end; margin-top: var(--space-md); }

/* Responsive */
@media (max-width: 1023px) {
  .health-two-col { grid-template-columns: 1fr; }
  .health-hero { flex-direction: column; text-align: center; }
}
@media (max-width: 767px) {
  .health-page { padding: var(--space-md); }
  .health-header { flex-wrap: wrap; }
  .health-hero-ring { width: 130px; height: 130px; }
  .health-hero-num { font-size: 36px; }
  .health-hero-verdict-text { font-size: var(--text-body-md); }
}
@media (prefers-reduced-motion: reduce) {
  .health-ring-arc { transition: none; }
  .health-typing-dot { animation: none; opacity: 0.8; }
  .health-spin { animation: none; }
}
</style>
