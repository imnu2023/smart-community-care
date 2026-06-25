<template>
  <div class="relative" id="auth-views">
    <!-- Error Alert -->
    <div
      v-if="errorMsg"
      class="mb-6 bg-error-container text-on-error-container px-4 py-3 rounded-xl font-label-sm text-label-sm flex items-center gap-2"
    >
      <span class="material-symbols-outlined text-base">error</span>
      {{ errorMsg }}
    </div>

    <!-- Login View -->
    <div :class="['view-transition w-full', isLoginMode ? 'active-view' : 'hidden-view']">
      <div class="mb-10">
        <h2 class="font-headline-lg text-5xl font-extrabold text-on-surface mb-3 tracking-tight">欢迎回来</h2>
        <p class="font-body-md text-base text-on-surface-variant">
          还没有账号？
          <button class="text-primary-container font-semibold hover:underline" @click="$emit('toggle', false)">点击注册</button>
        </p>
      </div>
      <form class="space-y-6" @submit.prevent="$emit('login', { username, password, captchaCode })">
        <div>
          <label class="block font-label-md text-base font-semibold text-on-surface mb-2" for="login-username">用户名</label>
          <div class="relative">
            <input
              v-model="username"
              class="w-full h-[56px] px-5 rounded-xl bg-surface-bright border border-outline-variant focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface transition-colors text-lg"
              :class="{ 'opacity-60': loading }"
              id="login-username" placeholder="输入您的用户名" type="text"
              :disabled="loading"
            >
            <span class="material-symbols-outlined absolute right-4 top-1/2 -translate-y-1/2 text-outline-variant">person</span>
          </div>
        </div>
        <div>
          <label class="block font-label-md text-base font-semibold text-on-surface mb-2" for="login-password">密码</label>
          <div class="relative">
            <input
              v-model="password"
              class="w-full h-[56px] px-5 rounded-xl bg-surface-bright border border-outline-variant focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface transition-colors text-lg"
              :class="{ 'opacity-60': loading }"
              id="login-password" placeholder="输入您的密码" type="password"
              :disabled="loading"
            >
            <span class="material-symbols-outlined absolute right-4 top-1/2 -translate-y-1/2 text-outline-variant">lock</span>
          </div>
        </div>
        <div>
          <label class="block font-label-md text-base font-semibold text-on-surface mb-2" for="login-captcha">验证码</label>
          <div class="flex gap-4">
            <div class="relative flex-1">
              <input
                v-model="captchaCode"
                class="w-full h-[56px] px-5 rounded-xl bg-surface-bright border border-outline-variant focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface transition-colors text-lg uppercase"
                :class="{ 'opacity-60': loading }"
                id="login-captcha" placeholder="输入验证码" type="text" maxlength="4"
                :disabled="loading"
              >
              <span class="material-symbols-outlined absolute right-4 top-1/2 -translate-y-1/2 text-outline-variant">verified_user</span>
            </div>
            <button
              type="button"
              class="w-[140px] h-[56px] rounded-xl overflow-hidden cursor-pointer shadow-sm border border-outline-variant hover:border-primary-container transition-colors shrink-0"
              :class="{ 'opacity-50': loading }"
              title="点击刷新验证码"
              :disabled="loading"
              @click="$emit('refreshCaptcha')"
            >
              <img
                v-if="captchaImg"
                :src="captchaImg"
                alt="验证码"
                class="w-full h-full object-cover"
              >
              <span v-else class="flex items-center justify-center h-full font-label-sm text-label-sm text-on-surface-variant">点击获取</span>
            </button>
          </div>
        </div>
        <button
          class="w-full h-[64px] mt-8 rounded-xl bg-primary-container text-on-primary-container font-headline-md text-xl font-bold hover:bg-surface-tint transition-all duration-300 shadow-md hover:shadow-lg active:scale-[0.99] disabled:opacity-50 flex items-center justify-center gap-2"
          type="button"
          :disabled="loading"
          @click="$emit('login', { username, password, captchaCode })"
        >
          <span v-if="loading" class="material-symbols-outlined animate-spin text-lg">progress_activity</span>
          {{ loading ? '登录中…' : '登录' }}
        </button>
      </form>
    </div>

    <!-- Register View -->
    <div :class="['view-transition w-full', !isLoginMode ? 'active-view' : 'hidden-view']">
      <div class="mb-10">
        <h2 class="font-headline-lg text-5xl font-extrabold text-on-surface mb-3 tracking-tight">加入社区</h2>
        <p class="font-body-md text-base text-on-surface-variant">
          已有账号？
          <button class="text-primary-container font-semibold hover:underline" @click="$emit('toggle', true)">点击登录</button>
        </p>
      </div>
      <form class="space-y-5" @submit.prevent="$emit('register', { role: regRole, fullname: regFullname, phone: regPhone, username: regUsername, password: regPassword })">
        <div class="mb-4">
          <label class="block font-label-sm text-base font-semibold text-on-surface mb-3" for="reg-role">选择角色</label>
          <div class="relative">
            <select
              v-model="regRole"
              class="w-full h-[56px] px-5 rounded-xl bg-surface-bright border border-outline-variant focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface text-lg appearance-none cursor-pointer transition-colors"
              :class="{ 'opacity-60': loading }"
              id="reg-role"
              :disabled="loading"
            >
              <option value="elderly">老人</option>
              <option value="family">家属</option>
            </select>
            <span class="material-symbols-outlined absolute right-4 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none">expand_more</span>
          </div>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block font-label-md text-base font-semibold text-on-surface mb-2" for="reg-fullname">姓名</label>
            <div class="relative">
              <input
                v-model="regFullname"
                class="w-full h-[56px] px-5 rounded-xl bg-surface-bright border border-outline-variant focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface text-lg"
                :class="{ 'opacity-60': loading }"
                id="reg-fullname" placeholder="真实姓名" type="text"
                :disabled="loading"
              >
            </div>
          </div>
          <div>
            <label class="block font-label-md text-base font-semibold text-on-surface mb-2" for="reg-phone">手机号码</label>
            <div class="relative">
              <input
                v-model="regPhone"
                class="w-full h-[56px] px-5 rounded-xl bg-surface-bright border border-outline-variant focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface text-lg"
                :class="{ 'opacity-60': loading }"
                id="reg-phone" placeholder="11位手机号" type="tel"
                :disabled="loading"
              >
            </div>
          </div>
        </div>
        <div>
          <label class="block font-label-md text-base font-semibold text-on-surface mb-2" for="reg-username">用户名</label>
          <div class="relative">
            <input
              v-model="regUsername"
              class="w-full h-[56px] px-5 rounded-xl bg-surface-bright border border-outline-variant focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface text-lg"
              :class="{ 'opacity-60': loading }"
              id="reg-username" placeholder="用于登录" type="text"
              :disabled="loading"
            >
          </div>
        </div>
        <div>
          <label class="block font-label-md text-base font-semibold text-on-surface mb-2" for="reg-password">密码</label>
          <div class="relative">
            <input
              v-model="regPassword"
              class="w-full h-[56px] px-5 rounded-xl bg-surface-bright border border-outline-variant focus:border-primary-container focus:ring-1 focus:ring-primary-container text-on-surface text-lg"
              :class="{ 'opacity-60': loading }"
              id="reg-password" placeholder="设置密码" type="password"
              :disabled="loading"
            >
          </div>
        </div>
        <button
          class="w-full h-[64px] mt-6 rounded-xl bg-primary-container text-on-primary-container font-headline-md text-xl font-bold hover:bg-surface-tint transition-all duration-300 shadow-md hover:shadow-lg active:scale-[0.99] disabled:opacity-50 flex items-center justify-center gap-2"
          type="button"
          :disabled="loading"
          @click="$emit('register', { role: regRole, fullname: regFullname, phone: regPhone, username: regUsername, password: regPassword })"
        >
          <span v-if="loading" class="material-symbols-outlined animate-spin text-lg">progress_activity</span>
          {{ loading ? '注册中…' : '注册' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  isLoginMode: { type: Boolean, required: true },
  loading: { type: Boolean, default: false },
  errorMsg: { type: String, default: '' },
  captchaImg: { type: String, default: '' }
})

defineEmits(['toggle', 'login', 'register', 'refreshCaptcha'])

// Login form state
const username = ref('')
const password = ref('')
const captchaCode = ref('')

// Register form state
const regRole = ref('elderly')
const regFullname = ref('')
const regPhone = ref('')
const regUsername = ref('')
const regPassword = ref('')
</script>

<style scoped>
.view-transition {
  transition: opacity 0.3s ease-in-out, transform 0.3s ease-in-out;
}
.hidden-view {
  opacity: 0;
  transform: translateY(10px);
  pointer-events: none;
  position: absolute;
  visibility: hidden;
}
.active-view {
  opacity: 1;
  transform: translateY(0);
  position: relative;
  visibility: visible;
}
</style>
