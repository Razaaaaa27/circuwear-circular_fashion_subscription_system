package com.smallacademy.userroles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Import R class dari package yang benar
import com.smallacademy.userroles.R;
// Import Reward class dari package yang benar
import com.example.clothrental.models.Reward;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private List<Reward> rewardList;
    private OnRewardClickListener listener;

    public interface OnRewardClickListener {
        void onRewardClick(Reward reward);
    }

    public RewardAdapter(List<Reward> rewardList, OnRewardClickListener listener) {
        this.rewardList = rewardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewardList.get(position);
        holder.bind(reward);
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    class RewardViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivRewardIcon;
        private TextView tvRewardTitle, tvRewardDescription, tvRewardPoints, tvRewardValue;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRewardIcon = itemView.findViewById(R.id.ivRewardIcon);
            tvRewardTitle = itemView.findViewById(R.id.tvRewardTitle);
            tvRewardDescription = itemView.findViewById(R.id.tvRewardDescription);
            tvRewardPoints = itemView.findViewById(R.id.tvRewardPoints);
            tvRewardValue = itemView.findViewById(R.id.tvRewardValue);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRewardClick(rewardList.get(position));
                }
            });
        }

        public void bind(Reward reward) {
            tvRewardTitle.setText(reward.getTitle());
            tvRewardDescription.setText(reward.getDescription());
            tvRewardPoints.setText(reward.getPointsRequired() + " Poin");
            tvRewardValue.setText(reward.getRewardValue());

            // Set icon based on reward type
            switch (reward.getType()) {
                case "discount":
                    ivRewardIcon.setImageResource(R.drawable.ic_discount);
                    break;
                case "accessory":
                    ivRewardIcon.setImageResource(R.drawable.ic_gift);
                    break;
                case "delivery":
                    ivRewardIcon.setImageResource(R.drawable.ic_delivery);
                    break;
                case "vip":
                    ivRewardIcon.setImageResource(R.drawable.ic_vip);
                    break;
                default:
                    ivRewardIcon.setImageResource(R.drawable.ic_reward);
                    break;
            }
        }
    }
}