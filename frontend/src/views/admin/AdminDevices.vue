<template>
  <div class="admin-devices">
    <div class="page-header">
      <h2>设备管理</h2>
    </div>
    
    <el-table :data="deviceList" border>
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="deviceName" label="设备名称" />
      <el-table-column prop="deviceType" label="设备类型">
        <template #default="scope">
          {{ getDeviceTypeName(scope.row.deviceType) }}
        </template>
      </el-table-column>
      <el-table-column prop="deviceId" label="设备ID" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="location" label="位置" />
      <el-table-column prop="lastOnlineTime" label="最后在线" />
      <el-table-column prop="createdAt" label="添加时间" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { deviceAPI } from '../../api'
import { ElMessage } from 'element-plus'

const deviceList = ref([])

const loadDevices = async () => {
  try {
    const response = await deviceAPI.getAll()
    if (response.code === 200) {
      deviceList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载设备列表失败')
  }
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

const getStatusType = (status) => {
  if (status === 'online' || status === 'active') return 'success'
  if (status === 'inactive') return 'warning'
  return 'danger'
}

const getStatusText = (status) => {
  if (status === 'online') return '在线'
  if (status === 'active') return '运行中'
  if (status === 'inactive') return '已关闭'
  return '离线'
}

onMounted(() => {
  loadDevices()
})
</script>

<style scoped>
.admin-devices {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}
</style>