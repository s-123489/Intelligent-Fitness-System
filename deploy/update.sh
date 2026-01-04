#!/bin/bash

# 智能健身系统 - Ubuntu更新脚本
# 用于在Ubuntu上更新已部署的应用

set -e

echo "========================================="
echo "智能健身系统 - 更新脚本"
echo "========================================="

# 检查是否以root权限运行
if [ "$EUID" -ne 0 ]; then
    echo "请使用sudo运行此脚本"
    exit 1
fi

# 应用目录
APP_DIR="/opt/fitness-system"
BACKUP_DIR="/opt/fitness-system-backup-$(date +%Y%m%d_%H%M%S)"
CURRENT_DIR="$(pwd)"

echo ""
echo "当前目录: $CURRENT_DIR"
echo "应用目录: $APP_DIR"
echo "备份目录: $BACKUP_DIR"
echo ""

# 确认更新
read -p "是否要更新应用? 这将替换当前运行的版本 (y/n): " -r
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "更新已取消"
    exit 0
fi

# 1. 停止服务
echo ""
echo "[1/6] 停止后端服务..."
systemctl stop fitness-backend
echo "✓ 服务已停止"

# 2. 备份当前版本
echo ""
echo "[2/6] 备份当前版本..."
mkdir -p "$BACKUP_DIR"
cp -r "$APP_DIR/"* "$BACKUP_DIR/"
echo "✓ 备份完成: $BACKUP_DIR"

# 3. 备份数据库
echo ""
echo "[3/6] 备份数据库..."
DATA_DIR="/var/lib/fitness/data"
if [ -d "$DATA_DIR" ]; then
    BACKUP_DATA_DIR="/var/lib/fitness/backup-$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$BACKUP_DATA_DIR"
    cp -r "$DATA_DIR/"* "$BACKUP_DATA_DIR/" 2>/dev/null || true
    echo "✓ 数据库备份完成: $BACKUP_DATA_DIR"
else
    echo "! 数据目录不存在，跳过数据库备份"
fi

# 4. 更新后端
echo ""
echo "[4/6] 更新后端..."
if [ -f "$CURRENT_DIR/backend/fitness-system-1.0.0.jar" ]; then
    cp "$CURRENT_DIR/backend/fitness-system-1.0.0.jar" "$APP_DIR/backend/"
    echo "✓ 后端JAR已更新"
else
    echo "! 警告: 未找到后端JAR文件"
fi

if [ -f "$CURRENT_DIR/backend/application-prod.yml" ]; then
    cp "$CURRENT_DIR/backend/application-prod.yml" "$APP_DIR/backend/"
    echo "✓ 后端配置已更新"
fi

# 5. 更新前端
echo ""
echo "[5/6] 更新前端..."
if [ -d "$CURRENT_DIR/frontend" ]; then
    rm -rf "$APP_DIR/frontend/"*
    cp -r "$CURRENT_DIR/frontend/"* "$APP_DIR/frontend/"
    echo "✓ 前端文件已更新"
else
    echo "! 警告: 未找到前端文件"
fi

# 6. 重启服务
echo ""
echo "[6/6] 重启服务..."
systemctl daemon-reload
systemctl start fitness-backend
systemctl reload nginx

echo "✓ 服务已重启"

# 等待服务启动
echo ""
echo "等待服务启动..."
sleep 5

# 检查服务状态
echo ""
echo "========================================="
echo "更新完成！"
echo "========================================="
echo ""
echo "服务状态:"
systemctl status fitness-backend --no-pager -l | head -n 10

echo ""
echo "查看实时日志:"
echo "  sudo journalctl -u fitness-backend -f"
echo ""
echo "如果更新出现问题，可以回滚:"
echo "  sudo systemctl stop fitness-backend"
echo "  sudo cp -r $BACKUP_DIR/* $APP_DIR/"
echo "  sudo systemctl start fitness-backend"
echo ""
