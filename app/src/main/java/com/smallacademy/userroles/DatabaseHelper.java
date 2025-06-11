package com.smallacademy.userroles;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseHelper {
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_CLOTHING_ITEMS = "clothing_items";
    private static final String COLLECTION_REWARDS = "rewards";
    private static final String COLLECTION_ANALYTICS = "analytics";

    private static FirebaseFirestore db = FirebaseManager.getFirestore();

    public static CollectionReference getUsersCollection() {
        return db.collection(COLLECTION_USERS);
    }

    public static CollectionReference getOrdersCollection() {
        return db.collection(COLLECTION_ORDERS);
    }

    public static CollectionReference getClothingItemsCollection() {
        return db.collection(COLLECTION_CLOTHING_ITEMS);
    }

    public static CollectionReference getRewardsCollection() {
        return db.collection(COLLECTION_REWARDS);
    }

    public static CollectionReference getAnalyticsCollection() {
        return db.collection(COLLECTION_ANALYTICS);
    }
}
