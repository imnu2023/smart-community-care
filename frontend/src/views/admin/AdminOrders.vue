<template>
  <div class="ao-page">
    <div class="ao-header">
      <h2 class="text-headline-md">订单管理</h2>
      <el-select v-model="statusFilter" placeholder="筛选状态" style="width:140px">
        <el-option label="全部" value="" />
        <el-option label="待确认" value="pending" />
        <el-option label="处理中" value="confirmed" />
        <el-option label="已完成" value="completed" />
        <el-option label="已取消" value="cancelled" />
      </el-select>
    </div>

    <div class="ao-table-wrap">
      <el-table :data="filteredOrders">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column prop="userId" label="用户ID" width="70" />
        <el-table-column prop="serviceId" label="服务ID" width="70" />
        <el-table-column prop="serviceName" label="服务名称" width="120" />
        <el-table-column prop="status" label="状态" width="80"><template #default="s"><el-tag :type="getStatusType(s.row.status)">{{ getStatusText(s.row.status) }}</el-tag></template></el-table-column>
        <el-table-column prop="serviceTime" label="服务时间" width="160" />
        <el-table-column prop="address" label="服务地址"><template #default="s">{{ fixAddress(s.row.address) }}</template></el-table-column>
        <el-table-column prop="amount" label="金额" width="80"><template #default="s">¥{{ s.row.amount }}</template></el-table-column>
        <el-table-column prop="paymentStatus" label="支付" width="80"><template #default="s"><el-tag :type="s.row.paymentStatus === 'paid' ? 'success' : 'warning'">{{ s.row.paymentStatus === 'paid' ? '已支付' : '未支付' }}</el-tag></template></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="260">
          <template #default="s">
            <div style="display:flex;gap:8px">
              <el-button v-if="s.row.status === 'pending'" type="primary" size="small" @click="updateStatus(s.row.id, 'confirmed')">确认接单</el-button>
              <el-button v-if="s.row.status === 'confirmed'" type="success" size="small" @click="updateStatus(s.row.id, 'completed')">完成</el-button>
              <el-button v-if="s.row.status === 'pending' || s.row.status === 'confirmed'" type="danger" size="small" @click="updateStatus(s.row.id, 'cancelled')">取消</el-button>
              <el-button size="small" plain @click="viewOrder(s.row)">查看</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog title="订单详情" v-model="showDetailModal">
      <div v-if="selectedOrder" class="ao-detail">
        <div class="ao-detail-row"><span>订单号</span><span>{{ selectedOrder.orderNo }}</span></div>
        <div class="ao-detail-row"><span>服务名称</span><span>{{ selectedOrder.serviceName }}</span></div>
        <div class="ao-detail-row"><span>服务时间</span><span>{{ selectedOrder.serviceTime }}</span></div>
        <div class="ao-detail-row"><span>服务地址</span><span>{{ fixAddress(selectedOrder.address) }}</span></div>
        <div class="ao-detail-row"><span>金额</span><span>¥{{ selectedOrder.amount }}</span></div>
        <div class="ao-detail-row"><span>支付状态</span><span>{{ selectedOrder.paymentStatus === 'paid' ? '已支付' : '未支付' }}</span></div>
        <div class="ao-detail-row"><span>订单状态</span><span>{{ getStatusText(selectedOrder.status) }}</span></div>
      </div>
      <template #footer><el-button @click="showDetailModal = false">关闭</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { orderAPI } from '../../api'
import { ElMessage } from 'element-plus'

const orderList = ref([]); const statusFilter = ref(''); const showDetailModal = ref(false); const selectedOrder = ref(null)

const filteredOrders = computed(() => statusFilter.value ? orderList.value.filter(o => o.status === statusFilter.value) : orderList.value)

const loadOrders = async () => { try { const r = await orderAPI.getAll(); if (r.code === 200) orderList.value = r.data || [] } catch (e) {} }
const updateStatus = async (id, status) => { try { await orderAPI.updateStatus(id, status); ElMessage.success('已更新'); loadOrders() } catch (e) { ElMessage.error('操作失败') } }
const viewOrder = (o) => { selectedOrder.value = o; showDetailModal.value = true }

const getStatusText = (s) => ({ pending: '待确认', confirmed: '处理中', completed: '已完成', cancelled: '已取消' }[s] || s)
const getStatusType = (s) => ({ pending: 'warning', confirmed: '', completed: 'success', cancelled: 'info' }[s] || '')
const fixAddress = (a) => a || '-'

onMounted(() => loadOrders())
</script>

<style scoped>
.ao-page { background: transparent; }
.ao-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-lg); }
.ao-header h2 { margin: 0; }
.ao-table-wrap { background: var(--color-surface-container-lowest); border-radius: var(--radius-xl); border: 1px solid var(--color-outline-variant); overflow: hidden; box-shadow: var(--shadow-card); }
.ao-detail { display: flex; flex-direction: column; gap: var(--space-sm); }
.ao-detail-row { display: flex; justify-content: space-between; padding: var(--space-sm) var(--space-xs); border-bottom: 1px solid var(--color-outline-variant); font-size: var(--text-body-md); background: var(--color-surface-container-low); border-radius: var(--radius-sm); margin-bottom: 2px; }
.ao-detail-row:last-child { border-bottom: none; }
</style>
