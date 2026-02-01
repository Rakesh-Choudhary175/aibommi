# AiBommi Project

Project Document SRS : https://docs.google.com/document/d/10EcDGU95Xqguh7yooUS15nqyXeMoYtIEO1NsuIAo5AQ/edit?tab=t.0

## Project Structure

The project is organized into two main distinct components:

*   **Frontend**: Source code located in the `frontend` directory. Please refer to the `frontend/README.md` file for installation, configuration, and running instructions.
*   **Backend**: Source code located in the `backend` directory. Please refer to the `backend/README.md` file for installation, configuration, and API documentation.

## Presentation & Demo

This repository includes:
*   **Presentation PPT**: Slides covering the project overview used for presentation.
*   **Demo Video**: A video demonstrating the features and usage of the application.




What the AI does
AI in RV is used only for explanation and interaction, not decision-making. Deterministic analytics engines compute student trends, subject strengths, and career scores using fixed formulas. An LLM is then used to explain these precomputed results, answer parent follow-up questions, and compare career options using Retrieval Augmented Generation (RAG).

Models used 

Claude Sonnet 4.5

Data provenance & licenses
All data is school-provided academic and extracurricular records or synthetic demo data. No external or scraped personal data is used. Data is processed per-student and isolated using student_id filters. No data is reused across users.

Hallucination, bias & guardrails

LLM cannot compute scores or invent facts

RAG retrieves only precomputed, auditable facts

Outputs must cite retrieved evidence

Fallback deterministic templates trigger on violations

Career formulas and weights are transparent and editable

Expected outcomes

Users: reduced anxiety, clearer career understanding

Schools: scalable, explainable guidance

Safety: reproducible decisions, no opaque AI judgment