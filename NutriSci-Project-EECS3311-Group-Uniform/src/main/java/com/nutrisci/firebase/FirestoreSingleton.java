package com.nutrisci.firebase;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Singleton class for Firebase Firestore operations.
 * Provides thread-safe access to Firestore database with comprehensive CRUD operations.
 */
public class FirestoreSingleton {
    private static final Logger logger = LoggerFactory.getLogger(FirestoreSingleton.class);
    
    // Singleton instance
    private static volatile FirestoreSingleton instance;
    
    // Firestore instance
    private Firestore firestore;
    
    // Private constructor to prevent instantiation
    private FirestoreSingleton() {
        initializeFirestore();
    }
    
    /**
     * Get the singleton instance (thread-safe)
     * @return FirestoreSingleton instance
     */
    public static FirestoreSingleton getInstance() {
        if (instance == null) {
            synchronized (FirestoreSingleton.class) {
                if (instance == null) {
                    instance = new FirestoreSingleton();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize Firebase and Firestore connection
     */
    private void initializeFirestore() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {

                // Load service account key file
                InputStream serviceAccount = new FileInputStream("./serviceAccount.json");
                
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://nutrisci-project-9ccbf.com")
                    .build();
                
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
            }
            
            // Get Firestore instance
            this.firestore = FirestoreClient.getFirestore();
            logger.info("Firestore connection established");
            
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: " + e.getMessage(), e);
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }
    
    /**
     * Add a document to a collection
     * @param collectionName Name of the collection
     * @param data Data to store as Map
     * @return Document ID of the created document
     */
    public String addDocument(String collectionName, Map<String, Object> data) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document();
            ApiFuture<WriteResult> result = docRef.set(data);
            result.get(); // Wait for the write to complete
            // logger.info("Document added to collection '{}' with ID: {}", collectionName, docRef.getId());
            return docRef.getId();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to add document to collection '{}': {}", collectionName, e.getMessage(), e);
            throw new RuntimeException("Failed to add document", e);
        }
    }
    
    /**
     * Add a document to a collection with a specific ID
     * @param collectionName Name of the collection
     * @param documentId Specific document ID
     * @param data Data to store as Map
     */
    public void addDocumentWithId(String collectionName, String documentId, Map<String, Object> data) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document(documentId);
            ApiFuture<WriteResult> result = docRef.set(data);
            result.get(); // Wait for the write to complete
            // logger.info("Document added to collection '{}' with ID: {}", collectionName, documentId);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to add document to collection '{}' with ID '{}': {}", 
                        collectionName, documentId, e.getMessage(), e);
            throw new RuntimeException("Failed to add document", e);
        }
    }
    
    /**
     * Get a document by ID
     * @param collectionName Name of the collection
     * @param documentId Document ID
     * @return Document data as Map, or null if not found
     */
    public Map<String, Object> getDocument(String collectionName, String documentId) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document(documentId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            
            if (document.exists()) {
                logger.info("Document retrieved from collection '{}' with ID: {}", collectionName, documentId);
                return document.getData();
            } else {
                logger.warn("Document not found in collection '{}' with ID: {}", collectionName, documentId);
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to get document from collection '{}' with ID '{}': {}", 
                        collectionName, documentId, e.getMessage(), e);
            throw new RuntimeException("Failed to get document", e);
        }
    }
    
    /**
     * Update a document
     * @param collectionName Name of the collection
     * @param documentId Document ID
     * @param data Data to update as Map
     */
    public void updateDocument(String collectionName, String documentId, Map<String, Object> data) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document(documentId);
            ApiFuture<WriteResult> result = docRef.update(data);
            result.get(); // Wait for the update to complete
            logger.info("Document updated in collection '{}' with ID: {}", collectionName, documentId);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to update document in collection '{}' with ID '{}': {}", 
                        collectionName, documentId, e.getMessage(), e);
            throw new RuntimeException("Failed to update document", e);
        }
    }
    
    /**
     * Delete a document
     * @param collectionName Name of the collection
     * @param documentId Document ID
     */
    public void deleteDocument(String collectionName, String documentId) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document(documentId);
            ApiFuture<WriteResult> result = docRef.delete();
            result.get(); // Wait for the delete to complete
            logger.info("Document deleted from collection '{}' with ID: {}", collectionName, documentId);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to delete document from collection '{}' with ID '{}': {}", 
                        collectionName, documentId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete document", e);
        }
    }
    
    /**
     * Get all documents from a collection
     * @param collectionName Name of the collection
     * @return List of document data as Maps
     */
    public List<Map<String, Object>> getAllDocuments(String collectionName) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).get();
            QuerySnapshot documents = future.get();
            
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                data.put("id", document.getId()); // Include document ID
                result.add(data);
            }
            
            logger.info("Retrieved {} documents from collection '{}'", result.size(), collectionName);
            return result;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to get documents from collection '{}': {}", collectionName, e.getMessage(), e);
            throw new RuntimeException("Failed to get documents", e);
        }
    }
    
    /**
     * Query documents with a specific field value
     * @param collectionName Name of the collection
     * @param fieldName Field name to query
     * @param fieldValue Field value to match
     * @return List of matching documents
     */
    public List<Map<String, Object>> queryDocuments(String collectionName, String fieldName, Object fieldValue) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(collectionName)
                .whereEqualTo(fieldName, fieldValue)
                .get();
            QuerySnapshot documents = future.get();
            
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                data.put("id", document.getId()); // Include document ID
                result.add(data);
            }
            
            logger.info("Retrieved {} documents from collection '{}' where {} = {}", 
                       result.size(), collectionName, fieldName, fieldValue);
            return result;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to query documents from collection '{}': {}", collectionName, e.getMessage(), e);
            throw new RuntimeException("Failed to query documents", e);
        }
    }
    
    /**
     * Get Firestore instance (for advanced operations)
     * @return Firestore instance
     */
    public Firestore getFirestore() {
        return this.firestore;
    }
    
    /**
     * Close Firestore connection
     */
    public void close() {
        try {
            if (firestore != null) {
                firestore.close();
                logger.info("Firestore connection closed");
            }
        } catch (Exception e) {
            logger.error("Error closing Firestore connection: {}", e.getMessage(), e);
        }
    }
} 