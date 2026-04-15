@echo off
echo Starting NeuroGuard Microservices and Frontend...

echo Starting Eureka Server...
start "Eureka Server" cmd /k "cd /d neuroguard-backend\eureka-server && mvn spring-boot:run"

echo Waiting 10 seconds for Eureka to initialize...
timeout /t 10 /nobreak >nul

echo Starting Gateway Service...
start "Gateway Service" cmd /k "cd /d neuroguard-backend\gateway && mvn spring-boot:run"

echo Starting User Service...
start "User Service" cmd /k "cd /d neuroguard-backend\user-service && mvn spring-boot:run"

echo Starting Consultation Service...
start "Consultation Service" cmd /k "cd /d neuroguard-backend\consultation-service && mvn spring-boot:run"

echo Starting Assurance Service...
start "Assurance Service" cmd /k "cd /d neuroguard-backend\assurance-service && mvn spring-boot:run"

echo Starting reservation Service...
start "reservation Service" cmd /k "cd /d neuroguard-backend\reservation-service && mvn spring-boot:run"



echo Starting medical history Service...
start "medical-history-service" cmd /k "cd /d neuroguard-backend\medical-history-service && mvn spring-boot:run"


echo Starting ml predictor Service...
start "ml-predictor-service" cmd /k "cd /d neuroguard-backend\ml-predictor-service && mvn spring-boot:run"


echo Starting risk-assessment-service...
start "risk-assessment-service" cmd /k "cd /d neuroguard-backend\risk-assessment-service && mvn spring-boot:run"



echo Starting Angular Frontend...
start "Angular Frontend" cmd /k "cd /d FrontEnd && ng serve"

echo All services have been launched in separate windows!
pause
