package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smallacademy.userroles.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManajemenPenggunaActivity extends AppCompatActivity {

    private static final String TAG = "ManajemenPengguna";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FloatingActionButton fabAddUser;

    private FirebaseFirestore db;
    private CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manajemen_pengguna);

        initViews();
        setupFirebase();
        setupRecyclerView();
        loadUsers();

        fabAddUser.setOnClickListener(v -> showAddUserDialog());
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manajemen Pengguna");

        recyclerView = findViewById(R.id.recyclerViewUsers);
        fabAddUser = findViewById(R.id.fabAddUser);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this::showUserDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    private void loadUsers() {
        // Filter untuk hanya menampilkan user biasa (bukan admin)
        usersRef.whereEqualTo("isUser", "1").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    user.setId(document.getId());

                    // Double check - pastikan bukan admin
                    if (isRegularUser(user)) {
                        userList.add(user);
                        Log.d(TAG, "Loaded user: " + user.getName() + " (ID: " + user.getId() + ")");
                    }
                }
                userAdapter.notifyDataSetChanged();
                Log.d(TAG, "Total users loaded: " + userList.size());
            } else {
                Log.e(TAG, "Error loading users: " + task.getException().getMessage());
                Toast.makeText(this, "Error loading users: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method untuk memastikan user adalah user biasa (bukan admin)
     */
    private boolean isRegularUser(User user) {
        // Filter berdasarkan isUser field
        if (user.getIsUser() != null && user.getIsUser().equals("1")) {
            return true;
        }

        // Filter berdasarkan email admin (opsional - sesuaikan dengan kebutuhan)
        if (user.getEmail() != null) {
            String email = user.getEmail().toLowerCase();
            // Kecualikan email admin tertentu
            if (email.contains("admin") ||
                    email.equals("admin@smallacademy.com") ||
                    email.equals("administrator@circuwear.com")) {
                return false;
            }
        }

        // Filter berdasarkan status admin (jika ada field role)
        if (user.getStatus() != null && user.getStatus().equals("Admin")) {
            return false;
        }

        return true;
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_user, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etUserName);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etUserEmail);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etUserPhone);

        builder.setView(dialogView)
                .setTitle("Tambah Pengguna")
                .setPositiveButton("Tambah", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
                        Toast.makeText(this, "Nama dan email harus diisi", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    addUser(name, email, phone);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void addUser(String name, String email, String phone) {
        Map<String, Object> user = new HashMap<>();
        // Use Firebase field names to match existing data structure
        user.put("fullName", name);
        user.put("UserEmail", email);
        user.put("PhoneNumber", phone != null && !phone.isEmpty() ? phone : "");
        user.put("status", "Aktif");
        user.put("joinDate", System.currentTimeMillis());
        user.put("isUser", "1"); // Add isUser field for consistency

        Log.d(TAG, "Adding user with data: " + user.toString());

        usersRef.add(user).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "User added successfully with ID: " + documentReference.getId());
            Toast.makeText(this, "Pengguna berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            loadUsers();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to add user: " + e.getMessage());
            Toast.makeText(this, "Gagal menambahkan pengguna: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showUserDetails(User user) {
        Log.d(TAG, "Attempting to open UserDetail for: " + user.getName() + " with ID: " + user.getId());

        try {
            // Validate user data before opening activity
            if (user.getId() == null || user.getId().isEmpty()) {
                Toast.makeText(this, "Error: User ID is invalid", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "User ID is null or empty");
                return;
            }

            Intent intent = new Intent(this, UserDetailActivity.class);
            intent.putExtra("userId", user.getId());

            // Add flags to prevent any issues with activity transitions
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Log.d(TAG, "Starting UserDetailActivity with userId: " + user.getId());
            startActivity(intent);

        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException when starting UserDetailActivity: " + e.getMessage());
            Toast.makeText(this, "Security error occurred. Please try again.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "General exception when starting UserDetailActivity: " + e.getMessage());
            Toast.makeText(this, "Error opening user details: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}