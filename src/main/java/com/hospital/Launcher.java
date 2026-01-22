package com.hospital;

/**
 * Launcher class to start the JavaFX application.
 * This is a workaround for the "JavaFX runtime components are missing" error.
 * When running a JavaFX application directly, the main class cannot extend
 * Application.
 * This launcher class calls the actual Application class's main method.
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
