# 智能健身系统 - 后端

## 技术栈
- Node.js 14+
- Express.js
- SQLite (better-sqlite3)
- JWT认证

## 安装依赖

```bash
npm install
```

## 运行

```bash
npm start
```

或者使用开发模式（自动重启）：

```bash
npm run dev
```

服务器将在 http://localhost:5000 运行

## API接口

### 用户认证
- POST /api/register - 用户注册
- POST /api/login - 用户登录
- GET /api/user/profile - 获取用户信息
- PUT /api/user/profile - 更新用户信息

### 身体数据
- POST /api/body-data - 添加身体数据
- GET /api/body-data - 获取身体数据历史

### 健身计划
- POST /api/fitness-plan/generate - AI生成健身计划
- GET /api/fitness-plans - 获取健身计划列表
- GET /api/fitness-plans/:id - 获取计划详情

### 训练记录
- POST /api/training-records - 添加训练记录
- GET /api/training-records - 获取训练记录
- GET /api/training-stats - 获取训练统计

### 营养建议
- GET /api/nutrition/recommendation - 获取营养建议

### 社区
- GET /api/community/posts - 获取社区动态
- POST /api/community/posts - 发布动态
- POST /api/community/posts/:id/like - 点赞/取消点赞
- POST /api/community/posts/:id/comments - 添加评论
- GET /api/community/posts/:id/comments - 获取评论列表
