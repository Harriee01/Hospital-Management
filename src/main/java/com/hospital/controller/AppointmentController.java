package com.hospital.controller;

import com.hospital.model.Appointment;
import com.hospital.model.AppointmentDTO;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.service.AppointmentServiceImpl;
import com.hospital.service.DoctorServiceImpl;
import com.hospital.service.PatientServiceImpl;
import com.hospital.util.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class AppointmentController {

    private final AppointmentServiceImpl appointmentService;
    private final PatientServiceImpl patientService;
    private final DoctorServiceImpl doctorService;

    private final TextField appointmentSearchField;
    private final TableView<AppointmentDTO> appointmentTable;
    private final TableColumn<AppointmentDTO, Integer> colAppointmentId;
    private final TableColumn<AppointmentDTO, String> colAppointmentPatient;
    private final TableColumn<AppointmentDTO, String> colAppointmentDoctor;
    private final TableColumn<AppointmentDTO, LocalDateTime> colAppointmentDate;
    private final TableColumn<AppointmentDTO, String> colAppointmentStatus;

    private final ComboBox<Patient> comboAppointmentPatient;
    private final ComboBox<Doctor> comboAppointmentDoctor;
    private final DatePicker dateAppointmentDate;
    private final TextField txtAppointmentTime;
    private final ComboBox<String> comboAppointmentStatus;
    private final TextArea txtAppointmentNotes;
    private final Label lblStatus;

    private ObservableList<AppointmentDTO> appointmentList;
    private ObservableList<Patient> allPatientsForCombo;
    private ObservableList<Doctor> allDoctorsForCombo;

    public AppointmentController(AppointmentServiceImpl appointmentService,
            PatientServiceImpl patientService,
            DoctorServiceImpl doctorService,
            TextField appointmentSearchField,
            TableView<AppointmentDTO> appointmentTable,
            TableColumn<AppointmentDTO, Integer> colAppointmentId,
            TableColumn<AppointmentDTO, String> colAppointmentPatient,
            TableColumn<AppointmentDTO, String> colAppointmentDoctor,
            TableColumn<AppointmentDTO, LocalDateTime> colAppointmentDate,
            TableColumn<AppointmentDTO, String> colAppointmentStatus,
            ComboBox<Patient> comboAppointmentPatient,
            ComboBox<Doctor> comboAppointmentDoctor,
            DatePicker dateAppointmentDate,
            TextField txtAppointmentTime,
            ComboBox<String> comboAppointmentStatus,
            TextArea txtAppointmentNotes,
            Label lblStatus) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentSearchField = appointmentSearchField;
        this.appointmentTable = appointmentTable;
        this.colAppointmentId = colAppointmentId;
        this.colAppointmentPatient = colAppointmentPatient;
        this.colAppointmentDoctor = colAppointmentDoctor;
        this.colAppointmentDate = colAppointmentDate;
        this.colAppointmentStatus = colAppointmentStatus;
        this.comboAppointmentPatient = comboAppointmentPatient;
        this.comboAppointmentDoctor = comboAppointmentDoctor;
        this.dateAppointmentDate = dateAppointmentDate;
        this.txtAppointmentTime = txtAppointmentTime;
        this.comboAppointmentStatus = comboAppointmentStatus;
        this.txtAppointmentNotes = txtAppointmentNotes;
        this.lblStatus = lblStatus;
    }

    public void initialize() {
        setupAppointmentTable();
        loadAppointments();
    }

    private void setupAppointmentTable() {
        if (colAppointmentId != null) {
            colAppointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            colAppointmentPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
            colAppointmentDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
            colAppointmentDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
            colAppointmentStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
    }

    public void loadAppointments() {
        if (appointmentTable != null) {
            appointmentList = FXCollections.observableArrayList(appointmentService.getAllAppointmentsWithNames());
            appointmentTable.setItems(appointmentList);

            // Populate patient and doctor combos for appointment form
            if (comboAppointmentPatient != null) {
                allPatientsForCombo = FXCollections.observableArrayList(patientService.getAllPatients());
                comboAppointmentPatient.setItems(allPatientsForCombo);
            }
            if (comboAppointmentDoctor != null) {
                allDoctorsForCombo = FXCollections.observableArrayList(doctorService.getAllDoctors());
                comboAppointmentDoctor.setItems(allDoctorsForCombo);
            }

            // Populate status combo
            if (comboAppointmentStatus != null) {
                ObservableList<String> statuses = FXCollections.observableArrayList("Scheduled", "Completed",
                        "Cancelled");
                comboAppointmentStatus.setItems(statuses);
                comboAppointmentStatus.getSelectionModel().select("Scheduled");
            }
        }
    }

    public void handleAppointmentSearch() {
        if (appointmentSearchField != null && appointmentTable != null) {
            String query = appointmentSearchField.getText();
            appointmentList = FXCollections
                    .observableArrayList(appointmentService.searchAppointmentsWithNames(query));
            appointmentTable.setItems(appointmentList);
            if (lblStatus != null) {
                lblStatus.setText("Found " + appointmentList.size() + " appointment results.");
            }
        }
    }

    public void handleResetAppointmentSearch() {
        if (appointmentSearchField != null) {
            appointmentSearchField.clear();
        }
        loadAppointments();
        if (appointmentTable != null) {
            appointmentTable.getSelectionModel().clearSelection();
        }
    }

    public void handleAddAppointment() {
        if (comboAppointmentPatient.getSelectionModel().getSelectedItem() == null) {
            AlertUtils.showAlert("Validation Error", "Please select a patient.");
            return;
        }
        if (comboAppointmentDoctor.getSelectionModel().getSelectedItem() == null) {
            AlertUtils.showAlert("Validation Error", "Please select a doctor.");
            return;
        }
        if (dateAppointmentDate.getValue() == null) {
            AlertUtils.showAlert("Validation Error", "Please select an appointment date.");
            return;
        }
        if (txtAppointmentTime.getText().trim().isEmpty()) {
            AlertUtils.showAlert("Validation Error", "Please enter appointment time (HH:MM format).");
            return;
        }

        LocalTime time;
        try {
            time = LocalTime.parse(txtAppointmentTime.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            AlertUtils.showAlert("Validation Error",
                    "Invalid time format. Please use HH:MM format (e.g., 09:00, 14:30).");
            return;
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(dateAppointmentDate.getValue(), time);

        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            AlertUtils.showAlert("Validation Error", "Appointment date and time cannot be in the past.");
            return;
        }

        Patient selectedPatient = comboAppointmentPatient.getSelectionModel().getSelectedItem();
        Doctor selectedDoctor = comboAppointmentDoctor.getSelectionModel().getSelectedItem();

        if (appointmentService.hasConflict(selectedDoctor.getDoctorId(), appointmentDateTime, -1)) {
            AlertUtils.showAlert("Conflict Error",
                    "This doctor already has an appointment scheduled at " +
                            appointmentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                            ". Please choose a different time.");
            return;
        }

        String status = comboAppointmentStatus.getSelectionModel().getSelectedItem();
        if (status == null || status.isEmpty()) {
            status = "Scheduled";
        }

        Appointment newAppointment = new Appointment(
                selectedPatient.getPatientId(),
                selectedDoctor.getDoctorId(),
                status,
                appointmentDateTime);

        if (appointmentService.addAppointment(newAppointment)) {
            AlertUtils.showAlert("Success", "Appointment added successfully.");
            clearAppointmentInputs();
            loadAppointments();
        } else {
            AlertUtils.showAlert("Error", "Failed to add appointment.");
        }
    }

    public void handleUpdateAppointment() {
        AppointmentDTO selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showAlert("Warning", "Please select an appointment to update.");
            return;
        }

        if (comboAppointmentPatient.getSelectionModel().getSelectedItem() == null) {
            AlertUtils.showAlert("Validation Error", "Please select a patient.");
            return;
        }
        if (comboAppointmentDoctor.getSelectionModel().getSelectedItem() == null) {
            AlertUtils.showAlert("Validation Error", "Please select a doctor.");
            return;
        }
        if (dateAppointmentDate.getValue() == null) {
            AlertUtils.showAlert("Validation Error", "Please select an appointment date.");
            return;
        }
        if (txtAppointmentTime.getText().trim().isEmpty()) {
            AlertUtils.showAlert("Validation Error", "Please enter appointment time (HH:MM format).");
            return;
        }

        LocalTime time;
        try {
            time = LocalTime.parse(txtAppointmentTime.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            AlertUtils.showAlert("Validation Error",
                    "Invalid time format. Please use HH:MM format (e.g., 09:00, 14:30).");
            return;
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(dateAppointmentDate.getValue(), time);

        Patient selectedPatient = comboAppointmentPatient.getSelectionModel().getSelectedItem();
        Doctor selectedDoctor = comboAppointmentDoctor.getSelectionModel().getSelectedItem();

        if (appointmentService.hasConflict(selectedDoctor.getDoctorId(), appointmentDateTime,
                selected.getAppointmentId())) {
            AlertUtils.showAlert("Conflict Error",
                    "This doctor already has an appointment scheduled at " +
                            appointmentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                            ". Please choose a different time.");
            return;
        }

        String status = comboAppointmentStatus.getSelectionModel().getSelectedItem();
        if (status == null || status.isEmpty()) {
            status = selected.getStatus();
        }

        Appointment updateAppointment = new Appointment(
                selected.getAppointmentId(),
                selectedPatient.getPatientId(),
                selectedDoctor.getDoctorId(),
                status,
                appointmentDateTime);

        if (appointmentService.updateAppointment(updateAppointment)) {
            AlertUtils.showAlert("Success", "Appointment updated successfully.");
            clearAppointmentInputs();
            loadAppointments();
        } else {
            AlertUtils.showAlert("Error", "Failed to update appointment.");
        }
    }

    public void handleDeleteAppointment() {
        AppointmentDTO selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showAlert("Warning", "Please select an appointment to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setContentText("Are you sure you want to delete this appointment?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (appointmentService.deleteAppointment(selected.getAppointmentId())) {
                AlertUtils.showAlert("Success", "Appointment deleted successfully.");
                clearAppointmentInputs();
                appointmentTable.getSelectionModel().clearSelection();
                loadAppointments();
            } else {
                AlertUtils.showAlert("Error", "Failed to delete appointment.");
            }
        }
    }

    public void handleAppointmentTableClick() {
        AppointmentDTO selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (comboAppointmentPatient.getItems() != null) {
                for (Patient p : comboAppointmentPatient.getItems()) {
                    if (p.getPatientId() == selected.getPatientId()) {
                        comboAppointmentPatient.getSelectionModel().select(p);
                        break;
                    }
                }
            }

            if (comboAppointmentDoctor.getItems() != null) {
                for (Doctor d : comboAppointmentDoctor.getItems()) {
                    if (d.getDoctorId() == selected.getDoctorId()) {
                        comboAppointmentDoctor.getSelectionModel().select(d);
                        break;
                    }
                }
            }

            dateAppointmentDate.setValue(selected.getAppointmentDate().toLocalDate());
            txtAppointmentTime.setText(selected.getAppointmentDate().format(DateTimeFormatter.ofPattern("HH:mm")));
            comboAppointmentStatus.getSelectionModel().select(selected.getStatus());
        }
    }

    private void clearAppointmentInputs() {
        if (comboAppointmentPatient != null)
            comboAppointmentPatient.getSelectionModel().clearSelection();
        if (comboAppointmentDoctor != null)
            comboAppointmentDoctor.getSelectionModel().clearSelection();
        if (dateAppointmentDate != null)
            dateAppointmentDate.setValue(null);
        if (txtAppointmentTime != null)
            txtAppointmentTime.clear();
        if (comboAppointmentStatus != null)
            comboAppointmentStatus.getSelectionModel().select("Scheduled");
        if (txtAppointmentNotes != null)
            txtAppointmentNotes.clear();
    }
}
