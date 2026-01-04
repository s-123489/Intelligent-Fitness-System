#!/bin/bash

# Ubuntu服务器安装脚本
# 在Ubuntu 18.04上安装和配置智能健身系统

set -e

echo "========================================="
echo "智能健身系统 - Ubuntu安装脚本"
echo "========================================="

# 检查是否以root权限运行
if [ "$EUID" -ne 0 ]; then
    echo "请使用sudo运行此脚本"
    exit 1
fi

# 1. 安装必要的软件
echo ""
echo "[1/6] 检查并安装必要软件..."

# 更新包列表
apt-get update

# 安装Java 8（项目使用Java 8）
if ! command -v java &> /dev/null; then
    echo "安装OpenJDK 8..."
    apt-get install -y openjdk-8-jdk
else
    echo "✓ Java已安装"
fi

# 验证Java版本
java -version

# 安装Nginx
if ! command -v nginx &> /dev/null; then
    echo "安装Nginx..."
    apt-get install -y nginx
else
    echo "✓ Nginx已安装"
fi

echo "✓ 软件安装完成"

# 2. 创建应用目录
echo ""
echo "[2/6] 创建应用目录..."

APP_DIR="/opt/fitness-system"
DATA_DIR="/var/lib/fitness/data"
LOG_DIR="/var/log/fitness"

mkdir -p "$APP_DIR/backend"
mkdir -p "$APP_DIR/frontend"
mkdir -p "$DATA_DIR"
mkdir -p "$LOG_DIR"

echo "✓ 目录创建完成"

# 3. 复制应用文件
echo ""
echo "[3/6] 复制应用文件..."

CURRENT_DIR="$(pwd)"

# 复制后端JAR
cp "$CURRENT_DIR/backend/fitness-system-1.0.0.jar" "$APP_DIR/backend/"
cp "$CURRENT_DIR/backend/application-prod.yml" "$APP_DIR/backend/"

# 复制前端文件
cp -r "$CURRENT_DIR/frontend/"* "$APP_DIR/frontend/"

echo "✓ 文件复制完成"

# 4. 配置Nginx
echo ""
echo "[4/6] 配置Nginx..."

if [ -f "$CURRENT_DIR/nginx.conf" ]; then
    cp "$CURRENT_DIR/nginx.conf" /etc/nginx/sites-available/fitness-system
    ln -sf /etc/nginx/sites-available/fitness-system /etc/nginx/sites-enabled/

    # 删除默认配置（如果存在）
    rm -f /etc/nginx/sites-enabled/default

    # 测试Nginx配置
    nginx -t

    echo "✓ Nginx配置完成"
else
    echo "警告: nginx.conf未找到，跳过Nginx配置"
fi

# 5. 配置systemd服务
echo ""
echo "[5/6] 配置系统服务..."

if [ -f "$CURRENT_DIR/fitness-backend.service" ]; then
    cp "$CURRENT_DIR/fitness-backend.service" /etc/systemd/system/

    # 重载systemd
    systemctl daemon-reload

    # 启用服务开机自启
    systemctl enable fitness-backend.service

    echo "✓ 系统服务配置完成"
else
    echo "警告: fitness-backend.service未找到，跳过服务配置"
fi

# 6. 设置JWT密钥环境变量
echo ""
echo "[6/6] 配置环境变量..."

JWT_SECRET=$(openssl rand -base64 32)
echo "export JWT_SECRET='$JWT_SECRET'" >> /etc/environment

echo "✓ 环境变量配置完成"

# 完成
echo ""
echo "========================================="
echo "安装完成！"
echo "========================================="
echo ""
echo "应用目录: $APP_DIR"
echo "数据目录: $DATA_DIR"
echo "日志目录: $LOG_DIR"
echo ""
echo "下一步操作:"
echo "1. 启动后端服务: sudo systemctl start fitness-backend"
echo "2. 重启Nginx: sudo systemctl restart nginx"
echo "3. 查看后端日志: sudo journalctl -u fitness-backend -f"
echo "4. 查看应用日志: sudo tail -f /var/log/fitness/application.log"
echo ""
echo "服务管理命令:"
echo "- 启动服务: sudo systemctl start fitness-backend"
echo "- 停止服务: sudo systemctl stop fitness-backend"
echo "- 重启服务: sudo systemctl restart fitness-backend"
echo "- 查看状态: sudo systemctl status fitness-backend"
echo ""
echo "访问地址: http://YOUR_SERVER_IP"
echo "API地址: http://YOUR_SERVER_IP/api"
