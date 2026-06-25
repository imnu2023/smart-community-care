<template>
  <div class="min-h-screen flex flex-col bg-surface text-on-surface">
    <!-- ========== Top Navigation ========== -->
    <header class="sticky top-0 z-30 bg-surface border-b border-outline-variant h-16 flex items-center justify-between px-4 md:px-10">
      <!-- Brand -->
      <div class="flex items-center gap-3">
        <div class="w-9 h-9 rounded-lg bg-primary flex items-center justify-center flex-shrink-0">
          <span class="text-white text-base font-bold">伴</span>
        </div>
        <span class="font-label-md text-label-md text-on-surface hidden sm:block">智伴养老</span>
      </div>

      <!-- Actions -->
      <div class="flex items-center gap-2">
        <!-- Messages -->
        <button
          class="relative touch-target flex items-center justify-center rounded-md text-on-surface-variant hover:bg-surface-container-high transition-ui"
          @click="$router.push('/elder/messages')"
          aria-label="消息中心"
        >
          <span class="material-symbols-outlined" style="font-size: 24px;">notifications</span>
          <span
            v-if="unreadCount > 0"
            class="absolute -top-0.5 -right-0.5 min-w-[20px] h-5 flex items-center justify-center rounded-full bg-error text-on-error font-label-sm text-label-sm px-1.5"
          >
            {{ unreadCount > 99 ? '99+' : unreadCount }}
          </span>
        </button>

        <!-- User Menu -->
        <div class="relative" ref="menuRef">
          <button
            class="flex items-center gap-2 touch-target px-3 rounded-md hover:bg-surface-container-high transition-ui"
            @click="menuOpen = !menuOpen"
            aria-label="用户菜单"
            aria-expanded="menuOpen"
          >
            <div class="w-8 h-8 rounded-full bg-primary-container text-on-primary-container flex items-center justify-center font-label-sm text-label-sm flex-shrink-0">
              {{ userInitial }}
            </div>
            <span class="hidden md:block font-body-md text-body-md text-on-surface max-w-[100px] truncate">{{ userName }}</span>
          </button>

          <!-- Dropdown Menu -->
          <Transition name="menu">
            <div
              v-if="menuOpen"
              class="absolute right-0 top-full mt-2 w-48 bg-surface-container-lowest rounded-lg shadow-soft-care-hover border border-outline-variant py-1 z-40"
            >
              <div class="px-4 py-3 border-b border-outline-variant">
                <div class="font-label-md text-label-md text-on-surface truncate">{{ userName }}</div>
                <div class="font-label-sm text-label-sm text-on-surface-variant mt-0.5">{{ userPhone }}</div>
              </div>
              <button
                class="w-full flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container transition-ui font-label-md text-label-md"
                @click="handleLogout"
              >
                <span class="material-symbols-outlined" style="font-size: 20px;">logout</span>
                退出登录
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </header>

    <!-- ========== Main Content ========== -->
    <main class="flex-1 pb-20 md:pb-8">
      <router-view />
    </main>

    <!-- ========== Mobile Bottom Navigation ========== -->
    <nav class="md:hidden fixed bottom-0 inset-x-0 z-30 bg-surface-container-lowest border-t border-outline-variant h-16 flex items-center justify-around safe-bottom">
      <router-link
        v-for="tab in bottomTabs"
        :key="tab.path"
        :to="tab.path"
        class="flex flex-col items-center gap-0.5 px-3 py-1.5 min-w-[64px] rounded-md transition-ui"
        :class="$route.path === tab.path || $route.path.startsWith(tab.path + '/')
          ? 'text-primary'
          : 'text-on-surface-variant'"
        aria-current="page"
      >
        <span class="material-symbols-outlined" style="font-size: 24px;">{{ tab.icon }}</span>
        <span class="font-label-sm text-label-sm whitespace-nowrap">{{ tab.label }}</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user.js'
import { messageAPI } from '../../api/index.js'

const router = useRouter()
const userStore = useUserStore()

// ============ State ============
const menuOpen = ref(false)
const menuRef = ref(null)
const unreadCount = ref(0)

// ============ Computed ============
const userName = computed(() => userStore.name || userStore.username || '用户')
const userPhone = computed(() => userStore.phone || '')
const userInitial = computed(() => (userStore.name || userStore.username || '用').charAt(0))

// ============ Bottom Tabs ============
const bottomTabs = [
  { path: '/elder', icon: 'dashboard', label: '首页' },
  { path: '/elder/health', icon: 'favorite', label: '健康' },
  { path: '/elder/services', icon: 'home_repair_service', label: '服务' },
  { path: '/elder/messages', icon: 'notifications', label: '消息' },
]

// ============ Methods ============
function handleLogout() {
  menuOpen.value = false
  userStore.logout()
}

// ============ Click outside ============
function handleClickOutside(e) {
  if (menuRef.value && !menuRef.value.contains(e.target)) {
    menuOpen.value = false
  }
}

// ============ Fetch unread count ============
async function fetchUnreadCount() {
  try {
    if (!userStore.userId) return
    const res = await messageAPI.countUnread(userStore.userId)
    unreadCount.value = res.data?.count ?? 0
  } catch {
    // 静默失败 — 通知计数不阻塞页面
  }
}

// ============ Lifecycle ============
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  fetchUnreadCount()
  // 每 30s 轮询未读消息数
  const timer = setInterval(fetchUnreadCount, 30000)
  onUnmounted(() => clearInterval(timer))
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* --- Dropdown transition --- */
.menu-enter-active,
.menu-leave-active {
  transition: opacity 150ms ease-out, transform 150ms ease-out;
}
.menu-enter-from,
.menu-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* --- Safe area for bottom nav on notched devices --- */
.safe-bottom {
  padding-bottom: env(safe-area-inset-bottom, 0);
}
</style>
