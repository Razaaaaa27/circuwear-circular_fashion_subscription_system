package com.smallacademy.userroles;

public class ClothingItem {
    private String id;
    private String name;
    private String category;
    private String price;
    private String code;
    private String status;
    private String type;
    private String imageUrl;
    private int imageResId;
    private boolean isActive;
    private long createdAt;

    // Default constructor (required for Firestore)
    public ClothingItem() {
    }

    // Constructor dengan semua parameter
    public ClothingItem(String id, String name, String category, String price, String code,
                        String status, String type, String imageUrl, int imageResId,
                        boolean isActive, long createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.code = code;
        this.status = status;
        this.type = type;
        this.imageUrl = imageUrl;
        this.imageResId = imageResId;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters dan Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Method untuk debugging
    @Override
    public String toString() {
        return "ClothingItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price='" + price + '\'' +
                ", code='" + code + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageResId=" + imageResId +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}