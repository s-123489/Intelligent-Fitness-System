<template>
  <div class="plans">
    <el-card style="margin-bottom: 20px">
      <el-button type="primary" @click="showGenerateDialog = true">
        <el-icon><MagicStick /></el-icon>
        AI生成健身计划
      </el-button>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="8" v-for="plan in plans" :key="plan.id">
        <el-card class="plan-card" @click="viewPlanDetail(plan.id)">
          <template #header>
            <div class="plan-header">
              <span class="plan-name">{{ plan.plan_name }}</span>
              <el-tag :type="getDifficultyType(plan.difficulty)">
                {{ plan.difficulty }}
              </el-tag>
            </div>
          </template>

          <div class="plan-content">
            <div class="plan-info">
              <el-icon><Flag /></el-icon>
              <span>目标：{{ plan.goal }}</span>
            </div>
            <div class="plan-info">
              <el-icon><Timer /></el-icon>
              <span>周期：{{ plan.duration }}周</span>
            </div>
            <div class="plan-desc">
              {{ plan.description }}
            </div>
            <div class="plan-date">
              创建时间：{{ formatDate(plan.created_at) }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- AI生成计划对话框 -->
    <el-dialog
      v-model="showGenerateDialog"
      title="AI生成健身计划"
      width="500px"
    >
      <el-form :model="generateForm" label-width="80px">
        <el-form-item label="健身目标">
          <el-select v-model="generateForm.goal" placeholder="请选择健身目标" style="width: 100%">
            <el-option label="减脂" value="减脂" />
            <el-option label="增肌" value="增肌" />
            <el-option label="塑形" value="塑形" />
            <el-option label="保持" value="保持" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showGenerateDialog = false">取消</el-button>
        <el-button type="primary" @click="generatePlan" :loading="generating">
          生成计划
        </el-button>
      </template>
    </el-dialog>

    <!-- 计划详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="currentPlan.plan_name"
      width="700px"
    >
      <div v-if="currentPlan.exercises">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="目标">{{ currentPlan.goal }}</el-descriptions-item>
          <el-descriptions-item label="难度">{{ currentPlan.difficulty }}</el-descriptions-item>
          <el-descriptions-item label="周期">{{ currentPlan.duration }}周</el-descriptions-item>
          <el-descriptions-item label="说明" :span="2">
            {{ currentPlan.description }}
          </el-descriptions-item>
        </el-descriptions>

        <el-divider>训练项目</el-divider>

        <el-table :data="currentPlan.exercises" style="width: 100%">
          <el-table-column prop="name" label="运动名称" />
          <el-table-column label="训练量">
            <template #default="{ row }">
              <span v-if="row.sets">{{ row.sets }}组 x {{ row.reps }}次</span>
              <span v-else-if="row.duration">{{ row.duration }}分钟</span>
            </template>
          </el-table-column>
          <el-table-column prop="frequency" label="频率" />
          <el-table-column prop="calories" label="消耗卡路里" />
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { generateFitnessPlan, getFitnessPlans, getPlanDetail } from '@/api'

const plans = ref([])
const showGenerateDialog = ref(false)
const showDetailDialog = ref(false)
const generating = ref(false)
const currentPlan = ref({})

const generateForm = ref({
  goal: ''
})

const loadPlans = async () => {
  try {
    const res = await getFitnessPlans()
    plans.value = res
  } catch (error) {
    console.error(error)
  }
}

const generatePlan = async () => {
  if (!generateForm.value.goal) {
    ElMessage.warning('请选择健身目标')
    return
  }

  try {
    generating.value = true
    await generateFitnessPlan(generateForm.value)
    ElMessage.success('计划生成成功')
    showGenerateDialog.value = false
    generateForm.value.goal = ''
    loadPlans()
  } catch (error) {
    console.error(error)
  } finally {
    generating.value = false
  }
}

const viewPlanDetail = async (planId) => {
  try {
    const res = await getPlanDetail(planId)
    currentPlan.value = res
    showDetailDialog.value = true
  } catch (error) {
    console.error(error)
  }
}

const getDifficultyType = (difficulty) => {
  const types = {
    '简单': 'success',
    '中等': 'warning',
    '困难': 'danger'
  }
  return types[difficulty] || 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.split(' ')[0]
}

onMounted(() => {
  loadPlans()
})
</script>

<style scoped>
.plans {
  width: 100%;
}

.plan-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.plan-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.plan-name {
  font-weight: bold;
  font-size: 16px;
}

.plan-content {
  padding: 10px 0;
}

.plan-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  color: #606266;
  font-size: 14px;
}

.plan-desc {
  margin: 15px 0;
  color: #909399;
  font-size: 14px;
  line-height: 1.6;
}

.plan-date {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 10px;
}
</style>
