<template>
  <div class="as-page">
    <div class="as-header">
      <h2 class="text-headline-md">服务管理</h2>
      <button class="btn btn-primary" @click="showAddModal = true">＋ 添加服务</button>
    </div>

    <div class="as-table-wrap">
      <el-table :data="serviceList">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="serviceName" label="服务名称" />
        <el-table-column prop="serviceType" label="服务类型" width="100"><template #default="s">{{ getServiceTypeName(s.row.serviceType) }}</template></el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="80"><template #default="s">¥{{ s.row.price }}</template></el-table-column>
        <el-table-column prop="provider" label="服务商" width="100" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="status" label="状态" width="80"><template #default="s"><el-tag :type="s.row.status === 'active' ? 'success' : 'danger'">{{ s.row.status === 'active' ? '启用' : '禁用' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="160"><template #default="s"><div style="display:flex;gap:8px"><el-button type="primary" size="small" @click="editService(s.row)">编辑</el-button><el-button type="danger" size="small" @click="deleteService(s.row.id)">删除</el-button></div></template></el-table-column>
      </el-table>
    </div>

    <el-dialog :title="editingId ? '编辑服务' : '添加服务'" v-model="showAddModal" @close="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.serviceName" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.serviceType"><el-option label="家政服务" value="housekeeping" /><el-option label="医疗护理" value="medical" /><el-option label="生活服务" value="life" /><el-option label="文化娱乐" value="culture" /></el-select></el-form-item>
        <el-form-item label="描述"><el-input type="textarea" v-model="form.description" /></el-form-item>
        <el-form-item label="价格"><el-input type="number" v-model="form.price" /></el-form-item>
        <el-form-item label="服务商"><el-input v-model="form.provider" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="showAddModal = false">取消</el-button><el-button type="primary" @click="saveService">{{ editingId ? '保存' : '添加' }}</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { serviceAPI } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const serviceList = ref([]); const showAddModal = ref(false); const editingId = ref(null)
const form = reactive({ serviceName: '', serviceType: '', description: '', price: '', provider: '', phone: '' })

const loadServices = async () => { try { const r = await serviceAPI.getAll(); if (r.code === 200) serviceList.value = r.data || [] } catch (e) {} }
const resetForm = () => { editingId.value = null; Object.keys(form).forEach(k => form[k] = '') }
const editService = (s) => { editingId.value = s.id; Object.assign(form, { serviceName: s.serviceName, serviceType: s.serviceType, description: s.description, price: s.price, provider: s.provider, phone: s.phone }); showAddModal.value = true }
const saveService = async () => {
  try {
    if (editingId.value) { await serviceAPI.update(editingId.value, { ...form }); ElMessage.success('已更新') }
    else { await serviceAPI.add({ ...form, status: 'active' }); ElMessage.success('已添加') }
    showAddModal.value = false; resetForm(); loadServices()
  } catch (e) { ElMessage.error('操作失败') }
}
const deleteService = async (id) => { await ElMessageBox.confirm('确定删除该服务？', '确认删除', { type: 'warning' }); try { await serviceAPI.delete(id); ElMessage.success('已删除'); loadServices() } catch (e) {} }
const getServiceTypeName = (t) => ({ housekeeping: '家政服务', medical: '医疗护理', life: '生活服务', culture: '文化娱乐' }[t] || t)

onMounted(() => loadServices())
</script>

<style scoped>
.as-page { background: transparent; }
.as-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-lg); }
.as-header h2 { margin: 0; }
.as-table-wrap { background: var(--color-surface-container-lowest); border-radius: var(--radius-xl); border: 1px solid var(--color-outline-variant); overflow: hidden; box-shadow: var(--shadow-card); }
</style>
