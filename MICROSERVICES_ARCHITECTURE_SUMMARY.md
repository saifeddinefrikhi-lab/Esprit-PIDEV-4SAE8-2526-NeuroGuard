# NeuroGuard Microservices Architecture Summary

## Overview
NeuroGuard is a Spring Cloud microservices-based healthcare platform with service discovery via Eureka, API gateway routing, and multiple specialized services for user management, medical records, consultations, assurance, reservations, and risk alerts.

---

## 1. SERVICE PORTS AND NAMES

| Service | Port | Application Name (Eureka) | Status |
|---------|------|--------------------------|--------|
| **Eureka Server** | 8761 | eureka-server | Discovery Service (Non-client) |
| **API Gateway** | 8083 | gateway | Central Routing |
| **User Service** | 8081 | user-service | Microservice |
| **Consultation Service** | 8082 | consultation-service | Microservice |
| **Medical History Service** | 8082 | medical-history-service | Microservice |
| **Assurance Service** | 8086 | assurance-service | Microservice |
| **Reservation Service** | 8087 | reservation-service | Microservice |
| **Risk Alert Service** | 8084 | risk-alert-service | Microservice |
| **ML Predictor Service** | 5000 | (Python Flask) | ML Pipeline (Not Java) |

**Note:** Medical History Service and Consultation Service share port 8082; they should use different ports in production.

---

## 2. DATABASE CONFIGURATIONS

### MySQL Databases

| Service | Database Name | URL | Credentials | DDL Strategy |
|---------|---------------|-----|-------------|--------------|
| User Service | `userdb` | jdbc:mysql://localhost:3306/userdb | root / (empty) | update |
| Consultation Service | `consultation_db` | jdbc:mysql://localhost:3306/consultation_db | root / (empty) | update |
| Medical History Service | `medical_history_db` | jdbc:mysql://localhost:3306/medical_history_db | root / (empty) | update |
| Assurance Service | `neuroguard_db` | jdbc:mysql://localhost:3306/neuroguard_db | root / (empty) | update |
| Reservation Service | `reservationdb` | jdbc:mysql://localhost:3306/reservationdb | root / (empty) | update |
| Risk Alert Service | `risk_alert_db` | jdbc:mysql://localhost:3306/risk_alert_db | root / (empty) | update |

### Additional Data Sources

| Service | Type | Configuration |
|---------|------|----------------|
| Consultation Service | Redis Cache | localhost:6379, DB=0, Max Pool=8, Timeout=60s |
| ML Predictor Service | Python Data | Data subdirectory (local file-based) |

#### Database Features
- **Auto-creation:** All MySQL databases use `createDatabaseIfNotExist=true`
- **SSL:** Disabled (`useSSL=false`)
- **Timezone:** UTC
- **Hibernate DDL:** All use `ddl-auto: update` (schema auto-evolution)
- **SQL Logging:** Enabled on all services (for debugging)

---

## 3. SERVICE DEPENDENCIES (SERVICE-TO-SERVICE INTERACTIONS)

### Dependency Graph

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8083)                        │
│                  (Spring Cloud Gateway)                      │
└─────────────────────────────────────────────────────────────┘
        │
        ├──> /auth/** ────────────────> User Service (8081)
        ├──> /users/** ─────────────────> User Service (8081)
        │
        ├──> /api/consultations/** ───> Consultation Service (8082)
        ├──> /api/availability/** ────> Consultation Service (8082)
        ├──> /api/providers/** ───────> Consultation Service (8082)
        ├──> /api/distance/** ────────> Consultation Service (8082)
        │
        ├──> /api/patient/medical-history/** ──> Medical History Service (8082)
        ├──> /api/provider/medical-history/** ─> Medical History Service (8082)
        ├──> /api/caregiver/medical-history/** > Medical History Service (8082)
        ├──> /files/** ──────────────> Medical History Service (8082)
        ├──> /test ──────────────────> Medical History Service (8082)
        │
        ├──> /api/assurances/** ────> Assurance Service (8086)
        │
        └──> /api/reservations/** ──> Reservation Service (8087)
            

User Service (8081)
    ├─ Cannot authenticate other services (no Feign client visible)
    └─ Role: Authentication/Authorization provider

Consultation Service (8082) - WITH FEIGN
    ├─ ✓ Can call User Service (http://localhost:8081)
    ├─ ✓ Can call ML Predictor Service (http://localhost:5000)
    ├─ ✓ Uses Redis for caching
    └─ Role: Provider search, distance calculation, real-time location tracking

Medical History Service (8082) - WITH FEIGN
    ├─ ✓ Can call User Service (http://localhost:8081)
    ├─ ✓ Uses Azure Blob Storage (external)
    └─ Role: Medical records management with file storage

Assurance Service (8086) - WITH FEIGN
    ├─ ✓ Can call User Service (via JWT verification)
    ├─ ✓ Can call ML Predictor Service (http://localhost:5000)
    ├─ ✓ Uses Email/SMS for notifications (Twilio + Gmail SMTP)
    └─ Role: Insurance/assurance management with async processing

Reservation Service (8087) - WITH FEIGN
    ├─ ✓ Uses JWT for authentication
    └─ Role: Appointment/reservation management

Risk Alert Service (8084) - WITH FEIGN
    ├─ ✓ Can call ML Predictor Service (http://localhost:5000)
    ├─ ✓ Uses scheduler for periodic alerts
    └─ Role: Monitor patient risk and trigger alerts

ML Predictor Service (Python, Port 5000)
    └─ Provides: Alzheimer's prediction, health risk assessment
```

### Feign Client Usage
Services with **@EnableFeignClients**:
- **Consultation Service** - Calls User Service for authentication
- **Medical History Service** - Calls User Service for data validation
- **Assurance Service** - Calls User Service & ML Predictor Service
- **Reservation Service** - May have feign clients configured
- **Risk Alert Service** - Calls ML Predictor Service for predictions

---

## 4. KEY FEATURES AND RESPONSIBILITIES

### **EUREKA SERVER (Port 8761)**
**Purpose:** Service Discovery & Registration Center

**Features:**
- Central registry for all microservices
- Self-preservation disabled for development
- Endpoints exposed: `/health`, `/info`
- **Authentication:** No client registration required (register-with-eureka: false, fetch-registry: false)

**Spring Dependencies:**
- `spring-cloud-starter-netflix-eureka-server`

---

### **API GATEWAY (Port 8083) - Gateway Service**
**Purpose:** Single entry point, request routing, CORS handling

**Features:**
- **CORS Configuration:**
  - Allowed Origins: `http://localhost:4200` (Angular Frontend)
  - Methods: GET, POST, PUT, DELETE, OPTIONS
  - Credentials: Enabled
  - Headers: All allowed (**)

- **Route Configuration (11 routes defined):**
  - Load balancer aware (using `lb://` scheme with Eureka discovery)
  - Prefix-based path routing
  - Debug logging enabled (Gateway & Netty)

- **Request Format:** Logs requests/responses for debugging

**Spring Dependencies:**
- `spring-cloud-starter-gateway`
- `spring-cloud-starter-netflix-eureka-client`
- `spring-boot-starter-actuator`

---

### **USER SERVICE (Port 8081)**
**Purpose:** Authentication, authorization, user management

**Key Endpoints:**
- `/auth/login` - Returns JWT token
- `/auth/register` - User registration
- `/auth/logout` - Token invalidation
- `/users/**` - User CRUD operations

**Features:**
- **Security:**
  - Spring Security enabled
  - JWT-based authentication (Secret: `XDkzF2YNPA/7vXmPYJmaACjY6VBhwHJbr4pzPF5jguE=`)
  - Default user: `admin/admin123` (ADMIN role)

- **Database:** MySQL (`userdb`)
  - Schema auto-evolution (update mode)

- **Responsibilities:**
  - User registration/login
  - JWT token generation & validation
  - Password management
  - User role management (at least ADMIN role)

**Spring Dependencies:**
- Web, Security, Data-JPA, Validation
- `spring-cloud-starter-netflix-eureka-client`
- `java-jwt` (com.auth0)

---

### **CONSULTATION SERVICE (Port 8082)**
**Purpose:** Provider consultations, availability, real-time location tracking, distance calculation

**Key Endpoints:**
- `/api/consultations/**` - Consultation booking/management
- `/api/availability/**` - Provider availability slots
- `/api/providers/**` - Provider search with filters
- `/api/distance/**` - Distance matrix calculations
- `/api/location` - Real-time location tracking (WebSocket)

**Features:**
- **Caching:** Redis (Database 0, TTL=120 min for distance calculations)
- **Location Services:**
  - Provider: Nominatim (OpenStreetMap-based geocoding)
  - Distance Matrix: Google Maps API (with caching)
  - Modes: DRIVING, WALKING, TRANSIT, BICYCLING
  - WebSocket streaming enabled (cache TTL=300s, broadcast=5000ms)

- **File Upload:** Max 10MB per request/file

- **Dependencies:**
  - Calls User Service for authentication
  - Validates JWT tokens

- **Database:** MySQL (`consultation_db`)

**Spring Dependencies:**
- Web, Security, Data-JPA, Actuator
- `spring-cloud-starter-openfeign` (Feign clients for dependencies)
- `spring-cloud-starter-netflix-eureka-client`
- Redis integration

---

### **MEDICAL HISTORY SERVICE (Port 8082)**
**Purpose:** Patient medical records management with file storage

**Key Endpoints:**
- `/api/patient/medical-history/**` - Patient health records
- `/api/provider/medical-history/**` - Provider view of patient history
- `/api/caregiver/medical-history/**` - Caregiver view of patient history
- `/files/**` - Medical file upload/download
- `/test` - Health check endpoint
- `/admin/migration/**` - Data migration admin tools

**Features:**
- **Cloud Storage:**
  - Azure Blob Storage integration (configured but uses env variables)
  - Container name: `medical-records`
  - Configurable CDN endpoint

- **File Management:**
  - Max upload: 10MB per file
  - Local upload directory: `uploads/medical-history/`

- **Email Integration:**
  - Gmail SMTP (`smtp.gmail.com:587`)
  - User: `saifeddinefrikhi@gmail.com`
  - Purpose: Notifications

- **Async Operations:** Enabled (@EnableAsync)

- **Dependencies:**
  - Calls User Service via Feign for authentication

- **Database:** MySQL (`medical_history_db`)

**Spring Dependencies:**
- Web, Security, Data-JPA, Actuator, Mail
- `spring-cloud-starter-openfeign`
- `spring-cloud-starter-netflix-eureka-client`
- Custom DotenvInitializer for .env file loading

---

### **ASSURANCE SERVICE (Port 8086)**
**Purpose:** Insurance/assurance policies, risk assessments, claim management

**Key Endpoints:**
- `/api/assurances/**` - Insurance policies/claims management
- Assurance reports and analysis

**Features:**
- **Notifications:**
  - Email (Gmail SMTP with TLS - configurable via env vars)
  - SMS (Twilio integration - configurable via env vars)

- **Template Engine:**
  - Thymeleaf for HTML email templates
  - Templates directory: `classpath:/templates/`

- **Async & Scheduling:** Enabled (@EnableAsync, @EnableScheduling)
  - Periodic task execution for report generation

- **Dependencies:**
  - Calls User Service for authentication
  - Calls ML Predictor Service for risk assessment (http://localhost:5000)

- **Database:** MySQL (`neuroguard_db`)

**Spring Dependencies:**
- Web, Security, Data-JPA, Actuator
- `spring-cloud-starter-openfeign`
- `spring-cloud-starter-netflix-eureka-client`
- `java-jwt` for token validation

---

### **RESERVATION SERVICE (Port 8087)**
**Purpose:** Appointment & reservation management

**Key Endpoints:**
- `/api/reservations/**` - Reservation CRUD operations

**Features:**
- **Authentication:** JWT-based with same secret as other services
- **Minimalist Design:** Focused exclusively on reservation operations
- **Database:** MySQL (`reservationdb`)

**Spring Dependencies:**
- Web, Security, Data-JPA
- `spring-cloud-starter-openfeign` (feign clients enabled)
- `spring-cloud-starter-netflix-eureka-client`

---

### **RISK ALERT SERVICE (Port 8084)**
**Purpose:** Patient risk monitoring and automated alerts

**Features:**
- **ML Integration:**
  - Calls Python ML Predictor Service (http://localhost:5000)
  - Analyzes risk patterns and triggers alerts

- **Scheduling:** Enabled (@EnableScheduling)
  - Periodic risk assessments at configured intervals

- **Notifications:**
  - Likely sends alerts via other services (Email/SMS integration pending)

- **JWT Security:** Uses shared secret for verification

- **Database:** MySQL (`risk_alert_db`)

**Spring Dependencies:**
- Data-JPA, Actuator, Security
- `spring-cloud-starter-openfeign`
- `spring-cloud-starter-netflix-eureka-client`

---

### **ML PREDICTOR SERVICE (Port 5000) - Python Flask**
**Purpose:** Machine learning predictions for Alzheimer's disease and patient risk assessment

**Key Files/Endpoints:**
- `app.py` - Main Flask application
- `alzheimers_app.py` - Alzheimer's-specific prediction model
- `feature_extraction.py` - Feature engineering
- Training models for health risk assessment

**Features:**
- **Models Supported:**
  - Alzheimer's prediction
  - General health risk assessment

- **Data Management:**
  - `data/` directory for training datasets
  - `generate_training_data.py` - Synthetic training data generation
  - Mock model support for testing

- **Integration:**
  - Called by Assurance Service for risk scoring
  - Called by Risk Alert Service for patient monitoring
  - Provides prediction API endpoints

- **Requirements:** Python dependencies in `requirements.txt`

---

## 5. AUTHENTICATION & AUTHORIZATION FLOW

### JWT Configuration
**Shared Secret:** `XDkzF2YNPA/7vXmPYJmaACjY6VBhwHJbr4pzPF5jguE=`

**Flow:**
1. User logs in via `/auth/login` (User Service)
2. User Service returns JWT token
3. Frontend includes token in Authorization header: `Bearer <token>`
4. Gateway passes token to backend services
5. Each service validates JWT using shared secret
6. Services enforce role-based access control (RBAC)

### Default Credentials
- Username: `admin`
- Password: `admin123`
- Role: `ADMIN`

---

## 6. EXTERNAL INTEGRATIONS

| Integration | Service | Configuration |
|-------------|---------|----------------|
| **Google Maps** | Consultation Service | API Key: `AIzaSyCmUOklXWRUK52Ijlqcmz8IiDmaYUWPkLo` |
| **Nominatim Geocoding** | Consultation Service | User-Agent: `NeuroGuard-ConsultationService/1.0` |
| **Gmail SMTP** | Medical History Service | `smtp.gmail.com:587`, user: `saifeddinefrikhi@gmail.com` |
| **Gmail SMTP** | Assurance Service | Configurable via env vars (MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD) |
| **Azure Blob Storage** | Medical History Service | Connection string from env var: `AZURE_STORAGE_CONNECTION_STRING` |
| **Twilio SMS** | Assurance Service | Account SID & Auth Token from env vars (TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN) |
| **Redis Cache** | Consultation Service | localhost:6379, Database 0 |

---

## 7. CROSS-CUTTING CONCERNS

### Logging Configuration
- **Eureka Server:** Minimal (actuator endpoints only)
- **Gateway:** DEBUG level for Spring Cloud Gateway & Reactor Netty
- **User Service:** Default Spring logging
- **Consultation Service:** DEBUG for Spring Security
- **Medical History Service:** DEBUG for security
- **Assurance Service:** DEBUG for security, Hibernate SQL, JWT filters
- **Risk Alert Service:** DEBUG for service package

### Actuator Endpoints
- **Available on All Microservices**
  - `/actuator/health` - Service health status
  - `/actuator/info` - Service information
  - Others based on Spring Boot version

### Spring Boot & Cloud Versions
| Service | Spring Boot | Spring Cloud |
|---------|-------------|--------------|
| Eureka Server | 3.2.4 | 2023.0.1 |
| Gateway | 3.5.11 | 2025.0.1 |
| User Service | 3.2.4 | 2023.0.1 |
| Consultation Service | 3.2.4 | 2023.0.1 |
| Medical History Service | 3.2.4 | 2023.0.1 |
| Assurance Service | 3.2.4 | 2023.0.1 |
| Reservation Service | 3.2.4 | 2023.0.1 |
| Risk Alert Service | 3.2.4 | 2023.0.1 |

---

## 8. DEPLOYMENT & STARTUP SEQUENCE

**Recommended startup order:**
1. Eureka Server (Port 8761) - Service registry
2. API Gateway (Port 8083) - Routes
3. User Service (Port 8081) - Auth dependency
4. Consultation Service (Port 8082) - Primary service
5. Medical History Service (Port 8082) - **CONFLICT**: Change to 8085 or 8090
6. Assurance Service (Port 8086) - Risk assessment service
7. Reservation Service (Port 8087) - Booking service
8. Risk Alert Service (Port 8084) - Monitoring
9. ML Predictor Service (Port 5000) - Python service

**Critical Issue:** Consultation Service and Medical History Service both use port 8082 - **PORT CONFLICT MUST BE RESOLVED BEFORE DEPLOYMENT**

---

## 9. POTENTIAL IMPROVEMENTS & RECOMMENDATIONS

### High Priority
1. **Port Conflict:** Change Medical History Service port from 8082 to 8085
2. **Database Passwords:** Implement secure password management (currently blank)
3. **API Key Security:** Move Google Maps & ML endpoint URLs to secure configuration
4. **Service Discovery:** Use Eureka hostname resolution instead of hardcoded localhost

### Medium Priority
1. **Redis Configuration:** Add connection pooling tuning for production
2. **Logging:** Implement centralized logging (ELK stack)
3. **Monitoring:** Add Prometheus/Grafana metrics collection
4. **API Documentation:** Implement Swagger/Springdoc for each service

### Low Priority
1. **Version Consistency:** Align Spring Cloud versions (currently mixed 2023.0.1 vs 2025.0.1)
2. **Caching Strategy:** Evaluate caching at API Gateway level
3. **Load Balancing:** Add load balancer configuration for high availability

---

## 10. CONFIGURATION CHECKLIST FOR LOCAL DEVELOPMENT

- [ ] MySQL Server running on localhost:3306 with root user, no password
- [ ] Redis server running on localhost:6379
- [ ] Eureka Server started (Port 8761)
- [ ] Change Medical History Service port to avoid conflicts
- [ ] Copy `.env` file with Azure credentials (if using Azure Blob)
- [ ] Verify Twilio credentials in `.env` (if SMS notifications needed)
- [ ] Python 3.8+ installed for ML Predictor Service
- [ ] pip install -r requirements.txt (in ml-predictor-service)
- [ ] Frontend at `http://localhost:4200` for CORS origin
- [ ] All services should auto-register with Eureka after startup

---

**Generated:** 2026-04-14
**Project:** NeuroGuard
**Architecture:** Spring Cloud Microservices with Service Discovery
