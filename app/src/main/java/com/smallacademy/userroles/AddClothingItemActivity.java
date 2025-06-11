package com.smallacademy.userroles;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddClothingItemActivity extends AppCompatActivity {

    private EditText etItemName, etItemPrice, etItemCode;
    private Spinner spinnerCategory, spinnerType, spinnerStatus;
    private Button btnSaveItem, btnCancel;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing_item);

        initViews();
        setupToolbar();
        setupSpinners();
        setupClickListeners();
        setupFirebase();
    }

    private void initViews() {
        etItemName = findViewById(R.id.etItemName);
        etItemPrice = findViewById(R.id.etItemPrice);
        etItemCode = findViewById(R.id.etItemCode);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSaveItem = findViewById(R.id.btnSaveItem);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        // Tidak ada lagi ivItemPreview - gambar preview dihapus
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tambah Item Baru");
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
        btnSaveItem.setOnClickListener(v -> saveItem());
        btnCancel.setOnClickListener(v -> finish());

        // Tidak ada lagi listener untuk spinnerType karena tidak ada preview image
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private int getImageResIdForItem(String category, String type) {
        // Pilih gambar berdasarkan category dan type untuk memberikan variasi
        if (type.equalsIgnoreCase("formal")) {
            switch (category) {
                case "Gaun Formal":
                    return R.drawable.baju1;
                case "Jas Pria":
                    return R.drawable.baju02;
                case "Kaos Formal":
                    return R.drawable.baju03;
                case "Celana Formal":
                    return R.drawable.baju1;
                default:
                    return R.drawable.baju1;
            }
        } else { // casual
            switch (category) {
                case "Kaos Casual":
                    return R.drawable.baju001;
                case "Celana Casual":
                    return R.drawable.baju08;
                case "Kaos Formal":
                    return R.drawable.baju04;
                default:
                    return R.drawable.baju001;
            }
        }
    }

    private void saveItem() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnSaveItem.setEnabled(false);

        // Get form data
        String name = etItemName.getText().toString().trim();
        String priceText = etItemPrice.getText().toString().trim();
        String code = etItemCode.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String type = spinnerType.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        // Format price
        String formattedPrice = formatPrice(priceText);

        // Get appropriate image resource based on category and type
        int imageResId = getImageResIdForItem(category, type);

        // Create item map
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("category", category);
        item.put("price", formattedPrice);
        item.put("code", code);
        item.put("status", status);
        item.put("type", type);
        item.put("imageUrl", "");
        item.put("imageResId", imageResId); // Dynamic image resource based on category/type
        item.put("isActive", true);
        item.put("createdAt", System.currentTimeMillis());

        // Save to Firestore
        db.collection("clothing_items")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Item berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSaveItem.setEnabled(true);
                    Toast.makeText(this, "Gagal menambahkan item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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