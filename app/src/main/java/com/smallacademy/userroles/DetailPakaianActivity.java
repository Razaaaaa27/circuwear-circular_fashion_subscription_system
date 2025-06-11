package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetailPakaianActivity extends AppCompatActivity {

    private ImageView ivItemImage, ivBack;
    private TextView tvItemName, tvDetailTitle, tvDeskripsiProduk;
    private TextView tvModel, tvPanjangJaket, tvWarna, tvBahan, tvDetail;
    private TextView tvHargaPerHari, tvTotalHarga;
    private EditText etJumlahHari;
    private Button btnMinus, btnPlus, btnRent, btnCart;
    private ProgressBar progressBar;

    // Data item
    private String itemId, itemName, itemCategory, itemPrice, itemCode, itemStatus, itemType;
    private int itemImageResId;
    private double hargaPerHari = 0;
    private int jumlahHari = 1;

    // Firebase
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pakaian);

        // Hide default action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupFirebase();
        getDataFromIntent();
        setupClickListeners();
        displayItemDetails();
    }

    private void initViews() {
        // Header
        ivBack = findViewById(R.id.ivBack);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Detail Pakaian");

        // Item details
        ivItemImage = findViewById(R.id.ivItemImage);
        tvItemName = findViewById(R.id.tvItemName);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDeskripsiProduk = findViewById(R.id.tvDeskripsiProduk);

        // Harga dan kalkulasi
        tvHargaPerHari = findViewById(R.id.tvHargaPerHari);
        tvTotalHarga = findViewById(R.id.tvTotalHarga);
        etJumlahHari = findViewById(R.id.etJumlahHari);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);

        // Specifications
        tvModel = findViewById(R.id.tvModel);
        tvPanjangJaket = findViewById(R.id.tvPanjangJaket);
        tvWarna = findViewById(R.id.tvWarna);
        tvBahan = findViewById(R.id.tvBahan);
        tvDetail = findViewById(R.id.tvDetail);

        // Buttons
        btnRent = findViewById(R.id.btnRent);
        btnCart = findViewById(R.id.btnCart);

        // Progress bar (optional)
        progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupFirebase() {
        try {
            db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            Log.e("DetailPakaian", "Error initializing Firestore", e);
            Toast.makeText(this, "Error initializing database", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        itemId = intent.getStringExtra("itemId");
        itemName = intent.getStringExtra("itemName");
        itemCategory = intent.getStringExtra("itemCategory");
        itemPrice = intent.getStringExtra("itemPrice");
        itemCode = intent.getStringExtra("itemCode");
        itemStatus = intent.getStringExtra("itemStatus");
        itemType = intent.getStringExtra("itemType");
        itemImageResId = intent.getIntExtra("itemImageResId", R.drawable.baju1);

        Log.d("DetailPakaian", "Received item: " + itemName + ", ImageResId: " + itemImageResId);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> onBackPressed());

        // Plus minus buttons untuk jumlah hari
        btnMinus.setOnClickListener(v -> {
            if (jumlahHari > 1) {
                jumlahHari--;
                updateHargaCalculation();
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (jumlahHari < 30) { // Maximum 30 hari
                jumlahHari++;
                updateHargaCalculation();
            }
        });

        // EditText listener untuk input manual
        etJumlahHari.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateAndUpdateJumlahHari();
            }
        });

        btnRent.setOnClickListener(v -> {
            if (itemStatus != null && itemStatus.equalsIgnoreCase("Tersedia")) {
                showRentConfirmation();
            } else {
                Toast.makeText(this, "Item tidak tersedia untuk disewa", Toast.LENGTH_SHORT).show();
            }
        });

        btnCart.setOnClickListener(v -> {
            // Add to cart functionality (bisa implement nanti)
            Toast.makeText(this, "Item ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke halaman sebelumnya
        });
    }

    private void displayItemDetails() {
        // Set gambar item
        if (itemImageResId != 0) {
            try {
                ivItemImage.setImageResource(itemImageResId);
                Log.d("DetailPakaian", "✅ Image set successfully: " + itemImageResId);
            } catch (Exception e) {
                Log.e("DetailPakaian", "❌ Error setting image: " + itemImageResId, e);
                ivItemImage.setImageResource(R.drawable.baju1); // Default fallback
            }
        } else {
            ivItemImage.setImageResource(R.drawable.baju1); // Default
        }

        // Set nama item
        tvItemName.setText(itemName != null ? itemName : "Nama Item");

        // Extract dan set harga per hari
        extractHargaPerHari();
        setupHargaCalculation();

        // Set deskripsi produk
        String deskripsi = generateProductDescription();
        tvDeskripsiProduk.setText(deskripsi);

        // Set spesifikasi produk
        setProductSpecifications();
    }

    private void extractHargaPerHari() {
        // Debug: Log original price
        Log.d("DetailPakaian", "🔍 Original price string: '" + itemPrice + "'");

        // Extract harga dari string format "Rp 150.000/hari"
        if (itemPrice != null && !itemPrice.isEmpty()) {
            try {
                // Remove common formatting characters
                String cleanPrice = itemPrice.replace("Rp ", "")
                        .replace("Rp", "")
                        .replace("/hari", "")
                        .replace(".", "")
                        .replace(",", "")
                        .replace(" ", "")
                        .trim();

                Log.d("DetailPakaian", "🧹 Cleaned price string: '" + cleanPrice + "'");

                if (!cleanPrice.isEmpty()) {
                    hargaPerHari = Double.parseDouble(cleanPrice);
                    Log.d("DetailPakaian", "✅ Harga per hari extracted: " + hargaPerHari);
                } else {
                    Log.w("DetailPakaian", "⚠️ Cleaned price is empty, using default");
                    hargaPerHari = getDefaultPriceByType();
                }

            } catch (NumberFormatException e) {
                Log.e("DetailPakaian", "❌ Error parsing price: '" + itemPrice + "'", e);
                hargaPerHari = getDefaultPriceByType();
            }
        } else {
            Log.w("DetailPakaian", "⚠️ Price is null or empty, using default");
            hargaPerHari = getDefaultPriceByType();
        }

        // Final debug log
        Log.d("DetailPakaian", "🎯 Final harga per hari: " + hargaPerHari + " for item: " + itemName);
    }

    private double getDefaultPriceByType() {
        // Set default price based on item type
        if (itemType != null && itemType.equalsIgnoreCase("formal")) {
            return 150000; // Default for formal
        } else {
            return 75000; // Default for casual
        }
    }

    private void setupHargaCalculation() {
        // Set harga per hari
        tvHargaPerHari.setText(formatRupiah(hargaPerHari) + "/hari");

        // Set initial jumlah hari
        etJumlahHari.setText(String.valueOf(jumlahHari));

        // Calculate dan tampilkan total harga
        updateHargaCalculation();
    }

    private void updateHargaCalculation() {
        // Update EditText
        etJumlahHari.setText(String.valueOf(jumlahHari));

        // Calculate total harga
        double totalHarga = hargaPerHari * jumlahHari;

        // Update display
        tvTotalHarga.setText(formatRupiah(totalHarga));

        // Update button text dengan total harga
        btnRent.setText("Sewa " + jumlahHari + " Hari - " + formatRupiah(totalHarga));

        Log.d("DetailPakaian", "Harga calculation: " + jumlahHari + " hari × " + formatRupiah(hargaPerHari) + " = " + formatRupiah(totalHarga));
    }

    private void validateAndUpdateJumlahHari() {
        try {
            String input = etJumlahHari.getText().toString().trim();
            if (!input.isEmpty()) {
                int newJumlahHari = Integer.parseInt(input);
                if (newJumlahHari >= 1 && newJumlahHari <= 30) {
                    jumlahHari = newJumlahHari;
                    updateHargaCalculation();
                } else {
                    // Reset ke nilai sebelumnya jika tidak valid
                    etJumlahHari.setText(String.valueOf(jumlahHari));
                    Toast.makeText(this, "Jumlah hari harus antara 1-30", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Reset ke 1 jika kosong
                jumlahHari = 1;
                updateHargaCalculation();
            }
        } catch (NumberFormatException e) {
            // Reset ke nilai sebelumnya jika tidak bisa di-parse
            etJumlahHari.setText(String.valueOf(jumlahHari));
            Toast.makeText(this, "Input tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatRupiah(double amount) {
        return String.format("Rp %,.0f", amount);
    }

    private String generateProductDescription() {
        if (itemType != null && itemType.equalsIgnoreCase("formal")) {
            return "Blazer eksklusif dengan motif bunga mawar timbul yang elegan. Terbuat dari bahan premium cotton blend, memberikan kenyamanan saat digunakan. Cocok untuk acara formal, business meeting, atau acara spesial. Dilengkapi dengan saku depan yang fungsional dan potongan yang pas di badan untuk menunjang penampilan kesan berkelas.";
        } else {
            return "Kaos casual dengan desain modern dan nyaman digunakan. Terbuat dari bahan berkualitas tinggi yang breathable dan tahan lama. Cocok untuk aktivitas sehari-hari, jalan-jalan, atau acara santai. Desain yang stylish memberikan kesan fresh dan trendy.";
        }
    }

    private void setProductSpecifications() {
        if (itemType != null && itemType.equalsIgnoreCase("formal")) {
            // Specifications untuk formal wear
            tvModel.setText("Blazer Unisex");

            if (itemCategory != null && itemCategory.contains("S")) {
                tvPanjangJaket.setText("S (Medium/sesuai pinggang)");
            } else if (itemCategory != null && itemCategory.contains("M")) {
                tvPanjangJaket.setText("M (Medium/sesuai pinggang)");
            } else if (itemCategory != null && itemCategory.contains("L")) {
                tvPanjangJaket.setText("L (Large/sesuai pinggang)");
            } else {
                tvPanjangJaket.setText("Medium/sesuai pinggang");
            }

            // Determine color from name
            if (itemName != null) {
                if (itemName.toLowerCase().contains("hitam") || itemName.toLowerCase().contains("black")) {
                    tvWarna.setText("Hitam");
                } else if (itemName.toLowerCase().contains("biru") || itemName.toLowerCase().contains("navy")) {
                    tvWarna.setText("Biru Navy");
                } else if (itemName.toLowerCase().contains("merah") || itemName.toLowerCase().contains("red")) {
                    tvWarna.setText("Merah");
                } else {
                    tvWarna.setText("Hitam");
                }
            } else {
                tvWarna.setText("Hitam");
            }

            tvBahan.setText("Premium Cotton Blend");
            tvDetail.setText("Saku Depan, Kancing Kualitas Tinggi");

        } else {
            // Specifications untuk casual wear
            tvModel.setText("Kaos Casual");

            if (itemCategory != null && itemCategory.contains("S")) {
                tvPanjangJaket.setText("S (Fit Body)");
            } else if (itemCategory != null && itemCategory.contains("M")) {
                tvPanjangJaket.setText("M (Regular Fit)");
            } else if (itemCategory != null && itemCategory.contains("L")) {
                tvPanjangJaket.setText("L (Loose Fit)");
            } else {
                tvPanjangJaket.setText("Regular Fit");
            }

            // Determine color from name
            if (itemName != null) {
                if (itemName.toLowerCase().contains("hitam") || itemName.toLowerCase().contains("black")) {
                    tvWarna.setText("Hitam");
                } else if (itemName.toLowerCase().contains("merah") || itemName.toLowerCase().contains("red")) {
                    tvWarna.setText("Merah");
                } else if (itemName.toLowerCase().contains("kuning") || itemName.toLowerCase().contains("yellow")) {
                    tvWarna.setText("Kuning");
                } else {
                    tvWarna.setText("Sesuai Gambar");
                }
            } else {
                tvWarna.setText("Sesuai Gambar");
            }

            tvBahan.setText("Cotton Combed 30s");
            tvDetail.setText("Sablon Premium, Jahitan Rapi");
        }
    }

    private void showRentConfirmation() {
        double totalHarga = hargaPerHari * jumlahHari;

        String message = "Apakah Anda yakin ingin menyewa item ini?\n\n" +
                "Item: " + itemName + "\n" +
                "Durasi: " + jumlahHari + " hari\n" +
                "Harga per hari: " + formatRupiah(hargaPerHari) + "\n" +
                "Total harga: " + formatRupiah(totalHarga) + "\n" +
                "Kode: " + itemCode;

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Sewa")
                .setMessage(message)
                .setPositiveButton("Ya, Sewa", (dialog, which) -> processRent())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void processRent() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        btnRent.setEnabled(false);

        double totalHarga = hargaPerHari * jumlahHari;

        // Update status item menjadi "Disewa" di Firestore dengan informasi sewa
        if (db != null && itemId != null) {
            DocumentReference itemRef = db.collection("clothing_items").document(itemId);

            // Create rental info
            Map<String, Object> rentalInfo = new HashMap<>();
            rentalInfo.put("status", "Disewa");
            rentalInfo.put("rentalDuration", jumlahHari);
            rentalInfo.put("pricePerDay", hargaPerHari);
            rentalInfo.put("totalPrice", totalHarga);
            rentalInfo.put("rentalDate", System.currentTimeMillis());
            rentalInfo.put("rentalId", generateRentalId());

            itemRef.update(rentalInfo)
                    .addOnSuccessListener(aVoid -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        Log.d("DetailPakaian", "✅ Item successfully rented: " + itemName + " for " + jumlahHari + " days");

                        // Show success message dengan detail lengkap
                        String successMessage = "Item " + itemName + " berhasil disewa!\n\n" +
                                "Durasi: " + jumlahHari + " hari\n" +
                                "Total biaya: " + formatRupiah(totalHarga) + "\n" +
                                "Kode booking: " + itemCode + "\n\n" +
                                "Silakan hubungi admin untuk proses selanjutnya.";

                        new AlertDialog.Builder(this)
                                .setTitle("Sewa Berhasil!")
                                .setMessage(successMessage)
                                .setPositiveButton("OK", (dialog, which) -> {
                                    // Kembali ke halaman sebelumnya
                                    finish();
                                })
                                .setCancelable(false)
                                .show();

                    })
                    .addOnFailureListener(e -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        btnRent.setEnabled(true);

                        Log.e("DetailPakaian", "❌ Failed to rent item: " + itemName, e);
                        Toast.makeText(this, "Gagal menyewa item: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            btnRent.setEnabled(true);
            Toast.makeText(this, "Error: Data item tidak lengkap", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateRentalId() {
        // Generate simple rental ID with timestamp
        return "RENT_" + System.currentTimeMillis();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}