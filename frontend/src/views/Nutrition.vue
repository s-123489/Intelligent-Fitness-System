<template>
  <div class="nutrition">
    <el-card v-if="recommendation">
      <template #header>
        <span>个性化营养建议</span>
      </template>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="nutrition-card">
            <div class="nutrition-icon">
              <el-icon :size="40" color="#409eff"><Sunny /></el-icon>
            </div>
            <div class="nutrition-info">
              <div class="nutrition-value">{{ recommendation.daily_calories }}</div>
              <div class="nutrition-label">每日建议热量(千卡)</div>
            </div>
          </div>
        </el-col>

        <el-col :span="6">
          <div class="nutrition-card">
            <div class="nutrition-icon">
              <el-icon :size="40" color="#67c23a"><Apple /></el-icon>
            </div>
            <div class="nutrition-info">
              <div class="nutrition-value">{{ recommendation.protein }}g</div>
              <div class="nutrition-label">蛋白质</div>
            </div>
          </div>
        </el-col>

        <el-col :span="6">
          <div class="nutrition-card">
            <div class="nutrition-icon">
              <el-icon :size="40" color="#e6a23c"><Food /></el-icon>
            </div>
            <div class="nutrition-info">
              <div class="nutrition-value">{{ recommendation.carbs }}g</div>
              <div class="nutrition-label">碳水化合物</div>
            </div>
          </div>
        </el-col>

        <el-col :span="6">
          <div class="nutrition-card">
            <div class="nutrition-icon">
              <el-icon :size="40" color="#f56c6c"><Grape /></el-icon>
            </div>
            <div class="nutrition-info">
              <div class="nutrition-value">{{ recommendation.fat }}g</div>
              <div class="nutrition-label">脂肪</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-divider />

      <el-row :gutter="20">
        <el-col :span="12">
          <div class="water-recommendation">
            <el-icon :size="50" color="#409eff"><MostlyCloudy /></el-icon>
            <div class="water-info">
              <div class="water-value">{{ recommendation.water }} ml</div>
              <div class="water-label">每日饮水建议</div>
            </div>
          </div>
        </el-col>

        <el-col :span="12">
          <div ref="nutritionChartRef" style="height: 200px"></div>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-top: 20px" v-if="recommendation">
      <template #header>
        <span>推荐食谱</span>
      </template>

      <el-timeline>
        <el-timeline-item
          v-for="(meal, index) in recommendation.meals"
          :key="index"
          :timestamp="getMealTime(index)"
          placement="top"
        >
          <el-card>
            <h4>{{ getMealName(index) }}</h4>
            <p>{{ meal }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-empty v-if="!recommendation" description="请先完善个人资料和身体数据">
      <el-button type="primary" @click="$router.push('/profile')">
        完善资料
      </el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getNutritionRecommendation } from '@/api'

const recommendation = ref(null)
const nutritionChartRef = ref(null)

const loadRecommendation = async () => {
  try {
    const res = await getNutritionRecommendation()
    recommendation.value = res
    await nextTick()
    renderChart()
  } catch (error) {
    console.error(error)
  }
}

const renderChart = () => {
  if (!nutritionChartRef.value || !recommendation.value) return

  const chart = echarts.init(nutritionChartRef.value)

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '营养素比例',
        type: 'pie',
        radius: '70%',
        data: [
          { value: recommendation.value.protein, name: '蛋白质' },
          { value: recommendation.value.carbs, name: '碳水化合物' },
          { value: recommendation.value.fat, name: '脂肪' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

const getMealName = (index) => {
  const names = ['早餐', '午餐', '晚餐', '加餐']
  return names[index] || ''
}

const getMealTime = (index) => {
  const times = ['07:00 - 08:00', '12:00 - 13:00', '18:00 - 19:00', '15:00 或 21:00']
  return times[index] || ''
}

onMounted(() => {
  loadRecommendation()
})
</script>

<style scoped>
.nutrition {
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

.nutrition-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 25px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  border-radius: 12px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.nutrition-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #667eea, #764ba2);
  transition: width 0.3s ease;
}

.nutrition-card:hover::before {
  width: 100%;
  opacity: 0.1;
}

.nutrition-card:hover {
  transform: translateY(-5px) scale(1.03);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.nutrition-icon {
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.nutrition-card:hover .nutrition-icon {
  transform: scale(1.1) rotate(5deg);
}

.nutrition-info {
  flex: 1;
}

.nutrition-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nutrition-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.water-recommendation {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  height: 200px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.water-recommendation::before {
  content: '';
  position: absolute;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  top: -100px;
  right: -100px;
  animation: float 6s infinite ease-in-out;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(-20px, 20px);
  }
}

.water-recommendation:hover {
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
  transform: translateY(-3px);
}

.water-info {
  flex: 1;
  color: #fff;
  position: relative;
  z-index: 1;
}

.water-value {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.water-label {
  font-size: 16px;
  opacity: 0.95;
}

:deep(.el-card) {
  border-radius: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

:deep(.el-card:hover) {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  font-weight: 600;
  font-size: 16px;
}

:deep(.el-timeline-item__card) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-timeline-item__card:hover) {
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
</style>
