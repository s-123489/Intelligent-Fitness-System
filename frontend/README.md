# 智能健身系统 - 前端

## 技术栈
- Vue 3
- Vite
- Element Plus
- ECharts
- Pinia
- Vue Router
- Axios

## 安装依赖

```bash
npm install
```

## 运行

```bash
npm run dev
```

应用将在 http://localhost:3000 运行

## 构建

```bash
npm run build
```

## 功能模块

### 1. 用户认证
- 用户注册
- 用户登录
- 个人资料管理

### 2. 仪表盘
- 训练数据统计
- 数据可视化图表
- 快速操作入口
- 最近训练记录

### 3. 健身计划
- AI智能生成健身计划
- 查看计划详情
- 多种健身目标支持

### 4. 训练记录
- 添加训练记录
- 查看历史记录
- 数据统计分析

### 5. 身体数据
- 记录体重、体脂率
- 体重变化趋势图
- BMI自动计算

### 6. 营养建议
- 个性化热量推荐
- 营养素配比
- 饮水建议
- 推荐食谱

### 7. 社区动态
- 分享训练成果
- 互动点赞
- 查看其他用户动态

## 目录结构

```
frontend/
├── src/
│   ├── api/           # API接口
│   ├── components/    # 公共组件
│   ├── router/        # 路由配置
│   ├── stores/        # 状态管理
│   ├── utils/         # 工具函数
│   ├── views/         # 页面组件
│   ├── App.vue        # 根组件
│   └── main.js        # 入口文件
├── index.html
├── package.json
└── vite.config.js
```
