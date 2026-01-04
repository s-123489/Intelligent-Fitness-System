# 部署检查清单

部署前、部署中、部署后的完整检查列表

## ✅ 部署前检查

### 本地环境
- [ ] JDK 8 或更高版本已安装
- [ ] Maven已安装（或使用项目自带的Maven Wrapper）
- [ ] Node.js 14+ 和 npm已安装
- [ ] 项目可以在本地正常运行

### 项目准备
- [ ] 后端代码无编译错误
- [ ] 前端代码无编译错误
- [ ] 已测试基本功能（注册、登录）
- [ ] 已更新JWT密钥配置

### Ubuntu服务器准备
- [ ] Ubuntu 18.04 服务器已准备
- [ ] 有root或sudo权限
- [ ] 服务器能访问互联网（下载依赖）
- [ ] 至少1GB RAM，2GB可用磁盘空间
- [ ] 已知服务器IP地址

---

## ✅ 构建阶段

### 后端构建
```bash
cd backend
mvnw.cmd clean package -DskipTests
```
- [ ] 构建成功，无错误
- [ ] JAR文件已生成: `target/fitness-system-1.0.0.jar`
- [ ] JAR文件大小合理（约30-50MB）

### 前端构建
```bash
cd frontend
npm install
npm run build
```
- [ ] 依赖安装成功
- [ ] 构建成功，无错误
- [ ] dist目录已生成
- [ ] dist目录包含index.html和assets文件夹

### 部署包创建
```bash
bash deploy/build.sh
```
- [ ] 脚本执行成功
- [ ] deploy/package目录已创建
- [ ] package目录包含所有必要文件

---

## ✅ 文件传输

### 传输前
- [ ] 部署包完整性检查
- [ ] 已准备传输方式（SCP或共享文件夹）

### 传输后
- [ ] 所有文件已成功传输到Ubuntu
- [ ] 文件大小和数量一致
- [ ] 压缩包（如有）已解压

---

## ✅ Ubuntu安装阶段

### 依赖安装
```bash
sudo ./install.sh
```
- [ ] OpenJDK 8已安装
  ```bash
  java -version
  # 应显示: openjdk version "1.8.x"
  ```
- [ ] Nginx已安装
  ```bash
  nginx -v
  # 应显示版本信息
  ```

### 目录创建
- [ ] /opt/fitness-system/backend/ 已创建
- [ ] /opt/fitness-system/frontend/ 已创建
- [ ] /var/lib/fitness/data/ 已创建
- [ ] /var/log/fitness/ 已创建

验证命令:
```bash
ls -la /opt/fitness-system/
ls -la /var/lib/fitness/
ls -la /var/log/fitness/
```

### 文件复制
- [ ] JAR文件已复制到 /opt/fitness-system/backend/
- [ ] application-prod.yml已复制
- [ ] 前端文件已复制到 /opt/fitness-system/frontend/
- [ ] 前端目录包含index.html

验证命令:
```bash
ls -la /opt/fitness-system/backend/
ls -la /opt/fitness-system/frontend/
```

### Nginx配置
- [ ] 配置文件已复制到 /etc/nginx/sites-available/
- [ ] 软链接已创建到 sites-enabled/
- [ ] Nginx配置测试通过
  ```bash
  sudo nginx -t
  # 应显示: syntax is ok
  ```

### systemd服务配置
- [ ] fitness-backend.service已复制
- [ ] systemd已重载
  ```bash
  sudo systemctl daemon-reload
  ```
- [ ] 服务已启用开机自启
  ```bash
  sudo systemctl is-enabled fitness-backend
  # 应显示: enabled
  ```

### 环境变量
- [ ] JWT_SECRET已配置
- [ ] 环境变量已生效

---

## ✅ 启动服务

### 启动后端
```bash
sudo systemctl start fitness-backend
```
- [ ] 服务启动成功
  ```bash
  sudo systemctl status fitness-backend
  # 应显示: active (running)
  ```
- [ ] 无错误日志
  ```bash
  sudo journalctl -u fitness-backend -n 50
  ```
- [ ] 日志显示"启动成功"信息

### 启动Nginx
```bash
sudo systemctl restart nginx
```
- [ ] Nginx启动成功
  ```bash
  sudo systemctl status nginx
  # 应显示: active (running)
  ```
- [ ] 无错误日志

---

## ✅ 验证部署

### 端口检查
```bash
sudo netstat -tlnp | grep 5000
sudo netstat -tlnp | grep 80
```
- [ ] 5000端口被java进程占用
- [ ] 80端口被nginx进程占用

### 后端API测试
```bash
curl http://localhost:5000/api/
```
- [ ] 返回响应（不是404或502）
- [ ] 响应格式为JSON

### Nginx代理测试
```bash
curl http://localhost/api/
```
- [ ] 成功代理到后端
- [ ] 返回与直接访问后端相同的结果

### 前端静态文件测试
```bash
curl http://localhost/
```
- [ ] 返回HTML内容
- [ ] 包含<!DOCTYPE html>标签
- [ ] 无404错误

### 浏览器访问测试
访问: `http://YOUR_SERVER_IP`
- [ ] 页面正常加载
- [ ] 能看到登录页面
- [ ] CSS样式正常显示
- [ ] 无JavaScript错误（F12查看控制台）

### 功能测试
- [ ] 能够访问注册页面
- [ ] 能够成功注册新用户
- [ ] 能够成功登录
- [ ] 登录后能看到系统主页
- [ ] API请求正常（查看Network标签）

---

## ✅ 日志检查

### 后端日志
```bash
sudo journalctl -u fitness-backend -n 100 --no-pager
```
- [ ] 无ERROR级别日志
- [ ] 无Exception堆栈
- [ ] 显示正常的请求日志

### 应用日志
```bash
sudo tail -n 100 /var/log/fitness/application.log
```
- [ ] 日志文件已创建
- [ ] 包含应用启动信息
- [ ] 无严重错误

### Nginx日志
```bash
sudo tail -n 50 /var/log/nginx/fitness-system-access.log
sudo tail -n 50 /var/log/nginx/fitness-system-error.log
```
- [ ] 访问日志正常记录
- [ ] 错误日志无严重错误（404是正常的）

---

## ✅ 性能检查

### 内存使用
```bash
free -h
ps aux | grep java
```
- [ ] 系统剩余内存充足（>100MB）
- [ ] Java进程内存使用合理（<1GB）

### 磁盘使用
```bash
df -h
du -sh /opt/fitness-system
du -sh /var/lib/fitness
```
- [ ] 磁盘空间充足（>500MB可用）
- [ ] 应用占用空间合理

### 响应时间
```bash
time curl http://localhost/api/
```
- [ ] API响应时间<2秒

---

## ✅ 安全检查

### JWT密钥
```bash
sudo cat /etc/systemd/system/fitness-backend.service | grep JWT_SECRET
```
- [ ] JWT_SECRET已修改（不是默认值）
- [ ] 密钥长度足够（至少32字符）

### 文件权限
```bash
ls -la /opt/fitness-system/backend/
ls -la /opt/fitness-system/frontend/
```
- [ ] 文件权限合理（不是777）
- [ ] 敏感文件权限受限

### H2控制台
```bash
grep "h2.console.enabled" /opt/fitness-system/backend/application-prod.yml
```
- [ ] 生产环境H2控制台已关闭（enabled: false）

### 防火墙
```bash
sudo ufw status
```
- [ ] 防火墙配置合理
- [ ] 80端口已开放
- [ ] 不必要的端口已关闭

---

## ✅ 自动启动检查

### 服务自启配置
```bash
sudo systemctl is-enabled fitness-backend
sudo systemctl is-enabled nginx
```
- [ ] fitness-backend: enabled
- [ ] nginx: enabled

### 重启测试（可选但推荐）
```bash
sudo reboot
# 重启后检查
sudo systemctl status fitness-backend
sudo systemctl status nginx
```
- [ ] 重启后服务自动启动
- [ ] 应用正常运行

---

## ✅ 备份检查

### 初始备份
```bash
sudo cp -r /var/lib/fitness/data /var/lib/fitness/data-backup-initial
```
- [ ] 初始数据库备份已创建

---

## ✅ 文档检查

- [ ] 已记录服务器IP地址
- [ ] 已记录管理员账户信息
- [ ] 已记录JWT密钥（安全存储）
- [ ] 已记录数据库位置
- [ ] 已创建维护文档

---

## 🎉 部署完成确认

全部检查项通过后：

- [ ] 系统可以正常访问
- [ ] 所有核心功能正常
- [ ] 日志无严重错误
- [ ] 性能表现良好
- [ ] 安全配置合理
- [ ] 服务开机自启
- [ ] 备份已创建
- [ ] 文档已完成

**部署完成时间**: _______________

**部署人员**: _______________

**验证人员**: _______________

---

## 🚨 问题记录

部署过程中遇到的问题及解决方案：

| 问题描述 | 解决方案 | 备注 |
|---------|---------|------|
|         |         |      |
|         |         |      |
|         |         |      |

---

## 📞 故障联系方式

如遇到问题：

1. 查看 `deploy/DEPLOYMENT.md` 的"常见问题"章节
2. 检查日志: `sudo journalctl -u fitness-backend -f`
3. 查看完整部署文档

---

**检查清单版本**: 1.0
**最后更新**: 2024-12-29
