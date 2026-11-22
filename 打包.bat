@echo off
chcp 65001 >nul
cd /d "%~dp0"
setlocal

set "SRC=%~dp0"
set "STAGE=%TEMP%\yza_migrate_stage"
set "ZIP=%SRC%网络服务营造-迁移包.zip"

echo [1/3] 清理旧的临时目录与压缩包 ...
if exist "%STAGE%" rmdir /s /q "%STAGE%"
if exist "%ZIP%" del /q "%ZIP%"
mkdir "%STAGE%"

echo [2/3] 复制（排除 .jishi / 运行时 / 大 wasm / 压缩包）...
robocopy "%SRC%" "%STAGE%" /E /XD "%SRC%.jishi" /XF *.db *.db.log *.lock jishi_wasm.wasm jishi-wasm.js *.zip /NFL /NDL /NJH /NJS /NP >nul

echo [3/3] 压缩 ...
powershell -NoProfile -Command "Compress-Archive -Path '%STAGE%\*' -DestinationPath '%ZIP%' -Force"

echo.
echo 完成！
echo   包： %ZIP%
for %%F in ("%ZIP%") do echo   大小： %%~zF 字节
echo.
echo 把这个 zip 拷到家里电脑，解压后运行 准备环境.bat 即可。
echo （家里需先装好基石 0.3.6）
pause