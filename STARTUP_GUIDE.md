# NeuroGuard - Corrected Startup Guide

## Pre-Startup Checklist

- [ ] Java 17+ installed: `java -version`
- [ ] Maven installed: `mvn --version`
- [ ] Node.js & npm installed: `node --version && npm --version`
- [ ] Angular CLI installed: `ng --version`
- [ ] All ports free: 8761 (Eureka), 8082 (User), 8083 (Medical), 8084 (Alert), 8080 (Gateway), 4200 (Angular)

## Step 1: Set JWT Secret (Optional)

### Windows (PowerShell)
```powershell
$env:JWT_SECRET = "your-secret-key-at-least-32-characters-long-must-be-same-for-all-services"
```

### Windows (Command Prompt)
```cmd
set JWT_SECRET=your-secret-key-at-least-32-characters-long-must-be-same-for-all-services
```

### Linux/Mac
```bash
export JWT_SECRET="your-secret-key-at-least-32-characters-long-must-be-same-for-all-services"
```

**Note:** If JWT_SECRET is not set, services will use the default development secret defined in application.yml.

## Step 2: Start Backend Services

Open separate terminal windows for each service (don't combine commands):

### Terminal 1: Eureka Server (Registry)
```bash
cd /path/to/neuroguard-backend/eureka-server
mvn spring-boot:run
# Wait for: "Eureka Server started on port 8761"
```

### Terminal 2: User Service (Auth & JWT)
```bash
cd /path/to/neuroguard-backend/user-service
mvn spring-boot:run
# Wait for: "Started UserServiceApplication"
```

### Terminal 3: Medical History Service (Data)
```bash
cd /path/to/neuroguard-backend/medical-history-service
mvn spring-boot:run
# Wait for: "Started MedicalHistoryServiceApplication"
```

### Terminal 4: Risk Alert Service (Alerts)
```bash
cd /path/to/neuroguard-backend/risk-alert-service
mvn spring-boot:run
# Wait for: "Started RiskAlertServiceApplication"
```

### Terminal 5: API Gateway (Proxy)
```bash
cd /path/to/neuroguard-backend/gateway
mvn spring-boot:run
# Wait for: "Started GatewayApplication"
```

### Terminal 6: ML Predictor Service (Python)
```bash
cd /path/to/neuroguard-backend/ml-predictor-service
python app.py
# Wait for: "Running on http://127.0.0.1:5000"
```

### Terminal 7: Angular Frontend
```bash
cd /path/to/FrontEnd
npm install  # First time only
ng serve
# Wait for: "Application bundle generation complete"
# Open browser to: http://localhost:4200
```

## Step 3: Verify All Services Running

Check each in browser or terminal:

```bash
# Eureka Dashboard (verify all services registered)
http://localhost:8761/

# User Service Health
curl http://localhost:8082/actuator/health

# Medical History Service Health
curl http://localhost:8083/actuator/health

# Alert Service Health
curl http://localhost:8084/actuator/health

# Gateway Health
curl http://localhost:8080/actuator/health

# Frontend
http://localhost:4200
```

## Step 4: Test All Functions

### 4.1 Authentication
1. Go to http://localhost:4200
2. Click "Login" (or Register new account)
3. Use test credentials:
   - **Username:** provider1
   - **Password:** password
4. Should redirect to provider dashboard

### 4.2 Create Medical History
1. Navigate to "Medical History"
2. Click "Add New Medical History"
3. Select a patient from dropdown
4. Fill in diagnosis and risk factors
5. Select caregivers to assign
6. Click "Create"
7. Should show success message
8. History should appear in list

### 4.3 Test Alert Generation
1. Go to "Alerts" section
2. Select a patient
3. Click "Generate Alerts" button
4. Should show "Alert generation triggered"
5. Within seconds, alerts should appear
6. Toast notification should show in bottom-right

### 4.4 Test Alert Management
1. Click an alert to see details
2. Click "Delete" -> should remove alert
3. Create manual alert:
   - Click "Create Alert"
   - Fill message and severity
   - Click "Create"
   - Should appear immediately

## Automated Startup (Windows .bat file)

The included `start-neuroguard.bat` automates this:

```bash
cd /path/to/NeuroGuard
start-neuroguard.bat
```

This will:
1. Start Eureka Server
2. Wait 10 seconds
3. Start Gateway
4. Start User Service
5. Start Medical History Service
6. Start Risk Alert Service
7. Start ML Predictor Service
8. Start Angular Frontend
9. Open all in new terminal windows

## Troubleshooting Startup

### "Port already in use"
```bash
# Find process on port (example: 8083)
# Windows
netstat -ano | findstr :8083
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8083
kill -9 <PID>
```

### "Service not registering with Eureka"
- Check Eureka dashboard: http://localhost:8761/
- If not showing, check service logs for "Registered with Eureka"
- Verify eureka.client.service-url.default-zone in application.yml

### "JWT validation failed"
- Verify JWT_SECRET environment variable is set (or using default)
- Check all services using same secret
- Look for "JWT verification failed" in logs

### "WebSocket connection failed"
- Check gateway is routing /ws/alerts to alert service
- Verify alert service WebSocket endpoint is enabled
- Check browser console for STOMP connection errors

## Monitoring Services

### View All Logs
```bash
# Watch logs in real-time (continue all terminal outputs)
# Most useful: grep for ERROR or WARN
# Search for: "ERROR", "WARN", "JWT", "STOMP", "CORS"
```

### Check Service Status
```bash
# Terminal where services are running, should see:
# 1. Started ServiceApplication
# 2. Registered with Eureka
# 3. WebSocket enabled (for alert service)

# If any service fails to start:
# - Check port conflict
# - Check JWT secret configuration
# - Check database connectivity (H2 in-memory)
```

## Production Deployment Notes

Before deploying to production:

1. **Change JWT Secret**
   - Generate strong random secret (>32 characters)
   - Set JWT_SECRET environment variable
   - Use HTTPS (not HTTP)

2. **Configure Database**
   - Migrate from H2 to PostgreSQL/MySQL
   - Set database connection strings via environment

3. **Configure Email**
   - Set SMTP credentials for notifications
   - Configure sender email address

4. **Configure Azure (Optional)**
   - Set AZURE_STORAGE_ENABLED=true
   - Set AZURE_STORAGE_CONNECTION_STRING

5. **Review Security**
   - Enable CORS only for trusted domains
   - Implement rate limiting
   - Add API authentication keys for services

6. **Logging**
   - Configure centralized logging (ELK, Splunk, etc.)
   - Set appropriate log levels

## Next Steps

After successful startup:

1. ✅ Create provider and patient accounts
2. ✅ Create medical histories for patients
3. ✅ Generate alerts and test delivery
4. ✅ Test all CRUD operations
5. ✅ Verify WebSocket alerts appear in real-time
6. ✅ Test caregiver and patient views

See `FIX_DOCUMENTATION.md` for detailed information about all fixes applied.
See `TROUBLESHOOTING.md` for common issues and solutions.
