<template>
  <div class="training">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Trophy /></el-icon>
        训练记录
      </h2>
      <el-button type="primary" @click="showAddDialog = true" class="add-btn">
        <el-icon><CirclePlus /></el-icon>
        添加训练记录
      </el-button>
    </div>

    <el-card class="records-card">
      <el-table :data="records" style="width: 100%" class="modern-table">
        <el-table-column prop="record_date" label="日期" width="140" sortable>
          <template #default="{ row }">
            <div class="date-cell">
              <el-icon color="#409eff"><Calendar /></el-icon>
              <span>{{ row.record_date }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="exercise_name" label="运动项目">
          <template #default="{ row }">
            <el-tag type="success" effect="light" round>{{ row.exercise_name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="120">
          <template #default="{ row }">
            <div class="metric-cell">
              <el-icon color="#67c23a"><Timer /></el-icon>
              <span>{{ row.duration }}分钟</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="calories" label="消耗卡路里" width="140">
          <template #default="{ row }">
            <div class="metric-cell">
              <el-icon color="#e6a23c"><Sunny /></el-icon>
              <span>{{ row.calories }}千卡</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
              link
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
              link
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑训练记录对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEditing ? '编辑训练记录' : '添加训练记录'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="运动项目" prop="exercise_name">
          <el-select
            v-model="form.exercise_name"
            placeholder="可选择或输入自定义运动项目"
            style="width: 100%"
            filterable
            allow-create
            default-first-option
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
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            💡 提示：可以直接输入自定义的运动项目名称
          </div>
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
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addTrainingRecord, getTrainingRecords, updateTrainingRecord, deleteTrainingRecord } from '@/api'

const records = ref([])
const showAddDialog = ref(false)
const loading = ref(false)
const formRef = ref(null)
const isEditing = ref(false)
const editingId = ref(null)

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

const resetForm = () => {
  form.exercise_name = ''
  form.duration = 30
  form.calories = 200
  form.record_date = new Date().toISOString().split('T')[0]
  form.notes = ''
  isEditing.value = false
  editingId.value = null
}

const handleCancel = () => {
  showAddDialog.value = false
  resetForm()
}

const handleEdit = (row) => {
  isEditing.value = true
  editingId.value = row.id
  form.exercise_name = row.exercise_name
  form.duration = row.duration
  form.calories = row.calories
  form.record_date = row.record_date
  form.notes = row.notes || ''
  showAddDialog.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条训练记录吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await deleteTrainingRecord(row.id)
    ElMessage.success('删除成功')
    loadRecords()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (isEditing.value) {
      await updateTrainingRecord(editingId.value, form)
      ElMessage.success('更新成功')
    } else {
      await addTrainingRecord(form)
      ElMessage.success('添加成功')
    }

    showAddDialog.value = false
    resetForm()
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
  animation: fadeIn 0.6s ease-in;
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.25);
}

.page-title {
  margin: 0;
  color: white;
  font-size: 28px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title .el-icon {
  font-size: 32px;
}

.add-btn {
  background: white !important;
  color: #667eea !important;
  border: none !important;
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.add-btn:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.records-card {
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: none;
}

.date-cell,
.metric-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.modern-table :deep(.el-table__header) {
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
}

.modern-table :deep(.el-table__header th) {
  background: transparent;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.modern-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.modern-table :deep(.el-table__row:hover) {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  transform: translateX(4px);
}

:deep(.el-card) {
  border-radius: 16px;
  transition: all 0.3s ease;
  border: none;
}
</style>
