package com.hospital.controller;

import com.hospital.exception.DuplicateEntryException;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.service.DoctorServiceImpl;
import com.hospital.service.MedicalRecordServiceImpl;
import com.hospital.service.PatientServiceImpl;
import com.hospital.util.AlertUtils;
import com.hospital.util.InputValidator;
import com.hospital.util.InputValidator.ValidationResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PatientController {

    private final PatientServiceImpl patientService;
    private final MedicalRecordServiceImpl medicalRecordService;
    private final DoctorServiceImpl doctorService;

    private final TextField searchField;
    private final TableView<Patient> patientTable;
    private final TableColumn<Patient, Integer> colId;
    private final TableColumn<Patient, String> colName;
    private final TableColumn<Patient, LocalDate> colDob;
    private final TableColumn<Patient, String> colContact;
    private final TextField txtName;
    private final DatePicker dateDob;
    private final TextField txtContact;
    private final Label lblStatus;

    private ObservableList<Patient> patientList;

    public PatientController(PatientServiceImpl patientService,
            MedicalRecordServiceImpl medicalRecordService,
            DoctorServiceImpl doctorService,
            TextField searchField,
            TableView<Patient> patientTable,
            TableColumn<Patient, Integer> colId,
            TableColumn<Patient, String> colName,
            TableColumn<Patient, LocalDate> colDob,
            TableColumn<Patient, String> colContact,
            TextField txtName,
            DatePicker dateDob,
            TextField txtContact,
            Label lblStatus) {
        this.patientService = patientService;
        this.medicalRecordService = medicalRecordService;
        this.doctorService = doctorService;
        this.searchField = searchField;
        this.patientTable = patientTable;
        this.colId = colId;
        this.colName = colName;
        this.colDob = colDob;
        this.colContact = colContact;
        this.txtName = txtName;
        this.dateDob = dateDob;
        this.txtContact = txtContact;
        this.lblStatus = lblStatus;
    }

    public void initialize() {
        setupPatientTable();
        loadPatients();
    }

    private void setupPatientTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
    }

    public void loadPatients() {
        patientList = FXCollections.observableArrayList(patientService.getAllPatients());
        patientTable.setItems(patientList);
        if (lblStatus != null) {
            lblStatus.setText("Loaded " + patientList.size() + " patients.");
        }
    }

    public void handleSearch() {
        String query = searchField.getText();
        patientList = FXCollections.observableArrayList(patientService.searchPatients(query));
        patientTable.setItems(patientList);
        if (lblStatus != null) {
            lblStatus.setText("Found " + patientList.size() + " results.");
        }
    }

    public void handleResetPatientSearch() {
        if (searchField != null) {
            searchField.clear();
        }
        loadPatients();
        if (patientTable != null) {
            patientTable.getSelectionModel().clearSelection();
        }
    }

    public void handleAddPatient() {
        ValidationResult nameResult = InputValidator.validateName(txtName.getText());
        if (!nameResult.isValid()) {
            AlertUtils.showAlert("Validation Error", nameResult.errorMessage());
            return;
        }

        ValidationResult dobResult = InputValidator.validateDateOfBirth(dateDob.getValue());
        if (!dobResult.isValid()) {
            AlertUtils.showAlert("Validation Error", dobResult.errorMessage());
            return;
        }

        if (!txtContact.getText().trim().isEmpty()) {
            ValidationResult contactResult = InputValidator.validateContact(txtContact.getText());
            if (!contactResult.isValid()) {
                AlertUtils.showAlert("Validation Error", contactResult.errorMessage());
                return;
            }
        }

        Patient newPatient = new Patient(
                txtName.getText().trim(),
                dateDob.getValue(),
                txtContact.getText().trim());

        try {
            if (patientService.addPatient(newPatient)) {
                AlertUtils.showAlert("Success", "Patient added successfully.");
                clearInputs();
                loadPatients();
            } else {
                AlertUtils.showAlert("Error", "Failed to add patient.");
            }
        } catch (DuplicateEntryException e) {
            AlertUtils.showAlert("Duplicate Entry",
                    e.getMessage() + "\n\nPlease use a different " + e.getFieldName() + ".");
        }
    }

    public void handleUpdatePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showAlert("Warning", "Please select a patient to update.");
            return;
        }

        ValidationResult nameResult = InputValidator.validateName(txtName.getText());
        if (!nameResult.isValid()) {
            AlertUtils.showAlert("Validation Error", nameResult.errorMessage());
            return;
        }

        ValidationResult dobResult = InputValidator.validateDateOfBirth(dateDob.getValue());
        if (!dobResult.isValid()) {
            AlertUtils.showAlert("Validation Error", dobResult.errorMessage());
            return;
        }

        if (!txtContact.getText().trim().isEmpty()) {
            ValidationResult contactResult = InputValidator.validateContact(txtContact.getText());
            if (!contactResult.isValid()) {
                AlertUtils.showAlert("Validation Error", contactResult.errorMessage());
                return;
            }
        }

        selected.setName(txtName.getText().trim());
        selected.setDateOfBirth(dateDob.getValue());
        selected.setContact(txtContact.getText().trim());

        if (patientService.updatePatient(selected)) {
            AlertUtils.showAlert("Success", "Patient updated successfully.");
            clearInputs();
            loadPatients();
        } else {
            AlertUtils.showAlert("Error", "Failed to update patient.");
        }
    }

    public void handleDeletePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showAlert("Warning", "Please select a patient to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Are you sure you want to delete " + selected.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (patientService.deletePatient(selected.getPatientId())) {
                AlertUtils.showAlert("Success", "Patient deleted successfully along with related records.");
                clearInputs();
                patientTable.getSelectionModel().clearSelection();
                loadPatients();
            } else {
                AlertUtils.showAlert("Error",
                        "Failed to delete patient. Please check if patient has active records that cannot be deleted.");
            }
        }
    }

    public void handleTableClick() {
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

    public void handleAddMedicalNote() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showAlert("Warning", "Please select a patient to add a medical note.");
            return;
        }

        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Add Medical Note");
        dialog.setHeaderText("Add Medical Note for: " + selected.getName());

        Label noteLabel = new Label("Medical Note:");
        TextArea noteArea = new TextArea();
        noteArea.setPrefRowCount(5);
        noteArea.setWrapText(true);
        noteArea.setPromptText("Enter medical note, observations, treatment details...");

        Label diagnosisLabel = new Label("Diagnosis:");
        TextField diagnosisField = new TextField();
        diagnosisField.setPromptText("Enter diagnosis");

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(noteLabel, noteArea, diagnosisLabel, diagnosisField);
        vbox.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (noteArea.getText().trim().isEmpty()) {
                    AlertUtils.showAlert("Validation Error", "Medical note cannot be empty.");
                    return null;
                }
                return new String[] { noteArea.getText().trim(), diagnosisField.getText().trim() };
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(data -> {
            String note = data[0];
            String diagnosis = data[1];

            List<Doctor> doctors = doctorService.getAllDoctors();
            Doctor selectedDoctor = (doctors != null && !doctors.isEmpty()) ? doctors.get(0) : null;
            int doctorId = (selectedDoctor != null) ? selectedDoctor.getDoctorId() : 1;

            if (medicalRecordService.addMedicalRecord(selected.getPatientId(), doctorId, note, diagnosis, null)) {
                AlertUtils.showAlert("Success", "Medical note added successfully to MongoDB.");
            } else {
                AlertUtils.showAlert("Error", "Failed to add medical note. Please check MongoDB connection.");
            }
        });
    }
}
