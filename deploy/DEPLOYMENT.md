# Ubuntu 18.04 部署指南

智能健身管理系统完整部署文档

## 目录

1. [环境要求](#环境要求)
2. [部署架构](#部署架构)
3. [部署步骤](#部署步骤)
4. [验证部署](#验证部署)
5. [常见问题](#常见问题)
6. [维护管理](#维护管理)

---

## 环境要求

### Ubuntu服务器
- 操作系统: Ubuntu 18.04 LTS
- 内存: 至少 1GB RAM
- 磁盘: 至少 2GB 可用空间
- 网络: 能够访问互联网（安装依赖）

### 本地开发环境（用于构建）
- JDK 8 或更高版本
- Node.js 14+ 和 npm
- Maven 3.6+ (可选，可使用项目自带的Maven Wrapper)

---

## 部署架构

```
┌─────────────────────────────────────────┐
│          Nginx (Port 80)                │
│  - 静态文件服务 (前端)                    │
│  - 反向代理 (API请求)                     │
└────────────┬────────────────────────────┘
             │
             ▼
    ┌────────────────┐
    │  Spring Boot   │
    │   (Port 5000)  │
    │   /api/*       │
    └────────┬───────┘
             │
             ▼
    ┌────────────────┐
    │  H2 Database   │
    │  (文件存储)     │
    └────────────────┘
```

### 目录结构

```
/opt/fitness-system/         # 应用主目录
├── backend/
│   ├── fitness-system-1.0.0.jar
│   └── application-prod.yml
└── frontend/                # 前端静态文件
    ├── index.html
    └── assets/

/var/lib/fitness/            # 数据目录
└── data/
    └── fitnessdb.*          # H2数据库文件

/var/log/fitness/            # 日志目录
└── application.log
```

---

## 部署步骤

### 方式一: 自动部署（推荐）

#### 步骤 1: 在本地构建项目

在Windows环境下（项目目录）：

```bash
# 如果在Windows上，使用Git Bash执行
cd /path/to/大作业
bash deploy/build.sh
```

或者手动执行：

**构建后端:**
```bash
cd backend
mvnw.cmd clean package -DskipTests
# 或使用系统Maven: mvn clean package -DskipTests
```

**构建前端:**
```bash
cd frontend
npm install
npm run build
```

#### 步骤 2: 传输文件到Ubuntu

**方法A: 使用共享文件夹**

如果你的Ubuntu是虚拟机并且已经配置了共享文件夹：

```bash
# 在Ubuntu上
cd /path/to/shared/folder/大作业/deploy
```

**方法B: 使用SCP传输**

```bash
# 在Windows上
scp -r deploy/package/* user@ubuntu-server-ip:/tmp/fitness-deploy/
```

#### 步骤 3: 在Ubuntu上安装

```bash
# SSH登录到Ubuntu服务器
ssh user@ubuntu-server-ip

# 进入部署包目录
cd /path/to/deploy/package

# 给安装脚本执行权限
chmod +x install.sh

# 运行安装脚本
sudo ./install.sh
```

#### 步骤 4: 启动服务

```bash
# 启动后端服务
sudo systemctl start fitness-backend

# 启动Nginx
sudo systemctl restart nginx

# 设置开机自启
sudo systemctl enable fitness-backend
sudo systemctl enable nginx
```

---

### 方式二: 手动部署

如果自动脚本出现问题，可以手动部署：

#### 1. 安装依赖

```bash
# 更新包列表
sudo apt-get update

# 安装OpenJDK 8
sudo apt-get install -y openjdk-8-jdk

# 安装Nginx
sudo apt-get install -y nginx

# 验证安装
java -version
nginx -v
```

#### 2. 创建目录

```bash
sudo mkdir -p /opt/fitness-system/backend
sudo mkdir -p /opt/fitness-system/frontend
sudo mkdir -p /var/lib/fitness/data
sudo mkdir -p /var/log/fitness
```

#### 3. 复制文件

```bash
# 复制后端JAR（从你的构建目录）
sudo cp backend/target/fitness-system-1.0.0.jar /opt/fitness-system/backend/
sudo cp backend/src/main/resources/application-prod.yml /opt/fitness-system/backend/

# 复制前端文件
sudo cp -r frontend/dist/* /opt/fitness-system/frontend/
```

#### 4. 配置Nginx

```bash
# 复制Nginx配置
sudo cp deploy/nginx.conf /etc/nginx/sites-available/fitness-system

# 创建软链接
sudo ln -sf /etc/nginx/sites-available/fitness-system /etc/nginx/sites-enabled/

# 删除默认配置
sudo rm -f /etc/nginx/sites-enabled/default

# 测试配置
sudo nginx -t

# 重启Nginx
sudo systemctl restart nginx
```

#### 5. 配置systemd服务

```bash
# 复制服务文件
sudo cp deploy/fitness-backend.service /etc/systemd/system/

# 生成JWT密钥
JWT_SECRET=$(openssl rand -base64 32)

# 编辑服务文件，更新JWT_SECRET
sudo nano /etc/systemd/system/fitness-backend.service
# 将 Environment="JWT_SECRET=please-change-this-secret-key"
# 改为 Environment="JWT_SECRET=生成的密钥"

# 重载systemd
sudo systemctl daemon-reload

# 启用并启动服务
sudo systemctl enable fitness-backend
sudo systemctl start fitness-backend
```

#### 6. 检查防火墙

```bash
# 如果启用了UFW防火墙
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw status
```

---

## 验证部署

### 1. 检查后端服务状态

```bash
# 查看服务状态
sudo systemctl status fitness-backend

# 查看服务日志
sudo journalctl -u fitness-backend -f

# 查看应用日志
sudo tail -f /var/log/fitness/application.log
```

**期望输出:**
- Status: active (running)
- 日志中显示 "智能健身管理系统启动成功"

### 2. 测试后端API

```bash
# 测试API是否响应
curl http://localhost:5000/api/auth/health

# 或从外部访问
curl http://YOUR_SERVER_IP/api/auth/health
```

### 3. 检查Nginx状态

```bash
sudo systemctl status nginx
sudo nginx -t
```

### 4. 访问前端

在浏览器中访问:
```
http://YOUR_SERVER_IP
```

应该能看到登录页面。

### 5. 完整功能测试

1. **注册新用户**
   - 访问 http://YOUR_SERVER_IP
   - 点击"立即注册"
   - 填写表单并注册

2. **登录测试**
   - 使用注册的账号登录
   - 检查是否能进入系统主页

3. **API测试**
   - 登录后，检查浏览器开发者工具
   - 查看Network标签，确认API请求正常

---

## 常见问题

### 1. 后端服务启动失败

**检查日志:**
```bash
sudo journalctl -u fitness-backend -n 50 --no-pager
```

**常见原因:**
- Java版本不匹配
- 端口5000被占用
- 配置文件路径错误

**解决方案:**
```bash
# 检查Java版本
java -version

# 检查端口占用
sudo netstat -tlnp | grep 5000

# 如果端口被占用，修改application-prod.yml中的端口
sudo nano /opt/fitness-system/backend/application-prod.yml
```

### 2. Nginx 403 Forbidden

**原因:** 文件权限问题

**解决方案:**
```bash
# 设置正确的权限
sudo chmod -R 755 /opt/fitness-system/frontend
sudo chown -R www-data:www-data /opt/fitness-system/frontend
```

### 3. API请求失败 (CORS错误)

检查Nginx配置中的proxy_pass是否正确：
```bash
sudo nano /etc/nginx/sites-available/fitness-system
```

### 4. 数据库文件权限问题

```bash
# 确保数据目录权限正确
sudo chmod -R 755 /var/lib/fitness/data
sudo chown -R root:root /var/lib/fitness/data
```

### 5. 前端页面空白

**检查:**
1. 浏览器控制台是否有错误
2. 前端文件是否正确复制
3. Nginx配置的root路径是否正确

```bash
# 验证前端文件
ls -la /opt/fitness-system/frontend/
# 应该能看到 index.html 和 assets 目录

# 检查Nginx错误日志
sudo tail -f /var/log/nginx/fitness-system-error.log
```

---

## 维护管理

### 日常操作命令

**查看服务状态:**
```bash
sudo systemctl status fitness-backend
sudo systemctl status nginx
```

**启动/停止/重启服务:**
```bash
# 后端服务
sudo systemctl start fitness-backend
sudo systemctl stop fitness-backend
sudo systemctl restart fitness-backend

# Nginx
sudo systemctl start nginx
sudo systemctl stop nginx
sudo systemctl restart nginx
```

**查看日志:**
```bash
# 实时查看后端日志
sudo journalctl -u fitness-backend -f

# 查看最近100行日志
sudo journalctl -u fitness-backend -n 100

# 查看应用日志
sudo tail -f /var/log/fitness/application.log

# 查看Nginx访问日志
sudo tail -f /var/log/nginx/fitness-system-access.log

# 查看Nginx错误日志
sudo tail -f /var/log/nginx/fitness-system-error.log
```

### 备份数据库

H2数据库文件位于 `/var/lib/fitness/data/`

```bash
# 创建备份
sudo cp -r /var/lib/fitness/data /var/lib/fitness/backup-$(date +%Y%m%d)

# 或创建压缩备份
sudo tar -czf /var/lib/fitness/backup-$(date +%Y%m%d).tar.gz -C /var/lib/fitness data/
```

### 更新应用

```bash
# 1. 停止服务
sudo systemctl stop fitness-backend

# 2. 备份当前版本
sudo cp /opt/fitness-system/backend/fitness-system-1.0.0.jar \
        /opt/fitness-system/backend/fitness-system-1.0.0.jar.backup

# 3. 复制新的JAR文件
sudo cp new-version.jar /opt/fitness-system/backend/fitness-system-1.0.0.jar

# 4. 重启服务
sudo systemctl start fitness-backend

# 5. 查看日志确认启动成功
sudo journalctl -u fitness-backend -f
```

### 更新前端

```bash
# 1. 备份当前前端
sudo cp -r /opt/fitness-system/frontend /opt/fitness-system/frontend.backup

# 2. 复制新的前端文件
sudo cp -r new-frontend-dist/* /opt/fitness-system/frontend/

# 3. 清除Nginx缓存（如果配置了缓存）
sudo systemctl reload nginx
```

### 监控系统资源

```bash
# 查看CPU和内存使用
top

# 查看磁盘使用
df -h

# 查看特定进程
ps aux | grep java
```

### 清理日志

```bash
# 清理旧的journal日志（保留最近7天）
sudo journalctl --vacuum-time=7d

# 清理应用日志（手动）
sudo truncate -s 0 /var/log/fitness/application.log
```

---

## 安全建议

### 1. 更改JWT密钥

生产环境务必使用强密钥：
```bash
# 生成新密钥
openssl rand -base64 32

# 更新服务配置
sudo nano /etc/systemd/system/fitness-backend.service
# 修改 Environment="JWT_SECRET=新密钥"

# 重启服务
sudo systemctl daemon-reload
sudo systemctl restart fitness-backend
```

### 2. 配置HTTPS

建议使用Let's Encrypt免费SSL证书：

```bash
# 安装Certbot
sudo apt-get install certbot python-certbot-nginx

# 获取证书（需要域名）
sudo certbot --nginx -d your-domain.com

# Certbot会自动配置Nginx
```

### 3. 防火墙配置

```bash
# 启用UFW
sudo ufw enable

# 允许SSH
sudo ufw allow 22/tcp

# 允许HTTP/HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# 查看规则
sudo ufw status
```

### 4. 定期更新

```bash
# 更新系统包
sudo apt-get update
sudo apt-get upgrade

# 更新Java（如果有新版本）
sudo apt-get install openjdk-8-jdk
```

---

## 性能优化

### 1. JVM参数调优

编辑 `/etc/systemd/system/fitness-backend.service`:

```ini
ExecStart=/usr/bin/java -jar \
    -Xms512m \
    -Xmx1024m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -Dspring.profiles.active=prod \
    -Dspring.config.location=/opt/fitness-system/backend/application-prod.yml \
    /opt/fitness-system/backend/fitness-system-1.0.0.jar
```

### 2. Nginx缓存优化

在nginx.conf中已配置静态资源缓存，可根据需要调整。

### 3. 数据库优化

如果数据量大，建议迁移到MySQL：

```bash
# 安装MySQL
sudo apt-get install mysql-server

# 创建数据库
sudo mysql
CREATE DATABASE fitness CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'fitness'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON fitness.* TO 'fitness'@'localhost';
FLUSH PRIVILEGES;

# 修改application-prod.yml使用MySQL配置
```

---

## 联系与支持

如遇到问题，请检查：
1. 日志文件
2. 本文档的常见问题部分
3. GitHub Issues

---

**部署完成！祝您使用愉快！**
