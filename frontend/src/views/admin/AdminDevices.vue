<template>
  <div class="ad2-page">
    <div class="ad2-header">
      <h2 class="text-headline-md">设备管理</h2>
      <button class="btn btn-primary" @click="openAddModal">＋ 添加设备</button>
    </div>

    <div class="ad2-table-wrap">
      <el-table :data="deviceList">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="deviceName" label="设备名称"><template #default="s">{{ fixDeviceName(s.row.deviceName) }}</template></el-table-column>
        <el-table-column prop="deviceType" label="设备类型" width="100"><template #default="s">{{ getDeviceTypeName(s.row.deviceType) }}</template></el-table-column>
        <el-table-column prop="deviceId" label="设备ID" />
        <el-table-column prop="status" label="状态" width="90"><template #default="s"><el-tag :type="getStatusType(s.row.status)">{{ getStatusText(s.row.status) }}</el-tag></template></el-table-column>
        <el-table-column prop="location" label="位置"><template #default="s">{{ fixLocation(s.row.location) }}</template></el-table-column>
        <el-table-column prop="lastOnlineTime" label="最后在线" width="160" />
        <el-table-column prop="createdAt" label="添加时间" width="160" />
        <el-table-column label="操作" width="160"><template #default="s"><div style="display:flex;gap:8px"><el-button type="primary" size="small" @click="openEditModal(s.row)">编辑</el-button><el-button type="danger" size="small" @click="handleDelete(s.row.id)">删除</el-button></div></template></el-table-column>
      </el-table>
    </div>

    <!-- Add/Edit modal -->
    <el-dialog :title="editingDevice ? '编辑设备' : '添加设备'" v-model="showFormModal" @close="closeFormModal">
      <el-form label-width="80px">
        <el-form-item label="设备名称"><el-input v-model="form.deviceName" placeholder="请输入设备名称" /></el-form-item>
        <el-form-item label="设备类型"><el-select v-model="form.deviceType"><el-option label="智能灯光" value="light" /><el-option label="智能空调" value="aircon" /><el-option label="智能窗帘" value="curtain" /><el-option label="安防设备" value="security" /><el-option label="传感器" value="sensor" /></el-select></el-form-item>
        <el-form-item label="设备ID"><el-input v-model="form.deviceId" placeholder="设备唯一标识" /></el-form-item>
        <el-form-item label="安装位置"><el-input v-model="form.location" placeholder="例如：客厅" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="closeFormModal">取消</el-button><el-button type="primary" @click="saveDevice">{{ editingDevice ? '保存' : '添加' }}</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { deviceAPI } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const deviceList = ref([]); const showFormModal = ref(false); const editingDevice = ref(null)
const form = reactive({ deviceName: '', deviceType: '', deviceId: '', location: '' })

const loadDevices = async () => { try { const r = await deviceAPI.getAll(); if (r.code === 200) deviceList.value = r.data || [] } catch (e) {} }
const openAddModal = () => { editingDevice.value = null; Object.keys(form).forEach(k => form[k] = ''); showFormModal.value = true }
const openEditModal = (d) => { editingDevice.value = d; Object.assign(form, { deviceName: d.deviceName, deviceType: d.deviceType, deviceId: d.deviceId, location: d.location }); showFormModal.value = true }
const closeFormModal = () => { showFormModal.value = false; editingDevice.value = null }
const saveDevice = async () => {
  try {
    if (editingDevice.value) { await deviceAPI.update(editingDevice.value.id, { ...form }); ElMessage.success('已更新') }
    else { await deviceAPI.add(form); ElMessage.success('已添加') }
    closeFormModal(); loadDevices()
  } catch (e) { ElMessage.error('操作失败') }
}
const handleDelete = async (id) => { await ElMessageBox.confirm('确定删除该设备？', '确认删除', { type: 'warning' }); try { await deviceAPI.delete(id); ElMessage.success('已删除'); loadDevices() } catch (e) {} }

const getStatusText = (s) => ({ online: '运行中', active: '运行中', inactive: '已关闭', offline: '离线' }[s] || s)
const getStatusType = (s) => ({ online: 'success', active: 'success', inactive: 'info', offline: 'danger' }[s] || '')
const getDeviceTypeName = (t) => ({ light: '智能灯光', aircon: '智能空调', curtain: '智能窗帘', security: '安防设备', sensor: '传感器' }[t] || t)
const fixDeviceName = (n) => n || '-'
const fixLocation = (l) => l || '-'

onMounted(() => loadDevices())
</script>

<style scoped>
.ad2-page { background: transparent; }
.ad2-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-lg); }
.ad2-header h2 { margin: 0; }
.ad2-table-wrap { background: var(--color-surface-container-lowest); border-radius: var(--radius-xl); border: 1px solid var(--color-outline-variant); overflow: hidden; box-shadow: var(--shadow-card); }
</style>
