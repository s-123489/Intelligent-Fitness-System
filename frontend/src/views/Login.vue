<template>
  <div class="login-container">
    <div class="background-animation">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <div class="login-content">
      <div class="left-section">
        <div class="brand-section">
          <div class="logo-circle">
            <el-icon :size="60" color="#fff"><TrendCharts /></el-icon>
          </div>
          <h1 class="brand-title">智能健身系统</h1>
          <p class="brand-subtitle">AI驱动的个性化健身管理平台</p>

          <div class="features">
            <div class="feature-item">
              <el-icon :size="24" color="#fff"><CircleCheck /></el-icon>
              <span>AI智能健身计划</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24" color="#fff"><DataAnalysis /></el-icon>
              <span>数据可视化分析</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24" color="#fff"><TrophyBase /></el-icon>
              <span>科学营养建议</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24" color="#fff"><ChatDotRound /></el-icon>
              <span>健身社区互动</span>
            </div>
          </div>
        </div>
      </div>

      <div class="right-section">
        <el-card class="login-card">
          <div class="card-header-custom">
            <h2>欢迎回来</h2>
            <p>登录您的账户，开始健身之旅</p>
          </div>

          <el-form :model="loginForm" :rules="rules" ref="formRef" label-width="0">
            <el-form-item prop="username">
              <div class="input-wrapper">
                <el-icon class="input-icon"><User /></el-icon>
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  size="large"
                  class="custom-input"
                />
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="input-wrapper">
                <el-icon class="input-icon"><Lock /></el-icon>
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                  class="custom-input"
                  @keyup.enter="handleLogin"
                />
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-button"
                :loading="loading"
                @click="handleLogin"
              >
                <span v-if="!loading">立即登录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>

            <el-form-item>
              <div class="register-link">
                <span>还没有账户？</span>
                <el-link type="primary" @click="$router.push('/register')" class="link-text">
                  立即注册
                </el-link>
              </div>
            </el-form-item>
          </el-form>

          <div class="demo-account">
            <el-divider>测试账户</el-divider>
            <div class="demo-info">
              <el-icon><InfoFilled /></el-icon>
              <span>用户名: <code>testuser</code> | 密码: <code>123456</code></span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    const res = await login(loginForm)
    userStore.setToken(res.token)
    userStore.setUserInfo(res.user)

    ElMessage.success('登录成功，欢迎回来！')
    router.push('/dashboard')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.background-animation {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.shape-1 {
  width: 300px;
  height: 300px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 200px;
  height: 200px;
  top: 60%;
  right: 15%;
  animation-delay: 5s;
}

.shape-3 {
  width: 150px;
  height: 150px;
  bottom: 10%;
  left: 50%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

.login-content {
  position: relative;
  display: flex;
  min-height: 100vh;
  z-index: 1;
}

.left-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: white;
}

.brand-section {
  max-width: 500px;
}

.logo-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  animation: pulse 3s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(255, 255, 255, 0.7);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 0 0 20px rgba(255, 255, 255, 0);
  }
}

.brand-title {
  font-size: 48px;
  font-weight: bold;
  margin: 0 0 15px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.brand-subtitle {
  font-size: 20px;
  margin: 0 0 50px 0;
  opacity: 0.9;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 18px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  transition: all 0.3s;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(10px);
}

.right-section {
  width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
}

.login-card {
  width: 100%;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
}

.card-header-custom {
  text-align: center;
  margin-bottom: 30px;
}

.card-header-custom h2 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 28px;
  font-weight: bold;
}

.card-header-custom p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 18px;
  color: #909399;
  z-index: 1;
}

.custom-input {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  padding-left: 45px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.custom-input :deep(.el-input__wrapper):hover {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.5);
}

.login-button {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.register-link {
  width: 100%;
  text-align: center;
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.link-text {
  font-weight: bold;
  font-size: 14px;
}

.demo-account {
  margin-top: 20px;
  padding-top: 20px;
}

.demo-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: #f0f9ff;
  border-radius: 8px;
  color: #606266;
  font-size: 13px;
}

.demo-info code {
  padding: 2px 6px;
  background: #e1f3ff;
  border-radius: 4px;
  color: #409eff;
  font-family: 'Courier New', monospace;
  font-weight: bold;
}

@media (max-width: 1024px) {
  .left-section {
    display: none;
  }

  .right-section {
    width: 100%;
    background: transparent;
  }
}
</style>
