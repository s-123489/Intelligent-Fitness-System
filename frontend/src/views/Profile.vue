<template>
  <div class="profile">
    <el-card>
      <template #header>
        <span>个人资料</span>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="年龄" prop="age">
          <el-input-number
            v-model="form.age"
            :min="10"
            :max="100"
            placeholder="请输入年龄"
          />
        </el-form-item>

        <el-form-item label="身高" prop="height">
          <el-input-number
            v-model="form.height"
            :min="100"
            :max="250"
            :precision="1"
            placeholder="请输入身高"
          />
          <span style="margin-left: 10px">cm</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="loading">
            保存修改
          </el-button>
          <el-button @click="loadProfile">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>账户信息</span>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">
          {{ userStore.userInfo.id }}
        </el-descriptions-item>
        <el-descriptions-item label="用户名">
          {{ userStore.userInfo.username }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱" :span="2">
          {{ form.email || '未设置' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  gender: '',
  age: null,
  height: null
})

const rules = {
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  age: [
    { type: 'number', message: '请输入年龄', trigger: 'blur' }
  ],
  height: [
    { type: 'number', message: '请输入身高', trigger: 'blur' }
  ]
}

const loadProfile = async () => {
  try {
    const res = await getProfile()
    form.username = res.username
    form.email = res.email
    form.gender = res.gender
    form.age = res.age
    form.height = res.height
  } catch (error) {
    console.error(error)
  }
}

const handleUpdate = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await updateProfile({
      email: form.email,
      gender: form.gender,
      age: form.age,
      height: form.height
    })

    ElMessage.success('更新成功')
    loadProfile()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.el-card) {
  border-radius: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

:deep(.el-card:hover) {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  font-weight: 600;
  font-size: 16px;
  border-bottom: 2px solid #667eea;
}

:deep(.el-form-item) {
  margin-bottom: 25px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.4);
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

:deep(.el-descriptions) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
}

:deep(.el-descriptions__content) {
  color: #303133;
  font-weight: 500;
}

:deep(.el-radio-group) {
  display: flex;
  gap: 20px;
}

:deep(.el-radio) {
  margin-right: 0;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-radio:hover) {
  background: #f5f7fa;
}

:deep(.el-input-number) {
  border-radius: 8px;
}
</style>
