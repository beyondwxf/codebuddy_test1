@echo off
chcp 65001 >nul 2>&1
echo ========================================
echo   Task Manager 项目启动脚本
echo ========================================
echo.

:: 启动后端（Spring Boot）
echo [1/2] 启动后端 Spring Boot (端口 8080) ...
start "TaskManager-Backend" cmd /c "cd /d %~dp0task-manager-backend && java -jar target\task-manager-backend-1.0.0.jar"

:: 等待 2 秒
timeout /t 2 /nobreak >nul

:: 启动前端（Vite Dev Server）
echo [2/2] 启动前端 Vite Dev Server (端口 3000) ...
start "TaskManager-Frontend" cmd /c "cd /d %~dp0task-manager-frontend && npm run dev"

echo.
echo ========================================
echo   启动完成！
echo   后端: http://localhost:8080
echo   前端: http://localhost:3000
echo   API文档: http://localhost:8080/doc.html
echo ========================================
pause
