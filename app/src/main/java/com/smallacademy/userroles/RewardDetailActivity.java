package com.smallacademy.userroles;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.clothrental.models.Reward;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RewardDetailActivity extends AppCompatActivity {

    private TextView tvRewardTitle, tvRewardDescription, tvRewardPoints, tvRewardValue, tvTimesUsed;
    private Button btnRedeemReward, btnDeactivateReward;

    private FirebaseFirestore db;
    private String rewardId;
    private Reward currentReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);

        initViews();
        setupFirebase();
        getRewardData();
        setupClickListeners();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Reward");

        tvRewardTitle = findViewById(R.id.tvRewardTitle);
        tvRewardDescription = findViewById(R.id.tvRewardDescription);
        tvRewardPoints = findViewById(R.id.tvRewardPoints);
        tvRewardValue = findViewById(R.id.tvRewardValue);
        tvTimesUsed = findViewById(R.id.tvTimesUsed);
        btnRedeemReward = findViewById(R.id.btnRedeemReward);
        btnDeactivateReward = findViewById(R.id.btnDeactivateReward);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        rewardId = getIntent().getStringExtra("rewardId");
    }

    private void getRewardData() {
        if (rewardId == null) {
            Toast.makeText(this, "Error: Reward ID tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DocumentReference rewardRef = db.collection("rewards").document(rewardId);
        rewardRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                currentReward = documentSnapshot.toObject(Reward.class);
                if (currentReward != null) {
                    currentReward.setId(documentSnapshot.getId());
                    displayRewardData();
                }
            } else {
                Toast.makeText(this, "Reward tidak ditemukan", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error memuat data reward", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void displayRewardData() {
        tvRewardTitle.setText(currentReward.getTitle());
        tvRewardDescription.setText(currentReward.getDescription());
        tvRewardPoints.setText(currentReward.getPointsRequired() + " Poin");
        tvRewardValue.setText(currentReward.getRewardValue());
        tvTimesUsed.setText("Digunakan: " + currentReward.getTimesUsed() + " kali");
    }

    private void setupClickListeners() {
        btnRedeemReward.setOnClickListener(v -> redeemReward());
        btnDeactivateReward.setOnClickListener(v -> deactivateReward());
    }

    private void redeemReward() {
        if (currentReward == null) return;

        // Update times used
        DocumentReference rewardRef = db.collection("rewards").document(rewardId);
        rewardRef.update("timesUsed", currentReward.getTimesUsed() + 1)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Reward berhasil ditukar!", Toast.LENGTH_SHORT).show();
                    currentReward.setTimesUsed(currentReward.getTimesUsed() + 1);
                    displayRewardData();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menukar reward", Toast.LENGTH_SHORT).show();
                });
    }

    private void deactivateReward() {
        if (currentReward == null) return;

        DocumentReference rewardRef = db.collection("rewards").document(rewardId);
        rewardRef.update("isActive", false)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Reward berhasil dinonaktifkan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menonaktifkan reward", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}