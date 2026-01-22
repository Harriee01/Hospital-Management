package com.hospital.controller;

import com.hospital.model.Patient;
import com.hospital.service.PatientService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.Optional;

public class MainController {

    // Services
    private PatientService patientService;

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

    @FXML
    private Label lblStatus;

    // Data
    private ObservableList<Patient> patientList;

    public MainController() {
        patientService = new PatientService();
    }

    @FXML
    public void initialize() {
        setupTable();
        loadPatients();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
    }

    private void loadPatients() {
        patientList = FXCollections.observableArrayList(patientService.getAllPatients());
        patientTable.setItems(patientList);
        lblStatus.setText("Loaded " + patientList.size() + " patients.");
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        patientList = FXCollections.observableArrayList(patientService.searchPatients(query));
        patientTable.setItems(patientList);
        lblStatus.setText("Found " + patientList.size() + " results.");
    }

    @FXML
    private void handleAddPatient() {
        if (!validateInputs())
            return;

        Patient newPatient = new Patient(
                txtName.getText(),
                dateDob.getValue(),
                txtContact.getText());

        if (patientService.addPatient(newPatient)) {
            lblStatus.setText("Patient added successfully.");
            clearInputs();
            loadPatients(); // Refresh list
        } else {
            showAlert("Error", "Failed to add patient.");
        }
    }

    @FXML
    private void handleUpdatePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a patient to update.");
            return;
        }
        if (!validateInputs())
            return;

        // Update selected object
        selected.setName(txtName.getText());
        selected.setDateOfBirth(dateDob.getValue());
        selected.setContact(txtContact.getText());

        if (patientService.updatePatient(selected)) {
            lblStatus.setText("Patient updated successfully.");
            clearInputs();
            loadPatients();
        } else {
            showAlert("Error", "Failed to update patient.");
        }
    }

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
            if (patientService.deletePatient(selected.getPatientId())) {
                lblStatus.setText("Patient deleted.");
                loadPatients();
            } else {
                showAlert("Error", "Failed to delete patient.");
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

    private boolean validateInputs() {
        if (txtName.getText().isEmpty() || dateDob.getValue() == null) {
            showAlert("Validation Error", "Please fill in all required fields (Name, DOB).");
            return false;
        }
        return true;
    }

    private void clearInputs() {
        txtName.clear();
        dateDob.setValue(null);
        txtContact.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
