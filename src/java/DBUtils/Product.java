package DBUtils;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

/**
 * Product Entity - Enhanced for PizzaStore Shopping Website
 */
public class Product implements Serializable {
    private int productID;
    private String productName;
    private int supplierID;
    private int categoryID;
    private String quantityPerUnit;
    private BigDecimal unitPrice;
    private int unitsInStock;
    private boolean discontinued;
    private String productImage;
    private Date createdDate;
    private Date modifiedDate;
    
    // Additional fields for display purposes
    private String categoryName;
    private String supplierName;
    
    // Default constructor
    public Product() {
        this.unitPrice = BigDecimal.ZERO;
        this.unitsInStock = 0;
        this.discontinued = false;
    }
    
    // Constructor with essential fields
    public Product(String productName, int supplierID, int categoryID, 
                  String quantityPerUnit, BigDecimal unitPrice) {
        this();
        this.productName = productName;
        this.supplierID = supplierID;
        this.categoryID = categoryID;
        this.quantityPerUnit = quantityPerUnit;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
    }
    
    // Constructor with all fields
    public Product(int productID, String productName, int supplierID, int categoryID,
                  String quantityPerUnit, BigDecimal unitPrice, String productImage) {
        this(productName, supplierID, categoryID, quantityPerUnit, unitPrice);
        this.productID = productID;
        this.productImage = productImage;
    }
    
    // Getters and Setters
    public int getProductID() {
        return productID;
    }
    
    public void setProductID(int productID) {
        this.productID = productID;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName != null ? productName.trim() : null;
    }
    
    public int getSupplierID() {
        return supplierID;
    }
    
    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }
    
    public int getCategoryID() {
        return categoryID;
    }
    
    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }
    
    public String getQuantityPerUnit() {
        return quantityPerUnit;
    }
    
    public void setQuantityPerUnit(String quantityPerUnit) {
        this.quantityPerUnit = quantityPerUnit != null ? quantityPerUnit.trim() : null;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
    }
    
    public String getProductImage() {
        return productImage;
    }
    
    public void setProductImage(String productImage) {
        this.productImage = productImage != null ? productImage.trim() : null;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName != null ? categoryName.trim() : null;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName != null ? supplierName.trim() : null;
    }
    
    public int getUnitsInStock() {
        return unitsInStock;
    }
    
    public void setUnitsInStock(int unitsInStock) {
        this.unitsInStock = unitsInStock;
    }
    
    public boolean isDiscontinued() {
        return discontinued;
    }
    
    public void setDiscontinued(boolean discontinued) {
        this.discontinued = discontinued;
    }
    
    /**
     * Check if product is available for purchase
     */
    public boolean isAvailable() {
        return !discontinued && unitsInStock > 0;
    }
    
    /**
     * Get availability status text
     */
    public String getAvailabilityStatus() {
        if (discontinued) {
            return "Discontinued";
        } else if (unitsInStock <= 0) {
            return "Out of Stock";
        } else if (unitsInStock <= 5) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
    
    /**
     * Validate product data
     */
    public boolean isValid() {
        return productName != null && !productName.trim().isEmpty() &&
               unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) > 0 &&
               supplierID > 0 && categoryID > 0;
    }
    
    /**
     * Get formatted price as string
     */
    public String getFormattedPrice() {
        return unitPrice != null ? String.format("$%.2f", unitPrice) : "$0.00";
    }
    
    /**
     * Check if product has image
     */
    public boolean hasImage() {
        return productImage != null && !productImage.trim().isEmpty();
    }
    
    /**
     * Get product type based on category
     */
    public String getProductType() {
        if (categoryName == null) return "Unknown";
        
        switch (categoryName.toLowerCase()) {
            case "pizza":
            case "pizzas":
                return "Pizza";
            case "beverages":
            case "drinks":
                return "Beverage";
            case "desserts":
                return "Dessert";
            case "appetizers":
                return "Appetizer";
            default:
                return categoryName;
        }
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productID +
                ", productName='" + productName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", unitPrice=" + unitPrice +
                ", unitsInStock=" + unitsInStock +
                ", available=" + isAvailable() +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Product product = (Product) obj;
        return productID == product.productID;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(productID);
    }
}