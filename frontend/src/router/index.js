import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: '/plans',
        name: 'Plans',
        component: () => import('@/views/Plans.vue')
      },
      {
        path: '/training',
        name: 'Training',
        component: () => import('@/views/Training.vue')
      },
      {
        path: '/body-data',
        name: 'BodyData',
        component: () => import('@/views/BodyData.vue')
      },
      {
        path: '/nutrition',
        name: 'Nutrition',
        component: () => import('@/views/Nutrition.vue')
      },
      {
        path: '/community',
        name: 'Community',
        component: () => import('@/views/Community.vue')
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && userStore.token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
