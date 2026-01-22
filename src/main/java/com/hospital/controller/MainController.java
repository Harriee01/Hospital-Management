package com.hospital.controller;

import com.hospital.dao.DepartmentDAO;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.service.DoctorServiceImpl;
import com.hospital.service.PatientServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.Optional;

public class MainController {

    // Services
    private PatientServiceImpl patientServiceImpl;
    private DoctorServiceImpl doctorServiceImpl;
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

    @FXML
    private Label lblStatus;

    // Data
    private ObservableList<Patient> patientList;
    private ObservableList<Doctor> doctorList;
    private ObservableList<Department> departmentList;

    public MainController() {
        patientServiceImpl = new PatientServiceImpl();
        doctorServiceImpl = new DoctorServiceImpl();
        departmentDAO = new DepartmentDAO();
    }

    @FXML
    public void initialize() {
        setupPatientTable();
        setupDoctorTable();
        loadPatients();
        loadDoctors();
        loadDepartments();
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

    private void loadDepartments() {
        departmentList = FXCollections.observableArrayList(departmentDAO.getAllDepartments());
        if (comboDepartment != null) {
            comboDepartment.setItems(departmentList);
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

    @FXML
    private void handleAddPatient() {
        if (!validateInputs())
            return;

        Patient newPatient = new Patient(
                txtName.getText(),
                dateDob.getValue(),
                txtContact.getText());

        if (patientServiceImpl.addPatient(newPatient)) {
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

        if (patientServiceImpl.updatePatient(selected)) {
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
            if (patientServiceImpl.deletePatient(selected.getPatientId())) {
                lblStatus.setText("Patient deleted successfully along with related records.");
                loadPatients();
            } else {
                showAlert("Error", "Failed to delete patient. Please check if patient has active records that cannot be deleted.");
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

    @FXML
    private void handleAddDoctor() {
        if (!validateDoctorInputs())
            return;

        Department selectedDept = comboDepartment.getSelectionModel().getSelectedItem();
        if (selectedDept == null) {
            showAlert("Validation Error", "Please select a department.");
            return;
        }

        Doctor newDoctor = new Doctor(
                txtDoctorName.getText(),
                txtSpecialization.getText(),
                selectedDept.getDepartmentId());

        if (doctorServiceImpl.addDoctor(newDoctor)) {
            lblStatus.setText("Doctor added successfully.");
            clearDoctorInputs();
            loadDoctors();
        } else {
            showAlert("Error", "Failed to add doctor.");
        }
    }

    @FXML
    private void handleUpdateDoctor() {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a doctor to update.");
            return;
        }
        if (!validateDoctorInputs())
            return;

        Department selectedDept = comboDepartment.getSelectionModel().getSelectedItem();
        if (selectedDept == null) {
            showAlert("Validation Error", "Please select a department.");
            return;
        }

        selected.setName(txtDoctorName.getText());
        selected.setSpecialization(txtSpecialization.getText());
        selected.setDepartmentId(selectedDept.getDepartmentId());

        if (doctorServiceImpl.updateDoctor(selected)) {
            lblStatus.setText("Doctor updated successfully.");
            clearDoctorInputs();
            loadDoctors();
        } else {
            showAlert("Error", "Failed to update doctor.");
        }
    }

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
                lblStatus.setText("Doctor deleted.");
                loadDoctors();
            } else {
                showAlert("Error", "Failed to delete doctor.");
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

    private boolean validateDoctorInputs() {
        if (txtDoctorName == null || txtSpecialization == null) {
            return false;
        }
        if (txtDoctorName.getText().isEmpty() || txtSpecialization.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill in all required fields (Name, Specialization).");
            return false;
        }
        return true;
    }

    private void clearDoctorInputs() {
        if (txtDoctorName != null) txtDoctorName.clear();
        if (txtSpecialization != null) txtSpecialization.clear();
        if (comboDepartment != null) comboDepartment.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
