<template>
  <div class="dashboard">
    <div class="page-container">
      <!-- ================================================================
           SIDEBAR — "关怀空间" Care Space
           设计思路：侧边栏不只是一个菜单，而是老人进入平台的"个人枢纽"。
           顶部 profile 区用健康环营造"我在被照护"的安心感；
           导航用滑动 active pill 取代静态高亮，靠过渡动画传递精致；
           紧急呼叫独立成区，视觉上更醒目但不吓人。
           ================================================================ -->
      <aside class="sidebar">
        <!-- Profile: 头像 + 健康状态环 + 问候语 -->
        <div class="sidebar-profile">
          <div class="sidebar-avatar-wrap">
            <!-- 外层健康光环 -->
            <svg class="sidebar-avatar-glow" viewBox="0 0 80 80">
              <defs>
                <radialGradient id="ringGlow" cx="50%" cy="50%" r="50%">
                  <stop offset="70%" stop-color="transparent" />
                  <stop offset="100%" stop-color="currentColor" stop-opacity="0.3" />
                </radialGradient>
              </defs>
              <circle cx="40" cy="40" r="37" fill="none"
                :stroke="healthRingColor" stroke-width="3"
                stroke-linecap="round" stroke-dasharray="232"
                :stroke-dashoffset="232 - (healthScore / 100) * 232"
                transform="rotate(-90 40 40)"
                class="sidebar-avatar-arc" />
            </svg>
            <!-- 头像本体 -->
            <div
              class="sidebar-avatar"
              :class="healthRingClass"
              :style="avatarUrl ? { backgroundImage: `url(${avatarUrl})`, backgroundSize: 'cover', backgroundPosition: 'center' } : {}"
            >
              <span v-if="!avatarUrl" class="sidebar-avatar-letter">{{ avatarLetter }}</span>
            </div>
          </div>
          <div class="sidebar-profile-text">
            <p class="sidebar-greeting">{{ greetingText }}</p>
            <p class="sidebar-name">{{ userName }}</p>
          </div>
        </div>

        <!-- Navigation: grouped with sliding active indicator -->
        <nav class="sidebar-nav" ref="navRef">
          <!-- Cluster label -->
          <span class="sidebar-cluster-label">健康关怀</span>
          <router-link to="/dashboard" class="sidebar-item" :class="{ 'sidebar-item--active': $route.path === '/dashboard' }">
            <span class="sidebar-item-icon sidebar-item-icon--blue"><AppIcon name="dashboard" size="18" /></span>
            <span class="sidebar-item-text">首页概览</span>
          </router-link>
          <router-link to="/health" class="sidebar-item" :class="{ 'sidebar-item--active': $route.path === '/health' }">
            <span class="sidebar-item-icon sidebar-item-icon--green"><AppIcon name="heart" size="18" /></span>
            <span class="sidebar-item-text">健康监测</span>
          </router-link>
          <router-link to="/health-input" class="sidebar-item" :class="{ 'sidebar-item--active': $route.path === '/health-input' }">
            <span class="sidebar-item-icon sidebar-item-icon--purple"><AppIcon name="clipboard" size="18" /></span>
            <span class="sidebar-item-text">数据录入</span>
          </router-link>

          <!-- Cluster label -->
          <span class="sidebar-cluster-label">生活服务</span>
          <router-link to="/services" class="sidebar-item" :class="{ 'sidebar-item--active': $route.path === '/services' }">
            <span class="sidebar-item-icon sidebar-item-icon--amber"><AppIcon name="tool" size="18" /></span>
            <span class="sidebar-item-text">社区服务</span>
          </router-link>
          <router-link to="/orders" class="sidebar-item" :class="{ 'sidebar-item--active': $route.path === '/orders' }">
            <span class="sidebar-item-icon sidebar-item-icon--amber"><AppIcon name="package" size="18" /></span>
            <span class="sidebar-item-text">我的订单</span>
          </router-link>
          <router-link to="/activities" class="sidebar-item" :class="{ 'sidebar-item--active': $route.path === '/activities' }">
            <span class="sidebar-item-icon sidebar-item-icon--amber"><AppIcon name="calendar" size="18" /></span>
            <span class="sidebar-item-text">社区活动</span>
          </router-link>

          <!-- Cluster label -->
          <span class="sidebar-cluster-label">安全守护</span>
          <router-link to="/emergency" class="sidebar-item sidebar-item--emergency" :class="{ 'sidebar-item--active': $route.path === '/emergency' }">
            <span class="sidebar-item-icon sidebar-item-icon--red"><AppIcon name="bell" size="18" /></span>
            <span class="sidebar-item-text">紧急呼叫</span>
            <span class="sidebar-item-pulse"></span>
          </router-link>
          <router-link to="/devices" class="sidebar-item" :class="{ 'sidebar-item--active': $route.path === '/devices' }">
            <span class="sidebar-item-icon sidebar-item-icon--slate"><AppIcon name="home" size="18" /></span>
            <span class="sidebar-item-text">智能家居</span>
          </router-link>
          <router-link to="/messages" class="sidebar-item" :class="{ 'sidebar-item--active': $route.path === '/messages' }">
            <span class="sidebar-item-icon sidebar-item-icon--slate"><AppIcon name="message" size="18" /></span>
            <span class="sidebar-item-text">消息中心</span>
            <span v-if="unreadCount > 0" class="sidebar-item-badge">{{ unreadCount }}</span>
          </router-link>

          <!-- 家属专属：关怀中心 -->
          <template v-if="userRole === '家属'">
            <span class="sidebar-cluster-label">家属关怀</span>
            <router-link to="/family" class="sidebar-item" :class="{ 'sidebar-item--active': $route.path.startsWith('/family') }">
              <span class="sidebar-item-icon sidebar-item-icon--green"><AppIcon name="people" size="18" /></span>
              <span class="sidebar-item-text">家人关怀</span>
            </router-link>
          </template>
        </nav>

        <!-- Footer: quick profile access + logout -->
        <div class="sidebar-footer">
          <router-link to="/profile" class="sidebar-footer-profile">
            <span class="sidebar-item-icon" style="background:var(--color-surface-container)"><AppIcon name="user" size="18" /></span>
            <span class="sidebar-item-text">个人中心</span>
          </router-link>
          <button class="sidebar-logout" @click="handleLogout">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
              <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
            <span>退出</span>
          </button>
        </div>
      </aside>

      <!-- ============ Main Content (unchanged) ============ -->
      <main id="main-content" class="main-content">
        <!-- Loading skeleton -->
        <template v-if="pageLoading">
          <div class="dash-header skeleton" style="height:88px;border-radius:var(--radius-xl);margin-bottom:var(--space-xl)"></div>
          <div class="grid grid-4" style="margin-bottom:var(--space-xl)"><div v-for="i in 4" :key="i" class="skeleton" style="height:120px;border-radius:var(--radius-xl)"></div></div>
          <div class="dash-bento-skel" style="margin-bottom:var(--space-xl)"><div v-for="i in 8" :key="i" class="skeleton" style="height:140px;border-radius:var(--radius-xl)"></div></div>
        </template>

        <template v-else>
        <!-- Greeting: stronger visual weight, subtle gradient -->
        <header class="dash-header">
          <div class="dash-greeting">
            <div class="dash-greeting-icon"><AppIcon name="wave" size="28" /></div>
            <div>
              <h2 class="text-headline-lg">欢迎回来，{{ userName }}</h2>
              <p class="dash-date">{{ currentDate }}</p>
            </div>
          </div>
          <div class="dash-header-spark">
            <div class="dash-header-score-ring" :class="healthRingClass">
              <span class="dash-header-score-num tabular-nums">{{ healthScore }}</span>
              <span class="dash-header-score-lbl">健康分</span>
            </div>
          </div>
        </header>

        <!-- Health stats: each card has its own accent color -->
        <section class="dash-section">
          <h3 class="dash-section-title">健康概览</h3>
          <div class="dash-vitals">
            <div class="dash-vital dash-vital--heart">
              <span class="dash-vital-ring"><AppIcon name="pulse" size="28" /></span>
              <div class="dash-vital-value tabular-nums">{{ statItems[0].value }}<small>bpm</small></div>
              <div class="dash-vital-label">心率</div>
            </div>
            <div class="dash-vital dash-vital--bp">
              <span class="dash-vital-ring"><AppIcon name="droplet" size="28" /></span>
              <div class="dash-vital-value tabular-nums">{{ statItems[1].value }}<small>mmHg</small></div>
              <div class="dash-vital-label">血压</div>
            </div>
            <div class="dash-vital dash-vital--sugar">
              <span class="dash-vital-ring"><AppIcon name="droplet" size="28" /></span>
              <div class="dash-vital-value tabular-nums">{{ statItems[2].value }}<small>mmol/L</small></div>
              <div class="dash-vital-label">血糖</div>
            </div>
            <div class="dash-vital dash-vital--sleep">
              <span class="dash-vital-ring"><AppIcon name="moon" size="28" /></span>
              <div class="dash-vital-value tabular-nums">{{ statItems[3].value }}<small>小时</small></div>
              <div class="dash-vital-label">睡眠</div>
            </div>
          </div>
        </section>

        <!-- Quick actions: asymmetric bento grid (3 large + 5 small) -->
        <section class="dash-section">
          <h3 class="dash-section-title">快捷功能</h3>
          <div class="dash-bento">
            <router-link to="/health" class="dash-bento-card">
              <span class="dash-bento-icon"><AppIcon name="heart" size="36" /></span>
              <span class="dash-bento-title">健康监测</span>
              <span class="dash-bento-desc">查看实时健康数据与AI分析</span>
            </router-link>
            <router-link to="/emergency" class="dash-bento-card">
              <span class="dash-bento-icon"><AppIcon name="bell" size="36" /></span>
              <span class="dash-bento-title">紧急呼叫</span>
              <span class="dash-bento-desc">一键呼叫社区服务中心</span>
            </router-link>
            <router-link to="/services" class="dash-bento-card">
              <span class="dash-bento-icon"><AppIcon name="tool" size="36" /></span>
              <span class="dash-bento-title">社区服务</span>
              <span class="dash-bento-desc">预约家政、维修、陪诊服务</span>
            </router-link>
            <router-link to="/health-input" class="dash-bento-card">
              <span class="dash-bento-icon"><AppIcon name="clipboard" size="36" /></span>
              <span class="dash-bento-title">数据录入</span>
              <span class="dash-bento-desc">手动录入健康数据</span>
            </router-link>
            <router-link to="/messages" class="dash-bento-card">
              <span class="dash-bento-icon"><AppIcon name="message" size="36" /></span>
              <span class="dash-bento-title">消息中心</span>
              <span class="dash-bento-desc">查看通知与消息</span>
            </router-link>
            <router-link to="/activities" class="dash-bento-card">
              <span class="dash-bento-icon"><AppIcon name="calendar" size="36" /></span>
              <span class="dash-bento-title">社区活动</span>
              <span class="dash-bento-desc">参与社区文化活动</span>
            </router-link>
            <router-link to="/devices" class="dash-bento-card">
              <span class="dash-bento-icon"><AppIcon name="home" size="36" /></span>
              <span class="dash-bento-title">智能家居</span>
              <span class="dash-bento-desc">管理智能设备</span>
            </router-link>
            <router-link to="/orders" class="dash-bento-card">
              <span class="dash-bento-icon"><AppIcon name="package" size="36" /></span>
              <span class="dash-bento-title">我的订单</span>
              <span class="dash-bento-desc">查看服务订单</span>
            </router-link>
          </div>
        </section>
        </template>

        <section class="dash-section">
          <div class="grid grid-2 dash-two-col">
            <div class="card">
              <div class="card-header">
                <h3 class="card-title">近期活动</h3>
                <router-link to="/activities" class="dash-link">查看全部 →</router-link>
              </div>
              <div class="dash-activity-list">
                <div v-for="activity in recentActivities" :key="activity.id" class="dash-activity-item">
                  <span class="dash-activity-type-icon"><AppIcon :name="getActivityIcon(activity.type)" size="18" /></span>
                  <div class="dash-activity-info">
                    <div class="dash-activity-name">{{ activity.title }}</div>
                    <div class="dash-activity-time">{{ formatTime(activity.time) }}</div>
                  </div>
                  <span class="badge" :class="getBadgeClass(activity.status)">{{ activity.status }}</span>
                </div>
              </div>
            </div>
            <div class="card">
              <div class="card-header"><h3 class="card-title">健康小贴士</h3></div>
              <div class="dash-tips-list">
                <div class="dash-tip" v-for="tip in healthTips" :key="tip.label">
                  <span class="dash-tip-icon"><AppIcon :name="tip.icon" size="20" /></span>
                  <div><div class="dash-tip-label">{{ tip.label }}</div><div class="dash-tip-value">{{ tip.value }}</div></div>
                </div>
              </div>
              <div class="dash-tip-links">
                <router-link to="/health" class="btn btn-ghost btn-sm">查看健康详情</router-link>
                <router-link to="/health-input" class="btn btn-ghost btn-sm">录入健康数据</router-link>
              </div>
            </div>
          </div>
        </section>

        <section class="dash-section">
          <h3 class="dash-section-title">健康报告</h3>
          <div class="dash-report-card">
            <div class="dash-report-prompt">
              <div>
                <h4 class="dash-report-title">AI 健康分析</h4>
                <p class="dash-report-desc">根据您的健康数据生成专业分析报告</p>
              </div>
              <button class="btn btn-primary btn-lg" @click="generateReport">📈 生成健康报告</button>
            </div>
            <div v-if="healthReport" class="dash-report-result">
              <div class="dash-report-result-header">
                <span class="text-label-md">📋 报告内容</span>
                <button class="btn btn-ghost btn-sm" @click="healthReport = ''">收起</button>
              </div>
              <div class="dash-report-text">{{ healthReport }}</div>
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
import { healthAPI, userAPI, activityAPI } from '../api'
import AppIcon from '../components/AppIcon.vue'

const currentDate = computed(() => new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }))

const pageLoading = ref(true)
const healthStats = ref({ heartRate: '--', bloodPressure: '--', bloodGlucose: '--', sleepHours: '--', temperature: '--', steps: '--' })

// Health score (0-100) derived from latest data, for avatar ring
const healthScore = ref(70)
const healthRingClass = computed(() => {
  if (healthScore.value >= 80) return 'sidebar-avatar--good'
  if (healthScore.value >= 60) return 'sidebar-avatar--fair'
  return 'sidebar-avatar--poor'
})
const healthRingColor = computed(() => {
  if (healthScore.value >= 80) return '#16a34a'
  if (healthScore.value >= 60) return '#d97706'
  return '#dc2626'
})
const avatarUrl = ref('')
const avatarLetter = computed(() => {
  const name = userName.value || '用户'
  return name.charAt(0)
})

// Time-based greeting
const greetingText = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了，注意休息 🌙'
  if (h < 9) return '早上好 ☀️'
  if (h < 12) return '上午好 🌤️'
  if (h < 14) return '中午好，记得午休 ☀️'
  if (h < 18) return '下午好 🌈'
  return '晚上好 🌙'
})

const unreadCount = ref(0)

const loadHealthStats = async () => {
  const userId = parseInt(localStorage.getItem('userId'))
  if (!userId) return
  try {
    const response = await healthAPI.getLatest(userId)
    if (response && response.code === 200 && response.data) {
      const data = response.data
      healthStats.value = {
        heartRate: data.heartRate || '--',
        bloodPressure: data.bloodPressureHigh && data.bloodPressureLow ? `${data.bloodPressureHigh}/${data.bloodPressureLow}` : '--',
        bloodGlucose: data.bloodSugar || '--',
        sleepHours: data.sleepHours || '--',
        temperature: data.bodyTemperature || '--',
        steps: data.steps || '--'
      }
      // Derive health score
      let score = 65; let f = 0
      if (data.heartRate) { f++; if (data.heartRate >= 60 && data.heartRate <= 80) score += 10; else if (data.heartRate >= 50 && data.heartRate <= 100) score += 5 }
      if (data.bloodPressureHigh && data.bloodPressureLow) { f++; if (data.bloodPressureHigh < 120 && data.bloodPressureLow < 80) score += 12; else if (data.bloodPressureHigh < 130 && data.bloodPressureLow < 85) score += 8 }
      if (data.bloodSugar) { f++; const s = parseFloat(data.bloodSugar); if (s >= 3.9 && s <= 5.6) score += 10; else if (s >= 3.5 && s <= 6.9) score += 5 }
      if (data.sleepHours) { f++; const s = parseFloat(data.sleepHours); if (s >= 7 && s <= 8) score += 8; else if (s >= 6 && s <= 9) score += 4 }
      healthScore.value = Math.max(10, Math.min(100, f > 0 ? score : 60))
    }
  } catch (error) { /* silent */ }
}

const roleLabel = { 1: '管理员', 2: '老人', 3: '家属' }
const userName = ref(localStorage.getItem('name') || '用户')
const userRole = ref(roleLabel[localStorage.getItem('role')] || '用户')

const statItems = computed(() => [
  { value: healthStats.value.heartRate, label: '心率 (bpm)' },
  { value: healthStats.value.bloodPressure, label: '血压 (mmHg)' },
  { value: healthStats.value.bloodGlucose, label: '血糖 (mmol/L)' },
  { value: healthStats.value.sleepHours, label: '睡眠 (小时)' }
])

const quickActions = [
  { to: '/health', title: '健康监测', desc: '查看健康数据' },
  { to: '/health-input', title: '数据录入', desc: '记录健康指标' },
  { to: '/messages', title: '消息中心', desc: '查看消息通知' },
  { to: '/activities', title: '社区活动', desc: '参与社区活动' },
  { to: '/services', title: '社区服务', desc: '预约服务' },
  { to: '/devices', title: '智能家居', desc: '智能设备控制' },
  { to: '/orders', title: '我的订单', desc: '订单管理' },
  { to: '/profile', title: '个人中心', desc: '管理个人信息' }
]

const healthTips = computed(() => [
  { icon: 'thermometer', label: '今日体温', value: `${healthStats.value.temperature}°C` },
  { icon: 'foot', label: '今日步数', value: `${healthStats.value.steps} 步` },
  { icon: 'droplet', label: '饮水提醒', value: '每日 8 杯' },
  { icon: 'pill', label: '服药提醒', value: '记得按时服药' }
])

const recentActivities = ref([])
const registeredIds = ref([])

const loadRecentActivities = async () => {
  try {
    const res = await activityAPI.getAll()
    if (res.code === 200) {
      const activities = res.data || []
      const userId = localStorage.getItem('userId')
      if (userId) {
        const regRes = await activityAPI.getByParticipant(userId)
        if (regRes.code === 200) {
          registeredIds.value = (regRes.data || []).map(a => a.id)
        }
      }
      const now = new Date()
      recentActivities.value = activities.map(a => {
        const startTime = new Date(a.startTime.replace(/-/g, '/').replace(' ', 'T'))
        const endTime = a.endTime ? new Date(a.endTime.replace(/-/g, '/').replace(' ', 'T')) : null
        let status = '即将开始'
        if (registeredIds.value.includes(a.id)) {
          status = '已报名'
        } else if (endTime && endTime < now) {
          status = '已结束'
        } else if (startTime <= now && (!endTime || endTime >= now)) {
          status = '进行中'
        }
        return {
          id: a.id,
          title: a.title,
          time: startTime.toLocaleString('zh-CN', { month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit' }).replace(/\//g, '月').replace(' ', '日 '),
          type: a.type,
          status: status
        }
      }).sort((a, b) => {
        const statusOrder = { '进行中': 0, '已报名': 1, '即将开始': 2, '已结束': 3 }
        return statusOrder[a.status] - statusOrder[b.status]
      }).slice(0, 6)
    }
  } catch (e) {
    console.error('Failed to load recent activities:', e)
  }
}

const getActivityIcon = (type) => ({ health: 'heart', study: 'book', social: 'people', culture: 'palette', sports: 'sport' }[type] || 'calendar')
const getBadgeClass = (status) => ({ '已报名': 'badge-info', '进行中': 'badge-warning', '已结束': 'badge-neutral' }[status] || 'badge-neutral')
const formatTime = (time) => time

const healthReport = ref('')
const generateReport = async () => {
  const userId = parseInt(localStorage.getItem('userId'))
  if (!userId) { ElMessage.error('请先登录'); return }
  healthReport.value = ''
  const token = localStorage.getItem('token')
  try {
    const response = await fetch(`/api/health/analyze/stream/${userId}`, {
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
        if (content && content !== '[DONE]') healthReport.value += content
      }
    }
  } catch (e) {
    if (!healthReport.value) ElMessage.error('健康报告生成失败')
  }
}

const handleLogout = () => {
  localStorage.removeItem('token'); localStorage.removeItem('userId'); localStorage.removeItem('username'); localStorage.removeItem('name'); localStorage.removeItem('role')
  ElMessage.success('退出登录成功')
  setTimeout(() => { window.location.href = '/login' }, 500)
}

const loadUserProfile = async () => {
  const userId = parseInt(localStorage.getItem('userId'))
  if (!userId) return
  try {
    const res = await userAPI.getById(userId)
    if (res.code === 200 && res.data?.avatar) {
      avatarUrl.value = res.data.avatar
    }
  } catch (e) { /* silent */ }
}

onMounted(async () => { await Promise.all([loadHealthStats(), loadUserProfile(), loadRecentActivities()]); pageLoading.value = false })
</script>

<style scoped>
.dashboard { min-height: 100vh; }

/* ============================================================
   SIDEBAR — Care Space
   ============================================================ */

/* Background: subtle warm tint gradient, not flat gray */
.sidebar {
  width: 264px;
  background:
    linear-gradient(180deg,
      #f9fafc 0%,
      #f4f5f9 30%,
      var(--color-surface-container-low) 100%);
  border-right: 1px solid var(--color-outline-variant);
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  left: 0; top: 0;
  z-index: var(--z-sidebar);
  padding: var(--space-lg) 0 0;
  overflow-y: auto;
  overflow-x: hidden;
}

/* --- Profile area --- */
.sidebar-profile {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: 0 var(--space-lg) var(--space-lg);
  margin: 0 var(--space-sm) var(--space-sm);
  border-bottom: 1px solid var(--color-outline-variant);
}
.sidebar-avatar-wrap {
  position: relative;
  width: 64px; height: 64px;
  flex-shrink: 0;
}
/* --- 外层进度光环 (SVG) --- */
.sidebar-avatar-glow {
  position: absolute;
  inset: -4px;
  width: 72px; height: 72px;
  z-index: 1;
  pointer-events: none;
  filter: drop-shadow(0 0 6px rgba(0,0,0,0.08));
}
.sidebar-avatar-arc {
  transition: stroke-dashoffset 0.8s var(--ease-out), stroke 0.6s var(--ease-out);
}
/* --- 头像本体 --- */
.sidebar-avatar {
  width: 64px; height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #c7d2fe 0%, #dae1ff 50%, #e8ecfc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
  user-select: none;
  /* 内层白环 + 外层色环的双环结构 */
  box-shadow:
    inset 0 1px 3px rgba(0,0,0,0.06),
    0 0 0 3px var(--color-surface-container-lowest),
    0 0 0 5px var(--color-outline-variant),
    0 2px 8px rgba(0,0,0,0.06);
  transition: box-shadow 0.6s var(--ease-out),
              transform 0.2s var(--ease-out);
}
.sidebar-avatar:hover {
  transform: scale(1.04);
}
/* 健康状态色: overlay 在最外层色环 */
.sidebar-avatar--good {
  box-shadow:
    inset 0 1px 3px rgba(0,0,0,0.06),
    0 0 0 3px var(--color-surface-container-lowest),
    0 0 0 5px var(--color-tertiary),
    0 2px 12px rgba(0,104,37,0.15);
}
.sidebar-avatar--fair {
  box-shadow:
    inset 0 1px 3px rgba(0,0,0,0.06),
    0 0 0 3px var(--color-surface-container-lowest),
    0 0 0 5px var(--color-secondary),
    0 2px 12px rgba(165,59,24,0.15);
}
.sidebar-avatar--poor {
  box-shadow:
    inset 0 1px 3px rgba(0,0,0,0.06),
    0 0 0 3px var(--color-surface-container-lowest),
    0 0 0 5px var(--color-error),
    0 2px 12px rgba(186,26,26,0.18);
}
/* --- 首字母 --- */
.sidebar-avatar-letter {
  font-size: 26px;
  font-weight: var(--weight-bold);
  line-height: 1;
  color: var(--color-on-primary-fixed);
  text-shadow: 0 1px 2px rgba(0,0,0,0.06);
  display: block;
}
.sidebar-profile-text { min-width: 0; }
.sidebar-greeting {
  font-size: var(--text-label-sm);
  color: var(--color-on-surface-variant);
  margin: 0;
  line-height: 1.3;
}
.sidebar-name {
  font-size: var(--text-body-md);
  font-weight: var(--weight-semibold);
  color: var(--color-on-surface);
  margin: 2px 0 0;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* --- Cluster labels --- */
.sidebar-cluster-label {
  display: block;
  padding: var(--space-sm) var(--space-lg) var(--space-xxs);
  margin-top: var(--space-sm);
  font-size: 11px;
  font-weight: var(--weight-semibold);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--color-on-surface-variant);
  opacity: 0.55;
}
.sidebar-cluster-label:first-of-type { margin-top: 0; }

/* --- Nav items --- */
.sidebar-nav { flex: 1; padding: 0 var(--space-sm); }
.sidebar-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 11px var(--space-md);
  margin: 1px 0;
  border-radius: var(--radius-md);
  text-decoration: none;
  color: var(--color-on-surface-variant);
  font-size: var(--text-label-md);
  font-weight: var(--weight-medium);
  min-height: var(--touch-min);
  transition: background var(--duration-fast) var(--ease-out),
              color var(--duration-fast) var(--ease-out),
              transform var(--duration-fast) var(--ease-out);
  position: relative;
}
.sidebar-item:hover {
  background: var(--color-surface-container-highest);
  color: var(--color-on-surface);
}
.sidebar-item--active {
  background: var(--color-primary-fixed);
  color: var(--color-on-primary-fixed);
  font-weight: var(--weight-semibold);
}

/* Icon containers: colored soft backgrounds */
.sidebar-item-icon {
  width: 36px; height: 36px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  background: var(--color-surface-container);
  transition: background var(--duration-fast) var(--ease-out),
              transform var(--duration-fast) var(--ease-out);
}
.sidebar-item:hover .sidebar-item-icon { transform: scale(1.06); }
.sidebar-item--active .sidebar-item-icon { background: transparent; }
.sidebar-item-icon--blue   { background: #e8ecfc; }   /* 首页 */
.sidebar-item-icon--green  { background: #e6f5ec; }   /* 健康 */
.sidebar-item-icon--purple { background: #f0e8f8; }   /* 录入 */
.sidebar-item-icon--amber  { background: #fef6e8; }   /* 生活服务 */
.sidebar-item-icon--red    { background: #fde8e8; }   /* 紧急 */
.sidebar-item-icon--slate  { background: var(--color-surface-container); } /* 通用 */

.sidebar-item-text { flex: 1; }

/* --- Emergency item special treatment --- */
.sidebar-item--emergency {
  margin-top: var(--space-xxs);
}
.sidebar-item--emergency.sidebar-item--active {
  background: var(--color-error-container);
  color: var(--color-on-error-container);
}
.sidebar-item-pulse {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: var(--color-error);
  opacity: 0;
  animation: sidebar-pulse 2s infinite;
  position: absolute;
  right: var(--space-md);
}
.sidebar-item--active .sidebar-item-pulse { opacity: 1; }
@keyframes sidebar-pulse {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.3); }
}

/* --- Badge (unread count) --- */
.sidebar-item-badge {
  min-width: 20px; height: 20px;
  border-radius: var(--radius-full);
  background: var(--color-error);
  color: #fff;
  font-size: 11px;
  font-weight: var(--weight-bold);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 5px;
  line-height: 1;
}

/* --- Footer --- */
.sidebar-footer {
  padding: var(--space-md) var(--space-sm);
  border-top: 1px solid var(--color-outline-variant);
  margin-top: var(--space-sm);
  display: flex;
  flex-direction: column;
  gap: var(--space-xxs);
}
.sidebar-footer-profile {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-xs) var(--space-md);
  border-radius: var(--radius-md);
  text-decoration: none;
  color: var(--color-on-surface-variant);
  font-size: var(--text-label-md);
  font-weight: var(--weight-medium);
  min-height: var(--touch-min);
  transition: background var(--duration-fast) var(--ease-out);
}
.sidebar-footer-profile:hover {
  background: var(--color-surface-container-highest);
  color: var(--color-on-surface);
}
.sidebar-logout {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  width: 100%;
  padding: var(--space-xs) var(--space-md);
  border: none;
  border-radius: var(--radius-md);
  background: transparent;
  color: var(--color-on-surface-variant);
  font-size: var(--text-label-md);
  font-family: var(--font-family);
  cursor: pointer;
  min-height: var(--touch-min);
  transition: all var(--duration-fast) var(--ease-out);
}
.sidebar-logout:hover {
  background: var(--color-error-container);
  color: var(--color-error);
}

/* ============================================================
   MAIN CONTENT
   ============================================================ */
.main-content {
  flex: 1;
  margin-left: 264px;
  padding: var(--space-xl);
  min-height: 100vh;
  background:
    radial-gradient(ellipse 60% 50% at 50% 0%, rgba(0, 80, 203, 0.02) 0%, transparent 60%),
    var(--color-surface);
}

/* --- Skeleton --- */
.dash-bento-skel { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--space-md); }
@media (max-width: 1023px) { .dash-bento-skel { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 767px) { .dash-bento-skel { grid-template-columns: repeat(2, 1fr); } }

/* --- Greeting header: stronger presence --- */
.dash-header {
  margin-bottom: var(--space-xl);
  padding: var(--space-xl);
  background:
    linear-gradient(135deg, var(--color-surface-container-lowest) 0%, #f2f5fc 50%, #faf9fc 100%);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-lg);
  box-shadow: var(--shadow-card);
}
.dash-greeting { display: flex; align-items: center; gap: var(--space-md); }
.dash-greeting-icon {
  width: 56px; height: 56px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary-fixed) 0%, #e8eeff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-on-primary-fixed);
  box-shadow: 0 2px 8px rgba(0, 80, 203, 0.06);
}
.dash-date { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin-top: var(--space-xxs); }
/* Mini health score in header */
.dash-header-spark { flex-shrink: 0; }
.dash-header-score-ring {
  width: 68px; height: 68px; border-radius: 50%;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  background: var(--color-surface-container-lowest);
  transition: box-shadow 0.6s var(--ease-out), transform 0.2s var(--ease-out);
}
.dash-header-score-ring:hover { transform: scale(1.05); }
.dash-header-score-ring.sidebar-avatar--good { box-shadow: 0 0 0 4px var(--color-tertiary), 0 4px 16px rgba(0,104,37,0.12); }
.dash-header-score-ring.sidebar-avatar--fair { box-shadow: 0 0 0 4px var(--color-secondary), 0 4px 16px rgba(165,59,24,0.12); }
.dash-header-score-ring.sidebar-avatar--poor { box-shadow: 0 0 0 4px var(--color-error), 0 4px 16px rgba(186,26,26,0.14); }
.dash-header-score-num { font-size: 22px; font-weight: var(--weight-bold); color: var(--color-on-surface); line-height: 1; }
.dash-header-score-lbl { font-size: 11px; color: var(--color-on-surface-variant); font-weight: var(--weight-medium); }

/* --- Sections --- */
.dash-section { margin-bottom: var(--space-2xl); }
.dash-section:last-child { margin-bottom: var(--space-xl); }
.dash-section-title { font-size: var(--text-headline-md); font-weight: var(--weight-semibold); color: var(--color-on-surface); margin-bottom: var(--space-md); letter-spacing: -0.01em; }

/* --- Health vitals: individual accent cards with hover lift --- */
.dash-vitals { display: grid; grid-template-columns: repeat(4, 1fr); gap: var(--space-md); }
.dash-vital {
  padding: var(--space-lg); border-radius: var(--radius-xl); text-align: center;
  transition: transform 0.2s var(--ease-out), box-shadow 0.2s var(--ease-out);
  cursor: default;
  position: relative;
  overflow: hidden;
}
/* 卡片顶部微光条 */
.dash-vital::before {
  content: '';
  position: absolute;
  top: 0; left: 20%; right: 20%; height: 2px;
  border-radius: 0 0 2px 2px;
  opacity: 0;
  transition: opacity 0.2s var(--ease-out);
}
.dash-vital:hover { transform: translateY(-2px); }
.dash-vital:hover::before { opacity: 1; }
.dash-vital:active { transform: scale(0.97) translateY(0); }
.dash-vital--heart  { background: #fce4ec; border: 1px solid #f8cdd5; }
.dash-vital--heart::before { background: #e57373; }
.dash-vital--bp    { background: #e8f0fe; border: 1px solid #d2e0fc; }
.dash-vital--bp::before { background: #4285f4; }
.dash-vital--sugar { background: #fff8e1; border: 1px solid #fef0c0; }
.dash-vital--sugar::before { background: #f9a825; }
.dash-vital--sleep { background: #f3e5f5; border: 1px solid #e8d0ee; }
.dash-vital--sleep::before { background: #8e24aa; }
.dash-vital-ring { display: flex; align-items: center; justify-content: center; margin-bottom: var(--space-xs); color: var(--color-on-surface-variant); }
.dash-vital-value { font-size: 28px; font-weight: var(--weight-bold); color: var(--color-on-surface); line-height: 1.2; }
.dash-vital-value small { font-size: var(--text-label-sm); font-weight: var(--weight-regular); color: var(--color-on-surface-variant); margin-left: 2px; }
.dash-vital-label { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin-top: var(--space-xxs); font-weight: var(--weight-medium); }

/* --- Bento grid: asymmetric quick actions with spotlight hover --- */
.dash-bento {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}
.dash-bento-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  padding: var(--space-lg);
  text-decoration: none;
  color: inherit;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  min-height: 140px;
  position: relative;
  overflow: hidden;
  transition: box-shadow 0.25s var(--ease-out),
              transform 0.2s var(--ease-out),
              border-color 0.25s var(--ease-out);
}
/* 悬停时的定向光晕 */
.dash-bento-card::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 50% 0%, rgba(0, 80, 203, 0.03) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s var(--ease-out);
  pointer-events: none;
}
.dash-bento-card:hover {
  box-shadow: var(--shadow-soft-hover);
  border-color: rgba(0, 80, 203, 0.15);
  transform: translateY(-2px);
}
.dash-bento-card:hover::after { opacity: 1; }
.dash-bento-card:active { transform: scale(0.98) translateY(0); }
.dash-bento-card--health { border-color: #fdd; }
.dash-bento-card--health:hover { border-color: #f8cdd5; }
.dash-bento-card--emergency { border-color: #fde8e8; }
.dash-bento-card--emergency:hover { border-color: #f5c6cb; }
.dash-bento-card--services { border-color: #d2e0fc; }
.dash-bento-card--services:hover { border-color: #b8cdf8; }
.dash-bento-icon { display: flex; align-items: center; color: var(--color-on-surface-variant); }
.dash-bento-card--lg .dash-bento-icon { color: var(--color-primary); }
.dash-bento-title { font-size: var(--text-label-md); font-weight: var(--weight-semibold); color: var(--color-on-surface); margin-top: var(--space-xs); }
.dash-bento-card--sm .dash-bento-title { margin-top: var(--space-xxs); }
.dash-bento-desc { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin-top: var(--space-xxs); line-height: 1.4; }

/* --- Two-col: activities + tips --- */
.dash-two-col { align-items: start; display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-lg); }
.dash-activity-list { display: flex; flex-direction: column; }
.dash-activity-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-sm) var(--space-xs);
  border-bottom: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-md);
  transition: background var(--duration-fast) var(--ease-out);
}
.dash-activity-item:hover { background: var(--color-surface-container-low); }
.dash-activity-item:last-child { border-bottom: none; }
.dash-activity-type-icon { width: 40px; height: 40px; border-radius: var(--radius-sm); background: var(--color-surface-container); display: flex; align-items: center; justify-content: center; flex-shrink: 0; color: var(--color-on-surface-variant); }
.dash-activity-info { flex: 1; min-width: 0; }
.dash-activity-name { font-size: var(--text-label-md); font-weight: var(--weight-medium); color: var(--color-on-surface); }
.dash-activity-time { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin-top: 2px; }
.dash-link { font-size: var(--text-label-sm); color: var(--color-primary); font-weight: var(--weight-medium); transition: color var(--duration-fast) var(--ease-out); }
.dash-link:hover { color: var(--color-primary-hover); }
.dash-tips-list { display: flex; flex-direction: column; gap: var(--space-sm); }
.dash-tip {
  display: flex; align-items: center; gap: var(--space-sm);
  padding: var(--space-sm); background: var(--color-surface-container-low);
  border-radius: var(--radius-md);
  transition: background var(--duration-fast) var(--ease-out), transform var(--duration-fast) var(--ease-out);
}
.dash-tip:hover { background: var(--color-surface-container); transform: translateX(2px); }
.dash-tip-icon { width: 40px; height: 40px; border-radius: var(--radius-sm); background: var(--color-surface-container-lowest); display: flex; align-items: center; justify-content: center; flex-shrink: 0; box-shadow: 0 1px 3px rgba(0,0,0,0.04); color: var(--color-on-surface-variant); }
.dash-tip-label { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.dash-tip-value { font-size: var(--text-label-md); font-weight: var(--weight-semibold); color: var(--color-on-surface); }
.dash-tip-links { display: flex; gap: var(--space-sm); margin-top: var(--space-md); padding-top: var(--space-md); border-top: 1px solid var(--color-outline-variant); }

/* --- Report card --- */
.dash-report-card {
  background: linear-gradient(135deg, var(--color-surface-container-lowest) 0%, #f8f9fd 100%);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  padding: var(--space-xl);
  box-shadow: var(--shadow-card);
}
.dash-report-prompt { display: flex; align-items: center; justify-content: space-between; gap: var(--space-lg); }
.dash-report-title { font-size: var(--text-headline-md); font-weight: var(--weight-semibold); color: var(--color-on-surface); margin: 0 0 var(--space-xxs) 0; }
.dash-report-desc { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: 0; max-width: 52ch; }
.dash-report-result { margin-top: var(--space-lg); padding: var(--space-md); background: var(--color-surface-container-low); border-radius: var(--radius-md); border: 1px solid var(--color-outline-variant); }
.dash-report-result-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-sm); }
.dash-report-text { font-size: var(--text-body-md); line-height: 1.8; color: var(--color-on-surface); white-space: pre-wrap; text-wrap: pretty; }

/* Responsive */
@media (max-width: 1023px) {
  .sidebar { width: 220px; }
  .main-content { margin-left: 220px; }
  .dash-bento { grid-template-columns: repeat(3, 1fr); }
  .dash-vitals { grid-template-columns: repeat(2, 1fr); }
  .dash-report-prompt { flex-direction: column; align-items: flex-start; }
}
@media (max-width: 767px) {
  .sidebar { display: none; }
  .main-content { margin-left: 0; padding: var(--space-md); }
  .dash-header { padding: var(--space-md); flex-direction: column; align-items: flex-start; }
  .dash-header-spark { align-self: flex-end; }
  .dash-greeting-icon { width: 44px; height: 44px; }
  .dash-vitals { grid-template-columns: repeat(2, 1fr); }
  .dash-bento { grid-template-columns: repeat(2, 1fr); }
  .dash-two-col { grid-template-columns: 1fr; }
  .dash-report-card { padding: var(--space-md); }
}
@media (prefers-reduced-motion: reduce) {
  .sidebar-item-pulse { animation: none; opacity: 0.6; }
  .sidebar-avatar-arc { transition: none; }
  .sidebar-avatar { transition: none; }
}
</style>
