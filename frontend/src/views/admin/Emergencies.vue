<template>
  <div class="admin-emergencies">
    <div class="page-header">
      <h2>紧急呼叫管理</h2>
      <el-button type="primary" @click="loadCalls">刷新列表</el-button>
    </div>
    
    <div class="stats-row">
      <el-card class="stat-card">
        <div class="stat-value">{{ pendingCount }}</div>
        <div class="stat-label">待响应</div>
      </el-card>
      <el-card class="stat-card warning">
        <div class="stat-value">{{ respondingCount }}</div>
        <div class="stat-label">处理中</div>
      </el-card>
      <el-card class="stat-card success">
        <div class="stat-value">{{ resolvedCount }}</div>
        <div class="stat-label">已处理</div>
      </el-card>
    </div>
    
    <el-table :data="callList" border>
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="userId" label="用户ID" />
      <el-table-column prop="callType" label="呼叫类型">
        <template #default="scope">
          <el-tag :type="getCallTypeTag(scope.row.callType)">
            {{ getCallTypeName(scope.row.callType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="callTime" label="呼叫时间" />
      <el-table-column prop="handler" label="处理人" />
      <el-table-column prop="responseTime" label="响应时间" />
      <el-table-column prop="resolvedTime" label="处理时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'pending'" type="primary" size="small" @click="respondCall(scope.row.id)">响应</el-button>
          <el-button v-if="scope.row.status === 'responding'" type="success" size="small" @click="resolveCall(scope.row.id)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { emergencyAPI } from '../../api'
import { ElMessage } from 'element-plus'

const callList = ref([])

const pendingCount = computed(() => callList.value.filter(c => c.status === 'pending').length)
const respondingCount = computed(() => callList.value.filter(c => c.status === 'responding').length)
const resolvedCount = computed(() => callList.value.filter(c => c.status === 'resolved').length)

const loadCalls = async () => {
  try {
    const response = await emergencyAPI.getUnresolved()
    if (response.code === 200) {
      callList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载呼叫记录失败')
  }
}

const respondCall = async (callId) => {
  try {
    const response = await emergencyAPI.respond(callId, localStorage.getItem('name'))
    if (response.code === 200) {
      ElMessage.success('已响应')
      loadCalls()
    }
  } catch (error) {
    ElMessage.error('响应失败')
  }
}

const resolveCall = async (callId) => {
  try {
    const response = await emergencyAPI.resolve(callId)
    if (response.code === 200) {
      ElMessage.success('已处理完成')
      loadCalls()
    }
  } catch (error) {
    ElMessage.error('处理失败')
  }
}

const getCallTypeName = (type) => {
  const types = {
    medical: '医疗急救',
    fire: '火灾报警',
    security: '安全求助'
  }
  return types[type] || type
}

const getCallTypeTag = (type) => {
  if (type === 'medical') return 'danger'
  if (type === 'fire') return 'warning'
  return 'info'
}

const getStatusType = (status) => {
  if (status === 'resolved') return 'success'
  if (status === 'responding') return 'warning'
  return 'info'
}

const getStatusText = (status) => {
  if (status === 'pending') return '待响应'
  if (status === 'responding') return '处理中'
  if (status === 'resolved') return '已处理'
  return '已取消'
}

onMounted(() => {
  loadCalls()
})
</script>

<style scoped>
.admin-emergencies {
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

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  border-left: 4px solid #667eea;
}

.stat-card.warning {
  border-left-color: #f5a623;
}

.stat-card.success {
  border-left-color: #7ed321;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #999;
  font-size: 14px;
}
</style>