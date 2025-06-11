package com.example.clothrental.models;

public class Reward {
    private String id;
    private String title;
    private String description;
    private int pointsRequired;
    private String rewardValue;
    private String type;
    private boolean isActive;
    private int timesUsed;
    private long createdAt;

    public Reward() {}

    public Reward(String title, String description, int pointsRequired, String rewardValue,
                  String type, boolean isActive, int timesUsed, long createdAt) {
        this.title = title;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.rewardValue = rewardValue;
        this.type = type;
        this.isActive = isActive;
        this.timesUsed = timesUsed;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPointsRequired() { return pointsRequired; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }

    public String getRewardValue() { return rewardValue; }
    public void setRewardValue(String rewardValue) { this.rewardValue = rewardValue; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getTimesUsed() { return timesUsed; }
    public void setTimesUsed(int timesUsed) { this.timesUsed = timesUsed; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
