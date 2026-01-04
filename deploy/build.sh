#!/bin/bash

# 智能健身系统部署脚本
# 用于在Ubuntu上构建前后端项目

set -e  # 遇到错误立即退出

echo "========================================="
echo "智能健身系统 - 部署构建脚本"
echo "========================================="

# 获取项目根目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "项目根目录: $PROJECT_ROOT"

# 1. 构建后端
echo ""
echo "[1/3] 开始构建后端..."
cd "$PROJECT_ROOT/backend"

# 检查是否有Maven
if command -v mvn &> /dev/null; then
    echo "使用系统Maven构建..."
    mvn clean package -DskipTests
else
    echo "使用Maven Wrapper构建..."
    chmod +x mvnw
    ./mvnw clean package -DskipTests
fi

echo "✓ 后端构建完成！"
echo "JAR文件位置: $PROJECT_ROOT/backend/target/fitness-system-1.0.0.jar"

# 2. 构建前端
echo ""
echo "[2/3] 开始构建前端..."
cd "$PROJECT_ROOT/frontend"

# 安装依赖（如果需要）
if [ ! -d "node_modules" ]; then
    echo "安装前端依赖..."
    npm install
fi

# 构建前端
echo "执行前端构建..."
npm run build

echo "✓ 前端构建完成！"
echo "静态文件位置: $PROJECT_ROOT/frontend/dist"

# 3. 创建部署包
echo ""
echo "[3/3] 创建部署包..."
cd "$PROJECT_ROOT"

DEPLOY_DIR="$PROJECT_ROOT/deploy/package"
mkdir -p "$DEPLOY_DIR"

# 复制后端JAR
mkdir -p "$DEPLOY_DIR/backend"
cp backend/target/fitness-system-1.0.0.jar "$DEPLOY_DIR/backend/"
cp backend/src/main/resources/application-prod.yml "$DEPLOY_DIR/backend/"

# 复制前端构建文件
mkdir -p "$DEPLOY_DIR/frontend"
cp -r frontend/dist/* "$DEPLOY_DIR/frontend/"

# 复制配置文件
cp deploy/nginx.conf "$DEPLOY_DIR/" 2>/dev/null || echo "注意: nginx.conf未找到"
cp deploy/fitness-backend.service "$DEPLOY_DIR/" 2>/dev/null || echo "注意: fitness-backend.service未找到"

echo "✓ 部署包创建完成！"
echo "部署包位置: $DEPLOY_DIR"

# 4. 创建压缩包（可选）
echo ""
echo "是否创建tar.gz压缩包? (y/n)"
read -r response
if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
    cd "$PROJECT_ROOT/deploy"
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    TAR_NAME="fitness-system-$TIMESTAMP.tar.gz"
    tar -czf "$TAR_NAME" -C package .
    echo "✓ 压缩包已创建: $PROJECT_ROOT/deploy/$TAR_NAME"
fi

echo ""
echo "========================================="
echo "构建完成！"
echo "========================================="
echo "下一步: 将部署包传输到Ubuntu服务器并运行安装脚本"
