package com.batch;

public class Customer {

    private String customerCode;
    private String customerName;
    private String customerType;
    private double creditLimit;

    public Customer(String code, String name, String type, double limit) {
        this.customerCode = code;
        this.customerName = name;
        this.customerType = type;
        this.creditLimit = limit;
    }

    public String getCustomerCode() { return customerCode; }
    public String getCustomerName() { return customerName; }
    public String getCustomerType() { return customerType; }
    public double getCreditLimit() { return creditLimit; }
}
