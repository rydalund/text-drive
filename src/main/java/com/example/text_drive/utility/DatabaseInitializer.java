package com.example.text_drive.utility;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

/**
 * A utility class that checks whether the specified database exists and creates it if it does not exist.
 * It uses the properties defined in the application environment for database connection details.
 */
public class DatabaseInitializer implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    /**
     * This method is called when the `ApplicationEnvironmentPreparedEvent` is triggered.
     * It attempts to initialize the database by checking if it exists and creating it if necessary.
     *
     * @param event The application environment prepared event.
     */
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Environment env = event.getEnvironment();  // Get environment properties

        // Get database connection properties from the environment
        String url = env.getProperty("spring.datasource.url");
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");

        // Log an error if any of the properties are missing
        if (url == null || username == null || password == null) {
            logger.error("Database properties not found in environment");
            return;
        }

        String databaseName = extractDatabaseName(url);  // Extract the database name from the URL
        String baseUrl = "jdbc:postgresql://localhost:5432/postgres";  // Default base URL for PostgreSQL

        logger.info("Attempting to initialize database: {}", databaseName);

        try (Connection connection = DriverManager.getConnection(baseUrl, username, password)) {
            // Check if the database exists, and create it if not
            if (!databaseExists(connection, databaseName)) {
                createDatabase(connection, databaseName);
            }
        } catch (SQLException e) {
            // Log error if database initialization fails
            logger.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Extracts the database name from the provided URL.
     *
     * @param url The JDBC connection URL.
     * @return The extracted database name.
     */
    private String extractDatabaseName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);  // Extract the part after the last "/"
    }

    /**
     * Checks if a database with the specified name exists in the connected PostgreSQL instance.
     *
     * @param connection The database connection.
     * @param databaseName The name of the database to check.
     * @return True if the database exists, otherwise false.
     * @throws SQLException If there is an error executing the query.
     */
    private boolean databaseExists(Connection connection, String databaseName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + databaseName + "'"
            );
            return resultSet.next();  // If a result exists, the database is present
        }
    }

    /**
     * Creates a new database with the specified name if it does not already exist.
     *
     * @param connection The database connection.
     * @param databaseName The name of the database to create.
     * @throws SQLException If there is an error executing the creation query.
     */
    private void createDatabase(Connection connection, String databaseName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE " + databaseName);  // Execute the SQL statement to create the database
            logger.info("Successfully created database '{}'", databaseName);  // Log success message

            try {
                Thread.sleep(1000);  // Wait for a second to allow the database to initialize properly
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Restore the interrupt status if interrupted
            }
        }
    }
}