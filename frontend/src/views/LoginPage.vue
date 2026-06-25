<template>
  <div class="min-h-screen bg-background text-on-background font-body-md antialiased flex items-center justify-center">
    <div class="w-[1100px] h-[780px] bg-surface-container-lowest rounded-3xl shadow-2xl flex flex-row overflow-hidden relative">
      <!-- Left Side: Auth Forms -->
      <div class="w-[45%] flex flex-col p-16 bg-surface-container-lowest relative z-10 justify-center">
        <AuthHeader />
        <AuthForms
          :is-login-mode="isLoginMode"
          :loading="loading"
          :error-msg="errorMsg"
          :captcha-img="captchaImg"
          @toggle="isLoginMode = $event"
          @login="handleLogin"
          @register="handleRegister"
          @refresh-captcha="refreshCaptcha"
        />
      </div>
      <!-- Right Side: Hero Illustration -->
      <AuthHero />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user.js'
import { captchaAPI } from '../api/index.js'
import AuthHeader from '../components/auth/AuthHeader.vue'
import AuthForms from '../components/auth/AuthForms.vue'
import AuthHero from '../components/auth/AuthHero.vue'

const router = useRouter()
const userStore = useUserStore()

// ============ State ============
const isLoginMode = ref(true)
const loading = ref(false)
const errorMsg = ref('')
const captchaImg = ref('')
const captchaUuid = ref('')

// ============ Captcha ============
async function refreshCaptcha() {
  errorMsg.value = ''
  try {
    const res = await captchaAPI.get()
    captchaImg.value = res.data.captchaImg
    captchaUuid.value = res.data.uuid
  } catch (e) {
    errorMsg.value = '验证码加载失败，请刷新重试'
  }
}

// ============ Login ============
async function handleLogin({ username, password, captchaCode }) {
  errorMsg.value = ''

  // 前端校验
  if (!username.trim()) return errorMsg.value = '请输入用户名'
  if (!password) return errorMsg.value = '请输入密码'
  if (!captchaCode.trim()) return errorMsg.value = '请输入验证码'
  if (captchaCode.trim().length !== 4) return errorMsg.value = '验证码为4位字符'

  loading.value = true

  try {
    const res = await userStore.login({
      username: username.trim(),
      password,
      captchaCode: captchaCode.trim(),
      uuid: captchaUuid.value
    })
    // 登录成功 → 跳转
    const target = userStore.roleMap[userStore.role] || '/'
    router.push(target)
  } catch (e) {
    errorMsg.value = e?.message || e?.data?.message || '登录失败，请重试'
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// ============ Register ============
async function handleRegister({ role, fullname, phone, username, password }) {
  errorMsg.value = ''

  // 前端校验
  if (!fullname.trim()) return errorMsg.value = '请输入姓名'
  if (!phone.trim() || phone.trim().length !== 11) return errorMsg.value = '请输入正确的11位手机号'
  if (!username.trim()) return errorMsg.value = '请输入用户名'
  if (!password || password.length < 6) return errorMsg.value = '密码至少6位'

  const roleMap = { elderly: 2, family: 3 }
  const roleCode = roleMap[role] || 2

  loading.value = true

  try {
    await userStore.register({
      name: fullname.trim(),
      phone: phone.trim(),
      username: username.trim(),
      password,
      role: roleCode
    })
    // 注册成功 → 切换到登录
    isLoginMode.value = true
    errorMsg.value = ''
    alert('注册成功，请登录')
    refreshCaptcha()
  } catch (e) {
    errorMsg.value = e?.message || e?.data?.message || '注册失败，请重试'
  } finally {
    loading.value = false
  }
}

// ============ Init ============
onMounted(() => {
  refreshCaptcha()
})
</script>
