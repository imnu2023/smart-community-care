<template>
  <div class="ae-page">
    <div class="ae-header">
      <h2 class="text-headline-md">紧急呼叫管理</h2>
      <button class="btn btn-secondary" @click="loadCalls">刷新列表</button>
    </div>
    <div class="ae-stats">
      <div class="ae-stat"><span class="ae-stat-num" style="color:var(--color-error)">{{ pendingCount }}</span><span>待响应</span></div>
      <div class="ae-stat"><span class="ae-stat-num" style="color:var(--color-secondary)">{{ respondingCount }}</span><span>处理中</span></div>
      <div class="ae-stat"><span class="ae-stat-num" style="color:var(--color-tertiary)">{{ resolvedCount }}</span><span>已处理</span></div>
    </div>
    <div class="ae-table-wrap">
      <el-table :data="callList">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userId" label="用户ID" width="70" />
        <el-table-column prop="callType" label="呼叫类型" width="100">
          <template #default="scope"><el-tag :type="getCallTypeTag(scope.row.callType)">{{ getCallTypeName(scope.row.callType) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="scope"><el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="callTime" label="呼叫时间" width="160" />
        <el-table-column prop="handler" label="处理人" width="100" />
        <el-table-column prop="responseTime" label="响应时间" min-width="140" />
        <el-table-column prop="resolvedTime" label="处理时间" min-width="140" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button v-if="scope.row.status === 'pending'" type="primary" size="small" @click="respondCall(scope.row.id)">响应</el-button>
            <el-button v-if="scope.row.status === 'responding'" type="success" size="small" @click="resolveCall(scope.row.id)">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
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

const loadCalls = async () => { try { const r = await emergencyAPI.getUnresolved(); if (r.code === 200) callList.value = r.data || [] } catch (e) {} }
const respondCall = async (id) => { const h = localStorage.getItem('name') || '管理员'; try { await emergencyAPI.respond(id, h); ElMessage.success('已响应'); loadCalls() } catch (e) { ElMessage.error('操作失败') } }
const resolveCall = async (id) => { try { await emergencyAPI.resolve(id); ElMessage.success('已完成'); loadCalls() } catch (e) { ElMessage.error('操作失败') } }

const getCallTypeName = (t) => ({ medical: '医疗急救', fire: '火灾报警', security: '安全求助' }[t] || t)
const getCallTypeTag = (t) => ({ medical: 'danger', fire: 'warning', security: 'info' }[t] || '')
const getStatusText = (s) => ({ pending: '待响应', responding: '处理中', resolved: '已处理', cancelled: '已取消' }[s] || s)
const getStatusType = (s) => ({ pending: 'warning', responding: 'info', resolved: 'success', cancelled: 'info' }[s] || '')

onMounted(() => loadCalls())
</script>

<style scoped>
.ae-page { background: transparent; }
.ae-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-lg); }
.ae-header h2 { margin: 0; }
.ae-stats { display: flex; gap: var(--space-lg); margin-bottom: var(--space-lg); }
.ae-stat { text-align: center; padding: var(--space-lg); background: var(--color-surface-container-lowest); border: 1px solid var(--color-outline-variant); border-radius: var(--radius-xl); min-width: 130px; box-shadow: var(--shadow-card); transition: transform 0.2s var(--ease-out); }
.ae-stat:hover { transform: translateY(-2px); }
.ae-stat-num { display: block; font-size: var(--text-headline-lg); font-weight: var(--weight-bold); letter-spacing: -0.02em; }
.ae-stat span:last-child { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.ae-table-wrap { background: var(--color-surface-container-lowest); border-radius: var(--radius-xl); border: 1px solid var(--color-outline-variant); overflow: hidden; box-shadow: var(--shadow-card); }
</style>
