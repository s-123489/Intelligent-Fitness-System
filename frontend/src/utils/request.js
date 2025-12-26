import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      const status = error.response.status
      const message = error.response.data.message || '请求失败'

      // 401未授权或403禁止访问，都清除token并跳转登录
      if (status === 401 || status === 403) {
        ElMessage.error('认证失败，请重新登录')
        const userStore = useUserStore()
        userStore.logout()
        window.location.href = '/login'
      } else {
        ElMessage.error(message)
      }
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
