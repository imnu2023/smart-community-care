<template>
  <div class="fa-page">
    <!-- Header -->
    <header class="fa-hero">
      <div>
        <h1 class="fa-title">动态追踪</h1>
        <p class="fa-desc">{{ activeFilter === 'all' ? '家人的活动与健康动态' : typeLabel(activeFilter) + '相关动态' }}</p>
      </div>
      <button class="btn btn-secondary" @click="loadAll">
        <AppIcon name="refresh" size="16" /> 刷新
      </button>
    </header>

    <!-- Filter tabs -->
    <nav class="fa-tabs">
      <button
        v-for="t in typeFilters"
        :key="t.key"
        class="fa-tab"
        :class="{ 'fa-tab--active': activeFilter === t.key }"
        @click="activeFilter = t.key"
      >
        <AppIcon :name="t.icon" size="16" />
        {{ t.label }}
      </button>
    </nav>

    <!-- Loading -->
    <div v-if="loading" class="fa-skeleton">
      <div v-for="i in 4" :key="i" class="skeleton" style="height:120px;border-radius:var(--radius-xl);margin-bottom:var(--space-md)"></div>
    </div>

    <!-- Empty -->
    <div v-else-if="filteredActivities.length === 0" class="fa-empty">
      <AppIcon name="calendar" size="48" class="fa-empty-icon" />
      <h3>暂无动态</h3>
      <p>家人的活动参与和健康动态会显示在这里</p>
    </div>

    <!-- Activity Timeline + Cards -->
    <div v-else class="fa-content">
      <!-- Active / upcoming activities in a bento grid -->
      <section v-if="activeFilter === 'all' || activeFilter === 'activity'" class="fa-section">
        <h2 class="fa-section-title">
          <AppIcon name="calendar" size="18" class="fa-section-title-icon" />
          社区活动
        </h2>
        <div class="fa-activity-grid">
          <article
            v-for="(act, i) in activities"
            :key="act.id"
            class="fa-activity-card"
            :style="{ animationDelay: `${0.06 * i}s` }"
          >
            <div class="fa-activity-top">
              <span class="fa-activity-badge" :class="`fa-activity-badge--${act.type || 'culture'}`">
                {{ activityTypeLabel(act.type) }}
              </span>
              <span class="fa-activity-spots" v-if="act.maxParticipants">
                {{ act.currentParticipants || 0 }}/{{ act.maxParticipants }}人
              </span>
            </div>
            <h3 class="fa-activity-name">{{ act.title }}</h3>
            <p class="fa-activity-desc" v-if="act.description">{{ act.description }}</p>
            <div class="fa-activity-meta">
              <span><AppIcon name="clock" size="13" />{{ formatDate(act.startTime) }}</span>
              <span><AppIcon name="map-pin" size="13" />{{ act.location || '待定' }}</span>
            </div>
            <div class="fa-activity-foot">
              <span class="fa-activity-organizer">{{ act.organizer || '社区' }}</span>
              <button
                v-if="!registeredIds.includes(act.id)"
                class="btn btn-primary btn-sm"
                :disabled="act.maxParticipants && act.currentParticipants >= act.maxParticipants"
                @click="registerActivity(act)"
              >
                {{ act.maxParticipants && act.currentParticipants >= act.maxParticipants ? '已满' : '报名' }}
              </button>
              <button v-else class="btn btn-secondary btn-sm" @click="unregisterActivity(act)">
                已报名 · 取消
              </button>
            </div>
          </article>
        </div>
      </section>

      <!-- Health updates timeline -->
      <section v-if="activeFilter === 'all' || activeFilter === 'health'" class="fa-section">
        <h2 class="fa-section-title">
          <AppIcon name="pulse" size="18" class="fa-section-title-icon fa-section-title-icon--health" />
          健康动态
        </h2>
        <div class="fa-timeline">
          <div
            v-for="(item, i) in healthTimeline"
            :key="i"
            class="fa-timeline-item"
          >
            <span class="fa-timeline-dot" :class="`fa-timeline-dot--${item.type}`">
              <AppIcon :name="item.icon" size="12" />
            </span>
            <div class="fa-timeline-body">
              <span class="fa-timeline-time">{{ item.time }}</span>
              <p>{{ item.text }}</p>
            </div>
          </div>
          <div v-if="healthTimeline.length === 0" class="fa-timeline-empty">
            <p>暂无健康动态</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { activityAPI, healthAPI, relationAPI } from '../../api'
import AppIcon from '../../components/AppIcon.vue'

const familyId = ref(parseInt(localStorage.getItem('userId') || '0'))
const loading = ref(true)
const activities = ref([])
const registeredIds = ref([])
const eldersMap = ref({})
const healthTimeline = ref([])
const activeFilter = ref('all')

const typeFilters = [
  { key: 'all', label: '全部', icon: 'dashboard' },
  { key: 'activity', label: '活动', icon: 'calendar' },
  { key: 'health', label: '健康', icon: 'heart' }
]

const typeLabel = (k) => typeFilters.find(t => t.key === k)?.label || ''
const activityTypeLabel = (t) => ({ culture: '文化', sports: '运动', study: '学习', health: '健康', social: '社交' }[t] || t || '社区')

const filteredActivities = computed(() => {
  if (activeFilter.value === 'all' || activeFilter.value === 'activity') return activities.value
  return []
})

const formatDate = (s) => {
  if (!s) return ''
  try { return new Date(s).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) } catch (e) { return '' }
}

const loadAll = async () => {
  loading.value = true
  try {
    // Load elders
    const relRes = await relationAPI.getByFamilyMember(familyId.value)
    if (relRes?.code === 200 && relRes.data) {
      relRes.data.forEach(r => { eldersMap.value[r.elderId] = r })
    }
    // Load activities
    const [activeRes, upcomingRes, participantRes] = await Promise.all([
      activityAPI.getActive(),
      activityAPI.getUpcoming(),
      activityAPI.getByParticipant(familyId.value)
    ])
    const all = []
    if (activeRes?.code === 200 && activeRes.data) all.push(...activeRes.data)
    if (upcomingRes?.code === 200 && upcomingRes.data) all.push(...(upcomingRes.data || []))
    // Dedupe by ID
    const seen = new Set()
    activities.value = all.filter(a => { if (seen.has(a.id)) return false; seen.add(a.id); return true })

    if (participantRes?.code === 200 && participantRes.data) {
      registeredIds.value = participantRes.data.map(a => a.id).filter(Boolean)
    }
    // Build health timeline
    const timeline = []
    for (const [id, elder] of Object.entries(eldersMap.value)) {
      const hRes = await healthAPI.getLatest(id)
      if (hRes?.code === 200 && hRes.data) {
        timeline.push({
          type: 'health', icon: 'pulse',
          time: new Date(hRes.data.measuredAt).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }),
          text: `${elder.elderName}的最新健康数据已更新`
        })
        if (hRes.data.warningLevel > 0) {
          timeline.push({
            type: 'warning', icon: 'bell',
            time: new Date(hRes.data.measuredAt).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }),
            text: `${elder.elderName}有健康预警，请关注`
          })
        }
      }
    }
    healthTimeline.value = timeline
  } catch (e) { /* silent */ }
  loading.value = false
}

const registerActivity = async (act) => {
  try {
    const res = await activityAPI.register(act.id, familyId.value)
    if (res?.code === 200) {
      registeredIds.value.push(act.id)
      ElMessage.success('报名成功')
    }
  } catch (e) { ElMessage.error('报名失败') }
}

const unregisterActivity = async (act) => {
  try {
    await activityAPI.unregister(act.id, familyId.value)
    registeredIds.value = registeredIds.value.filter(id => id !== act.id)
    ElMessage.success('已取消报名')
  } catch (e) { ElMessage.error('取消失败') }
}

onMounted(() => loadAll())
</script>

<style scoped>
.fa-page { padding-bottom: var(--space-section); }

/* Header */
.fa-hero { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: var(--space-xl); flex-wrap: wrap; gap: var(--space-md); }
.fa-title { font-size: clamp(28px, 4vw, 40px); font-weight: var(--weight-bold); letter-spacing: -0.025em; color: var(--color-on-surface); margin: 0 0 var(--space-xs); text-wrap: balance; }
.fa-desc { font-size: var(--text-body-lg); color: var(--color-on-surface-variant); margin: 0; }

/* Filter tabs */
.fa-tabs { display: flex; gap: var(--space-xs); margin-bottom: var(--space-xl); }
.fa-tab {
  display: flex; align-items: center; gap: var(--space-xs);
  padding: 8px 18px; border-radius: var(--radius-full);
  border: 1px solid var(--color-outline-variant);
  background: var(--color-surface-container-lowest);
  font-size: var(--text-label-md); font-weight: var(--weight-medium);
  color: var(--color-on-surface-variant); font-family: inherit; cursor: pointer;
  transition: all 0.2s cubic-bezier(0.32, 0.72, 0, 1);
}
.fa-tab:hover { background: var(--color-surface-container-high); color: var(--color-on-surface); }
.fa-tab--active { background: var(--color-primary-fixed); border-color: var(--color-primary); color: var(--color-on-primary-fixed); font-weight: var(--weight-semibold); }

/* Skeleton / Empty */
.fa-skeleton { max-width: 960px; }
.fa-empty { max-width: 480px; text-align: center; padding: var(--space-2xl); border: 1px dashed var(--color-outline-variant); border-radius: var(--radius-2xl); color: var(--color-on-surface-variant); }
.fa-empty-icon { margin-bottom: var(--space-lg); opacity: 0.35; }
.fa-empty h3 { margin: 0 0 var(--space-xs); color: var(--color-on-surface); font-size: var(--text-headline-md); }

/* Sections */
.fa-section { margin-bottom: var(--space-2xl); }
.fa-section-title {
  font-size: var(--text-headline-md); font-weight: var(--weight-semibold);
  color: var(--color-on-surface); margin: 0 0 var(--space-md);
  display: flex; align-items: center; gap: var(--space-sm); letter-spacing: -0.01em;
}
.fa-section-title-icon { color: var(--color-primary); }
.fa-section-title-icon--health { color: var(--color-tertiary); }

/* Activity cards */
.fa-activity-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: var(--space-lg); max-width: 960px; }
@media (max-width: 767px) { .fa-activity-grid { grid-template-columns: 1fr; } }

.fa-activity-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  padding: var(--space-lg);
  box-shadow: var(--shadow-card);
  display: flex; flex-direction: column; gap: var(--space-sm);
  opacity: 0;
  animation: fa-fade-up 0.45s cubic-bezier(0.32, 0.72, 0, 1) forwards;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}
.fa-activity-card:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }
@keyframes fa-fade-up { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }

.fa-activity-top { display: flex; justify-content: space-between; align-items: center; }
.fa-activity-badge {
  padding: 3px 10px; border-radius: var(--radius-full);
  font-size: 11px; font-weight: var(--weight-semibold);
  background: var(--color-surface-container-high); color: var(--color-on-surface-variant);
}
.fa-activity-badge--culture { background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); }
.fa-activity-badge--sports { background: var(--color-tertiary-fixed); color: var(--color-tertiary); }
.fa-activity-badge--health { background: var(--color-tertiary-fixed-dim); color: var(--color-on-tertiary-fixed-variant); }
.fa-activity-spots { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fa-activity-name { font-size: var(--text-body-lg); font-weight: var(--weight-semibold); margin: 0; color: var(--color-on-surface); }
.fa-activity-desc { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: 0; line-height: 1.5; }
.fa-activity-meta { display: flex; gap: var(--space-lg); font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fa-activity-meta span { display: flex; align-items: center; gap: 4px; }
.fa-activity-foot { display: flex; justify-content: space-between; align-items: center; padding-top: var(--space-sm); border-top: 1px solid var(--color-outline-variant); }
.fa-activity-organizer { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }

/* Health timeline */
.fa-timeline {
  max-width: 720px; position: relative; padding-left: var(--space-xl);
  border-left: 2px solid var(--color-outline-variant); margin-left: var(--space-sm);
  display: flex; flex-direction: column; gap: var(--space-lg);
}
.fa-timeline-item { position: relative; }
.fa-timeline-dot {
  position: absolute; left: calc(-1 * var(--space-xl) - 11px);
  width: 22px; height: 22px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  border: 3px solid var(--color-surface-container-lowest);
  color: var(--color-on-primary); font-size: 12px;
}
.fa-timeline-dot--health { background: var(--color-tertiary); }
.fa-timeline-dot--warning { background: var(--color-error); }
.fa-timeline-time { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); display: block; margin-bottom: 4px; }
.fa-timeline-body p { margin: 0; font-size: var(--text-body-md); }
.fa-timeline-empty { text-align: center; padding: var(--space-lg); color: var(--color-on-surface-variant); }

@media (max-width: 767px) {
  .fa-tabs { gap: 4px; }
  .fa-tab { padding: 6px 14px; font-size: var(--text-label-sm); }
}
</style>
