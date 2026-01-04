<template>
  <div class="community">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><ChatDotRound /></el-icon>
        健身社区
      </h2>
      <el-button type="primary" @click="showPostDialog = true" class="add-btn">
        <el-icon><EditPen /></el-icon>
        发布动态
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="24" v-for="post in posts" :key="post.id">
        <el-card class="post-card" style="margin-bottom: 20px">
          <template #header>
            <div class="post-header">
              <div class="user-info">
                <div class="avatar-wrapper">
                  <el-avatar :size="48" class="user-avatar">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <div class="online-indicator"></div>
                </div>
                <div class="user-detail">
                  <div class="username">{{ post.username }}</div>
                  <div class="post-time">
                    <el-icon><Clock /></el-icon>
                    {{ formatTime(post.created_at) }}
                  </div>
                </div>
              </div>
            </div>
          </template>

          <div class="post-content">
            <p>{{ post.content }}</p>
            <el-image
              v-if="post.image_url"
              :src="post.image_url"
              fit="cover"
              style="max-width: 100%; border-radius: 8px; margin-top: 10px"
            />
          </div>

          <div class="post-actions">
            <el-button
              text
              @click="handleLike(post)"
              :type="post.is_liked ? 'warning' : ''"
            >
              <el-icon v-if="post.is_liked"><StarFilled /></el-icon>
              <el-icon v-else><Star /></el-icon>
              {{ post.is_liked ? '已赞' : '点赞' }} ({{ post.likes }})
            </el-button>
            <el-button text @click="showComments(post)">
              <el-icon><ChatDotRound /></el-icon>
              评论 ({{ post.comment_count || 0 }})
            </el-button>
            <el-button text>
              <el-icon><Share /></el-icon>
              分享
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 发布动态对话框 -->
    <el-dialog
      v-model="showPostDialog"
      title="发布训练动态"
      width="600px"
    >
      <el-form :model="postForm" ref="formRef" label-width="0">
        <el-form-item>
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="6"
            placeholder="分享你的训练心得、成果或感受..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="postForm.image_url"
            placeholder="图片URL（可选）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showPostDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePost" :loading="posting">
          发布
        </el-button>
      </template>
    </el-dialog>

    <!-- 评论对话框 -->
    <el-dialog
      v-model="showCommentDialog"
      :title="`${currentPost.username}的动态`"
      width="700px"
    >
      <!-- 动态内容 -->
      <div class="comment-post-content">
        <p>{{ currentPost.content }}</p>
      </div>

      <el-divider>评论列表</el-divider>

      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-if="comments.length === 0" class="no-comments">
          <el-empty description="暂无评论，快来抢沙发吧！" :image-size="100" />
        </div>
        <div v-else>
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <el-avatar :size="30">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="comment-info">
                <div class="comment-username">{{ comment.username }}</div>
                <div class="comment-time">{{ formatTime(comment.created_at) }}</div>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </div>
        </div>
      </div>

      <!-- 添加评论 -->
      <div class="add-comment">
        <el-input
          v-model="commentContent"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="200"
          show-word-limit
        />
        <el-button
          type="primary"
          @click="handleAddComment"
          :loading="commenting"
          style="margin-top: 10px"
        >
          发表评论
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getCommunityPosts, createPost, toggleLike, addComment, getComments } from '@/api'

const posts = ref([])
const showPostDialog = ref(false)
const showCommentDialog = ref(false)
const posting = ref(false)
const commenting = ref(false)
const formRef = ref(null)
const currentPost = ref({})
const comments = ref([])
const commentContent = ref('')

const postForm = reactive({
  content: '',
  image_url: ''
})

const loadPosts = async () => {
  try {
    const res = await getCommunityPosts()
    posts.value = res
  } catch (error) {
    console.error(error)
  }
}

const handlePost = async () => {
  if (!postForm.content.trim()) {
    ElMessage.warning('请输入动态内容')
    return
  }

  try {
    posting.value = true
    await createPost(postForm)
    ElMessage.success('发布成功')
    showPostDialog.value = false

    postForm.content = ''
    postForm.image_url = ''

    loadPosts()
  } catch (error) {
    console.error(error)
  } finally {
    posting.value = false
  }
}

const handleLike = async (post) => {
  try {
    const res = await toggleLike(post.id)
    post.is_liked = res.is_liked
    post.likes = res.likes
    ElMessage.success(res.message)
  } catch (error) {
    console.error(error)
  }
}

const showComments = async (post) => {
  currentPost.value = post
  showCommentDialog.value = true
  await loadComments(post.id)
}

const loadComments = async (postId) => {
  try {
    const res = await getComments(postId)
    comments.value = res
  } catch (error) {
    console.error(error)
  }
}

const handleAddComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    commenting.value = true
    await addComment(currentPost.value.id, { content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''

    // 重新加载评论和动态列表
    await loadComments(currentPost.value.id)
    await loadPosts()
  } catch (error) {
    console.error(error)
  } finally {
    commenting.value = false
  }
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''

  try {
    // 处理数据库返回的时间格式
    const date = new Date(timeStr.replace(' ', 'T'))
    const now = new Date()
    const diff = now - date

    const minutes = Math.floor(diff / 60000)
    const hours = Math.floor(diff / 3600000)
    const days = Math.floor(diff / 86400000)

    if (minutes < 1) return '刚刚'
    if (minutes < 60) return `${minutes}分钟前`
    if (hours < 24) return `${hours}小时前`
    if (days < 7) return `${days}天前`

    // 格式化为 YYYY-MM-DD HH:mm
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')

    return `${year}-${month}-${day} ${hour}:${minute}`
  } catch (error) {
    return timeStr
  }
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.community {
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

.post-card {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: relative;
}

.post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  transform: scaleY(0);
  transform-origin: top;
  transition: transform 0.3s ease;
}

.post-card:hover::before {
  transform: scaleY(1);
}

.post-card:hover {
  box-shadow: 0 8px 28px rgba(102, 126, 234, 0.2);
  transform: translateY(-4px);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  position: relative;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #67c23a;
  border: 2px solid white;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.username {
  font-weight: 700;
  color: #303133;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.post-time {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-content {
  padding: 16px 0;
}

.post-content p {
  margin: 0;
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
  font-size: 15px;
}

.post-actions {
  display: flex;
  gap: 20px;
  padding-top: 16px;
  border-top: 2px solid #f5f7fa;
}

.post-actions :deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.post-actions :deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.comment-post-content {
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 12px;
  margin-bottom: 20px;
  border-left: 4px solid #667eea;
}

.comment-post-content p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.comments-list {
  max-height: 400px;
  overflow-y: auto;
  margin-bottom: 20px;
}

.no-comments {
  text-align: center;
  padding: 30px;
}

.comment-item {
  padding: 16px;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #fafafa 0%, #ffffff 100%);
  border-radius: 12px;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.comment-item:hover {
  background: linear-gradient(135deg, #f0f0f0 0%, #fafafa 100%);
  border-left-color: #667eea;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-info {
  flex: 1;
}

.comment-username {
  font-weight: 700;
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  padding-left: 42px;
  line-height: 1.6;
  color: #606266;
}

.add-comment {
  padding-top: 20px;
  border-top: 2px solid #f5f7fa;
}

:deep(.el-card) {
  border-radius: 16px;
  border: none;
}

:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: 700;
  font-size: 18px;
}
</style>
