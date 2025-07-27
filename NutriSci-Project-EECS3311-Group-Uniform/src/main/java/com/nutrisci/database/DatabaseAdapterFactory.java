package com.nutrisci.database;

/**
 * Factory class for creating database adapters.
 * This allows the application to easily switch between different
 * database types without changing the main business logic.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public class DatabaseAdapterFactory {
    
    /**
     * Enum for supported database types
     */
    public enum DatabaseType {
        MYSQL,
        POSTGRESQL
        // Can easily add more database types here
    }
    
    /**
     * Creates a database adapter based on the specified type
     * helped by AI
     * @param type The type of database adapter to create
     * @return The appropriate database adapter
     */
    public static DatabaseAdapter createDatabaseAdapter(DatabaseType type) {
        switch (type) {
            case MYSQL:
                return new MySQLDatabaseAdapter();
            case POSTGRESQL:
                return new PostgreSQLDatabaseAdapter();
            default:
                // Default to MySQL if unknown type
                System.out.println("Unknown database type, defaulting to MySQL");
                return new MySQLDatabaseAdapter();
        }
    }
    
    /**
     * Creates a MySQL database adapter
     * @return MySQL database adapter
     */
    public static DatabaseAdapter createMySQLAdapter() {
        return new MySQLDatabaseAdapter();
    }
    
    /**
     * Creates a PostgreSQL database adapter
     * @return PostgreSQL database adapter
     */
    public static DatabaseAdapter createPostgreSQLAdapter() {
        return new PostgreSQLDatabaseAdapter();
    }
} 