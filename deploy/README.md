# 快速部署指南

智能健身系统 - Ubuntu 18.04 快速部署

## 文件清单

```
deploy/
├── build.sh                    # 本地构建脚本
├── install.sh                  # Ubuntu安装脚本
├── nginx.conf                  # Nginx配置
├── fitness-backend.service     # systemd服务配置
├── DEPLOYMENT.md               # 完整部署文档
└── README.md                   # 本文件
```

## 快速部署（3步走）

### 第1步: 在本地构建（Windows/Linux）

**在Windows上使用Git Bash:**
```bash
cd /e/web/大作业
bash deploy/build.sh
```

**或手动构建:**
```bash
# 后端
cd backend
mvnw.cmd clean package -DskipTests

# 前端
cd frontend
npm install
npm run build
```

### 第2步: 传输到Ubuntu

**方式A - 共享文件夹（推荐）:**

如果Ubuntu是虚拟机并配置了共享文件夹，直接在Ubuntu中访问即可。

**方式B - SCP传输:**
```bash
# 压缩部署文件
cd deploy/package
tar -czf fitness-deploy.tar.gz *

# 传输到Ubuntu
scp fitness-deploy.tar.gz user@ubuntu-ip:/tmp/

# 在Ubuntu上解压
ssh user@ubuntu-ip
cd /tmp
tar -xzf fitness-deploy.tar.gz
```

### 第3步: 在Ubuntu上安装

```bash
# 进入部署目录
cd /tmp/fitness-deploy  # 或共享文件夹路径

# 给脚本执行权限
chmod +x install.sh

# 运行安装（需要root权限）
sudo ./install.sh

# 启动服务
sudo systemctl start fitness-backend
sudo systemctl restart nginx

# 查看状态
sudo systemctl status fitness-backend
```

## 验证部署

### 1. 检查服务状态
```bash
sudo systemctl status fitness-backend
sudo systemctl status nginx
```

### 2. 查看日志
```bash
# 后端日志
sudo journalctl -u fitness-backend -f

# Nginx日志
sudo tail -f /var/log/nginx/fitness-system-access.log
```

### 3. 测试访问
```bash
# 测试API
curl http://localhost/api/

# 在浏览器访问
http://YOUR_SERVER_IP
```

## 常用命令

### 服务管理
```bash
# 启动
sudo systemctl start fitness-backend

# 停止
sudo systemctl stop fitness-backend

# 重启
sudo systemctl restart fitness-backend

# 查看状态
sudo systemctl status fitness-backend
```

### 日志查看
```bash
# 实时查看后端日志
sudo journalctl -u fitness-backend -f

# 查看最近50行
sudo journalctl -u fitness-backend -n 50

# 查看应用日志
sudo tail -f /var/log/fitness/application.log
```

### 数据备份
```bash
# 备份数据库
sudo cp -r /var/lib/fitness/data /var/lib/fitness/backup-$(date +%Y%m%d)
```

## 目录结构

```
/opt/fitness-system/         # 应用目录
├── backend/
│   ├── fitness-system-1.0.0.jar
│   └── application-prod.yml
└── frontend/                # 前端静态文件

/var/lib/fitness/data/       # 数据库文件
/var/log/fitness/            # 应用日志
```

## 故障排查

### 后端无法启动
```bash
# 查看详细日志
sudo journalctl -u fitness-backend -n 100 --no-pager

# 检查端口占用
sudo netstat -tlnp | grep 5000

# 检查Java版本
java -version
```

### 前端无法访问
```bash
# 检查Nginx配置
sudo nginx -t

# 查看Nginx错误日志
sudo tail -f /var/log/nginx/error.log

# 检查文件权限
ls -la /opt/fitness-system/frontend/
```

### API请求失败
```bash
# 检查后端是否运行
sudo systemctl status fitness-backend

# 检查Nginx代理配置
sudo cat /etc/nginx/sites-available/fitness-system

# 测试API连接
curl http://localhost:5000/api/
```

## 更新应用

### 更新后端
```bash
# 停止服务
sudo systemctl stop fitness-backend

# 备份旧版本
sudo cp /opt/fitness-system/backend/fitness-system-1.0.0.jar \
        /opt/fitness-system/backend/fitness-system-1.0.0.jar.backup

# 复制新JAR
sudo cp new-version.jar /opt/fitness-system/backend/fitness-system-1.0.0.jar

# 启动服务
sudo systemctl start fitness-backend
```

### 更新前端
```bash
# 备份
sudo cp -r /opt/fitness-system/frontend /opt/fitness-system/frontend.backup

# 复制新文件
sudo cp -r new-dist/* /opt/fitness-system/frontend/

# 重载Nginx
sudo systemctl reload nginx
```

## 安全提示

1. **修改JWT密钥** - 生产环境必须修改！
   ```bash
   # 生成新密钥
   openssl rand -base64 32

   # 修改服务配置
   sudo nano /etc/systemd/system/fitness-backend.service
   # 更新 Environment="JWT_SECRET=新密钥"

   # 重启
   sudo systemctl daemon-reload
   sudo systemctl restart fitness-backend
   ```

2. **配置防火墙**
   ```bash
   sudo ufw allow 80/tcp
   sudo ufw allow 443/tcp
   sudo ufw enable
   ```

3. **建议配置HTTPS**
   ```bash
   sudo apt-get install certbot python-certbot-nginx
   sudo certbot --nginx -d your-domain.com
   ```

## 获取帮助

- 完整文档: `deploy/DEPLOYMENT.md`
- 查看日志: `sudo journalctl -u fitness-backend -f`
- 检查状态: `sudo systemctl status fitness-backend`

---

**部署问题？** 请先查看完整文档 `DEPLOYMENT.md` 中的"常见问题"章节。
