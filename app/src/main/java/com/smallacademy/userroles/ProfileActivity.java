package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private TextView tvProfileName, tvProfileEmail, tvProfilePhone;
    private AppCompatButton btnLogout;
    private ImageButton btnBack, btnEditProfile;
    private CardView cardEmail, cardPhone;

    // Bottom nav
    private ImageView navHome, navSaved, navCart, navHistory, navProfile;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setupListeners();
        loadUserDataFromFirebase();
        setupBottomNavigation();
    }

    private void initViews() {
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        cardEmail = findViewById(R.id.cardEmail);
        cardPhone = findViewById(R.id.cardPhone);

        navHome = findViewById(R.id.navHome);
        navSaved = findViewById(R.id.navSaved);
        navCart = findViewById(R.id.navCart);
        navHistory = findViewById(R.id.navHistory);
        navProfile = findViewById(R.id.navProfile);

        setupProfileSectionClicks();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnEditProfile.setOnClickListener(v -> {
            Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            editIntent.putExtra("fullName", tvProfileName.getText().toString());
            editIntent.putExtra("email", tvProfileEmail.getText().toString());
            editIntent.putExtra("phone", tvProfilePhone.getText().toString());
            startActivity(editIntent);
        });

        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void setupProfileSectionClicks() {
        View.OnClickListener sectionClickListener = v -> {
            String sectionName = "";

            if (v == cardEmail) {
                sectionName = "Email";
                // You can add email editing functionality here
            } else if (v == cardPhone) {
                sectionName = "Phone";
                // You can add phone editing functionality here
            }

            Toast.makeText(ProfileActivity.this, sectionName + " section clicked", Toast.LENGTH_SHORT).show();
        };

        cardEmail.setOnClickListener(sectionClickListener);
        cardPhone.setOnClickListener(sectionClickListener);
    }

    private void setupBottomNavigation() {
        View.OnClickListener navListener = v -> {
            resetNavIcons();
            v.setAlpha(1.0f);

            int id = v.getId();
            if (id == R.id.navHome) {
                startActivity(new Intent(ProfileActivity.this, HomepageActivity.class));
                finish();
            } else if (id == R.id.navSaved) {
                startActivity(new Intent(ProfileActivity.this, KatalogActivity.class));
                finish();
            } else if (id == R.id.navCart) {
                startActivity(new Intent(ProfileActivity.this, KeranjangActivity.class));
                finish();
            } else if (id == R.id.navHistory) {
                startActivity(new Intent(ProfileActivity.this, RiwayatTransaksiActivity.class));
                finish();
            } else if (id == R.id.navProfile) {
                Toast.makeText(ProfileActivity.this, "Already on Profile", Toast.LENGTH_SHORT).show();
            }
        };

        navHome.setOnClickListener(navListener);
        navSaved.setOnClickListener(navListener);
        navCart.setOnClickListener(navListener);
        navHistory.setOnClickListener(navListener);
        navProfile.setOnClickListener(navListener);

        // Highlight current nav
        resetNavIcons();
        navProfile.setAlpha(1.0f);
    }

    private void resetNavIcons() {
        navHome.setAlpha(0.7f);
        navSaved.setAlpha(0.7f);
        navCart.setAlpha(0.7f);
        navHistory.setAlpha(0.7f);
        navProfile.setAlpha(0.7f);
    }

    private void loadUserDataFromFirebase() {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get data from Firestore
                                String fullName = document.getString("fullName");
                                String email = document.getString("UserEmail");
                                String phone = document.getString("PhoneNumber");
                                String profileImageUrl = document.getString("profileImageUrl");

                                // Set data to views
                                if (fullName != null && !fullName.isEmpty()) {
                                    tvProfileName.setText(fullName);
                                } else {
                                    tvProfileName.setText("User Name");
                                }

                                if (email != null && !email.isEmpty()) {
                                    tvProfileEmail.setText(email);
                                } else {
                                    tvProfileEmail.setText(currentUser.getEmail());
                                }

                                if (phone != null && !phone.isEmpty()) {
                                    tvProfilePhone.setText(phone);
                                } else {
                                    tvProfilePhone.setText("No phone number");
                                }

                                // Load profile image
                                loadProfileImage(profileImageUrl);
                            } else {
                                // Document doesn't exist, use default values
                                tvProfileName.setText("User Name");
                                tvProfileEmail.setText(currentUser.getEmail());
                                tvProfilePhone.setText("No phone number");
                                loadProfileImage(null);
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Error loading profile data", Toast.LENGTH_SHORT).show();
                            // Use fallback data
                            tvProfileName.setText("User Name");
                            tvProfileEmail.setText(currentUser.getEmail());
                            tvProfilePhone.setText("No phone number");
                            loadProfileImage(null);
                        }
                    });
        }
    }

    private void loadProfileImage(String imageUrl) {
        RequestOptions requestOptions = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(ivProfilePicture);
        } else {
            // Use default profile image or initials
            Glide.with(this)
                    .load(R.drawable.profile_placeholder)
                    .apply(requestOptions)
                    .into(ivProfilePicture);
        }
    }

    private void performLogout() {
        mAuth.signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfileActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data when returning to this activity
        loadUserDataFromFirebase();
    }
}