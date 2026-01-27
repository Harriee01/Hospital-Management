module com.hospital {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires transitive java.sql;

    // MySQL JDBC Driver (automatic module name from mysql-connector-j JAR)
    requires mysql.connector.j;

    // MongoDB driver modules
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires io.github.cdimascio.dotenv.java;

    // Dotenv Java - Non-modular JAR, needs to be on classpath
    // Configure Maven/IDE to add it with --add-reads=com.hospital=ALL-UNNAMED

    opens com.hospital to javafx.fxml, javafx.graphics;
    opens com.hospital.controller to javafx.fxml;
    opens com.hospital.model to javafx.base;

    exports com.hospital;
    exports com.hospital.controller;
    exports com.hospital.model;
    exports com.hospital.dao;
    exports com.hospital.service;
    exports com.hospital.util;
    exports com.hospital.exception;
}
