package com.hospital.controller;

import com.hospital.dao.DepartmentDAO;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.DoctorDTO;
import com.hospital.service.DoctorServiceImpl;
import com.hospital.util.AlertUtils;
import com.hospital.util.InputValidator;
import com.hospital.util.InputValidator.ValidationResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class DoctorController {

    private final DoctorServiceImpl doctorService;
    private final DepartmentDAO departmentDAO;

    private final TextField doctorSearchField;
    private final TableView<DoctorDTO> doctorTable;
    private final TableColumn<DoctorDTO, Integer> colDoctorId;
    private final TableColumn<DoctorDTO, String> colDoctorName;
    private final TableColumn<DoctorDTO, String> colSpecialization;
    private final TableColumn<DoctorDTO, String> colDepartmentName;
    private final TextField txtDoctorName;
    private final TextField txtSpecialization;
    private final ComboBox<Department> comboDepartment;
    private final Label lblStatus;

    private ObservableList<DoctorDTO> doctorList;
    private ObservableList<Department> departmentList;

    public DoctorController(DoctorServiceImpl doctorService,
            DepartmentDAO departmentDAO,
            TextField doctorSearchField,
            TableView<DoctorDTO> doctorTable,
            TableColumn<DoctorDTO, Integer> colDoctorId,
            TableColumn<DoctorDTO, String> colDoctorName,
            TableColumn<DoctorDTO, String> colSpecialization,
            TableColumn<DoctorDTO, String> colDepartmentName,
            TextField txtDoctorName,
            TextField txtSpecialization,
            ComboBox<Department> comboDepartment,
            Label lblStatus) {
        this.doctorService = doctorService;
        this.departmentDAO = departmentDAO;
        this.doctorSearchField = doctorSearchField;
        this.doctorTable = doctorTable;
        this.colDoctorId = colDoctorId;
        this.colDoctorName = colDoctorName;
        this.colSpecialization = colSpecialization;
        this.colDepartmentName = colDepartmentName;
        this.txtDoctorName = txtDoctorName;
        this.txtSpecialization = txtSpecialization;
        this.comboDepartment = comboDepartment;
        this.lblStatus = lblStatus;
    }

    public void initialize() {
        setupDoctorTable();
        loadDepartments();
        loadDoctors();
    }

    private void setupDoctorTable() {
        colDoctorId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colDepartmentName.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
    }

    private void loadDepartments() {
        departmentList = FXCollections.observableArrayList(departmentDAO.getAllDepartments());
        if (comboDepartment != null) {
            comboDepartment.setItems(departmentList);
        }
    }

    public void loadDoctors() {
        if (doctorTable != null) {
            doctorList = FXCollections.observableArrayList(doctorService.getDoctorsWithDepartment());
            doctorTable.setItems(doctorList);
            if (lblStatus != null) {
                lblStatus.setText("Loaded " + doctorList.size() + " doctors.");
            }
        }
    }

    public void handleDoctorSearch() {
        if (doctorSearchField != null && doctorTable != null) {
            String query = doctorSearchField.getText();
            doctorList = FXCollections.observableArrayList(doctorService.searchDoctorsWithDepartment(query));
            doctorTable.setItems(doctorList);
            if (lblStatus != null) {
                lblStatus.setText("Found " + doctorList.size() + " doctor results.");
            }
        }
    }

    public void handleResetDoctorSearch() {
        if (doctorSearchField != null) {
            doctorSearchField.clear();
        }
        loadDoctors();
        if (doctorTable != null) {
            doctorTable.getSelectionModel().clearSelection();
        }
    }

    public void handleAddDoctor() {
        ValidationResult nameResult = InputValidator.validateName(txtDoctorName.getText());
        if (!nameResult.isValid()) {
            AlertUtils.showAlert("Validation Error", nameResult.errorMessage());
            return;
        }

        if (txtSpecialization.getText().trim().isEmpty()) {
            AlertUtils.showAlert("Validation Error", "Specialization is required.");
            return;
        }

        Department selectedDept = comboDepartment.getSelectionModel().getSelectedItem();
        if (selectedDept == null) {
            AlertUtils.showAlert("Validation Error", "Please select a department.");
            return;
        }

        Doctor newDoctor = new Doctor(
                txtDoctorName.getText().trim(),
                txtSpecialization.getText().trim(),
                selectedDept.getDepartmentId());

        if (doctorService.addDoctor(newDoctor)) {
            AlertUtils.showAlert("Success", "Doctor added successfully.");
            clearDoctorInputs();
            loadDoctors();
        } else {
            AlertUtils.showAlert("Error", "Failed to add doctor.");
        }
    }

    public void handleUpdateDoctor() {
        DoctorDTO selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showAlert("Warning", "Please select a doctor to update.");
            return;
        }

        ValidationResult nameResult = InputValidator.validateName(txtDoctorName.getText());
        if (!nameResult.isValid()) {
            AlertUtils.showAlert("Validation Error", nameResult.errorMessage());
            return;
        }

        if (txtSpecialization.getText().trim().isEmpty()) {
            AlertUtils.showAlert("Validation Error", "Specialization is required.");
            return;
        }

        Department selectedDept = comboDepartment.getSelectionModel().getSelectedItem();
        if (selectedDept == null) {
            AlertUtils.showAlert("Validation Error", "Please select a department.");
            return;
        }

        Doctor updateDoctor = new Doctor(selected.getDoctorId(),
                txtDoctorName.getText().trim(),
                txtSpecialization.getText().trim(),
                selectedDept.getDepartmentId());

        if (doctorService.updateDoctor(updateDoctor)) {
            AlertUtils.showAlert("Success", "Doctor updated successfully.");
            clearDoctorInputs();
            loadDoctors();
        } else {
            AlertUtils.showAlert("Error", "Failed to update doctor.");
        }
    }

    public void handleDeleteDoctor() {
        DoctorDTO selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showAlert("Warning", "Please select a doctor to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Are you sure you want to delete " + selected.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (doctorService.deleteDoctor(selected.getDoctorId())) {
                AlertUtils.showAlert("Success", "Doctor deleted successfully.");
                clearDoctorInputs();
                doctorTable.getSelectionModel().clearSelection();
                loadDoctors();
            } else {
                AlertUtils.showAlert("Error", "Failed to delete doctor.");
            }
        }
    }

    public void handleDoctorTableClick() {
        DoctorDTO selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtDoctorName.setText(selected.getName());
            txtSpecialization.setText(selected.getSpecialization());

            // Find and select the department
            if (departmentList != null) {
                for (Department dept : departmentList) {
                    if (dept.getDepartmentId() == selected.getDepartmentId()) {
                        comboDepartment.getSelectionModel().select(dept);
                        break;
                    }
                }
            }
        }
    }

    private void clearDoctorInputs() {
        if (txtDoctorName != null)
            txtDoctorName.clear();
        if (txtSpecialization != null)
            txtSpecialization.clear();
        if (comboDepartment != null)
            comboDepartment.getSelectionModel().clearSelection();
    }
}
