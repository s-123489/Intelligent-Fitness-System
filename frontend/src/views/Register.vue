<template>
  <div class="register-container">
    <div class="background-animation">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <div class="register-content">
      <div class="left-section">
        <div class="brand-section">
          <div class="logo-circle">
            <el-icon :size="60" color="#fff"><TrendCharts /></el-icon>
          </div>
          <h1 class="brand-title">加入我们</h1>
          <p class="brand-subtitle">开启您的智能健身之旅</p>

          <div class="benefits">
            <div class="benefit-item">
              <div class="benefit-icon">
                <el-icon :size="32"><MagicStick /></el-icon>
              </div>
              <div class="benefit-content">
                <h3>AI智能推荐</h3>
                <p>根据您的身体数据和目标，智能生成个性化健身计划</p>
              </div>
            </div>

            <div class="benefit-item">
              <div class="benefit-icon">
                <el-icon :size="32"><DataLine /></el-icon>
              </div>
              <div class="benefit-content">
                <h3>数据可视化</h3>
                <p>直观的图表展示训练成果，掌握每一次进步</p>
              </div>
            </div>

            <div class="benefit-item">
              <div class="benefit-icon">
                <el-icon :size="32"><Food /></el-icon>
              </div>
              <div class="benefit-content">
                <h3>营养指导</h3>
                <p>科学的营养建议，助力健身效果最大化</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="right-section">
        <el-card class="register-card">
          <div class="card-header-custom">
            <h2>创建账户</h2>
            <p>填写以下信息，开始您的健身之旅</p>
          </div>

          <el-form :model="registerForm" :rules="rules" ref="formRef" label-width="0">
            <el-form-item prop="username">
              <div class="input-wrapper">
                <el-icon class="input-icon"><User /></el-icon>
                <el-input
                  v-model="registerForm.username"
                  placeholder="请输入用户名（3-20个字符）"
                  size="large"
                  class="custom-input"
                />
              </div>
            </el-form-item>

            <el-form-item prop="email">
              <div class="input-wrapper">
                <el-icon class="input-icon"><Message /></el-icon>
                <el-input
                  v-model="registerForm.email"
                  placeholder="请输入邮箱地址"
                  size="large"
                  class="custom-input"
                />
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <div class="input-wrapper">
                <el-icon class="input-icon"><Lock /></el-icon>
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="请输入密码（6-20个字符）"
                  size="large"
                  show-password
                  class="custom-input"
                />
              </div>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <div class="input-wrapper">
                <el-icon class="input-icon"><Lock /></el-icon>
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入密码"
                  size="large"
                  show-password
                  class="custom-input"
                />
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="register-button"
                :loading="loading"
                @click="handleRegister"
              >
                <span v-if="!loading">立即注册</span>
                <span v-else>注册中...</span>
              </el-button>
            </el-form-item>

            <el-form-item>
              <div class="login-link">
                <span>已有账户？</span>
                <el-link type="primary" @click="$router.push('/login')" class="link-text">
                  立即登录
                </el-link>
              </div>
            </el-form-item>
          </el-form>

          <div class="terms">
            <el-icon><InfoFilled /></el-icon>
            <span>注册即表示您同意我们的服务条款和隐私政策</span>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

// 进入注册页时清除旧的认证信息
onMounted(() => {
  userStore.logout()
})

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await register({
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password
    })

    ElMessage.success({
      message: '注册成功！即将跳转到登录页面...',
      duration: 2000
    })

    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
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

.register-content {
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
  max-width: 550px;
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

.benefits {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.benefit-item {
  display: flex;
  gap: 20px;
  padding: 25px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  transition: all 0.3s;
}

.benefit-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(10px);
}

.benefit-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.benefit-content h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: bold;
}

.benefit-content p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.6;
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

.register-card {
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

.register-button {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.register-button:active {
  transform: translateY(0);
}

.login-link {
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

.terms {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  color: #909399;
  font-size: 12px;
  text-align: center;
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
