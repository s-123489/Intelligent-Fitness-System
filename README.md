# 智能健身系统

一个基于Web的智能健身管理系统，结合AI技术为用户提供个性化的健身计划、训练记录、身体数据管理、营养建议等功能。

## 项目背景及意义

随着人们健康意识的提升，越来越多的人开始关注健身和健康管理。本系统通过智能化的方式，帮助用户：
- 科学制定健身计划
- 记录和追踪训练数据
- 监测身体数据变化
- 获取个性化营养建议
- 与其他健身爱好者交流

## 技术栈及架构

### 前端技术
- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **UI组件库**: Element Plus
- **数据可视化**: ECharts
- **状态管理**: Pinia
- **路由管理**: Vue Router
- **HTTP客户端**: Axios

### 后端技术
- **语言**: Java 17
- **框架**: Spring Boot 3.2.0
- **安全**: Spring Security
- **数据持久化**: Spring Data JPA
- **数据库**: H2 Database (可切换MySQL)
- **认证**: JWT (jjwt 0.12.3)
- **密码加密**: BCrypt
- **项目管理**: Maven

### 数据库设计

#### 用户表 (users)
- id: 主键 (BIGINT)
- username: 用户名 (UNIQUE)
- password: 密码（BCrypt加密）
- email: 邮箱
- gender: 性别
- age: 年龄
- height: 身高(cm)
- created_at: 创建时间 (自动生成)

#### 身体数据表 (body_data)
- id: 主键
- user_id: 用户ID（外键）
- weight: 体重
- body_fat: 体脂率
- bmi: BMI指数
- record_date: 记录日期

#### 健身计划表 (fitness_plans)
- id: 主键
- user_id: 用户ID（外键）
- plan_name: 计划名称
- goal: 健身目标
- difficulty: 难度
- duration: 周期
- description: 描述
- plan_content: 计划内容（JSON）
- is_ai_generated: 是否AI生成
- created_at: 创建时间

#### 训练记录表 (training_records)
- id: 主键
- user_id: 用户ID（外键）
- plan_id: 计划ID（外键）
- exercise_name: 运动名称
- duration: 时长
- calories: 消耗卡路里
- notes: 备注
- record_date: 记录日期

#### 营养记录表 (nutrition_records)
- id: 主键
- user_id: 用户ID（外键）
- meal_type: 餐型
- food_name: 食物名称
- calories: 卡路里
- protein: 蛋白质
- carbs: 碳水化合物
- fat: 脂肪
- record_date: 记录日期

#### 社区动态表 (community_posts)
- id: 主键
- user_id: 用户ID（外键）
- content: 内容
- image_url: 图片URL
- likes: 点赞数
- created_at: 创建时间

## 核心功能模块

### 1. 用户认证系统
- 用户注册（密码加密存储）
- 用户登录（JWT令牌认证）
- 个人资料管理

### 2. AI智能健身计划
**关键技术**: 基于用户身体数据的AI推荐算法

根据用户的：
- 年龄、性别、身高、体重
- 健身目标（减脂、增肌、塑形、保持）
- 当前体脂率和BMI

智能生成个性化健身计划，包括：
- 运动项目推荐
- 训练强度和频率
- 预期效果

**解决方案**: 使用规则引擎结合用户数据，为不同目标匹配最优训练方案。

### 3. 训练记录与打卡
- 记录每日训练数据
- 运动项目、时长、消耗卡路里
- 训练历史查看
- 连续打卡统计

### 4. 身体数据管理与可视化
**关键技术**: ECharts数据可视化

功能：
- 体重、体脂率、BMI记录
- 数据变化趋势图
- 历史数据对比

**解决方案**: 使用ECharts绘制折线图，直观展示身体数据变化趋势，帮助用户了解训练效果。

### 5. 个性化营养建议
**关键技术**: 基础代谢率(BMR)计算与营养素配比

根据用户数据计算：
- 每日推荐热量摄入
- 蛋白质、碳水化合物、脂肪配比
- 每日饮水量建议
- 推荐食谱

**计算公式**:
- 男性BMR = 88.362 + (13.397 × 体重) + (4.799 × 身高) - (5.677 × 年龄)
- 女性BMR = 447.593 + (9.247 × 体重) + (3.098 × 身高) - (4.330 × 年龄)
- 总热量消耗 = BMR × 活动系数(1.5)

**解决方案**: 根据不同健身目标调整热量和营养素比例。

### 6. 社区互动
- 分享训练成果和心得
- 查看其他用户动态
- 点赞互动功能

### 7. 数据统计与仪表盘
**关键技术**: 实时数据统计与图表展示

统计指标：
- 总训练次数
- 总训练时长
- 总消耗卡路里
- 连续打卡天数
- 每日训练数据趋势图

**解决方案**: 使用ECharts柱状图和折线图组合，展示训练时长和卡路里消耗的双维度数据。

## 关键技术及解决方案

### 1. JWT身份认证
**技术难点**: 前后端分离架构下的用户认证

**解决方案**:
- 使用jjwt库生成和验证JWT令牌
- Spring Security Filter拦截请求验证token
- 前端在Authorization请求头中携带Bearer Token
- Token有效期7天，过期自动跳转登录
- 使用HMAC-SHA256签名算法保证安全性

### 2. AI健身计划生成算法
**技术难点**: 如何根据用户数据生成科学的健身计划

**解决方案**:
- 构建训练知识库（不同目标的运动项目库）
- 根据用户BMI、体脂率判断身体状态
- 匹配合适的训练强度和频率
- 动态调整训练周期

### 3. 数据可视化
**技术难点**: 大量数据的直观展示

**解决方案**:
- 使用ECharts实现多种图表
- 折线图展示趋势变化
- 柱状图对比每日数据
- 饼图展示营养素配比
- 响应式图表自适应屏幕

### 4. 跨域问题
**技术难点**: 前后端分离导致的跨域请求

**解决方案**:
- Spring Security中配置CORS策略
- 允许特定源（http://localhost:5173）的跨域请求
- 支持GET、POST、PUT、DELETE等HTTP方法
- 允许携带认证信息（credentials）
- 生产环境使用Nginx反向代理

### 5. 密码安全
**技术难点**: 用户密码的安全存储

**解决方案**:
- 使用BCrypt密码加密（比SHA256更安全）
- 自动加盐（salt）防止彩虹表攻击
- 密码不可逆加密存储
- 登录时使用BCrypt的matches方法验证
- 符合企业级安全标准

## 项目结构

```
大作业/
├── backend/                                 # Java后端
│   ├── src/main/java/com/fitness/
│   │   ├── FitnessApplication.java         # 主应用类
│   │   ├── config/                         # 配置类
│   │   │   └── SecurityConfig.java         # Spring Security配置
│   │   ├── controller/                     # 控制器层
│   │   │   ├── AuthController.java         # 认证API
│   │   │   ├── UserController.java         # 用户API
│   │   │   └── BodyDataController.java     # 身体数据API
│   │   ├── dto/                           # 数据传输对象
│   │   ├── entity/                        # JPA实体类
│   │   ├── repository/                    # 数据访问层
│   │   ├── service/                       # 业务逻辑层
│   │   ├── security/                      # 安全认证
│   │   └── util/                          # 工具类
│   ├── src/main/resources/
│   │   └── application.yml                # 配置文件
│   ├── pom.xml                            # Maven依赖
│   ├── mvnw & mvnw.cmd                    # Maven Wrapper
│   └── README.md                          # 后端详细说明
├── frontend/                              # Vue前端
│   ├── src/
│   │   ├── api/                          # API接口
│   │   ├── router/                       # 路由配置
│   │   ├── stores/                       # Pinia状态管理
│   │   ├── utils/                        # 工具函数
│   │   ├── views/                        # 页面组件
│   │   ├── App.vue                       # 根组件
│   │   └── main.js                       # 入口文件
│   ├── package.json                      # NPM依赖
│   └── vite.config.js                    # Vite配置
└── README.md                             # 项目总说明
```

## 快速开始

### 环境要求

**后端**:
- JDK 17 或更高版本
- Maven 3.6+ (或使用项目自带的Maven Wrapper)

**前端**:
- Node.js 14+
- npm 或 yarn

### 后端启动

```bash
# 进入后端目录
cd backend

# 使用Maven Wrapper运行（推荐，无需安装Maven）
# Windows:
mvnw.cmd spring-boot:run

# Linux/Mac:
./mvnw spring-boot:run

# 或者先打包再运行
mvnw.cmd clean package
java -jar target/fitness-system-1.0.0.jar
```

**启动成功后显示**:
```
========================================
智能健身管理系统启动成功！
API地址: http://localhost:5000/api
H2控制台: http://localhost:5000/api/h2-console
========================================
```

### 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 运行开发服务器
npm run dev
```

前端应用将在 http://localhost:5173 运行

## 使用说明

1. **注册账户**: 访问系统，点击"立即注册"创建账户
2. **完善资料**: 登录后进入"个人资料"完善性别、年龄、身高等信息
3. **记录身体数据**: 在"身体数据"模块记录当前体重和体脂率
4. **生成健身计划**: 在"健身计划"模块选择目标，使用AI生成个性化计划
5. **记录训练**: 在"训练记录"模块添加每日训练数据
6. **查看统计**: 在"仪表盘"查看训练统计和数据趋势
7. **营养建议**: 在"营养建议"模块查看个性化饮食建议
8. **社区互动**: 在"社区动态"分享训练成果

## 开发团队及分工

本项目由[团队成员]共同完成，具体分工如下：

### 成员1: [姓名]
- 后端开发（50%）
  - Spring Boot框架搭建
  - 数据库设计与JPA实体类实现
  - RESTful API接口开发
  - Spring Security + JWT认证实现
  - BCrypt密码加密
- 前端开发（20%）
  - 用户认证页面
  - 个人资料页面
- 测试与调试（15%）
- 文档编写（15%）

### 成员2: [姓名]
- 前端开发（60%）
  - Vue3项目搭建
  - 页面组件开发
  - Pinia状态管理
  - ECharts图表集成
  - 响应式布局和UI优化
- AI算法设计（20%）
  - 健身计划推荐算法
  - 营养建议算法
- 测试与调试（10%）
- 文档编写（10%）

## GitHub仓库

[待填写GitHub地址]

## 演示截图

[待添加系统截图]

## 未来优化方向

1. 增加更多运动项目和计划模板
2. 集成真实的AI模型（如使用机器学习）
3. 添加移动端适配
4. 实现社区评论功能
5. 添加好友系统和私信功能
6. 集成可穿戴设备数据
7. 增加视频教学功能

## 许可证

MIT License

## 联系方式

[待填写联系方式]
