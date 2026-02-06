# Quick Start Guide

Get the Issue Tracker up and running in minutes!

## Prerequisites

Choose one of these options:

### Option 1: Docker (Recommended - Easiest)
- Docker Desktop installed and running
- That's it!

### Option 2: Local Development
- Java 17+
- Node.js 18+
- PostgreSQL 15
- Maven 3.9+

## Quick Start with Docker (Recommended)

The fastest way to get started:

```bash
# 1. Navigate to project directory
cd data_analysis

# 2. Start everything with one command
docker-compose up -d

# Wait about 30 seconds for services to start, then access:
# - Frontend: http://localhost
# - Backend API: http://localhost:8080
# - PostgreSQL: localhost:5432
```

That's it! The application is now running.

### What just happened?

Docker Compose automatically:
- ✅ Set up PostgreSQL database
- ✅ Built and started the Java backend
- ✅ Built and started the React frontend
- ✅ Configured networking between services
- ✅ Set up health checks

## Using the Application

### 1. Register a New Account

Open http://localhost in your browser and click "Register":

```
Username: demo_user
Email: demo@example.com
Password: password123
Full Name: Demo User
```

### 2. Create Your First Issue

After logging in:
1. Click "Create New Issue"
2. Fill in the details:
   - Title: "Test Issue"
   - Description: "This is my first issue"
   - Priority: "High"
3. Click "Create Issue"

### 3. Explore Features

- **Dashboard**: View statistics and recent issues
- **Issues**: Browse, filter, and manage all issues
- **Analytics**: See metrics and distributions

## Quick Commands

```bash
# View logs
docker-compose logs -f backend
docker-compose logs -f frontend

# Stop the application
docker-compose down

# Stop and remove all data
docker-compose down -v

# Rebuild after code changes
docker-compose up -d --build

# Check service status
docker-compose ps
```

## Local Development Setup

If you prefer to run without Docker:

### 1. Start PostgreSQL

```bash
# Using Docker
docker run --name postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=issuetracker -p 5432:5432 -d postgres:15-alpine

# Or use your local PostgreSQL installation
```

### 2. Start Backend

```bash
# In project root
mvn spring-boot:run

# Backend will start on http://localhost:8080
```

### 3. Start Frontend

```bash
# In new terminal
cd frontend
npm install
npm start

# Frontend will start on http://localhost:3000
```

## Testing the API

### Using cURL

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'

# Login (save the token from response)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Create Issue (replace YOUR_TOKEN)
curl -X POST http://localhost:8080/api/issues \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "API Test Issue",
    "description": "Created via API",
    "priority": "HIGH"
  }'

# Get All Issues
curl -X GET http://localhost:8080/api/issues \
  -H "Authorization: Bearer YOUR_TOKEN"

# Get Analytics
curl -X GET http://localhost:8080/api/analytics \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Running Tests

```bash
# Backend tests
mvn test

# Frontend tests
cd frontend
npm test

# Or use the convenience script
./scripts/run-tests.sh
```

## Troubleshooting

### Docker Issues

**Port already in use:**
```bash
# Check what's using the port
lsof -i :8080  # or :80, :5432

# Change ports in docker-compose.yml if needed
```

**Containers won't start:**
```bash
# Check logs
docker-compose logs

# Restart everything
docker-compose down
docker-compose up -d
```

**Out of disk space:**
```bash
# Clean up Docker
docker system prune -a
```

### Backend Issues

**Database connection error:**
- Ensure PostgreSQL is running
- Check credentials in application.yml or .env
- Verify DATABASE_URL is correct

**Port 8080 already in use:**
```bash
# Find and kill the process
lsof -i :8080
kill -9 <PID>
```

### Frontend Issues

**npm install fails:**
```bash
# Clear cache and reinstall
cd frontend
rm -rf node_modules package-lock.json
npm install
```

**Can't connect to backend:**
- Verify backend is running: `curl http://localhost:8080/api/health`
- Check REACT_APP_API_URL in .env
- Clear browser cache

## Project Structure

```
data_analysis/
├── src/                    # Backend source code
│   ├── main/java/         # Java application code
│   └── test/java/         # Backend tests
├── frontend/              # React frontend
│   ├── src/               # Frontend source code
│   └── public/            # Static assets
├── deployment/            # Cloud deployment configs
│   ├── aws/              # AWS configurations
│   └── kubernetes/       # Kubernetes manifests
├── scripts/              # Utility scripts
├── docker-compose.yml    # Docker orchestration
├── Dockerfile            # Backend Docker image
└── pom.xml              # Maven configuration
```

## Next Steps

### Learn More
- Read [README.md](README.md) for comprehensive documentation
- Check [API.md](API.md) for complete API documentation
- See [DEPLOYMENT.md](DEPLOYMENT.md) for cloud deployment guides

### Customize
1. Update JWT secret in production (see .env.example)
2. Configure CORS for your domain
3. Set up proper database credentials
4. Enable HTTPS in production

### Deploy to Cloud
- Follow [DEPLOYMENT.md](DEPLOYMENT.md) for:
  - AWS (ECS Fargate)
  - GCP (Cloud Run)
  - IBM Cloud (Code Engine)
  - Kubernetes (any provider)

## Common Tasks

### Add a New User via Database
```sql
-- Connect to database
docker exec -it issuetracker-db psql -U postgres -d issuetracker

-- View users
SELECT id, username, email FROM users;

-- Exit
\q
```

### Reset Everything
```bash
# Stop and remove all data
docker-compose down -v

# Start fresh
docker-compose up -d
```

### Update Code and Rebuild
```bash
# Make your code changes, then:
docker-compose down
docker-compose up -d --build
```

### Export Database
```bash
docker exec issuetracker-db pg_dump -U postgres issuetracker > backup.sql
```

### Import Database
```bash
docker exec -i issuetracker-db psql -U postgres issuetracker < backup.sql
```

## Performance Tips

1. **Use connection pooling** (already configured)
2. **Enable caching** for frequently accessed data
3. **Add database indexes** for large datasets
4. **Use CDN** for frontend assets in production
5. **Scale horizontally** using load balancers

## Security Checklist

Before deploying to production:

- [ ] Change JWT secret to a strong 256-bit key
- [ ] Use strong database passwords
- [ ] Enable HTTPS/SSL
- [ ] Configure proper CORS origins
- [ ] Set up rate limiting
- [ ] Enable database backups
- [ ] Use environment variables for secrets
- [ ] Update dependencies regularly
- [ ] Enable logging and monitoring
- [ ] Set up security headers

## Getting Help

- **Documentation**: Check README.md, API.md, DEPLOYMENT.md
- **Issues**: Search existing issues on GitHub
- **Questions**: Open a new issue with the "question" label
- **Contributing**: See CONTRIBUTING.md

## What's Included

✅ Complete Java Spring Boot backend with REST API
✅ Modern React frontend with routing
✅ JWT authentication and authorization
✅ PostgreSQL database with JPA/Hibernate
✅ Docker and Docker Compose setup
✅ Comprehensive test suite
✅ CI/CD pipeline (GitHub Actions)
✅ Cloud deployment configurations
✅ Health checks and monitoring
✅ Analytics and reporting
✅ Production-ready architecture

## License

MIT License - See LICENSE file for details

---

**Enjoy building with Issue Tracker!** 🚀

For detailed documentation, see [README.md](README.md)
