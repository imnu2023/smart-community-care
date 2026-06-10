<template>
  <div class="profile-page">
    <div class="page-header">
      <div class="header-text">
        <h1>个人中心</h1>
        <p>管理您的账户信息</p>
      </div>
    </div>

    <div class="main-content">
      <div class="left-panel">
        <div class="profile-card">
          <div class="card-header">
            <div class="avatar-section">
              <div class="avatar">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </div>
              <div class="user-info">
                <h2>{{ userInfo.name }}</h2>
                <p class="username">@{{ userInfo.username }}</p>
                <p class="role-tag">{{ getRoleText(userInfo.role) }}</p>
              </div>
            </div>
          </div>
          
          <div class="card-body">
            <div class="info-grid">
              <div class="info-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                </svg>
                <span>{{ userInfo.phone || '未设置' }}</span>
              </div>
              <div class="info-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 8l7.89 5.26a2 2 0 0 0 2.22 0L21 8M5 19h14a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2z"/>
                </svg>
                <span>{{ userInfo.email || '未设置' }}</span>
              </div>
              <div class="info-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                  <circle cx="12" cy="10" r="3"/>
                </svg>
                <span>{{ userInfo.address || '未设置' }}</span>
              </div>
              <div class="info-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 20V10"/>
                  <path d="M18 20V4"/>
                  <path d="M6 20v-6"/>
                </svg>
                <span>{{ userInfo.age || '--' }} 岁</span>
              </div>
            </div>
          </div>

          <div class="card-footer">
            <button class="edit-profile-btn" @click="showEditModal = true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
              编辑资料
            </button>
          </div>
        </div>

        <div class="settings-card">
          <h3>账户设置</h3>
          <div class="settings-list">
            <button class="settings-item" @click="showChangePassword = true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              <span>修改密码</span>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7"/>
              </svg>
            </button>
            <button class="settings-item" @click="showEmergencyModal = true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
              </svg>
              <span>紧急联系人</span>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7"/>
              </svg>
            </button>
            <button class="settings-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
              <span>通知设置</span>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7"/>
              </svg>
            </button>
            <button class="settings-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
              <span>隐私设置</span>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div class="stats-card">
          <h3>健康数据统计</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-icon">❤️</div>
              <div class="stat-content">
                <span class="stat-value">{{ healthStats.heartRate || '--' }}</span>
                <span class="stat-label">平均心率</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">💪</div>
              <div class="stat-content">
                <span class="stat-value">{{ healthStats.bp || '--' }}</span>
                <span class="stat-label">平均血压</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">😴</div>
              <div class="stat-content">
                <span class="stat-value">{{ healthStats.sleep || '--' }}</span>
                <span class="stat-label">平均睡眠</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">👟</div>
              <div class="stat-content">
                <span class="stat-value">{{ healthStats.steps || '--' }}</span>
                <span class="stat-label">平均步数</span>
              </div>
            </div>
          </div>
        </div>

        <div class="activity-card">
          <h3>近期活动</h3>
          <div class="activity-list">
            <div class="activity-item">
              <div class="activity-icon health">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                </svg>
              </div>
              <div class="activity-content">
                <p class="activity-title">提交健康数据</p>
                <p class="activity-time">2小时前</p>
              </div>
            </div>
            <div class="activity-item">
              <div class="activity-icon order">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 7h-9M14 17H5m15-4h-9"/>
                  <circle cx="17" cy="7" r="2"/>
                  <circle cx="7" cy="17" r="2"/>
                  <circle cx="17" cy="17" r="2"/>
                </svg>
              </div>
              <div class="activity-content">
                <p class="activity-title">预约家政服务</p>
                <p class="activity-time">1天前</p>
              </div>
            </div>
            <div class="activity-item">
              <div class="activity-icon report">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                  <polyline points="14 2 14 8 20 8"/>
                  <line x1="16" y1="13" x2="8" y2="13"/>
                  <line x1="16" y1="17" x2="8" y2="17"/>
                  <polyline points="10 9 9 9 8 9"/>
                </svg>
              </div>
              <div class="activity-content">
                <p class="activity-title">生成健康报告</p>
                <p class="activity-time">3天前</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showEditModal" class="modal-overlay" @click="showEditModal = false">
      <div class="modal-content edit-modal" @click.stop>
        <div class="modal-header">
          <h3>编辑个人资料</h3>
          <button class="close-btn" @click="showEditModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveProfile" class="edit-form">
            <div class="form-grid">
              <div class="form-item">
                <label>姓名</label>
                <input v-model="editForm.name" placeholder="请输入姓名" />
              </div>
              <div class="form-item">
                <label>手机号</label>
                <input v-model="editForm.phone" placeholder="请输入手机号" />
              </div>
              <div class="form-item">
                <label>邮箱</label>
                <input v-model="editForm.email" placeholder="请输入邮箱" />
              </div>
              <div class="form-item">
                <label>年龄</label>
                <input type="number" v-model="editForm.age" placeholder="请输入年龄" />
              </div>
              <div class="form-item">
                <label>性别</label>
                <select v-model="editForm.gender">
                  <option value="男">男</option>
                  <option value="女">女</option>
                </select>
              </div>
              <div class="form-item">
                <label>地址</label>
                <input v-model="editForm.address" placeholder="请输入地址" />
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn-secondary" @click="showEditModal = false">取消</button>
              <button type="submit" class="btn-primary">保存修改</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div v-if="showChangePassword" class="modal-overlay" @click="showChangePassword = false">
      <div class="modal-content password-modal" @click.stop>
        <div class="modal-header">
          <h3>修改密码</h3>
          <button class="close-btn" @click="showChangePassword = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="changePassword" class="password-form">
            <div class="form-item">
              <label>旧密码</label>
              <input type="password" v-model="passwordForm.oldPassword" placeholder="请输入旧密码" />
            </div>
            <div class="form-item">
              <label>新密码</label>
              <input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" />
            </div>
            <div class="form-item">
              <label>确认密码</label>
              <input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
            </div>
            <div class="modal-footer">
              <button type="button" class="btn-secondary" @click="showChangePassword = false">取消</button>
              <button type="submit" class="btn-primary">确认修改</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div v-if="showEmergencyModal" class="modal-overlay" @click="showEmergencyModal = false">
      <div class="modal-content emergency-modal" @click.stop>
        <div class="modal-header">
          <h3>紧急联系人</h3>
          <button class="close-btn" @click="showEmergencyModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveEmergency" class="emergency-form">
            <div class="form-item">
              <label>紧急联系人</label>
              <input v-model="emergencyForm.contactName" placeholder="请输入联系人姓名" />
            </div>
            <div class="form-item">
              <label>联系电话</label>
              <input v-model="emergencyForm.contactPhone" placeholder="请输入联系电话" />
            </div>
            <div class="form-item">
              <label>与您的关系</label>
              <input v-model="emergencyForm.relation" placeholder="如：子女、配偶等" />
            </div>
            <div class="modal-footer">
              <button type="button" class="btn-secondary" @click="showEmergencyModal = false">取消</button>
              <button type="submit" class="btn-primary">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userAPI } from '../api'
import { ElMessage } from 'element-plus'

const userInfo = reactive({
  id: '',
  username: '',
  name: '',
  phone: '',
  email: '',
  age: '',
  gender: '',
  address: '',
  role: '',
  emergencyContact: '',
  emergencyPhone: ''
})

const editForm = reactive({
  name: '',
  phone: '',
  email: '',
  age: '',
  gender: '',
  address: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const emergencyForm = reactive({
  contactName: '',
  contactPhone: '',
  relation: ''
})

const healthStats = reactive({
  heartRate: '72',
  bp: '120/80',
  sleep: '7.5h',
  steps: '5,200'
})

const showEditModal = ref(false)
const showChangePassword = ref(false)
const showEmergencyModal = ref(false)

const loadUserInfo = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await userAPI.getById(userId)
    if (response.code === 200) {
      Object.assign(userInfo, response.data)
      Object.assign(editForm, {
        name: response.data.name || '',
        phone: response.data.phone || '',
        email: response.data.email || '',
        age: response.data.age || '',
        gender: response.data.gender || '男',
        address: response.data.address || ''
      })
      emergencyForm.contactName = response.data.emergencyContact || ''
      emergencyForm.contactPhone = response.data.emergencyPhone || ''
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const getRoleText = (role) => {
  if (role === 1) return '管理员'
  if (role === 2) return '老人用户'
  return '家属用户'
}

const saveProfile = async () => {
  try {
    const response = await userAPI.update(userInfo.id, {
      ...editForm,
      id: userInfo.id,
      username: userInfo.username,
      role: userInfo.role
    })
    if (response.code === 200) {
      Object.assign(userInfo, editForm)
      localStorage.setItem('name', userInfo.name)
      ElMessage.success('保存成功')
      showEditModal.value = false
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  ElMessage.success('密码修改成功')
  showChangePassword.value = false
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const saveEmergency = async () => {
  try {
    const response = await userAPI.update(userInfo.id, {
      ...userInfo,
      emergencyContact: emergencyForm.contactName,
      emergencyPhone: emergencyForm.contactPhone
    })
    if (response.code === 200) {
      userInfo.emergencyContact = emergencyForm.contactName
      userInfo.emergencyPhone = emergencyForm.contactPhone
      ElMessage.success('保存成功')
      showEmergencyModal.value = false
    }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
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

.left-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.card-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.avatar svg {
  width: 32px;
  height: 32px;
}

.user-info h2 {
  margin: 0 0 4px 0;
  font-size: 20px;
  color: white;
}

.username {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.role-tag {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 12px;
  color: white;
}

.card-body {
  padding: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 10px;
}

.info-item svg {
  width: 18px;
  height: 18px;
  color: #667eea;
}

.info-item span {
  font-size: 14px;
  color: #334155;
}

.card-footer {
  padding: 16px 20px;
  border-top: 1px solid #f1f5f9;
}

.edit-profile-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  color: white;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.edit-profile-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.edit-profile-btn svg {
  width: 16px;
  height: 16px;
}

.settings-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.settings-card h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #1e293b;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
}

.settings-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.settings-item svg:first-child {
  width: 18px;
  height: 18px;
  color: #667eea;
}

.settings-item span {
  flex: 1;
  text-align: left;
  margin-left: 12px;
  font-size: 14px;
  color: #334155;
}

.settings-item svg:last-child {
  width: 16px;
  height: 16px;
  color: #94a3b8;
}

.right-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.stats-card h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #1e293b;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.stat-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.stat-content .stat-value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-content .stat-label {
  font-size: 12px;
  color: #64748b;
}

.activity-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
}

.activity-card h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #1e293b;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s;
}

.activity-item:hover {
  background: #f1f5f9;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-icon.health {
  background: rgba(244, 63, 94, 0.1);
  color: #f43f5e;
}

.activity-icon.order {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.activity-icon.report {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.activity-icon svg {
  width: 18px;
  height: 18px;
}

.activity-content {
  flex: 1;
}

.activity-title {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #1e293b;
}

.activity-time {
  margin: 0;
  font-size: 12px;
  color: #94a3b8;
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
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
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
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn svg {
  width: 16px;
  height: 16px;
}

.modal-body {
  padding: 24px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
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

.form-item input,
.form-item select {
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-item input:focus,
.form-item select:focus {
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

.btn-secondary,
.btn-primary {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
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

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>