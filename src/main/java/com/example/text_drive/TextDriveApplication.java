package com.example.text_drive;

import com.example.text_drive.config.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TextDriveApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(TextDriveApplication.class);
		app.addListeners(new DatabaseInitializer());
		app.run(args);
	}
}
