package com.example.spendwise;

public class TransactionItem {
    public String category;
    public float amount;
    public String date;
    public String description;

    public TransactionItem(String category, float amount, String date, String description) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }
}
