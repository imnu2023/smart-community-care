<template>
  <div class="admin-orders">
    <div class="page-header">
      <h2>订单管理</h2>
      <el-select v-model="statusFilter" placeholder="筛选状态">
        <el-option label="全部" value="" />
        <el-option label="待处理" value="pending" />
        <el-option label="已完成" value="completed" />
        <el-option label="已取消" value="cancelled" />
      </el-select>
    </div>
    
    <el-table :data="filteredOrders" border>
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="userId" label="用户ID" />
      <el-table-column prop="serviceId" label="服务ID" />
      <el-table-column prop="serviceName" label="服务名称" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="serviceTime" label="服务时间" />
      <el-table-column prop="address" label="服务地址" />
      <el-table-column prop="amount" label="金额">
        <template #default="scope">
          ¥{{ scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column prop="paymentStatus" label="支付状态">
        <template #default="scope">
          <el-tag :type="scope.row.paymentStatus === 'paid' ? 'success' : 'warning'">
            {{ scope.row.paymentStatus === 'paid' ? '已支付' : '未支付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'pending'" type="primary" size="small" @click="updateStatus(scope.row.id, 'completed')">完成</el-button>
          <el-button v-if="scope.row.status === 'pending'" type="danger" size="small" @click="updateStatus(scope.row.id, 'cancelled')">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { orderAPI, serviceAPI } from '../../api'
import { ElMessage } from 'element-plus'

const orderList = ref([])
const serviceList = ref([])
const statusFilter = ref('')

const filteredOrders = computed(() => {
  let orders = orderList.value.map(order => ({
    ...order,
    serviceName: getServiceName(order.serviceId)
  }))
  
  if (statusFilter.value) {
    orders = orders.filter(o => o.status === statusFilter.value)
  }
  
  return orders
})

const loadOrders = async () => {
  try {
    const response = await orderAPI.getAll()
    if (response.code === 200) {
      orderList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  }
}

const loadServices = async () => {
  try {
    const response = await serviceAPI.getAll()
    if (response.code === 200) {
      serviceList.value = response.data
    }
  } catch (error) {
    console.error('加载服务列表失败')
  }
}

const getServiceName = (serviceId) => {
  const service = serviceList.value.find(s => s.id === serviceId)
  return service ? service.serviceName : '未知服务'
}

const updateStatus = async (orderId, status) => {
  try {
    const response = await orderAPI.updateStatus(orderId, status)
    if (response.code === 200) {
      ElMessage.success('状态已更新')
      loadOrders()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const getStatusType = (status) => {
  if (status === 'completed') return 'success'
  if (status === 'pending') return 'warning'
  return 'danger'
}

const getStatusText = (status) => {
  if (status === 'pending') return '待处理'
  if (status === 'completed') return '已完成'
  return '已取消'
}

onMounted(() => {
  loadOrders()
  loadServices()
})
</script>

<style scoped>
.admin-orders {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}
</style>