<template>
  <div class="dv-page">
    <!-- Header -->
    <header class="dv-header">
      <button class="btn-back" @click="goBack"><AppIcon name="x" size="22" /></button>
      <div class="dv-header-text">
        <h1 class="text-headline-lg">智能家居控制中心</h1>
        <p class="text-body-md">管理您的智能设备，打造智慧生活</p>
      </div>
      <button class="btn btn-primary" @click="showAddModal = true">＋ 添加设备</button>
    </header>

    <!-- Stats -->
    <div class="dv-stats">
      <div class="dv-stat">
        <span class="dv-stat-icon dv-stat-icon--active">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
        </span>
        <div>
          <div class="dv-stat-num">{{ activeCount }}</div>
          <div class="dv-stat-label">运行中设备</div>
        </div>
      </div>
      <div class="dv-stat">
        <span class="dv-stat-icon dv-stat-icon--total">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24"><rect x="2" y="3" width="15" height="13"/><polygon points="17 8 22 8 22 3 17 3"/></svg>
        </span>
        <div>
          <div class="dv-stat-num">{{ deviceList.length }}</div>
          <div class="dv-stat-label">设备总数</div>
        </div>
      </div>
      <div class="dv-stat">
        <span class="dv-stat-icon dv-stat-icon--offline">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><line x1="12" y1="2" x2="12" y2="22"/></svg>
        </span>
        <div>
          <div class="dv-stat-num">{{ offlineCount }}</div>
          <div class="dv-stat-label">离线设备</div>
        </div>
      </div>
    </div>

    <!-- Device grid -->
    <div class="dv-grid">
      <div v-for="device in deviceList" :key="device.id" class="dv-card" :class="{ 'dv-card--active': device.status === 'online' || device.status === 'active' }">
        <div class="dv-card-top">
          <span class="dv-card-icon" :class="'dv-card-icon--' + device.status">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="26" height="26">
              <path v-if="device.deviceType === 'light'" d="M12 7h.01M12 12h.01M12 17h.01M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0z"/>
              <path v-else-if="device.deviceType === 'aircon'" d="M8 16h.01M12 16h.01M16 16h.01M16 10V6a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v4"/>
              <path v-else-if="device.deviceType === 'curtain'" d="M3 9h18M3 15h18"/><rect v-else-if="device.deviceType === 'curtain'" x="3" y="3" width="18" height="18" rx="2"/>
              <path v-else-if="device.deviceType === 'security'" d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><path v-else-if="device.deviceType === 'security'" d="M9 12l2 2 4-4"/>
              <path v-else d="M20.317 4.37a9 9 0 0 0-12.73 0"/>
            </svg>
          </span>
          <span class="badge" :class="statusBadge(device.status)">{{ getStatusText(device.status) }}</span>
        </div>

        <div class="dv-card-info">
          <h3>{{ device.deviceName }}</h3>
          <span class="dv-card-type">{{ getDeviceTypeName(device.deviceType) }}</span>
          <span class="dv-card-loc">{{ device.location }}</span>
        </div>

        <div v-if="device.deviceValue !== null" class="dv-card-reading">
          {{ formatValue(device.deviceType, device.deviceValue) }}
        </div>

        <div class="dv-card-actions">
          <button class="dv-btn dv-btn--on" @click="controlDevice(device.id, 'on')" :disabled="device.status === 'online' || device.status === 'active'">开启</button>
          <button class="dv-btn dv-btn--off" @click="controlDevice(device.id, 'off')" :disabled="device.status === 'inactive'">关闭</button>
          <button class="dv-btn dv-btn--del" @click="showDeleteConfirm(device)">删除</button>
        </div>

        <div v-if="device.status === 'online' || device.status === 'active'" class="dv-slider">
          <div v-if="device.deviceType === 'light'" class="dv-slider-row">
            <label>亮度</label>
            <input type="range" min="10" max="100" v-model="device.brightness" @change="adjustDevice(device)" />
            <span>{{ device.brightness }}%</span>
          </div>
          <div v-if="device.deviceType === 'aircon'" class="dv-slider-row">
            <label>温度</label>
            <input type="range" min="16" max="30" v-model="device.temperature" @change="adjustDevice(device)" />
            <span>{{ device.temperature }}°C</span>
          </div>
          <div v-if="device.deviceType === 'curtain'" class="dv-slider-row">
            <label>开合度</label>
            <input type="range" min="0" max="100" v-model="device.position" @change="adjustDevice(device)" />
            <span>{{ device.position }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Add modal -->
    <dialog v-if="showAddModal" class="dv-modal-overlay" @click="showAddModal = false">
      <div class="dv-modal" @click.stop>
        <div class="dv-modal-header"><h3>添加智能设备</h3><button class="btn btn-ghost btn-sm" @click="showAddModal = false">✕</button></div>
        <form @submit.prevent="addDevice" class="dv-modal-body">
          <div class="form-group"><label class="form-label">设备名称</label><input class="form-input" v-model="form.deviceName" placeholder="例如：客厅灯" required /></div>
          <div class="form-group"><label class="form-label">设备类型</label><select class="form-input" v-model="form.deviceType" required><option value="">请选择</option><option value="light">智能灯光</option><option value="aircon">智能空调</option><option value="curtain">智能窗帘</option><option value="security">安防设备</option><option value="sensor">传感器</option></select></div>
          <div class="form-group"><label class="form-label">设备ID</label><input class="form-input" v-model="form.deviceId" placeholder="设备唯一标识" required /></div>
          <div class="form-group"><label class="form-label">安装位置</label><input class="form-input" v-model="form.location" placeholder="例如：客厅" required /></div>
          <div class="dv-modal-footer"><button type="button" class="btn btn-secondary" @click="showAddModal = false">取消</button><button type="submit" class="btn btn-primary">确认添加</button></div>
        </form>
      </div>
    </dialog>

    <!-- Delete modal -->
    <dialog v-if="showDeleteModal" class="dv-modal-overlay" @click="showDeleteModal = false">
      <div class="dv-modal dv-modal--sm" @click.stop>
        <div class="dv-delete-icon"><AppIcon name="bell" :size="48" /></div>
        <h3 style="text-align:center">确认删除</h3>
        <p style="text-align:center;color:var(--color-on-surface-variant)">确定删除「{{ deleteDeviceData?.deviceName }}」吗？此操作不可撤销。</p>
        <div class="dv-modal-footer"><button class="btn btn-secondary" @click="showDeleteModal = false">取消</button><button class="btn btn-danger" @click="deleteDevice(deleteDeviceData?.id)">确认删除</button></div>
      </div>
    </dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { deviceAPI } from '../api'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const goBack = () => router.push('/dashboard')

const deviceList = ref([])
const showAddModal = ref(false)
const showDeleteModal = ref(false)
const deleteDeviceData = ref(null)
const form = reactive({ deviceName: '', deviceType: '', deviceId: '', location: '' })

const activeCount = computed(() => deviceList.value.filter(d => d.status === 'online' || d.status === 'active').length)
const offlineCount = computed(() => deviceList.value.filter(d => d.status === 'offline').length)

const loadDevices = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const res = await deviceAPI.getByUser(userId)
    if (res.code === 200) deviceList.value = (res.data || []).map(d => ({ ...d, brightness: d.brightness || 80, temperature: d.temperature || 25, position: d.position || 50 }))
  } catch (e) { console.error(e) }
}

const addDevice = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const res = await deviceAPI.add({ userId: parseInt(userId), deviceName: form.deviceName, deviceType: form.deviceType, deviceId: form.deviceId, location: form.location })
    if (res.code === 200) { ElMessage.success('添加成功'); showAddModal.value = false; Object.keys(form).forEach(k => form[k] = ''); loadDevices() }
  } catch (e) { ElMessage.error('添加失败') }
}

const controlDevice = async (id, action) => {
  try { const res = await deviceAPI.control(id, action); if (res.code === 200) { ElMessage.success(action === 'on' ? '已开启' : '已关闭'); loadDevices() } } catch (e) { ElMessage.error('控制失败') }
}

const adjustDevice = (d) => console.log('调节:', d.deviceName, d)
const showDeleteConfirm = (d) => { deleteDeviceData.value = d; showDeleteModal.value = true }
const deleteDevice = async (id) => {
  try { const res = await deviceAPI.delete(id); if (res.code === 200) { ElMessage.success('已删除'); showDeleteModal.value = false; loadDevices() } } catch (e) { ElMessage.error('删除失败') }
}

const getStatusText = (s) => ({ online: '运行中', active: '运行中', inactive: '已关闭' }[s] || '离线')
const statusBadge = (s) => ({ online: 'badge-success', active: 'badge-success', inactive: 'badge-neutral', offline: 'badge-danger' }[s] || 'badge-neutral')
const getDeviceTypeName = (t) => ({ light: '智能灯光', aircon: '智能空调', curtain: '智能窗帘', security: '安防设备', sensor: '传感器' }[t] || t)
const formatValue = (t, v) => t === 'aircon' ? `${v}°C` : t === 'sensor' ? `${v}%` : v

onMounted(() => loadDevices())
</script>

<style scoped>
.dv-page { min-height: 100vh; background: var(--color-surface); padding: var(--space-lg); }
.dv-header { display: flex; align-items: center; gap: var(--space-md); margin-bottom: var(--space-xl); }
.dv-header-text { flex: 1; }
.dv-header-text h1 { margin: 0; }
.dv-header-text p { margin: var(--space-xxs) 0 0; color: var(--color-on-surface-variant); }

.dv-stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: var(--space-md); margin-bottom: var(--space-xl); }
.dv-stat { display: flex; align-items: center; gap: var(--space-md); padding: var(--space-lg); background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); box-shadow: var(--shadow-card); }
.dv-stat-icon { width: 48px; height: 48px; border-radius: var(--radius-md); display: flex; align-items: center; justify-content: center; color: #fff; }
.dv-stat-icon--active { background: var(--color-tertiary); }
.dv-stat-icon--total { background: var(--color-primary); }
.dv-stat-icon--offline { background: var(--color-on-surface-variant); }
.dv-stat-num { font-size: 28px; font-weight: var(--weight-bold); color: var(--color-on-surface); }
.dv-stat-label { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }

.dv-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: var(--space-lg); }
.dv-card { background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); padding: var(--space-lg); box-shadow: var(--shadow-card); transition: box-shadow var(--duration-fast) var(--ease-out), transform var(--duration-fast) var(--ease-out); }
.dv-card:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }
.dv-card--active { border-color: var(--color-tertiary-fixed-dim); }
.dv-card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-md); }
.dv-card-icon { width: 56px; height: 56px; border-radius: var(--radius-lg); display: flex; align-items: center; justify-content: center; color: #fff; }
.dv-card-icon--online, .dv-card-icon--active { background: var(--color-tertiary); }
.dv-card-icon--inactive { background: var(--color-on-surface-variant); }
.dv-card-icon--offline { background: var(--color-outline-variant); }
.dv-card-info h3 { font-size: var(--text-headline-md); margin: 0 0 var(--space-xxs); }
.dv-card-type { display: inline-block; padding: 2px var(--space-xs); background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); border-radius: var(--radius-sm); font-size: var(--text-label-sm); margin-right: var(--space-xs); }
.dv-card-loc { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.dv-card-reading { margin: var(--space-sm) 0; padding: var(--space-sm) var(--space-md); background: var(--color-surface-container-low); border-radius: var(--radius-md); font-size: var(--text-body-lg); font-weight: var(--weight-bold); color: var(--color-primary); }
.dv-card-actions { display: flex; gap: var(--space-xs); margin-bottom: var(--space-sm); }
.dv-btn { flex: 1; padding: var(--space-xs) var(--space-sm); border: none; border-radius: var(--radius-md); cursor: pointer; font-size: var(--text-label-sm); font-weight: var(--weight-medium); font-family: inherit; min-height: var(--touch-min); transition: background var(--duration-fast) var(--ease-out); }
.dv-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.dv-btn--on { background: var(--color-tertiary-fixed); color: var(--color-on-tertiary-fixed-variant); }
.dv-btn--on:hover:not(:disabled) { background: var(--color-tertiary-fixed-dim); }
.dv-btn--off { background: var(--color-secondary-fixed); color: var(--color-on-secondary-fixed-variant); }
.dv-btn--off:hover:not(:disabled) { background: var(--color-secondary-fixed-dim); }
.dv-btn--del { background: var(--color-error-container); color: var(--color-on-error-container); }
.dv-btn--del:hover { background: #ffc4bc; }
.dv-slider { padding: var(--space-sm); background: var(--color-surface-container-low); border-radius: var(--radius-md); }
.dv-slider-row { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: var(--space-xs); font-size: var(--text-label-sm); }
.dv-slider-row:last-child { margin-bottom: 0; }
.dv-slider-row label { width: 48px; color: var(--color-on-surface-variant); }
.dv-slider-row input[type=range] { flex: 1; accent-color: var(--color-primary); height: 6px; }
.dv-slider-row span { width: 40px; text-align: right; font-weight: var(--weight-semibold); color: var(--color-primary); }

/* Modals */
.dv-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); border: none; width: 100%; height: 100%; }
.dv-modal { background: var(--color-surface-container-lowest); border-radius: var(--radius-2xl); width: 90%; max-width: 480px; padding: 0; box-shadow: var(--shadow-soft-hover); }
.dv-modal--sm { max-width: 380px; padding: var(--space-xl); }
.dv-modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-lg) var(--space-xl); border-bottom: 1px solid var(--color-outline-variant); }
.dv-modal-header h3 { margin: 0; font-size: var(--text-headline-md); }
.dv-modal-body { padding: var(--space-xl); }
.dv-modal-footer { display: flex; justify-content: flex-end; gap: var(--space-sm); padding-top: var(--space-lg); border-top: 1px solid var(--color-outline-variant); margin-top: var(--space-md); }
.dv-delete-icon { text-align: center; margin-bottom: var(--space-md); color: var(--color-error); }

@media (max-width: 767px) {
  .dv-page { padding: var(--space-md); }
  .dv-header { flex-wrap: wrap; }
  .dv-grid { grid-template-columns: 1fr; }
}
</style>
