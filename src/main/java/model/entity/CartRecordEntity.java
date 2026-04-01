package model.entity;

public class CartRecordEntity {
    private int id;
    private int totalItems;
    private double totalCost;
    private String language;
    private java.sql.Timestamp createdAt;

    // Constructors
    public CartRecordEntity() {}

    public CartRecordEntity(int totalItems, double totalCost, String language) {
        this.totalItems = totalItems;
        this.totalCost = totalCost;
        this.language = language;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
}

