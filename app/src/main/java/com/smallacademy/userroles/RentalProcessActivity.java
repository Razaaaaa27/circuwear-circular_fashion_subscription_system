package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RentalProcessActivity extends AppCompatActivity {

    private ImageView btnBack, imgProduct;
    private TextView tvProductName, tvProductPrice;
    private Button btnConfirmRental;

    private String productName;
    private String productPrice;
    private int productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_process);

        initViews();
        setupData();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        imgProduct = findViewById(R.id.img_product);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductPrice = findViewById(R.id.tv_product_price);
        btnConfirmRental = findViewById(R.id.btn_confirm_rental);
    }

    private void setupData() {
        Intent intent = getIntent();
        if (intent != null) {
            productName = intent.getStringExtra("product_name");
            productPrice = intent.getStringExtra("product_price");
            productImage = intent.getIntExtra("product_image", R.drawable.baju02);

            tvProductName.setText(productName);
            tvProductPrice.setText(productPrice);
            imgProduct.setImageResource(productImage);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnConfirmRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRental();
            }
        });
    }

    private void confirmRental() {
        Toast.makeText(this, "Rental berhasil dikonfirmasi!", Toast.LENGTH_LONG).show();

        // Kembali ke halaman utama atau halaman rental history
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}