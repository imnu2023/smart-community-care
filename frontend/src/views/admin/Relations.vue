<template>
  <div class="ar-page">
    <div class="ar-header">
      <h2 class="text-headline-md">关系管理</h2>
      <button class="btn btn-primary" @click="openBindModal">＋ 绑定关系</button>
    </div>

    <!-- Stats -->
    <div class="ar-stats">
      <div class="ar-stat">
        <span class="ar-stat-num">{{ elders.length }}</span>
        <span class="ar-stat-lbl">老人总数</span>
      </div>
      <div class="ar-stat">
        <span class="ar-stat-num">{{ familyMembers.length }}</span>
        <span class="ar-stat-lbl">家属总数</span>
      </div>
      <div class="ar-stat">
        <span class="ar-stat-num">{{ totalRelations }}</span>
        <span class="ar-stat-lbl">绑定关系</span>
      </div>
    </div>

    <!-- Elder list with their family members -->
    <div class="ar-grid">
      <div v-for="elder in eldersWithRelations" :key="elder.id" class="ar-elder-shell">
        <div class="ar-elder-inner">
          <!-- Elder header -->
          <div class="ar-elder-top">
            <div class="ar-elder-avatar">{{ elder.name?.charAt(0) || '老' }}</div>
            <div class="ar-elder-info">
              <div class="ar-elder-name">{{ elder.name }}</div>
              <div class="ar-elder-meta">{{ elder.phone || '无手机号' }} · {{ elder.age ? elder.age + '岁' : '' }}</div>
            </div>
            <span class="badge badge-success">老人</span>
          </div>

          <!-- Linked family members -->
          <div class="ar-family-list" v-if="elder.familyMembers?.length > 0">
            <div class="ar-family-label">
              <AppIcon name="people" size="14" />
              绑定家属 ({{ elder.familyMembers.length }})
            </div>
            <div v-for="fm in elder.familyMembers" :key="fm.relationId" class="ar-family-item">
              <div class="ar-family-avatar">{{ fm.familyMemberName?.charAt(0) || '家' }}</div>
              <div class="ar-family-info">
                <span class="ar-family-name">{{ fm.familyMemberName }}</span>
                <span class="ar-family-relation">{{ getRelationLabel(fm.relationType) }}</span>
              </div>
              <span class="ar-family-phone">{{ fm.familyMemberPhone || '' }}</span>
              <button class="ar-unbind-btn" @click="confirmUnbind(fm.relationId, elder.name, fm.familyMemberName)" title="解绑">
                <AppIcon name="x" size="14" />
              </button>
            </div>
          </div>
          <div v-else class="ar-no-family">
            <AppIcon name="people" size="16" />
            暂无绑定家属
          </div>
        </div>
      </div>

      <!-- Empty state -->
      <div v-if="eldersWithRelations.length === 0 && !loading" class="empty-state" style="grid-column:1/-1">
        <div class="empty-icon"><AppIcon name="people" size="48" /></div>
        <h3>暂无关系数据</h3>
        <p>点击「绑定关系」创建老人与家属的关联</p>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="ar-grid">
      <div v-for="i in 3" :key="i" class="skeleton" style="height:200px;border-radius:var(--radius-2xl)"></div>
    </div>

    <!-- Bind Modal -->
    <el-dialog title="绑定家属关系" v-model="showBindModal" width="480px">
      <el-form label-width="80px">
        <el-form-item label="选择老人">
          <el-select v-model="bindForm.elderId" placeholder="请选择老人" style="width:100%">
            <el-option v-for="e in elders" :key="e.id" :label="e.name + (e.phone ? ' · ' + e.phone : '')" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择家属">
          <el-select v-model="bindForm.familyMemberId" placeholder="请选择家属" style="width:100%">
            <el-option v-for="f in familyMembers" :key="f.id" :label="f.name + (f.phone ? ' · ' + f.phone : '')" :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关系">
          <el-select v-model="bindForm.relationType" style="width:100%">
            <el-option label="子女" value="child" />
            <el-option label="配偶" value="spouse" />
            <el-option label="父母" value="parent" />
            <el-option label="兄弟姐妹" value="sibling" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBindModal = false">取消</el-button>
        <el-button type="primary" @click="handleBind" :loading="binding">确认绑定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { userAPI, relationAPI } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import AppIcon from '../../components/AppIcon.vue'

const loading = ref(true)
const showBindModal = ref(false)
const binding = ref(false)
const allRelations = ref([])

// User lists
const elders = ref([])
const familyMembers = ref([])

const bindForm = reactive({ elderId: '', familyMemberId: '', relationType: 'child' })

const totalRelations = computed(() => allRelations.value.length)

// Build elders with their linked family members
const eldersWithRelations = computed(() => {
  return elders.value.map(e => {
    const familyMembers = allRelations.value
      .filter(r => r.elderId === e.id)
      .map(r => ({
        relationId: r.id,
        familyMemberId: r.familyMemberId,
        familyMemberName: r.familyMemberName || '未知',
        familyMemberPhone: r.familyMemberPhone || '',
        relationType: r.relationType || 'child'
      }))
    return { ...e, familyMembers }
  })
})

const getRelationLabel = (t) => ({ child: '子女', parent: '父母', spouse: '配偶', sibling: '兄弟姐妹' }[t] || t)

const loadData = async () => {
  loading.value = true
  try {
    // Load all users, filter by role
    const uRes = await userAPI.getAll()
    if (uRes?.code === 200 && uRes.data) {
      elders.value = uRes.data.filter(u => u.role === 2)
      familyMembers.value = uRes.data.filter(u => u.role === 3)
    }

    // Load all relationships (query per elder and aggregate)
    const allRels = []
    for (const e of elders.value) {
      try {
        const rRes = await relationAPI.getByElder(e.id)
        if (rRes?.code === 200 && rRes.data) {
          rRes.data.forEach(r => allRels.push(r))
        }
      } catch (e) { /* skip */ }
    }
    allRelations.value = allRels
  } catch (e) {
    ElMessage.error('加载数据失败')
  }
  loading.value = false
}

const openBindModal = () => {
  bindForm.elderId = ''
  bindForm.familyMemberId = ''
  bindForm.relationType = 'child'
  showBindModal.value = true
}

const handleBind = async () => {
  if (!bindForm.elderId || !bindForm.familyMemberId) {
    ElMessage.warning('请选择老人和家属')
    return
  }
  binding.value = true
  try {
    const res = await relationAPI.bind(
      parseInt(bindForm.elderId),
      parseInt(bindForm.familyMemberId),
      bindForm.relationType
    )
    if (res?.code === 200) {
      ElMessage.success('绑定成功')
      showBindModal.value = false
      loadData()
    } else {
      ElMessage.error(res?.message || '绑定失败')
    }
  } catch (e) {
    ElMessage.error('绑定失败，请检查是否已存在绑定关系')
  }
  binding.value = false
}

const confirmUnbind = async (relationId, elderName, familyName) => {
  try {
    await ElMessageBox.confirm(
      `确定解除「${elderName}」与「${familyName}」的绑定关系？`,
      '确认解绑',
      { type: 'warning' }
    )
    try {
      await relationAPI.unbind(relationId)
      ElMessage.success('已解绑')
      loadData()
    } catch (e) {
      ElMessage.error('解绑失败')
    }
  } catch (e) { /* user cancelled */ }
}

onMounted(() => loadData())
</script>

<style scoped>
.ar-page { background: transparent; }
.ar-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-lg); }
.ar-header h2 { margin: 0; }

/* Stats */
.ar-stats { display: flex; gap: var(--space-lg); margin-bottom: var(--space-lg); }
.ar-stat {
  text-align: center; padding: var(--space-lg);
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  min-width: 130px;
  box-shadow: var(--shadow-card);
  transition: transform 0.2s var(--ease-out);
}
.ar-stat:hover { transform: translateY(-2px); }
.ar-stat-num { display: block; font-size: var(--text-headline-lg); font-weight: var(--weight-bold); letter-spacing: -0.02em; }
.ar-stat-lbl { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }

/* Elder Grid */
.ar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: var(--space-lg);
}

/* Elder Card — Double-Bezel */
.ar-elder-shell {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-2xl);
  padding: 3px;
  box-shadow: var(--shadow-card);
  transition: box-shadow 0.3s cubic-bezier(0.32,0.72,0,1), transform 0.25s cubic-bezier(0.32,0.72,0,1);
}
.ar-elder-shell:hover {
  box-shadow: var(--shadow-soft-hover);
  transform: translateY(-2px);
}
.ar-elder-inner {
  background: linear-gradient(135deg, #fcfdff 0%, #f8f9fd 100%);
  border-radius: calc(var(--radius-2xl) - 4px);
  padding: var(--space-lg);
}

/* Elder Header */
.ar-elder-top { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: var(--space-md); }
.ar-elder-avatar {
  width: 44px; height: 44px; border-radius: 50%;
  background: var(--color-tertiary-fixed); color: var(--color-on-tertiary-fixed);
  display: flex; align-items: center; justify-content: center;
  font-weight: var(--weight-bold); font-size: 18px; flex-shrink: 0;
}
.ar-elder-info { flex: 1; min-width: 0; }
.ar-elder-name { font-size: var(--text-body-md); font-weight: var(--weight-semibold); }
.ar-elder-meta { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }

/* Family List */
.ar-family-list { margin-top: var(--space-xs); }
.ar-family-label {
  display: flex; align-items: center; gap: var(--space-xs);
  font-size: var(--text-label-sm); font-weight: var(--weight-semibold);
  color: var(--color-on-surface-variant);
  margin-bottom: var(--space-xs);
  padding-bottom: var(--space-xs);
  border-bottom: 1px solid var(--color-outline-variant);
}
.ar-family-item {
  display: flex; align-items: center; gap: var(--space-sm);
  padding: var(--space-xs);
  border-radius: var(--radius-md);
  transition: background 0.2s var(--ease-out);
}
.ar-family-item:hover { background: var(--color-surface-container-low); }
.ar-family-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: var(--color-primary-fixed); color: var(--color-on-primary-fixed);
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; font-weight: var(--weight-semibold); flex-shrink: 0;
}
.ar-family-info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.ar-family-name { font-size: var(--text-label-md); font-weight: var(--weight-medium); }
.ar-family-relation { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.ar-family-phone { font-size: var(--text-label-sm); color: var(--color-on-surface-variant); flex-shrink: 0; }
.ar-unbind-btn {
  width: 28px; height: 28px; border-radius: 50%;
  border: 1px solid var(--color-outline-variant);
  background: transparent; cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  color: var(--color-on-surface-variant);
  transition: all 0.2s var(--ease-out);
  flex-shrink: 0;
}
.ar-unbind-btn:hover { background: var(--color-error-container); color: var(--color-error); border-color: var(--color-error); }
.ar-no-family {
  display: flex; align-items: center; gap: var(--space-xs);
  padding: var(--space-md); justify-content: center;
  font-size: var(--text-label-sm); color: var(--color-on-surface-variant);
}

@media (max-width: 767px) {
  .ar-stats { flex-wrap: wrap; }
  .ar-stat { min-width: 100px; flex: 1; }
  .ar-grid { grid-template-columns: 1fr; }
}
</style>
