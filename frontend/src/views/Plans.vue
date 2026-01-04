<template>
  <div class="plans">
    <el-card style="margin-bottom: 20px">
      <el-button type="primary" @click="showGenerateDialog = true">
        <el-icon><MagicStick /></el-icon>
        AI生成健身计划
      </el-button>
      <el-button type="success" @click="showCreateDialog = true" style="margin-left: 10px">
        <el-icon><Edit /></el-icon>
        自定义创建计划
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

    <!-- 自定义创建计划对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="自定义创建健身计划"
      width="600px"
    >
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="createForm.planName" placeholder="请输入计划名称" />
        </el-form-item>

        <el-form-item label="健身目标" prop="goal">
          <el-select v-model="createForm.goal" placeholder="请选择健身目标" style="width: 100%">
            <el-option label="减脂" value="减脂" />
            <el-option label="增肌" value="增肌" />
            <el-option label="塑形" value="塑形" />
            <el-option label="保持" value="保持" />
          </el-select>
        </el-form-item>

        <el-form-item label="难度等级" prop="difficulty">
          <el-select v-model="createForm.difficulty" placeholder="请选择难度等级" style="width: 100%">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>

        <el-form-item label="训练周期" prop="duration">
          <el-input-number v-model="createForm.duration" :min="1" :max="52" style="width: 100%" />
          <span style="margin-left: 10px">周</span>
        </el-form-item>

        <el-form-item label="计划描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入计划描述"
          />
        </el-form-item>

        <el-form-item label="训练内容" prop="planContent">
          <el-input
            v-model="createForm.planContent"
            type="textarea"
            :rows="6"
            placeholder="请输入详细的训练内容，例如：&#10;第1-4周：&#10;周一：胸部训练 - 卧推 4组x12次&#10;周三：背部训练 - 引体向上 4组x10次&#10;周五：腿部训练 - 深蹲 4组x15次"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="handleCancelCreate">取消</el-button>
        <el-button type="primary" @click="handleCreatePlan" :loading="creating">
          创建计划
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

        <el-divider />

        <el-button type="warning" @click="handleOptimizePlan" style="width: 100%">
          <el-icon><Magic /></el-icon>
          AI优化此计划
        </el-button>
      </div>
    </el-dialog>

    <!-- AI优化计划对话框 -->
    <el-dialog
      v-model="showOptimizeDialog"
      title="AI优化健身计划"
      width="500px"
    >
      <el-alert
        title="提示"
        type="info"
        description="AI将根据新的训练目标重新优化您的计划内容"
        :closable="false"
        style="margin-bottom: 20px"
      />
      <el-form :model="optimizeForm" label-width="100px">
        <el-form-item label="新训练目标">
          <el-select v-model="optimizeForm.goal" placeholder="请选择新的训练目标" style="width: 100%">
            <el-option label="减脂" value="减脂" />
            <el-option label="增肌" value="增肌" />
            <el-option label="塑形" value="塑形" />
            <el-option label="保持" value="保持" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showOptimizeDialog = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmOptimize" :loading="optimizing">
          开始优化
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { generateFitnessPlan, createFitnessPlan, optimizeFitnessPlan, getFitnessPlans, getPlanDetail } from '@/api'

const plans = ref([])
const showGenerateDialog = ref(false)
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const showOptimizeDialog = ref(false)
const generating = ref(false)
const creating = ref(false)
const optimizing = ref(false)
const currentPlan = ref({})
const createFormRef = ref(null)

const generateForm = ref({
  goal: ''
})

const optimizeForm = reactive({
  goal: ''
})

const createForm = reactive({
  planName: '',
  goal: '',
  difficulty: '',
  duration: 8,
  description: '',
  planContent: ''
})

const createRules = {
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' }
  ],
  goal: [
    { required: true, message: '请选择健身目标', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入训练周期', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入计划描述', trigger: 'blur' }
  ],
  planContent: [
    { required: true, message: '请输入训练内容', trigger: 'blur' }
  ]
}

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

const handleCancelCreate = () => {
  showCreateDialog.value = false
  resetCreateForm()
}

const resetCreateForm = () => {
  createForm.planName = ''
  createForm.goal = ''
  createForm.difficulty = ''
  createForm.duration = 8
  createForm.description = ''
  createForm.planContent = ''
}

const handleCreatePlan = async () => {
  try {
    await createFormRef.value.validate()
    creating.value = true

    await createFitnessPlan({
      plan_name: createForm.planName,
      goal: createForm.goal,
      difficulty: createForm.difficulty,
      duration: createForm.duration,
      description: createForm.description,
      plan_content: createForm.planContent
    })

    ElMessage.success('计划创建成功')
    showCreateDialog.value = false
    resetCreateForm()
    loadPlans()
  } catch (error) {
    console.error(error)
  } finally {
    creating.value = false
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

const handleOptimizePlan = () => {
  optimizeForm.goal = currentPlan.value.goal || ''
  showOptimizeDialog.value = true
}

const handleConfirmOptimize = async () => {
  if (!optimizeForm.goal) {
    ElMessage.warning('请选择新的训练目标')
    return
  }

  try {
    optimizing.value = true
    await optimizeFitnessPlan(currentPlan.value.id, {
      goal: optimizeForm.goal
    })
    ElMessage.success('计划优化成功')
    showOptimizeDialog.value = false
    showDetailDialog.value = false
    loadPlans()
  } catch (error) {
    console.error(error)
  } finally {
    optimizing.value = false
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

.plan-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.plan-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.plan-card:hover::before {
  transform: scaleX(1);
}

.plan-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.25);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.plan-name {
  font-weight: bold;
  font-size: 17px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.plan-content {
  padding: 15px 0;
}

.plan-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  color: #606266;
  font-size: 14px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.plan-info:hover {
  background: #e9ecef;
  transform: translateX(5px);
}

.plan-desc {
  margin: 15px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
  padding: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-left: 3px solid #667eea;
  border-radius: 4px;
}

.plan-date {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #ebeef5;
}

:deep(.el-card) {
  border-radius: 12px;
  transition: all 0.3s ease;
}

:deep(.el-card:hover) {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
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

:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-tag) {
  border-radius: 6px;
  font-weight: 500;
  padding: 4px 12px;
}
</style>
