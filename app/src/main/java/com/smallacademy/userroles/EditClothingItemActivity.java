package com.smallacademy.userroles;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditClothingItemActivity extends AppCompatActivity {

    private EditText etItemName, etItemPrice, etItemCode;
    private Spinner spinnerCategory, spinnerType, spinnerStatus;
    private Button btnUpdateItem, btnCancel;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String itemId;
    private ClothingItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clothing_item);

        initViews();
        setupToolbar();
        setupSpinners();
        setupClickListeners();
        setupFirebase();

        // Get item ID from intent
        itemId = getIntent().getStringExtra("itemId");
        if (itemId == null) {
            Toast.makeText(this, "Error: Item ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadItemData();
    }

    private void initViews() {
        etItemName = findViewById(R.id.etItemName);
        etItemPrice = findViewById(R.id.etItemPrice);
        etItemCode = findViewById(R.id.etItemCode);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdateItem = findViewById(R.id.btnUpdateItem);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        // Tidak ada lagi ivItemPreview - gambar preview dihapus
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Item");
        }
    }

    private void setupSpinners() {
        // Category Spinner
        String[] categories = {"Gaun Formal", "Jas Pria", "Kaos Formal", "Kaos Casual", "Celana Formal", "Celana Casual"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Type Spinner
        String[] types = {"formal", "casual"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Status Spinner
        String[] statuses = {"Tersedia", "Disewa", "Perawatan"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupClickListeners() {
        btnUpdateItem.setOnClickListener(v -> updateItem());
        btnCancel.setOnClickListener(v -> finish());

        // Tidak ada lagi listener untuk spinnerType karena tidak ada preview image
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadItemData() {
        progressBar.setVisibility(View.VISIBLE);

        DocumentReference docRef = db.collection("clothing_items").document(itemId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            progressBar.setVisibility(View.GONE);

            if (documentSnapshot.exists()) {
                currentItem = documentSnapshot.toObject(ClothingItem.class);
                if (currentItem != null) {
                    currentItem.setId(documentSnapshot.getId());

                    // Handle imageResId dari Firestore
                    if (documentSnapshot.contains("imageResId")) {
                        Long imageResIdLong = documentSnapshot.getLong("imageResId");
                        if (imageResIdLong != null) {
                            currentItem.setImageResId(imageResIdLong.intValue());
                        }
                    }

                    populateFields();
                }
            } else {
                Toast.makeText(this, "Item tidak ditemukan", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error loading item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void populateFields() {
        etItemName.setText(currentItem.getName());
        etItemCode.setText(currentItem.getCode());

        // Extract price number from formatted price string
        String price = currentItem.getPrice();
        if (price.startsWith("Rp ")) {
            price = price.replace("Rp ", "").replace("/hari", "").replace(".", "").trim();
        }
        etItemPrice.setText(price);

        // Set spinner selections
        setSpinnerSelection(spinnerCategory, currentItem.getCategory());
        setSpinnerSelection(spinnerType, currentItem.getType());
        setSpinnerSelection(spinnerStatus, currentItem.getStatus());

        // Tidak ada lagi updatePreviewImage() karena tidak ada preview image
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void updateItem() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnUpdateItem.setEnabled(false);

        // Get form data
        String name = etItemName.getText().toString().trim();
        String priceText = etItemPrice.getText().toString().trim();
        String code = etItemCode.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String type = spinnerType.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        // Format price
        String formattedPrice = formatPrice(priceText);

        // Create update map
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("category", category);
        updates.put("price", formattedPrice);
        updates.put("code", code);
        updates.put("status", status);
        updates.put("type", type);

        // Update in Firestore
        db.collection("clothing_items").document(itemId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Item berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnUpdateItem.setEnabled(true);
                    Toast.makeText(this, "Gagal memperbarui item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(etItemName.getText().toString().trim())) {
            etItemName.setError("Nama item tidak boleh kosong");
            etItemName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etItemPrice.getText().toString().trim())) {
            etItemPrice.setError("Harga tidak boleh kosong");
            etItemPrice.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(etItemCode.getText().toString().trim())) {
            etItemCode.setError("Kode item tidak boleh kosong");
            etItemCode.requestFocus();
            return false;
        }

        // Validate price is numeric
        try {
            Double.parseDouble(etItemPrice.getText().toString().trim());
        } catch (NumberFormatException e) {
            etItemPrice.setError("Harga harus berupa angka");
            etItemPrice.requestFocus();
            return false;
        }

        return true;
    }

    private String formatPrice(String price) {
        try {
            double priceValue = Double.parseDouble(price);
            return String.format("Rp %.0f/hari", priceValue);
        } catch (NumberFormatException e) {
            return "Rp " + price + "/hari";
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}