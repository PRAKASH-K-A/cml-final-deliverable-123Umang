package com.batch;

public class Security {

    private String symbol;
    private String securityType;
    private String description;
    private String underlying;
    private int lotSize;

    // Constructor
    public Security(String symbol, String securityType,
                    String description, String underlying, int lotSize) {
        this.symbol = symbol;
        this.securityType = securityType;
        this.description = description;
        this.underlying = underlying;
        this.lotSize = lotSize;
    }

    // Getters
    public String getSymbol() { return symbol; }
    public String getSecurityType() { return securityType; }
    public String getDescription() { return description; }
    public String getUnderlying() { return underlying; }
    public int getLotSize() { return lotSize; }

    // Setters
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setSecurityType(String securityType) { this.securityType = securityType; }
    public void setDescription(String description) { this.description = description; }
    public void setUnderlying(String underlying) { this.underlying = underlying; }
    public void setLotSize(int lotSize) { this.lotSize = lotSize; }
}
