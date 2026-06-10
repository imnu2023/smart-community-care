<template>
  <div class="devices-page">
    <div class="page-header">
      <div class="header-content">
        <h1>智能家居控制中心</h1>
        <p>管理您的智能设备，打造智慧生活</p>
      </div>
      <button class="add-device-btn" @click="showAddModal = true">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 4v16m8-8H4"/>
        </svg>
        添加设备
      </button>
    </div>

    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon active">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ activeCount }}</span>
          <span class="stat-label">运行中设备</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon total">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="2" y="3" width="15" height="13"/>
            <polygon points="17 8 22 8 22 3 17 3"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ deviceList.length }}</span>
          <span class="stat-label">设备总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon offline">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
            <line x1="12" y1="2" x2="12" y2="22"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ offlineCount }}</span>
          <span class="stat-label">离线设备</span>
        </div>
      </div>
    </div>

    <div class="device-grid">
      <div 
        v-for="device in deviceList" 
        :key="device.id" 
        class="device-card"
        :class="{ active: device.status === 'online' || device.status === 'active' }"
      >
        <div class="card-header">
          <div class="device-icon" :class="device.status">
            <svg v-if="device.deviceType === 'light'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 7h.01M12 12h.01M12 17h.01M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0z"/>
            </svg>
            <svg v-else-if="device.deviceType === 'aircon'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M8 16h.01M12 16h.01M16 16h.01M16 10V6a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v4m16 0h-2.5M6 10h-2.5M6 10h2.5M6 10h6m6 0h2.5M18 10h-2.5M12 10h2.5M12 10H6"/>
            </svg>
            <svg v-else-if="device.deviceType === 'curtain'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
              <path d="M3 9h18M3 15h18"/>
            </svg>
            <svg v-else-if="device.deviceType === 'security'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              <path d="M9 12l2 2 4-4"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20.317 4.37a9 9 0 0 0-12.73 0M16 14.016a9 9 0 0 1-11.628 0M18.741 16.11A12 12 0 0 1 12 20.05a12 12 0 0 1-6.74-3.94M16 14.016V16a2 2 0 1 1-4 0v-.016M4 14.016V16a2 2 0 1 0 4 0v-.016"/>
            </svg>
          </div>
          <div class="device-status" :class="device.status">
            <span class="status-indicator"></span>
            {{ getStatusText(device.status) }}
          </div>
        </div>

        <div class="device-info">
          <h3>{{ device.deviceName }}</h3>
          <span class="device-type">{{ getDeviceTypeName(device.deviceType) }}</span>
          <span class="device-location">📍 {{ device.location }}</span>
        </div>

        <div v-if="device.deviceValue !== null" class="device-readings">
          <div class="reading-item">
            <span class="reading-label">当前值</span>
            <span class="reading-value">{{ formatValue(device.deviceType, device.deviceValue) }}</span>
          </div>
        </div>

        <div class="device-controls">
          <button 
            class="control-btn power-on" 
            @click="controlDevice(device.id, 'on')"
            :disabled="device.status === 'online' || device.status === 'active'"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M13 2l3 6 6 1.5M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
            </svg>
            开启
          </button>
          <button 
            class="control-btn power-off" 
            @click="controlDevice(device.id, 'off')"
            :disabled="device.status === 'inactive'"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10z"/>
              <path d="M9 9h6v6H9z"/>
            </svg>
            关闭
          </button>
          <button class="control-btn delete" @click="showDeleteConfirm(device)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 6h18M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
            </svg>
            删除
          </button>
        </div>

        <div v-if="device.status === 'online' || device.status === 'active'" class="device-slider-panel">
          <div class="slider-header">
            <span>⚡ 设备控制</span>
            <span class="power-active"></span>
          </div>
          <div v-if="device.deviceType === 'light'" class="slider-item">
            <label>亮度</label>
            <div class="slider-wrapper">
              <input type="range" min="10" max="100" v-model="device.brightness" @change="adjustDevice(device)">
              <span>{{ device.brightness }}%</span>
            </div>
          </div>
          <div v-if="device.deviceType === 'aircon'" class="slider-item">
            <label>温度</label>
            <div class="slider-wrapper">
              <input type="range" min="16" max="30" v-model="device.temperature" @change="adjustDevice(device)">
              <span>{{ device.temperature }}°C</span>
            </div>
          </div>
          <div v-if="device.deviceType === 'curtain'" class="slider-item">
            <label>开合度</label>
            <div class="slider-wrapper">
              <input type="range" min="0" max="100" v-model="device.position" @change="adjustDevice(device)">
              <span>{{ device.position }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>添加智能设备</h3>
          <button class="close-btn" @click="showAddModal = false">×</button>
        </div>
        <form @submit.prevent="addDevice">
          <div class="form-group">
            <label>设备名称</label>
            <input v-model="form.deviceName" placeholder="例如：客厅灯、卧室空调" required />
          </div>
          <div class="form-group">
            <label>设备类型</label>
            <select v-model="form.deviceType" required>
              <option value="">请选择设备类型</option>
              <option value="light">智能灯光</option>
              <option value="aircon">智能空调</option>
              <option value="curtain">智能窗帘</option>
              <option value="security">安防设备</option>
              <option value="sensor">传感器</option>
            </select>
          </div>
          <div class="form-group">
            <label>设备ID</label>
            <input v-model="form.deviceId" placeholder="设备唯一标识" required />
          </div>
          <div class="form-group">
            <label>安装位置</label>
            <input v-model="form.location" placeholder="例如：客厅、卧室、阳台" required />
          </div>
          <div class="modal-footer">
            <button type="button" class="btn-cancel" @click="showAddModal = false">取消</button>
            <button type="submit" class="btn-confirm">确认添加</button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div class="delete-modal" @click.stop>
        <div class="delete-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 9v4m0 4h.01M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0z"/>
          </svg>
        </div>
        <h3>确认删除</h3>
        <p>确定要删除设备「{{ deleteDeviceData?.deviceName }}」吗？</p>
        <p class="warning">此操作无法撤销</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showDeleteModal = false">取消</button>
          <button class="btn-delete" @click="deleteDevice(deleteDeviceData?.id)">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { deviceAPI } from '../api'
import { ElMessage } from 'element-plus'

const deviceList = ref([])
const showAddModal = ref(false)
const showDeleteModal = ref(false)
const deleteDeviceData = ref(null)
const form = reactive({
  deviceName: '',
  deviceType: '',
  deviceId: '',
  location: ''
})

const activeCount = computed(() => {
  return deviceList.value.filter(d => d.status === 'online' || d.status === 'active').length
})

const offlineCount = computed(() => {
  return deviceList.value.filter(d => d.status === 'offline').length
})

const loadDevices = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await deviceAPI.getByUser(userId)
    if (response.code === 200) {
      deviceList.value = (response.data || []).map(d => ({
        ...d,
        brightness: d.brightness || 80,
        temperature: d.temperature || 25,
        position: d.position || 50
      }))
    }
  } catch (error) {
    console.error('加载设备失败:', error)
  }
}

const addDevice = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await deviceAPI.add({
      userId: parseInt(userId),
      deviceName: form.deviceName,
      deviceType: form.deviceType,
      deviceId: form.deviceId,
      location: form.location
    })
    
    if (response.code === 200) {
      ElMessage.success('添加成功')
      showAddModal.value = false
      form.deviceName = ''
      form.deviceType = ''
      form.deviceId = ''
      form.location = ''
      loadDevices()
    }
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const controlDevice = async (deviceId, action) => {
  try {
    const response = await deviceAPI.control(deviceId, action)
    if (response.code === 200) {
      ElMessage.success(action === 'on' ? '设备已开启' : '设备已关闭')
      loadDevices()
    }
  } catch (error) {
    ElMessage.error('控制失败')
  }
}

const adjustDevice = (device) => {
  console.log('调节设备:', device.deviceName, device)
}

const showDeleteConfirm = (device) => {
  deleteDeviceData.value = device
  showDeleteModal.value = true
}

const deleteDevice = async (deviceId) => {
  try {
    const response = await deviceAPI.delete(deviceId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      showDeleteModal.value = false
      deleteDeviceData.value = null
      loadDevices()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const getStatusText = (status) => {
  if (status === 'online' || status === 'active') return '运行中'
  if (status === 'inactive') return '已关闭'
  return '离线'
}

const getDeviceTypeName = (type) => {
  const types = {
    light: '智能灯光',
    aircon: '智能空调',
    curtain: '智能窗帘',
    security: '安防设备',
    sensor: '传感器'
  }
  return types[type] || type
}

const formatValue = (type, value) => {
  if (type === 'aircon') return value + '°C'
  if (type === 'sensor') return value + '%'
  return value
}

onMounted(() => {
  loadDevices()
})
</script>

<style scoped>
.devices-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 32px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-content h1 {
  font-size: 28px;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.header-content p {
  margin: 0;
  color: #64748b;
}

.add-device-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.add-device-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.add-device-btn svg {
  width: 18px;
  height: 18px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.active {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.offline {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
}

.stat-icon svg {
  width: 24px;
  height: 24px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #1e293b;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
}

.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.device-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  border: 2px solid transparent;
}

.device-card.active {
  border-color: rgba(16, 185, 129, 0.3);
}

.device-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.device-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.device-icon.online,
.device-icon.active {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
}

.device-icon.inactive {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
}

.device-icon.offline {
  background: linear-gradient(135deg, #cbd5e1 0%, #94a3b8 100%);
}

.device-icon svg {
  width: 26px;
  height: 26px;
}

.device-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.device-status.online,
.device-status.active {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.device-status.inactive {
  background: rgba(148, 163, 184, 0.1);
  color: #64748b;
}

.device-status.offline {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
}

.device-info {
  margin-bottom: 16px;
}

.device-info h3 {
  font-size: 18px;
  margin: 0 0 8px 0;
  color: #1e293b;
}

.device-type {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-radius: 12px;
  font-size: 12px;
  margin-right: 8px;
}

.device-location {
  font-size: 13px;
  color: #94a3b8;
}

.device-readings {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.reading-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.reading-label {
  color: #64748b;
  font-size: 14px;
}

.reading-value {
  font-size: 20px;
  font-weight: bold;
  color: #667eea;
}

.device-controls {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.control-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
}

.control-btn svg {
  width: 14px;
  height: 14px;
}

.control-btn.power-on {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.control-btn.power-on:hover:not(:disabled) {
  background: rgba(16, 185, 129, 0.2);
  transform: translateY(-2px);
}

.control-btn.power-off {
  background: rgba(245, 158, 11, 0.1);
  color: #d97706;
}

.control-btn.power-off:hover:not(:disabled) {
  background: rgba(245, 158, 11, 0.2);
  transform: translateY(-2px);
}

.control-btn.delete {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.control-btn.delete:hover {
  background: rgba(239, 68, 68, 0.2);
  transform: translateY(-2px);
}

.control-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.device-slider-panel {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.05) 0%, rgba(5, 150, 105, 0.05) 100%);
  border-radius: 12px;
  padding: 16px;
  border: 1px dashed rgba(16, 185, 129, 0.2);
}

.slider-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
  color: #1e293b;
}

.power-active {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #10b981;
  box-shadow: 0 0 10px #10b981;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.slider-item {
  margin-bottom: 12px;
}

.slider-item:last-child {
  margin-bottom: 0;
}

.slider-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  color: #64748b;
}

.slider-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.slider-wrapper input[type="range"] {
  flex: 1;
  height: 6px;
  border-radius: 3px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
}

.slider-wrapper input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

.slider-wrapper span {
  font-weight: bold;
  color: #667eea;
  min-width: 45px;
  text-align: right;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: white;
  border-radius: 20px;
  padding: 0;
  width: 90%;
  max-width: 450px;
  animation: slideUp 0.3s;
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f1f5f9;
  border-radius: 8px;
  cursor: pointer;
  font-size: 20px;
  color: #64748b;
}

.modal-content form {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #334155;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e2e8f0;
}

.btn-cancel {
  padding: 12px 24px;
  background: #f1f5f9;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  color: #64748b;
}

.btn-cancel:hover {
  background: #e2e8f0;
}

.btn-confirm {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  color: white;
}

.delete-modal {
  background: white;
  border-radius: 20px;
  padding: 40px;
  width: 90%;
  max-width: 400px;
  text-align: center;
}

.delete-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 20px;
  background: rgba(239, 68, 68, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ef4444;
}

.delete-icon svg {
  width: 28px;
  height: 28px;
}

.delete-modal h3 {
  font-size: 20px;
  margin: 0 0 12px 0;
  color: #1e293b;
}

.delete-modal p {
  margin: 0 0 8px 0;
  color: #64748b;
}

.delete-modal .warning {
  color: #ef4444 !important;
  font-size: 13px !important;
}

.btn-delete {
  padding: 12px 24px;
  background: #ef4444;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  color: white;
}

.btn-delete:hover {
  background: #dc2626;
}
</style>