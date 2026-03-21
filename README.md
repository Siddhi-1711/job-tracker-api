# Job Tracker API

A production-grade REST API for tracking job applications built with Spring Boot 3.5.

## Tech Stack

- Java 23 + Spring Boot 3.5.12
- PostgreSQL 16 (Docker)
- Spring Security + JWT (access + refresh tokens)
- Flyway migrations
- Spring Mail (Gmail SMTP)
- SpringDoc OpenAPI (Swagger UI)
- Spring Actuator
- Testcontainers + Mockito (24 tests)

## Features

- JWT authentication with refresh token rotation
- Full CRUD for job applications
- Interview round tracking
- Status history timeline (audit trail)
- Soft delete
- Email reminders for interviews and follow-ups
- Analytics funnel + CSV export
- Pagination, search, filtering
- API versioning (/api/v1/)
- Standard response wrapper
- Health monitoring via Actuator

## Setup

### Prerequisites
- Java 23
- Docker Desktop
- Maven

### Steps

1. Clone the repo
2. Copy `src/main/resources/application.properties.example` to `application.properties`
3. Set environment variables (see below)
4. Start PostgreSQL: `docker compose up -d`
5. Run: `mvn spring-boot:run`

### Environment Variables
```
DB_USERNAME=jobtracker_user
DB_PASSWORD=jobtracker_pass
JWT_SECRET=your-base64-encoded-secret-min-44-chars
MAIL_USERNAME=your@gmail.com
MAIL_PASSWORD=your-app-password
```
> **Note:** JWT_SECRET must be a Base64-encoded string.  
> Generate one with: `openssl rand -base64 32`  
> Your current secret `3cfa76ef...` is already a valid hex string encoded as Base64.
## API Endpoints

### Auth
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/auth/register | Register |
| POST | /api/v1/auth/login | Login |
| POST | /api/v1/auth/refresh | Refresh token |
| POST | /api/v1/auth/logout | Logout |

### Applications
| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/v1/applications | Get all |
| POST | /api/v1/applications | Create |
| GET | /api/v1/applications/{id} | Get by id |
| PUT | /api/v1/applications/{id} | Update |
| DELETE | /api/v1/applications/{id} | Soft delete |
| PATCH | /api/v1/applications/{id}/status | Update status |
| GET | /api/v1/applications/{id}/history | Status history |
| GET | /api/v1/applications/search | Search |
| GET | /api/v1/applications/paged | Paginated |

### Interviews
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/applications/{id}/interviews | Add round |
| GET | /api/v1/applications/{id}/interviews | Get rounds |
| PUT | /api/v1/applications/{id}/interviews/{roundId} | Update round |
| DELETE | /api/v1/applications/{id}/interviews/{roundId} | Delete round |

### Analytics
| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/v1/analytics/funnel | Funnel stats |
| GET | /api/v1/analytics/summary | Summary |
| GET | /api/v1/analytics/export/csv | Export CSV |

### Monitoring
| Method | URL | Description |
|--------|-----|-------------|
| GET | /actuator/health | Health check |
| GET | /actuator/info | App info |

## Swagger UI

http://localhost:8080/swagger-ui.html

## Running Tests
```bash
mvn test
```

24 tests — unit + integration with Testcontainers.