package com.smallacademy.userroles;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserDetailActivity extends AppCompatActivity {

    private static final String TAG = "UserDetailActivity";

    private TextView tvUserName, tvUserEmail, tvUserPhone, tvUserStatus, tvJoinDate;
    private Button btnEditUser, btnDeleteUser, btnToggleStatus;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        initViews();
        setupToolbar();
        setupFirebase();

        // Get user ID from intent
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "UserDetailActivity started with userId: " + userId);
        loadUserDetails();
        setupClickListeners();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvUserStatus = findViewById(R.id.tvUserStatus);
        tvJoinDate = findViewById(R.id.tvJoinDate);
        btnEditUser = findViewById(R.id.btnEditUser);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        btnToggleStatus = findViewById(R.id.btnToggleStatus);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Pengguna");
        }
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadUserDetails() {
        progressBar.setVisibility(View.VISIBLE);

        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            progressBar.setVisibility(View.GONE);

            if (documentSnapshot.exists()) {
                try {
                    currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser != null) {
                        currentUser.setId(documentSnapshot.getId());

                        // Log user data untuk debugging
                        Log.d(TAG, "User loaded: " + currentUser.getName() +
                                ", Status: " + currentUser.getStatus() +
                                ", Email: " + currentUser.getEmail());

                        displayUserDetails();
                    } else {
                        Log.e(TAG, "Failed to convert document to User object");
                        Toast.makeText(this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing user data: " + e.getMessage());
                    Toast.makeText(this, "Error processing user data", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Log.e(TAG, "User document does not exist");
                Toast.makeText(this, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, "Error loading user: " + e.getMessage());
            Toast.makeText(this, "Error loading user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void displayUserDetails() {
        // Safe display with null checks
        tvUserName.setText(currentUser.getName() != null ? currentUser.getName() : "Nama tidak tersedia");
        tvUserEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Email tidak tersedia");
        tvUserPhone.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "Tidak tersedia");

        // Safe status handling with default value
        String status = currentUser.getStatus();
        if (status == null || status.isEmpty()) {
            status = "Aktif"; // Default status
            currentUser.setStatus(status);
            // Update status in Firebase
            updateStatusInFirebase(status);
        }
        tvUserStatus.setText(status);

        // Format join date safely
        if (currentUser.getJoinDate() != 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                String formattedDate = sdf.format(new Date(currentUser.getJoinDate()));
                tvJoinDate.setText("Bergabung: " + formattedDate);
            } catch (Exception e) {
                Log.e(TAG, "Error formatting date: " + e.getMessage());
                tvJoinDate.setText("Bergabung: Tidak tersedia");
            }
        } else {
            tvJoinDate.setText("Bergabung: Tidak tersedia");
        }

        // Update button after ensuring status is not null
        updateToggleStatusButton();
    }

    private void updateToggleStatusButton() {
        // Safe null check before calling equals
        String status = currentUser.getStatus();
        if (status != null && status.equals("Aktif")) {
            btnToggleStatus.setText("Nonaktifkan");
            btnToggleStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            btnToggleStatus.setText("Aktifkan");
            btnToggleStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void updateStatusInFirebase(String status) {
        db.collection("users").document(userId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Status updated to: " + status))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update status: " + e.getMessage()));
    }

    private void setupClickListeners() {
        btnEditUser.setOnClickListener(v -> {
            // TODO: Implement edit user functionality
            Toast.makeText(this, "Edit user functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        btnDeleteUser.setOnClickListener(v -> showDeleteConfirmation());

        btnToggleStatus.setOnClickListener(v -> toggleUserStatus());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Pengguna")
                .setMessage("Apakah Anda yakin ingin menghapus pengguna ini?")
                .setPositiveButton("Ya", (dialog, which) -> deleteUser())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteUser() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Pengguna berhasil dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal menghapus pengguna: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleUserStatus() {
        String currentStatus = currentUser.getStatus();
        String newStatus = (currentStatus != null && currentStatus.equals("Aktif")) ? "Nonaktif" : "Aktif";

        progressBar.setVisibility(View.VISIBLE);

        db.collection("users").document(userId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    currentUser.setStatus(newStatus);
                    tvUserStatus.setText(newStatus);
                    updateToggleStatusButton();
                    Toast.makeText(this, "Status berhasil diubah", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal mengubah status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}