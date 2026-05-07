# NeuroGuard Services - Unified Structure Documentation

## Overview
This document outlines the unified code structure and Kubernetes configuration applied to all three microservices: **careplan-service**, **prescription-service**, and **pharmacy-service**.

## Unified Code Structure

### 1. **Utility Classes Pattern**
All three services now follow the same utility pattern:

```
src/main/java/
├── [service]/
│   ├── utils/
│   │   ├── Constants.java      # Centralized configuration constants
│   │   └── ServiceUtils.java   # Reusable utility methods
│   ├── services/
│   ├── repositories/
│   ├── entities/
│   ├── dto/
│   ├── config/
│   ├── controllers/
│   └── ...
```

### 2. **Constants Class**
Each service has a `Constants` class with:

**careplan-service/utils/Constants.java:**
```java
public static final String ROLE_PATIENT = "PATIENT";
public static final String ROLE_PROVIDER = "PROVIDER";
public static final String ROLE_ADMIN = "ADMIN";
public static final String CARE_PLAN_NOT_FOUND = "Care plan not found";
public static final String WEBSOCKET_CARE_PLANS_TOPIC = "/topic/care-plans/";
```

**prescription-service/utils/Constants.java:**
```java
public static final String ROLE_PATIENT = "PATIENT";
public static final String ROLE_PROVIDER = "PROVIDER";
public static final String PRESCRIPTION_NOT_FOUND = "Prescription not found";
public static final String WEBSOCKET_PRESCRIPTIONS_TOPIC = "/topic/prescriptions/";
```

**pharmacy-service/utils/Constants.java:**
```java
public static final String ROLE_PATIENT = "PATIENT";
public static final String ROLE_PHARMACIST = "PHARMACIST";
public static final String MEDICATION_NOT_FOUND = "Medication not found";
public static final String WEBSOCKET_MEDICATIONS_TOPIC = "/topic/medications/";
```

### 3. **ServiceUtils Class**
Each service has reusable utility methods:

```java
public static String extractFullName(UserDto user)
public static String getFullName(String firstName, String lastName)
public static String enumNameOrDefault(Enum<?> enumValue, String defaultValue)
public static boolean isValidString(String value)
```

### 4. **Service Layer Improvements**
All services implement:
- Centralized constant usage (no magic strings)
- Extracted authorization methods
- Proper null handling
- Consistent error messages
- Comprehensive Javadoc

## Unified Kubernetes Structure

### 1. **Directory Structure**
```
neuroguard-backend/k8s/
├── careplan-service/
│   ├── deployment.yaml      ✅ Complete with init container, probes, secrets
│   ├── configmap.yaml       ✅ Full application properties
│   ├── mysql-deployment.yaml
│   ├── mysql-secret.yaml
│   └── namespace.yaml
├── prescription-service/
│   ├── deployment.yaml      ✅ Unified structure (3 replicas)
│   ├── configmap.yaml       ✅ Unified configuration
│   └── secret.yaml          ✅ Database credentials
├── pharmacy-service/
│   ├── deployment.yaml      ✅ Unified structure (3 replicas)
│   ├── configmap.yaml       ✅ Unified configuration
│   └── secret.yaml          ✅ Database credentials
└── ...other services...
```

### 2. **Unified Deployment Configuration**

All deployments now include:

#### ✅ Init Container
```yaml
initContainers:
- name: wait-for-mysql
  image: alpine:3.19
  command: ['sh', '-c', 'until (echo > /dev/tcp/mysql.neuroguard.svc.cluster.local/3306)...
```
**Purpose:** Ensures MySQL is ready before application starts

#### ✅ Complete Environment Variables
```yaml
env:
- name: SPRING_DATASOURCE_URL
  value: "jdbc:mysql://mysql.neuroguard.svc.cluster.local:3306/{dbname}?..."
- name: SPRING_DATASOURCE_USERNAME
  valueFrom:
    secretKeyRef:
      name: {service}-db-secret
      key: username
- name: SPRING_DATASOURCE_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {service}-db-secret
      key: password
```
**Purpose:** Database connectivity with secret-based credentials

#### ✅ Three-Tier Health Probes
```yaml
startupProbe:          # 30s delay, 100 retries (500s total)
  - Allows 500 seconds for Spring Boot initialization
livenessProbe:         # 120s delay, fail after 3 failures
  - Checks if app is still running
readinessProbe:        # 60s delay, checks readiness endpoint
  - Signals when ready to receive traffic
```
**Purpose:** Robust health checking for Kubernetes orchestration

#### ✅ Resource Requests & Limits
```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```
**Purpose:** Proper resource allocation and protection

#### ✅ Service Definition
```yaml
kind: Service
metadata:
  name: {service-name}
spec:
  type: ClusterIP
  ports:
  - port: {port}
    targetPort: {port}
```
**Purpose:** Internal service discovery within cluster

### 3. **Unified ConfigMap**

**Pattern:**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: {service}-service-config
  namespace: neuroguard
data:
  application.properties: |
    # Core Configuration
    spring.application.name={service}
    server.port={port}
    eureka.client.serviceUrl.defaultZone=http://eureka-server.neuroguard.svc.cluster.local:8761/eureka
    
    # MySQL Configuration
    spring.datasource.url=jdbc:mysql://mysql.neuroguard.svc.cluster.local:3306/{dbname}?...
    
    # Connection Pool
    spring.datasource.hikari.connection-timeout=30000
    spring.datasource.hikari.maximum-pool-size=5
    
    # Hibernate/JPA
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**Per-Service Values:**

| Service | Port | Database |
|---------|------|----------|
| careplan-service | 8081 | careplandb |
| prescription-service | 8089 | prescriptiondb |
| pharmacy-service | 8090 | pharmacydb |

### 4. **Unified Secret**

**Pattern:**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: {service}-db-secret
  namespace: neuroguard
type: Opaque
data:
  username: {base64_encoded}
  password: {base64_encoded}
```

**Per-Service Credentials:**

| Service | Username | Password (decoded) |
|---------|----------|-------------------|
| careplan | neuroguard | neuroguardpass |
| prescription | prescription | prescriptionpass |
| pharmacy | pharmacy | pharmacypass |

## Image Registry

All services use the unified DockerHub registry:

```
Registry: Docker Hub
Account: ameniferjeni
Pattern: ameniferjeni/{service-name}:latest

Examples:
- ameniferjeni/careplan-service:latest
- ameniferjeni/prescription-service:latest
- ameniferjeni/pharmacy-service:latest
```

## Namespace

All services deploy to the same namespace:
```
Namespace: neuroguard
```

## Database Connectivity

All services connect to shared MySQL instance:

```
Host: mysql.neuroguard.svc.cluster.local
Port: 3306
Connection Pool:
  - Connection Timeout: 30 seconds
  - Max Pool Size: 5
  - Initialization Timeout: 120 seconds
```

## Deployment Replicas

| Service | Replicas | Reason |
|---------|----------|--------|
| careplan-service | 1 | Low-frequency operations |
| prescription-service | 3 | High-frequency WebSocket notifications |
| pharmacy-service | 3 | High-frequency inventory operations |

## Health Check Endpoints

All services expose Actuator endpoints:

```
/actuator/health              - Overall health
/actuator/health/readiness    - Readiness status
/actuator/health/liveness     - Liveness status
/actuator/metrics             - Prometheus metrics
```

## Troubleshooting

### Check Deployment Status
```bash
kubectl get deployments -n neuroguard
kubectl get pods -n neuroguard
kubectl describe deployment {service-name} -n neuroguard
```

### View Logs
```bash
kubectl logs -f deployment/{service-name} -n neuroguard
```

### Check Service Discovery
```bash
kubectl get services -n neuroguard
kubectl get endpoints {service-name} -n neuroguard
```

### Verify MySQL Connection
```bash
kubectl exec -it pod/{pod-name} -n neuroguard -- /bin/bash
mysql -h mysql.neuroguard.svc.cluster.local -u neuroguard -p
```

## Deployment Order

1. **Namespace**: `kubectl apply -f k8s/careplan-service/namespace.yaml`
2. **MySQL**: `kubectl apply -f k8s/careplan-service/mysql-secret.yaml`
3. **MySQL Deployment**: `kubectl apply -f k8s/careplan-service/mysql-deployment.yaml`
4. **Services (in any order)**:
   - `kubectl apply -f k8s/careplan-service/`
   - `kubectl apply -f k8s/prescription-service/`
   - `kubectl apply -f k8s/pharmacy-service/`

## CI/CD Integration

All services use unified CI/CD pipeline structure:

**Jenkinsfile Pipeline:**
1. Code Checkout
2. Unit Tests + JaCoCo Coverage
3. SonarQube Analysis
4. Quality Gate Check
5. CD Pipeline Trigger

**JaCoCo Coverage:**
- Report path: `target/site/jacoco/jacoco.xml`
- Exclusions: `src/test/**/*.*`
- Integration: SonarQube for metrics dashboard

## Maintenance

### Adding a New Microservice
Follow these steps to maintain consistency:

1. Create `utils/Constants.java` with service-specific constants
2. Create `utils/ServiceUtils.java` with common utilities
3. Create `k8s/{service}/deployment.yaml` following unified pattern
4. Create `k8s/{service}/configmap.yaml` with service config
5. Create `k8s/{service}/secret.yaml` with DB credentials
6. Update Jenkinsfile with proper JaCoCo and SonarQube paths

### Updating Configuration
- Modify `configmap.yaml` and apply: `kubectl apply -f configmap.yaml`
- Configuration changes don't require pod restart (Spring reloads)
- For code changes: redeploy application image

### Database Changes
- Hibernate `ddl-auto=update` handles schema changes
- Ensure proper migration scripts for breaking changes
- Database credentials stored in secrets (never in configmap)

## Performance Considerations

1. **Init Container**: ~3-5 seconds per pod startup
2. **Startup Probe**: ~30-300 seconds (100 retries × 3-5s)
3. **Total Startup Time**: 5-10 minutes for full initialization
4. **Connection Pool**: 5 connections per service = 15 max total
5. **Memory Limit**: 1Gi per pod, monitor for OOM conditions

## Security

- **Database Secrets**: Kubernetes Secrets (encrypted at rest, optional)
- **Image Registry**: DockerHub (consider private registry)
- **Network Policies**: None configured (add as needed)
- **RBAC**: Not configured (add service accounts as needed)

## Summary of Improvements

✅ **Code Level:**
- Centralized constants (no magic strings)
- Reusable utility methods
- Consistent error handling
- Comprehensive Javadoc

✅ **Kubernetes Level:**
- Unified deployment structure
- Complete init containers
- Proper health probe configuration
- Database credential management via secrets
- Consistent service discovery setup
- Resource allocation and limits

✅ **Maintainability:**
- Easy to onboard new developers
- Consistent patterns across all services
- Clear configuration management
- Standard CI/CD integration
- Documented troubleshooting procedures
