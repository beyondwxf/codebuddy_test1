@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"
set JAVA_TOOL_OPTIONS=-Djansi.strip=true

REM 先尝试系统 Maven
where mvn >nul 2>&1
if %errorlevel%==0 (
    echo 使用系统 Maven 编译...
    call mvn clean package -DskipTests
    goto :end
)

REM 尝试直接调用 mvnw
echo 尝试使用 Maven Wrapper...
call mvnw.cmd clean package -DskipTests
if %errorlevel%==0 (
    goto :end
)

echo.
echo ============================================
echo   Maven 未安装且 Maven Wrapper 不可用
echo   请安装 Maven 或修复 mvnw.cmd
echo ============================================
exit /b 1

:end
if exist "target\task-manager-backend-1.0.0.jar" (
    echo.
    echo 编译成功！
) else (
    echo.
    echo 编译失败！
    exit /b 1
)
