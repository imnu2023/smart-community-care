import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authAPI, userAPI } from '../api/index.js'

export const useUserStore = defineStore('user', () => {
  // ============ State ============
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(Number(localStorage.getItem('role')) || 0)
  const userId = ref(Number(localStorage.getItem('userId')) || 0)
  const username = ref(localStorage.getItem('username') || '')
  const name = ref(localStorage.getItem('name') || '')
  const phone = ref(localStorage.getItem('phone') || '')
  const avatar = ref(localStorage.getItem('avatar') || '')

  // ============ Getters ============
  const isLoggedIn = computed(() => !!token.value)
  const roleLabel = computed(() => {
    const map = { 1: '管理员', 2: '老人', 3: '家属' }
    return map[role.value] || '未知'
  })
  const roleMap = { 1: '/admin', 2: '/elder', 3: '/family' }

  // ============ Actions ============

  /** 登录 — 调用真实 API */
  async function login({ username: uname, password, captchaCode, uuid }) {
    const res = await authAPI.login({ username: uname, password, captchaCode, uuid })
    const data = res.data
    token.value = data.token
    role.value = data.role
    userId.value = data.userId
    username.value = data.username
    name.value = data.name
    phone.value = data.phone || ''

    localStorage.setItem('token', data.token)
    localStorage.setItem('role', data.role)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('username', data.username)
    localStorage.setItem('name', data.name || '')
    localStorage.setItem('phone', data.phone || '')
  }

  /** 注册 */
  async function register(form) {
    await authAPI.register(form)
  }

  /** 退出登录 */
  function logout() {
    token.value = ''
    role.value = 0
    userId.value = 0
    username.value = ''
    name.value = ''
    phone.value = ''
    avatar.value = ''

    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('name')
    localStorage.removeItem('phone')
    localStorage.removeItem('avatar')
    window.location.href = '/login'
  }

  /** 拉取当前用户完整信息 */
  async function fetchProfile() {
    if (!userId.value) return
    const res = await userAPI.getById(userId.value)
    const user = res.data
    if (user) {
      name.value = user.name || name.value
      phone.value = user.phone || phone.value
      avatar.value = user.avatar || ''
      role.value = user.role ?? role.value

      localStorage.setItem('name', name.value)
      localStorage.setItem('phone', phone.value)
      localStorage.setItem('avatar', avatar.value)
      localStorage.setItem('role', role.value)
    }
  }

  return {
    token, role, userId, username, name, phone, avatar,
    isLoggedIn, roleLabel, roleMap,
    login, register, logout, fetchProfile
  }
})
