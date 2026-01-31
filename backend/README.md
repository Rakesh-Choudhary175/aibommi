Backend README

This backend is a Spring Boot application that provides:
- Excel import endpoints for academic and extracurricular data
- Analytics to compute career suitability scores
- Profile generation using OpenAI (configurable via env vars)

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