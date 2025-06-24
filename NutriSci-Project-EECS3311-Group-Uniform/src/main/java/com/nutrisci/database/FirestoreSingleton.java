package com.nutrisci.database;

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

// helped by AI

// Singleton for Firebase Firestore operations
public class FirestoreSingleton {
    private static final Logger logger = LoggerFactory.getLogger(FirestoreSingleton.class);
    private static volatile FirestoreSingleton instance;
    private Firestore firestore;

    // Private constructor initializes Firestore
    private FirestoreSingleton() {
        initializeFirestore();
    }

    // Returns the singleton instance (thread-safe)
    // helped by AI
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

    // Initializes Firebase and Firestore connection
    // helped by AI
    private void initializeFirestore() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = new FileInputStream("./serviceAccount.json");
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://nutrisci-project-9ccbf.com")
                    .build();
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
            }
            this.firestore = FirestoreClient.getFirestore();
            logger.info("Firestore connection established");
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: " + e.getMessage(), e);
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }

    // Adds a document to a collection and returns its ID
    // helped by AI
    public String addDocument(String collectionName, Map<String, Object> data) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document();
            ApiFuture<WriteResult> result = docRef.set(data);
            result.get();
            return docRef.getId();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to add document to collection '{}': {}", collectionName, e.getMessage(), e);
            throw new RuntimeException("Failed to add document", e);
        }
    }

    // Adds a document with a specific ID
    // helped by AI
    public void addDocumentWithId(String collectionName, String documentId, Map<String, Object> data) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document(documentId);
            ApiFuture<WriteResult> result = docRef.set(data);
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to add document to collection '{}' with ID '{}': {}", 
                        collectionName, documentId, e.getMessage(), e);
            throw new RuntimeException("Failed to add document", e);
        }
    }

    // Gets a document by ID
    // helped by AI
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

    // Updates a document
    // helped by AI
    public void updateDocument(String collectionName, String documentId, Map<String, Object> data) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document(documentId);
            ApiFuture<WriteResult> result = docRef.update(data);
            result.get();
            logger.info("Document updated in collection '{}' with ID: {}", collectionName, documentId);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to update document in collection '{}' with ID '{}': {}", 
                        collectionName, documentId, e.getMessage(), e);
            throw new RuntimeException("Failed to update document", e);
        }
    }

    // Deletes a document
    public void deleteDocument(String collectionName, String documentId) {
        try {
            DocumentReference docRef = firestore.collection(collectionName).document(documentId);
            ApiFuture<WriteResult> result = docRef.delete();
            result.get();
            logger.info("Document deleted from collection '{}' with ID: {}", collectionName, documentId);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to delete document from collection '{}' with ID '{}': {}", 
                        collectionName, documentId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete document", e);
        }
    }

    // Gets all documents from a collection
    // helped by AI
    public List<Map<String, Object>> getAllDocuments(String collectionName) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).get();
            QuerySnapshot documents = future.get();
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                data.put("id", document.getId());
                result.add(data);
            }
            logger.info("Retrieved {} documents from collection '{}'", result.size(), collectionName);
            return result;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to get documents from collection '{}': {}", collectionName, e.getMessage(), e);
            throw new RuntimeException("Failed to get documents", e);
        }
    }

    // Queries documents with a specific field value
    // helped by AI
    public List<Map<String, Object>> queryDocuments(String collectionName, String fieldName, Object fieldValue) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(collectionName)
                .whereEqualTo(fieldName, fieldValue)
                .get();
            QuerySnapshot documents = future.get();
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                data.put("id", document.getId());
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

    // Returns the Firestore instance
    public Firestore getFirestore() {
        return this.firestore;
    }

    // Closes the Firestore connection
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