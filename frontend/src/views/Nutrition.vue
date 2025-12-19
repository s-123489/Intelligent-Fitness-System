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
}

.nutrition-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 8px;
}

.nutrition-icon {
  flex-shrink: 0;
}

.nutrition-info {
  flex: 1;
}

.nutrition-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.nutrition-label {
  font-size: 14px;
  color: #606266;
}

.water-recommendation {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  height: 200px;
}

.water-info {
  flex: 1;
  color: #fff;
}

.water-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
}

.water-label {
  font-size: 16px;
}
</style>
