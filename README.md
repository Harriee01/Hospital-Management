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

### Database Setup
1.  Open your MySQL Client (Workbench or Terminal).
2.  Run the script located at `database/hospital_schema.sql`.
    ```bash
    mysql -u root -p < database/hospital_schema.sql
    ```
3.  **Important**: Update `src/main/java/com/hospital/util/DatabaseHelper.java` with your MySQL username and password.

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
*   **Database**: 3NF Normalized MySQL Schema with Indexes.
*   **JavaFX UI**: Responsive layout with TabPane.
*   **CRUD**: Full Create, Read, Update, Delete for Patients.
*   **Search**: Fast Case-insensitive search.
*   **Optimization**: 
    *   **SQL Indexes** on `last_name`, `dob`, etc.
    *   **In-Memory Caching** (HashMap) in `PatientService` to reduce DB hits.
*   **NoSQL Design**: Validated design for Patient Medical Notes (MongoDB).

## 4. Documentation
See the `docs/` folder for:
*   [Performance Report](docs/PERFORMANCE_REPORT.md)
*   [NoSQL Design](docs/NOSQL_DESIGN.md)

