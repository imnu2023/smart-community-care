<template>
  <div class="admin-users">
    <div class="page-header">
      <h2>用户管理</h2>
      <button class="add-btn" @click="showAddModal = true">
        <span>+</span>
        <span>添加用户</span>
      </button>
    </div>
    
    <div class="user-table">
      <el-table :data="userList">
      <el-table-column prop="id" label="ID" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="age" label="年龄" />
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <el-tag :type="getRoleType(scope.row.role)">
            {{ getRoleText(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="healthStatus" label="健康状态">
        <template #default="scope">
          <el-tag :type="getHealthStatusType(scope.row.healthStatus)">
            {{ getHealthStatusText(scope.row.healthStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <div class="action-btn">
            <el-button type="primary" size="small" @click="editUser(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteUser(scope.row.id)">删除</el-button>
          </div>
        </template>
      </el-table-column>
      </el-table>
    </div>
    
    <el-dialog class="custom-modal" title="添加用户" v-model="showAddModal">
      <div class="modal-body">
        <el-form :model="form" label-width="100px">
          <el-form-item label="用户名" class="form-item">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" class="form-item">
            <el-input type="password" v-model="form.password" placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="姓名" class="form-item">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号" class="form-item">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="年龄" class="form-item">
            <el-input type="number" v-model="form.age" min="1" max="120" placeholder="请输入年龄" />
          </el-form-item>
          <el-form-item label="性别" class="form-item">
            <el-select v-model="form.gender" placeholder="请选择性别">
              <el-option label="男" value="male" />
              <el-option label="女" value="female" />
            </el-select>
          </el-form-item>
          <el-form-item label="角色" class="form-item">
            <el-select v-model="form.role" placeholder="请选择角色">
              <el-option label="管理员" :value="1" />
              <el-option label="老人" :value="2" />
              <el-option label="家属" :value="3" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="modal-footer">
          <el-button @click="showAddModal = false">取消</el-button>
          <el-button type="primary" @click="addUser">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userAPI, authAPI } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref([])
const showAddModal = ref(false)
const form = reactive({
  username: '',
  password: '',
  name: '',
  phone: '',
  age: '',
  gender: 'male',
  role: 2
})

const loadUsers = async () => {
  try {
    const response = await userAPI.getAll()
    if (response.code === 200) {
      userList.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

const addUser = async () => {
  try {
    const response = await authAPI.register(form)
    if (response.code === 200) {
      ElMessage.success('添加成功')
      showAddModal.value = false
      form.username = ''
      form.password = ''
      form.name = ''
      form.phone = ''
      form.age = ''
      form.gender = 'male'
      form.role = 2
      loadUsers()
    }
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const editUser = (user) => {
  Object.assign(form, user)
  showAddModal.value = true
}

const deleteUser = async (userId) => {
  await ElMessageBox.confirm('确定要删除该用户吗？', '确认删除', {
    type: 'warning'
  })
  
  try {
    const response = await userAPI.delete(userId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadUsers()
    }
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const getRoleType = (role) => {
  if (role === 1) return 'danger'
  if (role === 2) return 'success'
  return 'info'
}

const getRoleText = (role) => {
  if (role === 1) return '管理员'
  if (role === 2) return '老人用户'
  return '家属用户'
}

const getHealthStatusType = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'warning'
  return 'info'
}

const getHealthStatusText = (status) => {
  if (status === 1) return '良好'
  if (status === 2) return '一般'
  return '未知'
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.user-table {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.user-table .el-table {
  border: none;
}

.user-table .el-table th {
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
  font-size: 13px;
  padding: 16px;
  border-bottom: 2px solid #e2e8f0;
}

.user-table .el-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #f1f5f9;
}

.user-table .el-table tr:hover {
  background: #f8fafc;
}

.role-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.role-tag.admin {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.role-tag.elder {
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
}

.role-tag.family {
  background: rgba(59, 130, 246, 0.1);
  color: #2563eb;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.good {
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
}

.status-tag.normal {
  background: rgba(251, 191, 36, 0.1);
  color: #ca8a04;
}

.status-tag.unknown {
  background: rgba(148, 163, 184, 0.1);
  color: #64748b;
}

.action-btn {
  display: flex;
  gap: 8px;
}

.action-btn .el-button {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 6px;
}

.action-btn .el-button--primary {
  background: #3b82f6;
  border-color: #3b82f6;
}

.action-btn .el-button--primary:hover {
  background: #2563eb;
  border-color: #2563eb;
}

.action-btn .el-button--danger {
  background: #ef4444;
  border-color: #ef4444;
}

.action-btn .el-button--danger:hover {
  background: #dc2626;
  border-color: #dc2626;
}

.modal-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 24px;
  border-radius: 12px 12px 0 0;
}

.modal-header .el-dialog__title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.modal-body {
  padding: 24px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  font-weight: 500;
  color: #334155;
  margin-bottom: 8px;
}

.form-item .el-input,
.form-item .el-select {
  width: 100%;
}

.form-item .el-input__inner,
.form-item .el-select__input {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  padding: 12px 14px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.form-item .el-input__inner:focus,
.form-item .el-select__input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modal-footer {
  padding: 16px 24px;
  background: #f8fafc;
  border-radius: 0 0 12px 12px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-footer .el-button {
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 500;
}

.modal-footer .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.modal-footer .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.modal-footer .el-button--default {
  background: #f1f5f9;
  border-color: #e2e8f0;
  color: #64748b;
}

.modal-footer .el-button--default:hover {
  background: #e2e8f0;
}

.custom-modal {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.custom-modal .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 24px;
}

.custom-modal .el-dialog__title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.custom-modal .el-dialog__headerbtn .el-dialog__close {
  color: rgba(255, 255, 255, 0.8);
  font-size: 20px;
}

.custom-modal .el-dialog__headerbtn .el-dialog__close:hover {
  color: #fff;
}
</style>