# NeuroGuard - Complete Fix Documentation

## Issues Fixed

### 1. **401 Unauthorized Error** ✅
**Problem:** Medical history creation and alert operations were failing with 401 Unauthorized errors.

**Root Cause:** JWT authentication filter couldn't validate tokens because the `jwt.secret` property was not configured in the backend services.

**Solution Applied:**
- Created `application.yml` files for all backend services with JWT secret configuration
- Updated all services to use the same `JWT_SECRET` environment variable
- Added fallback default secret for development (must be changed in production)

**Files Modified:**
- `neuroguard-backend/user-service/src/main/resources/application.yml` (created)
- `neuroguard-backend/medical-history-service/src/main/resources/application.yml` (created)
- `neuroguard-backend/risk-alert-service/src/main/resources/application.yml` (created)

### 2. **403 Forbidden Error** ✅  
**Problem:** Alert deletion and creation operations were returning 403 Forbidden errors.

**Root Cause:** Authorization checks were failing because:
- User ID wasn't being properly extracted from JWT tokens
- Provider assignment checks couldn't access medical history

**Solution Applied:**
- Added `getUserIdFromToken()` method to user-service JwtUtils for consistency
- Ensured all services use the same JWT secret (configured in application.yml files)
- Verified authorization logic in AlertService and MedicalHistoryService is correct

**Files Modified:**
- `neuroguard-backend/user-service/src/main/java/com/neuroguard/userservice/security/JwtUtils.java`

### 3. **SASS Deprecation Warnings** ✅
**Problem:** Multiple build warnings about deprecated SASS functions and @import usage.

**Root Cause:** 
- Using deprecated `map-get()` function instead of `map.get()` from `sass:map` module
- Using deprecated `@import` rules instead of `@use` statements

**Solution Applied:**
- Updated `preset-style.scss` to:
  - Use `@use 'sass:map'` module
  - Replace `map-get()` with `map.get()`
  - Convert `@import` to `@use ... as *`
- Updated `styles.scss` to:
  - Convert all SCSS `@import` statements to `@use ... as *`
  - Keep CSS @import for external stylesheets (tabler-icons.min.css)

**Files Modified:**
- `FrontEnd/src/scss/themes/preset-style.scss`
- `FrontEnd/src/styles.scss`

### 4. **Auth Interceptor Improvements** ✅
**Problem:** Token wasn't being properly validated and cleared on 401 errors.

**Solution Applied:**
- Enhanced HTTP Interceptor to:
  - Log token addition to requests
  - Catch 401 errors and clear stored token
  - Redirect to login on 401 unauthorized
  - Provide better error handling

**Files Modified:**
- `FrontEnd/src/app/core/interceptors/auth.interceptor.ts`

## How Alert Generation Works

### Rule-Based Alerts
- **Trigger:** Manual via provider button or scheduled every 6 hours
- **Endpoint:** `POST /api/provider/alerts/generate`
- **Rules:** Check progression stage, age, allergies, comorbidities, cognitive scores, behavioral issues
- **Broadcast:** Via WebSocket to `/topic/alerts/patient/{patientId}` and `/topic/alerts/provider`

### ML Predictive Alerts
- **Trigger:** Manual via provider button or scheduled every 12 hours  
- **Endpoint:** `POST /api/provider/alerts/generate-predictive`
- **Process:** Calls ML service to assess hospitalization risk
- **Broadcast:** Via WebSocket with risk level assessment

### Caregiver & Patient Alerts
- **Caregiver:** Can view alerts for assigned patients via WebSocket subscription
- **Patient:** Receives own alerts via WebSocket at `/topic/alerts/patient/{patientId}`

## How Medical History CRUD Works

### Create
1. Provider creates medical history via form
2. System validates patient assignment
3. Medical history saved with provider ID added automatically
4. Caregivers assigned via form input
5. Email notifications sent to patient and caregivers
6. Auto-alerts generated based on diagnosis

### Read
- Provider can view medical histories they created/assigned to
- Caregiver can view assigned patient histories
- Patient can view their own history
- Authorization checked via role and assignment

### Update
1. Provider updates medical history
2. System verifies provider is still assigned to patient
3. History updated with new data
4. Email notifications sent
5. Alert rules re-evaluated

### Delete  
1. Provider requests medical history deletion
2. System verifies authorization
3. Associated Azure Blob files deleted
4. Medical history record removed
5. Confirmation emails sent

## Configuration Required

### Environment Variables (Optional)
```bash
# JWT Secret - shared across all services
JWT_SECRET=your-very-secret-key-at-least-32-characters-long

# Azure Storage (Optional)
AZURE_STORAGE_ENABLED=true
AZURE_STORAGE_CONNECTION_STRING=DefaultEndpointsProtocol=https;...
```

### Default Configuration
If environment variables not set, services will use:
- **JWT Secret:** Default development secret (MUST change for production)
- **Databases:** In-memory H2 databases (reset on restart)
- **Azure Storage:** Disabled

## Testing the Fixes

### Test 401 Error Fix
1. Start all backend services: `cd neuroguard-backend && mvn spring-boot:run` (in separate terminals)
2. Login in Angular frontend
3. Try creating a medical history
4. Should succeed without 401 errors

### Test 403 Error Fix  
1. After creating medical history, select a patient
2. Trigger alert generation
3. Try deleting an alert
4. Should succeed without 403 errors

### Test SASS Fixes
1. Check Angular build output: `ng serve` or `ng build`
2. Should not see deprecation warnings about @import or map-get

### Test Alert Generation
1. Create medical history for a patient
2. Click "Generate Alerts" button
3. Should see alerts appear in sidebar within seconds
4. Toast notifications should appear in bottom-right corner

## Known Limitations & Next Steps

1. **Database:** Currently using in-memory H2 (data lost on restart)
   - Recommend: Migrate to PostgreSQL for persistence

2. **Email Notifications:** Currently requires configuration
   - Configure SMTP settings for production

3. **Azure Blob Storage:** Optional, configure for production

4. **JWT Token Expiration:** 24 hours (may be too short for testing)
   - Adjust in JwtUtils.generateJwtToken() if needed

## Recovery Guidelines

### If 401 Errors Continue
1. Verify `JWT_SECRET` environment variable is set (or using default)
2. Check all services are running
3. Clear browser localStorage and login again
4. Check backend logs for JWT validation errors

### If 403 Errors Continue  
1. Verify provider is assigned to patient in medical history
2. Check user ID is correctly extracted from JWT
3. Ensure authorization checks pass provider/caregiver checks

### If Alerts Don't Appear
1. Verify WebSocket connection: Check browser console for STOMP messages
2. Verify alert generation was triggered: Check backend logs
3. Verify broadcast is working: Search logs for "broadcastAlert"
4. Verify gateway is routing to correct alert service port (8084)
