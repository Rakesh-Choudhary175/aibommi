Backend README

This backend is a Spring Boot application that provides:
- Excel import endpoints for academic and extracurricular data
- Analytics to compute career suitability scores
- Data persistence using MySQL database
- Profile generation using AI (FastRouter using Claude Sonnet model)

## Getting Started

### Prerequisites
- Java 21 SDK
- MySQL Server (running on port 3306)

### Configuration
The application is configured in `edupath/src/main/resources/application.properties`.

1. **Database**:
   Ensure your MySQL server is running and creates the database `edupath_db`.
   Default credentials are `root` / `root`.
   You can override these with environment variables:
   - `JDBC_DATABASE_USERNAME`
   - `JDBC_DATABASE_PASSWORD`

2. **AI Service**:
   The application uses FastRouter (OpenAI-compatible) for profile generations using the Claude Sonnet model.
   - Default key is configured in `application.properties`.
   - Update `fastrouter.api.key` if needed.

### Running the Application
Navigate to the `edupath` directory:
```bash
cd edupath
```

Run using Gradle Wrapper:
```bash
./gradlew bootRun
```
Or on Windows:
```cmd
gradlew.bat bootRun
```

The server will start on `http://localhost:8080`.

### API Documentation
Once the application is running, you can access the Swagger UI documentation at:
http://localhost:8080/swagger-ui/index.html

## Features
Profile generation configuration
- Set environment variable OPENAI_API_KEY with your OpenAI API key.
- Optional: OPENAI_API_URL and OPENAI_API_MODEL to override defaults.

Endpoints (summary)
- POST /api/upload/academics - multipart Excel upload for academic records
- POST /api/upload/extracurriculars - multipart Excel upload for activities
- POST /api/students/analyze - compute and persist career scores for all students
- GET /api/students/{id}/careers - fetch persisted career scores for student
- POST /api/students/{id}/profile/generate - build profile JSON, send to OpenAI, and save summary

Notes
- Development: CORS is currently open to all origins. Restrict in production.