# EduPath UI

A React + Vite application for Student Performance Analysis and Career Guidance.

## Requirements

Before starting, ensure you have the following installed on your machine:

- **Node.js**: Version 20.0.0 or higher (LTS recommended)
- **npm**: Version 10.0.0 or higher

## Getting Started

Follow these steps to set up and run the application on a new machine:

1.  **Clone the repository (if applicable) or navigate to the project directory:**
    ```bash
    cd EduPath_UI
    ```

2.  **Install dependencies:**
    ```bash
    npm install
    ```

3.  **Start the development server:**
    ```bash
    npm run dev
    ```

4.  **Open the application:**
    Open your browser and navigate to the URL shown in the terminal (typically `http://localhost:5173`).

## Usage Guide

### Admin Dashboard

1.  **Login:**
    -   Go to the Admin Login section.
    -   **Username:** `admin`
    -   **Password:** `pass`

2.  **Data Management:**
    -   Once logged in, you will find two specific upload sections:
        1.  **Student Academic Data**
        2.  **Student Extracurriculars**
    -   Upload the correct data files for each section.

3.  **Analyze Data (Compulsory):**
    -   After uploading the files, you **MUST** click the **"Analyze Data"** button. This step is mandatory to process the data for the students.

### Parent/Student Login

1.  **Login:**
    -   Go to the Parent Login section.
    -   **Student ID:** Enter the student's ID (e.g., `S001`, `S002`, etc.) to log in.

2.  **Features:**
    -   **Student Marks Graph:** View visual analytics of academic performance.
    -   **Career Report:** Access a comprehensive report on career possibilities.
    -   **AI Chat:** Use the chat interface to discuss career suggestions and receive personalized guidance from the AI.
