<template>
  <div class="max-w-3xl mx-auto px-container-padding-mobile md:px-container-padding-desktop py-6 md:py-8">
    <!-- Back nav -->
    <button class="flex items-center gap-1 font-label-md text-label-md text-on-surface-variant hover:text-on-surface transition-ui mb-6 cursor-pointer bg-transparent border-0 p-0" @click="$router.push('/elder')">
      <span class="material-symbols-outlined text-lg">arrow_back</span>
      返回首页
    </button>

    <h1 class="font-headline-lg text-headline-lg text-on-surface mb-6">健康详情</h1>

    <!-- ========== Loading ========== -->
    <div v-if="loading" class="space-y-6">
      <div class="skeleton-pulse h-48 rounded-xl" />
      <div class="skeleton-pulse h-32 rounded-xl" />
    </div>

    <!-- ========== Error ========== -->
    <div v-else-if="loadError" class="text-center py-16">
      <span class="material-symbols-outlined text-4xl text-on-surface-variant mb-3">error_outline</span>
      <p class="font-body-lg text-body-lg text-on-surface mb-2">加载失败</p>
      <p class="font-body-md text-body-md text-on-surface-variant mb-4">{{ loadError }}</p>
      <button class="bg-primary text-on-primary font-label-md text-label-md px-6 py-3 rounded-md touch-target transition-ui hover:bg-primary-hover" @click="fetchData">重试</button>
    </div>

    <!-- ========== Content ========== -->
    <template v-else>
      <!-- Status Banner -->
      <div
        class="rounded-xl p-6 mb-6"
        :class="warningLevel > 0 ? 'bg-error-container text-on-error-container' : 'bg-tertiary-fixed/30 text-on-tertiary-fixed-variant'"
      >
        <div class="flex items-center gap-3">
          <span class="material-symbols-outlined text-3xl">
            {{ warningLevel > 0 ? 'warning' : 'check_circle' }}
          </span>
          <div>
            <p class="font-headline-md text-headline-md">{{ warningLevel > 0 ? '需要关注' : '各项指标正常' }}</p>
            <p class="font-body-md text-body-md mt-0.5">{{ statusMessage }}</p>
          </div>
        </div>
      </div>

      <!-- Latest Metrics Grid -->
      <section class="mb-8">
        <h2 class="font-headline-md text-headline-md text-on-surface mb-4">最新测量数据</h2>

        <div v-if="!latestData" class="text-center py-10 bg-surface-container-lowest rounded-xl border border-outline-variant">
          <span class="material-symbols-outlined text-4xl text-on-surface-variant mb-3">monitor_heart</span>
          <p class="font-body-md text-body-md text-on-surface mb-1">暂无健康数据</p>
          <p class="font-label-md text-label-md text-on-surface-variant">绑定智能设备后可自动记录健康数据</p>
        </div>

        <div v-else class="grid grid-cols-2 md:grid-cols-3 gap-4">
          <div
            v-for="metric in metrics"
            :key="metric.key"
            class="bg-surface-container-lowest rounded-xl border border-outline-variant p-5"
          >
            <div class="flex items-center gap-2 mb-2">
              <span class="material-symbols-outlined text-xl" :class="metric.color">{{ metric.icon }}</span>
              <span class="font-label-md text-label-md text-on-surface-variant">{{ metric.label }}</span>
            </div>
            <div class="font-headline-md text-headline-md text-on-surface">
              {{ formatValue(metric) }}
              <span v-if="metric.unit && latestData[metric.key] != null" class="font-label-sm text-label-sm text-on-surface-variant font-normal">{{ metric.unit }}</span>
            </div>
            <p
              v-if="metric.normalRange"
              class="font-label-sm text-label-sm text-on-surface-variant mt-1"
            >
              正常范围: {{ metric.normalRange }}
            </p>
          </div>
        </div>
      </section>

      <!-- Weekly Trend Chart -->
      <section class="mb-8">
        <h2 class="font-headline-md text-headline-md text-on-surface mb-4">近7天趋势</h2>
        <div class="bg-surface-container-lowest rounded-xl border border-outline-variant p-5">
          <div v-if="!weeklyData || weeklyData.length === 0" class="text-center py-8">
            <p class="font-label-md text-label-md text-on-surface-variant">暂无趋势数据</p>
          </div>
          <div v-else ref="chartRef" class="w-full h-64" />
        </div>
      </section>

      <!-- Health Report -->
      <section>
        <div class="bg-surface-container-lowest rounded-xl border border-outline-variant p-6">
          <div class="flex items-center justify-between mb-3">
            <div>
              <h2 class="font-label-md text-label-md text-on-surface">健康报告</h2>
              <p class="font-label-sm text-label-sm text-on-surface-variant mt-0.5">基于您的健康数据生成分析报告</p>
            </div>
          </div>
          <div v-if="report" class="bg-surface rounded-lg p-4 font-body-md text-body-md text-on-surface-variant whitespace-pre-wrap">
            {{ report }}
          </div>
          <div v-else class="text-center py-4">
            <p class="font-label-md text-label-md text-on-surface-variant mb-3">还没有健康报告</p>
          </div>
          <button
            class="mt-4 bg-primary text-on-primary font-label-md text-label-md px-6 py-3 rounded-md touch-target transition-ui hover:bg-primary-hover disabled:opacity-50"
            :disabled="reportLoading"
            @click="generateReport"
          >
            <span v-if="reportLoading" class="flex items-center gap-2">
              <span class="material-symbols-outlined text-lg animate-spin">progress_activity</span>
              生成中...
            </span>
            <span v-else>生成健康报告</span>
          </button>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useUserStore } from '../stores/user.js'
import { healthAPI } from '../api/index.js'

let echarts = null

const userStore = useUserStore()

// ============ State ============
const loading = ref(true)
const loadError = ref('')
const latestData = ref(null)
const weeklyData = ref([])
const report = ref('')
const reportLoading = ref(false)
const chartRef = ref(null)

// ============ Computed ============
const warningLevel = computed(() => latestData.value?.warningLevel ?? 0)

const statusMessage = computed(() => {
  if (warningLevel.value === 0) return '继续保持良好的生活习惯'
  if (warningLevel.value <= 2) return '部分指标略有波动，建议关注'
  return '建议尽快联系社区医生进行咨询'
})

const metrics = [
  { key: 'heartRate', label: '心率', icon: 'favorite', unit: 'bpm', normalRange: '60-100', color: 'text-error' },
  { key: 'bloodPressureHigh', label: '收缩压', icon: 'monitor_heart', unit: 'mmHg', normalRange: '90-140', color: 'text-primary' },
  { key: 'bloodPressureLow', label: '舒张压', icon: 'monitor_heart', unit: 'mmHg', normalRange: '60-90', color: 'text-primary' },
  { key: 'bloodSugar', label: '血糖', icon: 'bloodtype', unit: 'mmol/L', normalRange: '3.9-6.1', color: 'text-secondary' },
  { key: 'bodyTemperature', label: '体温', icon: 'thermostat', unit: '°C', normalRange: '36.0-37.3', color: 'text-tertiary' },
  { key: 'sleepHours', label: '睡眠', icon: 'bedtime', unit: '小时', normalRange: '6-8', color: 'text-primary' },
]

function formatValue(metric) {
  const val = latestData.value?.[metric.key]
  if (val == null) return '--'
  if (metric.key === 'bloodSugar' || metric.key === 'bodyTemperature') {
    return Number(val).toFixed(1)
  }
  return val
}

// ============ Fetch ============
async function fetchData() {
  loading.value = true
  loadError.value = ''

  const userId = userStore.userId
  if (!userId) {
    loadError.value = '用户信息未加载'
    loading.value = false
    return
  }

  try {
    const [latestRes, weeklyRes, analyzeRes] = await Promise.all([
      healthAPI.getLatest(userId),
      healthAPI.getWeekly(userId),
      healthAPI.analyze(userId).catch(() => ({ data: '' })),
    ])
    latestData.value = latestRes.data
    weeklyData.value = weeklyRes.data || []
    report.value = analyzeRes.data || ''
    loading.value = false

    await nextTick()
    renderChart()
  } catch {
    loadError.value = '数据加载失败，请检查网络'
    loading.value = false
  }
}

// ============ Chart ============
async function renderChart() {
  if (!chartRef.value || !weeklyData.value || weeklyData.value.length === 0) return

  // 动态加载 echarts（按需加载，减小首屏包体积）
  if (!echarts) {
    echarts = await import('echarts')
  }

  const existing = echarts.getInstanceByDom(chartRef.value)
  if (existing) existing.dispose()

  const chart = echarts.init(chartRef.value)
  const dates = weeklyData.value.map(d => {
    const date = new Date(d.measuredAt || d.createdAt)
    return `${date.getMonth() + 1}/${date.getDate()}`
  })

  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['心率', '收缩压', '舒张压'], bottom: 0, textStyle: { fontSize: 12 } },
    grid: { top: 16, right: 16, bottom: 32, left: 40 },
    xAxis: { type: 'category', data: dates, axisLabel: { fontSize: 12 } },
    yAxis: [
      { type: 'value', name: 'bpm', axisLabel: { fontSize: 11 } },
      { type: 'value', name: 'mmHg', axisLabel: { fontSize: 11 } },
    ],
    series: [
      {
        name: '心率', type: 'line', smooth: true,
        data: weeklyData.value.map(d => d.heartRate ?? null),
        lineStyle: { color: '#ba1a1a', width: 2 },
        itemStyle: { color: '#ba1a1a' },
        symbol: 'circle', symbolSize: 6,
      },
      {
        name: '收缩压', type: 'line', smooth: true, yAxisIndex: 1,
        data: weeklyData.value.map(d => d.bloodPressureHigh ?? null),
        lineStyle: { color: '#0050cb', width: 2 },
        itemStyle: { color: '#0050cb' },
        symbol: 'circle', symbolSize: 6,
      },
      {
        name: '舒张压', type: 'line', smooth: true, yAxisIndex: 1,
        data: weeklyData.value.map(d => d.bloodPressureLow ?? null),
        lineStyle: { color: '#006825', width: 2, type: 'dashed' },
        itemStyle: { color: '#006825' },
        symbol: 'circle', symbolSize: 6,
      },
    ],
  })

  // Resize on window change
  const handleResize = () => chart.resize()
  window.addEventListener('resize', handleResize)
}

// ============ Generate Report ============
async function generateReport() {
  reportLoading.value = true
  try {
    const res = await healthAPI.generateReport(userStore.userId)
    report.value = res.data || '报告已生成'
  } catch {
    alert('报告生成失败，请稍后重试')
  } finally {
    reportLoading.value = false
  }
}

onMounted(fetchData)
</script>
