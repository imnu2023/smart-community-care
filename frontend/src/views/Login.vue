<template>
  <div class="login-container">
    <div class="bg-animation">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-wrapper">
      <div class="login-box">
        <div class="logo-section">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5z"/>
              <path d="M2 17l10 5 10-5"/>
              <path d="M2 12l10 5 10-5"/>
            </svg>
          </div>
          <div class="logo-text">
            <h1>智慧社区养老平台</h1>
            <p>AI驱动 · 智慧养老</p>
          </div>
        </div>
        
        <div class="form-container">
          <div class="form-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号信息</p>
          </div>
          
          <el-form ref="formRef" :model="form" class="login-form">
            <div class="input-group">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </div>
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名" 
                class="custom-input"
                :prefix-icon="null"
              />
            </div>
            
            <div class="input-group">
              <div class="input-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
              </div>
              <el-input 
                type="password" 
                v-model="form.password" 
                placeholder="请输入密码" 
                class="custom-input"
                :prefix-icon="null"
              />
            </div>
            
            <div class="form-options">
              <label class="checkbox-label">
                <input type="checkbox" v-model="rememberMe" />
                <span class="checkmark"></span>
                <span>记住我</span>
              </label>
              <a href="#" class="forgot-link">忘记密码?</a>
            </div>
            
            <el-button type="primary" class="login-btn" @click="handleLogin">
              <span class="btn-text">登 录</span>
              <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </el-button>
          </el-form>
          
          <div class="register-section">
            <span>还没有账号？</span>
            <a href="/register" class="register-link">立即注册</a>
          </div>
        </div>
        
        <div class="login-footer">
          <p>© 2026 智慧社区养老平台 · 用心服务每一位老人</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { authAPI } from '../api'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const rememberMe = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  try {
    const response = await authAPI.login(form)
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
        } else {
          window.location.href = '/dashboard/health'
        }
      }, 500)
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    ElMessage.error('登录失败，请检查用户名和密码')
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.bg-animation {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.3;
  animation: float 15s infinite ease-in-out;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.3);
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: rgba(102, 126, 234, 0.4);
  bottom: -150px;
  right: -100px;
  animation-delay: -5s;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: rgba(118, 75, 162, 0.3);
  top: 50%;
  right: 10%;
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(50px, -50px) scale(1.1); }
  50% { transform: translate(-30px, 30px) scale(0.9); }
  75% { transform: translate(20px, 20px) scale(1.05); }
}

.login-wrapper {
  position: relative;
  z-index: 10;
}

.login-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 48px;
  border-radius: 24px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255, 255, 255, 0.1);
  width: 420px;
  overflow: hidden;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.logo-icon {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.logo-icon svg {
  width: 26px;
  height: 26px;
}

.logo-text h1 {
  color: #1e293b;
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.5px;
}

.logo-text p {
  color: #94a3b8;
  font-size: 12px;
  margin: 4px 0 0 0;
}

.form-container {
  margin-bottom: 24px;
}

.form-header {
  text-align: center;
  margin-bottom: 28px;
}

.form-header h2 {
  color: #1e293b;
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.form-header p {
  color: #64748b;
  font-size: 14px;
  margin: 6px 0 0 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  z-index: 1;
}

.input-icon svg {
  width: 18px;
  height: 18px;
}

.custom-input {
  width: 100%;
  height: 48px;
  padding-left: 48px !important;
  padding-right: 16px !important;
  border: 2px solid #e2e8f0;
  border-radius: 14px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: #f8fafc;
}

.custom-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background: #fff;
}

.custom-input::placeholder {
  color: #94a3b8;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 4px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 13px;
  color: #64748b;
}

.checkbox-label input[type="checkbox"] {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 2px solid #cbd5e1;
  border-radius: 5px;
  position: relative;
  transition: all 0.3s ease;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid #fff;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.forgot-link {
  color: #667eea;
  font-size: 13px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #764ba2;
}

.login-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 14px !important;
  font-size: 16px !important;
  font-weight: 600 !important;
  color: #fff !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 10px !important;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.5);
}

.login-btn:active {
  transform: translateY(0);
}

.btn-icon {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.login-btn:hover .btn-icon {
  transform: translateX(5px);
}

.register-section {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.register-section span {
  color: #64748b;
  font-size: 14px;
}

.register-link {
  color: #667eea;
  font-size: 14px;
  font-weight: 500;
  margin-left: 8px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: #764ba2;
}

.login-footer {
  text-align: center;
  padding-top: 16px;
  margin-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.login-footer p {
  color: #94a3b8;
  font-size: 12px;
  margin: 0;
}
</style>