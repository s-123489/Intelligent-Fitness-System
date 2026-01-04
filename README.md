# 智能健身管理系统

一个基于 Spring Boot + Vue 3 的全栈健身管理平台，提供个性化健身计划、训练记录、身体数据分析、营养建议和社区互动等功能。

## 项目简介

本系统旨在帮助健身爱好者科学管理训练计划、追踪身体数据变化、获取个性化营养建议，并通过社区功能与其他用户分享健身经验。

### 核心功能

- **用户认证系统**：基于 JWT 的安全认证机制
- **健身计划管理**：创建和管理个性化健身计划
- **训练记录追踪**：记录每日训练数据，统计运动成果
- **身体数据管理**：追踪体重、体脂率、BMI 等关键指标
- **营养建议**：根据用户数据提供个性化饮食建议
- **社区互动**：发布动态、评论、点赞，分享健身心得
- **数据可视化**：使用 ECharts 展示训练趋势和身体数据变化

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 1.8+ | 编程语言 |
| Spring Boot | 2.7.18 | 应用框架 |
| Spring Security | - | 安全框架 |
| Spring Data JPA | - | 数据持久化 |
| JWT | 0.11.5 | 认证令牌 |
| H2 Database | - | 开发环境数据库 |
| MySQL | - | 生产环境数据库 |
| Lombok | - | 简化 Java 代码 |
| Maven | - | 项目构建工具 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4+ | 前端框架 |
| Vite | 5.0+ | 构建工具 |
| Element Plus | 2.5+ | UI 组件库 |
| ECharts | 5.4+ | 数据可视化 |
| Pinia | 2.1+ | 状态管理 |
| Vue Router | 4.2+ | 路由管理 |
| Axios | 1.6+ | HTTP 客户端 |

## 项目结构

```
大作业/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fitness/
│   │   │   │   ├── config/           # 配置类（安全配置等）
│   │   │   │   ├── controller/       # REST API 控制器
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── UserController.java
│   │   │   │   │   ├── BodyDataController.java
│   │   │   │   │   ├── FitnessPlanController.java
│   │   │   │   │   ├── TrainingRecordController.java
│   │   │   │   │   ├── NutritionController.java
│   │   │   │   │   ├── CommunityController.java
│   │   │   │   │   └── TrainingStatsController.java
│   │   │   │   ├── entity/           # 实体类
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── BodyData.java
│   │   │   │   │   ├── FitnessPlan.java
│   │   │   │   │   ├── TrainingRecord.java
│   │   │   │   │   ├── CommunityPost.java
│   │   │   │   │   ├── PostComment.java
│   │   │   │   │   └── PostLike.java
│   │   │   │   ├── repository/       # 数据访问层
│   │   │   │   ├── service/          # 业务逻辑层
│   │   │   │   ├── security/         # 安全相关（JWT 过滤器等）
│   │   │   │   ├── dto/              # 数据传输对象
│   │   │   │   └── util/             # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml           # 开发环境配置
│   │   │       └── application-prod.yml      # 生产环境配置
│   │   └── test/                    # 测试代码
│   ├── pom.xml                      # Maven 配置
│   └── data/                        # H2 数据库文件目录
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口封装
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia 状态管理
│   │   ├── views/             # 页面组件
│   │   │   ├── Login.vue          # 登录页
│   │   │   ├── Register.vue       # 注册页
│   │   │   ├── Home.vue           # 首页
│   │   │   ├── Dashboard.vue      # 数据仪表盘
│   │   │   ├── Profile.vue        # 个人资料
│   │   │   ├── BodyData.vue       # 身体数据管理
│   │   │   ├── Plans.vue          # 健身计划
│   │   │   ├── Training.vue       # 训练记录
│   │   │   ├── Nutrition.vue      # 营养建议
│   │   │   └── Community.vue      # 社区动态
│   │   ├── App.vue            # 根组件
│   │   └── main.js            # 入口文件
│   ├── package.json           # 项目依赖
│   └── vite.config.js         # Vite 配置
│
├── deploy/                    # 部署脚本和配置
│   ├── build.sh              # 构建脚本
│   ├── install.sh            # 安装脚本
│   ├── nginx.conf            # Nginx 配置
│   └── fitness-backend.service  # Systemd 服务配置
│
├── start-backend.bat          # Windows 后端启动脚本
├── start-frontend.bat         # Windows 前端启动脚本
└── README.md                  # 项目说明文档
```

## 快速开始

### 环境要求

- **Java**: JDK 1.8 或更高版本
- **Maven**: 3.6+
- **Node.js**: 16.0+
- **npm**: 8.0+ 或 yarn

### 后端启动

```bash
# 1. 进入后端目录
cd backend

# 2. 安装依赖（首次运行）
mvn clean install

# 3. 启动后端服务
mvn spring-boot:run

# 或者在 Windows 上直接运行
start-backend.bat
```

后端服务默认运行在 `http://localhost:5000/api`

**H2 数据库控制台**：访问 `http://localhost:5000/api/h2-console`
- JDBC URL: `jdbc:h2:file:./data/fitnessdb`
- 用户名: `sa`
- 密码: (留空)

### 前端启动

```bash
# 1. 进入前端目录
cd frontend

# 2. 安装依赖（首次运行）
npm install

# 3. 启动开发服务器
npm run dev

# 或者在 Windows 上直接运行
start-frontend.bat
```

前端应用默认运行在 `http://localhost:3000`

### 生产环境构建

```bash
# 后端打包
cd backend
mvn clean package

# 前端打包
cd frontend
npm run build
```

## API 接口文档

### 认证接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 否 |
| POST | `/api/auth/login` | 用户登录 | 否 |

### 用户管理

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/users/me` | 获取当前用户信息 | 是 |
| PUT | `/api/users/me` | 更新用户信息 | 是 |

### 身体数据

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/body-data` | 获取身体数据列表 | 是 |
| POST | `/api/body-data` | 添加身体数据 | 是 |
| DELETE | `/api/body-data/{id}` | 删除身体数据 | 是 |

### 健身计划

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/plans` | 获取健身计划列表 | 是 |
| POST | `/api/plans` | 创建健身计划 | 是 |
| GET | `/api/plans/{id}` | 获取计划详情 | 是 |
| PUT | `/api/plans/{id}` | 更新健身计划 | 是 |
| DELETE | `/api/plans/{id}` | 删除健身计划 | 是 |

### 训练记录

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/training-records` | 获取训练记录 | 是 |
| POST | `/api/training-records` | 添加训练记录 | 是 |
| DELETE | `/api/training-records/{id}` | 删除训练记录 | 是 |
| GET | `/api/training-stats` | 获取训练统计数据 | 是 |

### 营养建议

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/nutrition/recommendations` | 获取营养建议 | 是 |

### 社区动态

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/community/posts` | 获取动态列表 | 是 |
| POST | `/api/community/posts` | 发布动态 | 是 |
| DELETE | `/api/community/posts/{id}` | 删除动态 | 是 |
| POST | `/api/community/posts/{id}/like` | 点赞动态 | 是 |
| GET | `/api/community/posts/{id}/comments` | 获取评论 | 是 |
| POST | `/api/community/posts/{id}/comments` | 添加评论 | 是 |

## 数据库设计

### 核心表结构

#### 用户表 (User)
- `id`: 主键
- `username`: 用户名（唯一）
- `password`: 加密密码（BCrypt）
- `email`: 邮箱
- `gender`: 性别
- `age`: 年龄
- `height`: 身高（cm）
- `created_at`: 注册时间

#### 身体数据表 (BodyData)
- `id`: 主键
- `user_id`: 用户ID（外键）
- `weight`: 体重（kg）
- `body_fat`: 体脂率（%）
- `bmi`: BMI指数
- `record_date`: 记录日期

#### 健身计划表 (FitnessPlan)
- `id`: 主键
- `user_id`: 用户ID（外键）
- `plan_name`: 计划名称
- `goal`: 健身目标
- `difficulty`: 难度级别
- `duration`: 训练周期
- `description`: 计划描述
- `plan_content`: 计划内容（JSON）
- `created_at`: 创建时间

#### 训练记录表 (TrainingRecord)
- `id`: 主键
- `user_id`: 用户ID（外键）
- `plan_id`: 计划ID（外键，可空）
- `exercise_name`: 运动名称
- `duration`: 训练时长（分钟）
- `calories`: 消耗卡路里
- `notes`: 备注
- `record_date`: 训练日期

#### 社区动态表 (CommunityPost)
- `id`: 主键
- `user_id`: 用户ID（外键）
- `content`: 动态内容
- `image_url`: 图片URL（可空）
- `created_at`: 发布时间

#### 动态评论表 (PostComment)
- `id`: 主键
- `post_id`: 动态ID（外键）
- `user_id`: 用户ID（外键）
- `content`: 评论内容
- `created_at`: 评论时间

#### 动态点赞表 (PostLike)
- `id`: 主键
- `post_id`: 动态ID（外键）
- `user_id`: 用户ID（外键）
- `created_at`: 点赞时间

## 核心功能说明

### 1. JWT 认证机制

系统使用 JWT (JSON Web Token) 进行身份认证：
- 用户登录成功后，后端生成 JWT 令牌
- 前端将令牌存储在 localStorage
- 每次请求在 Authorization Header 中携带令牌
- 后端通过 JwtAuthenticationFilter 验证令牌有效性
- 令牌有效期 7 天，过期后需重新登录

### 2. 密码安全

- 使用 Spring Security 的 BCryptPasswordEncoder 加密密码
- 密码采用 BCrypt 哈希算法，不可逆加密
- 每个密码都有唯一的盐值，防止彩虹表攻击

### 3. 数据可视化

前端使用 ECharts 实现数据可视化：
- **身体数据趋势图**：折线图展示体重、体脂率变化
- **训练统计图**：柱状图展示每日训练时长和卡路里消耗
- **营养配比图**：饼图展示蛋白质、碳水、脂肪占比

### 4. 营养建议算法

基于 Harris-Benedict 公式计算基础代谢率（BMR）：
- **男性**：BMR = 88.362 + (13.397 × 体重kg) + (4.799 × 身高cm) - (5.677 × 年龄)
- **女性**：BMR = 447.593 + (9.247 × 体重kg) + (3.098 × 身高cm) - (4.330 × 年龄)
- **每日总消耗**：TDEE = BMR × 活动系数（1.2 - 1.9）

根据健身目标调整热量和营养素配比：
- **减脂**：TDEE - 300~500 卡路里，高蛋白低碳水
- **增肌**：TDEE + 300~500 卡路里，高蛋白高碳水
- **维持**：TDEE，均衡营养

## 部署说明

### 使用 Docker 部署（推荐）

详见 `deploy/DEPLOYMENT.md` 文档

### 传统部署

#### 后端部署

```bash
# 1. 打包
cd backend
mvn clean package -DskipTests

# 2. 配置生产环境数据库（MySQL）
# 编辑 src/main/resources/application-prod.yml

# 3. 运行
java -jar target/fitness-system-1.0.0.jar --spring.profiles.active=prod
```

#### 前端部署

```bash
# 1. 构建
cd frontend
npm run build

# 2. 将 dist 目录部署到 Nginx
# 参考 deploy/nginx.conf 配置
```

### Nginx 反向代理配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端
    location / {
        root /var/www/fitness/frontend;
        try_files $uri $uri/ /index.html;
    }

    # 后端API
    location /api/ {
        proxy_pass http://localhost:5000/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 开发指南

### 后端开发

```bash
# 运行测试
mvn test

# 代码检查
mvn checkstyle:check

# 热重载（Spring Boot DevTools）
# 修改代码后自动重启
```

### 前端开发

```bash
# 开发服务器（热重载）
npm run dev

# 代码格式化
npm run format

# 生产构建
npm run build
```

## 常见问题

### 1. 后端启动失败

**问题**：端口 5000 被占用
**解决**：修改 `application.yml` 中的 `server.port`

**问题**：数据库连接失败
**解决**：检查数据库配置，确保 H2 或 MySQL 正常运行

### 2. 前端启动失败

**问题**：依赖安装失败
**解决**：删除 `node_modules` 和 `package-lock.json`，重新运行 `npm install`

**问题**：跨域错误
**解决**：确保后端 CORS 配置正确，或使用 Vite 代理配置

### 3. JWT 令牌失效

**问题**：登录后立即提示未授权
**解决**：
- 检查系统时间是否正确
- 清除浏览器 localStorage 重新登录
- 确认后端 JWT 密钥配置一致

## 技术亮点

1. **前后端分离架构**：前端 Vue 3 + 后端 Spring Boot，职责清晰
2. **RESTful API 设计**：符合 REST 规范，接口语义化
3. **JWT 无状态认证**：提高系统可扩展性
4. **JPA 数据持久化**：简化数据库操作，支持多种数据库
5. **响应式设计**：前端自适应不同屏幕尺寸
6. **数据可视化**：ECharts 图表直观展示训练成果
7. **组件化开发**：Vue 组件复用性强，易于维护

## 未来优化方向

- [ ] 集成第三方 AI 模型提供智能健身建议
- [ ] 支持移动端 APP（React Native / Flutter）
- [ ] 添加好友系统和私信功能
- [ ] 集成可穿戴设备数据同步
- [ ] 增加视频教学功能
- [ ] 实现多语言国际化
- [ ] 添加数据导出功能（PDF/Excel）
- [ ] 引入缓存机制（Redis）提升性能

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎提交 Issue 或 Pull Request。

---

**项目开发团队**
- 成员1: 商雨婷 - 后端开发、数据库设计
- 成员2: 郭盈盈 - 前端开发、UI/UX 设计
