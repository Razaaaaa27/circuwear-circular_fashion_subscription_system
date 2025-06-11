package com.smallacademy.userroles;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.smallacademy.userroles.R;

import java.util.List;

public class ClothingItemAdapter extends RecyclerView.Adapter<ClothingItemAdapter.ItemViewHolder> {

    private List<ClothingItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ClothingItem item);
    }

    public ClothingItemAdapter(List<ClothingItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clothing, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ClothingItem item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivClothingImage;
        private TextView tvItemName, tvItemCategory, tvItemPrice, tvItemCode, tvItemStatus;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivClothingImage = itemView.findViewById(R.id.ivClothingImage);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemCategory = itemView.findViewById(R.id.tvItemCategory);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemCode = itemView.findViewById(R.id.tvItemCode);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(itemList.get(position));
                }
            });
        }

        public void bind(ClothingItem item) {
            tvItemName.setText(item.getName());
            tvItemCategory.setText(item.getCategory());
            tvItemPrice.setText(item.getPrice());
            tvItemCode.setText(item.getCode());
            tvItemStatus.setText(item.getStatus());

            // Debug log untuk melihat imageResId
            Log.d("ClothingItemAdapter", "Item: " + item.getName() + ", ImageResId: " + item.getImageResId());

            // Set status color and background
            int statusColor;
            int backgroundColor;
            switch (item.getStatus().toLowerCase()) {
                case "tersedia":
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.white);
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark);
                    break;
                case "disewa":
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.white);
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_orange_dark);
                    break;
                case "perawatan":
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.white);
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark);
                    break;
                default:
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.black);
                    backgroundColor = ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray);
                    break;
            }
            tvItemStatus.setTextColor(statusColor);
            tvItemStatus.setBackgroundColor(backgroundColor);

            // Set image - HANYA menggunakan imageResId dari database, tidak ada placeholder
            if (item.getImageResId() != 0) {
                try {
                    ivClothingImage.setImageResource(item.getImageResId());
                    Log.d("ClothingItemAdapter", "✅ Berhasil set image: " + item.getImageResId());
                } catch (Exception e) {
                    Log.e("ClothingItemAdapter", "❌ Error loading image: " + item.getImageResId(), e);
                    // Jika error loading, gunakan default image (baju1)
                    ivClothingImage.setImageResource(R.drawable.baju1);
                }
            } else {
                Log.w("ClothingItemAdapter", "⚠️ ImageResId = 0, using default for: " + item.getName());
                // Jika tidak ada imageResId, gunakan default image
                ivClothingImage.setImageResource(R.drawable.baju1);
            }
        }
    }
}