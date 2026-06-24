<template>
  <div class="au-page">
    <div class="au-header">
      <h2 class="text-headline-md">用户管理</h2>
      <button class="btn btn-primary" @click="showAddModal = true">＋ 添加用户</button>
    </div>

    <div class="au-table-wrap">
      <el-table :data="userList">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="role" label="角色" width="80">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)">{{ getRoleText(scope.row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="healthStatus" label="健康状态" width="80">
          <template #default="scope">
            <el-tag :type="getHealthStatusType(scope.row.healthStatus)">{{ getHealthStatusText(scope.row.healthStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <div style="display:flex;gap:8px">
              <el-button type="primary" size="small" @click="editUser(scope.row)">编辑</el-button>
              <el-button type="success" size="small" @click="openRechargeModal(scope.row)">充值</el-button>
              <el-button type="danger" size="small" @click="deleteUser(scope.row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="isEditMode ? '编辑用户' : '添加用户'" v-model="showAddModal" @close="handleModalClose">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" placeholder="请输入用户名" /></el-form-item>
        <el-form-item v-if="!isEditMode" label="密码"><el-input type="password" v-model="form.password" placeholder="请输入密码" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" placeholder="请输入姓名" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" placeholder="请输入手机号" /></el-form-item>
        <el-form-item label="年龄"><el-input type="number" v-model="form.age" min="1" max="120" /></el-form-item>
        <el-form-item label="性别"><el-select v-model="form.gender"><el-option label="男" value="male" /><el-option label="女" value="female" /></el-select></el-form-item>
        <el-form-item label="角色"><el-select v-model="form.role"><el-option label="管理员" :value="1" /><el-option label="老人" :value="2" /><el-option label="家属" :value="3" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="showAddModal = false">取消</el-button><el-button type="primary" @click="saveUser">{{ isEditMode ? '保存' : '确定' }}</el-button></template>
    </el-dialog>

    <el-dialog title="用户充值" v-model="showRechargeModal">
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="用户"><el-input :value="rechargeForm.userName" disabled /></el-form-item>
        <el-form-item label="金额"><el-input type="number" v-model="rechargeForm.amount" placeholder="请输入充值金额" /></el-form-item>
        <div style="display:flex;gap:8px;margin-top:12px"><button v-for="amt in [10,50,100,200,500]" :key="amt" type="button" class="btn btn-secondary btn-sm" @click="rechargeForm.amount = amt">¥{{ amt }}</button></div>
      </el-form>
      <template #footer><el-button @click="closeRechargeModal">取消</el-button><el-button type="primary" @click="handleRecharge">确认充值</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userAPI, authAPI, walletAPI } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref([]); const showAddModal = ref(false); const showRechargeModal = ref(false); const isEditMode = ref(false)
const form = reactive({ id: '', username: '', password: '', name: '', phone: '', age: '', gender: 'male', role: 2 })
const rechargeForm = reactive({ userId: '', userName: '', amount: '' })

const loadUsers = async () => { try { const r = await userAPI.getAll(); if (r.code === 200) userList.value = r.data } catch (e) { ElMessage.error('加载失败') } }
const resetForm = () => { Object.assign(form, { id: '', username: '', password: '', name: '', phone: '', age: '', gender: 'male', role: 2 }) }
const editUser = (u) => { isEditMode.value = true; Object.assign(form, u); showAddModal.value = true }
const saveUser = async () => {
  try {
    if (isEditMode.value) { await userAPI.update(form.id, { username: form.username, name: form.name, phone: form.phone, age: form.age, gender: form.gender, role: form.role }); ElMessage.success('已修改') }
    else { await authAPI.register(form); ElMessage.success('已添加') }
    showAddModal.value = false; isEditMode.value = false; resetForm(); loadUsers()
  } catch (e) { ElMessage.error('操作失败') }
}
const handleModalClose = () => { isEditMode.value = false; resetForm() }
const deleteUser = async (id) => { await ElMessageBox.confirm('确定删除该用户？', '确认删除', { type: 'warning' }); try { await userAPI.delete(id); ElMessage.success('已删除'); loadUsers() } catch (e) {} }
const openRechargeModal = (u) => { rechargeForm.userId = u.id; rechargeForm.userName = u.name; rechargeForm.amount = ''; showRechargeModal.value = true }
const closeRechargeModal = () => { showRechargeModal.value = false }
const handleRecharge = async () => { if (!rechargeForm.amount || parseFloat(rechargeForm.amount) <= 0) { ElMessage.error('请输入金额'); return }; try { await walletAPI.recharge(rechargeForm.userId, parseFloat(rechargeForm.amount)); ElMessage.success('充值成功'); closeRechargeModal() } catch (e) { ElMessage.error('充值失败') } }
const getRoleType = (r) => ({ 1: 'danger', 2: 'success', 3: 'warning' }[r] || 'info')
const getRoleText = (r) => ({ 1: '管理员', 2: '老人', 3: '家属' }[r] || '未知')
const getHealthStatusType = (s) => ({ 1: 'success', 2: 'warning' }[s] || 'info')
const getHealthStatusText = (s) => ({ 1: '良好', 2: '一般' }[s] || '未知')

onMounted(() => loadUsers())
</script>

<style scoped>
.au-page { background: transparent; }
.au-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-lg); }
.au-header h2 { margin: 0; }
.au-table-wrap { background: var(--color-surface-container-lowest); border-radius: var(--radius-xl); border: 1px solid var(--color-outline-variant); overflow: hidden; box-shadow: var(--shadow-card); }
</style>
