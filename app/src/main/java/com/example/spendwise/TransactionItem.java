package com.example.spendwise;

public class TransactionItem {
    private String category;
    private float amount;
    private String date;
    private String description;

    public TransactionItem(String category, float amount, String date, String description) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public float getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
