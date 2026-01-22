# NoSQL Database Design - Patient Medical Notes

## 1. Justification for NoSQL (MongoDB)

For the **Patient Medical Notes** module, a NoSQL approach was chosen ( MongoDB) for the following reasons:

1.  **Unstructured/Semi-Structured Data**: Medical notes often vary significantly in length, structure, and content. One doctor might write a simple text paragraph, while another might include structured checklists, embedded images, or dynamic fields. A rigid Relational schema would require frequent migrations or complex EAV (Entity-Attribute-Value) tables.
2.  **Scalability**: Medical logs grow rapidly. NoSQL databases are designed to scale horizontally (sharding) more easily than traditional RDBMS, making them suitable for storing vast amounts of log data.
3.  **Flexibility (Schema Evolution)**: As new medical procedures or note-taking standards emerge, the schema can evolve without downtime or complex `ALTER TABLE` operations. We can simply start adding new fields to new documents.
4.  **Aggregation**: Storing all notes for a patient in a single document (or queried easily as a collection) allows for faster retrieval of a complete "Patient Timeline" without complex joins.

## 2. Schema Design (JSON/BSON Implementation)

We will use a **Document Model**. There are two common approaches: "Embedding" vs "Referencing". Given that medical notes can grow indefinitely, **Referencing** is safer to avoid hitting the 16MB document limit in MongoDB, but for this specific use case (Patient Medical Notes), we create a collection `medical_notes` where each document represents a single interaction/note, indexed by `patient_id`.

### Collection: `medical_notes`

**Example Document:**

```json
{
  "_id": { "$oid": "653a1b2c9d8e7f001a2b3c4d" },
  "patient_id": 101,  // Reference to MySQL Patient ID
  "doctor_id": 205,   // Reference to MySQL Doctor ID
  "appointment_id": 5001, // Optional reference
  "created_at": { "$date": "2023-10-27T14:30:00Z" },
  "type": "Consultation",
  "content": {
    "chief_complaint": "Persistent migraines",
    "history_of_present_illness": "Patient reports headaches starting 2 weeks ago...",
    "vitals": {
      "bp": "120/80",
      "heart_rate": 72,
      "temp_f": 98.6
    },
    "assessment": "Migraine without aura",
    "plan": [
      "Prescribed Sumatriptan",
      "Advised rest and hydration"
    ]
  },
  "tags": ["neurology", "headache", "acute"],
  "attachments": [
    {
      "file_name": "scan_results.pdf",
      "s3_url": "https://s3.bucket/medical/scan_123.pdf",
      "uploaded_at": { "$date": "2023-10-27T14:35:00Z" }
    }
  ]
}
```

## 3. Integration Strategy

The implementation acts as a **Polyglot Persistence** architecture:
*   **MySQL**: Handles structured, transactional data (Users, Appointments, Billing, Inventory).
*   **NoSQL (MongoDB)**: Handles the "heavy lifting" of unstructured text and logs (Medical Notes).

**Application Logic:**
1.  When a doctor opens a patient's file, the application queries MySQL for `SELECT * FROM patients WHERE id = ?`.
2.  Simultaneously, it queries MongoDB for `db.medical_notes.find({ patient_id: ? }).sort({ created_at: -1 })`.
3.  The UI combines these results: MySQL provides the header (Name, Age), and MongoDB provides the scrollable history of notes.

## 4. Performance Comparison (Projected)

*   **Write Performance**: faster in NoSQL for complex objects (no need to insert into multiple normalized tables like `note_vitals`, `note_tags`, `note_paragraphs`). Insert is O(1).
*   **Read Performance**: fast retrieval of a full history list. In MySQL, reconstructing this complex object would require multiple JOINs.

## 5. Implementation Details

### 5.1 MongoDB Connection
The application uses the MongoDB Java Driver (mongodb-driver-sync) to connect to MongoDB. Connection configuration is loaded from `.env` file:
- `MONGO_URI`: MongoDB connection string (default: `mongodb://localhost:27017`)
- `MONGO_DATABASE`: Database name (default: `hospital_medical_records`)

### 5.2 Document Schema (Implemented)
Each medical record document follows this structure:
```json
{
  "_id": ObjectId("..."),
  "patientId": 101,
  "doctorId": 205,
  "appointmentId": 5001,
  "createdAt": ISODate("2023-10-27T14:30:00Z"),
  "type": "Consultation",
  "content": {
    "note": "Patient reports persistent migraines...",
    "diagnosis": "Migraine without aura",
    "treatment": "Prescribed Sumatriptan",
    "vitals": {},
    "attachments": []
  },
  "tags": ["neurology", "headache"]
}
```

### 5.3 Operations Implemented
1. **Insert Medical Record**: `MedicalRecordDAO.insertMedicalRecord()` - Saves new medical note to MongoDB
2. **Retrieve Patient Records**: `MedicalRecordDAO.getMedicalRecordsByPatient()` - Gets all records for a patient, sorted by date
3. **Aggregation Example**: `MedicalRecordDAO.getRecordCountsLast30Days()` - Counts records per patient in last 30 days for reporting

### 5.4 JavaFX Integration
- "Add Medical Note" button in Patient detail view
- Dialog form for entering medical note and diagnosis
- Saves directly to MongoDB via `MedicalRecordService`
- Success/error alerts for user feedback

### 5.5 Justification: Why MongoDB for Medical Records (200-300 words)

Medical records in a hospital setting are inherently unstructured and variable. A consultation note might be a simple paragraph, while a diagnostic report might include structured vitals, lab results, imaging attachments, and treatment plans. This variability makes relational databases (like MySQL) less suitable because:

1. **Schema Rigidity**: Relational databases require fixed schemas. To store varying medical note structures, we would need either:
   - Multiple tables (notes, vitals, attachments, tags) with complex JOINs for retrieval
   - An Entity-Attribute-Value (EAV) pattern, which sacrifices query performance
   - Frequent schema migrations as new note types emerge

2. **Write Performance**: In MySQL, inserting a medical note with vitals, tags, and attachments requires:
   - INSERT into `medical_notes` table
   - INSERT into `note_vitals` table (if vitals present)
   - INSERT into `note_tags` table (multiple rows for tags)
   - INSERT into `note_attachments` table (multiple rows for attachments)
   - All within a transaction for consistency
   This is 4+ database operations. In MongoDB, it's a single document insert (O(1) operation), typically 3-5x faster.

3. **Read Performance**: Retrieving a patient's complete medical history in MySQL requires:
   - JOIN across notes, vitals, tags, and attachments tables
   - Complex WHERE clauses to filter by patient
   - Sorting and aggregation in application layer
   In MongoDB, a single query with an index on `patientId` retrieves all records sorted by date, typically 3-5x faster.

4. **Schema Evolution**: As medical practices evolve, new note types emerge (e.g., telemedicine notes, AI-assisted diagnoses). MongoDB allows adding new fields to documents without schema migrations, while MySQL requires ALTER TABLE operations that can lock tables during execution.

5. **Text Search**: Medical notes often require full-text search capabilities. MongoDB provides text indexes and aggregation pipelines for searching within note content, which is more efficient than MySQL's LIKE queries on large text fields.

**Conclusion**: The hybrid architecture (MySQL for structured transactional data, MongoDB for unstructured medical logs) provides optimal performance, flexibility, and maintainability for a hospital management system.