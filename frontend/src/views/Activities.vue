<template>
  <div class="act-page">
    <header class="act-header">
      <button class="btn-back" @click="goBack"><AppIcon name="x" size="22" /></button>
      <div class="act-header-text">
        <h1 class="text-headline-lg">社区活动</h1>
        <p class="text-body-md">参与丰富多彩的社区活动，丰富晚年生活</p>
      </div>
      <button class="btn btn-primary" @click="showCreateModal = true">＋ 创建活动</button>
    </header>

    <div class="act-tabs">
      <button v-for="tab in tabs" :key="tab.value" class="act-tab" :class="{ 'act-tab--active': activeTab === tab.value }" @click="activeTab = tab.value">
        <AppIcon :name="tab.icon" :size="16" /> {{ tab.label }}
      </button>
    </div>

    <div class="act-grid">
      <div v-for="activity in activities" :key="activity.id" class="act-card" @click="showActivityDetail(activity)">
        <div class="act-card-cover">
          <span class="act-cover-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="40" height="40"><path :d="getActivityIcon(activity.type)"/></svg>
          </span>
          <span class="badge" :class="getActivityTypeBadge(activity.type)">{{ getActivityTypeName(activity.type) }}</span>
        </div>
        <div class="act-card-body">
          <h3>{{ activity.title }}</h3>
          <p class="act-desc">{{ activity.description }}</p>
          <div class="act-meta">
            <span><AppIcon name="map-pin" :size="14" /> {{ activity.location }}</span>
            <span><AppIcon name="clock" :size="14" /> {{ formatDateTime(activity.startTime) }}</span>
          </div>
          <div class="act-card-footer">
            <span><AppIcon name="people" :size="14" /> {{ activity.currentParticipants }}/{{ activity.maxParticipants }}</span>
            <button class="btn" :class="isParticipating(activity.id) ? 'btn-secondary btn-sm' : 'btn-primary btn-sm'" @click.stop="registerActivity(activity)">
              {{ isParticipating(activity.id) ? '取消报名' : '立即报名' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create modal -->
    <dialog v-if="showCreateModal" class="act-modal-overlay" @click="showCreateModal = false">
      <div class="act-modal" @click.stop>
        <div class="act-modal-header"><h3>创建活动</h3><button class="btn btn-ghost btn-sm" @click="showCreateModal = false">✕</button></div>
        <form @submit.prevent="handleCreate" class="act-modal-body">
          <div class="form-group"><label class="form-label">活动标题</label><input class="form-input" v-model="createForm.title" required /></div>
          <div class="form-group"><label class="form-label">活动描述</label><textarea class="form-input" v-model="createForm.description" rows="3" style="resize:vertical"></textarea></div>
          <div class="form-group"><label class="form-label">活动类型</label><select class="form-input" v-model="createForm.type"><option value="health">健康</option><option value="study">学习</option><option value="social">社交</option><option value="culture">文化</option><option value="sports">运动</option></select></div>
          <div class="form-group"><label class="form-label">活动地点</label><input class="form-input" v-model="createForm.location" required /></div>
          <div class="form-group"><label class="form-label">开始时间</label><input class="form-input" type="datetime-local" v-model="createForm.startTime" required /></div>
          <div class="form-group"><label class="form-label">最大人数</label><input class="form-input" type="number" v-model="createForm.maxParticipants" min="1" required /></div>
          <div class="act-modal-footer"><button type="button" class="btn btn-secondary" @click="showCreateModal = false">取消</button><button type="submit" class="btn btn-primary">创建</button></div>
        </form>
      </div>
    </dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { activityAPI } from '../api'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const goBack = () => router.push('/dashboard')

const tabs = [{ label: '全部', value: 'all', icon: 'clipboard' },{ label: '进行中', value: 'active', icon: 'pulse' },{ label: '即将开始', value: 'upcoming', icon: 'calendar' }]
const activities = ref([])
const activeTab = ref('all')
const showCreateModal = ref(false)
const createForm = ref({ title: '', description: '', type: 'health', location: '', startTime: '', maxParticipants: 20 })
const registeredIds = ref([])

const loadActivities = async () => {
  try {
    let res
    if (activeTab.value === 'active') res = await activityAPI.getActive()
    else if (activeTab.value === 'upcoming') res = await activityAPI.getUpcoming()
    else res = await activityAPI.getAll()
    if (res.code === 200) activities.value = res.data || []
  } catch (e) {}
}

const loadRegistrations = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try { const res = await activityAPI.getByParticipant(userId); if (res.code === 200) registeredIds.value = (res.data || []).map(a => a.id) } catch (e) {}
}

const isParticipating = (id) => registeredIds.value.includes(id)
const registerActivity = async (a) => {
  const userId = localStorage.getItem('userId'); if (!userId) return
  try {
    if (isParticipating(a.id)) { await activityAPI.unregister(a.id, userId); registeredIds.value = registeredIds.value.filter(x => x !== a.id); ElMessage.success('已取消') }
    else { await activityAPI.register(a.id, userId); registeredIds.value.push(a.id); ElMessage.success('报名成功') }
    loadActivities()
  } catch (e) { ElMessage.error('操作失败') }
}

const handleCreate = async () => {
  try {
    const res = await activityAPI.create(createForm.value)
    if (res.code === 200) { ElMessage.success('创建成功'); showCreateModal.value = false; createForm.value = { title: '', description: '', type: 'health', location: '', startTime: '', maxParticipants: 20 }; loadActivities() }
  } catch (e) { ElMessage.error('创建失败') }
}

const showActivityDetail = (a) => { /* navigate or open detail */ }

const getActivityIcon = (t) => ({ health: 'M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0', study: 'M4 19.5A2.5 2.5 0 0 1 6.5 17H20', social: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2', culture: 'M12 2L2 7l10 5 10-5-10-5z', sports: 'M12 12c2-2 6-2 8 0' }[t] || 'M12 2L2 7l10 5 10-5-10-5z')
const getActivityTypeName = (t) => ({ health: '健康', study: '学习', social: '社交', culture: '文化', sports: '运动' }[t] || t)
const getActivityTypeBadge = (t) => ({ health: 'badge-success', study: 'badge-info', social: 'badge-warning', culture: 'badge-neutral', sports: 'badge-info' }[t] || 'badge-neutral')
const formatDateTime = (s) => s ? new Date(s).toLocaleString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''

onMounted(() => { loadActivities(); loadRegistrations() })
</script>

<style scoped>
.act-page { min-height: 100vh; background: var(--color-surface); padding: var(--space-lg); }
.act-header { display: flex; align-items: center; gap: var(--space-md); margin-bottom: var(--space-xl); }
.act-header-text { flex: 1; }
.act-header-text h1 { margin: 0; }
.act-header-text p { margin: var(--space-xxs) 0 0; color: var(--color-on-surface-variant); }

.act-tabs { display: flex; gap: var(--space-xs); margin-bottom: var(--space-lg); background: var(--color-surface-container-lowest); padding: var(--space-xs); border-radius: var(--radius-lg); border: 1px solid var(--color-outline-variant); }
.act-tab { flex: 1; padding: var(--space-sm); border: none; border-radius: var(--radius-md); background: transparent; cursor: pointer; font-size: var(--text-label-md); font-family: var(--font-family); color: var(--color-on-surface-variant); transition: all var(--duration-fast) var(--ease-out); min-height: var(--touch-min); }
.act-tab--active { background: var(--color-primary); color: var(--color-on-primary); font-weight: var(--weight-semibold); }

.act-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: var(--space-lg); }
.act-card { background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); overflow: hidden; cursor: pointer; box-shadow: var(--shadow-card); transition: box-shadow var(--duration-fast) var(--ease-out), transform var(--duration-fast) var(--ease-out); }
.act-card:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }
.act-card-cover { display: flex; justify-content: space-between; align-items: flex-start; padding: var(--space-lg) var(--space-lg) 0; }
.act-cover-icon { width: 60px; height: 60px; border-radius: var(--radius-lg); background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); display: flex; align-items: center; justify-content: center; }
.act-card-body { padding: var(--space-lg); }
.act-card-body h3 { font-size: var(--text-headline-md); margin: 0 0 var(--space-xxs); }
.act-desc { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: 0 0 var(--space-sm); display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.act-meta { display: flex; gap: var(--space-md); font-size: var(--text-label-sm); color: var(--color-on-surface-variant); margin-bottom: var(--space-sm); }
.act-card-footer { display: flex; justify-content: space-between; align-items: center; padding-top: var(--space-sm); border-top: 1px solid var(--color-outline-variant); font-size: var(--text-label-md); }

.act-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); border: none; width: 100%; height: 100%; }
.act-modal { background: var(--color-surface-container-lowest); border-radius: var(--radius-2xl); width: 90%; max-width: 480px; box-shadow: var(--shadow-soft-hover); }
.act-modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-lg) var(--space-xl); border-bottom: 1px solid var(--color-outline-variant); }
.act-modal-header h3 { margin: 0; font-size: var(--text-headline-md); }
.act-modal-body { padding: var(--space-xl); }
.act-modal-footer { display: flex; gap: var(--space-sm); justify-content: flex-end; padding-top: var(--space-lg); border-top: 1px solid var(--color-outline-variant); margin-top: var(--space-md); }

@media (max-width: 767px) {
  .act-page { padding: var(--space-md); }
  .act-header { flex-wrap: wrap; }
  .act-grid { grid-template-columns: 1fr; }
}
</style>
