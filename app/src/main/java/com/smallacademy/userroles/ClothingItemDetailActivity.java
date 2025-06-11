package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClothingItemDetailActivity extends AppCompatActivity {

    private ImageView ivItemImage;
    private TextView tvItemName, tvItemCategory, tvItemPrice, tvItemCode, tvItemStatus, tvCreatedDate;
    private Button btnEditItem, btnDeleteItem, btnChangeStatus;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String itemId;
    private ClothingItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_item_detail);

        initViews();
        setupToolbar();
        setupFirebase();

        // Get item ID from intent
        itemId = getIntent().getStringExtra("itemId");
        if (itemId == null) {
            Toast.makeText(this, "Error: Item ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadItemDetails();
        setupClickListeners();
    }

    private void initViews() {
        ivItemImage = findViewById(R.id.ivItemImage);
        tvItemName = findViewById(R.id.tvItemName);
        tvItemCategory = findViewById(R.id.tvItemCategory);
        tvItemPrice = findViewById(R.id.tvItemPrice);
        tvItemCode = findViewById(R.id.tvItemCode);
        tvItemStatus = findViewById(R.id.tvItemStatus);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        btnEditItem = findViewById(R.id.btnEditItem);
        btnDeleteItem = findViewById(R.id.btnDeleteItem);
        btnChangeStatus = findViewById(R.id.btnChangeStatus);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Item");
        }
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadItemDetails() {
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
                            Log.d("ClothingItemDetail", "✅ ImageResId loaded: " + currentItem.getImageResId());
                        } else {
                            Log.w("ClothingItemDetail", "⚠️ ImageResId Long is null");
                            currentItem.setImageResId(R.drawable.baju1);
                        }
                    } else {
                        Log.w("ClothingItemDetail", "⚠️ No imageResId field");
                        currentItem.setImageResId(R.drawable.baju1);
                    }

                    displayItemDetails();
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

    private void displayItemDetails() {
        tvItemName.setText(currentItem.getName());
        tvItemCategory.setText(currentItem.getCategory());
        tvItemPrice.setText(currentItem.getPrice());
        tvItemCode.setText(currentItem.getCode());
        tvItemStatus.setText(currentItem.getStatus());

        // Format created date
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault());
        String formattedDate = sdf.format(new java.util.Date(currentItem.getCreatedAt()));
        tvCreatedDate.setText("Dibuat: " + formattedDate);

        // Set status color
        updateStatusDisplay();

        // Set item image - HANYA menggunakan imageResId dari database, tidak ada placeholder
        Log.d("ClothingItemDetail", "Setting image for: " + currentItem.getName() + ", ImageResId: " + currentItem.getImageResId());

        if (currentItem.getImageResId() != 0) {
            try {
                ivItemImage.setImageResource(currentItem.getImageResId());
                Log.d("ClothingItemDetail", "✅ Berhasil set image: " + currentItem.getImageResId());
            } catch (Exception e) {
                Log.e("ClothingItemDetail", "❌ Error loading image: " + currentItem.getImageResId(), e);
                // Fallback to default image if error
                setDefaultImage();
            }
        } else {
            Log.w("ClothingItemDetail", "⚠️ ImageResId = 0, using default");
            setDefaultImage();
        }

        // Update button text based on current status
        updateChangeStatusButton();
    }

    private void setDefaultImage() {
        // Tidak menggunakan placeholder, gunakan default image (baju1)
        ivItemImage.setImageResource(R.drawable.baju1);
        Log.d("ClothingItemDetail", "Using default image: R.drawable.baju1");
    }

    private void updateStatusDisplay() {
        int statusColor;
        int backgroundColor;

        switch (currentItem.getStatus().toLowerCase()) {
            case "tersedia":
                statusColor = ContextCompat.getColor(this, android.R.color.white);
                backgroundColor = ContextCompat.getColor(this, android.R.color.holo_green_dark);
                break;
            case "disewa":
                statusColor = ContextCompat.getColor(this, android.R.color.white);
                backgroundColor = ContextCompat.getColor(this, android.R.color.holo_orange_dark);
                break;
            case "perawatan":
                statusColor = ContextCompat.getColor(this, android.R.color.white);
                backgroundColor = ContextCompat.getColor(this, android.R.color.holo_red_dark);
                break;
            default:
                statusColor = ContextCompat.getColor(this, android.R.color.black);
                backgroundColor = ContextCompat.getColor(this, android.R.color.darker_gray);
                break;
        }

        tvItemStatus.setTextColor(statusColor);
        tvItemStatus.setBackgroundColor(backgroundColor);
    }

    private void updateChangeStatusButton() {
        String currentStatus = currentItem.getStatus().toLowerCase();
        switch (currentStatus) {
            case "tersedia":
                btnChangeStatus.setText("Tandai Disewa");
                break;
            case "disewa":
                btnChangeStatus.setText("Tandai Tersedia");
                break;
            case "perawatan":
                btnChangeStatus.setText("Tandai Tersedia");
                break;
            default:
                btnChangeStatus.setText("Ubah Status");
                break;
        }
    }

    private void setupClickListeners() {
        btnEditItem.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditClothingItemActivity.class);
            intent.putExtra("itemId", itemId);
            startActivity(intent);
        });

        btnDeleteItem.setOnClickListener(v -> showDeleteConfirmation());

        btnChangeStatus.setOnClickListener(v -> changeItemStatus());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Item")
                .setMessage("Apakah Anda yakin ingin menghapus item ini?")
                .setPositiveButton("Ya", (dialog, which) -> deleteItem())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteItem() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("clothing_items").document(itemId)
                .update("isActive", false)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Item berhasil dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal menghapus item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void changeItemStatus() {
        String newStatus;
        String currentStatus = currentItem.getStatus().toLowerCase();

        switch (currentStatus) {
            case "tersedia":
                newStatus = "Disewa";
                break;
            case "disewa":
                newStatus = "Tersedia";
                break;
            case "perawatan":
                newStatus = "Tersedia";
                break;
            default:
                newStatus = "Tersedia";
                break;
        }

        progressBar.setVisibility(View.VISIBLE);

        db.collection("clothing_items").document(itemId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    currentItem.setStatus(newStatus);
                    tvItemStatus.setText(newStatus);
                    updateStatusDisplay();
                    updateChangeStatusButton();
                    Toast.makeText(this, "Status berhasil diubah", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal mengubah status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload item details when returning from edit activity
        if (itemId != null) {
            loadItemDetails();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}