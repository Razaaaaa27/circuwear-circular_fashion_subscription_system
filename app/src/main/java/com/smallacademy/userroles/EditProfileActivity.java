package com.smallacademy.userroles;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private EditText etFullName, etEmail, etPhone;
    private AppCompatButton btnSave;
    private ImageButton btnBack, btnSelectImage;
    private ProgressBar progressBar;
    private CardView cardViewProfile;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseUser currentUser;

    // Image selection
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setupImagePicker();
        setupListeners();
        loadCurrentData();
    }

    private void initViews() {
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        progressBar = findViewById(R.id.progressBar);
        cardViewProfile = findViewById(R.id.cardViewProfile);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            loadImageToView(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnSelectImage.setOnClickListener(v -> openImagePicker());

        cardViewProfile.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentData() {
        // Get data from intent (passed from ProfileActivity)
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("fullName");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");

        if (fullName != null && !fullName.equals("Loading...")) {
            etFullName.setText(fullName);
        }

        if (email != null && !email.equals("Loading...")) {
            etEmail.setText(email);
        }

        if (phone != null && !phone.equals("Loading...") && !phone.equals("No phone number")) {
            etPhone.setText(phone);
        }

        // Load current profile image
        loadCurrentProfileImage();
    }

    private void loadCurrentProfileImage() {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String profileImageUrl = document.getString("profileImageUrl");
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                RequestOptions requestOptions = new RequestOptions()
                                        .circleCrop()
                                        .placeholder(R.drawable.profile_placeholder)
                                        .error(R.drawable.profile_placeholder);

                                Glide.with(this)
                                        .load(profileImageUrl)
                                        .apply(requestOptions)
                                        .into(ivProfilePicture);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Load default image on failure
                        loadImageToView(null);
                    });
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void loadImageToView(Uri imageUri) {
        RequestOptions requestOptions = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder);

        if (imageUri != null) {
            Glide.with(this)
                    .load(imageUri)
                    .apply(requestOptions)
                    .into(ivProfilePicture);
        } else {
            Glide.with(this)
                    .load(R.drawable.profile_placeholder)
                    .apply(requestOptions)
                    .into(ivProfilePicture);
        }
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        showProgress(true);

        if (currentUser != null) {
            String userId = currentUser.getUid();

            if (selectedImageUri != null) {
                // Upload image first, then save profile
                uploadImageAndSaveProfile(userId, fullName, email, phone);
            } else {
                // Save profile without changing image
                saveProfileData(userId, fullName, email, phone, null);
            }
        }
    }

    private void uploadImageAndSaveProfile(String userId, String fullName, String email, String phone) {
        // Create a unique filename for the image
        String imageFileName = "profile_images/" + userId + "_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(imageFileName);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String imageUrl = downloadUri.toString();
                        saveProfileData(userId, fullName, email, phone, imageUrl);
                    }).addOnFailureListener(e -> {
                        showProgress(false);
                        Toast.makeText(EditProfileActivity.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(EditProfileActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProfileData(String userId, String fullName, String email, String phone, String imageUrl) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("fullName", fullName);
        userUpdates.put("UserEmail", email);
        userUpdates.put("PhoneNumber", phone);

        if (imageUrl != null) {
            userUpdates.put("profileImageUrl", imageUrl);
        }

        db.collection("users").document(userId)
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    showProgress(false);
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Return to ProfileActivity
                })
                .addOnFailureListener(e -> {
                    showProgress(false);
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
        btnSave.setText(show ? "Saving..." : "Save Changes");
    }
}