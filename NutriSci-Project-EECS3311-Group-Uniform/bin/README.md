# NutriSci Project EECS3311 Group Uniform

A Java project with Firebase Firestore integration using a Singleton pattern for database operations.

## Features

- **Thread-safe Singleton pattern** for Firebase Firestore operations
- **Comprehensive CRUD operations** (Create, Read, Update, Delete)
- **Query capabilities** for filtering documents
- **CSV data import** for nutrition database
- **Nutrition data access utilities**
- **Proper error handling** and logging
- **Maven project structure** with all necessary dependencies

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Firebase project with Firestore database enabled
- Firebase service account key

## Setup Instructions

### 1. Firebase Project Setup

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select an existing one
3. Enable Firestore Database:
   - Go to Firestore Database in the left sidebar
   - Click "Create database"
   - Choose "Start in test mode" (for development)
   - Select a location for your database

### 2. Generate Service Account Key

1. In Firebase Console, go to Project Settings (gear icon)
2. Go to the "Service accounts" tab
3. Click "Generate new private key"
4. Download the JSON file
5. Rename it to `serviceAccountKey.json`
6. Place it in the project root directory

### 3. Update Configuration

1. Open `src/main/java/com/nutrisci/firebase/FirestoreSingleton.java`
2. Update the following lines with your Firebase project details:
   ```java
   // Line 67: Update the path to your service account key
   InputStream serviceAccount = new FileInputStream("path/to/your/serviceAccountKey.json");
   
   // Line 70: Update with your project ID
   .setProjectId("your-project-id")
   ```

### 4. Build and Run

```bash
# Navigate to project directory
cd NutriSci-Project-EECS3311-Group-Uniform

# Install dependencies
mvn clean install

# Run the example
mvn exec:java -Dexec.mainClass="com.nutrisci.firebase.FirestoreExample"
```

## Usage Examples

### Basic Usage

```java
// Get the singleton instance
FirestoreSingleton firestore = FirestoreSingleton.getInstance();

// Add a document
Map<String, Object> data = new HashMap<>();
data.put("name", "John Doe");
data.put("email", "john@example.com");
String documentId = firestore.addDocument("users", data);

// Retrieve a document
Map<String, Object> user = firestore.getDocument("users", documentId);

// Update a document
Map<String, Object> updateData = new HashMap<>();
updateData.put("age", 30);
firestore.updateDocument("users", documentId, updateData);

// Delete a document
firestore.deleteDocument("users", documentId);
```

### Querying Documents

```java
// Query documents by field value
List<Map<String, Object>> usersInToronto = 
    firestore.queryDocuments("users", "city", "Toronto");

// Get all documents from a collection
List<Map<String, Object>> allUsers = firestore.getAllDocuments("users");
```

### Advanced Operations

```java
// Add document with specific ID
firestore.addDocumentWithId("products", "laptop-001", productData);

// Get Firestore instance for advanced operations
Firestore db = firestore.getFirestore();
```

## Project Structure

```
NutriSci-Project-EECS3311-Group-Uniform/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── nutrisci/
│   │   │           └── firebase/
│   │   │               ├── FirestoreSingleton.java    # Main Singleton class
│   │   │               └── FirestoreExample.java      # Usage examples
│   │   └── resources/
│   │       ├── serviceAccountKey.json.template        # Template for Firebase config
│   │       └── logback.xml                            # Logging configuration
│   └── test/
│       └── java/                                      # Test files (to be added)
├── pom.xml                                            # Maven dependencies
├── serviceAccountKey.json                             # Your Firebase credentials (not in repo)
└── README.md                                          # This file
```

## API Reference

### FirestoreSingleton Methods

| Method | Description | Parameters | Returns |
|--------|-------------|------------|---------|
| `getInstance()` | Get singleton instance | None | `FirestoreSingleton` |
| `addDocument()` | Add document with auto-generated ID | `collectionName`, `data` | `String` (document ID) |
| `addDocumentWithId()` | Add document with specific ID | `collectionName`, `documentId`, `data` | `void` |
| `getDocument()` | Retrieve document by ID | `collectionName`, `documentId` | `Map<String, Object>` |
| `updateDocument()` | Update existing document | `collectionName`, `documentId`, `data` | `void` |
| `deleteDocument()` | Delete document | `collectionName`, `documentId` | `void` |
| `getAllDocuments()` | Get all documents from collection | `collectionName` | `List<Map<String, Object>>` |
| `queryDocuments()` | Query documents by field | `collectionName`, `fieldName`, `fieldValue` | `List<Map<String, Object>>` |
| `getFirestore()` | Get raw Firestore instance | None | `Firestore` |
| `close()` | Close Firestore connection | None | `void` |

## Error Handling

The Singleton includes comprehensive error handling:
- All database operations are wrapped in try-catch blocks
- Detailed logging for debugging
- Runtime exceptions for critical errors
- Graceful handling of missing documents

## Security Considerations

1. **Never commit your `serviceAccountKey.json`** to version control
2. Add `serviceAccountKey.json` to your `.gitignore` file
3. Use environment variables for production deployments
4. Set up proper Firestore security rules

## Troubleshooting

### Common Issues

1. **"File not found" error**: Ensure `serviceAccountKey.json` is in the correct path
2. **"Project ID not found"**: Verify your Firebase project ID in the configuration
3. **"Permission denied"**: Check Firestore security rules and service account permissions
4. **"Connection timeout"**: Verify internet connection and Firebase project status

### Logging

The application uses SLF4J with Logback for logging. Logs are written to:
- Console output
- `logs/firestore.log` file

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Importing CSV Data

The project includes functionality to import nutrition data from CSV files into Firestore.

### CSV Import Process

1. Place your CSV files in a directory (default is `./CSV/`)
2. Run the CSV import tool:
   ```bash
   mvn exec:java -Dexec.mainClass="com.nutrisci.csv.CSVImportRunner" -Dexec.args="./path/to/csv/directory"
   ```
3. The tool will:
   - Import all CSV files in the specified directory
   - Create collections in Firestore for each CSV file
   - Generate a mapping file (`id_mappings.csv`) to track original IDs to Firestore document IDs

### Using Nutrition Data

After importing the data, you can use the `NutritionDataAccessor` class to access the data:

```java
// Create an accessor and load ID mappings
NutritionDataAccessor accessor = new NutritionDataAccessor();
accessor.loadIdMappings("./CSV/id_mappings.csv");

// Get food by ID
Map<String, Object> food = accessor.getFoodById("1234");

// Search foods by name
List<Map<String, Object>> foods = accessor.searchFoodsByName("apple");

// Get nutrients for a food
List<Map<String, Object>> nutrients = accessor.getNutrientAmountsForFood("1234");
```

You can also run the demo application to explore the data:
```bash
mvn exec:java -Dexec.mainClass="com.nutrisci.csv.NutritionDataDemo" -Dexec.args="./CSV"
```

## License

This project is part of EECS3311 coursework at York University.

## Support

For issues related to:
- Firebase setup: Check [Firebase Documentation](https://firebase.google.com/docs)
- Java/Maven: Check [Maven Documentation](https://maven.apache.org/guides/)
- Project-specific issues: Contact your team members