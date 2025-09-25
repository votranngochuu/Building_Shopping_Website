package entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Order entity class representing the Orders table
 */
public class Order {
    private int orderID;
    private String customerID;
    private Date orderDate;
    private Date requiredDate;
    private Date shippedDate;
    private BigDecimal freight;
    private String shipAddress;
    
    // Additional fields
    private String customerName;
    private List<OrderDetail> orderDetails;
    private BigDecimal totalAmount;
    
    // Constructors
    public Order() {
    }
    
    public Order(int orderID, String customerID, Date orderDate, Date requiredDate, 
                 Date shippedDate, BigDecimal freight, String shipAddress) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.orderDate = orderDate;
        this.requiredDate = requiredDate;
        this.shippedDate = shippedDate;
        this.freight = freight;
        this.shipAddress = shipAddress;
    }
    
    // Getters and Setters
    public int getOrderID() {
        return orderID;
    }
    
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
    
    public String getCustomerID() {
        return customerID;
    }
    
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    
    public Date getRequiredDate() {
        return requiredDate;
    }
    
    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }
    
    public Date getShippedDate() {
        return shippedDate;
    }
    
    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }
    
    public BigDecimal getFreight() {
        return freight;
    }
    
    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }
    
    public String getShipAddress() {
        return shipAddress;
    }
    
    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }
    
    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
    
    public BigDecimal getTotalAmount() {
        if (totalAmount == null && orderDetails != null) {
            totalAmount = BigDecimal.ZERO;
            for (OrderDetail detail : orderDetails) {
                totalAmount = totalAmount.add(detail.getSubTotal());
            }
            if (freight != null) {
                totalAmount = totalAmount.add(freight);
            }
        }
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getStatus() {
        if (shippedDate != null) {
            return "Shipped";
        } else if (requiredDate != null) {
            return "Processing";
        } else {
            return "Pending";
        }
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", customerID='" + customerID + '\'' +
                ", orderDate=" + orderDate +
                ", requiredDate=" + requiredDate +
                ", shippedDate=" + shippedDate +
                ", freight=" + freight +
                ", shipAddress='" + shipAddress + '\'' +
                '}';
    }
}
