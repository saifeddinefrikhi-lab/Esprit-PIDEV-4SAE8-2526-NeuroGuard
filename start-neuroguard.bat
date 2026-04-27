@echo off
TITLE NeuroGuard Microservices Starter
COLOR 0B

echo ======================================================
echo          NeuroGuard Microservices Starter
echo ======================================================
echo.

:: Check for Maven wrapper
echo [*] Checking environment...
where mvnw.cmd >nul 2>nul
if %errorlevel% neq 0 (
    echo [!] mvnw.cmd not found in root, will check subdirectories.
)

:: Start Eureka Server first
echo [1/7] Starting Eureka Server (Port: 8761)...
start "Eureka Server" cmd /k "cd eureka-server && mvnw.cmd spring-boot:run"
echo Waiting for Eureka Server to initialize (15s)...
timeout /t 15 /nobreak > nul

:: Start Gateway
echo [2/7] Starting API Gateway (Port: 8083)...
start "API Gateway" cmd /k "cd gateway && mvnw.cmd spring-boot:run"
timeout /t 5 /nobreak > nul

:: Start User Service
echo [3/7] Starting User Service (Port: 8081)...
start "User Service" cmd /k "cd user-service && mvnw.cmd spring-boot:run"

:: Start Monitoring Service
echo [4/7] Starting Monitoring Service (Port: 8085)...
start "Monitoring Service" cmd /k "cd monitoring-service && mvnw.cmd spring-boot:run"

:: Start Wellbeing Service
echo [5/7] Starting Wellbeing Service (Port: 8084)...
start "Wellbeing Service" cmd /k "cd wellbeing-ms\wellbeing-service && mvnw.cmd spring-boot:run"

:: Start NeuroGuard Backend
echo [6/7] Starting NeuroGuard Backend (Port: 8081)...
echo [!] Note: This might conflict with User Service if both use port 8081.
start "NeuroGuard Backend" cmd /k "cd NeuroGuard\neuroguard-backend && mvnw.cmd spring-boot:run"

:: Start Frontend
echo [7/7] Starting Frontend (Angular - Port: 4200)...
start "Frontend" cmd /k "cd NeuroGuard\FrontEnd && npm start"

echo.
echo ======================================================
echo    All services are being started in separate windows.
echo    Please check each window for startup status.
echo ======================================================
echo.
pause
