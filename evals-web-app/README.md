# Evals Web App

A comprehensive web application for managing AI evaluation records with human-in-the-loop scoring capabilities.

## Features

### Core Functionality
- **Evaluation Table**: Display evaluation records with input, response, LLM score/reason, and human score/reason
- **Detail Modals**: Click details buttons to view full content of any field in expandable modals
- **Human Scoring**: Update human scores and reasons directly from the interface
- **Run Management**: Create, view, edit, and manage evaluation runs
- **File-based Storage**: All data persisted to JSON files on disk

### User Interface
- **Bootstrap 5**: Clean, responsive design using Bootstrap components
- **Interactive Modals**: View full field content without leaving the page
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Real-time Updates**: Changes saved immediately to disk

## Getting Started

### Prerequisites
- Java 21
- Maven 3.6+

### Running the Application

1. **Navigate to the module directory:**
   ```bash
   cd evals-web-app
   ```

2. **Build and run:**
   ```bash
   ../mvnw spring-boot:run
   ```

3. **Access the application:**
   - Open your browser to: http://localhost:8081
   - The app will redirect to the main evaluations table

### Sample Data

The application comes with sample evaluation data that includes:
- Sample evaluation records with different scores
- Two sample evaluation runs
- Various input/response examples

## Application Structure

### Pages
1. **Home Page** (`/`) - Welcome screen with navigation options
2. **Evaluations Table** (`/evaluations`) - Main table view with all records
3. **Run Management** (`/runs`) - View and manage evaluation runs
4. **New Run** (`/runs/new`) - Create new evaluation runs
5. **Record Details** (`/evaluations/{id}/detail`) - Detailed view of individual records

### Key Features
- **Filter by Run**: Use the dropdown to filter records by evaluation run
- **Details Buttons**: Click "Details" buttons to view full field content in modals
- **Human Scoring**: Click "Score" buttons to add or update human evaluation scores
- **Run Actions**: Create, edit, duplicate, and delete evaluation runs

## Data Model

### EvaluationRecord
- `id` - Unique identifier
- `runId` - Associated evaluation run
- `input` - Original input text
- `response` - AI-generated response
- `llmScore` - AI evaluation score (Good/Bad/Unknown)
- `llmReason` - AI reasoning for the score
- `humanScore` - Human evaluation score (Good/Bad/Unknown)
- `humanReason` - Human reasoning for the score
- `timestamp` - Record creation time
- `metadata` - Additional information (model, processing time, etc.)

### EvaluationRun
- `id` - Unique identifier
- `name` - Display name for the run
- `description` - Detailed description
- `status` - Current status (Created/Running/Completed/Failed/Cancelled)
- `totalRecords` - Expected number of records
- `completedRecords` - Number of completed records
- `configuration` - Run configuration settings

## File Storage

Data is stored in JSON files in the `src/main/data` directory:
- `evaluation-records.json` - All evaluation records
- `evaluation-runs.json` - All evaluation runs
- `input-questions.json` - Input questions for creating new evaluation runs

The application automatically creates sample data on first run if these files don't exist.

## Configuration

Key configuration in `application.yml`:
- `evals.data.directory` - Directory for data files (default: `evals-web-app/src/main/data`)
- `evals.questions.filename` - Input questions filename (default: `input-questions.json`)
- `server.port` - Application port (default: 8081)

## API Endpoints

### Web Pages
- `GET /` - Home page (redirects to `/evaluations`)
- `GET /evaluations` - Main evaluations table
- `GET /evaluations?runId={id}` - Filter by specific run
- `GET /evaluations/{id}/detail` - Record detail page
- `GET /runs` - Runs management page
- `GET /runs/new` - Create new run page

### API Actions
- `POST /evaluations/{id}/human-score` - Update human score
- `GET /evaluations/{id}/field/{fieldName}` - Get field content for modals
- `DELETE /evaluations/{id}` - Delete evaluation record
- `POST /runs/new` - Create new run
- `DELETE /runs/{id}` - Delete run
- `POST /runs/{id}/duplicate` - Duplicate run

### Health & Monitoring
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

## Development

### Project Structure
```
evals-web-app/
├── src/main/java/org/rag4j/evals/
│   ├── EvalsWebApplication.java        # Main application class
│   ├── controller/                     # MVC controllers
│   │   ├── HomeController.java
│   │   ├── EvaluationController.java
│   │   └── RunController.java
│   ├── service/                        # Business logic
│   │   └── EvaluationDataService.java
│   ├── model/                          # Data models
│   │   ├── EvaluationRecord.java
│   │   ├── EvaluationRun.java
│   │   ├── ScoreType.java
│   │   └── ...
│   └── config/                         # Configuration
│       └── WebConfig.java
├── src/main/resources/
│   ├── templates/                      # Thymeleaf templates
│   │   ├── fragments/template.html
│   │   ├── home.html
│   │   └── evals/
│   │       ├── evaluations-table.html
│   │       ├── record-detail.html
│   │       ├── runs-list.html
│   │       └── new-run.html
│   ├── static/                         # Static resources
│   │   ├── css/evals.css
│   │   └── js/evals.js
│   └── application.yml                 # Configuration
└── src/main/data/                      # Data files
    ├── evaluation-records.json
    └── evaluation-runs.json
```

### Technology Stack
- **Spring Boot 3.5.5** - Main framework
- **Thymeleaf** - Template engine
- **Bootstrap 5** - CSS framework
- **Jackson** - JSON processing
- **Maven** - Build tool

## Future Enhancements

Potential improvements for the application:
- Database integration (PostgreSQL, MySQL)
- User authentication and authorization
- CSV/JSON import/export functionality
- Advanced filtering and search
- Batch operations for human scoring
- API rate limiting
- Integration with external evaluation services

## Contributing

1. Follow the existing code structure and patterns
2. Use Bootstrap classes wherever possible for styling
3. Ensure responsive design for all new components
4. Add appropriate logging for debugging
5. Test manually with various screen sizes

---

**Happy Evaluating! 📊✨**
