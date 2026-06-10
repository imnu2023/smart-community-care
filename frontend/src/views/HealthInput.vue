<template>
  <div class="health-input-page">
    <div class="page-header">
      <div class="header-text">
        <h1>健康数据录入</h1>
        <p>记录您的每日健康数据</p>
      </div>
    </div>

    <div class="main-content">
      <div class="form-section">
        <div class="section-header">
          <h2>今日数据</h2>
          <span class="date-badge">{{ currentDate }}</span>
        </div>
        
        <form @submit.prevent="submitForm" class="health-form">
          <div class="form-grid">
            <div class="form-item" v-for="field in formFields" :key="field.key">
              <label :for="field.key">{{ field.label }}</label>
              <div class="input-wrapper">
                <input
                  :type="field.type === 'number' ? 'number' : 'text'"
                  :id="field.key"
                  v-model="form[field.key]"
                  :placeholder="field.placeholder"
                  :min="field.min"
                  :max="field.max"
                  :step="field.step"
                  @input="validateField(field.key)"
                />
                <span class="input-unit">{{ field.unit }}</span>
              </div>
              <span v-if="errors[field.key]" class="error-message">{{ errors[field.key] }}</span>
            </div>
            
            <div class="form-item">
              <label for="measuredAt">记录日期</label>
              <div class="input-wrapper">
                <input
                  type="date"
                  id="measuredAt"
                  v-model="form.measuredAt"
                  class="date-input"
                />
              </div>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn-secondary" @click="resetForm">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="23 4 23 10 17 10"/>
                <polyline points="1 20 1 14 7 14"/>
                <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
              </svg>
              重置
            </button>
            <button type="submit" class="btn-primary" :disabled="hasErrors">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="7 10 12 15 17 10"/>
                <line x1="12" y1="15" x2="12" y2="3"/>
              </svg>
              提交数据
            </button>
          </div>
        </form>
      </div>

      <div class="history-section">
        <div class="section-header">
          <h2>历史记录</h2>
          <span class="record-count">最近 {{ historyData.length }} 条记录</span>
        </div>
        
        <div v-if="historyData.length === 0" class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 8v4l3 3"/>
            <circle cx="12" cy="12" r="10"/>
          </svg>
          <p>暂无历史记录</p>
        </div>
        
        <div v-else class="timeline">
          <div 
            v-for="(record, index) in historyData" 
            :key="record.id || index"
            class="timeline-item"
          >
            <div class="timeline-dot"></div>
            <div class="timeline-card">
              <div class="timeline-header">
                <span class="timeline-date">{{ formatDate(record.recordDate) }}</span>
                <span class="timeline-time">{{ formatTime(record.recordDate) }}</span>
              </div>
              <div class="timeline-content">
                <div class="data-grid">
                  <div class="data-item">
                    <span class="data-label">心率</span>
                    <span class="data-value">{{ record.heartRate }} <span class="data-unit">bpm</span></span>
                  </div>
                  <div class="data-item">
                    <span class="data-label">血压</span>
                    <span class="data-value">{{ record.bloodPressureHigh }}/{{ record.bloodPressureLow }} <span class="data-unit">mmHg</span></span>
                  </div>
                  <div class="data-item">
                    <span class="data-label">血糖</span>
                    <span class="data-value">{{ record.bloodSugar }} <span class="data-unit">mmol/L</span></span>
                  </div>
                  <div class="data-item">
                    <span class="data-label">睡眠</span>
                    <span class="data-value">{{ record.sleepHours }} <span class="data-unit">小时</span></span>
                  </div>
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
import { healthAPI } from '../api'
import { ElMessage } from 'element-plus'

const currentDate = new Date().toLocaleDateString('zh-CN', { 
  year: 'numeric', 
  month: 'long', 
  day: 'numeric',
  weekday: 'long'
})

const formFields = [
  { key: 'heartRate', label: '心率', type: 'number', placeholder: '请输入心率', unit: '次/分钟', min: 40, max: 180, step: 1 },
  { key: 'bloodPressureHigh', label: '血压(高压)', type: 'number', placeholder: '请输入高压', unit: 'mmHg', min: 80, max: 200, step: 1 },
  { key: 'bloodPressureLow', label: '血压(低压)', type: 'number', placeholder: '请输入低压', unit: 'mmHg', min: 50, max: 120, step: 1 },
  { key: 'bloodSugar', label: '血糖', type: 'number', placeholder: '请输入血糖', unit: 'mmol/L', min: 2, max: 20, step: 0.1 },
  { key: 'bodyTemperature', label: '体温', type: 'number', placeholder: '请输入体温', unit: '℃', min: 35, max: 42, step: 0.1 },
  { key: 'sleepHours', label: '睡眠时长', type: 'number', placeholder: '请输入睡眠时长', unit: '小时', min: 0, max: 24, step: 0.5 },
  { key: 'steps', label: '步数', type: 'number', placeholder: '请输入步数', unit: '步', min: 0, max: 50000, step: 1 }
]

const form = reactive({
  heartRate: '',
  bloodPressureHigh: '',
  bloodPressureLow: '',
  bloodSugar: '',
  bodyTemperature: '',
  sleepHours: '',
  steps: '',
  measuredAt: new Date().toISOString().split('T')[0]
})

const errors = reactive({})
const historyData = ref([])

const hasErrors = computed(() => {
  return Object.keys(errors).some(key => errors[key])
})

const validateField = (fieldKey) => {
  const field = formFields.find(f => f.key === fieldKey)
  const value = form[fieldKey]
  
  if (!value) {
    errors[fieldKey] = ''
    return
  }
  
  const numValue = parseFloat(value)
  
  if (isNaN(numValue)) {
    errors[fieldKey] = '请输入有效数字'
  } else if (field.min !== undefined && numValue < field.min) {
    errors[fieldKey] = `最小值为 ${field.min}`
  } else if (field.max !== undefined && numValue > field.max) {
    errors[fieldKey] = `最大值为 ${field.max}`
  } else {
    errors[fieldKey] = ''
  }
}

const loadHistory = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await healthAPI.getWeekly(userId)
    if (response.code === 200) {
      historyData.value = response.data || []
    }
  } catch (error) {
    console.error('加载历史数据失败:', error)
  }
}

const submitForm = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.error('请先登录')
    return
  }
  
  for (const field of formFields) {
    validateField(field.key)
  }
  
  if (hasErrors.value) {
    ElMessage.warning('请检查输入的信息')
    return
  }
  
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
    
    const response = await healthAPI.save(data)
    if (response.code === 200) {
      ElMessage.success('数据提交成功')
      resetForm()
      loadHistory()
    } else {
      ElMessage.error(response.message || '提交失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

const resetForm = () => {
  form.heartRate = ''
  form.bloodPressureHigh = ''
  form.bloodPressureLow = ''
  form.bloodSugar = ''
  form.bodyTemperature = ''
  form.sleepHours = ''
  form.steps = ''
  form.measuredAt = new Date().toISOString().split('T')[0]
  
  for (const key in errors) {
    errors[key] = ''
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.health-input-page {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

.page-header {
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

.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
  font-weight: 600;
}

.date-badge,
.record-count {
  padding: 6px 14px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.form-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.health-form {
  margin-top: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper input {
  flex: 1;
  padding: 14px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.3s;
  background: #f8fafc;
}

.input-wrapper input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-wrapper input::placeholder {
  color: #94a3b8;
}

.input-unit {
  position: absolute;
  right: 16px;
  color: #64748b;
  font-size: 14px;
  pointer-events: none;
}

.date-input {
  padding-right: 16px !important;
}

.error-message {
  font-size: 12px;
  color: #ef4444;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.btn-secondary,
.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
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

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary svg,
.btn-primary svg {
  width: 16px;
  height: 16px;
}

.history-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  max-height: 600px;
  overflow-y: auto;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #94a3b8;
}

.empty-state svg {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.timeline {
  position: relative;
  padding-left: 20px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 1px;
}

.timeline-item {
  position: relative;
  margin-bottom: 20px;
}

.timeline-item:last-child {
  margin-bottom: 0;
}

.timeline-dot {
  position: absolute;
  left: -17px;
  top: 16px;
  width: 12px;
  height: 12px;
  background: white;
  border: 3px solid #667eea;
  border-radius: 50%;
  z-index: 1;
}

.timeline-card {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.3s;
}

.timeline-card:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.timeline-date {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.timeline-time {
  font-size: 12px;
  color: #94a3b8;
}

.timeline-content {
  margin-top: 8px;
}

.data-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: white;
  border-radius: 8px;
}

.data-label {
  font-size: 12px;
  color: #64748b;
}

.data-value {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.data-unit {
  font-size: 12px;
  font-weight: 400;
  color: #94a3b8;
  margin-left: 4px;
}

@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>