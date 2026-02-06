# Deployment Guide

This guide covers deploying the Issue Tracker application to various cloud platforms.

## Table of Contents
- [Prerequisites](#prerequisites)
- [AWS Deployment](#aws-deployment)
- [GCP Deployment](#gcp-deployment)
- [IBM Cloud Deployment](#ibm-cloud-deployment)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Environment Variables](#environment-variables)
- [Security Best Practices](#security-best-practices)

## Prerequisites

All deployment methods require:
- Docker images built and pushed to a container registry
- PostgreSQL database (managed service recommended)
- Domain name (optional but recommended)
- SSL certificate (use Let's Encrypt or cloud provider)

## AWS Deployment

### Option 1: AWS ECS Fargate (Recommended)

1. **Set up RDS PostgreSQL**
```bash
aws rds create-db-instance \
  --db-instance-identifier issue-tracker-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 15.3 \
  --master-username admin \
  --master-user-password <strong-password> \
  --allocated-storage 20 \
  --vpc-security-group-ids sg-xxxxx \
  --db-subnet-group-name default
```

2. **Create ECR Repositories**
```bash
aws ecr create-repository --repository-name issue-tracker-backend
aws ecr create-repository --repository-name issue-tracker-frontend
```

3. **Push Docker Images**
```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag issue-tracker-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/issue-tracker-backend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/issue-tracker-backend:latest

docker tag issue-tracker-frontend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/issue-tracker-frontend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/issue-tracker-frontend:latest
```

4. **Create Secrets in AWS Secrets Manager**
```bash
aws secretsmanager create-secret \
  --name issue-tracker/database-password \
  --secret-string "your-db-password"

aws secretsmanager create-secret \
  --name issue-tracker/jwt-secret \
  --secret-string "your-256-bit-jwt-secret"
```

5. **Create ECS Cluster**
```bash
aws ecs create-cluster --cluster-name issue-tracker-cluster
```

6. **Register Task Definition**
```bash
aws ecs register-task-definition \
  --cli-input-json file://deployment/aws/task-definition.json
```

7. **Create ECS Service**
```bash
aws ecs create-service \
  --cluster issue-tracker-cluster \
  --service-name issue-tracker-service \
  --task-definition issue-tracker \
  --desired-count 2 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxxxx],securityGroups=[sg-xxxxx],assignPublicIp=ENABLED}" \
  --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:...,containerName=frontend,containerPort=80"
```

8. **Set up Application Load Balancer**
- Create ALB in AWS Console
- Configure listeners for HTTP (redirect to HTTPS) and HTTPS
- Attach SSL certificate
- Configure target groups for frontend and backend

### Option 2: AWS Elastic Beanstalk

1. Install EB CLI:
```bash
pip install awsebcli
```

2. Initialize EB application:
```bash
eb init -p docker issue-tracker --region us-east-1
```

3. Create environment:
```bash
eb create issue-tracker-prod --database
```

4. Deploy:
```bash
eb deploy
```

## GCP Deployment

### Option 1: Cloud Run (Serverless)

1. **Set up Cloud SQL PostgreSQL**
```bash
gcloud sql instances create issue-tracker-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1

gcloud sql databases create issuetracker \
  --instance=issue-tracker-db

gcloud sql users create admin \
  --instance=issue-tracker-db \
  --password=<strong-password>
```

2. **Push to Container Registry**
```bash
# Configure Docker for GCR
gcloud auth configure-docker

# Tag and push
docker tag issue-tracker-backend:latest gcr.io/<project-id>/issue-tracker-backend:latest
docker push gcr.io/<project-id>/issue-tracker-backend:latest

docker tag issue-tracker-frontend:latest gcr.io/<project-id>/issue-tracker-frontend:latest
docker push gcr.io/<project-id>/issue-tracker-frontend:latest
```

3. **Create Secrets**
```bash
echo -n "your-db-password" | \
  gcloud secrets create database-password --data-file=-

echo -n "your-jwt-secret" | \
  gcloud secrets create jwt-secret --data-file=-
```

4. **Deploy Backend**
```bash
gcloud run deploy issue-tracker-backend \
  --image gcr.io/<project-id>/issue-tracker-backend:latest \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars DATABASE_URL=jdbc:postgresql:///<database-name>?cloudSqlInstance=<instance-connection-name>&socketFactory=com.google.cloud.sql.postgres.SocketFactory \
  --set-secrets DATABASE_PASSWORD=database-password:latest,JWT_SECRET=jwt-secret:latest \
  --add-cloudsql-instances <instance-connection-name>
```

5. **Deploy Frontend**
```bash
gcloud run deploy issue-tracker-frontend \
  --image gcr.io/<project-id>/issue-tracker-frontend:latest \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars REACT_APP_API_URL=https://<backend-url>/api
```

### Option 2: Google Kubernetes Engine (GKE)

1. Create GKE cluster:
```bash
gcloud container clusters create issue-tracker-cluster \
  --num-nodes=3 \
  --zone=us-central1-a
```

2. Get credentials:
```bash
gcloud container clusters get-credentials issue-tracker-cluster --zone=us-central1-a
```

3. Deploy using Kubernetes manifests:
```bash
kubectl apply -f deployment/kubernetes/deployment.yaml
```

## IBM Cloud Deployment

### Using IBM Cloud Code Engine

1. **Install IBM Cloud CLI**
```bash
# Install CLI
curl -fsSL https://clis.cloud.ibm.com/install/linux | sh

# Install Code Engine plugin
ibmcloud plugin install code-engine
```

2. **Login and Setup**
```bash
ibmcloud login
ibmcloud target -r us-south -g Default
ibmcloud ce project create --name issue-tracker
ibmcloud ce project select --name issue-tracker
```

3. **Create PostgreSQL Database**
```bash
ibmcloud resource service-instance-create issue-tracker-db \
  databases-for-postgresql standard us-south \
  -p '{"members_memory_allocation_mb": "3072"}'
```

4. **Deploy Backend**
```bash
ibmcloud ce application create \
  --name issue-tracker-backend \
  --image <registry>/issue-tracker-backend:latest \
  --port 8080 \
  --env DATABASE_URL=<connection-string> \
  --env DATABASE_PASSWORD=<password> \
  --env JWT_SECRET=<secret> \
  --min-scale 1 \
  --max-scale 10
```

5. **Deploy Frontend**
```bash
ibmcloud ce application create \
  --name issue-tracker-frontend \
  --image <registry>/issue-tracker-frontend:latest \
  --port 80 \
  --env REACT_APP_API_URL=https://<backend-url>/api \
  --min-scale 1 \
  --max-scale 10
```

## Kubernetes Deployment

For any Kubernetes cluster (EKS, GKE, AKS, or self-hosted):

1. **Create namespace and apply manifests**
```bash
kubectl apply -f deployment/kubernetes/deployment.yaml
```

2. **Verify deployment**
```bash
kubectl get pods -n issue-tracker
kubectl get services -n issue-tracker
```

3. **Check logs**
```bash
kubectl logs -f deployment/backend -n issue-tracker
kubectl logs -f deployment/frontend -n issue-tracker
```

4. **Scale deployment**
```bash
kubectl scale deployment backend --replicas=5 -n issue-tracker
```

## Environment Variables

### Backend Environment Variables
```
DATABASE_URL=jdbc:postgresql://<host>:<port>/issuetracker
DATABASE_USERNAME=<username>
DATABASE_PASSWORD=<password>
JWT_SECRET=<256-bit-secret>
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=https://your-domain.com
PORT=8080
```

### Frontend Environment Variables
```
REACT_APP_API_URL=https://api.your-domain.com/api
```

## Security Best Practices

1. **Use Secrets Management**
   - AWS: Secrets Manager or Systems Manager Parameter Store
   - GCP: Secret Manager
   - IBM Cloud: Secrets Manager
   - Never hardcode secrets in code or environment variables

2. **Database Security**
   - Use managed database services
   - Enable SSL/TLS connections
   - Use strong passwords (20+ characters)
   - Restrict network access (VPC/firewall rules)
   - Enable automated backups

3. **Network Security**
   - Use private subnets for backend/database
   - Enable WAF (Web Application Firewall)
   - Use security groups/firewall rules
   - Implement rate limiting

4. **Application Security**
   - Always use HTTPS (enable redirect from HTTP)
   - Use strong JWT secrets (256-bit minimum)
   - Implement proper CORS configuration
   - Keep dependencies updated
   - Run security scans (Trivy, Snyk, etc.)

5. **Monitoring and Logging**
   - Enable application logs
   - Set up alerts for errors and performance issues
   - Monitor resource usage
   - Track API performance metrics

6. **Backup and Recovery**
   - Enable automated database backups
   - Test restore procedures regularly
   - Store backups in different region/zone
   - Document recovery procedures

## Cost Optimization

1. **AWS**
   - Use Fargate Spot for development environments
   - Enable auto-scaling based on CPU/memory
   - Use Reserved Instances for production
   - Set up CloudWatch alarms for cost monitoring

2. **GCP**
   - Use Cloud Run for automatic scaling
   - Enable committed use discounts
   - Set up budget alerts
   - Use preemptible instances for dev/test

3. **General**
   - Implement caching (Redis/Memcached)
   - Use CDN for frontend assets
   - Optimize Docker images (multi-stage builds)
   - Right-size resources based on monitoring

## Troubleshooting

### Application won't start
- Check environment variables are set correctly
- Verify database connectivity
- Check logs: `docker logs <container>` or `kubectl logs <pod>`
- Verify security groups/firewall rules

### Database connection errors
- Check connection string format
- Verify credentials
- Ensure database is accessible from application network
- Check SSL/TLS requirements

### High latency
- Enable database connection pooling
- Add caching layer
- Check database query performance
- Scale horizontally

### Out of memory errors
- Increase container memory limits
- Check for memory leaks
- Optimize JVM settings: `-Xmx512m -Xms256m`
- Profile application memory usage

## Support

For deployment issues:
1. Check application logs
2. Review cloud provider documentation
3. Open an issue on GitHub
4. Contact support team
