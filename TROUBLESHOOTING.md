# NeuroGuard - Quick Troubleshooting Guide

## Issue: 401 Unauthorized when creating medical history

### Quick Fixes
1. **Clear browser cache and login again**
   ```
   localStorage.clear()
   // Then login again
   ```

2. **Verify JWT_SECRET is set**
   ```bash
   # Check if environment variable is set
   echo %JWT_SECRET%  # Windows
   echo $JWT_SECRET   # Linux/Mac
   
   # If empty, service will use default: 
   # "your-secret-key-for-development-change-in-production-at-least-32-characters-long-for-security"
   ```

3. **Check backend service logs**
   - Look for: "JWT Filter - Setting authentication" (success) or "Token validation failed" (failure)
   - If JWT validation fails, check secret key matches across all services

4. **Restart all backend services**
   - Stop all Java processes
   - Restart them in order:
     ```bash
     1. Eureka Server (8761)
     2. User Service (8082)
     3. Medical History Service (8083)
     4. Risk Alert Service (8084)
     5. Gateway (8080)
     ```

## Issue: 403 Forbidden when deleting alerts

### Root Causes & Fixes
1. **Provider not assigned to patient**
   - Ensure provider created the medical history for that patient
   - Or add provider to medical history's provider list

2. **Incorrect user ID in token**
   - Check backend logs for "Extracted userId from token"
   - Should show the correct numeric ID
   - If null, token generation failed

3. **Token expired**
   - Token expires after 24 hours
   - Login again to get fresh token

## Issue: Alerts not appearing

### Checklist
- [ ] Medical history created for patient?
- [ ] Alert generation triggered? (check console logs)
- [ ] WebSocket connected? (Check browser console)
- [ ] Alert generated successfully? (Check backend logs)

### Debug Steps
1. **Check WebSocket connection**
   ```
   Open browser console (F12)
   Look for: "[AlertService] Connected to STOMP over WebSocket"
   If not, WebSocket might not be connecting to gateway
   ```

2. **Verify alert generation endpoint**
   ```
   POST http://localhost:8083/api/provider/alerts/generate
   Should return: "Alert generation triggered"
   Check backend logs for alert generation process
   ```

3. **Check medical history for required fields**
   ```
   Required for any alert:
   - Patient ID
   - Diagnosis or progression stage
   - At least one risk factor (age > 75, MMSE < 18, etc.)
   ```

4. **Test with predictive alerts**
   ```
   POST http://localhost:8083/api/provider/alerts/generate-predictive
   Requires ML service running and medical history
   ```

## Issue: SASS build errors (though should be fixed)

### If warnings still appear
1. **Clear node_modules and reinstall**
   ```bash
   cd FrontEnd
   rm -rf node_modules package-lock.json
   npm install
   ```

2. **Use latest angular-cli**
   ```bash
   npm install -g @angular/cli@latest
   ```

3. **Manual SCSS check**
   - Verify no `@import` statements in preset-style.scss (should use `@use`)
   - Verify no `map-get()` usage (should be `map.get()`)

## Issue: Medical history creation returns 401

### Check These
1. **Is token being sent?**
   - Open Network tab (F12 > Network)
   - Create medical history
   - Check request header: `Authorization: Bearer ...`
   - Should have token

2. **Is token valid?**
   - Medical history service JWT filter should validate
   - If invalid, returns 401 immediately
   - Check backend logs: "JWT token verification successful" or "JWT verification failed"

3. **Is provider assigned to patient?**
   - Provider ID extracted from token
   - But patient must allow this provider
   - Check medical history service logs

## Quick Test Sequence

### 1. Test Authentication
```
1. Login with provider account
2. Should redirect to provider dashboard
3. Check token in localStorage: localStorage.getItem('authToken')
4. Token should be a JWT (three parts separated by dots)
```

### 2. Test Medical History
```
1. Go to Medical History section
2. Click "Add New"
3. Should show patient dropdown
4. Select a patient and fill form
5. Click "Create" -> should succeed
6. Should see new history in list
```

### 3. Test Alert Generation
```
1. Create a medical history first
2. Go to Provider Alerts section
3. Click "Generate Alerts"
4. Should show success message
5. If patient has risk factors, alerts should appear
6. Should see toast notification
```

### 4. Test Alert Management  
```
1. View alert list
2. Try to delete an alert -> should succeed
3. Try to create manual alert -> should succeed
4. Try to resolve alert -> should succeed
```

## Emergency Fixes

### If nothing works - Full reset
```bash
# 1. Stop all services
# 2. Stop Angular dev server
# 3. Clear all data
clear browser cache and localStorage

# 4. Restart in order
cd neuroguard-backend
mvn clean install  # Rebuild all services

# Start in separate terminals:
cd eureka-server && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd medical-history-service && mvn spring-boot:run
cd risk-alert-service && mvn spring-boot:run
cd gateway && mvn spring-boot:run

# 5. Restart frontend
cd FrontEnd && npm install && ng serve

# 6. Login and test
```

## Getting Help

### Check Logs
- **Angular Console:** F12 > Console (most errors shown here)
- **Backend Logs:** Check terminal where service is running
- **Network Errors:** F12 > Network > look for 401/403/500 responses

### Enable Debug Logging
Add to backend application.yml:
```yaml
logging:
  level:
    com.neuroguard: DEBUG  # More verbose logging
```

### Common Error Messages

| Error | Cause | Fix |
|-------|-------|-----|
| "Invalid or expired token" | JWT validation failed | Login again |
| "Provider not assigned" | Authorization check failed | Add provider to medical history |
| "Medical history already exists" | Duplicate creation | Select different patient |
| "Access denied" | Role/permission issue | Check user role and assignments |
| "Service Unavailable" | Backend service not running | Start all services |
