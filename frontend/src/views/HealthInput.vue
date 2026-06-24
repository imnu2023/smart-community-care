<template>
  <div class="hi-page">
    <header class="hi-header">
      <button class="btn-back" @click="goBack"><AppIcon name="x" size="22" /></button>
      <div class="hi-header-text">
        <h1 class="text-headline-lg">健康数据录入</h1>
        <p class="text-body-md">记录您的每日健康数据</p>
      </div>
    </header>

    <div class="hi-layout">
      <!-- Form card -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">今日数据</h3>
          <span class="badge badge-neutral">{{ currentDate }}</span>
        </div>

        <form @submit.prevent="submitForm" class="hi-form">
          <!-- Vital signs group -->
          <fieldset class="hi-fieldset">
            <legend class="hi-legend">
              <span><AppIcon name="pulse" size="20" /></span> 生命体征
            </legend>
            <div class="hi-form-grid">
              <div v-for="field in vitalFields" :key="field.key" class="hi-field">
                <label :for="field.key" class="hi-label">
                  {{ field.label }}
                  <span class="hi-range-hint">{{ field.rangeHint }}</span>
                </label>
                <div class="hi-input-wrap">
                  <input
                    :type="field.type"
                    :id="field.key"
                    v-model="form[field.key]"
                    :placeholder="field.placeholder"
                    :min="field.min" :max="field.max" :step="field.step"
                    class="hi-input"
                    :class="{ 'hi-input--error': errors[field.key], 'hi-input--ok': form[field.key] && !errors[field.key] }"
                    @input="validateField(field.key)"
                  />
                  <span class="hi-unit">{{ field.unit }}</span>
                </div>
                <span v-if="errors[field.key]" class="hi-error">{{ errors[field.key] }}</span>
              </div>
            </div>
          </fieldset>

          <!-- Lifestyle group -->
          <fieldset class="hi-fieldset">
            <legend class="hi-legend">
              <span><AppIcon name="foot" size="20" /></span> 生活数据
            </legend>
            <div class="hi-form-grid">
              <div v-for="field in lifeFields" :key="field.key" class="hi-field">
                <label :for="field.key" class="hi-label">
                  {{ field.label }}
                  <span class="hi-range-hint">{{ field.rangeHint }}</span>
                </label>
                <div class="hi-input-wrap">
                  <input
                    :type="field.type"
                    :id="field.key"
                    v-model="form[field.key]"
                    :placeholder="field.placeholder"
                    :min="field.min" :max="field.max" :step="field.step"
                    class="hi-input"
                    :class="{ 'hi-input--ok': form[field.key] && !errors[field.key] }"
                    @input="validateField(field.key)"
                  />
                  <span class="hi-unit">{{ field.unit }}</span>
                </div>
                <span v-if="errors[field.key]" class="hi-error">{{ errors[field.key] }}</span>
              </div>
              <div class="hi-field">
                <label for="measuredAt" class="hi-label">记录日期</label>
                <input type="date" id="measuredAt" v-model="form.measuredAt" class="hi-input hi-date-input" />
              </div>
            </div>
          </fieldset>

          <!-- Quick presets -->
          <div class="hi-presets">
            <span class="hi-presets-label">快捷填入：</span>
            <button type="button" class="hi-preset-btn" @click="fillPreset('normal')">正常范围</button>
            <button type="button" class="hi-preset-btn" @click="fillPreset('clear')">清空</button>
          </div>

          <div class="hi-actions">
            <button type="button" class="btn btn-secondary" @click="resetForm">重置</button>
            <button type="submit" class="btn btn-primary" :disabled="hasErrors || submitting">
              {{ submitting ? '提交中…' : '提交数据' }}
            </button>
          </div>
        </form>
      </div>

      <!-- History -->
      <div class="card hi-history-card">
        <div class="card-header">
          <h3 class="card-title">历史记录</h3>
          <span class="badge badge-neutral">最近 {{ historyData.length }} 条</span>
        </div>

        <div v-if="historyLoading" class="hi-history-loading">
          <div v-for="i in 3" :key="i" class="skeleton" style="height:80px;border-radius:var(--radius-md);margin-bottom:var(--space-sm)"></div>
        </div>

        <div v-else-if="historyData.length === 0" class="empty-state">
          <div class="empty-icon">📋</div>
          <h3>暂无历史记录</h3>
          <p>提交数据后将在此显示</p>
        </div>

        <div v-else class="hi-timeline">
          <div v-for="(record, index) in historyData" :key="record.id || index" class="hi-tl-item">
            <div class="hi-tl-dot" :class="recordHealthLevel(record)"></div>
            <div class="hi-tl-card">
              <div class="hi-tl-top">
                <span class="hi-tl-date">{{ formatDate(record.recordDate) }}</span>
                <span class="hi-tl-time">{{ formatTime(record.recordDate) }}</span>
                <span class="hi-tl-level" :class="recordHealthLevel(record)">{{ recordHealthLabel(record) }}</span>
              </div>
              <div class="hi-tl-grid">
                <div class="hi-tl-data" v-for="item in recordSummary(record)" :key="item.label">
                  <span class="hi-tl-data-label">{{ item.label }}</span>
                  <span class="hi-tl-data-value">{{ item.value }} <small>{{ item.unit }}</small></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { healthAPI } from '../api'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const goBack = () => router.push('/dashboard')

const currentDate = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
const submitting = ref(false)
const historyLoading = ref(true)

const vitalFields = [
  { key: 'heartRate', label: '心率', type: 'number', placeholder: '60-100', unit: '次/分钟', min: 40, max: 180, step: 1, rangeHint: '正常 60～100' },
  { key: 'bloodPressureHigh', label: '收缩压', type: 'number', placeholder: '90-130', unit: 'mmHg', min: 80, max: 200, step: 1, rangeHint: '正常 90～130' },
  { key: 'bloodPressureLow', label: '舒张压', type: 'number', placeholder: '60-80', unit: 'mmHg', min: 50, max: 120, step: 1, rangeHint: '正常 60～85' },
  { key: 'bloodSugar', label: '血糖', type: 'number', placeholder: '3.9-6.1', unit: 'mmol/L', min: 2, max: 20, step: 0.1, rangeHint: '空腹 3.9～6.1' },
  { key: 'bodyTemperature', label: '体温', type: 'number', placeholder: '36.0-37.2', unit: '℃', min: 35, max: 42, step: 0.1, rangeHint: '正常 36.0～37.2' }
]

const lifeFields = [
  { key: 'sleepHours', label: '睡眠时长', type: 'number', placeholder: '7-9', unit: '小时', min: 0, max: 24, step: 0.5, rangeHint: '推荐 7～9 小时' },
  { key: 'steps', label: '步数', type: 'number', placeholder: '5000-10000', unit: '步', min: 0, max: 50000, step: 1, rangeHint: '推荐 ≥5000 步' }
]

const form = reactive({
  heartRate: '', bloodPressureHigh: '', bloodPressureLow: '',
  bloodSugar: '', bodyTemperature: '', sleepHours: '', steps: '',
  measuredAt: new Date().toISOString().split('T')[0]
})

const errors = reactive({})
const historyData = ref([])

const hasErrors = computed(() => Object.values(errors).some(e => e))

const validateField = (key) => {
  const allFields = [...vitalFields, ...lifeFields]
  const field = allFields.find(f => f.key === key)
  if (!field) return
  const val = form[key]
  if (!val) { errors[key] = ''; return }
  const n = parseFloat(val)
  if (isNaN(n)) errors[key] = '请输入有效数字'
  else if (field.min !== undefined && n < field.min) errors[key] = `低于正常范围`
  else if (field.max !== undefined && n > field.max) errors[key] = `超过正常范围`
  else errors[key] = ''
}

const fillPreset = (type) => {
  if (type === 'clear') { resetForm(); return }
  const presets = { heartRate: 72, bloodPressureHigh: 120, bloodPressureLow: 75, bloodSugar: 5.2, bodyTemperature: 36.5, sleepHours: 7.5, steps: 6000 }
  Object.entries(presets).forEach(([k, v]) => { form[k] = v; validateField(k) })
  ElMessage.success('已填入正常范围参考值')
}

const loadHistory = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) { historyLoading.value = false; return }
  try {
    const res = await healthAPI.getWeekly(userId)
    if (res.code === 200) historyData.value = res.data || []
  } catch (e) { /* silent */ }
  historyLoading.value = false
}

const submitForm = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) { ElMessage.error('请先登录'); return }
  const allFields = [...vitalFields, ...lifeFields]
  for (const f of allFields) validateField(f.key)
  if (hasErrors.value) { ElMessage.warning('请检查标红的输入项'); return }

  submitting.value = true
  try {
    const data = {
      userId: parseInt(userId),
      heartRate: form.heartRate ? parseInt(form.heartRate) : null,
      bloodPressureHigh: form.bloodPressureHigh ? parseInt(form.bloodPressureHigh) : null,
      bloodPressureLow: form.bloodPressureLow ? parseInt(form.bloodPressureLow) : null,
      bloodSugar: form.bloodSugar ? parseFloat(form.bloodSugar) : null,
      bodyTemperature: form.bodyTemperature ? parseFloat(form.bodyTemperature) : null,
      sleepHours: form.sleepHours ? parseFloat(form.sleepHours) : null,
      steps: form.steps ? parseInt(form.steps) : null,
      measuredAt: form.measuredAt ? form.measuredAt + 'T00:00:00' : new Date().toISOString().slice(0, 19)
    }
    const res = await healthAPI.save(data)
    if (res.code === 200) {
      ElMessage.success('数据提交成功！刷新健康监测页面查看最新评分')
      resetForm()
      await loadHistory()
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch (e) { ElMessage.error('提交失败，请检查网络连接') }
  submitting.value = false
}

const resetForm = () => {
  const allFields = [...vitalFields, ...lifeFields]
  for (const k of allFields.map(f => f.key)) form[k] = ''
  form.measuredAt = new Date().toISOString().split('T')[0]
  for (const k in errors) errors[k] = ''
}

// History helpers
const recordHealthLevel = (r) => {
  let issues = 0
  if (r.heartRate && (r.heartRate < 55 || r.heartRate > 100)) issues++
  if (r.bloodPressureHigh && r.bloodPressureLow && (r.bloodPressureHigh >= 140 || r.bloodPressureLow >= 90)) issues++
  if (r.bloodSugar && (parseFloat(r.bloodSugar) < 3.5 || parseFloat(r.bloodSugar) > 7.0)) issues++
  if (issues >= 2) return 'hi-level--poor'
  if (issues === 1) return 'hi-level--fair'
  return 'hi-level--good'
}
const recordHealthLabel = (r) => {
  const lvl = recordHealthLevel(r)
  if (lvl === 'hi-level--good') return '良好'
  if (lvl === 'hi-level--fair') return '关注'
  return '异常'
}
const recordSummary = (r) => [
  { label: '心率', value: r.heartRate ?? '--', unit: 'bpm' },
  { label: '血压', value: r.bloodPressureHigh ? `${r.bloodPressureHigh}/${r.bloodPressureLow}` : '--', unit: 'mmHg' },
  { label: '血糖', value: r.bloodSugar ?? '--', unit: 'mmol/L' },
  { label: '睡眠', value: r.sleepHours ?? '--', unit: 'h' }
]

const formatDate = (s) => s ? new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }) : ''
const formatTime = (s) => s ? new Date(s).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) : ''

onMounted(() => loadHistory())
</script>

<style scoped>
.hi-page { min-height: 100vh; background: var(--color-surface); padding: var(--space-lg); }
.hi-header { display: flex; align-items: center; gap: var(--space-md); margin-bottom: var(--space-xl); }
.hi-header-text h1 { margin: 0; }
.hi-header-text p { margin: var(--space-xxs) 0 0; color: var(--color-on-surface-variant); }

/* Layout */
.hi-layout { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-lg); align-items: start; }

/* Form */
.hi-form { margin-top: var(--space-md); background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); padding: var(--space-lg); box-shadow: var(--shadow-card); }

/* Fieldsets */
.hi-fieldset { border: none; padding: 0; margin: 0 0 var(--space-lg); }
.hi-legend { font-size: var(--text-label-md); font-weight: var(--weight-semibold); color: var(--color-on-surface); padding: 0 0 var(--space-sm); width: 100%; border-bottom: 1px solid var(--color-outline-variant); margin-bottom: var(--space-md); display: flex; align-items: center; gap: var(--space-xs); }
.hi-legend span { font-size: 18px; }

.hi-form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-md); }
.hi-field { display: flex; flex-direction: column; gap: var(--space-xxs); }
.hi-label { font-size: var(--text-label-md); font-weight: var(--weight-medium); color: var(--color-on-surface); display: flex; align-items: baseline; gap: var(--space-xs); }
.hi-range-hint { font-size: var(--text-label-sm); font-weight: var(--weight-regular); color: var(--color-on-surface-variant); }

.hi-input-wrap { position: relative; }
.hi-input { width: 100%; min-height: var(--input-min-height); padding: 14px var(--space-md); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-md); font-size: var(--text-body-md); font-family: var(--font-family); color: var(--color-on-surface); background: var(--color-surface-container-lowest); transition: all var(--duration-fast) var(--ease-out); }
.hi-input:focus { outline: none; border: 2px solid var(--color-primary); padding: 13px calc(var(--space-md) - 1px); }
.hi-input::placeholder { color: var(--color-on-surface-variant); font-size: var(--text-label-md); }
.hi-input--ok { border-color: var(--color-tertiary-fixed-dim); }
.hi-input--error { border: 2px solid var(--color-error) !important; padding: 13px calc(var(--space-md) - 1px) !important; }
.hi-unit { position: absolute; right: var(--space-md); top: 50%; transform: translateY(-50%); font-size: var(--text-label-sm); color: var(--color-on-surface-variant); pointer-events: none; }
.hi-date-input { padding-right: var(--space-md) !important; }
.hi-error { font-size: var(--text-label-sm); color: var(--color-error); font-weight: var(--weight-medium); }

/* Presets */
.hi-presets { display: flex; align-items: center; gap: var(--space-xs); margin-bottom: var(--space-md); }
.hi-presets-label { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.hi-preset-btn { padding: var(--space-xxs) var(--space-sm); background: var(--color-surface-container-low); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-sm); font-size: var(--text-label-sm); font-family: var(--font-family); color: var(--color-on-surface-variant); cursor: pointer; transition: all var(--duration-fast) var(--ease-out); }
.hi-preset-btn:hover { border-color: var(--color-primary); color: var(--color-primary); background: var(--color-primary-fixed); }

.hi-actions { display: flex; gap: var(--space-sm); justify-content: flex-end; padding-top: var(--space-md); border-top: 1px solid var(--color-outline-variant); }

/* History */
.hi-history-card { max-height: 680px; overflow-y: auto; }
.hi-history-loading { padding: var(--space-sm); }

.hi-timeline { display: flex; flex-direction: column; gap: var(--space-sm); }
.hi-tl-item { display: flex; gap: var(--space-md); }
.hi-tl-dot { width: 12px; height: 12px; border-radius: 50%; margin-top: var(--space-md); flex-shrink: 0; border: 2px solid; }
.hi-tl-dot.hi-level--good { background: var(--color-tertiary-fixed); border-color: var(--color-tertiary); }
.hi-tl-dot.hi-level--fair { background: var(--color-secondary-fixed); border-color: var(--color-secondary); }
.hi-tl-dot.hi-level--poor { background: var(--color-error-container); border-color: var(--color-error); }

.hi-tl-card { flex: 1; background: var(--color-surface-container-low); border-radius: var(--radius-md); padding: var(--space-sm) var(--space-md); }
.hi-tl-top { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: var(--space-sm); }
.hi-tl-date { font-size: var(--text-label-md); font-weight: var(--weight-semibold); color: var(--color-on-surface); }
.hi-tl-time { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.hi-tl-level { margin-left: auto; font-size: var(--text-label-sm); font-weight: var(--weight-semibold); padding: 1px var(--space-xs); border-radius: var(--radius-sm); }
.hi-tl-level.hi-level--good { color: var(--color-tertiary); background: var(--color-tertiary-fixed); }
.hi-tl-level.hi-level--fair { color: var(--color-secondary); background: var(--color-secondary-fixed); }
.hi-tl-level.hi-level--poor { color: var(--color-error); background: var(--color-error-container); }

.hi-tl-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-xs); }
.hi-tl-data { display: flex; justify-content: space-between; align-items: center; padding: var(--space-xxs) var(--space-xs); background: var(--color-surface-container-lowest); border-radius: var(--radius-sm); }
.hi-tl-data-label { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.hi-tl-data-value { font-size: var(--text-label-sm); font-weight: var(--weight-semibold); color: var(--color-on-surface); }
.hi-tl-data-value small { font-weight: var(--weight-regular); color: var(--color-on-surface-variant); }

/* Responsive */
@media (max-width: 1023px) {
  .hi-layout { grid-template-columns: 1fr; }
  .hi-form-grid { grid-template-columns: 1fr 1fr; }
}
@media (max-width: 767px) {
  .hi-page { padding: var(--space-md); }
  .hi-form-grid { grid-template-columns: 1fr; }
  .hi-tl-grid { grid-template-columns: 1fr; }
  .hi-history-card { max-height: none; }
}
</style>
