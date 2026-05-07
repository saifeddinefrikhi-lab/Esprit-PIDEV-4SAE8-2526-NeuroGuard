#!/bin/bash

# NeuroGuard Unified Deployment Script
# This script deploys all three microservices with the unified structure

set -e  # Exit on error

NAMESPACE="neuroguard"
KUBE_CONTEXT="vagrant"  # Change based on your cluster context

echo "=========================================="
echo "NeuroGuard Services - Unified Deployment"
echo "=========================================="

# Function to apply manifest with color output
apply_manifest() {
    local manifest=$1
    local description=$2
    echo -e "\n📦 Deploying: $description"
    echo "   File: $manifest"
    kubectl apply -f "$manifest" --context="$KUBE_CONTEXT"
    echo "   ✅ Done"
}

# Function to wait for deployment
wait_deployment() {
    local deployment=$1
    local timeout=${2:-300}  # Default 5 minutes
    echo -e "\n⏳ Waiting for deployment: $deployment"
    kubectl rollout status deployment/"$deployment" -n "$NAMESPACE" --timeout="${timeout}s" --context="$KUBE_CONTEXT"
    echo "   ✅ Ready"
}

# Verify kubectl connectivity
echo -e "\n🔗 Checking Kubernetes connectivity..."
kubectl cluster-info --context="$KUBE_CONTEXT" || {
    echo "❌ Failed to connect to Kubernetes cluster"
    exit 1
}

# Step 1: Create namespace
echo -e "\n📋 Step 1/6: Namespace Setup"
apply_manifest "neuroguard-backend/k8s/careplan-service/namespace.yaml" "Namespace (neuroguard)"

# Step 2: Deploy MySQL
echo -e "\n📋 Step 2/6: MySQL Database Setup"
apply_manifest "neuroguard-backend/k8s/careplan-service/mysql-secret.yaml" "MySQL Secret"
apply_manifest "neuroguard-backend/k8s/careplan-service/mysql-deployment.yaml" "MySQL Deployment"
wait_deployment "mysql" 300

# Step 3: Deploy CarePlan Service
echo -e "\n📋 Step 3/6: CarePlan Service"
apply_manifest "neuroguard-backend/k8s/careplan-service/configmap.yaml" "CarePlan ConfigMap"
apply_manifest "neuroguard-backend/k8s/careplan-service/secret.yaml" "CarePlan Secret (if exists)"
apply_manifest "neuroguard-backend/k8s/careplan-service/deployment.yaml" "CarePlan Deployment"
wait_deployment "careplan-service" 600

# Step 4: Deploy Prescription Service
echo -e "\n📋 Step 4/6: Prescription Service"
apply_manifest "neuroguard-backend/k8s/prescription-service/secret.yaml" "Prescription Secret"
apply_manifest "neuroguard-backend/k8s/prescription-service/configmap.yaml" "Prescription ConfigMap"
apply_manifest "neuroguard-backend/k8s/prescription-service/deployment.yaml" "Prescription Deployment"
wait_deployment "prescription-service" 600

# Step 5: Deploy Pharmacy Service
echo -e "\n📋 Step 5/6: Pharmacy Service"
apply_manifest "neuroguard-backend/k8s/pharmacy-service/secret.yaml" "Pharmacy Secret"
apply_manifest "neuroguard-backend/k8s/pharmacy-service/configmap.yaml" "Pharmacy ConfigMap"
apply_manifest "neuroguard-backend/k8s/pharmacy-service/deployment.yaml" "Pharmacy Deployment"
wait_deployment "pharmacy-service" 600

# Step 6: Verification
echo -e "\n📋 Step 6/6: Verification"
echo -e "\n🔍 Checking all deployments:"
kubectl get deployments -n "$NAMESPACE" --context="$KUBE_CONTEXT"

echo -e "\n🔍 Checking all pods:"
kubectl get pods -n "$NAMESPACE" --context="$KUBE_CONTEXT"

echo -e "\n🔍 Checking all services:"
kubectl get services -n "$NAMESPACE" --context="$KUBE_CONTEXT"

echo -e "\n=========================================="
echo "✅ NeuroGuard Deployment Complete!"
echo "=========================================="

echo -e "\n📊 Service Status:"
echo "  • careplan-service (8081):     Ready"
echo "  • prescription-service (8089): Ready (3 replicas)"
echo "  • pharmacy-service (8090):     Ready (3 replicas)"

echo -e "\n🔗 Service Discovery:"
echo "  • careplan-service.neuroguard.svc.cluster.local:8081"
echo "  • prescription-service.neuroguard.svc.cluster.local:8089"
echo "  • pharmacy-service.neuroguard.svc.cluster.local:8090"
echo "  • mysql.neuroguard.svc.cluster.local:3306"

echo -e "\n📝 Next Steps:"
echo "  1. Verify logs: kubectl logs -f deployment/{service-name} -n neuroguard"
echo "  2. Check health: kubectl exec {pod-name} -- curl localhost:{port}/actuator/health"
echo "  3. Monitor: kubectl top nodes && kubectl top pods -n neuroguard"

exit 0
