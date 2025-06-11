package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class KeranjangActivity extends AppCompatActivity {

    private GridView gridViewItems;
    private Button btnDeleteAll, btnSelectAll, btnRent;
    private TextView tvSelectedItems;
    private ImageView navHome, navSaved, navCart, navHistory, navProfile;
    private ProgressBar progressBar;

    private KeranjangAdapter adapter;
    private List<ClothingItem> clothingItems;

    // Firebase
    private FirebaseFirestore db;
    private CollectionReference itemsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initUI();
        setupFirebase();
        setupData();
        setupListeners();
        setupBottomNavigation();
        updateSelectedItemsCount();
    }

    private void initUI() {
        gridViewItems = findViewById(R.id.gridViewItems);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnSelectAll = findViewById(R.id.btnSelectAll);
        btnRent = findViewById(R.id.btnRent);
        tvSelectedItems = findViewById(R.id.tvSelectedItems);

        navHome = findViewById(R.id.navHome);
        navSaved = findViewById(R.id.navSaved);
        navCart = findViewById(R.id.navCart);
        navHistory = findViewById(R.id.navHistory);
        navProfile = findViewById(R.id.navProfile);

        // ProgressBar bisa ditambahkan jika ada di layout
        // progressBar = findViewById(R.id.progressBar);
    }

    private void setupFirebase() {
        try {
            db = FirebaseFirestore.getInstance();
            itemsRef = db.collection("clothing_items");
            Log.d("KeranjangActivity", "✅ Firestore initialized successfully");
        } catch (Exception e) {
            Log.e("KeranjangActivity", "❌ Error initializing Firestore", e);
            Toast.makeText(this, "Gagal menginisialisasi Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupData() {
        clothingItems = new ArrayList<>();
        loadItemsFromFirebase();
    }

    private void loadItemsFromFirebase() {
        Log.d("KeranjangActivity", "Loading items from Firebase...");

        // Show loading if progressBar exists
        // if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Load only available items for cart
        itemsRef.whereEqualTo("isActive", true)
                .whereEqualTo("status", "Tersedia") // Only show available items in cart
                .get()
                .addOnCompleteListener(task -> {
                    // Hide loading
                    // if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        clothingItems.clear();
                        Log.d("KeranjangActivity", "Found " + task.getResult().size() + " documents from Firestore");

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                ClothingItem item = document.toObject(ClothingItem.class);
                                item.setId(document.getId());

                                // ===== BAGIAN PENTING: Handle imageResId dari Firestore =====
                                if (document.contains("imageResId")) {
                                    Long imageResIdLong = document.getLong("imageResId");
                                    if (imageResIdLong != null) {
                                        item.setImageResId(imageResIdLong.intValue());
                                        Log.d("KeranjangActivity", "✅ Item: " + item.getName() + ", ImageResId: " + item.getImageResId());
                                    } else {
                                        Log.w("KeranjangActivity", "⚠️ ImageResId Long is null for: " + item.getName());
                                        item.setImageResId(R.drawable.baju1); // default
                                    }
                                } else {
                                    Log.w("KeranjangActivity", "⚠️ No imageResId field for: " + item.getName());
                                    // Set default image if imageResId is 0 - TANPA PLACEHOLDER
                                    setImageResIdBasedOnType(item);
                                }
                                // ===== AKHIR BAGIAN PENTING =====

                                clothingItems.add(item);
                            } catch (Exception e) {
                                Log.e("KeranjangActivity", "Error parsing item: " + document.getId(), e);
                            }
                        }

                        // Update adapter
                        adapter = new KeranjangAdapter(KeranjangActivity.this, clothingItems);
                        gridViewItems.setAdapter(adapter);
                        updateSelectedItemsCount();

                        Log.d("KeranjangActivity", "✅ Successfully loaded " + clothingItems.size() + " items");

                    } else {
                        Log.e("KeranjangActivity", "❌ Error loading items", task.getException());
                        Toast.makeText(KeranjangActivity.this, "Gagal memuat item: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        // Fallback to static data if Firebase fails
                        loadStaticDataAsFallback();
                    }
                });
    }

    private void setImageResIdBasedOnType(ClothingItem item) {
        // Set default images based on type and name - TANPA PLACEHOLDER!
        if (item.getType() != null && item.getType().equalsIgnoreCase("formal")) {
            if (item.getName().toLowerCase().contains("gaun") && item.getName().toLowerCase().contains("hitam")) {
                item.setImageResId(R.drawable.baju1);
            } else if (item.getName().toLowerCase().contains("jas") && item.getName().toLowerCase().contains("biru")) {
                item.setImageResId(R.drawable.baju02);
            } else if (item.getName().toLowerCase().contains("gaun") && item.getName().toLowerCase().contains("merah")) {
                item.setImageResId(R.drawable.baju03);
            } else if (item.getName().toLowerCase().contains("jas")) {
                item.setImageResId(R.drawable.baju02); // Default jas
            } else {
                item.setImageResId(R.drawable.baju1); // Default formal - BUKAN formal_placeholder
            }
        } else {
            // For casual items
            if (item.getName().toLowerCase().contains("kaos") && item.getName().toLowerCase().contains("over")) {
                item.setImageResId(R.drawable.baju001);
            } else if (item.getName().toLowerCase().contains("kaos") && item.getName().toLowerCase().contains("hitam")) {
                item.setImageResId(R.drawable.baju08);
            } else if (item.getName().toLowerCase().contains("kaos") && item.getName().toLowerCase().contains("merah")) {
                item.setImageResId(R.drawable.baju04);
            } else if (item.getName().toLowerCase().contains("kaos") && item.getName().toLowerCase().contains("kuning")) {
                item.setImageResId(R.drawable.baju001);
            } else {
                item.setImageResId(R.drawable.baju001); // Default casual - BUKAN casual_placeholder
            }
        }

        Log.d("KeranjangActivity", "Set imageResId for " + item.getName() + ": " + item.getImageResId());
    }

    private void loadStaticDataAsFallback() {
        Log.d("KeranjangActivity", "Loading static fallback data...");

        // Static data as fallback - MENGGUNAKAN CONSTRUCTOR YANG BENAR
        clothingItems.clear();

        // Menggunakan constructor dengan 11 parameter: (id, name, category, price, code, status, type, imageUrl, imageResId, isActive, createdAt)
        clothingItems.add(new ClothingItem(
                "fallback1",
                "Gaun Formal Hijau",
                "Gaun Formal • Ukuran S",
                "Rp 150.000/hari",
                "GF-005-S",
                "Tersedia",
                "formal",
                "",
                R.drawable.baju1,
                true,
                System.currentTimeMillis()
        ));

        clothingItems.add(new ClothingItem(
                "fallback2",
                "Jas Pria Biru Navy",
                "Jas Pria • Ukuran M",
                "Rp 175.000/hari",
                "JP-003-M",
                "Tersedia",
                "formal",
                "",
                R.drawable.baju02,
                true,
                System.currentTimeMillis()
        ));

        clothingItems.add(new ClothingItem(
                "fallback3",
                "Gaun Formal Merah",
                "Gaun Formal • Ukuran M",
                "Rp 160.000/hari",
                "GF-002-M",
                "Tersedia",
                "formal",
                "",
                R.drawable.baju03,
                true,
                System.currentTimeMillis()
        ));

        clothingItems.add(new ClothingItem(
                "fallback4",
                "Kaos Polos Over Size",
                "Kaos Casual • Ukuran L",
                "Rp 85.000/hari",
                "KC-005-L",
                "Tersedia",
                "casual",
                "",
                R.drawable.baju001,
                true,
                System.currentTimeMillis()
        ));

        clothingItems.add(new ClothingItem(
                "fallback5",
                "Kaos Hitam",
                "Kaos • Ukuran L",
                "Rp 65.000/hari",
                "KC-001-L",
                "Tersedia",
                "casual",
                "",
                R.drawable.baju08,
                true,
                System.currentTimeMillis()
        ));

        clothingItems.add(new ClothingItem(
                "fallback6",
                "Kaos Merah",
                "Kaos • Ukuran S",
                "Rp 70.000/hari",
                "KC-007-S",
                "Tersedia",
                "casual",
                "",
                R.drawable.baju04,
                true,
                System.currentTimeMillis()
        ));

        adapter = new KeranjangAdapter(KeranjangActivity.this, clothingItems);
        gridViewItems.setAdapter(adapter);
        updateSelectedItemsCount();

        Log.d("KeranjangActivity", "✅ Loaded " + clothingItems.size() + " fallback items");
    }

    private void setupListeners() {
        btnDeleteAll.setOnClickListener(v -> {
            if (adapter != null) {
                List<ClothingItem> selectedItems = adapter.getSelectedItems();
                if (!selectedItems.isEmpty()) {
                    clothingItems.removeAll(selectedItems);
                    adapter = new KeranjangAdapter(KeranjangActivity.this, clothingItems);
                    gridViewItems.setAdapter(adapter);
                    updateSelectedItemsCount();
                    Toast.makeText(KeranjangActivity.this, "Items deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(KeranjangActivity.this, "No items selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSelectAll.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.selectAll(true);
                updateSelectedItemsCount();
            }
        });

        btnRent.setOnClickListener(v -> {
            if (adapter != null) {
                List<ClothingItem> selectedItems = adapter.getSelectedItems();
                if (!selectedItems.isEmpty()) {
                    Toast.makeText(this, "Proceeding to rent " + selectedItems.size() + " items", Toast.LENGTH_SHORT).show();
                    // TODO: Implement rent functionality
                } else {
                    Toast.makeText(this, "Please select items to rent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupBottomNavigation() {
        View.OnClickListener navListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetNavIcons();
                v.setAlpha(1.0f);
                int id = v.getId();
                if (id == R.id.navHome) {
                    startActivity(new Intent(KeranjangActivity.this, HomepageActivity.class));
                } else if (id == R.id.navSaved) {
                    startActivity(new Intent(KeranjangActivity.this, KatalogActivity.class));
                } else if (id == R.id.navCart) {
                    Toast.makeText(KeranjangActivity.this, "Already in Cart", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.navHistory) {
                    startActivity(new Intent(KeranjangActivity.this, RiwayatTransaksiActivity.class));
                } else if (id == R.id.navProfile) {
                    startActivity(new Intent(KeranjangActivity.this, ProfileActivity.class));
                }
            }
        };

        navHome.setOnClickListener(navListener);
        navSaved.setOnClickListener(navListener);
        navCart.setOnClickListener(navListener);
        navHistory.setOnClickListener(navListener);
        navProfile.setOnClickListener(navListener);

        // Set current tab (Cart) as active
        navCart.setAlpha(1.0f);
        navHome.setAlpha(0.7f);
        navSaved.setAlpha(0.7f);
        navHistory.setAlpha(0.7f);
        navProfile.setAlpha(0.7f);
    }

    private void resetNavIcons() {
        navHome.setAlpha(0.7f);
        navSaved.setAlpha(0.7f);
        navCart.setAlpha(0.7f);
        navHistory.setAlpha(0.7f);
        navProfile.setAlpha(0.7f);
    }

    public void updateSelectedItemsCount() {
        if (adapter != null) {
            int selectedCount = adapter.getSelectedItemsCount();
            tvSelectedItems.setText(selectedCount + " selected item" + (selectedCount != 1 ? "s" : ""));

            // Enable/disable buttons based on selection
            btnDeleteAll.setEnabled(selectedCount > 0);
            btnRent.setEnabled(selectedCount > 0);
        } else {
            tvSelectedItems.setText("0 selected items");
            btnDeleteAll.setEnabled(false);
            btnRent.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload items when returning to activity
        loadItemsFromFirebase();
    }
}