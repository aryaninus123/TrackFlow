# Cloud-Based Issue Tracker with Analytics

A production-ready issue tracking system built with Java Spring Boot, React, PostgreSQL, and Docker. Features include JWT authentication, real-time analytics, collaboration tools, and cloud deployment capabilities.

## Features

### Core Features
- **Issue Management**: Create, update, track, and delete issues with status and priority levels
- **User Authentication**: Secure JWT-based authentication system
- **User Assignment**: Assign issues to team members with dropdown selection
- **Analytics Dashboard**: Real-time metrics including:
  - Status distribution (Open, In Progress, Resolved, Closed, Reopened)
  - Priority distribution (Low, Medium, High, Critical)
  - Average time-to-resolution
  - Issue statistics and trends

### Collaboration Features
- **Comments System**: Add discussions and updates to issues
- **File Attachments**: Upload and download files (images, documents, etc.)
- **Activity Tracking**: Track issue creation, updates, and resolution times

### Search & Navigation
- **Full-Text Search**: Search issues by title and description
- **Pagination**: Efficient browsing of large issue lists
- **Status Filtering**: Filter by issue status and priority

### Developer Experience
- **RESTful API**: Clean, well-documented REST endpoints
- **Swagger/OpenAPI**: Interactive API documentation at `/swagger-ui.html`
- **Modern UI**: Responsive React frontend with intuitive design
- **Cloud-Ready**: Dockerized application ready for AWS/GCP/Azure deployment
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

## Quick Start

### Prerequisites
- Docker Desktop installed and running
- Java 17+ (for local development)
- Node.js 18+ (for local development)
- Git

### Running with Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd data_analysis
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost
   - Backend API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - PostgreSQL: localhost:5432

4. **Create your first user**
   - Go to http://localhost
   - Click "Register" and create an account
   - Login and start creating issues!

### Stopping the application
```bash
docker-compose down
```

### Viewing logs
```bash
# All services
docker-compose logs -f

# Backend only
docker logs issuetracker-backend -f

# Frontend only
docker logs issuetracker-frontend -f
```

## Development Setup

### Backend Development
```bash
# Navigate to project root
cd data_analysis

# Run with Maven
./mvnw spring-boot:run

# Run tests
./mvnw test
```

### Frontend Development
```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start dev server
npm start

# Run tests
npm test
```

## API Documentation

Once the application is running, access the interactive API documentation:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Main API Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

#### Issues
- `GET /api/issues` - Get all issues
- `GET /api/issues/paginated` - Get paginated issues
- `GET /api/issues/search` - Search issues
- `GET /api/issues/{id}` - Get issue by ID
- `POST /api/issues` - Create new issue
- `PUT /api/issues/{id}` - Update issue
- `DELETE /api/issues/{id}` - Delete issue

#### Comments
- `GET /api/issues/{issueId}/comments` - Get issue comments (paginated)
- `POST /api/issues/{issueId}/comments` - Add comment
- `PUT /api/comments/{id}` - Update comment
- `DELETE /api/comments/{id}` - Delete comment

#### Attachments
- `GET /api/issues/{issueId}/attachments` - Get issue attachments
- `POST /api/issues/{issueId}/attachments` - Upload file
- `GET /api/attachments/{id}/download` - Download file
- `DELETE /api/attachments/{id}` - Delete attachment

#### Analytics
- `GET /api/analytics` - Get dashboard analytics

#### Users
- `GET /api/users` - Get all users (for assignee selection)

## Environment Variables

### Backend (`application.yml`)
```yaml
DB_HOST: PostgreSQL host (default: postgres)
DB_PORT: PostgreSQL port (default: 5432)
DB_NAME: Database name (default: issuetracker)
DB_USERNAME: Database username (default: postgres)
DB_PASSWORD: Database password (default: postgres)
JWT_SECRET: Secret key for JWT tokens (change in production!)
JWT_EXPIRATION: Token expiration time in ms (default: 86400000)
FILE_UPLOAD_DIR: Directory for file uploads (default: uploads)
FILE_UPLOAD_MAX_SIZE: Max file size in bytes (default: 10MB)
```

### Frontend
```bash
REACT_APP_API_URL: Backend API URL (default: /api)
```

## CI/CD Pipeline

The project includes a GitHub Actions workflow that:
1. **Tests Backend** - Runs JUnit tests with PostgreSQL
2. **Tests Frontend** - Runs React component tests
3. **Builds Docker Images** - Creates production images
4. **Pushes to Registry** - Pushes to Docker Hub (on main branch)
5. **Security Scan** - Runs Trivy vulnerability scanner

### Setting up CI/CD
1. Add GitHub secrets:
   - `DOCKER_USERNAME` - Your Docker Hub username
   - `DOCKER_PASSWORD` - Your Docker Hub password
2. Push to `main` or `develop` branch to trigger the pipeline

## Cloud Deployment

### Deploy to AWS ECS
See [AWS_DEPLOYMENT.md](./docs/AWS_DEPLOYMENT.md) for detailed instructions.

Quick overview:
1. Create RDS PostgreSQL database
2. Push images to ECR
3. Create ECS cluster with Fargate
4. Set up Application Load Balancer
5. Configure secrets in AWS Secrets Manager

### Deploy to GCP Cloud Run
See [GCP_DEPLOYMENT.md](./docs/GCP_DEPLOYMENT.md) for detailed instructions.

### Deploy to Azure Container Instances
See [AZURE_DEPLOYMENT.md](./docs/AZURE_DEPLOYMENT.md) for detailed instructions.

## Project Structure

```
data_analysis/
├── src/
│   ├── main/
│   │   ├── java/com/issuetracker/
│   │   │   ├── config/          # Security, CORS, OpenAPI config
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── exception/       # Exception handlers
│   │   │   ├── model/           # JPA entities
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── security/        # JWT authentication
│   │   │   └── service/         # Business logic
│   │   └── resources/
│   │       ├── application.yml  # Main configuration
│   │       └── application-test.yml  # Test configuration
│   └── test/                    # Unit and integration tests
├── frontend/
│   ├── public/                  # Static assets
│   ├── src/
│   │   ├── components/          # React components
│   │   ├── services/            # API client
│   │   ├── App.js              # Main app component
│   │   └── App.css             # Global styles
│   └── Dockerfile              # Frontend container
├── docker-compose.yml          # Local development setup
├── Dockerfile                  # Backend container
├── pom.xml                     # Maven dependencies
└── README.md                   # This file
```

## Security Considerations

- ✅ JWT-based authentication
- ✅ Password hashing with BCrypt
- ✅ CORS configuration
- ✅ SQL injection prevention (JPA)
- ✅ Input validation
- ⚠️ **Change JWT_SECRET in production!**
- ⚠️ **Use HTTPS in production**
- ⚠️ **Enable database encryption for sensitive data**
- ⚠️ **Implement rate limiting for APIs**

## Troubleshooting

### Docker issues
```bash
# Rebuild all containers
docker-compose down
docker-compose build --no-cache
docker-compose up -d

# Check container logs
docker logs issuetracker-backend
docker logs issuetracker-frontend
docker logs issuetracker-db

# Verify containers are healthy
docker ps
```

### Database connection issues
```bash
# Connect to PostgreSQL container
docker exec -it issuetracker-db psql -U postgres -d issuetracker

# Check tables
\dt

# Check users
SELECT * FROM users;
```

### Frontend not connecting to backend
- Verify backend is running: http://localhost:8080/api/health
- Check browser console for CORS errors
- Ensure `REACT_APP_API_URL` is set correctly

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Spring Boot team for the excellent framework
- React community for modern frontend tools
- Docker for containerization simplicity

## Contact

For questions or support, please open an issue on GitHub.

