<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

// 检查并清除无效的token
onMounted(() => {
  const token = userStore.token

  // 如果token是临时token或格式不对（不是JWT格式），清除它
  if (token && (token === 'temporary-token' || !token.includes('.'))) {
    console.warn('检测到无效token，已自动清除')
    userStore.logout()
    // 如果不在登录/注册页，跳转到登录页
    if (!['/login', '/register'].includes(router.currentRoute.value.path)) {
      router.push('/login')
    }
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  background-color: #f5f7fa;
}

#app {
  width: 100%;
  min-height: 100vh;
}
</style>
