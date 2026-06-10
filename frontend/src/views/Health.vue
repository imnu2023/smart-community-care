<template>
  <div class="health-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1>健康监测</h1>
          <p>实时追踪您的健康数据</p>
        </div>
        <button class="refresh-btn" @click="refreshData">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 4 23 10 17 10"/>
            <polyline points="1 20 1 14 7 14"/>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
          </svg>
          <span>刷新数据</span>
        </button>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.label">
        <div class="stat-icon" :class="stat.type">
          <svg :viewBox="stat.iconViewBox" fill="none" stroke="currentColor" stroke-width="2">
            <path :d="stat.iconPath"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            <span>{{ stat.value }}</span>
            <span class="stat-unit">{{ stat.unit }}</span>
          </div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-status" :class="stat.status">
            <span class="status-dot"></span>
            {{ stat.statusText }}
          </div>
        </div>
        <div class="stat-trend" :class="stat.trend">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path :d="stat.trend === 'up' ? 'M12 19l7-7 3 3-7 7-3-3z' : 'M12 5l-7 7-3-3 7-7 3 3z'"/>
          </svg>
          <span>{{ stat.trendText }}</span>
        </div>
      </div>
    </div>

    <div class="charts-section">
      <div class="chart-card">
        <div class="card-header">
          <h3>健康趋势</h3>
          <div class="chart-tabs">
            <button 
              v-for="tab in timeTabs" 
              :key="tab.value"
              :class="['tab-btn', { active: activeTimeTab === tab.value }]"
              @click="activeTimeTab = tab.value"
            >
              {{ tab.label }}
            </button>
          </div>
        </div>
        <div class="chart-container">
          <div class="mini-chart">
            <div 
              v-for="(bar, index) in chartData" 
              :key="index"
              class="chart-bar-wrapper"
            >
              <div 
                class="chart-bar" 
                :style="{ height: bar.height + '%' }"
                :class="bar.type"
              ></div>
              <span class="chart-label">{{ bar.label }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <h3>健康评分</h3>
        </div>
        <div class="health-score">
          <div class="score-ring">
            <svg viewBox="0 0 120 120" class="ring-svg">
              <circle cx="60" cy="60" r="50" fill="none" stroke="#e2e8f0" stroke-width="8"/>
              <circle 
                cx="60" cy="60" r="50" 
                fill="none" 
                stroke="url(#scoreGradient)" 
                stroke-width="8"
                :stroke-dasharray="circumference"
                :stroke-dashoffset="scoreOffset"
                stroke-linecap="round"
                transform="rotate(-90 60 60)"
              />
              <defs>
                <linearGradient id="scoreGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stop-color="#667eea"/>
                  <stop offset="100%" stop-color="#764ba2"/>
                </linearGradient>
              </defs>
            </svg>
            <div class="score-text">
              <span class="score-value">{{ healthScore }}</span>
              <span class="score-label">健康评分</span>
            </div>
          </div>
          <div class="score-details">
            <div class="detail-item">
              <span class="detail-icon">❤️</span>
              <span class="detail-text">心率正常</span>
            </div>
            <div class="detail-item">
              <span class="detail-icon">💪</span>
              <span class="detail-text">血压稳定</span>
            </div>
            <div class="detail-item">
              <span class="detail-icon">😴</span>
              <span class="detail-text">睡眠充足</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="report-section">
      <div class="report-card">
        <div class="card-header">
          <h3>AI 健康分析报告</h3>
          <button class="generate-btn" @click="generateReport">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <polyline points="10 9 9 9 8 9"/>
            </svg>
            生成报告
          </button>
        </div>
        <div class="report-content">
          <div v-if="!reportGenerated" class="empty-report">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
            <p>点击上方按钮生成您的健康分析报告</p>
          </div>
          <div v-else class="report-text">
            <div class="report-header">
              <span class="report-date">{{ reportDate }}</span>
              <span class="report-status success">分析完成</span>
            </div>
            <div class="report-body">
              <p>{{ healthReport }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { healthAPI } from '../api'
import { ElMessage } from 'element-plus'

const latestData = ref(null)
const healthReport = ref('')
const reportGenerated = ref(false)
const reportDate = ref('')
const activeTimeTab = ref('week')

const timeTabs = [
  { label: '近7天', value: 'week' },
  { label: '近30天', value: 'month' },
  { label: '近3月', value: 'quarter' }
]

const circumference = 2 * Math.PI * 50
const healthScore = ref(85)
const scoreOffset = computed(() => {
  return circumference - (healthScore.value / 100) * circumference
})

const chartData = ref([
  { label: '周一', height: 75, type: 'normal' },
  { label: '周二', height: 82, type: 'good' },
  { label: '周三', height: 68, type: 'warning' },
  { label: '周四', height: 88, type: 'good' },
  { label: '周五', height: 79, type: 'normal' },
  { label: '周六', height: 92, type: 'excellent' },
  { label: '周日', height: 85, type: 'good' }
])

const stats = computed(() => [
  {
    label: '心率',
    value: latestData.value?.heartRate || '--',
    unit: 'bpm',
    type: 'heart',
    iconViewBox: '0 0 24 24',
    iconPath: 'M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z',
    status: 'normal',
    statusText: '正常',
    trend: 'down',
    trendText: '-2%'
  },
  {
    label: '血压',
    value: latestData.value?.bloodPressureHigh || '--',
    unit: '/' + (latestData.value?.bloodPressureLow || '--') + ' mmHg',
    type: 'bp',
    iconViewBox: '0 0 24 24',
    iconPath: 'M22 12h-4l-3 9L9 3l-3 9H2',
    status: 'normal',
    statusText: '稳定',
    trend: 'up',
    trendText: '+1%'
  },
  {
    label: '血糖',
    value: latestData.value?.bloodSugar || '--',
    unit: 'mmol/L',
    type: 'sugar',
    iconViewBox: '0 0 24 24',
    iconPath: 'M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z',
    status: 'good',
    statusText: '良好',
    trend: 'down',
    trendText: '-3%'
  },
  {
    label: '睡眠',
    value: latestData.value?.sleepHours || '--',
    unit: '小时',
    type: 'sleep',
    iconViewBox: '0 0 24 24',
    iconPath: 'M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 1 1-8 0 4 4 0 0 1 8 0z',
    status: 'good',
    statusText: '充足',
    trend: 'up',
    trendText: '+5%'
  }
])

const loadData = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const latestRes = await healthAPI.getLatest(userId)
    if (latestRes.code === 200) {
      latestData.value = latestRes.data
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const generateReport = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await healthAPI.analyze(userId)
    if (response.code === 200) {
      healthReport.value = response.data || '根据您的健康数据分析：\n\n1. 心率状况：您的心率处于正常范围，心血管功能良好。\n\n2. 血压状况：血压控制在理想水平，建议继续保持健康的生活方式。\n\n3. 血糖状况：血糖值正常，请注意饮食均衡。\n\n4. 睡眠状况：睡眠时长充足，睡眠质量良好。\n\n综合建议：您的整体健康状况良好，请继续保持规律作息和适度运动。'
      reportGenerated.value = true
      reportDate.value = new Date().toLocaleDateString('zh-CN')
      ElMessage.success('报告生成成功')
    }
  } catch (error) {
    ElMessage.error('生成报告失败')
  }
}

const refreshData = () => {
  loadData()
  ElMessage.success('数据已刷新')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.health-page {
  padding: 24px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

.page-header {
  margin-bottom: 28px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  color: #667eea;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.refresh-btn:hover {
  background: rgba(102, 126, 234, 0.05);
  border-color: #667eea;
}

.refresh-btn svg {
  width: 18px;
  height: 18px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 28px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  color: white;
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-icon.heart {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  box-shadow: 0 4px 15px rgba(244, 63, 94, 0.4);
}

.stat-icon.bp {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  box-shadow: 0 4px 15px rgba(6, 182, 212, 0.4);
}

.stat-icon.sugar {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  box-shadow: 0 4px 15px rgba(251, 191, 36, 0.4);
}

.stat-icon.sleep {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.4);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-unit {
  font-size: 14px;
  font-weight: 400;
  color: #64748b;
  margin-left: 4px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.stat-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 500;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.stat-status.normal .status-dot {
  background: #22c55e;
}

.stat-status.normal {
  color: #22c55e;
}

.stat-status.good .status-dot {
  background: #3b82f6;
}

.stat-status.good {
  color: #3b82f6;
}

.stat-status.warning .status-dot {
  background: #f59e0b;
}

.stat-status.warning {
  color: #f59e0b;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.stat-trend svg {
  width: 14px;
  height: 14px;
}

.stat-trend.up {
  color: #22c55e;
}

.stat-trend.down {
  color: #ef4444;
}

.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 28px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
  font-weight: 600;
}

.chart-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  padding: 6px 16px;
  background: #f1f5f9;
  border: none;
  border-radius: 20px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.chart-container {
  height: 200px;
}

.mini-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 100%;
  padding-top: 20px;
}

.chart-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 12%;
}

.chart-bar {
  width: 100%;
  max-width: 32px;
  border-radius: 8px 8px 0 0;
  transition: height 0.5s ease;
}

.chart-bar.normal {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.chart-bar.good {
  background: linear-gradient(180deg, #22c55e 0%, #16a34a 100%);
}

.chart-bar.warning {
  background: linear-gradient(180deg, #f59e0b 0%, #d97706 100%);
}

.chart-bar.excellent {
  background: linear-gradient(180deg, #06b6d4 0%, #0891b2 100%);
}

.chart-label {
  font-size: 12px;
  color: #94a3b8;
}

.health-score {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-ring {
  position: relative;
  width: 140px;
  height: 140px;
  margin-bottom: 20px;
}

.ring-svg {
  width: 100%;
  height: 100%;
}

.score-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.score-value {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
}

.score-label {
  font-size: 12px;
  color: #64748b;
}

.score-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.detail-icon {
  font-size: 20px;
}

.detail-text {
  font-size: 14px;
  color: #334155;
}

.report-section {
  margin-bottom: 28px;
}

.report-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.generate-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  color: white;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.generate-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.generate-btn svg {
  width: 16px;
  height: 16px;
}

.report-content {
  min-height: 200px;
  border-radius: 12px;
  padding: 20px;
  background: #f8fafc;
}

.empty-report {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #94a3b8;
}

.empty-report svg {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}

.empty-report p {
  margin: 0;
  font-size: 14px;
}

.report-text {
  color: #334155;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e2e8f0;
}

.report-date {
  font-size: 13px;
  color: #64748b;
}

.report-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.report-status.success {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.report-body p {
  margin: 0;
  font-size: 14px;
  line-height: 1.8;
  white-space: pre-wrap;
}

@media (max-width: 768px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>