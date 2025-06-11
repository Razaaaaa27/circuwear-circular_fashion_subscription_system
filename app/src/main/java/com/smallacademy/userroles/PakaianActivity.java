package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PakaianActivity extends AppCompatActivity {

    private ImageView btnBack, btnMenu, imgProduct;
    private TextView tvProductName, tvProductSubtitle, tvDescription;
    private Button btnLokasiPhoto, btnRent, btnCart;

    // Data produk
    private String productName;
    private String productPrice;
    private String productDescription;
    private int productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pakaian);

        initViews();
        setupData();
        setupClickListeners();
    }

    private void initViews() {
        // Header
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);

        // Product Info
        imgProduct = findViewById(R.id.img_product);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductSubtitle = findViewById(R.id.tv_product_subtitle);
        tvDescription = findViewById(R.id.tv_description);

        // Buttons
        btnLokasiPhoto = findViewById(R.id.btn_lokasi_foto);
        btnRent = findViewById(R.id.btn_rent);
        btnCart = findViewById(R.id.btn_cart);
    }

    private void setupData() {
        // Ambil data dari Intent jika ada
        Intent intent = getIntent();
        if (intent != null) {
            productName = intent.getStringExtra("product_name");
            productPrice = intent.getStringExtra("product_price");
            productDescription = intent.getStringExtra("product_description");
            productImage = intent.getIntExtra("product_image", R.drawable.baju02);
        }

        // Set default data jika tidak ada data dari intent
        if (productName == null) {
            productName = "Black Rose Suit Blazer";
            productPrice = "Jacket - Rp 99.000";
            productDescription = "Blazer eksekutif dengan motif bunga mawar timbul yang elegan. " +
                    "Terbuat dari bahan premium cotton blend, memberikan kenyamanan dan tampilan " +
                    "mewah untuk acara formal maupun semi-formal. Dilengkapi dengan satu kancing " +
                    "yang fungsional dan potongan modern yang pas di badan, menambah kesan berkelas.";
            productImage = R.drawable.baju02;
        }

        // Set data ke views
        tvProductName.setText(productName);
        tvProductSubtitle.setText(productPrice);
        tvDescription.setText(productDescription);
        imgProduct.setImageResource(productImage);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Menu button
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementasi menu options
                showMenuOptions();
            }
        });

        // Product image click
        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka image viewer atau gallery
                openImageViewer();
            }
        });

        // Lokasi Photo button
        btnLokasiPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buka lokasi foto
                openPhotoLocation();
            }
        });

        // Rent button
        btnRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Proses rental
                processRental();
            }
        });

        // Cart button
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tambah ke keranjang
                addToCart();
            }
        });
    }

    private void showMenuOptions() {
        // Implementasi popup menu atau bottom sheet
        Toast.makeText(this, "Menu options", Toast.LENGTH_SHORT).show();

        // Contoh implementasi dengan PopupMenu:
        /*
        PopupMenu popup = new PopupMenu(this, btnMenu);
        popup.getMenuInflater().inflate(R.menu.detail_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_share:
                        shareProduct();
                        return true;
                    case R.id.menu_favorite:
                        toggleFavorite();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
        */
    }

    private void openImageViewer() {
        Toast.makeText(this, "Membuka image viewer", Toast.LENGTH_SHORT).show();
        // Implementasi untuk membuka full screen image viewer
        // Intent intent = new Intent(this, ImageViewerActivity.class);
        // intent.putExtra("image_resource", productImage);
        // startActivity(intent);
    }

    private void openPhotoLocation() {
        Toast.makeText(this, "Membuka lokasi foto", Toast.LENGTH_SHORT).show();
        // Implementasi untuk membuka maps atau lokasi foto
        // Intent intent = new Intent(this, PhotoLocationActivity.class);
        // startActivity(intent);
    }

    private void processRental() {
        Toast.makeText(this, "Memproses rental untuk " + productName, Toast.LENGTH_SHORT).show();

        // Implementasi proses rental
        Intent intent = new Intent(this, RentalProcessActivity.class);
        intent.putExtra("product_name", productName);
        intent.putExtra("product_price", productPrice);
        intent.putExtra("product_image", productImage);
        // startActivity(intent);
    }

    private void addToCart() {
        Toast.makeText(this, productName + " ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();

        // Implementasi tambah ke keranjang
        // Bisa menggunakan SharedPreferences, Room Database, atau server API
        saveToCart();
    }

    private void saveToCart() {
        // Implementasi penyimpanan ke cart
        // Contoh menggunakan SharedPreferences:
        /*
        SharedPreferences prefs = getSharedPreferences("cart", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Simpan data produk ke cart
        String cartItems = prefs.getString("items", "");
        JSONObject item = new JSONObject();
        try {
            item.put("name", productName);
            item.put("price", productPrice);
            item.put("image", productImage);

            JSONArray cartArray;
            if (cartItems.isEmpty()) {
                cartArray = new JSONArray();
            } else {
                cartArray = new JSONArray(cartItems);
            }
            cartArray.put(item);

            editor.putString("items", cartArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    private void shareProduct() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Lihat produk ini!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, productName + " - " + productPrice +
                "\n\n" + productDescription);

        startActivity(Intent.createChooser(shareIntent, "Bagikan produk"));
    }

    private void toggleFavorite() {
        // Implementasi toggle favorite
        Toast.makeText(this, "Toggle favorite", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Gunakan animasi bawaan Android
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    // Method untuk update data produk dari luar
    public void updateProductData(String name, String price, String description, int imageResource) {
        this.productName = name;
        this.productPrice = price;
        this.productDescription = description;
        this.productImage = imageResource;

        setupData();
    }
}