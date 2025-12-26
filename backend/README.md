# 智能健身管理系统 - Java后端

基于Spring Boot 3 + Spring Security + JWT的智能健身管理系统后端。

## 技术栈

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据持久化
- **JWT (jjwt 0.12.3)** - Token认证
- **H2 Database** - 内存数据库（可切换MySQL）
- **Lombok** - 简化代码
- **Maven** - 项目管理

## 项目结构

```
backend-java/
├── src/main/java/com/fitness/
│   ├── FitnessApplication.java      # 主应用类
│   ├── config/                      # 配置类
│   │   └── SecurityConfig.java      # Security配置
│   ├── controller/                  # 控制器层
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   └── BodyDataController.java
│   ├── dto/                         # 数据传输对象
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   └── ApiResponse.java
│   ├── entity/                      # 实体类
│   │   ├── User.java
│   │   ├── BodyData.java
│   │   ├── FitnessPlan.java
│   │   ├── TrainingRecord.java
│   │   ├── CommunityPost.java
│   │   ├── PostLike.java
│   │   └── PostComment.java
│   ├── repository/                  # 数据访问层
│   │   ├── UserRepository.java
│   │   ├── BodyDataRepository.java
│   │   └── ...
│   ├── service/                     # 业务逻辑层
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   └── BodyDataService.java
│   ├── security/                    # 安全相关
│   │   └── JwtAuthenticationFilter.java
│   └── util/                        # 工具类
│       └── JwtUtil.java
└── src/main/resources/
    └── application.yml              # 配置文件
```

## 快速开始

### 前置要求

- JDK 17 或更高版本
- Maven 3.6+

### 安装运行

1. **克隆项目**
```bash
cd backend-java
```

2. **使用Maven构建**
```bash
mvn clean install
```

3. **运行应用**
```bash
mvn spring-boot:run
```

或者直接运行JAR包：
```bash
java -jar target/fitness-system-1.0.0.jar
```

4. **访问应用**
- API地址: http://localhost:5000/api
- H2控制台: http://localhost:5000/api/h2-console

### H2数据库配置

开发环境默认使用H2内存数据库：
- JDBC URL: `jdbc:h2:mem:fitnessdb`
- 用户名: `sa`
- 密码: (空)

### 切换到MySQL

修改 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fitness?useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

## API接口文档

### 认证接口

#### 用户注册
```http
POST /api/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com"
}
```

#### 用户登录
```http
POST /api/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

响应：
```json
{
  "message": "登录成功",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "testuser"
  }
}
```

### 用户接口

所有以下接口需要在请求头中携带Token：
```http
Authorization: Bearer <your-token>
```

#### 获取用户信息
```http
GET /api/user/profile
```

#### 更新用户信息
```http
PUT /api/user/profile
Content-Type: application/json

{
  "email": "new@example.com",
  "gender": "男",
  "age": 25,
  "height": 175.0
}
```

### 身体数据接口

#### 添加身体数据
```http
POST /api/body-data
Content-Type: application/json

{
  "weight": 70.5,
  "bodyFat": 18.5,
  "recordDate": "2025-12-26"
}
```

#### 获取身体数据历史
```http
GET /api/body-data
```

## 核心功能实现

### 1. JWT认证机制

**JwtUtil.java** - JWT生成和验证
```java
public String generateToken(Long userId, String username) {
    // 生成JWT Token，有效期7天
}

public boolean validateToken(String token) {
    // 验证Token是否有效
}
```

**JwtAuthenticationFilter.java** - 请求拦截器
- 从请求头获取Token
- 验证Token有效性
- 设置Spring Security上下文

### 2. 密码加密

使用BCrypt进行密码加密：
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 3. 数据验证

使用Jakarta Validation进行参数验证：
```java
@NotBlank(message = "用户名不能为空")
@Size(min = 3, max = 20, message = "用户名长度应为3-20个字符")
private String username;
```

### 4. CORS跨域配置

在SecurityConfig中配置CORS：
```java
configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
```

### 5. 异常处理

使用try-catch捕获异常并返回友好的错误信息：
```java
try {
    authService.register(request);
    return ResponseEntity.ok(ApiResponse.success("注册成功"));
} catch (Exception e) {
    return ResponseEntity.badRequest()
        .body(ApiResponse.error(e.getMessage()));
}
```

## 数据库表结构

### users - 用户表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(255) | 密码（BCrypt加密） |
| email | VARCHAR(100) | 邮箱 |
| gender | VARCHAR(10) | 性别 |
| age | INT | 年龄 |
| height | DOUBLE | 身高(cm) |
| created_at | TIMESTAMP | 创建时间 |

### body_data - 身体数据表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID（外键） |
| weight | DOUBLE | 体重(kg) |
| body_fat | DOUBLE | 体脂率(%) |
| bmi | DOUBLE | BMI指数 |
| record_date | DATE | 记录日期 |

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 合理使用注释和Javadoc

### Git提交规范
```bash
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具链相关
```

## 常见问题

### Q: 启动时报错"java.lang.NoClassDefFoundError"？
A: 确保JDK版本为17或更高，并执行`mvn clean install`。

### Q: Token验证失败？
A: 检查JWT secret配置，确保前后端secret一致。

### Q: 数据库连接失败？
A: 检查application.yml中的数据库配置。

## 待实现功能

以下功能的Service和Controller需要继续完善：

- [ ] 健身计划Service（AI生成算法）
- [ ] 训练记录Service（统计功能）
- [ ] 营养建议Service（BMR计算）
- [ ] 社区动态Service（点赞、评论）

## 许可证

MIT License

## 联系方式

如有问题请提Issue或联系开发团队。
