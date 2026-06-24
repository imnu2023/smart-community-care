<template>
  <div class="login-page">
    <!-- 背景装饰：柔和径向光晕 -->
    <div class="login-bg-glow login-bg-glow--1"></div>
    <div class="login-bg-glow login-bg-glow--2"></div>
    <div class="login-bg-glow login-bg-glow--3"></div>

    <main id="main-content" class="login-card">
      <!-- Logo -->
      <div class="login-logo">
        <div class="login-logo-mark">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2L2 7l10 5 10-5-10-5z" />
            <path d="M2 17l10 5 10-5" />
            <path d="M2 12l10 5 10-5" />
          </svg>
        </div>
        <div class="login-logo-text">
          <h1>智慧社区养老</h1>
          <p>温暖可信 · 服务每一位老人</p>
        </div>
      </div>

      <!-- Form -->
      <div class="login-form-wrap">
        <div class="login-form-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>

        <el-form ref="formRef" :model="form" class="login-form" @keyup.enter="handleLogin">
          <!-- Username -->
          <div class="login-input-group">
            <label class="login-input-label" for="username">用户名</label>
            <el-input
              id="username"
              v-model="form.username"
              placeholder="请输入用户名"
              size="large"
            >
              <template #prefix>
                <span class="login-input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                    <circle cx="12" cy="7" r="4" />
                  </svg>
                </span>
              </template>
            </el-input>
          </div>

          <!-- Password -->
          <div class="login-input-group">
            <label class="login-input-label" for="password">密码</label>
            <el-input
              id="password"
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
            >
              <template #prefix>
                <span class="login-input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                </span>
              </template>
            </el-input>
          </div>

          <!-- Captcha -->
          <div class="login-input-group">
            <label class="login-input-label" for="captcha">验证码</label>
            <div class="captcha-row">
              <el-input
                id="captcha"
                v-model="form.captchaCode"
                placeholder="验证码"
                maxlength="4"
                size="large"
                class="captcha-input"
              >
                <template #prefix>
                  <span class="login-input-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                      <line x1="9" y1="9" x2="15" y2="9" />
                      <line x1="9" y1="13" x2="15" y2="13" />
                      <line x1="9" y1="17" x2="12" y2="17" />
                    </svg>
                  </span>
                </template>
              </el-input>
              <div class="captcha-img-box" @click="fetchCaptcha" title="点击刷新验证码">
                <img
                  v-if="captchaImg"
                  :src="captchaImg"
                  alt="验证码图片"
                  class="captcha-img"
                />
                <span v-else class="captcha-img-placeholder">点击获取</span>
                <div class="captcha-img-hint">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15a9 9 0 1 1-2.15-9.37L23 10"/></svg>
                  <span>刷新</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Remember me + Forgot -->
          <div class="login-options">
            <label class="login-checkbox">
              <input type="checkbox" v-model="rememberMe" />
              <span class="login-checkbox-mark"></span>
              <span>记住我</span>
            </label>
            <a class="login-forgot" @click.prevent="handleForgotPassword">忘记密码？</a>
          </div>

          <!-- Submit -->
          <el-button
            type="primary"
            size="large"
            class="login-submit-btn"
            @click="handleLogin"
            :loading="loading"
          >
            登 录
          </el-button>
        </el-form>

        <!-- Register link -->
        <div class="login-register">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>

      <!-- Footer -->
      <div class="login-footer">
        <p>© 2026 智慧社区养老平台 · <router-link to="/register">隐私政策</router-link></p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { captchaAPI, authAPI } from '../api'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const rememberMe = ref(false)
const captchaImg = ref('')
const captchaUuid = ref('')
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  captchaCode: ''
})

const fetchCaptcha = async () => {
  try {
    const response = await captchaAPI.get()
    if (response.code === 200) {
      captchaImg.value = response.data.captchaImg
      captchaUuid.value = response.data.uuid
    } else {
      ElMessage.error(response.message || '获取验证码失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，获取验证码失败')
  }
}

onMounted(() => {
  fetchCaptcha()
})

const handleForgotPassword = () => {
  ElMessage.info('请联系社区管理员重置密码')
}

const handleLogin = async () => {
  if (!form.captchaCode) {
    ElMessage.warning('请输入验证码')
    return
  }
  loading.value = true
  try {
    const response = await authAPI.login({
      username: form.username,
      password: form.password,
      captchaCode: form.captchaCode,
      uuid: captchaUuid.value
    })
    if (response.code === 200) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('userId', response.data.userId)
      localStorage.setItem('username', response.data.username)
      localStorage.setItem('name', response.data.name)
      localStorage.setItem('role', response.data.role)

      ElMessage.success('登录成功')
      setTimeout(() => {
        if (response.data.role === 1) {
          window.location.href = '/admin/users'
        } else if (response.data.role === 3) {
          window.location.href = '/family'
        } else {
          window.location.href = '/dashboard'
        }
      }, 500)
    } else {
      fetchCaptcha()
      form.captchaCode = ''
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error) {
    fetchCaptcha()
    form.captchaCode = ''
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ============================================================
   Login Page — 温暖、深度、信任感
   ============================================================ */

/* ---- Page: 柔和渐变背景 + 径向光晕装饰 ---- */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(ellipse 80% 60% at 20% 20%, rgba(0, 80, 203, 0.04) 0%, transparent 60%),
    radial-gradient(ellipse 60% 80% at 80% 70%, rgba(0, 104, 37, 0.03) 0%, transparent 60%),
    radial-gradient(ellipse 50% 50% at 50% 100%, rgba(165, 59, 24, 0.02) 0%, transparent 50%),
    var(--color-surface);
  padding: var(--space-lg);
  position: relative;
  overflow: hidden;
}

/* 大型径向光晕装饰 */
.login-bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
  animation: login-glow-drift 20s ease-in-out infinite;
}
.login-bg-glow--1 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(0, 80, 203, 0.08) 0%, transparent 70%);
  top: -15%; left: -10%;
  animation-delay: 0s;
}
.login-bg-glow--2 {
  width: 400px; height: 400px;
  background: radial-gradient(circle, rgba(0, 104, 37, 0.06) 0%, transparent 70%);
  bottom: -10%; right: -8%;
  animation-delay: -7s;
}
.login-bg-glow--3 {
  width: 350px; height: 350px;
  background: radial-gradient(circle, rgba(165, 59, 24, 0.04) 0%, transparent 70%);
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -14s;
}
@keyframes login-glow-drift {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.08); }
  66% { transform: translate(-20px, 15px) scale(0.95); }
}

/* ---- Card: 柔和层次，微玻璃质感 ---- */
.login-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-2xl);
  box-shadow:
    0 4px 32px rgba(0, 47, 112, 0.07),
    0 1px 3px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  width: 440px;
  max-width: 100%;
  padding: var(--space-2xl) var(--space-xl) var(--space-xl);
  position: relative;
  z-index: 1;
  animation: login-card-in 0.6s var(--ease-out) both;
}
@keyframes login-card-in {
  from { opacity: 0; transform: translateY(16px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* ---- Logo ---- */
.login-logo {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-xl);
  animation: login-fade-up 0.5s var(--ease-out) 0.1s both;
}

.login-logo-mark {
  width: 52px; height: 52px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-hover) 100%);
  color: var(--color-on-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(0, 80, 203, 0.18);
}

.login-logo-mark svg {
  width: 26px; height: 26px;
}

.login-logo-text h1 {
  font-size: var(--text-headline-md);
  font-weight: var(--weight-semibold);
  color: var(--color-on-surface);
  line-height: var(--leading-headline-md);
  margin: 0;
  letter-spacing: -0.01em;
}

.login-logo-text p {
  font-size: var(--text-label-sm);
  color: var(--color-on-surface-variant);
  margin: var(--space-xxs) 0 0 0;
}

/* ---- Form ---- */
.login-form-header {
  text-align: center;
  margin-bottom: var(--space-xl);
  animation: login-fade-up 0.5s var(--ease-out) 0.15s both;
}

.login-form-header h2 {
  font-size: var(--text-headline-lg);
  font-weight: var(--weight-semibold);
  color: var(--color-on-surface);
  margin: 0;
  letter-spacing: -0.02em;
}

.login-form-header p {
  font-size: var(--text-body-md);
  color: var(--color-on-surface-variant);
  margin: var(--space-xxs) 0 0 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

/* 表单项逐个淡入 */
.login-input-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-xxs);
}
.login-input-group:nth-child(1) { animation: login-fade-up 0.45s var(--ease-out) 0.2s both; }
.login-input-group:nth-child(2) { animation: login-fade-up 0.45s var(--ease-out) 0.25s both; }
.login-input-group:nth-child(3) { animation: login-fade-up 0.45s var(--ease-out) 0.3s both; }

.login-input-label {
  font-size: var(--text-body-md);
  font-weight: var(--weight-medium);
  color: var(--color-on-surface);
}

.login-input-icon {
  display: flex;
  align-items: center;
  color: var(--color-on-surface-variant);
}

.login-input-icon svg {
  width: 20px; height: 20px;
}

/* ---- Captcha ---- */
.captcha-row {
  display: flex;
  gap: var(--space-sm);
}

.captcha-input { flex: 1; }

.captcha-img-box {
  width: 120px; height: 56px;
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  position: relative;
  border: 2px solid var(--color-outline-variant);
  background: var(--color-surface-container);
  flex-shrink: 0;
  transition: border-color var(--duration-fast) var(--ease-out),
              box-shadow var(--duration-fast) var(--ease-out);
}

.captcha-img-box:hover {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(0, 80, 203, 0.08);
}

.captcha-img {
  width: 100%; height: 100%;
  object-fit: cover;
  display: block;
}

.captcha-img-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-size: var(--text-label-sm);
  color: var(--color-on-surface-variant);
}

.captcha-img-hint {
  position: absolute;
  inset: 0;
  background: rgba(0, 80, 203, 0.88);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #fff;
  font-size: var(--text-label-sm);
  font-weight: var(--weight-medium);
  opacity: 0;
  transition: opacity var(--duration-fast) var(--ease-out);
}

.captcha-img-box:hover .captcha-img-hint { opacity: 1; }

/* ---- Options ---- */
.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  animation: login-fade-up 0.45s var(--ease-out) 0.35s both;
}

.login-checkbox {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  cursor: pointer;
  font-size: var(--text-label-md);
  color: var(--color-on-surface-variant);
  user-select: none;
}

.login-checkbox input[type='checkbox'] {
  position: absolute;
  opacity: 0;
  width: 0; height: 0;
}

.login-checkbox-mark {
  width: 20px; height: 20px;
  border: 2px solid var(--color-outline-variant);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--duration-fast) var(--ease-out);
  flex-shrink: 0;
}

.login-checkbox input:checked + .login-checkbox-mark {
  background: var(--color-primary);
  border-color: var(--color-primary);
}

.login-checkbox input:checked + .login-checkbox-mark::after {
  content: '';
  width: 6px; height: 10px;
  border: solid var(--color-on-primary);
  border-width: 0 2px 2px 0;
  transform: rotate(45deg) translateY(-1px);
}

.login-forgot {
  font-size: var(--text-label-md);
  color: var(--color-primary);
  text-decoration: none;
  cursor: pointer;
  background: none;
  border: none;
  font-family: inherit;
  transition: color var(--duration-fast) var(--ease-out);
}

.login-forgot:hover { color: var(--color-primary-hover); }

/* ---- Submit button ---- */
.login-submit-btn {
  width: 100%;
  height: 52px;
  font-size: var(--text-body-md);
  font-weight: var(--weight-semibold);
  margin-top: var(--space-xs);
  letter-spacing: 0.05em;
  animation: login-fade-up 0.45s var(--ease-out) 0.4s both;
  transition: transform 0.15s var(--ease-out), box-shadow 0.15s var(--ease-out);
}
.login-submit-btn:not(:disabled):active {
  transform: scale(0.98);
}

/* ---- Register link ---- */
.login-register {
  text-align: center;
  margin-top: var(--space-lg);
  padding-top: var(--space-lg);
  border-top: 1px solid var(--color-outline-variant);
  font-size: var(--text-body-md);
  color: var(--color-on-surface-variant);
  animation: login-fade-up 0.5s var(--ease-out) 0.45s both;
}

.login-register a {
  font-weight: var(--weight-semibold);
  margin-left: var(--space-xs);
}

/* ---- Footer ---- */
.login-footer {
  text-align: center;
  margin-top: var(--space-lg);
  padding-top: var(--space-md);
  border-top: 1px solid var(--color-outline-variant);
}

.login-footer p {
  font-size: var(--text-label-sm);
  color: var(--color-on-surface-variant);
  margin: 0;
}

.login-footer a {
  font-size: var(--text-label-sm);
  color: var(--color-on-surface-variant);
  text-decoration: underline;
  text-underline-offset: 2px;
}
.login-footer a:hover { color: var(--color-primary); }

/* 通用淡入动画 */
@keyframes login-fade-up {
  from { opacity: 0; transform: translateY(10px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ---- Responsive ---- */
@media (max-width: 767px) {
  .login-page {
    padding: var(--space-md);
    align-items: flex-start;
    padding-top: var(--space-2xl);
  }

  .login-card {
    padding: var(--space-xl) var(--space-lg);
    border-radius: var(--radius-xl);
  }

  .login-logo {
    flex-direction: column;
    text-align: center;
  }

  .login-form-header h2 {
    font-size: var(--text-headline-md);
  }
}
</style>
