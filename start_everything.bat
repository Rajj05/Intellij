@echo off
title Portfolio Manager Launcher

:: Get the directory where this script lives
set "BASE_DIR=%~dp0"

echo [1/5] Starting MySQL Server...
start "MySQL Server" /min "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe" --defaults-file="D:\mysql_data\my.ini"

echo      Waiting for MySQL to start...
timeout /t 5 /nobreak >nul

echo [2/5] Starting Backend Server (Port 8080)...
start "Portfolio Backend" cmd /k "cd /d "%BASE_DIR%backend" && java -jar target\portfolio-manager-1.0.0.jar"

echo      Waiting for backend to start...
timeout /t 10 /nobreak >nul

echo [3/5] Starting Frontend Server (Port 3000)...
start "Portfolio Frontend" cmd /k "cd /d "%BASE_DIR%frontend" && set BROWSER=none && npm start"

echo      Waiting for frontend to start...
timeout /t 3 /nobreak >nul

echo [4/5] Starting Quantum API Server (Port 5000)...
start "Quantum API" cmd /k "cd /d "%BASE_DIR%quantum" && python app.py"

echo [5/5] All services started successfully!
echo.
echo MySQL:    localhost:3306
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:3000
echo Quantum:  http://localhost:5000
echo Swagger:  http://localhost:8080/swagger-ui.html
echo.
pause

