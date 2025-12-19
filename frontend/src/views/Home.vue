<template>
  <el-container class="home-container">
    <el-aside width="200px">
      <div class="logo">
        <el-icon :size="30"><TrendCharts /></el-icon>
        <span>智能健身</span>
      </div>

      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-menu-item index="/plans">
          <el-icon><DocumentCopy /></el-icon>
          <span>健身计划</span>
        </el-menu-item>

        <el-menu-item index="/training">
          <el-icon><SetUp /></el-icon>
          <span>训练记录</span>
        </el-menu-item>

        <el-menu-item index="/body-data">
          <el-icon><DataLine /></el-icon>
          <span>身体数据</span>
        </el-menu-item>

        <el-menu-item index="/nutrition">
          <el-icon><Food /></el-icon>
          <span>营养建议</span>
        </el-menu-item>

        <el-menu-item index="/community">
          <el-icon><ChatDotRound /></el-icon>
          <span>社区动态</span>
        </el-menu-item>

        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人资料</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-content">
          <div class="header-left">
            <h3>欢迎回来，{{ userStore.userInfo.username }}</h3>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-dropdown">
                <el-icon><User /></el-icon>
                {{ userStore.userInfo.username }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      userStore.logout()
      router.push('/login')
    } catch (error) {
      // 取消操作
    }
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.home-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  color: #fff;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  gap: 10px;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left h3 {
  margin: 0;
  color: #303133;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 8px 15px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
