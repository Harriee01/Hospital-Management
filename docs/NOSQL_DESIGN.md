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
