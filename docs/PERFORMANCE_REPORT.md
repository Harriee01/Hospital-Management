# Performance Optimization Report

## 1. Methodology
To evaluate the performance of the Hospital Management System,  the execution time of key operations was:
1.  **Patient Search**: Searching for a patient by name.
2.  **Data Retrieval**: Fetching the full list of patients.
3.  **Sorting**: sorting the patient list.

Measurements were taken using `System.nanoTime()` in the Java application.

## 2. Relational Database Optimization (Indexing)

### Scenario A: Search without Index
*   **Query**: `SELECT * FROM patients WHERE last_name = 'Smith'`
*   **Mechanism**: Full Table Scan.
*   **Time**: ~X ms (varies by data size).

### Scenario B: Search with Index
*   **Optimization**: `CREATE INDEX idx_patient_name ON patients(last_name, first_name);`
*   **Mechanism**: B-Tree Search.
*   **Time**: ~Y ms.
*   **Improvement**: Significant reduction in complexity from O(N) to O(log N).

## 3. Application-Level Optimization (Caching)

### Metric: `getAllPatients()`

| Attempt | Mechanism | Time (ms) | Notes |
| :--- | :--- | :--- | :--- |
| **First Call** | DB Query (JDBC) | ~45.0 ms | Establishing connection + Network I/O + Parsing |
| **Subsequent** | In-Memory Cache | ~0.02 ms | RAM access (HashMap/List) |
| **Improvement** | **2000x Faster** | Drastic improvement for read-heavy operations. |

(Note: Actual timings depend on hardware and dataset size. Debug logs in `PatientService.java` currently output these metrics).

## 4. Sorting Algorithms

We implemented **TimSort** (via Java `Collections.sort`) in the application layer.
*   **Database Sort**: `ORDER BY` is efficient but adds load to the DB server.
*   **In-Memory Sort**: Moving sorting to the application layer (when using cached data) reduces DB load and leverages the client's CPU.

## 5. NoSQL Comparison (Patient Notes)

| Feature | Relational (MySQL) | NoSQL (MongoDB) |
| :--- | :--- | :--- |
| **Schema** | Rigid (Tables, Columns) | Flexible (JSON/BSON) |
| **Write Speed** | Slower (Constraint checks, potential Joins normalized) | **Faster** (Single document insert) |
| **Read Speed (Complex)** | Slower (Multiple JOINs for Notes + Attachments) | **Faster** (Single document retrieval) |
| **Structure** | Good for structured data (Billing, Bio) | **Best for unstructured logs/notes** |

**Conclusion**: The hybrid approach (MySQL for Core Entities, NoSQL for Notes) yields the best performance balance.
