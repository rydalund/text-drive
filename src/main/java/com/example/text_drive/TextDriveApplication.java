package com.example.text_drive;

import com.example.text_drive.service.SystemUserService;
import com.example.text_drive.utility.DatabaseInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * The main class that launches the Spring Boot application.
 * It also adds the DatabaseInitializer listener to the application to ensure that
 * the necessary database setup is performed when the application starts.
 */
@SpringBootApplication
public class TextDriveApplication {

	public static void main(String[] args) {
		// Create a SpringApplication and add the DatabaseInitializer to handle database initialization at startup
		SpringApplication app = new SpringApplication(TextDriveApplication.class);
		app.addListeners(new DatabaseInitializer());  // Add DatabaseInitializer to manage database logic
		app.run(args);  // Run the Spring Boot application
	}

	/**
	 * Creates a CommandLineRunner Bean to create the system user at startup.
	 *
	 * @param systemUserService The service responsible for system user operations.
	 * @param fallbackPassword  The fallback password for the system user, injected from application properties.
	 * @return A CommandLineRunner to create the system user if it doesn't already exist.
	 */
	@SuppressWarnings("unused")
    @Bean
	public CommandLineRunner createSystemUser(SystemUserService systemUserService, @Value("${system.user.fallback.password}") String fallbackPassword) {
		return args -> {
			// Create the system user if it doesn't exist
			systemUserService.createSystemUserIfNotExists(fallbackPassword);
		};
	}
}