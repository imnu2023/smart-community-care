import axios from 'axios'

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

instance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

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
  respond: (callId, handler) => instance.put(`/emergency/${callId}/respond`, { handler }),
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