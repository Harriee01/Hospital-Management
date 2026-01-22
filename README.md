# Hospital Management System

A comprehensive JavaFX application for managing hospital records, including Patients, Doctors, and Medical Notes.

## Project Structure
*   `src/main/java`: Source code (Controllers, Models, Services, DAOs).
*   `src/main/resources`: FXML views.
*   `database`: SQL Scripts for MySQL.
*   `docs`: Design documents, Performance reports, and Git workflow.

## 1. Setup Instructions

### Prerequisites
*   Java JDK 17+
*   Maven 3.x
*   MySQL Server 8.x
*   MongoDB Server 4.4+ (for medical records storage)

### Configuration Setup (.env file)
1.  Copy the example environment file:
    ```bash
    cp .env.example .env
    ```
2.  Edit `.env` file and update with your actual database credentials:
    ```
    DB_URL=jdbc:mysql://localhost:3306/hospital_db
    DB_USER=root
    DB_PASSWORD=your_mysql_password_here
    MONGO_URI=mongodb://localhost:27017
    MONGO_DATABASE=hospital_medical_records
    ```
3.  **Important**: Never commit `.env` to version control (it's in `.gitignore`).
    The application will fall back to system environment variables if `.env` is not found.

### Database Setup
1.  **MySQL Setup**: Open your MySQL Client (Workbench or Terminal).
    ```bash
    mysql -u root -p < database/hospital_schema.sql
    ```
    This creates the database schema with UNIQUE constraints for duplicate prevention.

2.  **MongoDB Setup**: Ensure MongoDB is running on your system.
    ```bash
    # Start MongoDB (varies by OS)
    # Windows: net start MongoDB
    # Linux/Mac: sudo systemctl start mongod
    ```
    The application will automatically create the `hospital_medical_records` database and collection on first use.

## 2. Running the Application

This is a Maven project. You can run it using your IDE (IntelliJ IDEA) or the command line.

**Command Line:**
```bash
mvn javafx:run
```

**IntelliJ IDEA:**
1.  Open the project folder.
2.  Reload Maven project.
3.  Run `com.hospital.Main`.

## 3. Features Implemented

### Core Features
*   **Database**: 3NF Normalized MySQL Schema with Indexes and UNIQUE constraints.
*   **JavaFX UI**: Responsive layout with TabPane for Patients, Doctors, and Appointments.
*   **CRUD**: Full Create, Read, Update, Delete for Patients, Doctors, and Appointments.
*   **Search**: Fast Case-insensitive search with reset functionality.

### Data Integrity
*   **Duplicate Prevention**: 
    - Database level: UNIQUE constraints on `Patient.contact` and composite UNIQUE on `(name, date_of_birth)`
    - Application level: Pre-insert validation checks with user-friendly error messages
    - Graceful handling of SQL duplicate key violations (MySQL error code 1062)

### Performance Optimization
*   **SQL Indexes** on frequently queried columns (patient name, appointment date, doctor department, etc.)
*   **In-Memory Caching** (HashMap) in Service layers to reduce DB hits (2000x faster for cached reads)
*   **Connection Pooling** for efficient database connection management

### NoSQL Integration (MongoDB)
*   **Medical Records Storage**: Patient medical notes, diagnoses, and treatment history stored in MongoDB
*   **Polyglot Persistence**: MySQL for structured data (patients, doctors, appointments), MongoDB for unstructured medical logs
*   **UI Integration**: "Add Medical Note" button in patient detail view saves to MongoDB
*   **Aggregation Support**: Example aggregation queries for reporting (e.g., record counts per patient)

### Configuration Management
*   **.env File Support**: Sensitive credentials loaded from `.env` file (never hardcoded)
*   **Secure Defaults**: Falls back to system environment variables if `.env` missing
*   **Git Ignored**: `.env` file excluded from version control for security

## 4. Documentation
See the `docs/` folder for:
*   [Performance Report](docs/PERFORMANCE_REPORT.md)
*   [NoSQL Design](docs/NOSQL_DESIGN.md)

