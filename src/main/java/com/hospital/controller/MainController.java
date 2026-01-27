package com.hospital.controller;

import com.hospital.dao.DepartmentDAO;
import com.hospital.model.AppointmentDTO;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.DoctorDTO;
import com.hospital.model.Patient;
import com.hospital.service.AppointmentServiceImpl;
import com.hospital.service.DoctorServiceImpl;
import com.hospital.service.MedicalRecordServiceImpl;
import com.hospital.service.PatientServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainController {

    // Sub-controllers
    private PatientController patientController;
    private DoctorController doctorController;
    private AppointmentController appointmentController;

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
    private TableView<DoctorDTO> doctorTable;
    @FXML
    private TableColumn<DoctorDTO, Integer> colDoctorId;
    @FXML
    private TableColumn<DoctorDTO, String> colDoctorName;
    @FXML
    private TableColumn<DoctorDTO, String> colSpecialization;
    @FXML
    private TableColumn<DoctorDTO, String> colDepartmentId; // Renamed to match FXML fx:id

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
    private TableView<AppointmentDTO> appointmentTable;
    @FXML
    private TableColumn<AppointmentDTO, Integer> colAppointmentId;
    @FXML
    private TableColumn<AppointmentDTO, String> colAppointmentPatient;
    @FXML
    private TableColumn<AppointmentDTO, String> colAppointmentDoctor;
    @FXML
    private TableColumn<AppointmentDTO, LocalDateTime> colAppointmentDate;
    @FXML
    private TableColumn<AppointmentDTO, String> colAppointmentStatus;

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

    public MainController() {
    }

    @FXML
    public void initialize() {
        // Initialize Services
        PatientServiceImpl patientService = new PatientServiceImpl();
        DoctorServiceImpl doctorService = new DoctorServiceImpl();
        AppointmentServiceImpl appointmentService = new AppointmentServiceImpl();
        MedicalRecordServiceImpl medicalRecordService = new MedicalRecordServiceImpl();
        DepartmentDAO departmentDAO = new DepartmentDAO();

        // Initialize Sub-controllers
        patientController = new PatientController(patientService, medicalRecordService, doctorService,
                searchField, patientTable, colId, colName, colDob, colContact,
                txtName, dateDob, txtContact, lblStatus);

        doctorController = new DoctorController(doctorService, departmentDAO,
                doctorSearchField, doctorTable, colDoctorId, colDoctorName, colSpecialization, colDepartmentId,
                txtDoctorName, txtSpecialization, comboDepartment, lblStatus);

        appointmentController = new AppointmentController(appointmentService, patientService, doctorService,
                appointmentSearchField, appointmentTable, colAppointmentId, colAppointmentPatient, colAppointmentDoctor,
                colAppointmentDate, colAppointmentStatus, comboAppointmentPatient, comboAppointmentDoctor,
                dateAppointmentDate, txtAppointmentTime, comboAppointmentStatus, txtAppointmentNotes, lblStatus);

        // Run initialization logic for each controller
        patientController.initialize();
        doctorController.initialize();
        appointmentController.initialize();
    }

    // ========== Patient Delegates ==========
    @FXML
    private void handleSearch() {
        patientController.handleSearch();
    }

    @FXML
    private void handleResetPatientSearch() {
        patientController.handleResetPatientSearch();
    }

    @FXML
    private void handleAddPatient() {
        patientController.handleAddPatient();
    }

    @FXML
    private void handleUpdatePatient() {
        patientController.handleUpdatePatient();
    }

    @FXML
    private void handleDeletePatient() {
        patientController.handleDeletePatient();
    }

    @FXML
    private void handleTableClick() {
        patientController.handleTableClick();
    }

    @FXML
    private void handleAddMedicalNote() {
        patientController.handleAddMedicalNote();
    }

    // ========== Doctor Delegates ==========
    @FXML
    private void handleDoctorSearch() {
        doctorController.handleDoctorSearch();
    }

    @FXML
    private void handleResetDoctorSearch() {
        doctorController.handleResetDoctorSearch();
    }

    @FXML
    private void handleAddDoctor() {
        doctorController.handleAddDoctor();
    }

    @FXML
    private void handleUpdateDoctor() {
        doctorController.handleUpdateDoctor();
    }

    @FXML
    private void handleDeleteDoctor() {
        doctorController.handleDeleteDoctor();
    }

    @FXML
    private void handleDoctorTableClick() {
        doctorController.handleDoctorTableClick();
    }

    // ========== Appointment Delegates ==========
    @FXML
    private void handleAppointmentSearch() {
        appointmentController.handleAppointmentSearch();
    }

    @FXML
    private void handleResetAppointmentSearch() {
        appointmentController.handleResetAppointmentSearch();
    }

    @FXML
    private void handleAddAppointment() {
        appointmentController.handleAddAppointment();
    }

    @FXML
    private void handleUpdateAppointment() {
        appointmentController.handleUpdateAppointment();
    }

    @FXML
    private void handleDeleteAppointment() {
        appointmentController.handleDeleteAppointment();
    }

    @FXML
    private void handleAppointmentTableClick() {
        appointmentController.handleAppointmentTableClick();
    }
}
