import axios from 'axios'

export const instance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// ==================== 请求拦截：Token 注入 ====================
instance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// ==================== 响应拦截：统一错误处理 ====================
instance.interceptors.response.use(
  // HTTP 2xx：仍需检查业务 code
  response => {
    const body = response.data
    if (body && body.code === 200) {
      return body
    }
    // code !== 200：业务异常（乐观锁冲突、余额不足、权限不足等）
    return Promise.reject(body || { code: -1, message: '未知错误' })
  },
  // HTTP 4xx/5xx：网络或服务端异常
  error => {
    if (error.response) {
      const { status, data } = error.response

      // 401：Token 过期或未登录
      if (status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userId')
        localStorage.removeItem('name')
        localStorage.removeItem('role')
        window.location.href = '/login'
        return Promise.reject(data || { code: 401, message: '登录已过期，请重新登录' })
      }

      // 403：权限不足
      if (status === 403) {
        return Promise.reject(data || { code: 403, message: '权限不足' })
      }

      // 服务端返回了错误体
      if (data) {
        return Promise.reject(data)
      }

      // 仅有 HTTP 状态码，无错误体
      return Promise.reject({ code: status, message: `请求失败 (${status})` })
    }

    // 网络超时或断网
    if (error.code === 'ECONNABORTED') {
      return Promise.reject({ code: -1, message: '请求超时，请稍后重试' })
    }

    // 其他网络错误
    return Promise.reject({ code: -1, message: '网络连接失败，请检查网络' })
  }
)

export const captchaAPI = {
  get: () => instance.get('/auth/captcha')
}

export const authAPI = {
  login: (data) => instance.post('/auth/login', data),
  register: (data) => instance.post('/auth/register', data)
}

export const userAPI = {
  getAll: () => instance.get('/users'),
  getById: (id) => instance.get(`/users/${id}`),
  update: (id, data) => instance.put(`/users/${id}`, data),
  delete: (id) => instance.delete(`/users/${id}`)
}

export const healthAPI = {
  save: (data) => instance.post('/health', data),
  getWeekly: (userId) => instance.get(`/health/weekly/${userId}`),
  getWarning: (userId) => instance.get(`/health/warning/${userId}`),
  getLatest: (userId) => instance.get(`/health/latest/${userId}`),
  analyze: (userId) => instance.get(`/health/analyze/${userId}`),
  generateReport: (userId) => instance.post(`/health/report/${userId}`)
}

export const deviceAPI = {
  getAll: () => instance.get('/devices'),
  add: (data) => instance.post('/devices', data),
  update: (id, data) => instance.put(`/devices/${id}`, data),
  delete: (id) => instance.delete(`/devices/${id}`),
  getByUser: (userId) => instance.get(`/devices/user/${userId}`),
  getById: (id) => instance.get(`/devices/${id}`),
  control: (deviceId, action) => instance.post(`/devices/${deviceId}/control`, { action })
}

export const emergencyAPI = {
  create: (data) => instance.post('/emergency/call', data),
  getById: (id) => instance.get(`/emergency/${id}`),
  getByUser: (userId) => instance.get(`/emergency/user/${userId}`),
  getUnresolved: () => instance.get('/emergency/unresolved'),
  respond: (callId, handler) => instance.put(`/emergency/${callId}/respond?handler=${encodeURIComponent(handler)}`),
  resolve: (callId) => instance.put(`/emergency/${callId}/resolve`),
  cancel: (callId) => instance.put(`/emergency/${callId}/cancel`)
}

export const serviceAPI = {
  add: (data) => instance.post('/services', data),
  update: (id, data) => instance.put(`/services/${id}`, data),
  delete: (id) => instance.delete(`/services/${id}`),
  getAll: () => instance.get('/services'),
  getByType: (type) => instance.get(`/services/type/${type}`),
  getById: (id) => instance.get(`/services/${id}`)
}

export const orderAPI = {
  getAll: () => instance.get('/orders'),
  create: (data) => instance.post('/orders', data),
  getById: (id) => instance.get(`/orders/${id}`),
  getByUser: (userId) => instance.get(`/orders/user/${userId}`),
  getByStatus: (status) => instance.get(`/orders/status/${status}`),
  updateStatus: (orderId, status) => instance.put(`/orders/${orderId}/status`, { status }),
  cancel: (orderId) => instance.put(`/orders/${orderId}/cancel`)
}

export const messageAPI = {
  getList: (userId) => instance.get(`/messages/${userId}`),
  getUnread: (userId) => instance.get(`/messages/unread/${userId}`),
  countUnread: (userId) => instance.get(`/messages/unread/count/${userId}`),
  markAsRead: (messageId) => instance.put(`/messages/${messageId}/read`),
  markAllAsRead: (userId) => instance.put(`/messages/read-all/${userId}`),
  delete: (messageId) => instance.delete(`/messages/${messageId}`)
}

export const activityAPI = {
  getAll: () => instance.get('/activities'),
  getActive: () => instance.get('/activities/active'),
  getUpcoming: () => instance.get('/activities/upcoming'),
  getByType: (type) => instance.get(`/activities/type/${type}`),
  getByParticipant: (userId) => instance.get(`/activities/participant/${Number(userId)}`),
  getById: (id) => instance.get(`/activities/${id}`),
  create: (data) => instance.post('/activities', data),
  update: (id, data) => instance.put(`/activities/${id}`, data),
  delete: (id) => instance.delete(`/activities/${id}`),
  register: (activityId, userId) => instance.post(`/activities/${activityId}/register?userId=${userId}`),
  unregister: (activityId, userId) => instance.delete(`/activities/${activityId}/register?userId=${userId}`),
  getParticipants: (activityId) => instance.get(`/activities/${activityId}/participants`),
  isParticipant: (activityId, userId) => instance.get(`/activities/${activityId}/participant/${userId}`)
}

export const relationAPI = {
  bind: (elderId, familyMemberId, relationType) => instance.post('/relations/bind', { elderId, familyMemberId, relationType }),
  unbind: (id) => instance.delete(`/relations/${id}`),
  getByElder: (elderId) => instance.get(`/relations/elder/${elderId}`),
  getByFamilyMember: (familyMemberId) => instance.get(`/relations/family/${familyMemberId}`),
  exists: (elderId, familyMemberId) => instance.get(`/relations/exists?elderId=${elderId}&familyMemberId=${familyMemberId}`)
}

export const paymentAPI = {
  pay: (orderId, payerId) => instance.post('/payments/pay', { orderId, payerId }),
  familyPay: (orderId, familyId) => instance.post('/payments/family-pay', { orderId, familyId }),
  confirm: (orderId) => instance.post(`/payments/confirm/${orderId}`),
  getNeedConfirm: () => instance.get('/payments/orders/need-confirm'),
  getElderOrders: (familyId) => instance.get(`/payments/elder-orders/${Number(familyId)}`)
}

export const walletAPI = {
  getWallet: (userId) => instance.get(`/wallet/${userId}`),
  getBalance: (userId) => instance.get(`/wallet/balance/${userId}`),
  recharge: (userId, amount) => instance.post('/wallet/recharge', { userId, amount }),
  pay: (userId, amount) => instance.post('/wallet/pay', { userId, amount })
}