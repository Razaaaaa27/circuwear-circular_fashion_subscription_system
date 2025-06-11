package com.smallacademy.userroles;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth; // Tambahkan impor ini
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseManager {
    private static FirebaseFirestore db;
    private static FirebaseAuth auth; // Tambahkan variabel untuk FirebaseAuth

    public static void initialize() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();

            // Configure Firestore settings
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
        }
        if (auth == null) {
            auth = FirebaseAuth.getInstance(); // Inisialisasi FirebaseAuth
        }
    }

    public static FirebaseFirestore getFirestore() {
        if (db == null) {
            initialize();
        }
        return db;
    }

    public static void signOut() {
        if (auth != null) {
            auth.signOut(); // Panggil metode signOut dari FirebaseAuth
        }
    }
}