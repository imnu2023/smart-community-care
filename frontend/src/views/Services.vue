<template>
  <div class="services-page">
    <div class="page-header">
      <h2>社区服务</h2>
      <select v-model="selectedType" class="service-select">
        <option value="">全部服务</option>
        <option value="housekeeping">家政服务</option>
        <option value="medical">医疗护理</option>
        <option value="life">生活服务</option>
        <option value="culture">文化娱乐</option>
      </select>
    </div>
    
    <div class="services-grid">
      <div v-for="service in filteredServices" :key="service.id" class="service-card">
        <div class="service-image">
          <svg v-if="service.serviceType === 'housekeeping'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 21l1.65-3.8a9 9 0 1 1 3.4 2.9L3 21"/>
            <path d="M16 11.3A6 6 0 1 1 10.7 6"/>
          </svg>
          <svg v-else-if="service.serviceType === 'medical'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
          <svg v-else-if="service.serviceType === 'life'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
        </div>
        <h3>{{ service.serviceName }}</h3>
        <p class="service-desc">{{ service.description }}</p>
        <div class="service-info">
          <span class="price">¥{{ service.price }}/{{ getUnit(service.serviceType) }}</span>
          <span class="service-type-tag">{{ getServiceTypeName(service.serviceType) }}</span>
        </div>
        <div class="service-contact">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
          </svg>
          <span>{{ service.phone }}</span>
        </div>
        <button class="book-btn" @click="bookService(service)">
          <span>立即预约</span>
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 5l7 7-7 7"/>
          </svg>
        </button>
      </div>
    </div>
    
    <div v-if="showBookModal" class="modal-overlay" @click.self="showBookModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>预约服务</h3>
          <button class="close-btn" @click="showBookModal = false">×</button>
        </div>
        <form @submit.prevent="submitBooking">
          <div class="form-item">
            <label>服务项目</label>
            <input type="text" :value="selectedService?.serviceName" disabled />
          </div>
          <div class="form-item">
            <label>服务时间</label>
            <input type="datetime-local" v-model="bookForm.serviceTime" required />
          </div>
          <div class="form-item">
            <label>服务地址</label>
            <input type="text" v-model="bookForm.address" placeholder="请输入服务地址" required />
          </div>
          <div class="form-item">
            <label>备注信息</label>
            <textarea v-model="bookForm.remark" placeholder="请输入备注信息（选填）"></textarea>
          </div>
          <div class="modal-footer">
            <button type="button" class="cancel-btn" @click="showBookModal = false">取消</button>
            <button type="submit" class="confirm-btn">确认预约</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { serviceAPI, orderAPI } from '../api'
import { ElMessage } from 'element-plus'

const serviceList = ref([])
const selectedType = ref('')
const showBookModal = ref(false)
const selectedService = ref(null)
const bookForm = reactive({
  serviceId: '',
  serviceTime: '',
  address: '',
  remark: ''
})

const loadServices = async () => {
  try {
    const response = await serviceAPI.getAll()
    if (response.code === 200) {
      serviceList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载服务列表失败')
  }
}

const filteredServices = computed(() => {
  if (!selectedType.value) return serviceList.value
  return serviceList.value.filter(s => s.serviceType === selectedType.value)
})

const getServiceTypeName = (type) => {
  const types = {
    housekeeping: '家政服务',
    medical: '医疗护理',
    life: '生活服务',
    culture: '文化娱乐'
  }
  return types[type] || type
}

const getUnit = (type) => {
  const units = {
    housekeeping: '小时',
    medical: '次',
    life: '次',
    culture: '次'
  }
  return units[type] || '次'
}

const bookService = (service) => {
  selectedService.value = service
  bookForm.serviceId = service.id
  showBookModal.value = true
}

const submitBooking = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  
  try {
    const response = await orderAPI.create({
      userId: parseInt(userId),
      serviceId: bookForm.serviceId,
      serviceTime: bookForm.serviceTime,
      address: bookForm.address,
      remark: bookForm.remark
    })
    
    if (response.code === 200) {
      ElMessage.success('预约成功')
      showBookModal.value = false
      selectedService.value = null
      bookForm.serviceId = ''
      bookForm.serviceTime = ''
      bookForm.address = ''
      bookForm.remark = ''
    }
  } catch (error) {
    ElMessage.error('预约失败')
  }
}

onMounted(() => {
  loadServices()
})
</script>

<style scoped>
.services-page {
  padding: 24px;
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #1e293b;
}

.service-select {
  padding: 12px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  transition: border-color 0.3s;
}

.service-select:focus {
  outline: none;
  border-color: #667eea;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.service-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.service-image {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.service-image svg {
  width: 36px;
  height: 36px;
}

.service-card h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #1e293b;
}

.service-desc {
  color: #64748b;
  font-size: 14px;
  margin: 0 0 16px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.service-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.price {
  font-size: 22px;
  font-weight: bold;
  color: #ef4444;
}

.service-type-tag {
  padding: 6px 14px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.service-contact {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #64748b;
  font-size: 13px;
  margin-bottom: 20px;
}

.service-contact svg {
  width: 16px;
  height: 16px;
}

.book-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.book-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.book-btn svg {
  width: 16px;
  height: 16px;
  transition: transform 0.3s;
}

.book-btn:hover svg {
  transform: translateX(4px);
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
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content form {
  padding: 24px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #334155;
}

.form-item input,
.form-item textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-item input:focus,
.form-item textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-item textarea {
  height: 100px;
  resize: none;
}

.form-item input:disabled {
  background: #f8fafc;
  color: #94a3b8;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e2e8f0;
}

.cancel-btn {
  padding: 12px 24px;
  background: #f1f5f9;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  color: #64748b;
  transition: background 0.3s;
}

.cancel-btn:hover {
  background: #e2e8f0;
}

.confirm-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  color: white;
  transition: all 0.3s;
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}
</style>