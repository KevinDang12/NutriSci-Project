package com.nutrisci.database;

/**
 * Manager class for database adapters.
 * This provides a singleton instance of the database adapter
 * and allows switching between different database types at runtime.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public class DatabaseAdapterManager {
    
    private static DatabaseAdapterManager instance;
    private DatabaseAdapter currentAdapter;
    
    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseAdapterManager() {
        // Default to MySQL adapter
        this.currentAdapter = DatabaseAdapterFactory.createMySQLAdapter();
    }
    
    /**
     * Gets the singleton instance of DatabaseAdapterManager
     * helped by AI
     * @return DatabaseAdapterManager instance
     */
    public static DatabaseAdapterManager getInstance() {
        if (instance == null) {
            instance = new DatabaseAdapterManager();
        }
        return instance;
    }
    
    /**
     * Gets the current database adapter
     * @return Current database adapter
     */
    public DatabaseAdapter getCurrentAdapter() {
        return currentAdapter;
    }
    
    /**
     * Switches to a different database type
     * helped by AI
     * @param type The database type to switch to
     */
    public void switchDatabase(DatabaseAdapterFactory.DatabaseType type) {
        // Close current connection if it exists
        if (currentAdapter != null) {
            currentAdapter.closeConnection();
        }
        
        // Create new adapter
        currentAdapter = DatabaseAdapterFactory.createDatabaseAdapter(type);
        System.out.println("Switched to " + type + " database adapter");
    }
    
    /**
     * Switches to MySQL database
     */
    public void switchToMySQL() {
        switchDatabase(DatabaseAdapterFactory.DatabaseType.MYSQL);
    }
    
    /**
     * Switches to PostgreSQL database
     */
    public void switchToPostgreSQL() {
        switchDatabase(DatabaseAdapterFactory.DatabaseType.POSTGRESQL);
    }
    
    /**
     * Gets the current database type
     * @return Current database type
     */
    public DatabaseAdapterFactory.DatabaseType getCurrentDatabaseType() {
        if (currentAdapter instanceof MySQLDatabaseAdapter) {
            return DatabaseAdapterFactory.DatabaseType.MYSQL;
        } else if (currentAdapter instanceof PostgreSQLDatabaseAdapter) {
            return DatabaseAdapterFactory.DatabaseType.POSTGRESQL;
        } else {
            return DatabaseAdapterFactory.DatabaseType.MYSQL; // Default
        }
    }
} 