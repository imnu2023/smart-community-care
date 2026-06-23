<template>
  <div class="profile-page">
    <div class="page-header">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5m0 0l7-7m-7 7l7 7"/>
        </svg>
        <span>返回</span>
      </button>
      <div class="header-text">
        <h1>个人中心</h1>
        <p>管理您的账户信息</p>
      </div>
    </div>

    <div class="main-content">
      <div class="profile-header-card">
        <div class="header-left">
          <div class="avatar-wrapper">
            <div class="avatar" :style="userInfo.avatar ? { backgroundImage: `url(${userInfo.avatar})`, backgroundSize: 'cover', backgroundPosition: 'center' } : ''">
              <svg v-if="!userInfo.avatar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
            </div>
            <label class="avatar-upload-btn">
              <input type="file" accept="image/*" @change="handleAvatarUpload" />
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 4v16m8-8H4"/>
              </svg>
            </label>
          </div>
          <div class="user-details">
            <div class="user-name-row">
              <h2>{{ userInfo.name }}</h2>
              <span class="role-badge">{{ getRoleText(userInfo.role) }}</span>
            </div>
            <p class="username">@{{ userInfo.username }}</p>
          </div>
        </div>
        
        <div class="header-right">
          <div class="wallet-info">
            <span class="wallet-label">我的钱包</span>
            <span class="wallet-amount">¥{{ walletBalance.toFixed(2) }}</span>
          </div>
          <button class="recharge-btn" @click="openRechargeModal">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19"/>
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            充值
          </button>
        </div>
      </div>

      <div class="content-grid">
        <div class="info-card">
          <div class="card-title-row">
            <h3>个人信息</h3>
          </div>
          <div class="info-grid">
            <div class="info-item" v-for="(item, index) in infoItems" :key="index">
              <svg :viewBox="item.iconViewBox" fill="none" stroke="currentColor" stroke-width="2">
                <path :d="item.iconPath"/>
              </svg>
              <div class="info-content">
                <span class="info-label">{{ item.label }}</span>
                <span class="info-value">{{ item.value }}</span>
              </div>
            </div>
          </div>
          <div class="action-buttons">
            <button class="action-btn primary" @click="showEditModal = true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
              编辑资料
            </button>
            <button class="action-btn secondary" @click="showChangePassword = true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
              修改密码
            </button>
          </div>
        </div>

        <div class="stats-card">
          <div class="card-title-row">
            <h3>健康数据统计</h3>
            <span class="card-subtitle">近期健康指标</span>
          </div>
          <div class="stats-grid">
            <div class="stat-item" v-for="(stat, index) in healthStatItems" :key="index">
              <div class="stat-icon" :class="stat.colorClass">{{ stat.icon }}</div>
              <div class="stat-content">
                <span class="stat-value">{{ stat.value }}</span>
                <span class="stat-label">{{ stat.label }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="settings-card">
          <div class="card-title-row">
            <h3>账户设置</h3>
          </div>
          <div class="settings-list">
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
                <path d="M12 15v2m-6 4h12a2 2 0 0 0 2-2v-6a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2zm10-10V7a4 4 0 0 0-8 0v4h8z"/>
              </svg>
              <span>隐私设置</span>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7"/>
              </svg>
            </button>
            <button class="settings-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14 2 14 8 20 8"/>
                <line x1="16" y1="13" x2="8" y2="13"/>
                <line x1="16" y1="17" x2="8" y2="17"/>
                <polyline points="10 9 9 9 8 9"/>
              </svg>
              <span>帮助中心</span>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7"/>
              </svg>
            </button>
          </div>
        </div>

        <div class="activity-card">
          <div class="card-title-row">
            <h3>近期活动</h3>
          </div>
          <div class="activity-list">
            <div class="activity-item" v-for="(activity, index) in recentActivities" :key="index">
              <div class="activity-icon" :class="activity.type">
                <svg :viewBox="activity.iconViewBox" fill="none" stroke="currentColor" stroke-width="2">
                  <path :d="activity.iconPath"/>
                </svg>
              </div>
              <div class="activity-content">
                <p class="activity-title">{{ activity.title }}</p>
                <p class="activity-time">{{ activity.time }}</p>
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

    <div v-if="showRechargeModal" class="modal-overlay" @click="showRechargeModal = false">
      <div class="modal-content recharge-modal" @click.stop>
        <div class="modal-header">
          <h3>钱包充值</h3>
          <button class="close-btn" @click="showRechargeModal = false">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="handleRecharge" class="recharge-form">
            <div class="form-item">
              <label>充值金额</label>
              <input type="number" v-model="rechargeForm.amount" placeholder="请输入充值金额" />
            </div>
            <div class="quick-amounts">
              <button type="button" v-for="amount in [10, 50, 100, 200]" :key="amount" 
                      class="quick-btn" @click="rechargeForm.amount = amount">
                ¥{{ amount }}
              </button>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn-secondary" @click="showRechargeModal = false">取消</button>
              <button type="submit" class="btn-primary">确认充值</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userAPI, walletAPI } from '../api'
import { instance as axios } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()

const goBack = () => {
  router.push('/dashboard')
}

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
  emergencyPhone: '',
  avatar: ''
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
const showRechargeModal = ref(false)
const walletBalance = ref(0)

const rechargeForm = reactive({
  amount: '',
  password: ''
})

const infoItems = computed(() => [
  { iconViewBox: '0 0 24 24', iconPath: 'M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z', label: '联系电话', value: userInfo.phone || '未设置' },
  { iconViewBox: '0 0 24 24', iconPath: 'M3 8l7.89 5.26a2 2 0 0 0 2.22 0L21 8M5 19h14a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2z', label: '邮箱地址', value: userInfo.email || '未设置' },
  { iconViewBox: '0 0 24 24', iconPath: 'M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z M12 10c0 3-1.5 5-3 5s-3-2-3-5 1.5-5 3-5 3 2 3 5', label: '住址', value: userInfo.address || '未设置' },
  { iconViewBox: '0 0 24 24', iconPath: 'M12 20V10 M18 20V4 M6 20v-6', label: '年龄', value: (userInfo.age || '--') + ' 岁' }
])

const healthStatItems = computed(() => [
  { icon: '❤️', value: healthStats.heartRate, label: '平均心率', colorClass: 'heart' },
  { icon: '🩸', value: healthStats.bp, label: '平均血压', colorClass: 'bp' },
  { icon: '😴', value: healthStats.sleep, label: '平均睡眠', colorClass: 'sleep' },
  { icon: '👟', value: healthStats.steps, label: '平均步数', colorClass: 'steps' }
])

const recentActivities = ref([
  { title: '提交健康数据', time: '2小时前', type: 'health', iconViewBox: '0 0 24 24', iconPath: 'M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z' },
  { title: '预约家政服务', time: '1天前', type: 'order', iconViewBox: '0 0 24 24', iconPath: 'M20 7h-9M14 17H5m15-4h-9 M17 7a2 2 0 1 1-4 0 2 2 0 0 1 4 0M7 17a2 2 0 1 1-4 0 2 2 0 0 1 4 0M17 17a2 2 0 1 1-4 0 2 2 0 0 1 4 0' },
  { title: '生成健康报告', time: '3天前', type: 'report', iconViewBox: '0 0 24 24', iconPath: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z M14 2v6h6M16 13H8M16 17H8M10 9H9H8' }
])

const loadUserInfo = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await userAPI.getById(userId)
    if (response.code === 200) {
      Object.assign(userInfo, response.data)
      userInfo.avatar = response.data.avatar || ''
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

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('头像文件大小不能超过 5MB')
    return
  }
  
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const response = await axios.post('http://localhost:8081/api/upload/avatar', formData)
    console.log('上传响应:', response)
    
    if (response && response.code === 200) {
      const avatarUrl = response.data
      console.log('头像URL:', avatarUrl)
      userInfo.avatar = avatarUrl
      console.log('userInfo.avatar:', userInfo.avatar)
      
      try {
        await userAPI.update(userInfo.id, {
          ...userInfo,
          avatar: avatarUrl
        })
      } catch (error) {
        console.error('更新用户头像失败:', error)
      }
      
      ElMessage.success('头像上传成功')
    } else {
      console.error('响应格式错误:', response)
      ElMessage.error('上传失败：' + (response?.message || '未知错误'))
    }
  } catch (error) {
    console.error('上传头像失败:', error.response?.data || error.message)
    ElMessage.error('上传头像失败')
  }
  
  event.target.value = ''
}

const loadWalletBalance = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await walletAPI.getBalance(parseInt(userId))
    if (response.code === 200) {
      walletBalance.value = response.data.balance || 0
    }
  } catch (error) {
    console.error('加载钱包余额失败:', error)
    walletBalance.value = 0
  }
}

const openRechargeModal = () => {
  rechargeForm.amount = ''
  rechargeForm.password = ''
  showRechargeModal.value = true
}

const closeRechargeModal = () => {
  showRechargeModal.value = false
}

const handleRecharge = async () => {
  if (!rechargeForm.amount || parseFloat(rechargeForm.amount) <= 0) {
    ElMessage.error('请输入有效的充值金额')
    return
  }
  
  const userId = localStorage.getItem('userId')
  try {
    const response = await walletAPI.recharge(parseInt(userId), parseFloat(rechargeForm.amount))
    if (response.code === 200) {
      ElMessage.success('充值成功')
      walletBalance.value = response.data.balance
      closeRechargeModal()
    }
  } catch (error) {
    ElMessage.error('充值失败')
  }
}

const getRoleText = (role) => {
  if (role === 1) return '管理员'
  if (role === 2) return '老人'
  if (role === 3) return '家属'
  return '未知'
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
  loadWalletBalance()
})
</script>

<style scoped>
.profile-page {
  padding: 20px 24px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  margin-left: 0 !important;
  width: calc(100% - 280px);
  margin-left: 260px;
}

.page-header {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  transition: all var(--transition-fast);
}

.back-btn:hover {
  background: #f8fafc;
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.back-btn svg {
  width: 16px;
  height: 16px;
}

.header-text h1 {
  margin: 0;
  font-size: 26px;
  color: var(--text-primary);
  font-weight: 700;
}

.header-text p {
  margin: 6px 0 0 0;
  color: var(--text-secondary);
  font-size: 13px;
}

.main-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.profile-header-card {
  background: linear-gradient(135deg, var(--primary-color) 0%, #8b5cf6 100%);
  border-radius: var(--border-radius-xl);
  padding: 20px;
  color: white;
  box-shadow: 0 12px 40px rgba(99, 102, 241, 0.3);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-wrapper {
  position: relative;
}

.avatar {
  width: 70px;
  height: 70px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.avatar svg {
  width: 30px;
  height: 30px;
}

.avatar-upload-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 26px;
  height: 26px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: 3px solid var(--primary-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all var(--transition-fast);
}

.avatar-upload-btn:hover {
  background: #f8fafc;
  transform: scale(1.1);
}

.avatar-upload-btn svg {
  width: 14px;
  height: 14px;
  color: var(--primary-color);
}

.avatar-upload-btn input {
  display: none;
}

.user-details {
  flex: 1;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
}

.user-details h2 {
  margin: 0;
  font-size: 19px;
  font-weight: 700;
}

.role-badge {
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--border-radius-full);
  font-size: 12px;
  font-weight: 500;
}

.username {
  margin: 0;
  font-size: 14px;
  opacity: 0.85;
}

.wallet-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.wallet-info {
  display: flex;
  flex-direction: column;
}

.wallet-label {
  font-size: 13px;
  opacity: 0.85;
  margin-bottom: 4px;
}

.wallet-amount {
  font-size: 26px;
  font-weight: 700;
}

.recharge-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: white;
  border: none;
  border-radius: var(--border-radius-lg);
  color: var(--primary-color);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-fast);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.recharge-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.recharge-btn svg {
  width: 16px;
  height: 16px;
}

.content-grid {
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: var(--border-radius-xl);
  padding: 20px;
  box-shadow: var(--shadow-md);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-bottom: 16px;
}

.info-grid .info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f8fafc;
  border-radius: var(--border-radius-md);
}

.info-grid .info-item svg {
  width: 18px;
  height: 18px;
  color: var(--primary-color);
}

.info-content {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 2px;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  border: none;
  border-radius: var(--border-radius-lg);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.action-btn.primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, #8b5cf6 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
}

.action-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.5);
}

.action-btn.secondary {
  background: white;
  color: var(--text-primary);
  border: 2px solid #e2e8f0;
}

.action-btn.secondary:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.stats-card,
.settings-card,
.activity-card {
  background: white;
  border-radius: var(--border-radius-xl);
  padding: 20px;
  box-shadow: var(--shadow-md);
}

.card-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title-row h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.card-subtitle {
  font-size: 12px;
  color: var(--text-muted);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background: linear-gradient(145deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: var(--border-radius-lg);
}

.stat-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.stat-content .stat-value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.stat-content .stat-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.settings-card {
  background: white;
  border-radius: var(--border-radius-xl);
  padding: 16px;
  box-shadow: var(--shadow-md);
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
  padding: 12px 14px;
  background: #f8fafc;
  border-radius: var(--border-radius-md);
  border: none;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.settings-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.settings-item svg:first-child {
  width: 18px;
  height: 18px;
  color: var(--primary-color);
}

.settings-item span {
  flex: 1;
  text-align: left;
  margin-left: 12px;
  font-size: 14px;
  color: var(--text-primary);
}

.settings-item svg:last-child {
  width: 16px;
  height: 16px;
  color: var(--text-muted);
}

.activity-card {
  background: white;
  border-radius: var(--border-radius-xl);
  padding: 16px;
  box-shadow: var(--shadow-md);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #f8fafc;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);
}

.activity-item:hover {
  background: #f1f5f9;
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--border-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-icon.health {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.activity-icon.order {
  background: rgba(99, 102, 241, 0.1);
  color: var(--primary-color);
}

.activity-icon.report {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success-color);
}

.activity-icon svg {
  width: 16px;
  height: 16px;
}

.activity-content {
  flex: 1;
}

.activity-title {
  margin: 0 0 2px 0;
  font-size: 14px;
  color: var(--text-primary);
}

.activity-time {
  margin: 0;
  font-size: 12px;
  color: var(--text-muted);
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
  border-radius: var(--border-radius-xl);
  width: 90%;
  max-width: 480px;
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
  color: var(--text-primary);
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f1f5f9;
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn svg {
  width: 16px;
  height: 16px;
}

.modal-body {
  padding: 20px 24px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.form-item input,
.form-item select {
  padding: 12px 14px;
  border: 2px solid #e2e8f0;
  border-radius: var(--border-radius-md);
  font-size: 14px;
  transition: border-color var(--transition-fast);
}

.form-item input:focus,
.form-item select:focus {
  outline: none;
  border-color: var(--primary-color);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
}

.btn-secondary,
.btn-primary {
  padding: 10px 20px;
  border: none;
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.btn-secondary {
  background: #f1f5f9;
  color: var(--text-secondary);
}

.btn-secondary:hover {
  background: #e2e8f0;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, #8b5cf6 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.5);
}

.quick-amounts {
  display: flex;
  gap: 10px;
  margin-top: 14px;
}

.quick-btn {
  flex: 1;
  padding: 10px;
  border: 2px solid #e2e8f0;
  border-radius: var(--border-radius-md);
  background: white;
  color: var(--text-primary);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.quick-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
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

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .profile-header-card {
    padding: 16px;
  }
  
  .avatar-area {
    flex-direction: column;
    text-align: center;
  }
  
  .user-name-row {
    justify-content: center;
  }
}
</style>
