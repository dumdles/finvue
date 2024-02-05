package com.sp.finvue;

public class Transaction {
    private String name;
    private String category;
    private String location;
    private double cost;
    private String date;
    private String id;
    private String mop;
    private String remarks;
    private String submissionTime;
    private String userId;
    private String transactionId;

    // Constructor to initialize the transaction with its attributes
    public Transaction(String userId, String transactionId, String category, double cost, String date, String location, String mop, String name, String remarks, String submissionTime) {
        this.userId = userId;
        this.transactionId = transactionId;
        this.category = category;
        this.cost = cost;
        this.date = date;
        this.location = location;
        this.mop = mop;
        this.remarks = remarks;
        this.submissionTime = submissionTime;
    }


    // Getter methods to access the attributes
    public String getUserId() {
        return this.userId;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.category;
    }

    public String getLocation() {
        return this.location;
    }

    public String getDate() {
        return this.date;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getCost() {
        return this.cost;
    }

    public String getMop() {
        return this.mop;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public String getSubmissionTime() {
        return this.submissionTime;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(String cost) {
        this.cost = Double.parseDouble(cost);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
