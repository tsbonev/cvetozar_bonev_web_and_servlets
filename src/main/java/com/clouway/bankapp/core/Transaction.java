package com.clouway.bankapp.core;

import java.sql.Date;

public class Transaction {

    private int id;
    private Operation operation;
    private int userId;
    private Date date;
    private double amount;
    private String username;

    public Transaction(){}

    public Transaction(int id, Operation operation, String username, Date date, double amount) {
        this.id = id;
        this.operation = operation;
        this.username = username;
        this.date = date;
        this.amount = amount;
    }

    public Transaction(Operation operation, int userId, Date date, double amount) {
        this.operation = operation;
        this.userId = userId;
        this.date = date;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountFormatted() {return String.format("%.2f", this.getAmount());}

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
