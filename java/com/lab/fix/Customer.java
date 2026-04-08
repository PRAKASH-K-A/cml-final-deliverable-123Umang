package com.lab.fix;

public class Customer {

    private String customerCode;
    private String customerName;
    private String customerType;
    private double creditLimit;

    // Constructor
    public Customer(String customerCode, String customerName,
                    String customerType, double creditLimit) {

        this.customerCode = customerCode;
        this.customerName = customerName;
        this.customerType = customerType;
        this.creditLimit = creditLimit;
    }

    // Getters
    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    // Setters
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }
}
