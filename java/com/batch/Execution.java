 package com.batch;

public class Execution {
	private String orderId;
    private String symbol;
    private char side;
    private int execQty;
    private double execPrice;
    private String ClOrdId;

    public Execution(String orderId, String symbol,
                     char side, int execQty, double execPrice,String ClOrdId) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.execQty = execQty;
        this.execPrice = execPrice;
        this.ClOrdId = ClOrdId;
    } 

    public String getOrderId() { return orderId; }
    public String getSymbol() { return symbol; }
    public char getSide() { return side; }
    public int getExecQty() { return execQty; }
    public double getExecPrice() { return execPrice; }
    public String getClOrdID()

{
    	return ClOrdId;
}
	
}

