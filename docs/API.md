# 📚 VotingSystem API Reference

Complete API documentation for the VotingSystem REST endpoints, including authentication, user management, and error handling.

## Table of Contents

1. [API Overview](#api-overview)
2. [Authentication](#authentication)
3. [Base URLs & Headers](#base-urls--headers)
4. [Authentication Endpoints](#authentication-endpoints)
5. [User Management Endpoints](#user-management-endpoints)
6. [Error Responses](#error-responses)
7. [Rate Limiting](#rate-limiting)
8. [Examples](#examples)

## API Overview

The VotingSystem API is a RESTful service that provides secure user authentication and management functionality. All endpoints return JSON responses and follow standard HTTP status codes.

### API Characteristics

- **Protocol**: HTTPS (recommended for production)
- **Data Format**: JSON
- **Authentication**: JWT Bearer tokens
- **CORS**: Configurable cross-origin support
- **Rate Limiting**: Planned feature for production

### Base Information

| Property | Value |
|----------|-------|
| **Base URL** | `http://localhost:8080` (development) |
| **API Version** | v1 (implicit) |
| **Content-Type** | `application/json` |
| **Authentication** | Bearer token |

## Authentication

The API uses a two-step authentication process:

1. **Google OAuth2 Validation**: Verify Google access tokens
2. **JWT Token Generation**: Issue custom JWT tokens for API access

### Token Types

| Token Type | Purpose | Expiry | Usage |
|------------|---------|--------|-------|
| **Access Token** | API authentication | 1 hour | `Authorization: Bearer <token>` |
| **Refresh Token** | Token renewal | 7 days | Future refresh endpoint |

### JWT Claims Structure

```json
{
  "sub": "user@example.com",
  "email": "user@example.com",
  "name": "John Doe",
  "userId": 1,
  "type": "access-token",
  "iat": 1719849000,
  "exp": 1719852600
}
```

## Base URLs & Headers

### Development Environment
```
Base URL: http://localhost:8080
```

### Production Environment
```
Base URL: https://your-domain.com
```

### Common Headers

#### For All Requests
```http
Content-Type: application/json
Accept: application/json
```

#### For Protected Endpoints
```http
Authorization: Bearer <your_jwt_token>
```

## Authentication Endpoints

All authentication endpoints are under the `/auth` path and do not require authentication.

### POST `/auth/google`

Authenticate a user using a Google OAuth2 access token.

#### Request

**URL**: `POST /auth/google`

**Headers**:
```http
Content-Type: application/json
```

**Body**:
```json
{
  "googleToken": "google_oauth2_access_token"
}
```

**Parameters**:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `googleToken` | string | Yes | Valid Google OAuth2 access token |

#### Success Response

**Status**: `200 OK`

```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresAt": "2025-06-28T15:30:00.000Z",
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "department": null
}
```

**Response Fields**:

| Field | Type | Description |
|-------|------|-------------|
| `accessToken` | string | JWT access token for API authentication |
| `refreshToken` | string | JWT refresh token for token renewal |
| `expiresAt` | string (ISO 8601) | Access token expiration timestamp |
| `id` | number | User's database ID |
| `email` | string | User's email address |
| `name` | string | User's display name |
| `department` | string\|null | User's department (future feature) |

#### Error Responses

**Invalid Google Token**:
```http
Status: 400 Bad Request
```
```json
{
  "error": "Invalid request",
  "message": "Invalid Google token",
  "timestamp": "2025-06-28T14:30:00",
  "status": 400
}
```

**Server Error**:
```http
Status: 500 Internal Server Error
```
```json
{
  "error": "Internal server error",
  "message": "An unexpected error occurred",
  "timestamp": "2025-06-28T14:30:00",
  "status": 500
}
```

#### Example Usage

**cURL**:
```bash
curl -X POST http://localhost:8080/auth/google \
  -H "Content-Type: application/json" \
  -d '{
    "googleToken": "ya29.a0AfH6SMC..."
  }'
```

**JavaScript**:
```javascript
const response = await fetch('/auth/google', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    googleToken: 'ya29.a0AfH6SMC...'
  })
});

const authData = await response.json();
```

### GET `/auth/test`

Health check endpoint for the authentication service.

#### Request

**URL**: `GET /auth/test`

**Headers**: None required

#### Success Response

**Status**: `200 OK`

```json
"Auth endpoint is working"
```

#### Example Usage

**cURL**:
```bash
curl -X GET http://localhost:8080/auth/test
```

## User Management Endpoints

All user management endpoints require authentication and are under the `/api/users` path.

### GET `/api/users/me`

Retrieve the current authenticated user's information.

#### Request

**URL**: `GET /api/users/me`

**Headers**:
```http
Authorization: Bearer <jwt
