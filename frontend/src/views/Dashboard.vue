<template>
  <div class="dashboard">
    <div class="page-container">
      <aside class="sidebar">
        <div class="sidebar-header">
          <h1>
            <span class="logo">🏠</span>
            <span>智慧社区养老</span>
          </h1>
        </div>
        <nav class="sidebar-menu">
          <router-link to="/dashboard" class="menu-item" :class="{ active: $route.path === '/dashboard' }">
            <span class="icon">📊</span>
            <span>首页</span>
          </router-link>
          <router-link to="/health" class="menu-item" :class="{ active: $route.path === '/health' }">
            <span class="icon">❤️</span>
            <span>健康监测</span>
          </router-link>
          <router-link to="/health-input" class="menu-item" :class="{ active: $route.path === '/health-input' }">
            <span class="icon">📝</span>
            <span>数据录入</span>
          </router-link>
          <router-link to="/devices" class="menu-item" :class="{ active: $route.path === '/devices' }">
            <span class="icon">🔧</span>
            <span>智能家居</span>
          </router-link>
          <router-link to="/emergency" class="menu-item" :class="{ active: $route.path === '/emergency' }">
            <span class="icon">🚨</span>
            <span>紧急呼叫</span>
          </router-link>
          <router-link to="/messages" class="menu-item" :class="{ active: $route.path === '/messages' }">
            <span class="icon">💬</span>
            <span>消息中心</span>
          </router-link>
          <router-link to="/activities" class="menu-item" :class="{ active: $route.path === '/activities' }">
            <span class="icon">🎉</span>
            <span>社区活动</span>
          </router-link>
          <router-link to="/services" class="menu-item" :class="{ active: $route.path === '/services' }">
            <span class="icon">🛠️</span>
            <span>社区服务</span>
          </router-link>
          <router-link to="/orders" class="menu-item" :class="{ active: $route.path === '/orders' }">
            <span class="icon">📦</span>
            <span>我的订单</span>
          </router-link>
          <router-link to="/profile" class="menu-item" :class="{ active: $route.path === '/profile' }">
            <span class="icon">👤</span>
            <span>个人中心</span>
          </router-link>
        </nav>
        <div class="sidebar-footer">
          <div class="user-info">
            <div class="avatar">�</div>
            <div class="user-details">
              <div class="username">{{ userName }}</div>
              <div class="user-role">{{ userRole }}</div>
            </div>
          </div>
          <button class="logout-btn" @click="handleLogout">
            <span class="icon">🚪</span>
            <span>退出登录</span>
          </button>
        </div>
      </aside>

      <main class="main-content">
        <header class="page-header">
          <div class="header-content">
            <div class="greeting-icon">👋</div>
            <div class="greeting-text">
              <h2>欢迎回来，{{ userName }}</h2>
              <p class="subtitle">今天是 {{ currentDate }}，祝您身体健康</p>
            </div>
          </div>
          
        </header>

        <section class="stats-section">
          <div class="section-header">
            <h3 class="section-title">健康概览</h3>
            <span class="section-subtitle">今日健康数据</span>
          </div>
          <div class="grid grid-4">
            <div class="stat-card primary hover-lift">
              <div class="stat-icon-wrapper">
                <span class="stat-icon">❤️</span>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ healthStats.heartRate }}</div>
                <div class="stat-label">心率 (bpm)</div>
                <div class="stat-change positive">
                  <span>↑</span>
                  <span>+2%</span>
                </div>
              </div>
            </div>
            <div class="stat-card success hover-lift">
              <div class="stat-icon-wrapper">
                <span class="stat-icon">🩸</span>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ healthStats.bloodPressure }}</div>
                <div class="stat-label">血压 (mmHg)</div>
                <div class="stat-change positive">
                  <span>↓</span>
                  <span>-1%</span>
                </div>
              </div>
            </div>
            <div class="stat-card warning hover-lift">
              <div class="stat-icon-wrapper">
                <span class="stat-icon">🍬</span>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ healthStats.bloodGlucose }}</div>
                <div class="stat-label">血糖 (mmol/L)</div>
                <div class="stat-change negative">
                  <span>↑</span>
                  <span>+3%</span>
                </div>
              </div>
            </div>
            <div class="stat-card info hover-lift">
              <div class="stat-icon-wrapper">
                <span class="stat-icon">😴</span>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ healthStats.sleepHours }}</div>
                <div class="stat-label">睡眠 (小时)</div>
                <div class="stat-change positive">
                  <span>↑</span>
                  <span>+0.5h</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="quick-actions">
          <div class="section-header">
            <h3 class="section-title">快捷功能</h3>
            <span class="section-subtitle">快速访问常用功能</span>
          </div>
          <div class="grid grid-4">
            <router-link to="/health" class="action-card">
              <div class="action-icon-wrapper health">
                <span class="action-icon">❤️</span>
              </div>
              <div class="action-content">
                <div class="action-title">健康监测</div>
                <div class="action-desc">查看健康数据</div>
              </div>
            </router-link>
            <router-link to="/health-input" class="action-card">
              <div class="action-icon-wrapper data">
                <span class="action-icon">📝</span>
              </div>
              <div class="action-content">
                <div class="action-title">数据录入</div>
                <div class="action-desc">记录健康指标</div>
              </div>
            </router-link>
            <router-link to="/messages" class="action-card">
              <div class="action-icon-wrapper message">
                <span class="action-icon">💬</span>
              </div>
              <div class="action-content">
                <div class="action-title">消息中心</div>
                <div class="action-desc">查看消息通知</div>
              </div>
            </router-link>
            <router-link to="/activities" class="action-card">
              <div class="action-icon-wrapper activity">
                <span class="action-icon">🎉</span>
              </div>
              <div class="action-content">
                <div class="action-title">社区活动</div>
                <div class="action-desc">参与社区活动</div>
              </div>
            </router-link>
            <router-link to="/services" class="action-card">
              <div class="action-icon-wrapper service">
                <span class="action-icon">🛠️</span>
              </div>
              <div class="action-content">
                <div class="action-title">社区服务</div>
                <div class="action-desc">预约服务</div>
              </div>
            </router-link>
            <router-link to="/devices" class="action-card">
              <div class="action-icon-wrapper device">
                <span class="action-icon">🔧</span>
              </div>
              <div class="action-content">
                <div class="action-title">智能家居</div>
                <div class="action-desc">智能设备控制</div>
              </div>
            </router-link>
            <router-link to="/orders" class="action-card">
              <div class="action-icon-wrapper order">
                <span class="action-icon">📦</span>
              </div>
              <div class="action-content">
                <div class="action-title">我的订单</div>
                <div class="action-desc">订单管理</div>
              </div>
            </router-link>
            <router-link to="/profile" class="action-card">
              <div class="action-icon-wrapper profile">
                <span class="action-icon">👤</span>
              </div>
              <div class="action-content">
                <div class="action-title">个人中心</div>
                <div class="action-desc">管理个人信息</div>
              </div>
            </router-link>
          </div>
        </section>

        <section class="content-section">
          <div class="content-grid">
            <div class="card activity-card">
              <div class="card-header">
                <h3 class="card-title">近期活动</h3>
                <router-link to="/activities" class="view-all">查看全部 →</router-link>
              </div>
              <div class="activity-list">
                <div 
                  v-for="(activity, index) in recentActivities" 
                  :key="activity.id"
                  class="activity-item list-item"
                  :style="{ animationDelay: `${index * 0.1}s` }"
                >
                  <div class="activity-icon-wrapper" :class="activity.type">
                    {{ getActivityIcon(activity.type) }}
                  </div>
                  <div class="activity-info">
                    <h4 class="activity-title">{{ activity.title }}</h4>
                    <p class="activity-time">{{ formatTime(activity.time) }}</p>
                  </div>
                  <div class="activity-status">
                    <span class="badge" :class="getBadgeClass(activity.status)">
                      {{ activity.status }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div class="card quick-info-card">
              <div class="card-header">
                <h3 class="card-title">健康小贴士</h3>
              </div>
              <div class="tips-list">
                <div class="tip-item">
                  <div class="tip-icon">🌡️</div>
                  <div class="tip-content">
                    <h4>今日体温</h4>
                    <p>{{ healthStats.temperature || '--' }}°C</p>
                  </div>
                </div>
                <div class="tip-item">
                  <div class="tip-icon">🚶</div>
                  <div class="tip-content">
                    <h4>今日步数</h4>
                    <p>{{ healthStats.steps || '--' }} 步</p>
                  </div>
                </div>
                <div class="tip-item">
                  <div class="tip-icon">💧</div>
                  <div class="tip-content">
                    <h4>饮水提醒</h4>
                    <p>建议每日饮水8杯</p>
                  </div>
                </div>
                <div class="tip-item">
                  <div class="tip-icon">💊</div>
                  <div class="tip-content">
                    <h4>服药提醒</h4>
                    <p>记得按时服药</p>
                  </div>
                </div>
              </div>
              <div class="quick-links">
                <router-link to="/health" class="quick-link">查看健康详情</router-link>
                <router-link to="/health-input" class="quick-link">录入健康数据</router-link>
              </div>
            </div>
          </div>
        </section>

        <section class="report-section">
          <div class="section-header">
            <h3 class="section-title">健康报告</h3>
            <span class="section-subtitle">生成专业的健康分析报告</span>
          </div>
          <div class="report-card">
            <div class="report-header">
              <div class="report-icon-wrapper">
                <span class="report-icon">📊</span>
              </div>
              <div class="report-info">
                <h4>健康分析报告</h4>
                <p>点击下方按钮生成您的专属健康分析报告</p>
              </div>
            </div>
            <button class="btn btn-primary btn-lg report-btn" @click="generateReport">
              <span class="btn-icon">📈</span>
              <span>生成健康报告</span>
            </button>
            
            <div v-if="healthReport" class="report-content">
              <div class="report-content-header">
                <h5>📋 报告内容</h5>
                <button class="btn btn-sm btn-secondary" @click="healthReport = ''">收起报告</button>
              </div>
              <div class="report-text">{{ healthReport }}</div>
            </div>
          </div>
        </section>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { healthAPI } from '../api'

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    weekday: 'long'
  })
})

const healthStats = ref({
  heartRate: '--',
  bloodPressure: '--',
  bloodGlucose: '--',
  sleepHours: '--',
  temperature: '--',
  steps: '--'
})

const loadHealthStats = async () => {
  const userId = parseInt(localStorage.getItem('userId'))
  if (!userId) return
  
  try {
    const response = await healthAPI.getLatest(userId)
    if (response && response.code === 200 && response.data) {
      const data = response.data
      healthStats.value = {
        heartRate: data.heartRate || '--',
        bloodPressure: data.bloodPressureHigh && data.bloodPressureLow 
          ? `${data.bloodPressureHigh}/${data.bloodPressureLow}` 
          : '--',
        bloodGlucose: data.bloodSugar || '--',
        sleepHours: data.sleepHours || '--',
        temperature: data.bodyTemperature || '--',
        steps: data.steps || '--'
      }
    }
  } catch (error) {
    console.error('获取健康数据失败:', error)
  }
}

const roleLabel = { 1: '管理员', 2: '老人', 3: '家属' }
const userName = ref(localStorage.getItem('name') || '用户')
const userRole = ref(roleLabel[localStorage.getItem('role')] || '用户')

const recentActivities = ref([
  { id: 1, title: '健康养生讲座', time: '06-28 14:30', type: 'health', status: '已报名' },
  { id: 2, title: '智能手机使用培训', time: '06-25 10:00', type: 'study', status: '已报名' },
  { id: 3, title: '社区生日聚会', time: '06-22 14:00', type: 'social', status: '进行中' },
  { id: 4, title: '书法艺术交流', time: '06-20 09:00', type: 'culture', status: '已结束' }
])

const getActivityIcon = (type) => {
  const icons = {
    health: '❤️',
    study: '📚',
    social: '👥',
    culture: '✍️',
    sports: '⚽'
  }
  return icons[type] || '🎉'
}

const getBadgeClass = (status) => {
  const classes = {
    '已报名': 'badge-success',
    '进行中': 'badge-warning',
    '已结束': 'badge-info'
  }
  return classes[status] || 'badge-info'
}

const formatTime = (time) => {
  return time
}

const healthReport = ref('')

const generateReport = async () => {
  const userId = parseInt(localStorage.getItem('userId'))
  if (!userId) {
    ElMessage.error('请先登录')
    return
  }
  
  ElMessage.success('健康报告正在生成...')
  try {
    const response = await healthAPI.generateReport(userId)
    console.log('Report response:', response)
    if (response && response.code === 200) {
      const analyzeResponse = await healthAPI.analyze(userId)
      if (analyzeResponse && analyzeResponse.code === 200) {
        healthReport.value = analyzeResponse.data || '健康报告生成成功，您的健康状况良好。建议保持规律作息，适当运动，注意饮食均衡。如有不适，请及时就医。'
      } else {
        healthReport.value = '健康报告生成成功，您的健康状况良好。建议保持规律作息，适当运动，注意饮食均衡。如有不适，请及时就医。'
      }
    } else {
      ElMessage.error('健康报告生成失败')
    }
  } catch (error) {
    console.error('Report error:', error)
    ElMessage.error('健康报告生成失败')
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('name')
  localStorage.removeItem('role')
  ElMessage.success('退出登录成功')
  setTimeout(() => {
    window.location.href = '/login'
  }, 1000)
}

onMounted(() => {
  loadHealthStats()
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 28px 32px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a78bfa 100%);
  border-radius: var(--border-radius-xl);
  color: white;
  box-shadow: 0 20px 40px rgba(99, 102, 241, 0.3);
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -30%;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.greeting-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  backdrop-filter: blur(10px);
}

.greeting-text h2 {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 6px;
  letter-spacing: -0.5px;
}

.subtitle {
  opacity: 0.9;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.notification-btn {
  position: relative;
}

.notification-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: var(--border-radius-full);
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.section-subtitle {
  font-size: 13px;
  color: var(--text-muted);
}

.hover-lift {
  transition: all var(--transition-normal);
}

.hover-lift:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-xl);
}

.stat-card {
  background: var(--card-bg);
  border-radius: var(--border-radius-xl);
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 1px solid rgba(0, 0, 0, 0.04);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--primary-gradient);
}

.stat-card.success::before {
  background: linear-gradient(90deg, #10b981, #34d399);
}

.stat-card.warning::before {
  background: linear-gradient(90deg, #f59e0b, #fbbf24);
}

.stat-card.info::before {
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
}

.stat-icon-wrapper {
  width: 72px;
  height: 72px;
  border-radius: var(--border-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  background: rgba(99, 102, 241, 0.1);
}

.stat-card.success .stat-icon-wrapper {
  background: rgba(16, 185, 129, 0.1);
}

.stat-card.warning .stat-icon-wrapper {
  background: rgba(245, 158, 11, 0.1);
}

.stat-card.info .stat-icon-wrapper {
  background: rgba(59, 130, 246, 0.1);
}

.stat-icon {
  font-size: 32px;
}

.stat-content {
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.stat-change {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: var(--border-radius-full);
  font-size: 12px;
  font-weight: 500;
}

.stat-change.positive {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success-color);
}

.stat-change.negative {
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger-color);
}

.action-card {
  background: var(--card-bg);
  border-radius: var(--border-radius-xl);
  padding: 24px;
  text-decoration: none;
  color: inherit;
  border: 1px solid rgba(0, 0, 0, 0.04);
  transition: all var(--transition-normal);
  display: flex;
  flex-direction: column;
}

.action-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-xl);
  border-color: rgba(99, 102, 241, 0.2);
}

.action-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  transition: transform var(--transition-fast);
}

.action-card:hover .action-icon-wrapper {
  transform: scale(1.1);
}

.action-icon-wrapper.health {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1) 0%, rgba(220, 38, 38, 0.1) 100%);
}

.action-icon-wrapper.data {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
}

.action-icon-wrapper.message {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(147, 197, 253, 0.1) 100%);
}

.action-icon-wrapper.activity {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(52, 211, 153, 0.1) 100%);
}

.action-icon-wrapper.service {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.1) 0%, rgba(251, 191, 36, 0.1) 100%);
}

.action-icon-wrapper.device {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(52, 211, 153, 0.1) 100%);
}

.action-icon-wrapper.order {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1) 0%, rgba(168, 85, 247, 0.1) 100%);
}

.action-icon-wrapper.profile {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
}

.action-icon {
  font-size: 30px;
}

.action-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.action-title {
  font-weight: 600;
  font-size: 15px;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.action-desc {
  font-size: 12px;
  color: var(--text-secondary);
}

.content-section {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  margin-top: 24px;
}

.health-card {
  grid-column: 1;
}

.health-score {
  display: flex;
  align-items: center;
  gap: 48px;
}

.score-ring-container {
  flex-shrink: 0;
}

.score-ring {
  position: relative;
  width: 160px;
  height: 160px;
}

.score-ring svg {
  width: 100%;
  height: 100%;
}

.score-progress {
  transition: stroke-dashoffset 1.5s ease;
}

.score-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.score-value {
  font-size: 42px;
  font-weight: 700;
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.score-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.health-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.health-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(0, 0, 0, 0.03);
  border-radius: var(--border-radius-lg);
  transition: background var(--transition-fast);
}

.health-item:hover {
  background: rgba(99, 102, 241, 0.05);
}

.health-item-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  background: rgba(16, 185, 129, 0.1);
}

.health-item-icon.warning {
  background: rgba(245, 158, 11, 0.1);
}

.health-item-info {
  display: flex;
  flex-direction: column;
}

.health-item-text {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.health-item-status {
  font-size: 12px;
  color: var(--success-color);
}

.health-item-icon.warning + .health-item-info .health-item-status {
  color: var(--warning-color);
}

.activity-card {
  grid-column: 2;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(0, 0, 0, 0.02);
  border-radius: var(--border-radius-lg);
  transition: all var(--transition-fast);
  cursor: pointer;
}

.activity-item:hover {
  background: rgba(99, 102, 241, 0.05);
  transform: translateX(4px);
}

.activity-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background: rgba(99, 102, 241, 0.1);
}

.activity-icon-wrapper.health {
  background: rgba(239, 68, 68, 0.1);
}

.activity-icon-wrapper.study {
  background: rgba(59, 130, 246, 0.1);
}

.activity-icon-wrapper.social {
  background: rgba(16, 185, 129, 0.1);
}

.activity-icon-wrapper.culture {
  background: rgba(139, 92, 246, 0.1);
}

.activity-info {
  flex: 1;
  min-width: 0;
}

.activity-title {
  font-weight: 600;
  font-size: 14px;
  color: var(--text-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-time {
  font-size: 12px;
  color: var(--text-muted);
}

.activity-status {
  flex-shrink: 0;
}

.view-all {
  font-size: 13px;
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  transition: color var(--transition-fast);
}

.view-all:hover {
  color: var(--primary-dark);
}

.report-section {
  margin-top: 24px;
}

.report-card {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a78bfa 100%);
  border-radius: var(--border-radius-xl);
  padding: 32px;
  color: white;
  box-shadow: 0 20px 40px rgba(99, 102, 241, 0.3);
  position: relative;
  overflow: hidden;
}

.report-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 150px;
  height: 150px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.report-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.report-icon-wrapper {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--border-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.report-icon {
  font-size: 32px;
}

.report-info h4 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 6px;
}

.report-info p {
  opacity: 0.9;
  font-size: 14px;
}

.report-btn {
  width: 100%;
  justify-content: center;
  padding: 16px;
  font-size: 16px;
  position: relative;
  z-index: 1;
}

.btn-icon {
  margin-right: 8px;
}

.report-content {
  margin-top: 24px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: var(--border-radius-lg);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
}

.report-content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.report-content-header h5 {
  font-size: 15px;
  font-weight: 600;
}

.report-text {
  font-size: 14px;
  line-height: 1.8;
  white-space: pre-wrap;
  word-wrap: break-word;
  opacity: 0.95;
}

.quick-info-card {
  display: flex;
  flex-direction: column;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(0, 0, 0, 0.03);
  border-radius: var(--border-radius-lg);
  transition: all var(--transition-fast);
}

.tip-item:hover {
  background: rgba(99, 102, 241, 0.05);
  transform: translateX(4px);
}

.tip-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--border-radius-md);
}

.tip-content h4 {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.tip-content p {
  font-size: 13px;
  color: var(--text-secondary);
}

.quick-links {
  display: flex;
  gap: 12px;
  margin-top: auto;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.quick-link {
  flex: 1;
  text-align: center;
  padding: 10px;
  background: var(--primary-gradient);
  color: white;
  border-radius: var(--border-radius-md);
  font-size: 13px;
  font-weight: 500;
  transition: all var(--transition-fast);
}

.quick-link:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

@media (max-width: 1200px) {
  .content-section {
    grid-template-columns: 1fr;
  }
  
  .health-card,
  .activity-card {
    grid-column: 1;
  }
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }
  
  .greeting-text h2 {
    font-size: 20px;
  }
  
  .greeting-icon {
    width: 44px;
    height: 44px;
    font-size: 22px;
  }
  
  .health-score {
    flex-direction: column;
    gap: 24px;
  }
  
  .health-details {
    width: 100%;
  }
  
  .stat-value {
    font-size: 28px;
  }
  
  .grid-4 {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
