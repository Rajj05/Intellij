@echo off
title Portfolio Manager Launcher

echo [1/4] Starting Backend Server (Port 8080)...
start "Portfolio Backend" cmd /k "cd /d C:\Users\Administrator\Desktop\Intellij_portfolio\Intellij\backend && java -jar target\portfolio-manager-1.0.0.jar"

echo      Waiting for backend to start...
timeout /t 5 /nobreak >nul

echo [2/4] Starting Frontend Server (Port 3000)...
start "Portfolio Frontend" cmd /k "cd /d C:\Users\Administrator\Desktop\Intellij_portfolio\Intellij\frontend && npm start"

echo      Waiting for frontend to start...
timeout /t 3 /nobreak >nul

echo [3/4] Starting Quantum API Server (Port 5000)...
start "Quantum API" cmd /k "cd /d C:\Users\Administrator\Desktop\Intellij_portfolio\Intellij\quantum && python app.py"

echo [4/4] All services started successfully!
echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:3000
echo Quantum:  http://localhost:5000
echo.
pause

