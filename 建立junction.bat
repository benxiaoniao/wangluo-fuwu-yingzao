@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist ".jishi\packages" mkdir ".jishi\packages"
if exist ".jishi\packages\网络服务营造" rmdir ".jishi\packages\网络服务营造"
cmd /c mklink /J ".jishi\packages\网络服务营造" "网络服务营造"
echo.
echo 已完成：本地包 junction 建好了。
echo 现在可以在本目录运行： jishi 示例\博客\manage.jsh
pause