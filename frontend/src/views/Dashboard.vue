<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409eff"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalTraining }}</div>
              <div class="stat-label">总训练次数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67c23a"><Timer /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalDuration }}</div>
              <div class="stat-label">总训练时长(分钟)</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#e6a23c"><Sunny /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalCalories }}</div>
              <div class="stat-label">总消耗卡路里</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#f56c6c"><Stopwatch /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ continuousDays }}</div>
              <div class="stat-label">连续打卡天数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>训练数据统计</span>
            </div>
          </template>
          <div ref="trainingChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/training')" style="width: 100%; margin-bottom: 15px">
              <el-icon><CirclePlus /></el-icon>
              添加训练记录
            </el-button>
            <el-button type="success" @click="$router.push('/plans')" style="width: 100%; margin-bottom: 15px">
              <el-icon><DocumentAdd /></el-icon>
              生成健身计划
            </el-button>
            <el-button type="warning" @click="$router.push('/body-data')" style="width: 100%; margin-bottom: 15px">
              <el-icon><DataLine /></el-icon>
              记录身体数据
            </el-button>
            <el-button type="info" @click="$router.push('/nutrition')" style="width: 100%">
              <el-icon><Food /></el-icon>
              查看营养建议
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近训练记录</span>
              <el-button text type="primary" @click="$router.push('/training')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentRecords" style="width: 100%">
            <el-table-column prop="record_date" label="日期" width="120" />
            <el-table-column prop="exercise_name" label="运动项目" />
            <el-table-column prop="duration" label="时长(分钟)" width="120" />
            <el-table-column prop="calories" label="消耗卡路里" width="120" />
            <el-table-column prop="notes" label="备注" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getTrainingStats, getTrainingRecords } from '@/api'

const stats = ref({
  totalTraining: 0,
  totalDuration: 0,
  totalCalories: 0
})
const continuousDays = ref(0)
const recentRecords = ref([])
const trainingChartRef = ref(null)

const loadStats = async () => {
  try {
    const res = await getTrainingStats()
    stats.value = {
      totalTraining: res.total.count || 0,
      totalDuration: res.total.total_duration || 0,
      totalCalories: Math.round(res.total.total_calories || 0)
    }

    // 计算连续打卡天数
    if (res.daily_stats && res.daily_stats.length > 0) {
      const dates = res.daily_stats.map(s => s.date).sort().reverse()
      let continuous = 0
      const today = new Date().toISOString().split('T')[0]

      for (let i = 0; i < dates.length; i++) {
        const expectedDate = new Date()
        expectedDate.setDate(expectedDate.getDate() - i)
        const expectedDateStr = expectedDate.toISOString().split('T')[0]

        if (dates[i] === expectedDateStr) {
          continuous++
        } else {
          break
        }
      }
      continuousDays.value = continuous
    }

    await nextTick()
    renderChart(res.daily_stats || [])
  } catch (error) {
    console.error(error)
  }
}

const loadRecentRecords = async () => {
  try {
    const res = await getTrainingRecords({ days: 7 })
    recentRecords.value = res.slice(0, 5)
  } catch (error) {
    console.error(error)
  }
}

const renderChart = (dailyStats) => {
  if (!trainingChartRef.value) return

  const chart = echarts.init(trainingChartRef.value)

  const dates = dailyStats.map(s => s.date)
  const durations = dailyStats.map(s => s.duration)
  const calories = dailyStats.map(s => s.calories)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['训练时长(分钟)', '消耗卡路里']
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: [
      {
        type: 'value',
        name: '时长(分钟)',
        position: 'left'
      },
      {
        type: 'value',
        name: '卡路里',
        position: 'right'
      }
    ],
    series: [
      {
        name: '训练时长(分钟)',
        type: 'bar',
        data: durations,
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '消耗卡路里',
        type: 'line',
        yAxisIndex: 1,
        data: calories,
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  }

  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

onMounted(() => {
  loadStats()
  loadRecentRecords()
})
</script>

<style scoped>
.dashboard {
  width: 100%;
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

.stat-card {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  overflow: hidden;
  position: relative;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #409eff, #67c23a, #e6a23c, #f56c6c);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.2);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px;
}

.stat-icon {
  font-size: 48px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}

.quick-actions {
  padding: 10px 0;
}

.quick-actions :deep(.el-button) {
  transition: all 0.3s ease;
  border-radius: 8px;
  font-weight: 500;
}

.quick-actions :deep(.el-button:hover) {
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-card) {
  border-radius: 12px;
  transition: all 0.3s ease;
}

:deep(.el-card:hover) {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

:deep(.el-table) {
  border-radius: 8px;
}

:deep(.el-table__header-wrapper) {
  border-radius: 8px 8px 0 0;
}
</style>
