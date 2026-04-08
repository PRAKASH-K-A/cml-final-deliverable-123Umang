package com.lab.fix;

public class Order {

    private String orderId;
    private String clOrdID;
    private String customerCode;
    private String symbol;
    private char side;
    private double price;
    private int quantity;

    public Order(String orderId, String clOrdID, String customerCode,
                 String symbol, char side, double price, int quantity) {
        this.orderId = orderId;
        this.clOrdID = clOrdID;
        this.customerCode = customerCode;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    public String getOrderId() { return orderId; }
    public String getClOrdID() { return clOrdID; }
    public String getCustomerCode() { return customerCode; }
    public String getSymbol() { return symbol; }
    public char getSide() { return side; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}