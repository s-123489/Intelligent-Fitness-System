<template>
  <div class="body-data">
    <el-card style="margin-bottom: 20px">
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><CirclePlus /></el-icon>
        添加身体数据
      </el-button>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>体重变化趋势</span>
          </template>
          <div ref="weightChartRef" style="height: 400px"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <span>最新数据</span>
          </template>
          <div class="latest-data" v-if="bodyData.length > 0">
            <div class="data-item">
              <div class="data-label">体重</div>
              <div class="data-value">{{ bodyData[0].weight }} kg</div>
            </div>
            <div class="data-item">
              <div class="data-label">体脂率</div>
              <div class="data-value">{{ bodyData[0].body_fat }}%</div>
            </div>
            <div class="data-item">
              <div class="data-label">BMI</div>
              <div class="data-value">{{ bodyData[0].bmi }}</div>
            </div>
            <div class="data-item">
              <div class="data-label">记录日期</div>
              <div class="data-value">{{ bodyData[0].record_date }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>历史记录</span>
      </template>
      <el-table :data="bodyData" style="width: 100%">
        <el-table-column prop="record_date" label="日期" width="120" sortable />
        <el-table-column prop="weight" label="体重(kg)" width="120" />
        <el-table-column prop="body_fat" label="体脂率(%)" width="120" />
        <el-table-column prop="bmi" label="BMI" width="120" />
      </el-table>
    </el-card>

    <!-- 添加身体数据对话框 -->
    <el-dialog
      v-model="showAddDialog"
      title="添加身体数据"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="体重" prop="weight">
          <el-input-number
            v-model="form.weight"
            :min="30"
            :max="200"
            :precision="1"
            :step="0.1"
            style="width: 100%"
          />
          <span style="margin-left: 10px">kg</span>
        </el-form-item>

        <el-form-item label="体脂率" prop="body_fat">
          <el-input-number
            v-model="form.body_fat"
            :min="5"
            :max="50"
            :precision="1"
            :step="0.1"
            style="width: 100%"
          />
          <span style="margin-left: 10px">%</span>
        </el-form-item>

        <el-form-item label="记录日期" prop="record_date">
          <el-date-picker
            v-model="form.record_date"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd" :loading="loading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { addBodyData, getBodyData } from '@/api'

const bodyData = ref([])
const showAddDialog = ref(false)
const loading = ref(false)
const formRef = ref(null)
const weightChartRef = ref(null)

const form = reactive({
  weight: 0,
  body_fat: 0,
  record_date: new Date().toISOString().split('T')[0]
})

const rules = {
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' }
  ],
  body_fat: [
    { required: true, message: '请输入体脂率', trigger: 'blur' }
  ],
  record_date: [
    { required: true, message: '请选择记录日期', trigger: 'change' }
  ]
}

const loadBodyData = async () => {
  try {
    const res = await getBodyData()
    bodyData.value = res.reverse()
    await nextTick()
    renderChart()
  } catch (error) {
    console.error(error)
  }
}

const renderChart = () => {
  if (!weightChartRef.value || bodyData.value.length === 0) return

  const chart = echarts.init(weightChartRef.value)

  const dates = bodyData.value.map(d => d.record_date)
  const weights = bodyData.value.map(d => d.weight)
  const bodyFats = bodyData.value.map(d => d.body_fat)
  const bmis = bodyData.value.map(d => d.bmi)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['体重(kg)', '体脂率(%)', 'BMI']
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '体重(kg)',
        type: 'line',
        data: weights,
        smooth: true,
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '体脂率(%)',
        type: 'line',
        data: bodyFats,
        smooth: true,
        itemStyle: {
          color: '#67c23a'
        }
      },
      {
        name: 'BMI',
        type: 'line',
        data: bmis,
        smooth: true,
        itemStyle: {
          color: '#e6a23c'
        }
      }
    ]
  }

  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

const handleAdd = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await addBodyData(form)
    ElMessage.success('添加成功')
    showAddDialog.value = false

    form.weight = 0
    form.body_fat = 0
    form.record_date = new Date().toISOString().split('T')[0]

    loadBodyData()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadBodyData()
})
</script>

<style scoped>
.body-data {
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

.latest-data {
  padding: 10px 0;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 15px;
  border-bottom: 1px solid #ebeef5;
  transition: all 0.3s ease;
  border-radius: 8px;
  margin-bottom: 8px;
}

.data-item:hover {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  transform: translateX(5px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.data-item:last-child {
  border-bottom: none;
}

.data-label {
  font-size: 15px;
  color: #606266;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}

.data-label::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.data-value {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

:deep(.el-card) {
  border-radius: 12px;
  transition: all 0.3s ease;
  overflow: hidden;
}

:deep(.el-card:hover) {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  font-weight: 600;
  font-size: 16px;
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

:deep(.el-table) {
  border-radius: 8px;
}

:deep(.el-dialog) {
  border-radius: 12px;
}
</style>
