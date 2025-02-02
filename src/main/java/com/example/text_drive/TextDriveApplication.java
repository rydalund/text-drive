package com.example.text_drive;

import com.example.text_drive.utility.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class that launches the Spring Boot application.
 * It also adds the DatabaseInitializer listener to the application to ensure that
 * the necessary database setup is performed when the application starts.
 */
@SpringBootApplication
public class TextDriveApplication {

	public static void main(String[] args) {
		// Create a SpringApplication instance for the TextDriveApplication class
		SpringApplication app = new SpringApplication(TextDriveApplication.class);

		// Add the DatabaseInitializer listener to handle database initialization on startup
		app.addListeners(new DatabaseInitializer());

		app.run(args);
	}
}
