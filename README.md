# PlaceTrack - College Placement Management System

A full-stack web application for managing college placement activities, built with Spring Boot backend and React/Vite frontend.

## Features

### Backend (Spring Boot)
- User authentication (register, login, logout) with BCrypt password hashing
- Student CRUD with search and filtering
- Company management with stats
- Placement drives management
- Job opportunities management
- Placements tracking
- Dashboard with real-time statistics
- Profile management (skills, projects, certifications, internships)
- Resume upload/download/delete
- MySQL database integration with JPA

### Frontend (React + Vite)
- Login and registration pages with validation
- Dashboard with statistics and recent placements
- Student management (CRUD, search, filter)
- Company management (CRUD, search, filter, stats)
- Placement management (CRUD, filter)
- Protected routes and authentication
- Responsive sidebar navigation
- Toast notifications
- Confirmation dialogs
- Form validation

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- MySQL 8.0
- BCrypt password hashing
- Maven build

### Frontend
- React 19.3.0
- Vite 8.3.0
- React Router 7.18.3
- Axios 1.20.0
- CSS (custom styles)

## Project Structure

```
PlaceTrack/
├── backend/                 # Spring Boot backend
│   ├── src/main/java/      # Java source code
│   │   └── com/placetrack/backend/
│   │       ├── controller/ # REST controllers
│   │       ├── service/    # Business logic
│   │       ├── entity/     # JPA entities
│   │       ├── repository/ # Data access
│   │       ├── dto/        # Data transfer objects
│   │       ├── config/     # Configuration
│   │       └── security/   # Authentication
│   ├── src/main/resources/ # Application config
│   └── pom.xml            # Maven dependencies
│
├── frontend/               # React frontend
│   ├── src/
│   │   ├── components/    # Reusable components
│   │   ├── pages/         # Page components
│   │   ├── services/      # API services
│   │   ├── App.jsx        # Main app component
│   │   └── main.jsx       # Entry point
│   ├── public/            # Static assets
│   ├── package.json       # npm dependencies
│   └── vite.config.js     # Vite configuration
│
└── .gitignore             # Git ignore rules
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Node.js 18+ and npm
- MySQL 8.0+

### Backend Setup

1. Navigate to backend directory:
   ```bash
   cd backend
   ```

2. Configure database in `src/main/resources/application.properties` or set environment variables:
   ```bash
   export DB_HOST=localhost
   export DB_PORT=3306
   export DB_NAME=placetrack_db
   export DB_USER=root
   export DB_PASSWORD=your_password
   ```

3. Build and run:
   ```bash
   mvn spring-boot:run
   ```
   
   The backend runs on http://localhost:8080

### Frontend Setup

1. Navigate to frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run development server:
   ```bash
   npm run dev
   ```
   
   The frontend runs on http://localhost:5173

## API Endpoints

### Authentication
- POST /api/auth/register - Register new user
- POST /api/auth/login - Login user
- POST /api/auth/logout - Logout user

### Students
- GET /api/students - List all students (with search/filter)
- POST /api/students - Create student
- GET /api/students/{id} - Get student by ID
- PUT /api/students/{id} - Update student
- DELETE /api/students/{id} - Delete student

### Companies
- GET /api/companies - List all companies (with search)
- POST /api/companies - Create company
- GET /api/companies/{id} - Get company by ID
- PUT /api/companies/{id} - Update company
- DELETE /api/companies/{id} - Delete company
- GET /api/companies/stats - Get company statistics

### Placement Drives
- GET /api/placement-drives - List drives (with upcoming filter)
- POST /api/placement-drives - Create drive
- GET /api/placement-drives/{id} - Get drive by ID
- PUT /api/placement-drives/{id} - Update drive
- DELETE /api/placement-drives/{id} - Delete drive

### Jobs
- GET /api/jobs - List jobs (with search/filter)
- POST /api/jobs - Create job
- GET /api/jobs/{id} - Get job by ID
- PUT /api/jobs/{id} - Update job
- DELETE /api/jobs/{id} - Delete job

### Placements
- GET /api/placements - List placements (with filter)
- POST /api/placements - Create placement
- GET /api/placements/{id} - Get placement by ID
- PUT /api/placements/{id} - Update placement
- DELETE /api/placements/{id} - Delete placement

### Dashboard
- GET /api/dashboard - Get dashboard statistics

### Profile (Authenticated)
- GET /api/profile - Get current user profile
- PUT /api/profile - Update profile
- POST /api/profile/resume - Upload resume
- GET /api/profile/resume - Download resume
- DELETE /api/profile/resume - Delete resume

## Environment Variables

Create a `.env` file in the backend directory (gitignored):

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=placetrack_db
DB_USER=root
DB_PASSWORD=your_mysql_password
UPLOAD_DIR=./uploads
```

## Security

- Passwords are hashed using BCrypt
- Authentication uses token-based session management
- CORS is configured for frontend-backend communication
- Sensitive files (.env, passwords) are gitignored

## License

This project is part of the PlaceTrack college placement management system.
