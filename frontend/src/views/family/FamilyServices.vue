<template>
  <div class="fs-page">
    <!-- Header -->
    <header class="fs-hero">
      <div>
        <h1 class="fs-title">社区服务</h1>
        <p class="fs-desc">{{ activeType === 'all' ? '为家人预约社区服务' : typeLabel(activeType) }}</p>
      </div>
    </header>

    <!-- Type filter -->
    <nav class="fs-tabs">
      <button
        v-for="t in serviceTypes"
        :key="t.key"
        class="fs-tab"
        :class="{ 'fs-tab--active': activeType === t.key }"
        @click="activeType = t.key"
      >
        <AppIcon :name="t.icon" size="16" />
        {{ t.label }}
      </button>
    </nav>

    <!-- Loading -->
    <div v-if="loading" class="fs-skeleton">
      <div class="fs-grid">
        <div v-for="i in 6" :key="i" class="skeleton" style="height:220px;border-radius:var(--radius-xl)"></div>
      </div>
    </div>

    <!-- Empty -->
    <div v-else-if="filteredServices.length === 0" class="fs-empty">
      <AppIcon name="tool" size="48" class="fs-empty-icon" />
      <h3>暂无此类服务</h3>
      <p>社区暂未发布此类服务，请稍后再查看</p>
    </div>

    <!-- Service cards grid -->
    <div v-else class="fs-grid">
      <article
        v-for="(svc, i) in filteredServices"
        :key="svc.id"
        class="fs-card"
        :style="{ animationDelay: `${0.05 * i}s` }"
      >
        <div class="fs-card-top">
          <span class="fs-card-type-badge" :class="`fs-card-type-badge--${svc.serviceType || 'life'}`">
            {{ typeLabel(svc.serviceType) }}
          </span>
          <span class="fs-card-price">¥{{ svc.price?.toFixed(2) || '0.00' }}</span>
        </div>

        <h3 class="fs-card-name">{{ svc.serviceName }}</h3>
        <p class="fs-card-desc" v-if="svc.description">{{ svc.description }}</p>

        <div class="fs-card-meta">
          <span v-if="svc.provider"><AppIcon name="user" size="13" />{{ svc.provider }}</span>
          <span v-if="svc.phone"><AppIcon name="phone" size="13" />{{ svc.phone }}</span>
        </div>

        <div class="fs-card-foot">
          <button class="btn btn-primary btn-sm" @click="openOrderModal(svc)">
            <AppIcon name="star" size="14" /> 为家人预约
          </button>
        </div>
      </article>
    </div>

    <!-- ================================================================
         ORDER MODAL
         ================================================================ -->
    <Teleport to="body">
      <transition name="fs-modal">
        <div v-if="showOrderModal" class="fs-overlay" @click.self="closeOrderModal">
          <div class="fs-modal-card">
            <div class="fs-modal-head">
              <h3>预约服务</h3>
              <button class="btn btn-ghost btn-sm" @click="closeOrderModal"><AppIcon name="x" size="18" /></button>
            </div>

            <div class="fs-modal-body" v-if="orderTarget">
              <div class="fs-modal-service">
                <span class="fs-modal-svc-icon"><AppIcon name="tool" size="22" /></span>
                <div>
                  <strong>{{ orderTarget.serviceName }}</strong>
                  <p>¥{{ orderTarget.price?.toFixed(2) }} · {{ orderTarget.provider }}</p>
                </div>
              </div>

              <!-- Select elder -->
              <div class="form-group">
                <label class="form-label">为哪位家人预约</label>
                <select v-model="selectedElderId" class="form-input">
                  <option :value="null" disabled>请选择家人</option>
                  <option v-for="e in elders" :key="e.elderId" :value="e.elderId">
                    {{ e.elderName }}（{{ relationLabel(e.relationType) }}）
                  </option>
                </select>
              </div>

              <!-- Service time -->
              <div class="form-group">
                <label class="form-label">预约时间</label>
                <input type="datetime-local" v-model="serviceTime" class="form-input" />
              </div>

              <!-- Address -->
              <div class="form-group">
                <label class="form-label">服务地址</label>
                <input type="text" v-model="address" class="form-input" placeholder="老人的居住地址" />
              </div>

              <!-- Remark -->
              <div class="form-group">
                <label class="form-label">备注（可选）</label>
                <textarea v-model="remark" class="form-input" rows="2" placeholder="特殊需求说明"></textarea>
              </div>
            </div>

            <div class="fs-modal-foot">
              <button class="btn btn-secondary" @click="closeOrderModal">取消</button>
              <button class="btn btn-primary" :disabled="!selectedElderId || !serviceTime || creating" @click="createOrder">
                {{ creating ? '提交中…' : '确认预约' }}
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { serviceAPI, orderAPI, relationAPI } from '../../api'
import AppIcon from '../../components/AppIcon.vue'

const familyId = ref(parseInt(localStorage.getItem('userId') || '0'))
const loading = ref(true)
const services = ref([])
const elders = ref([])
const activeType = ref('all')
const showOrderModal = ref(false)
const orderTarget = ref(null)
const selectedElderId = ref(null)
const serviceTime = ref('')
const address = ref('')
const remark = ref('')
const creating = ref(false)

const serviceTypes = [
  { key: 'all', label: '全部', icon: 'dashboard' },
  { key: 'housekeeping', label: '家政', icon: 'tool' },
  { key: 'medical', label: '医疗', icon: 'heart' },
  { key: 'food', label: '餐饮', icon: 'star' },
  { key: 'life', label: '生活', icon: 'home' },
  { key: 'shopping', label: '购物', icon: 'package' },
  { key: 'companion', label: '陪伴', icon: 'people' }
]

const typeLabel = (t) => serviceTypes.find(s => s.key === t)?.label || t
const relationLabel = (t) => ({ child: '子女', parent: '父母', spouse: '配偶', sibling: '兄弟姐妹' }[t] || '家属')

const filteredServices = computed(() => {
  if (activeType.value === 'all') return services.value
  return services.value.filter(s => s.serviceType === activeType.value)
})

const loadServices = async () => {
  try {
    let res
    if (activeType.value === 'all') res = await serviceAPI.getAll()
    else res = await serviceAPI.getByType(activeType.value)
    if (res?.code === 200) services.value = res.data || []
  } catch (e) { /* silent */ }
  loading.value = false
}

const loadElders = async () => {
  try {
    const res = await relationAPI.getByFamilyMember(familyId.value)
    if (res?.code === 200 && res.data) {
      elders.value = res.data.map(r => ({ elderId: r.elderId, elderName: r.elderName || '老人', relationType: r.relationType }))
    }
  } catch (e) { /* silent */ }
}

const openOrderModal = (svc) => {
  orderTarget.value = svc
  selectedElderId.value = null
  serviceTime.value = ''
  address.value = ''
  remark.value = ''
  showOrderModal.value = true
}

const closeOrderModal = () => { showOrderModal.value = false; orderTarget.value = null }

const createOrder = async () => {
  if (!selectedElderId.value || !serviceTime.value || creating.value) return
  creating.value = true
  try {
    const res = await orderAPI.create({
      userId: selectedElderId.value,
      serviceId: orderTarget.value.id,
      serviceTime: serviceTime.value.replace('T', ' ') + ':00',
      address: address.value || '社区服务中心',
      remark: remark.value
    })
    if (res?.code === 200) {
      ElMessage.success('预约成功')
      closeOrderModal()
    } else {
      ElMessage.error(res?.message || '预约失败')
    }
  } catch (e) { ElMessage.error('预约失败') }
  creating.value = false
}

onMounted(() => { loadServices(); loadElders() })
</script>

<style scoped>
.fs-page { padding-bottom: var(--space-section); }

/* Header */
.fs-hero { margin-bottom: var(--space-xl); }
.fs-title {
  font-size: clamp(28px, 4vw, 40px); font-weight: var(--weight-bold);
  letter-spacing: -0.025em; color: var(--color-on-surface);
  margin: 0 0 var(--space-xs); text-wrap: balance;
}
.fs-desc { font-size: var(--text-body-lg); color: var(--color-on-surface-variant); margin: 0; }

/* Filter tabs */
.fs-tabs {
  display: flex; gap: var(--space-xs); margin-bottom: var(--space-xl);
  flex-wrap: wrap; overflow-x: auto; padding-bottom: var(--space-xs);
}
.fs-tab {
  display: flex; align-items: center; gap: 5px;
  padding: 7px 16px; border-radius: var(--radius-full);
  border: 1px solid var(--color-outline-variant);
  background: var(--color-surface-container-lowest);
  font-size: var(--text-label-sm); font-weight: var(--weight-medium);
  color: var(--color-on-surface-variant); font-family: inherit;
  cursor: pointer; white-space: nowrap;
  transition: all 0.2s cubic-bezier(0.32, 0.72, 0, 1);
}
.fs-tab:hover { background: var(--color-surface-container-high); color: var(--color-on-surface); }
.fs-tab--active { background: var(--color-primary-fixed); border-color: var(--color-primary); color: var(--color-on-primary-fixed); font-weight: var(--weight-semibold); }

/* Skeleton / Empty */
.fs-skeleton .fs-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: var(--space-lg); }
.fs-empty { max-width: 480px; text-align: center; padding: var(--space-2xl); border: 1px dashed var(--color-outline-variant); border-radius: var(--radius-2xl); color: var(--color-on-surface-variant); }
.fs-empty-icon { margin-bottom: var(--space-lg); opacity: 0.35; }
.fs-empty h3 { margin: 0 0 var(--space-xs); color: var(--color-on-surface); font-size: var(--text-headline-md); }

/* Service cards */
.fs-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: var(--space-lg); }
@media (max-width: 767px) { .fs-grid { grid-template-columns: 1fr; } }

.fs-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-xl);
  padding: var(--space-lg);
  box-shadow: var(--shadow-card);
  display: flex; flex-direction: column; gap: var(--space-sm);
  opacity: 0;
  animation: fs-fade-up 0.45s cubic-bezier(0.32, 0.72, 0, 1) forwards;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  position: relative; overflow: hidden;
}
.fs-card::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px;
  background: transparent;
  transition: background-color 0.3s ease;
}
.fs-card:hover::before { background: var(--color-primary); }
.fs-card:hover { box-shadow: var(--shadow-soft-hover); transform: translateY(-2px); }
@keyframes fs-fade-up { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: translateY(0); } }

.fs-card-top { display: flex; justify-content: space-between; align-items: flex-start; }
.fs-card-type-badge {
  padding: 3px 10px; border-radius: var(--radius-full);
  font-size: 11px; font-weight: var(--weight-semibold);
  background: var(--color-surface-container-high); color: var(--color-on-surface-variant);
}
.fs-card-type-badge--housekeeping { background: var(--color-primary-fixed); color: var(--color-on-primary-fixed); }
.fs-card-type-badge--medical { background: var(--color-error-container); color: var(--color-on-error-container); }
.fs-card-type-badge--food { background: var(--color-secondary-fixed); color: var(--color-on-secondary-fixed); }
.fs-card-type-badge--life { background: var(--color-tertiary-fixed); color: var(--color-on-tertiary-fixed); }
.fs-card-price { font-size: var(--text-headline-md); font-weight: var(--weight-bold); color: var(--color-primary); }

.fs-card-name { font-size: var(--text-body-lg); font-weight: var(--weight-semibold); margin: 0; color: var(--color-on-surface); }
.fs-card-desc { font-size: var(--text-body-md); color: var(--color-on-surface-variant); margin: 0; line-height: 1.5; }
.fs-card-meta { display: flex; gap: var(--space-lg); font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fs-card-meta span { display: flex; align-items: center; gap: 4px; }
.fs-card-foot { padding-top: var(--space-sm); border-top: 1px solid var(--color-outline-variant); display: flex; justify-content: flex-end; }

/* ============================================================
   MODAL
   ============================================================ */
.fs-overlay { position: fixed; inset: 0; z-index: var(--z-modal); background: rgba(0,0,0,0.25); backdrop-filter: blur(6px); display: flex; align-items: center; justify-content: center; }
.fs-modal-card { width: 90%; max-width: 480px; background: var(--color-surface-container-lowest); border-radius: var(--radius-2xl); box-shadow: 0 24px 64px rgba(0,47,112,0.12); overflow: hidden; }
.fs-modal-head { display: flex; justify-content: space-between; align-items: center; padding: var(--space-lg) var(--space-xl); border-bottom: 1px solid var(--color-outline-variant); }
.fs-modal-head h3 { margin: 0; font-size: var(--text-headline-md); }
.fs-modal-body { padding: var(--space-xl); display: flex; flex-direction: column; gap: var(--space-md); }
.fs-modal-foot { display: flex; gap: var(--space-sm); justify-content: flex-end; padding: var(--space-lg) var(--space-xl); border-top: 1px solid var(--color-outline-variant); }
.fs-modal-service { display: flex; align-items: center; gap: var(--space-md); padding: var(--space-md); background: var(--color-surface-container-low); border-radius: var(--radius-lg); }
.fs-modal-service strong { display: block; font-size: var(--text-label-md); }
.fs-modal-service p { margin: 2px 0 0; font-size: var(--text-label-sm); color: var(--color-on-surface-variant); }
.fs-modal-svc-icon { width: 44px; height: 44px; border-radius: var(--radius-md); background: var(--color-primary-fixed); color: var(--color-primary); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }

.fs-modal-enter-active { transition: opacity 0.3s var(--ease-out); }
.fs-modal-leave-active { transition: opacity 0.2s var(--ease-out); }
.fs-modal-enter-from, .fs-modal-leave-to { opacity: 0; }

@media (max-width: 767px) {
  .fs-tabs { gap: 4px; }
  .fs-tab { padding: 6px 14px; font-size: var(--text-label-sm); }
  .fs-modal-card { max-width: 100%; border-radius: var(--radius-xl) var(--radius-xl) 0 0; align-self: flex-end; }
}
</style>
