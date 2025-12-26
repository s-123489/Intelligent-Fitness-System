<template>
  <div class="training">
    <el-card style="margin-bottom: 20px">
      <el-button type="primary" @click="showAddDialog = true">
        <el-icon><CirclePlus /></el-icon>
        添加训练记录
      </el-button>
    </el-card>

    <el-card>
      <el-table :data="records" style="width: 100%">
        <el-table-column prop="record_date" label="日期" width="120" sortable />
        <el-table-column prop="exercise_name" label="运动项目" />
        <el-table-column prop="duration" label="时长(分钟)" width="120" />
        <el-table-column prop="calories" label="消耗卡路里" width="120" />
        <el-table-column prop="notes" label="备注" />
      </el-table>
    </el-card>

    <!-- 添加训练记录对话框 -->
    <el-dialog
      v-model="showAddDialog"
      title="添加训练记录"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="运动项目" prop="exercise_name">
          <el-select
            v-model="form.exercise_name"
            placeholder="请选择运动项目"
            style="width: 100%"
            filterable
            allow-create
          >
            <el-option label="跑步" value="跑步" />
            <el-option label="游泳" value="游泳" />
            <el-option label="骑行" value="骑行" />
            <el-option label="深蹲" value="深蹲" />
            <el-option label="卧推" value="卧推" />
            <el-option label="硬拉" value="硬拉" />
            <el-option label="瑜伽" value="瑜伽" />
            <el-option label="普拉提" value="普拉提" />
            <el-option label="开合跳" value="开合跳" />
            <el-option label="波比跳" value="波比跳" />
          </el-select>
        </el-form-item>

        <el-form-item label="训练时长" prop="duration">
          <el-input-number
            v-model="form.duration"
            :min="1"
            :max="300"
            style="width: 100%"
          />
          <span style="margin-left: 10px">分钟</span>
        </el-form-item>

        <el-form-item label="消耗卡路里" prop="calories">
          <el-input-number
            v-model="form.calories"
            :min="0"
            :max="5000"
            style="width: 100%"
          />
          <span style="margin-left: 10px">千卡</span>
        </el-form-item>

        <el-form-item label="训练日期" prop="record_date">
          <el-date-picker
            v-model="form.record_date"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="form.notes"
            type="textarea"
            :rows="3"
            placeholder="记录训练感受..."
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
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { addTrainingRecord, getTrainingRecords } from '@/api'

const records = ref([])
const showAddDialog = ref(false)
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  exercise_name: '',
  duration: 30,
  calories: 200,
  record_date: new Date().toISOString().split('T')[0],
  notes: ''
})

const rules = {
  exercise_name: [
    { required: true, message: '请选择运动项目', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入训练时长', trigger: 'blur' }
  ],
  calories: [
    { required: true, message: '请输入消耗卡路里', trigger: 'blur' }
  ],
  record_date: [
    { required: true, message: '请选择训练日期', trigger: 'change' }
  ]
}

const loadRecords = async () => {
  try {
    const res = await getTrainingRecords()
    records.value = res
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    await addTrainingRecord(form)
    ElMessage.success('添加成功')
    showAddDialog.value = false

    // 重置表单
    form.exercise_name = ''
    form.duration = 30
    form.calories = 200
    form.record_date = new Date().toISOString().split('T')[0]
    form.notes = ''

    loadRecords()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.training {
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

:deep(.el-card) {
  border-radius: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

:deep(.el-card:hover) {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
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

:deep(.el-table th) {
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  font-weight: 600;
}

:deep(.el-table__row) {
  transition: all 0.3s ease;
}

:deep(.el-table__row:hover) {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  transform: scale(1.01);
}

:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}
</style>
