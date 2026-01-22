module com.hospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires transitive java.sql;

    opens com.hospital to javafx.fxml, javafx.graphics;
    opens com.hospital.controller to javafx.fxml;
    opens com.hospital.model to javafx.base;

    exports com.hospital;
    exports com.hospital.controller;
    exports com.hospital.model;
    exports com.hospital.dao;
    exports com.hospital.service;
    exports com.hospital.util;
}
