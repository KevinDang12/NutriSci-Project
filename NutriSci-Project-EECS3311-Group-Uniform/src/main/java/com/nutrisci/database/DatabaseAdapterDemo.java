package com.nutrisci.database;

/**
 * Demonstration class for the Database Adapter pattern.
 * This class shows how to use the adapter pattern to switch
 * between different database types without changing the main application code.
 * 
 * @author NutriSci Team
 * @version 1.0
 */
public class DatabaseAdapterDemo {
    
    /**
     * Demonstrates the database adapter pattern functionality
     */
    public static void demonstrateAdapterPattern() {
        System.out.println("=== Database Adapter Pattern Demonstration ===");
        
        // Get the database adapter manager
        DatabaseAdapterManager manager = DatabaseAdapterManager.getInstance();
        
        // Show current database type
        System.out.println("Current database type: " + manager.getCurrentDatabaseType());
        
        // Demonstrate switching to PostgreSQL
        System.out.println("\nSwitching to PostgreSQL...");
        manager.switchToPostgreSQL();
        System.out.println("Current database type: " + manager.getCurrentDatabaseType());
        
        // Demonstrate switching back to MySQL
        System.out.println("\nSwitching back to MySQL...");
        manager.switchToMySQL();
        System.out.println("Current database type: " + manager.getCurrentDatabaseType());
        
        // Demonstrate using the factory directly
        System.out.println("\nUsing factory to create adapters:");
        DatabaseAdapter mysqlAdapter = DatabaseAdapterFactory.createMySQLAdapter();
        DatabaseAdapter postgresAdapter = DatabaseAdapterFactory.createPostgreSQLAdapter();
        
        System.out.println("MySQL adapter created: " + (mysqlAdapter instanceof MySQLDatabaseAdapter));
        System.out.println("PostgreSQL adapter created: " + (postgresAdapter instanceof PostgreSQLDatabaseAdapter));
        
        System.out.println("\n=== Demonstration Complete ===");
    }
    
    /**
     * Shows how to use the adapter in practice
     */
    public static void showPracticalUsage() {
        System.out.println("\n=== Practical Usage Example ===");
        
        // Get the current adapter (works with any database type)
        DatabaseAdapter adapter = DatabaseAdapterManager.getInstance().getCurrentAdapter();
        
        // Use the adapter - the code doesn't need to know which database it's using
        System.out.println("Using database adapter: " + adapter.getClass().getSimpleName());
        
        // All database operations work the same way regardless of the underlying database
        System.out.println("Database operations are abstracted through the adapter interface");
        
        System.out.println("=== Practical Usage Complete ===");
    }
    
    /**
     * Main method to run the demonstration
     */
    public static void main(String[] args) {
        demonstrateAdapterPattern();
        showPracticalUsage();
    }
} 