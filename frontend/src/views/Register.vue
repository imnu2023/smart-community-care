<template>
  <div class="register-page">
    <div class="register-bg-glow register-bg-glow--1"></div>
    <div class="register-bg-glow register-bg-glow--2"></div>
    <div class="register-bg-glow register-bg-glow--3"></div>

    <main id="main-content" class="register-card">
      <!-- Logo -->
      <div class="register-logo">
        <div class="register-logo-mark">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z" />
            <path d="M2 17l10 5 10-5" />
            <path d="M2 12l10 5 10-5" />
          </svg>
        </div>
        <div class="register-logo-text">
          <h1>智慧社区养老</h1>
          <p>创建您的账号</p>
        </div>
      </div>

      <!-- Form -->
      <div class="register-form-wrap">
        <div class="register-form-header">
          <h2>新用户注册</h2>
          <p>填写以下信息加入智慧社区</p>
        </div>

        <el-form ref="formRef" :model="form" class="register-form" @keyup.enter="handleRegister">
          <div class="register-field">
            <label class="register-label" for="reg-username">用户名</label>
            <el-input id="reg-username" v-model="form.username" placeholder="请输入用户名" size="large" />
          </div>

          <div class="register-field">
            <label class="register-label" for="reg-password">密码</label>
            <el-input id="reg-password" v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password />
          </div>

          <div class="register-field">
            <label class="register-label" for="reg-name">姓名</label>
            <el-input id="reg-name" v-model="form.name" placeholder="请输入真实姓名" size="large" />
          </div>

          <div class="register-row">
            <div class="register-field">
              <label class="register-label" for="reg-phone">手机号</label>
              <el-input id="reg-phone" v-model="form.phone" placeholder="请输入手机号" size="large" />
            </div>
            <div class="register-field">
              <label class="register-label" for="reg-email">邮箱</label>
              <el-input id="reg-email" v-model="form.email" placeholder="请输入邮箱" size="large" />
            </div>
          </div>

          <div class="register-row register-row-3">
            <div class="register-field">
              <label class="register-label" for="reg-age">年龄</label>
              <el-input id="reg-age" v-model="form.age" type="number" placeholder="年龄" size="large" />
            </div>
            <div class="register-field">
              <label class="register-label" for="reg-gender">性别</label>
              <el-select id="reg-gender" v-model="form.gender" placeholder="选择" size="large" style="width:100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </div>
            <div class="register-field">
              <label class="register-label" for="reg-role">角色</label>
              <el-select id="reg-role" v-model="form.role" placeholder="选择" size="large" style="width:100%">
                <el-option label="老人" :value="2" />
                <el-option label="家属" :value="3" />
              </el-select>
            </div>
          </div>

          <el-button type="primary" size="large" class="register-submit-btn" @click="handleRegister" :loading="loading">
            注 册
          </el-button>
        </el-form>

        <div class="register-login-link">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { authAPI } from '../api'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppIcon from '../components/AppIcon.vue'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  name: '',
  phone: '',
  email: '',
  age: '',
  gender: '',
  role: 2
})

const handleRegister = async () => {
  if (!form.username || !form.password || !form.name) {
    ElMessage.warning('请填写必填信息')
    return
  }
  loading.value = true
  try {
    const response = await authAPI.register(form)
    if (response.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error) {
    ElMessage.error('注册失败，请检查信息')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ---- Page: 柔和渐变背景 + 径向光晕装饰 ---- */
.register-page {
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

.register-bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
  animation: register-glow-drift 20s ease-in-out infinite;
}
.register-bg-glow--1 {
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(0, 80, 203, 0.08) 0%, transparent 70%);
  top: -15%; left: -10%;
  animation-delay: 0s;
}
.register-bg-glow--2 {
  width: 400px; height: 400px;
  background: radial-gradient(circle, rgba(0, 104, 37, 0.06) 0%, transparent 70%);
  bottom: -10%; right: -8%;
  animation-delay: -7s;
}
.register-bg-glow--3 {
  width: 350px; height: 350px;
  background: radial-gradient(circle, rgba(165, 59, 24, 0.04) 0%, transparent 70%);
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -14s;
}
@keyframes register-glow-drift {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -20px) scale(1.08); }
  66% { transform: translate(-20px, 15px) scale(0.95); }
}

/* ---- Card: 柔和层次，微玻璃质感 ---- */
.register-card {
  background: var(--color-surface-container-lowest);
  border: 1px solid var(--color-outline-variant);
  border-radius: var(--radius-2xl);
  box-shadow:
    0 4px 32px rgba(0, 47, 112, 0.07),
    0 1px 3px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  width: 560px;
  max-width: 100%;
  padding: var(--space-2xl) var(--space-xl) var(--space-xl);
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  z-index: 1;
  animation: login-card-in 0.6s var(--ease-out) both;
}
@keyframes login-card-in {
  from { opacity: 0; transform: translateY(16px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* ---- Logo ---- */
.register-logo {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-xl);
  animation: login-fade-up 0.5s var(--ease-out) 0.1s both;
}

.register-logo-mark {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-hover) 100%);
  color: var(--color-on-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(0, 80, 203, 0.18);
}

.register-logo-mark svg {
  width: 26px;
  height: 26px;
}

.register-logo-text h1 {
  font-size: var(--text-headline-md);
  font-weight: var(--weight-semibold);
  color: var(--color-on-surface);
  line-height: var(--leading-headline-md);
  margin: 0;
  letter-spacing: -0.01em;
}

.register-logo-text p {
  font-size: var(--text-label-sm);
  color: var(--color-on-surface-variant);
  margin: var(--space-xxs) 0 0 0;
}

/* ---- Form ---- */
.register-form-header {
  text-align: center;
  margin-bottom: var(--space-xl);
  animation: login-fade-up 0.5s var(--ease-out) 0.15s both;
}

.register-form-header h2 {
  font-size: var(--text-headline-lg);
  font-weight: var(--weight-semibold);
  color: var(--color-on-surface);
  margin: 0;
  letter-spacing: -0.02em;
}

.register-form-header p {
  font-size: var(--text-body-md);
  color: var(--color-on-surface-variant);
  margin: var(--space-xxs) 0 0 0;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.register-field {
  display: flex;
  flex-direction: column;
  gap: var(--space-xxs);
  flex: 1;
}
.register-field:nth-child(1) { animation: login-fade-up 0.45s var(--ease-out) 0.2s both; }
.register-field:nth-child(2) { animation: login-fade-up 0.45s var(--ease-out) 0.25s both; }
.register-field:nth-child(3) { animation: login-fade-up 0.45s var(--ease-out) 0.3s both; }
.register-row:nth-child(4) { animation: login-fade-up 0.45s var(--ease-out) 0.35s both; }
.register-row:nth-child(5) { animation: login-fade-up 0.45s var(--ease-out) 0.4s both; }

.register-label {
  font-size: var(--text-body-md);
  font-weight: var(--weight-medium);
  color: var(--color-on-surface);
}

.register-row {
  display: flex;
  gap: var(--space-md);
}

.register-row-3 {
  flex-wrap: wrap;
}

/* ---- Submit ---- */
.register-submit-btn {
  width: 100%;
  height: 52px;
  font-size: var(--text-body-md);
  font-weight: var(--weight-semibold);
  margin-top: var(--space-xs);
  letter-spacing: 0.05em;
  animation: login-fade-up 0.45s var(--ease-out) 0.45s both;
  transition: transform 0.15s var(--ease-out), box-shadow 0.15s var(--ease-out);
}
.register-submit-btn:not(:disabled):active {
  transform: scale(0.98);
}

/* ---- Login link ---- */
.register-login-link {
  text-align: center;
  margin-top: var(--space-lg);
  padding-top: var(--space-lg);
  border-top: 1px solid var(--color-outline-variant);
  font-size: var(--text-body-md);
  color: var(--color-on-surface-variant);
  animation: login-fade-up 0.5s var(--ease-out) 0.5s both;
}

.register-login-link a {
  font-weight: var(--weight-semibold);
  margin-left: var(--space-xs);
}

@keyframes login-fade-up {
  from { opacity: 0; transform: translateY(10px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ---- Responsive ---- */
@media (max-width: 767px) {
  .register-page {
    padding: var(--space-md);
    align-items: flex-start;
    padding-top: var(--space-2xl);
  }

  .register-card {
    padding: var(--space-xl) var(--space-lg);
    border-radius: var(--radius-xl);
  }

  .register-row {
    flex-direction: column;
  }

  .register-logo {
    flex-direction: column;
    text-align: center;
  }
}
</style>
