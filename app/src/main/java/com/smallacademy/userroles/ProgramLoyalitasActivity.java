package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Import R class dari package yang benar
import com.smallacademy.userroles.R;
// RewardAdapter sekarang berada di package yang sama, tidak perlu import
// import com.smallacademy.userroles.RewardAdapter; // tidak diperlukan
import com.example.clothrental.models.Reward;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramLoyalitasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RewardAdapter rewardAdapter;
    private List<Reward> rewardList;
    private FloatingActionButton fabAddReward;

    private FirebaseFirestore db;
    private CollectionReference rewardsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_loyalitas);

        initViews();
        setupFirebase();
        setupRecyclerView();
        loadRewards();

        fabAddReward.setOnClickListener(v -> showAddRewardDialog());
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Program Loyalitas");

        recyclerView = findViewById(R.id.recyclerViewRewards);
        fabAddReward = findViewById(R.id.fabAddReward);
    }

    private void setupFirebase() {
        db = FirebaseFirestore.getInstance();
        rewardsRef = db.collection("rewards");
    }

    private void setupRecyclerView() {
        rewardList = new ArrayList<>();
        rewardAdapter = new RewardAdapter(rewardList, this::showRewardDetails);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(rewardAdapter);

        // Add default rewards if none exist
        addDefaultRewards();
    }

    private void addDefaultRewards() {
        rewardsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                addDefaultReward("Diskon 25%", "Dapatkan diskon pembelian berlaku untuk semua item", 500, "25%", "discount");
                addDefaultReward("Gratis Aksesoris", "Gratis 1 aksesoris dengan penyewaan pakaian", 750, "Gratis", "accessory");
                addDefaultReward("Gratis Pengiriman", "Gratis pengiriman untuk 4 penyewaan berulangnya", 1200, "12x", "delivery");
                addDefaultReward("Akses VIP", "Akses VIP ke koleksi terbaru selama 3 bulan", 1200, "VIP", "vip");
            }
        });
    }

    private void addDefaultReward(String title, String description, int points, String value, String type) {
        Map<String, Object> reward = new HashMap<>();
        reward.put("title", title);
        reward.put("description", description);
        reward.put("pointsRequired", points);
        reward.put("rewardValue", value);
        reward.put("type", type);
        reward.put("isActive", true);
        reward.put("timesUsed", 0);

        rewardsRef.add(reward);
    }

    private void loadRewards() {
        rewardsRef.whereEqualTo("isActive", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        rewardList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reward reward = document.toObject(Reward.class);
                            reward.setId(document.getId());
                            rewardList.add(reward);
                        }
                        rewardAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading rewards", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddRewardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_reward, null);

        TextInputEditText etTitle = dialogView.findViewById(R.id.etRewardTitle);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etRewardDescription);
        TextInputEditText etPoints = dialogView.findViewById(R.id.etRewardPoints);
        TextInputEditText etValue = dialogView.findViewById(R.id.etRewardValue);

        builder.setView(dialogView)
                .setTitle("Tambah Reward")
                .setPositiveButton("Tambah", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String pointsStr = etPoints.getText().toString().trim();
                    String value = etValue.getText().toString().trim();

                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(pointsStr)) {
                        Toast.makeText(this, "Judul dan poin harus diisi", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int points = Integer.parseInt(pointsStr);
                    addReward(title, description, points, value);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void addReward(String title, String description, int points, String value) {
        Map<String, Object> reward = new HashMap<>();
        reward.put("title", title);
        reward.put("description", description);
        reward.put("pointsRequired", points);
        reward.put("rewardValue", value);
        reward.put("type", "custom");
        reward.put("isActive", true);
        reward.put("timesUsed", 0);
        reward.put("createdAt", System.currentTimeMillis());

        rewardsRef.add(reward).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Reward berhasil ditambahkan", Toast.LENGTH_SHORT).show();
            loadRewards();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Gagal menambahkan reward", Toast.LENGTH_SHORT).show();
        });
    }

    private void showRewardDetails(Reward reward) {
        Intent intent = new Intent(this, RewardDetailActivity.class);
        intent.putExtra("rewardId", reward.getId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}