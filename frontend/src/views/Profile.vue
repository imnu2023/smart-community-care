<template>
  <div class="pf-page">
    <header class="pf-header">
      <button class="btn-back" @click="goBack"><AppIcon name="x" size="22" /></button>
      <h1 class="text-headline-lg">个人中心</h1>
    </header>

    <!-- Profile header card -->
    <div class="pf-hero">
      <div class="pf-hero-left">
        <div class="pf-avatar-wrap">
          <div class="pf-avatar" :style="userInfo.avatar ? { backgroundImage: `url(${userInfo.avatar})`, backgroundSize: 'cover' } : ''">
            <svg v-if="!userInfo.avatar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="28" height="28"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </div>
          <label class="pf-avatar-upload"><input type="file" accept="image/*" @change="handleAvatarUpload" hidden />＋</label>
        </div>
        <div>
          <div class="pf-name-row"><h2>{{ userInfo.name }}</h2><span class="badge" :class="roleBadge(userInfo.role)">{{ getRoleText(userInfo.role) }}</span></div>
          <p class="pf-username">@{{ userInfo.username }}</p>
        </div>
      </div>
      <div class="pf-hero-right">
        <div class="pf-wallet"><span>钱包余额</span><strong>¥{{ walletBalance.toFixed(2) }}</strong></div>
        <button class="btn btn-primary btn-sm" @click="openRechargeModal">充值</button>
      </div>
    </div>

    <!-- Info + Stats -->
    <div class="pf-grid">
      <div class="card">
        <div class="card-header"><h3 class="card-title">个人信息</h3></div>
        <div class="pf-info-grid">
          <div v-for="item in infoItems" :key="item.label" class="pf-info-item">
            <span class="pf-info-icon"><AppIcon :name="item.icon" size="22" /></span>
            <div><span class="pf-info-label">{{ item.label }}</span><span class="pf-info-value">{{ item.value }}</span></div>
          </div>
        </div>
        <div class="pf-info-actions">
          <button class="btn btn-primary btn-sm" @click="showEditModal = true">编辑资料</button>
          <button class="btn btn-secondary btn-sm" @click="showChangePassword = true">修改密码</button>
        </div>
      </div>

      <div class="card">
        <div class="card-header"><h3 class="card-title">健康数据统计</h3></div>
        <div class="pf-stats">
          <div v-for="s in healthStatItems" :key="s.label" class="pf-stat">
            <span class="pf-stat-icon"><AppIcon :name="s.icon" size="28" /></span>
            <span class="pf-stat-value">{{ s.value }}</span>
            <span class="pf-stat-label">{{ s.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit modal -->
    <dialog v-if="showEditModal" class="pf-modal-overlay" @click="showEditModal = false">
      <div class="pf-modal" @click.stop>
        <div class="pf-modal-header"><h3>编辑资料</h3><button class="btn btn-ghost btn-sm" @click="showEditModal = false">✕</button></div>
        <form @submit.prevent="handleUpdateProfile" class="pf-modal-body">
          <div class="form-group"><label class="form-label">姓名</label><input class="form-input" v-model="editForm.name" /></div>
          <div class="form-group"><label class="form-label">手机号</label><input class="form-input" v-model="editForm.phone" /></div>
          <div class="form-group"><label class="form-label">邮箱</label><input class="form-input" v-model="editForm.email" /></div>
          <div class="form-group"><label class="form-label">年龄</label><input class="form-input" type="number" v-model="editForm.age" /></div>
          <div class="pf-modal-footer"><button type="button" class="btn btn-secondary" @click="showEditModal = false">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
        </form>
      </div>
    </dialog>

    <!-- Password modal -->
    <dialog v-if="showChangePassword" class="pf-modal-overlay" @click="showChangePassword = false">
      <div class="pf-modal pf-modal--sm" @click.stop>
        <div class="pf-modal-header"><h3>修改密码</h3><button class="btn btn-ghost btn-sm" @click="showChangePassword = false">✕</button></div>
        <form @submit.prevent="handleChangePassword" class="pf-modal-body">
          <div class="form-group"><label class="form-label">旧密码</label><input class="form-input" type="password" v-model="pwdForm.oldPassword" /></div>
          <div class="form-group"><label class="form-label">新密码</label><input class="form-input" type="password" v-model="pwdForm.newPassword" /></div>
          <div class="form-group"><label class="form-label">确认密码</label><input class="form-input" type="password" v-model="pwdForm.confirmPassword" /></div>
          <div class="pf-modal-footer"><button type="button" class="btn btn-secondary" @click="showChangePassword = false">取消</button><button type="submit" class="btn btn-primary">确认修改</button></div>
        </form>
      </div>
    </dialog>

    <!-- Recharge modal -->
    <dialog v-if="showRechargeModal" class="pf-modal-overlay" @click="showRechargeModal = false">
      <div class="pf-modal pf-modal--sm" @click.stop>
        <div class="pf-modal-header"><h3>钱包充值</h3><button class="btn btn-ghost btn-sm" @click="showRechargeModal = false">✕</button></div>
        <div class="pf-modal-body" style="text-align:center">
          <p class="text-body-lg" style="margin-bottom:var(--space-lg)">当前余额：<strong>¥{{ walletBalance.toFixed(2) }}</strong></p>
          <div class="pf-recharge-grid">
            <button v-for="amt in [50, 100, 200, 500]" :key="amt" class="btn btn-secondary" @click="handleRecharge(amt)">¥{{ amt }}</button>
          </div>
        </div>
      </div>
    </dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userAPI, healthAPI, walletAPI } from '../api'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const goBack = () => router.push('/dashboard')

const userId = parseInt(localStorage.getItem('userId') || '1')
const userInfo = ref({ name: '', username: '', role: '', phone: '', email: '', age: '', gender: '', avatar: '' })
const walletBalance = ref(0)
const latestHealth = ref({})
const showEditModal = ref(false)
const showChangePassword = ref(false)
const showRechargeModal = ref(false)
const editForm = ref({ name: '', phone: '', email: '', age: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const getRoleText = (r) => ({ '1': '管理员', '2': '老人', '3': '家属' }[String(r)] || '用户')
const roleBadge = (r) => ({ '1': 'badge-danger', '2': 'badge-success', '3': 'badge-info' }[String(r)] || 'badge-neutral')

const infoItems = computed(() => [
  { icon: 'phone', label: '手机号', value: userInfo.value.phone || '未设置' },
  { icon: 'mail', label: '邮箱', value: userInfo.value.email || '未设置' },
  { icon: 'calendar', label: '年龄', value: userInfo.value.age ? userInfo.value.age + ' 岁' : '未设置' },
  { icon: 'user', label: '性别', value: userInfo.value.gender || '未设置' }
])

const healthStatItems = computed(() => [
  { icon: 'pulse', value: latestHealth.value.heartRate || '--', label: '心率' },
  { icon: 'droplet', value: latestHealth.value.bloodPressureHigh ? `${latestHealth.value.bloodPressureHigh}/${latestHealth.value.bloodPressureLow}` : '--', label: '血压' },
  { icon: 'droplet', value: latestHealth.value.bloodSugar || '--', label: '血糖' },
  { icon: 'moon', value: latestHealth.value.sleepHours || '--', label: '睡眠' }
])

const loadProfile = async () => {
  try { const res = await userAPI.getById(userId); if (res.code === 200) { const d = res.data; userInfo.value = { ...userInfo.value, ...d }; editForm.value = { name: d.name || '', phone: d.phone || '', email: d.email || '', age: d.age || '' } } } catch (e) {}
}
const loadWallet = async () => { try { const res = await walletAPI.getBalance(userId); if (res.code === 200) walletBalance.value = res.data.balance || 0 } catch (e) {} }
const loadHealth = async () => { try { const res = await healthAPI.getLatest(userId); if (res.code === 200) latestHealth.value = res.data || {} } catch (e) {} }

const handleAvatarUpload = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  if (!file.type.startsWith('image/')) { ElMessage.error('请选择图片文件'); return }
  if (file.size > 5 * 1024 * 1024) { ElMessage.error('头像不能超过 5MB'); return }
  try {
    const fd = new FormData()
    fd.append('file', file)
    const res = await axios.post('/api/upload/avatar', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
    if (res.data?.code === 200) {
      const avatarUrl = res.data.data
      userInfo.value.avatar = avatarUrl
      await userAPI.update(userId, { avatar: avatarUrl })
      ElMessage.success('头像已更新')
    } else {
      ElMessage.error(res.data?.message || '上传失败')
    }
  } catch (e) { ElMessage.error('头像上传失败') }
}
const handleUpdateProfile = async () => { try { await userAPI.update(userId, editForm.value); ElMessage.success('已更新'); showEditModal.value = false; loadProfile() } catch (e) { ElMessage.error('更新失败') } }
const handleChangePassword = async () => { if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) { ElMessage.warning('密码不一致'); return }; ElMessage.success('密码已修改'); showChangePassword.value = false }
const openRechargeModal = () => { showRechargeModal.value = true }
const handleRecharge = async (amt) => { try { await walletAPI.recharge(userId, amt); ElMessage.success(`已充值 ¥${amt}`); showRechargeModal.value = false; loadWallet() } catch (e) { ElMessage.error('充值失败') } }

onMounted(() => { loadProfile(); loadWallet(); loadHealth() })
</script>

<style scoped>
.pf-page { min-height: 100vh; background: var(--color-surface); padding: var(--space-lg); }
.pf-header { display: flex; align-items: center; gap: var(--space-md); margin-bottom: var(--space-xl); }
.pf-header h1 { margin: 0; }

.pf-hero { display: flex; justify-content: space-between; align-items: center; padding: var(--space-xl); background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); margin-bottom: var(--space-xl); box-shadow: var(--shadow-card); }
.pf-hero-left { display: flex; align-items: center; gap: var(--space-md); }
.pf-avatar-wrap { position: relative; }
.pf-avatar { width: 72px; height: 72px; border-radius: 50%; background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); display: flex; align-items: center; justify-content: center; }
.pf-avatar-upload { position: absolute; bottom: -4px; right: -4px; width: 28px; height: 28px; border-radius: 50%; background: var(--color-primary); color: var(--color-on-primary); border: 2px solid var(--color-surface-container-lowest); cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 14px; transition: transform 0.15s var(--ease-out); }
.pf-avatar-upload:hover { transform: scale(1.1); }
.pf-name-row { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: var(--space-xxs); }
.pf-name-row h2 { font-size: var(--text-headline-md); margin: 0; }
.pf-username { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: 0; }
.pf-hero-right { display: flex; flex-direction: column; align-items: flex-end; gap: var(--space-sm); }
.pf-wallet { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.pf-wallet strong { display: block; font-size: var(--text-headline-md); color: var(--color-on-surface); }

.pf-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-lg); }
.pf-info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-sm); margin-bottom: var(--space-md); }
.pf-info-item { display: flex; align-items: center; gap: var(--space-sm); padding: var(--space-sm); background: var(--color-surface-container-low); border-radius: var(--radius-md); transition: background 0.2s var(--ease-out); }
.pf-info-item:hover { background: var(--color-surface-container); }
.pf-info-icon { display: flex; align-items: center; justify-content: center; color: var(--color-on-surface-variant); flex-shrink: 0; }
.pf-info-label { display: block; font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.pf-info-value { font-size: var(--text-label-md); font-weight: var(--weight-medium); color: var(--color-on-surface); }
.pf-info-actions { display: flex; gap: var(--space-sm); }
.pf-stats { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-sm); }
.pf-stat { text-align: center; padding: var(--space-md); background: var(--color-surface-container-low); border-radius: var(--radius-md); transition: transform 0.2s var(--ease-out); }
.pf-stat:hover { transform: translateY(-2px); }
.pf-stat-icon { display: flex; align-items: center; justify-content: center; margin-bottom: var(--space-xxs); color: var(--color-on-surface-variant); }
.pf-stat-value { font-size: var(--text-headline-md); font-weight: var(--weight-bold); display: block; }
.pf-stat-label { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }

.pf-modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; z-index: var(--z-modal); border: none; width: 100%; height: 100%; }
.pf-modal { background: var(--color-surface-container-lowest); border-radius: var(--radius-2xl); width: 90%; max-width: 480px; box-shadow: var(--shadow-soft-hover); }
.pf-modal--sm { max-width: 380px; padding: var(--space-xl); }
.pf-modal-header { display: flex; justify-content: space-between; align-items: center; padding: var(--space-lg) var(--space-xl); border-bottom: 1px solid var(--color-outline-variant); }
.pf-modal-header h3 { margin: 0; font-size: var(--text-headline-md); }
.pf-modal-body { padding: var(--space-xl); }
.pf-modal-footer { display: flex; gap: var(--space-sm); justify-content: flex-end; padding-top: var(--space-lg); border-top: 1px solid var(--color-outline-variant); margin-top: var(--space-md); }
.pf-recharge-grid { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-sm); }

@media (max-width: 767px) {
  .pf-page { padding: var(--space-md); }
  .pf-hero { flex-direction: column; align-items: flex-start; gap: var(--space-md); }
  .pf-hero-right { flex-direction: row; align-items: center; width: 100%; }
  .pf-grid { grid-template-columns: 1fr; }
  .pf-info-grid { grid-template-columns: 1fr; }
}
</style>
