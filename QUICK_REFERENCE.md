# NeuroGuard Unified Services - Quick Reference

## 🚀 Quick Start

### Deploy Everything
```bash
./deploy-unified.sh
```

### Verify Deployment
```bash
kubectl get deployments -n neuroguard
kubectl get pods -n neuroguard
kubectl get svc -n neuroguard
```

---

## 📊 Service Endpoints

| Service | Port | Health URL |
|---------|------|-----------|
| careplan-service | 8081 | http://localhost:8081/actuator/health |
| prescription-service | 8089 | http://localhost:8089/actuator/health |
| pharmacy-service | 8090 | http://localhost:8090/actuator/health |

## 🔗 Kubernetes Service DNS

```
careplan-service.neuroguard.svc.cluster.local:8081
prescription-service.neuroguard.svc.cluster.local:8089
pharmacy-service.neuroguard.svc.cluster.local:8090
mysql.neuroguard.svc.cluster.local:3306
```

---

## 📁 Project Structure

### Code Files
```
careplan-service/
  └── utils/
      ├── Constants.java          [NEW] 32 lines
      └── ServiceUtils.java       [NEW] 48 lines

prescription-service/
  └── utils/
      ├── Constants.java          [NEW] 28 lines
      └── ServiceUtils.java       [NEW] 36 lines

pharmacy-service/
  └── utils/
      ├── Constants.java          [NEW] 28 lines
      └── ServiceUtils.java       [NEW] 36 lines
```

### Kubernetes Files
```
k8s/
├── careplan-service/
│   ├── deployment.yaml           ✅ VERIFIED
│   ├── configmap.yaml            ✅ VERIFIED
│   ├── mysql-deployment.yaml     ✅ VERIFIED
│   ├── mysql-secret.yaml         ✅ VERIFIED
│   └── namespace.yaml            ✅ VERIFIED
│
├── prescription-service/
│   ├── deployment.yaml           ✅ UPDATED
│   ├── configmap.yaml            ✅ UPDATED
│   └── secret.yaml               ✅ CREATED
│
└── pharmacy-service/
    ├── deployment.yaml           ✅ UPDATED
    ├── configmap.yaml            ✅ UPDATED
    └── secret.yaml               ✅ CREATED
```

---

## 🔧 Common Commands

### Check Deployments
```bash
# All services
kubectl get deployments -n neuroguard

# Specific service
kubectl get deployment careplan-service -n neuroguard
kubectl get deployment prescription-service -n neuroguard
kubectl get deployment pharmacy-service -n neuroguard

# With details
kubectl describe deployment careplan-service -n neuroguard
```

### View Logs
```bash
# Stream logs from deployment
kubectl logs -f deployment/careplan-service -n neuroguard
kubectl logs -f deployment/prescription-service -n neuroguard
kubectl logs -f deployment/pharmacy-service -n neuroguard

# Specific pod
kubectl logs -f pod/{pod-name} -n neuroguard
```

### Access Services
```bash
# Port forward to access service
kubectl port-forward service/careplan-service 8081:8081 -n neuroguard
kubectl port-forward service/prescription-service 8089:8089 -n neuroguard
kubectl port-forward service/pharmacy-service 8090:8090 -n neuroguard

# Execute command in pod
kubectl exec -it pod/{pod-name} -n neuroguard -- /bin/bash
```

### Check Health
```bash
# Health check for each service
curl http://localhost:8081/actuator/health
curl http://localhost:8089/actuator/health
curl http://localhost:8090/actuator/health

# Via kubectl
kubectl exec -it pod/{pod-name} -n neuroguard -- curl localhost:8081/actuator/health
```

### Database Operations
```bash
# Connect to MySQL
kubectl exec -it pod/mysql -n neuroguard -- mysql -u root -p

# Show databases
kubectl exec -it pod/mysql -n neuroguard -- mysql -u root -p -e "SHOW DATABASES;"

# Check connection from service pod
kubectl exec -it pod/{service-pod} -n neuroguard -- mysql -h mysql.neuroguard.svc.cluster.local -u neuroguard -p -e "SHOW TABLES;"
```

---

## 📋 Configuration Details

### Database Credentials
```
careplan-service:
  username: neuroguard
  password: neuroguardpass
  database: careplandb

prescription-service:
  username: prescription
  password: prescriptionpass
  database: prescriptiondb

pharmacy-service:
  username: pharmacy
  password: pharmacypass
  database: pharmacydb
```

### Replica Counts
```
careplan-service:     1 replica (read-heavy)
prescription-service: 3 replicas (high throughput)
pharmacy-service:     3 replicas (inventory heavy)
```

### Resource Allocation
```
Per Pod:
  CPU Request:    250m
  CPU Limit:      500m
  Memory Request: 512Mi
  Memory Limit:   1Gi
```

### Health Probes
```
All Services:
  Startup Probe:
    - Initial Delay: 30s
    - Period: 5s
    - Failure Threshold: 100 (500s total)
  
  Liveness Probe:
    - Initial Delay: 120s
    - Period: 10s
    - Failure Threshold: 3
  
  Readiness Probe:
    - Initial Delay: 60s
    - Period: 10s
    - Failure Threshold: 3
```

---

## 🐛 Troubleshooting

### Pod not starting?
```bash
# Check pod status
kubectl describe pod {pod-name} -n neuroguard

# Check events
kubectl get events -n neuroguard --sort-by='.lastTimestamp'

# View logs
kubectl logs {pod-name} -n neuroguard
kubectl logs {pod-name} -n neuroguard --previous  # Previous run logs
```

### MySQL connection fails?
```bash
# Check MySQL pod
kubectl get pod mysql -n neuroguard
kubectl logs mysql -n neuroguard

# Test connectivity from service pod
kubectl exec -it {service-pod} -n neuroguard -- \
  nslookup mysql.neuroguard.svc.cluster.local

# Check MySQL is accepting connections
kubectl exec -it mysql -n neuroguard -- \
  mysql -u root -p -e "SELECT 1"
```

### Service discovery not working?
```bash
# Check DNS in pod
kubectl exec -it {pod} -n neuroguard -- \
  nslookup {service-name}.neuroguard.svc.cluster.local

# Check service endpoint
kubectl get endpoints {service-name} -n neuroguard
```

### Out of resources?
```bash
# Check node resources
kubectl top nodes
kubectl top pods -n neuroguard

# Check pod requests/limits
kubectl get pods -n neuroguard -o custom-columns=NAME:.metadata.name,CPU:.spec.containers[].resources.requests.cpu,MEM:.spec.containers[].resources.requests.memory
```

---

## ✅ Checklist Before Production

- [ ] All 3 services deployed successfully
- [ ] All pods running: `kubectl get pods -n neuroguard`
- [ ] All services ready: `kubectl get svc -n neuroguard`
- [ ] Health checks passing: `/actuator/health`
- [ ] MySQL connection working: Can query databases
- [ ] Logs clean: No ERROR or WARN messages
- [ ] Resources available: `kubectl top nodes` shows available capacity
- [ ] Network connectivity: Services can reach each other
- [ ] Database backups configured
- [ ] Monitoring/alerts setup
- [ ] CI/CD pipelines working

---

## 📚 Documentation Files

```
├── UNIFIED_STRUCTURE_GUIDE.md          ← Complete reference guide
├── COMPLETE_UNIFICATION_SUMMARY.md     ← Executive summary
├── MAINTAINABILITY_IMPROVEMENTS.md     ← Code quality improvements
├── CHANGES_CHECKLIST.md                ← Detailed change log
├── QUICK_REFERENCE.md                  ← This file
└── deploy-unified.sh                   ← Automated deployment
```

---

## 🔗 Links

- **Code Repository**: https://github.com/saifeddinefrikhi-lab/Esprit-PIDEV-4SAE8-2526-NeuroGuard
- **Kubernetes Cluster**: https://10.0.2.15:6443 (Vagrant)
- **DockerHub Registry**: https://hub.docker.com/u/ameniferjeni
- **Eureka Server**: http://eureka-server.neuroguard.svc.cluster.local:8761

---

## 📞 Support

For issues or questions:

1. **Check Logs**: `kubectl logs -f deployment/{service} -n neuroguard`
2. **Check Status**: `kubectl describe deployment/{service} -n neuroguard`
3. **Check Events**: `kubectl get events -n neuroguard --sort-by='.lastTimestamp'`
4. **Review Docs**: See documentation files above
5. **Verify Config**: `kubectl get configmap {service}-service-config -n neuroguard -o yaml`

---

## 🎯 Success Indicators

✅ All deployments created
✅ All pods in Running state
✅ All services have IP addresses
✅ Health checks returning 200 OK
✅ Services can reach MySQL
✅ Logs show successful startup
✅ Eureka shows all services registered
✅ CI/CD pipelines triggered
✅ SonarQube shows improved metrics
✅ JaCoCo coverage appearing in SonarQube

---

**Last Updated**: 7 May 2026
**Version**: 1.0 Final
**Status**: ✅ Ready for Deployment
