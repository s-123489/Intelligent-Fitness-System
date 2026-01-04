# Ubuntu 部署流程图

## 部署架构图

```
┌──────────────────────────────────────────────────────────┐
│                    客户端浏览器                           │
│                http://SERVER_IP                          │
└──────────────────────┬───────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────┐
│                   Nginx (Port 80)                        │
│  ┌──────────────────────────────────────────────────┐   │
│  │  静态文件服务: /opt/fitness-system/frontend/     │   │
│  │  - index.html, assets/*                          │   │
│  │  - 缓存策略: 1年                                  │   │
│  └──────────────────────────────────────────────────┘   │
│                                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │  反向代理: /api/* → http://localhost:5000       │   │
│  │  - WebSocket支持                                 │   │
│  │  - gzip压缩                                      │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────┬───────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────┐
│           Spring Boot Application (Port 5000)            │
│  ┌──────────────────────────────────────────────────┐   │
│  │  JAR: fitness-system-1.0.0.jar                   │   │
│  │  配置: application-prod.yml                      │   │
│  │  位置: /opt/fitness-system/backend/              │   │
│  └──────────────────────────────────────────────────┘   │
│                                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Spring Security + JWT                           │   │
│  │  - Token验证                                     │   │
│  │  - BCrypt密码加密                                │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────┬───────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────┐
│              H2 Database (文件模式)                       │
│  位置: /var/lib/fitness/data/fitnessdb.mv.db            │
│  - 自动持久化                                            │
│  - 支持热备份                                            │
└──────────────────────────────────────────────────────────┘
```

## 部署步骤流程

```
┌─────────────────────────────────────────────────────────────┐
│  第1步: 本地构建 (Windows/开发机)                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 构建后端                                                │
│     cd backend                                              │
│     mvnw.cmd clean package -DskipTests                      │
│     ✓ 生成: target/fitness-system-1.0.0.jar                │
│                                                             │
│  2. 构建前端                                                │
│     cd frontend                                             │
│     npm install                                             │
│     npm run build                                           │
│     ✓ 生成: dist/                                          │
│                                                             │
│  3. 创建部署包                                              │
│     bash deploy/build.sh                                    │
│     ✓ 生成: deploy/package/                                │
│                                                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│  第2步: 传输文件到Ubuntu                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  方式A: 共享文件夹 (虚拟机)                                 │
│  - 直接访问共享目录                                         │
│                                                             │
│  方式B: SCP传输                                             │
│  - scp -r deploy/package/* user@ubuntu-ip:/tmp/            │
│                                                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│  第3步: Ubuntu服务器安装                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 安装依赖                                                │
│     sudo ./install.sh                                       │
│     - OpenJDK 8                                             │
│     - Nginx                                                 │
│                                                             │
│  2. 创建目录                                                │
│     - /opt/fitness-system/                                  │
│     - /var/lib/fitness/data/                                │
│     - /var/log/fitness/                                     │
│                                                             │
│  3. 复制文件                                                │
│     - backend JAR → /opt/fitness-system/backend/           │
│     - frontend dist → /opt/fitness-system/frontend/        │
│                                                             │
│  4. 配置Nginx                                               │
│     - 复制配置到 /etc/nginx/sites-available/               │
│     - 创建软链接到 sites-enabled/                          │
│                                                             │
│  5. 配置systemd服务                                         │
│     - 复制 fitness-backend.service                          │
│     - systemctl daemon-reload                               │
│     - systemctl enable fitness-backend                      │
│                                                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│  第4步: 启动服务                                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  sudo systemctl start fitness-backend                       │
│  sudo systemctl restart nginx                               │
│                                                             │
│  ✓ 后端运行在: http://localhost:5000/api                   │
│  ✓ 前端服务: http://SERVER_IP                              │
│                                                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│  第5步: 验证部署                                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 检查服务状态                                            │
│     systemctl status fitness-backend                        │
│     systemctl status nginx                                  │
│                                                             │
│  2. 查看日志                                                │
│     journalctl -u fitness-backend -f                        │
│                                                             │
│  3. 测试访问                                                │
│     curl http://localhost/api/                              │
│     浏览器访问: http://SERVER_IP                            │
│                                                             │
│  4. 功能测试                                                │
│     - 注册新用户                                            │
│     - 登录测试                                              │
│     - API调用测试                                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 系统服务启动流程

```
系统启动
   │
   ▼
systemd启动
   │
   ├─> nginx.service
   │   └─> 监听80端口
   │       ├─> 服务静态文件
   │       └─> 代理API请求
   │
   └─> fitness-backend.service
       └─> 启动Spring Boot
           ├─> 加载application-prod.yml
           ├─> 连接H2数据库
           ├─> 初始化JWT配置
           ├─> 启动Spring Security
           └─> 监听5000端口
```

## 请求处理流程

```
用户请求
   │
   ▼
Nginx (Port 80)
   │
   ├─────────────────────────────────────┐
   │                                     │
   ▼                                     ▼
静态资源 (/*)                          API请求 (/api/*)
   │                                     │
   │                                     │ 反向代理
   │                                     ▼
   │                              Spring Boot (5000)
   │                                     │
   │                                     │
   │                              ┌──────┴──────┐
   │                              │             │
   │                              ▼             ▼
   │                         JWT验证         业务逻辑
   │                              │             │
   │                              │             ▼
   │                              │        数据库操作
   │                              │             │
   │                              └──────┬──────┘
   │                                     │
   │                                     ▼
   │                                 返回JSON
   │                                     │
   ▼                                     │
返回HTML/CSS/JS                          │
   │                                     │
   └─────────────────┬───────────────────┘
                     │
                     ▼
                 客户端浏览器
```

## 数据流向

```
┌─────────────┐
│   Browser   │
└──────┬──────┘
       │ HTTP Request
       ▼
┌─────────────┐
│    Nginx    │
│   Port 80   │
└──────┬──────┘
       │ Proxy
       ▼
┌─────────────┐      ┌────────────┐
│ Spring Boot │◄────►│ H2 Database│
│  Port 5000  │ JPA  │   (File)   │
└──────┬──────┘      └────────────┘
       │ JSON Response
       ▼
┌─────────────┐
│    Nginx    │
└──────┬──────┘
       │ HTTP Response
       ▼
┌─────────────┐
│   Browser   │
└─────────────┘
```

## 文件部署位置

```
Ubuntu 文件系统
│
├── /opt/fitness-system/          # 应用主目录
│   ├── backend/
│   │   ├── fitness-system-1.0.0.jar
│   │   └── application-prod.yml
│   └── frontend/
│       ├── index.html
│       └── assets/
│           ├── index-*.js
│           ├── index-*.css
│           └── ...
│
├── /var/lib/fitness/             # 数据目录
│   └── data/
│       ├── fitnessdb.mv.db       # H2数据库
│       └── fitnessdb.trace.db
│
├── /var/log/fitness/             # 日志目录
│   └── application.log
│
├── /etc/nginx/
│   └── sites-available/
│       └── fitness-system        # Nginx配置
│
└── /etc/systemd/system/
    └── fitness-backend.service   # 系统服务配置
```

## 端口使用

```
┌──────────┬────────────────────────────────────┐
│  端口    │  用途                              │
├──────────┼────────────────────────────────────┤
│   80     │  Nginx HTTP服务                    │
│  5000    │  Spring Boot应用 (内部)            │
│  443     │  HTTPS (可选，配置SSL后)           │
└──────────┴────────────────────────────────────┘
```

## 日志位置

```
┌─────────────────────────────────┬──────────────────────┐
│  日志类型                       │  位置                │
├─────────────────────────────────┼──────────────────────┤
│  systemd服务日志                │  journalctl          │
│  Spring Boot应用日志            │  /var/log/fitness/   │
│  Nginx访问日志                  │  /var/log/nginx/     │
│  Nginx错误日志                  │  /var/log/nginx/     │
└─────────────────────────────────┴──────────────────────┘
```

## 常用检查命令速查

```bash
# 服务状态
systemctl status fitness-backend
systemctl status nginx

# 日志查看
journalctl -u fitness-backend -f              # 实时系统日志
tail -f /var/log/fitness/application.log      # 应用日志
tail -f /var/log/nginx/fitness-system-access.log  # Nginx访问日志

# 测试连接
curl http://localhost:5000/api/               # 测试后端
curl http://localhost/                         # 测试Nginx

# 进程检查
ps aux | grep java                            # 检查Java进程
ps aux | grep nginx                           # 检查Nginx进程

# 端口检查
netstat -tlnp | grep 5000                     # 检查5000端口
netstat -tlnp | grep 80                       # 检查80端口

# 磁盘使用
df -h                                         # 查看磁盘空间
du -sh /var/lib/fitness/data                  # 数据库大小
du -sh /var/log/fitness                       # 日志大小
```
