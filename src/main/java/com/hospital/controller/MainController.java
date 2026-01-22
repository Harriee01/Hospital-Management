package com.hospital.controller;

import com.hospital.dao.DepartmentDAO;
import com.hospital.model.Appointment;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.service.AppointmentServiceImpl;
import com.hospital.service.DoctorServiceImpl;
import com.hospital.service.PatientServiceImpl;
import com.hospital.util.InputValidator;
import com.hospital.util.InputValidator.ValidationResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class MainController {

    // Services
    private PatientServiceImpl patientServiceImpl;
    private DoctorServiceImpl doctorServiceImpl;
    private AppointmentServiceImpl appointmentServiceImpl;
    private DepartmentDAO departmentDAO;

    // UI Components - Patients Tab
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Patient> patientTable;
    @FXML
    private TableColumn<Patient, Integer> colId;
    @FXML
    private TableColumn<Patient, String> colName;
    @FXML
    private TableColumn<Patient, LocalDate> colDob;
    @FXML
    private TableColumn<Patient, String> colContact;

    // UI Components - Patient Inputs
    @FXML
    private TextField txtName;
    @FXML
    private DatePicker dateDob;
    @FXML
    private TextField txtContact;

    // UI Components - Doctors Tab
    @FXML
    private TextField doctorSearchField;
    @FXML
    private TableView<Doctor> doctorTable;
    @FXML
    private TableColumn<Doctor, Integer> colDoctorId;
    @FXML
    private TableColumn<Doctor, String> colDoctorName;
    @FXML
    private TableColumn<Doctor, String> colSpecialization;
    @FXML
    private TableColumn<Doctor, Integer> colDepartmentId;

    // UI Components - Doctor Inputs
    @FXML
    private TextField txtDoctorName;
    @FXML
    private TextField txtSpecialization;
    @FXML
    private ComboBox<Department> comboDepartment;

    // UI Components - Appointments Tab
    @FXML
    private TextField appointmentSearchField;
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, Integer> colAppointmentId;
    @FXML
    private TableColumn<Appointment, Integer> colAppointmentPatient;
    @FXML
    private TableColumn<Appointment, Integer> colAppointmentDoctor;
    @FXML
    private TableColumn<Appointment, LocalDateTime> colAppointmentDate;
    @FXML
    private TableColumn<Appointment, String> colAppointmentStatus;

    // UI Components - Appointment Inputs
    @FXML
    private ComboBox<Patient> comboAppointmentPatient;
    @FXML
    private ComboBox<Doctor> comboAppointmentDoctor;
    @FXML
    private DatePicker dateAppointmentDate;
    @FXML
    private TextField txtAppointmentTime;
    @FXML
    private ComboBox<String> comboAppointmentStatus;
    @FXML
    private TextArea txtAppointmentNotes;

    @FXML
    private Label lblStatus;

    // Data
    private ObservableList<Patient> patientList;
    private ObservableList<Doctor> doctorList;
    private ObservableList<Department> departmentList;
    private ObservableList<Appointment> appointmentList;

    public MainController() {
        patientServiceImpl = new PatientServiceImpl();
        doctorServiceImpl = new DoctorServiceImpl();
        appointmentServiceImpl = new AppointmentServiceImpl();
        departmentDAO = new DepartmentDAO();
    }

    @FXML
    public void initialize() {
        setupPatientTable();
        setupDoctorTable();
        setupAppointmentTable();
        loadPatients();
        loadDoctors();
        loadDepartments();
        loadAppointments();
    }

    private void setupPatientTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
    }

    private void setupDoctorTable() {
        colDoctorId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colDepartmentId.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
    }

    private void setupAppointmentTable() {
        if (colAppointmentId != null) {
            colAppointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            colAppointmentPatient.setCellValueFactory(new PropertyValueFactory<>("patientId"));
            colAppointmentDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
            colAppointmentDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
            colAppointmentStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
    }

    private void loadDepartments() {
        departmentList = FXCollections.observableArrayList(departmentDAO.getAllDepartments());
        if (comboDepartment != null) {
            comboDepartment.setItems(departmentList);
        }
    }

    private void loadAppointments() {
        if (appointmentTable != null) {
            appointmentList = FXCollections.observableArrayList(appointmentServiceImpl.getAllAppointments());
            appointmentTable.setItems(appointmentList);
            
            // Populate patient and doctor combos for appointment form
            if (comboAppointmentPatient != null && patientList != null) {
                comboAppointmentPatient.setItems(patientList);
            }
            if (comboAppointmentDoctor != null && doctorList != null) {
                comboAppointmentDoctor.setItems(doctorList);
            }
            
            // Populate status combo
            if (comboAppointmentStatus != null) {
                ObservableList<String> statuses = FXCollections.observableArrayList("Scheduled", "Completed", "Cancelled");
                comboAppointmentStatus.setItems(statuses);
                comboAppointmentStatus.getSelectionModel().select("Scheduled");
            }
        }
    }

    private void loadPatients() {
        patientList = FXCollections.observableArrayList(patientServiceImpl.getAllPatients());
        patientTable.setItems(patientList);
        lblStatus.setText("Loaded " + patientList.size() + " patients.");
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        patientList = FXCollections.observableArrayList(patientServiceImpl.searchPatients(query));
        patientTable.setItems(patientList);
        lblStatus.setText("Found " + patientList.size() + " results.");
    }

    /**
     * Resets the patient search by clearing the search field and reloading all patients.
     * This fulfills requirement #2: Add "Reset" / "Clear Search" button functionality.
     */
    @FXML
    private void handleResetPatientSearch() {
        if (searchField != null) {
            searchField.clear();
        }
        loadPatients(); // Reload full unfiltered list
        if (patientTable != null) {
            patientTable.getSelectionModel().clearSelection();
        }
    }

    /**
     * Handles adding a new patient with input validation.
     * Uses InputValidator to validate name, contact, and date of birth.
     * Clears inputs only on successful operation (requirement #3).
     * 
     * This fulfills requirement #1: Input validation using Regex.
     */
    @FXML
    private void handleAddPatient() {
        // Validate name using regex pattern
        ValidationResult nameResult = InputValidator.validateName(txtName.getText());
        if (!nameResult.isValid()) {
            showAlert("Validation Error", nameResult.errorMessage());
            return; // Don't clear inputs on validation error
        }

        // Validate date of birth
        ValidationResult dobResult = InputValidator.validateDateOfBirth(dateDob.getValue());
        if (!dobResult.isValid()) {
            showAlert("Validation Error", dobResult.errorMessage());
            return; // Don't clear inputs on validation error
        }

        // Validate contact (optional field, but if provided, must be valid)
        if (!txtContact.getText().trim().isEmpty()) {
            ValidationResult contactResult = InputValidator.validateContact(txtContact.getText());
            if (!contactResult.isValid()) {
                showAlert("Validation Error", contactResult.errorMessage());
                return; // Don't clear inputs on validation error
            }
        }

        // All validations passed, create patient
        Patient newPatient = new Patient(
                txtName.getText().trim(),
                dateDob.getValue(),
                txtContact.getText().trim());

        if (patientServiceImpl.addPatient(newPatient)) {
            showAlert("Success", "Patient added successfully.");
            clearInputs(); // Clear inputs only on success (requirement #3)
            loadPatients(); // Refresh list
        } else {
            showAlert("Error", "Failed to add patient.");
            // Don't clear inputs on DB error - user can correct and retry
        }
    }

    /**
     * Handles updating an existing patient with input validation.
     * Uses InputValidator to validate name, contact, and date of birth.
     * Clears inputs only on successful operation (requirement #3).
     * 
     * This fulfills requirement #1: Input validation using Regex.
     */
    @FXML
    private void handleUpdatePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a patient to update.");
            return;
        }

        // Validate name using regex pattern
        ValidationResult nameResult = InputValidator.validateName(txtName.getText());
        if (!nameResult.isValid()) {
            showAlert("Validation Error", nameResult.errorMessage());
            return; // Don't clear inputs on validation error
        }

        // Validate date of birth
        ValidationResult dobResult = InputValidator.validateDateOfBirth(dateDob.getValue());
        if (!dobResult.isValid()) {
            showAlert("Validation Error", dobResult.errorMessage());
            return; // Don't clear inputs on validation error
        }

        // Validate contact (optional field, but if provided, must be valid)
        if (!txtContact.getText().trim().isEmpty()) {
            ValidationResult contactResult = InputValidator.validateContact(txtContact.getText());
            if (!contactResult.isValid()) {
                showAlert("Validation Error", contactResult.errorMessage());
                return; // Don't clear inputs on validation error
            }
        }

        // All validations passed, update patient
        selected.setName(txtName.getText().trim());
        selected.setDateOfBirth(dateDob.getValue());
        selected.setContact(txtContact.getText().trim());

        if (patientServiceImpl.updatePatient(selected)) {
            showAlert("Success", "Patient updated successfully.");
            clearInputs(); // Clear inputs only on success (requirement #3)
            loadPatients();
        } else {
            showAlert("Error", "Failed to update patient.");
            // Don't clear inputs on DB error - user can correct and retry
        }
    }

    /**
     * Handles deleting a patient.
     * Clears inputs and table selection only on successful operation (requirement #3).
     */
    @FXML
    private void handleDeletePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a patient to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Are you sure you want to delete " + selected.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (patientServiceImpl.deletePatient(selected.getPatientId())) {
                showAlert("Success", "Patient deleted successfully along with related records.");
                clearInputs(); // Clear inputs only on success (requirement #3)
                patientTable.getSelectionModel().clearSelection(); // Clear table selection
                loadPatients();
            } else {
                showAlert("Error", "Failed to delete patient. Please check if patient has active records that cannot be deleted.");
                // Don't clear inputs on error
            }
        }
    }

    @FXML
    private void handleTableClick() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtName.setText(selected.getName());
            dateDob.setValue(selected.getDateOfBirth());
            txtContact.setText(selected.getContact());
        }
    }


    private void clearInputs() {
        txtName.clear();
        dateDob.setValue(null);
        txtContact.clear();
    }

    // ========== Doctor Management Methods ==========

    private void loadDoctors() {
        if (doctorTable != null) {
            doctorList = FXCollections.observableArrayList(doctorServiceImpl.getAllDoctors());
            doctorTable.setItems(doctorList);
            if (lblStatus != null) {
                lblStatus.setText("Loaded " + doctorList.size() + " doctors.");
            }
        }
    }

    @FXML
    private void handleDoctorSearch() {
        if (doctorSearchField != null && doctorTable != null) {
            String query = doctorSearchField.getText();
            doctorList = FXCollections.observableArrayList(doctorServiceImpl.searchDoctors(query));
            doctorTable.setItems(doctorList);
            if (lblStatus != null) {
                lblStatus.setText("Found " + doctorList.size() + " doctor results.");
            }
        }
    }

    /**
     * Resets the doctor search by clearing the search field and reloading all doctors.
     * This fulfills requirement #2: Add "Reset" / "Clear Search" button functionality.
     */
    @FXML
    private void handleResetDoctorSearch() {
        if (doctorSearchField != null) {
            doctorSearchField.clear();
        }
        loadDoctors(); // Reload full unfiltered list
        if (doctorTable != null) {
            doctorTable.getSelectionModel().clearSelection();
        }
    }

    /**
     * Handles adding a new doctor with input validation.
     * Uses InputValidator to validate doctor name.
     * Clears inputs only on successful operation (requirement #3).
     * 
     * This fulfills requirement #1: Input validation using Regex.
     */
    @FXML
    private void handleAddDoctor() {
        // Validate doctor name using regex pattern
        ValidationResult nameResult = InputValidator.validateName(txtDoctorName.getText());
        if (!nameResult.isValid()) {
            showAlert("Validation Error", nameResult.errorMessage());
            return; // Don't clear inputs on validation error
        }

        // Validate specialization (required field)
        if (txtSpecialization.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Specialization is required.");
            return; // Don't clear inputs on validation error
        }

        Department selectedDept = comboDepartment.getSelectionModel().getSelectedItem();
        if (selectedDept == null) {
            showAlert("Validation Error", "Please select a department.");
            return; // Don't clear inputs on validation error
        }

        // All validations passed, create doctor
        Doctor newDoctor = new Doctor(
                txtDoctorName.getText().trim(),
                txtSpecialization.getText().trim(),
                selectedDept.getDepartmentId());

        if (doctorServiceImpl.addDoctor(newDoctor)) {
            showAlert("Success", "Doctor added successfully.");
            clearDoctorInputs(); // Clear inputs only on success (requirement #3)
            loadDoctors();
        } else {
            showAlert("Error", "Failed to add doctor.");
            // Don't clear inputs on DB error - user can correct and retry
        }
    }

    /**
     * Handles updating an existing doctor with input validation.
     * Uses InputValidator to validate doctor name.
     * Clears inputs only on successful operation (requirement #3).
     * 
     * This fulfills requirement #1: Input validation using Regex.
     */
    @FXML
    private void handleUpdateDoctor() {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a doctor to update.");
            return;
        }

        // Validate doctor name using regex pattern
        ValidationResult nameResult = InputValidator.validateName(txtDoctorName.getText());
        if (!nameResult.isValid()) {
            showAlert("Validation Error", nameResult.errorMessage());
            return; // Don't clear inputs on validation error
        }

        // Validate specialization (required field)
        if (txtSpecialization.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Specialization is required.");
            return; // Don't clear inputs on validation error
        }

        Department selectedDept = comboDepartment.getSelectionModel().getSelectedItem();
        if (selectedDept == null) {
            showAlert("Validation Error", "Please select a department.");
            return; // Don't clear inputs on validation error
        }

        // All validations passed, update doctor
        selected.setName(txtDoctorName.getText().trim());
        selected.setSpecialization(txtSpecialization.getText().trim());
        selected.setDepartmentId(selectedDept.getDepartmentId());

        if (doctorServiceImpl.updateDoctor(selected)) {
            showAlert("Success", "Doctor updated successfully.");
            clearDoctorInputs(); // Clear inputs only on success (requirement #3)
            loadDoctors();
        } else {
            showAlert("Error", "Failed to update doctor.");
            // Don't clear inputs on DB error - user can correct and retry
        }
    }

    /**
     * Handles deleting a doctor.
     * Clears inputs and table selection only on successful operation (requirement #3).
     */
    @FXML
    private void handleDeleteDoctor() {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a doctor to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Are you sure you want to delete " + selected.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (doctorServiceImpl.deleteDoctor(selected.getDoctorId())) {
                showAlert("Success", "Doctor deleted successfully.");
                clearDoctorInputs(); // Clear inputs only on success (requirement #3)
                doctorTable.getSelectionModel().clearSelection(); // Clear table selection
                loadDoctors();
            } else {
                showAlert("Error", "Failed to delete doctor.");
                // Don't clear inputs on error
            }
        }
    }

    @FXML
    private void handleDoctorTableClick() {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtDoctorName.setText(selected.getName());
            txtSpecialization.setText(selected.getSpecialization());
            
            // Find and select the department
            for (Department dept : departmentList) {
                if (dept.getDepartmentId() == selected.getDepartmentId()) {
                    comboDepartment.getSelectionModel().select(dept);
                    break;
                }
            }
        }
    }


    private void clearDoctorInputs() {
        if (txtDoctorName != null) txtDoctorName.clear();
        if (txtSpecialization != null) txtSpecialization.clear();
        if (comboDepartment != null) comboDepartment.getSelectionModel().clearSelection();
    }

    /**
     * Shows an alert dialog with the given title and content.
     * Uses appropriate alert type based on title (Success uses INFORMATION, Error uses ERROR).
     */
    // ========== Appointment Management Methods ==========


    @FXML
    private void handleAppointmentSearch() {
        if (appointmentSearchField != null && appointmentTable != null) {
            String query = appointmentSearchField.getText();
            appointmentList = FXCollections.observableArrayList(appointmentServiceImpl.searchAppointments(query));
            appointmentTable.setItems(appointmentList);
            if (lblStatus != null) {
                lblStatus.setText("Found " + appointmentList.size() + " appointment results.");
            }
        }
    }

    /**
     * Resets the appointment search by clearing the search field and reloading all appointments.
     * This fulfills requirement #2: Add "Reset" / "Clear Search" button functionality.
     */
    @FXML
    private void handleResetAppointmentSearch() {
        if (appointmentSearchField != null) {
            appointmentSearchField.clear();
        }
        loadAppointments(); // Reload full unfiltered list
        if (appointmentTable != null) {
            appointmentTable.getSelectionModel().clearSelection();
        }
    }

    /**
     * Handles adding a new appointment with duplicate/conflict checking.
     * This fulfills requirement #5: Appointment management with duplicate prevention.
     * 
     * Logic:
     * 1. Validate all required fields
     * 2. Parse date and time
     * 3. Check for conflicts using hasConflict() method
     * 4. If no conflict, create appointment
     * 5. Clear inputs only on success (requirement #3)
     */
    @FXML
    private void handleAddAppointment() {
        // Validate required fields
        if (comboAppointmentPatient.getSelectionModel().getSelectedItem() == null) {
            showAlert("Validation Error", "Please select a patient.");
            return;
        }
        if (comboAppointmentDoctor.getSelectionModel().getSelectedItem() == null) {
            showAlert("Validation Error", "Please select a doctor.");
            return;
        }
        if (dateAppointmentDate.getValue() == null) {
            showAlert("Validation Error", "Please select an appointment date.");
            return;
        }
        if (txtAppointmentTime.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter appointment time (HH:MM format).");
            return;
        }

        // Parse time
        LocalTime time;
        try {
            time = LocalTime.parse(txtAppointmentTime.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            showAlert("Validation Error", "Invalid time format. Please use HH:MM format (e.g., 09:00, 14:30).");
            return;
        }

        // Combine date and time
        LocalDateTime appointmentDateTime = LocalDateTime.of(dateAppointmentDate.getValue(), time);

        // Check if appointment is in the past
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            showAlert("Validation Error", "Appointment date and time cannot be in the past.");
            return;
        }

        // Get selected patient and doctor
        Patient selectedPatient = comboAppointmentPatient.getSelectionModel().getSelectedItem();
        Doctor selectedDoctor = comboAppointmentDoctor.getSelectionModel().getSelectedItem();

        // Check for duplicate/conflict (requirement #5)
        // For new appointments, excludeAppointmentId is -1
        if (appointmentServiceImpl.hasConflict(selectedDoctor.getDoctorId(), appointmentDateTime, -1)) {
            showAlert("Conflict Error", 
                "This doctor already has an appointment scheduled at " + 
                appointmentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + 
                ". Please choose a different time.");
            return; // Don't clear inputs on conflict
        }

        // Get status (default to "Scheduled" if not selected)
        String status = comboAppointmentStatus.getSelectionModel().getSelectedItem();
        if (status == null || status.isEmpty()) {
            status = "Scheduled";
        }

        // Create appointment
        Appointment newAppointment = new Appointment(
                selectedPatient.getPatientId(),
                selectedDoctor.getDoctorId(),
                status,
                appointmentDateTime);

        if (appointmentServiceImpl.addAppointment(newAppointment)) {
            showAlert("Success", "Appointment added successfully.");
            clearAppointmentInputs(); // Clear inputs only on success (requirement #3)
            loadAppointments();
        } else {
            showAlert("Error", "Failed to add appointment.");
            // Don't clear inputs on DB error
        }
    }

    /**
     * Handles updating an existing appointment with duplicate/conflict checking.
     * This fulfills requirement #5: Appointment management with duplicate prevention.
     */
    @FXML
    private void handleUpdateAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an appointment to update.");
            return;
        }

        // Validate required fields
        if (comboAppointmentPatient.getSelectionModel().getSelectedItem() == null) {
            showAlert("Validation Error", "Please select a patient.");
            return;
        }
        if (comboAppointmentDoctor.getSelectionModel().getSelectedItem() == null) {
            showAlert("Validation Error", "Please select a doctor.");
            return;
        }
        if (dateAppointmentDate.getValue() == null) {
            showAlert("Validation Error", "Please select an appointment date.");
            return;
        }
        if (txtAppointmentTime.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter appointment time (HH:MM format).");
            return;
        }

        // Parse time
        LocalTime time;
        try {
            time = LocalTime.parse(txtAppointmentTime.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            showAlert("Validation Error", "Invalid time format. Please use HH:MM format (e.g., 09:00, 14:30).");
            return;
        }

        // Combine date and time
        LocalDateTime appointmentDateTime = LocalDateTime.of(dateAppointmentDate.getValue(), time);

        // Get selected patient and doctor
        Patient selectedPatient = comboAppointmentPatient.getSelectionModel().getSelectedItem();
        Doctor selectedDoctor = comboAppointmentDoctor.getSelectionModel().getSelectedItem();

        // Check for duplicate/conflict (requirement #5)
        // For updates, exclude the current appointment ID
        if (appointmentServiceImpl.hasConflict(selectedDoctor.getDoctorId(), appointmentDateTime, selected.getAppointmentId())) {
            showAlert("Conflict Error", 
                "This doctor already has an appointment scheduled at " + 
                appointmentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + 
                ". Please choose a different time.");
            return; // Don't clear inputs on conflict
        }

        // Get status
        String status = comboAppointmentStatus.getSelectionModel().getSelectedItem();
        if (status == null || status.isEmpty()) {
            status = selected.getStatus(); // Keep existing status if not changed
        }

        // Update appointment
        selected.setPatientId(selectedPatient.getPatientId());
        selected.setDoctorId(selectedDoctor.getDoctorId());
        selected.setAppointmentDate(appointmentDateTime);
        selected.setStatus(status);

        if (appointmentServiceImpl.updateAppointment(selected)) {
            showAlert("Success", "Appointment updated successfully.");
            clearAppointmentInputs(); // Clear inputs only on success (requirement #3)
            loadAppointments();
        } else {
            showAlert("Error", "Failed to update appointment.");
            // Don't clear inputs on DB error
        }
    }

    /**
     * Handles deleting an appointment.
     * Clears inputs and table selection only on successful operation (requirement #3).
     */
    @FXML
    private void handleDeleteAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an appointment to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Are you sure you want to delete this appointment?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (appointmentServiceImpl.deleteAppointment(selected.getAppointmentId())) {
                showAlert("Success", "Appointment deleted successfully.");
                clearAppointmentInputs(); // Clear inputs only on success (requirement #3)
                appointmentTable.getSelectionModel().clearSelection(); // Clear table selection
                loadAppointments();
            } else {
                showAlert("Error", "Failed to delete appointment.");
                // Don't clear inputs on error
            }
        }
    }

    /**
     * Handles clicking on an appointment in the table to populate the form.
     */
    @FXML
    private void handleAppointmentTableClick() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Find and select patient
            for (Patient p : patientList) {
                if (p.getPatientId() == selected.getPatientId()) {
                    comboAppointmentPatient.getSelectionModel().select(p);
                    break;
                }
            }
            
            // Find and select doctor
            for (Doctor d : doctorList) {
                if (d.getDoctorId() == selected.getDoctorId()) {
                    comboAppointmentDoctor.getSelectionModel().select(d);
                    break;
                }
            }
            
            // Set date and time
            dateAppointmentDate.setValue(selected.getAppointmentDate().toLocalDate());
            txtAppointmentTime.setText(selected.getAppointmentDate().format(DateTimeFormatter.ofPattern("HH:mm")));
            
            // Set status
            comboAppointmentStatus.getSelectionModel().select(selected.getStatus());
        }
    }

    /**
     * Clears all appointment input fields.
     */
    private void clearAppointmentInputs() {
        if (comboAppointmentPatient != null) comboAppointmentPatient.getSelectionModel().clearSelection();
        if (comboAppointmentDoctor != null) comboAppointmentDoctor.getSelectionModel().clearSelection();
        if (dateAppointmentDate != null) dateAppointmentDate.setValue(null);
        if (txtAppointmentTime != null) txtAppointmentTime.clear();
        if (comboAppointmentStatus != null) comboAppointmentStatus.getSelectionModel().select("Scheduled");
        if (txtAppointmentNotes != null) txtAppointmentNotes.clear();
    }

    private void showAlert(String title, String content) {
        Alert.AlertType alertType = title.equals("Success") ? Alert.AlertType.INFORMATION : 
                                    title.equals("Error") ? Alert.AlertType.ERROR :
                                    Alert.AlertType.INFORMATION;
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
