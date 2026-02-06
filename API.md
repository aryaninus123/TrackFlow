# API Documentation

Complete REST API documentation for the Issue Tracker application.

## Base URL

- Local: `http://localhost:8080/api`
- Production: `https://your-domain.com/api`

## Authentication

Most endpoints require authentication using JWT tokens.

Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## Authentication Endpoints

### Register User

Create a new user account.

**Endpoint:** `POST /auth/register`

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "fullName": "John Doe"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["USER"]
}
```

**Validation Rules:**
- `username`: 3-50 characters, required, unique
- `email`: Valid email format, required, unique
- `password`: Minimum 6 characters, required
- `fullName`: Required

### Login

Authenticate and receive a JWT token.

**Endpoint:** `POST /auth/login`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["USER"]
}
```

**Error Responses:**
- `401 Unauthorized`: Invalid credentials

---

## Issue Endpoints

All issue endpoints require authentication.

### Create Issue

Create a new issue.

**Endpoint:** `POST /issues`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Login page not responding",
  "description": "When clicking the login button, nothing happens",
  "status": "OPEN",
  "priority": "HIGH",
  "assigneeId": 2
}
```

**Fields:**
- `title`: Required
- `description`: Optional
- `status`: Optional (default: OPEN) - Values: OPEN, IN_PROGRESS, RESOLVED, CLOSED, REOPENED
- `priority`: Optional (default: MEDIUM) - Values: LOW, MEDIUM, HIGH, CRITICAL
- `assigneeId`: Optional - ID of user to assign

**Response:** `201 Created`
```json
{
  "id": 1,
  "title": "Login page not responding",
  "description": "When clicking the login button, nothing happens",
  "status": "OPEN",
  "priority": "HIGH",
  "reporter": {
    "id": 1,
    "username": "john_doe",
    "fullName": "John Doe"
  },
  "assignee": {
    "id": 2,
    "username": "jane_smith",
    "fullName": "Jane Smith"
  },
  "resolvedAt": null,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

### Get All Issues

Retrieve all issues.

**Endpoint:** `GET /issues`

**Query Parameters:**
- `status` (optional): Filter by status (OPEN, IN_PROGRESS, etc.)

**Examples:**
```
GET /issues
GET /issues?status=OPEN
GET /issues?status=IN_PROGRESS
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Login page not responding",
    "description": "When clicking the login button, nothing happens",
    "status": "OPEN",
    "priority": "HIGH",
    "reporter": {
      "id": 1,
      "username": "john_doe",
      "fullName": "John Doe"
    },
    "assignee": {
      "id": 2,
      "username": "jane_smith",
      "fullName": "Jane Smith"
    },
    "resolvedAt": null,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
]
```

### Get Issue by ID

Retrieve a specific issue.

**Endpoint:** `GET /issues/{id}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Login page not responding",
  "description": "When clicking the login button, nothing happens",
  "status": "OPEN",
  "priority": "HIGH",
  "reporter": {
    "id": 1,
    "username": "john_doe",
    "fullName": "John Doe"
  },
  "assignee": {
    "id": 2,
    "username": "jane_smith",
    "fullName": "Jane Smith"
  },
  "resolvedAt": null,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

**Error Responses:**
- `404 Not Found`: Issue not found

### Update Issue

Update an existing issue.

**Endpoint:** `PUT /issues/{id}`

**Request Body:**
```json
{
  "title": "Login page not responding (Updated)",
  "status": "IN_PROGRESS",
  "priority": "CRITICAL",
  "assigneeId": 3
}
```

**Notes:**
- All fields are optional
- Only provided fields will be updated
- When status changes to RESOLVED or CLOSED, resolvedAt is automatically set

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Login page not responding (Updated)",
  "description": "When clicking the login button, nothing happens",
  "status": "IN_PROGRESS",
  "priority": "CRITICAL",
  "reporter": {
    "id": 1,
    "username": "john_doe",
    "fullName": "John Doe"
  },
  "assignee": {
    "id": 3,
    "username": "bob_jones",
    "fullName": "Bob Jones"
  },
  "resolvedAt": null,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T11:45:00Z"
}
```

### Delete Issue

Delete an issue.

**Endpoint:** `DELETE /issues/{id}`

**Response:** `204 No Content`

**Error Responses:**
- `404 Not Found`: Issue not found

### Get My Issues

Get all issues created by the authenticated user.

**Endpoint:** `GET /issues/my-issues`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "My Issue",
    ...
  }
]
```

### Get Assigned Issues

Get all issues assigned to the authenticated user.

**Endpoint:** `GET /issues/assigned-to-me`

**Response:** `200 OK`
```json
[
  {
    "id": 2,
    "title": "Assigned Issue",
    ...
  }
]
```

---

## Analytics Endpoints

### Get Analytics Dashboard

Retrieve comprehensive analytics data.

**Endpoint:** `GET /analytics`

**Response:** `200 OK`
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
    "CLOSED": 50,
    "REOPENED": 0
  },
  "priorityDistribution": {
    "LOW": 30,
    "MEDIUM": 70,
    "HIGH": 35,
    "CRITICAL": 15
  }
}
```

**Analytics Metrics:**
- `totalIssues`: Total number of issues
- `openIssues`: Count of open issues
- `closedIssues`: Count of closed issues
- `inProgressIssues`: Count of in-progress issues
- `averageResolutionTimeHours`: Average time to resolve issues (in hours)
- `statusDistribution`: Breakdown by status
- `priorityDistribution`: Breakdown by priority

---

## Health Check

### Health Status

Check if the API is running.

**Endpoint:** `GET /health`

**No authentication required**

**Response:** `200 OK`
```json
{
  "status": "UP",
  "service": "Issue Tracker API"
}
```

---

## Error Responses

### Common Error Codes

- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Missing or invalid authentication token
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/issues"
}
```

---

## Rate Limiting

Currently no rate limiting is implemented. In production, consider:
- 100 requests per minute per IP
- 1000 requests per hour per user

---

## Pagination

Pagination is not currently implemented. For large datasets, consider adding:
- `page`: Page number (default: 0)
- `size`: Items per page (default: 20)
- `sort`: Sort field and direction

Example: `GET /issues?page=0&size=20&sort=createdAt,desc`

---

## Filtering and Sorting

Currently supported:
- Filter by status: `GET /issues?status=OPEN`

Future enhancements:
- Filter by priority
- Filter by date range
- Sort by multiple fields
- Full-text search

---

## Versioning

API version: v1

Version is implicit in the current implementation. Future versions may use:
- URL versioning: `/api/v1/issues`, `/api/v2/issues`
- Header versioning: `Accept: application/vnd.issuetracker.v1+json`

---

## CORS

CORS is configured to allow requests from configured origins (see application.yml).

Default allowed origins:
- `http://localhost:3000` (development)
- `http://localhost:80` (local Docker)

Configure additional origins via `CORS_ALLOWED_ORIGINS` environment variable.

---

## Testing the API

### Using cURL

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"password123","fullName":"Test User"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"password123"}'

# Create Issue (replace TOKEN)
curl -X POST http://localhost:8080/api/issues \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Issue","priority":"HIGH"}'

# Get Issues
curl -X GET http://localhost:8080/api/issues \
  -H "Authorization: Bearer TOKEN"
```

### Using Postman

1. Import the endpoints as a collection
2. Set up environment variables for base URL and token
3. Use the auth token in subsequent requests

---

## Support

For API questions or issues:
- Open an issue on GitHub
- Check the README.md for examples
- Review the source code in the controllers
