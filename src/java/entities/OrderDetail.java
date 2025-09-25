package entities;

import java.math.BigDecimal;

/**
 * OrderDetail entity class representing the OrderDetails table
 */
public class OrderDetail {
    private int orderID;
    private int productID;
    private BigDecimal unitPrice;
    private int quantity;
    
    // Additional fields for display
    private String productName;
    private BigDecimal subTotal;
    
    // Constructors
    public OrderDetail() {
    }
    
    public OrderDetail(int orderID, int productID, BigDecimal unitPrice, int quantity) {
        this.orderID = orderID;
        this.productID = productID;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public int getOrderID() {
        return orderID;
    }
    
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public BigDecimal getSubTotal() {
        if (subTotal == null && unitPrice != null) {
            subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return subTotal;
    }
    
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
    
    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderID=" + orderID +
                ", productID=" + productID +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                '}';
    }
}
