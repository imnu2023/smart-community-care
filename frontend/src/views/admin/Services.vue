<template>
  <div class="admin-services">
    <div class="page-header">
      <h2>服务管理</h2>
      <el-button type="primary" @click="showAddModal = true">添加服务</el-button>
    </div>
    
    <el-table :data="serviceList" border>
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="serviceName" label="服务名称" />
      <el-table-column prop="serviceType" label="服务类型">
        <template #default="scope">
          {{ getServiceTypeName(scope.row.serviceType) }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="price" label="价格">
        <template #default="scope">
          ¥{{ scope.row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="provider" label="服务商" />
      <el-table-column prop="phone" label="联系电话" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'">
            {{ scope.row.status === 'active' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="editService(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteService(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog title="添加服务" v-model="showAddModal">
      <el-form :model="form" label-width="100px">
        <el-form-item label="服务名称">
          <el-input v-model="form.serviceName" />
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="form.serviceType">
            <el-option label="家政服务" value="housekeeping" />
            <el-option label="医疗护理" value="medical" />
            <el-option label="生活服务" value="life" />
            <el-option label="文化娱乐" value="culture" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input type="number" v-model="form.price" />
        </el-form-item>
        <el-form-item label="服务商">
          <el-input v-model="form.provider" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="saveService">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { serviceAPI } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const serviceList = ref([])
const showAddModal = ref(false)
const form = reactive({
  id: '',
  serviceName: '',
  serviceType: '',
  description: '',
  price: '',
  provider: '',
  phone: ''
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

const editService = (service) => {
  Object.assign(form, service)
  showAddModal.value = true
}

const saveService = async () => {
  try {
    let response
    if (form.id) {
      response = await serviceAPI.update(form.id, form)
    } else {
      response = await serviceAPI.add(form)
    }
    
    if (response.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '添加成功')
      showAddModal.value = false
      form.id = ''
      form.serviceName = ''
      form.serviceType = ''
      form.description = ''
      form.price = ''
      form.provider = ''
      form.phone = ''
      loadServices()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteService = async (serviceId) => {
  await ElMessageBox.confirm('确定要删除该服务吗？', '确认删除', {
    type: 'warning'
  })
  
  try {
    const response = await serviceAPI.delete(serviceId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadServices()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const getServiceTypeName = (type) => {
  const types = {
    housekeeping: '家政服务',
    medical: '医疗护理',
    life: '生活服务',
    culture: '文化娱乐'
  }
  return types[type] || type
}

onMounted(() => {
  loadServices()
})
</script>

<style scoped>
.admin-services {
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