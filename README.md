# Cloud-Based Issue Tracker with Analytics

A production-ready issue tracking system built with Java Spring Boot, React, PostgreSQL, and Docker. Features include JWT authentication, real-time analytics, and cloud deployment capabilities.

## Features

- **Issue Management**: Create, update, track, and delete issues with status and priority levels
- **User Authentication**: Secure JWT-based authentication system
- **Analytics Dashboard**: Real-time metrics including:
  - Status distribution (Open, In Progress, Resolved, Closed)
  - Priority distribution (Low, Medium, High, Critical)
  - Average time-to-resolution
  - Issue statistics
- **RESTful API**: Clean, well-documented REST endpoints
- **Modern UI**: Responsive React frontend with intuitive design
- **Cloud-Ready**: Dockerized application ready for cloud deployment
- **CI/CD Pipeline**: Automated testing and deployment with GitHub Actions

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL 15
- Maven

### Frontend
- React 18
- React Router
- Axios
- Modern CSS

### DevOps
- Docker & Docker Compose
- GitHub Actions (CI/CD)
- Nginx (Frontend server)

## Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL 15 (or use Docker)
- Maven 3.9+
- Docker & Docker Compose (optional)

## Quick Start with Docker

The easiest way to run the application is using Docker Compose:

```bash
# Clone the repository
git clone <repository-url>
cd data_analysis

# Start all services
docker-compose up -d

# Access the application
# Frontend: http://localhost
# Backend API: http://localhost:8080
# PostgreSQL: localhost:5432
```

The application will automatically:
- Build the backend and frontend
- Set up PostgreSQL database
- Configure networking between services
- Start all containers

## Local Development Setup

### Backend Setup

```bash
# Navigate to project root
cd data_analysis

# Start PostgreSQL (if not using Docker)
# Create database 'issuetracker'

# Configure database in src/main/resources/application.yml
# Or use environment variables (see .env.example)

# Build and run
mvn clean install
mvn spring-boot:run
```

Backend will be available at `http://localhost:8080`

### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

Frontend will be available at `http://localhost:3000`

## Environment Configuration

Copy `.env.example` to `.env` and configure:

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/issuetracker
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password

# JWT Secret (use strong 256-bit key in production)
JWT_SECRET=your-secret-key-change-in-production

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000

# API URL (for frontend)
REACT_APP_API_URL=http://localhost:8080/api
```

## API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

### Issue Endpoints (Requires Authentication)

#### Create Issue
```http
POST /api/issues
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Bug in login page",
  "description": "Users cannot login",
  "status": "OPEN",
  "priority": "HIGH",
  "assigneeId": 2
}
```

#### Get All Issues
```http
GET /api/issues
Authorization: Bearer <token>
```

#### Get Issue by ID
```http
GET /api/issues/{id}
Authorization: Bearer <token>
```

#### Update Issue
```http
PUT /api/issues/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "IN_PROGRESS",
  "priority": "CRITICAL"
}
```

#### Delete Issue
```http
DELETE /api/issues/{id}
Authorization: Bearer <token>
```

#### Get My Issues
```http
GET /api/issues/my-issues
Authorization: Bearer <token>
```

#### Get Assigned Issues
```http
GET /api/issues/assigned-to-me
Authorization: Bearer <token>
```

### Analytics Endpoints

#### Get Analytics Dashboard
```http
GET /api/analytics
Authorization: Bearer <token>
```

Returns:
```json
{
  "totalIssues": 150,
  "openIssues": 45,
  "closedIssues": 80,
  "inProgressIssues": 25,
  "averageResolutionTimeHours": 24.5,
  "statusDistribution": {
    "OPEN": 45,
    "IN_PROGRESS": 25,
    "RESOLVED": 30,
    "CLOSED": 50
  },
  "priorityDistribution": {
    "LOW": 30,
    "MEDIUM": 70,
    "HIGH": 35,
    "CRITICAL": 15
  }
}
```

## Running Tests

### Backend Tests
```bash
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

### Integration Tests
```bash
mvn verify
```

## Building for Production

### Build Backend JAR
```bash
mvn clean package -DskipTests
# JAR will be in target/issue-tracker-1.0.0.jar
```

### Build Frontend
```bash
cd frontend
npm run build
# Build will be in frontend/build/
```

### Build Docker Images
```bash
# Backend
docker build -t issue-tracker-backend:latest .

# Frontend
docker build -t issue-tracker-frontend:latest ./frontend
```

## Cloud Deployment

### AWS Deployment (ECS/Fargate)

1. **Prerequisites**
   - AWS CLI configured
   - ECR repositories created
   - RDS PostgreSQL instance set up

2. **Push Images to ECR**
```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push backend
docker tag issue-tracker-backend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/issue-tracker-backend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/issue-tracker-backend:latest

# Tag and push frontend
docker tag issue-tracker-frontend:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/issue-tracker-frontend:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/issue-tracker-frontend:latest
```

3. **Create ECS Task Definition**
   - See `deployment/aws/task-definition.json`

4. **Create ECS Service**
```bash
aws ecs create-service \
  --cluster issue-tracker-cluster \
  --service-name issue-tracker-service \
  --task-definition issue-tracker \
  --desired-count 2 \
  --launch-type FARGATE
```

### GCP Deployment (Cloud Run)

1. **Prerequisites**
   - gcloud CLI configured
   - Project created

2. **Push to Container Registry**
```bash
# Tag images
docker tag issue-tracker-backend:latest gcr.io/<project-id>/issue-tracker-backend:latest
docker tag issue-tracker-frontend:latest gcr.io/<project-id>/issue-tracker-frontend:latest

# Push images
docker push gcr.io/<project-id>/issue-tracker-backend:latest
docker push gcr.io/<project-id>/issue-tracker-frontend:latest
```

3. **Deploy to Cloud Run**
```bash
# Deploy backend
gcloud run deploy issue-tracker-backend \
  --image gcr.io/<project-id>/issue-tracker-backend:latest \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated

# Deploy frontend
gcloud run deploy issue-tracker-frontend \
  --image gcr.io/<project-id>/issue-tracker-frontend:latest \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

### IBM Cloud Deployment (Code Engine)

1. **Prerequisites**
   - IBM Cloud CLI installed
   - Code Engine project created

2. **Deploy Application**
```bash
# Login to IBM Cloud
ibmcloud login

# Select Code Engine project
ibmcloud ce project select --name issue-tracker

# Deploy backend
ibmcloud ce application create \
  --name issue-tracker-backend \
  --image <registry>/issue-tracker-backend:latest \
  --port 8080

# Deploy frontend
ibmcloud ce application create \
  --name issue-tracker-frontend \
  --image <registry>/issue-tracker-frontend:latest \
  --port 80
```

## CI/CD Pipeline

The project includes a GitHub Actions workflow that:

1. **Test Stage**: Runs unit and integration tests for both backend and frontend
2. **Build Stage**: Builds Docker images for production
3. **Push Stage**: Pushes images to Docker Hub (on main branch)
4. **Security Stage**: Runs vulnerability scans with Trivy

### Setup CI/CD

1. Add GitHub Secrets:
   - `DOCKER_USERNAME`: Your Docker Hub username
   - `DOCKER_PASSWORD`: Your Docker Hub password/token

2. Push to main branch triggers full pipeline

## Database Schema

### Users Table
- `id`: Primary key
- `username`: Unique username
- `email`: Unique email
- `password`: Encrypted password
- `full_name`: User's full name
- `roles`: User roles (USER, ADMIN)
- `active`: Account status
- `created_at`: Registration timestamp
- `updated_at`: Last update timestamp

### Issues Table
- `id`: Primary key
- `title`: Issue title
- `description`: Detailed description
- `status`: OPEN, IN_PROGRESS, RESOLVED, CLOSED, REOPENED
- `priority`: LOW, MEDIUM, HIGH, CRITICAL
- `reporter_id`: Foreign key to users
- `assignee_id`: Foreign key to users (nullable)
- `resolved_at`: Resolution timestamp
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp

## Security Considerations

1. **JWT Tokens**: Use strong secret keys (256-bit minimum)
2. **Password Encryption**: BCrypt with cost factor 10
3. **CORS**: Configure allowed origins in production
4. **Database**: Use strong passwords, enable SSL
5. **Environment Variables**: Never commit secrets to repository
6. **HTTPS**: Always use HTTPS in production
7. **Rate Limiting**: Consider adding rate limiting for APIs

## Monitoring and Logging

- Application logs available via Docker logs:
  ```bash
  docker-compose logs -f backend
  docker-compose logs -f frontend
  ```

- Health check endpoint: `GET /api/health`

- Consider integrating:
  - Prometheus for metrics
  - Grafana for dashboards
  - ELK Stack for log aggregation
  - Sentry for error tracking

## Troubleshooting

### Backend won't start
- Check PostgreSQL is running and accessible
- Verify database credentials
- Check Java version (requires 17+)
- Review application logs

### Frontend can't connect to backend
- Verify `REACT_APP_API_URL` is set correctly
- Check CORS configuration in backend
- Ensure backend is running

### Docker issues
- Ensure Docker and Docker Compose are installed
- Check port conflicts (8080, 5432, 80)
- Run `docker-compose down -v` to reset

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features
5. Run tests: `mvn test && cd frontend && npm test`
6. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For issues, questions, or contributions, please open an issue on GitHub.
