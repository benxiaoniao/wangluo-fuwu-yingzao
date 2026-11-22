@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion
cd /d "%~dp0"

echo ============================================
echo   网络服务营造 —— 新机器环境准备
echo ============================================
echo.

echo [1/5] 检查 jishi（基石）...
set "JISHI_EXE="
for /f "delims=" %%i in ('where jishi 2^>nul') do set "JISHI_EXE=%%i"
if not defined JISHI_EXE {
  if exist "%LOCALAPPDATA%\Programs\jishi\bin\jishi.exe" set "JISHI_EXE=%LOCALAPPDATA%\Programs\jishi\bin\jishi.exe"
}
if not defined JISHI_EXE (
  echo [错误] 没找到 jishi。请先安装基石 0.3.6，默认在 %LOCALAPPDATA%\Programs\jishi\bin
  echo        安装后重新运行本脚本。
  pause
  exit /b 1
)
set "JISHI_BIN="
for %%F in ("!JISHI_EXE!") do set "JISHI_BIN=%%~dpF"
"!JISHI_EXE!" --version

echo.
echo [2/5] 建本地包 junction ...
if not exist ".jishi\packages" mkdir ".jishi\packages"
if exist ".jishi\packages\网络服务营造" rmdir ".jishi\packages\网络服务营造"
mklink /J ".jishi\packages\网络服务营造" "网络服务营造"
if errorlevel 1 (
  echo [错误] 建 junction 失败。
  pause
  exit /b 1
)

echo.
echo [3/5] 复制 WASM 运行时（从 jishi 安装目录）...
if not exist "示例\博客\静态\前端" mkdir "示例\博客\静态\前端"
if exist "!JISHI_BIN!jishi-wasm.js" copy /Y "!JISHI_BIN!jishi-wasm.js" "示例\博客\静态\前端\jishi-wasm.js" >nul
if exist "!JISHI_BIN!jishi_wasm.wasm" copy /Y "!JISHI_BIN!jishi_wasm.wasm" "示例\博客\静态\前端\jishi_wasm.wasm" >nul
if exist "示例\博客\静态\前端\jishi_wasm.wasm" (echo    wasm 已就位) else (echo    [警告] 没找到 jishi_wasm.wasm，前端演示可能不可用)

echo.
echo [4/5] 跑冒烟测试（应全 OK）...
jishi tests\冒烟_核心.jsh
jishi tests\冒烟_后台.jsh

echo.
echo [5/5] 完成！
echo   现在可以： jishi 示例\博客\manage.jsh
echo   浏览器打开 http://127.0.0.1:8092/   （后台 /后台/ admin/admin123）
echo.
pause