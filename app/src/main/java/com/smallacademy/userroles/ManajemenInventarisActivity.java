package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManajemenInventarisActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClothingItemAdapter itemAdapter;
    private List<ClothingItem> itemList;
    private FloatingActionButton fabAddItem;
    private Button btnAllCategories, btnAllStatus, btnAddItem;

    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manajemen_inventaris);

        initViews();
        setupFirebase();
        setupRecyclerView();
        setupButtons();
        loadItems();

        // Add default items if none exist
        addDefaultItems();

        // ===== JALANKAN SEKALI SAJA UNTUK UPDATE DATABASE =====
        // Uncomment line di bawah ini, jalankan app sekali, lalu comment lagi
        // updateExistingItemsWithImageResId();
        // ===== JALANKAN SEKALI SAJA =====
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manajemen Inventaris");
        }

        recyclerView = findViewById(R.id.recyclerViewItems);
        fabAddItem = findViewById(R.id.fabAddItem);
        btnAllCategories = findViewById(R.id.btnAllCategories);
        btnAllStatus = findViewById(R.id.btnAllStatus);
        btnAddItem = findViewById(R.id.btnAddItem);
    }

    private void setupFirebase() {
        try {
            db = FirebaseFirestore.getInstance();
            itemsRef = db.collection("clothing_items");
        } catch (Exception e) {
            Log.e("ManajemenInventaris", "Error initializing Firestore", e);
            Toast.makeText(this, "Gagal menginisialisasi Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        itemList = new ArrayList<>();
        itemAdapter = new ClothingItemAdapter(itemList, this::showItemDetails);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(itemAdapter);
    }

    private void setupButtons() {
        fabAddItem.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, AddClothingItemActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("ManajemenInventaris", "Error navigating to AddClothingItemActivity", e);
                Toast.makeText(this, "Gagal membuka Tambah Item: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btnAllCategories.setOnClickListener(v -> {
            currentFilter = "all";
            loadItems();
        });

        btnAllStatus.setOnClickListener(v -> {
            currentFilter = "available";
            loadAvailableItems();
        });

        btnAddItem.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, AddClothingItemActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("ManajemenInventaris", "Error navigating to AddClothingItemActivity", e);
                Toast.makeText(this, "Gagal membuka Tambah Item: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addDefaultItems() {
        itemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    // Formal items dengan harga berbeda
                    addDefaultItem("Gaun Formal Hitam", "Gaun Formal • Ukuran S", "Rp 150000/hari", "GF-005-S", "Tersedia", "formal", null, R.drawable.baju1);
                    addDefaultItem("Jas Pria Biru Navy", "Jas Pria • Ukuran M", "Rp 200000/hari", "JP-003-M", "Disewa", "formal", null, R.drawable.baju02);
                    addDefaultItem("Gaun Formal Merah", "Gaun Formal • Ukuran M", "Rp 175000/hari", "GF-002-M", "Perawatan", "formal", null, R.drawable.baju03);
                    addDefaultItem("Jas Pria Hitam", "Jas Pria • Ukuran L", "Rp 180000/hari", "JP-001-L", "Tersedia", "formal", null, R.drawable.baju1);

                    // Casual items dengan harga yang berbeda
                    addDefaultItem("Kaos Polos Over Size", "Kaos Casual • Ukuran L", "Rp 75000/hari", "KC-005-L", "Tersedia", "casual", null, R.drawable.baju001);
                    addDefaultItem("Kaos Hitam", "Kaos • Ukuran L", "Rp 65000/hari", "KC-001-L", "Disewa", "casual", null, R.drawable.baju08);
                    addDefaultItem("Kaos Merah", "Kaos • Ukuran S", "Rp 70000/hari", "KC-007-S", "Tersedia", "casual", null, R.drawable.baju04);
                    addDefaultItem("Kaos Kuning Uniqlo", "Kaos • Ukuran M", "Rp 80000/hari", "KC-002-M", "Perawatan", "casual", null, R.drawable.baju001);
                    addDefaultItem("Kaos Merah Besar", "Kaos • Ukuran L", "Rp 85000/hari", "KC-003-L", "Tersedia", "casual", null, R.drawable.baju08);
                }
            } else {
                Log.e("ManajemenInventaris", "Error checking collection", task.getException());
                Toast.makeText(this, "Gagal memeriksa koleksi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addDefaultItem(String name, String category, String price, String code, String status, String type, String imageUrl, int imageResId) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("category", category);
        item.put("price", price);
        item.put("code", code);
        item.put("status", status);
        item.put("type", type);
        item.put("imageUrl", imageUrl != null ? imageUrl : "");
        item.put("imageResId", imageResId);
        item.put("isActive", true);
        item.put("createdAt", System.currentTimeMillis());

        itemsRef.add(item).addOnFailureListener(e -> {
            Log.e("ManajemenInventaris", "Error adding default item: " + name, e);
            Toast.makeText(this, "Gagal menambahkan item default: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadItems() {
        itemsRef.whereEqualTo("isActive", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                ClothingItem item = document.toObject(ClothingItem.class);
                                item.setId(document.getId());

                                // Handle imageResId dari Firestore
                                if (document.contains("imageResId")) {
                                    Long imageResIdLong = document.getLong("imageResId");
                                    if (imageResIdLong != null) {
                                        item.setImageResId(imageResIdLong.intValue());
                                        Log.d("ManajemenInventaris", "✅ Item: " + item.getName() + ", ImageResId: " + item.getImageResId());
                                    } else {
                                        Log.w("ManajemenInventaris", "⚠️ ImageResId Long is null for: " + item.getName());
                                        item.setImageResId(R.drawable.baju1);
                                    }
                                } else {
                                    Log.w("ManajemenInventaris", "⚠️ No imageResId field for: " + item.getName());
                                    item.setImageResId(R.drawable.baju1);
                                }

                                itemList.add(item);
                            } catch (Exception e) {
                                Log.e("ManajemenInventaris", "Error parsing item: " + document.getId(), e);
                            }
                        }
                        itemAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("ManajemenInventaris", "Error loading items", task.getException());
                        Toast.makeText(this, "Gagal memuat item: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadAvailableItems() {
        itemsRef.whereEqualTo("isActive", true)
                .whereEqualTo("status", "Tersedia")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                ClothingItem item = document.toObject(ClothingItem.class);
                                item.setId(document.getId());

                                // Handle imageResId dari Firestore
                                if (document.contains("imageResId")) {
                                    Long imageResIdLong = document.getLong("imageResId");
                                    if (imageResIdLong != null) {
                                        item.setImageResId(imageResIdLong.intValue());
                                    } else {
                                        item.setImageResId(R.drawable.baju1);
                                    }
                                } else {
                                    item.setImageResId(R.drawable.baju1);
                                }

                                itemList.add(item);
                            } catch (Exception e) {
                                Log.e("ManajemenInventaris", "Error parsing item: " + document.getId(), e);
                            }
                        }
                        itemAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("ManajemenInventaris", "Error loading available items", task.getException());
                        Toast.makeText(this, "Gagal memuat item tersedia: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ===== METHOD UNTUK UPDATE DATABASE YANG SUDAH ADA =====
    private void updateExistingItemsWithImageResId() {
        Log.d("UpdateImageResId", "Starting database update...");

        // Map untuk menentukan imageResId berdasarkan name
        Map<String, Integer> imageMapping = new HashMap<>();

        // Formal items
        imageMapping.put("Gaun Formal Hitam", R.drawable.baju1);
        imageMapping.put("Jas Pria Biru Navy", R.drawable.baju02);
        imageMapping.put("Gaun Formal Merah", R.drawable.baju03);
        imageMapping.put("Jas Pria Hitam", R.drawable.baju1);

        // Casual items
        imageMapping.put("Kaos Polos Over Size", R.drawable.baju001);
        imageMapping.put("Kaos Hitam", R.drawable.baju08);
        imageMapping.put("Kaos Merah", R.drawable.baju04);
        imageMapping.put("Kaos Kuning Uniqlo", R.drawable.baju001);
        imageMapping.put("Kaos Merah Besar", R.drawable.baju08);

        // Get semua items dari Firestore
        itemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("UpdateImageResId", "Found " + task.getResult().size() + " documents to update");

                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        String itemName = document.getString("name");
                        String category = document.getString("category");
                        String type = document.getString("type");

                        Log.d("UpdateImageResId", "Processing: " + itemName);

                        // Tentukan imageResId
                        Integer imageResId = null;

                        // Coba cari berdasarkan nama exact
                        if (imageMapping.containsKey(itemName)) {
                            imageResId = imageMapping.get(itemName);
                            Log.d("UpdateImageResId", "Found exact match for: " + itemName + " -> " + imageResId);
                        } else {
                            // Fallback berdasarkan category dan type
                            imageResId = getImageResIdForCategoryType(category, type);
                            Log.d("UpdateImageResId", "Using category/type fallback for: " + itemName + " -> " + imageResId);
                        }

                        // Update document dengan imageResId
                        if (imageResId != null) {
                            final Integer finalImageResId = imageResId; // Make it final for lambda
                            final String finalItemName = itemName; // Make it final for lambda

                            document.getReference().update("imageResId", finalImageResId)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("UpdateImageResId", "✅ Updated " + finalItemName + " with imageResId: " + finalImageResId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("UpdateImageResId", "❌ Failed to update " + finalItemName, e);
                                    });
                        }

                    } catch (Exception e) {
                        Log.e("UpdateImageResId", "Error processing document: " + document.getId(), e);
                    }
                }

                // Refresh data setelah update
                new Handler().postDelayed(() -> {
                    loadItems();
                    Toast.makeText(this, "Database berhasil diupdate dengan imageResId", Toast.LENGTH_LONG).show();
                }, 3000);

            } else {
                Log.e("UpdateImageResId", "Error getting documents", task.getException());
                Toast.makeText(this, "Gagal mengambil data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private int getImageResIdForCategoryType(String category, String type) {
        if (type != null && type.equalsIgnoreCase("formal")) {
            if (category != null && category.contains("Gaun")) {
                return R.drawable.baju1;
            } else if (category != null && category.contains("Jas")) {
                return R.drawable.baju02;
            } else {
                return R.drawable.baju03; // Kaos Formal
            }
        } else { // casual
            if (category != null && category.contains("Kaos")) {
                return R.drawable.baju001;
            } else if (category != null && category.contains("Celana")) {
                return R.drawable.baju08;
            } else {
                return R.drawable.baju04;
            }
        }
    }

    private void showItemDetails(ClothingItem item) {
        try {
            Intent intent = new Intent(this, ClothingItemDetailActivity.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        } catch (Exception e) {
            Log.e("ManajemenInventaris", "Error navigating to ClothingItemDetailActivity", e);
            Toast.makeText(this, "Gagal membuka Detail Item: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}