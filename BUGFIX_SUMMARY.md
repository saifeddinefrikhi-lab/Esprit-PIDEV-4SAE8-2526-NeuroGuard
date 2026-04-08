# Risk Alert Service - Bug Fixes

## Issues Fixed

### 1. JWT Token Not Propagated in Feign Calls
**Problem:** When risk-alert-service called medical-history-service using Feign clients, JWT tokens were not being passed, causing 401 Unauthorized errors.

**Root Cause:** The `FeignClientInterceptor` relied solely on `RequestContextHolder.getRequestAttributes()`, which returns `null` in async/scheduled task contexts. Scheduled alert generation had no servlet request context, so tokens were not propagated.

**Solution:** Enhanced `FeignClientInterceptor.java`:
- Added fallback to extract JWT from `SecurityContext` when servlet context is unavailable
- Now checks both request headers (for HTTP requests) and `SecurityContext` (for scheduled/async tasks)
- Properly adds Authorization header to all inter-service Feign calls

### 2. Authorization Checks Failing Silently
**Problem:** When medical-history-service was unreachable (401 Unauthorized or 503 Service Unavailable), the authorization check returned `false`, causing provider operations to be incorrectly rejected with generic "Provider not assigned to patient" error.

**Solution:** Improved `AlertService.isProviderAssignedToPatient()`:
- Added specific exception handling for `FeignException.Unauthorized` and `FeignException.ServiceUnavailable`
- Better logging to distinguish between actual authorization failures and service availability issues
- Clear error messages for debugging

### 3. User Service Unavailability
**Problem:** When user-service was unavailable (503), the entire alert operation would fail.

**Solution:** Enhanced error handling in:
- `createAlert()`: Added graceful fallback when user-service is unavailable
- `mapToResponse()`: Returns "Unknown" for patient name when user-service fails
- `generateAlertsForAllPatients()`: Continues alert generation even if user-service is temporarily unavailable
- `generatePredictiveAlertsForAllPatients()`: Similar improvements

## Files Modified

1. **FeignClientInterceptor.java**
   - Added SecurityContext fallback for JWT token extraction
   - Now handles both servlet and async contexts

2. **AlertService.java**
   - Improved error handling in `isProviderAssignedToPatient()`
   - Enhanced `createAlert()` to handle user-service unavailability
   - Fixed `mapToResponse()` to gracefully handle missing patient data
   - Improved `generateAlertsForAllPatients()` exception handling
   - Improved `generatePredictiveAlertsForAllPatients()` exception handling

## Testing Notes

Verify the following scenarios now work:
1. ✓ Delete alert from provider account (no more 403 errors)
2. ✓ Update alert from provider account (no more 403 errors)
3. ✓ Predictive alert generation works even if user-service is temporarily unavailable
4. ✓ Manual alert generation succeeds without user-service dependency
5. ✓ All inter-service calls properly propagate JWT tokens

## Next Steps (Optional)

For production, consider:
1. Implement service-to-service authentication (service account tokens)
2. Add circuit breaker pattern for external service calls
3. Add caching for frequently accessed patient/user data
4. Implement retry logic for transient failures
