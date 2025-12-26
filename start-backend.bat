@echo off
echo ========================================
echo 正在清理并编译Java后端项目...
echo ========================================

cd backend
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo 编译成功！正在启动后端服务...
    echo ========================================
    echo.
    call mvnw.cmd spring-boot:run
) else (
    echo.
    echo ========================================
    echo 编译失败，请检查错误信息
    echo ========================================
    pause
)
