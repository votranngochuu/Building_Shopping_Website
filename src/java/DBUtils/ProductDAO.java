package DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Product Data Access Object - Enhanced for PizzaStore Shopping Website
 */
public class ProductDAO {
    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());
    
    // Database connection settings
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=PizzaStore;trustServerCertificate=true;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "12345";
    
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            LOGGER.info("SQL Server JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load SQL Server JDBC Driver", e);
        }
    }
    
    /**
     * Get database connection
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Get all products with enhanced information
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, p.SupplierID, p.CategoryID, " +
                    "p.QuantityPerUnit, p.UnitPrice, p.UnitsInStock, p.Discontinued, p.ProductImage, " +
                    "c.CategoryName, s.CompanyName as SupplierName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "ORDER BY p.ProductName";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                products.add(product);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} products from database", products.size());
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all products", e);
        }
        
        return products;
    }
    
    /**
     * Get product by ID
     */
    public Product getProductById(int productID) {
        if (productID <= 0) {
            LOGGER.log(Level.WARNING, "Invalid product ID: {0}", productID);
            return null;
        }
        
        String sql = "SELECT p.ProductID, p.ProductName, p.SupplierID, p.CategoryID, " +
                    "p.QuantityPerUnit, p.UnitPrice, p.UnitsInStock, p.Discontinued, p.ProductImage, " +
                    "c.CategoryName, s.CompanyName as SupplierName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE p.ProductID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Product product = createProductFromResultSet(rs);
                    LOGGER.log(Level.INFO, "Retrieved product: {0}", product.getProductName());
                    return product;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving product with ID: " + productID, e);
        }
        
        return null;
    }
    
    /**
     * Search products by name (case insensitive)
     */
    public List<Product> searchProductsByName(String productName) {
        List<Product> products = new ArrayList<>();
        
        if (productName == null || productName.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Empty product name provided for search");
            return products;
        }
        
        String sql = "SELECT p.ProductID, p.ProductName, p.SupplierID, p.CategoryID, " +
                    "p.QuantityPerUnit, p.UnitPrice, p.UnitsInStock, p.Discontinued, p.ProductImage, " +
                    "c.CategoryName, s.CompanyName as SupplierName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE LOWER(p.ProductName) LIKE LOWER(?) " +
                    "ORDER BY p.ProductName";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + productName.trim() + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = createProductFromResultSet(rs);
                    products.add(product);
                }
            }
            
            LOGGER.log(Level.INFO, "Found {0} products matching search term: {1}", 
                      new Object[]{products.size(), productName});
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching products by name: " + productName, e);
        }
        
        return products;
    }
    
    /**
     * Search products by price range
     */
    public List<Product> searchProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = new ArrayList<>();
        
        if (minPrice == null || maxPrice == null || 
            minPrice.compareTo(BigDecimal.ZERO) < 0 || 
            maxPrice.compareTo(minPrice) < 0) {
            LOGGER.log(Level.WARNING, "Invalid price range: {0} - {1}", 
                      new Object[]{minPrice, maxPrice});
            return products;
        }
        
        String sql = "SELECT p.ProductID, p.ProductName, p.SupplierID, p.CategoryID, " +
                    "p.QuantityPerUnit, p.UnitPrice, p.UnitsInStock, p.Discontinued, p.ProductImage, " +
                    "c.CategoryName, s.CompanyName as SupplierName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE p.UnitPrice BETWEEN ? AND ? " +
                    "ORDER BY p.UnitPrice";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, minPrice);
            pstmt.setBigDecimal(2, maxPrice);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = createProductFromResultSet(rs);
                    products.add(product);
                }
            }
            
            LOGGER.log(Level.INFO, "Found {0} products in price range: {1} - {2}", 
                      new Object[]{products.size(), minPrice, maxPrice});
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching products by price range", e);
        }
        
        return products;
    }
    
    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(int categoryID) {
        List<Product> products = new ArrayList<>();
        
        if (categoryID <= 0) {
            LOGGER.log(Level.WARNING, "Invalid category ID: {0}", categoryID);
            return products;
        }
        
        String sql = "SELECT p.ProductID, p.ProductName, p.SupplierID, p.CategoryID, " +
                    "p.QuantityPerUnit, p.UnitPrice, p.UnitsInStock, p.Discontinued, p.ProductImage, " +
                    "c.CategoryName, s.CompanyName as SupplierName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE p.CategoryID = ? " +
                    "ORDER BY p.ProductName";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = createProductFromResultSet(rs);
                    products.add(product);
                }
            }
            
            LOGGER.log(Level.INFO, "Found {0} products in category: {1}", 
                      new Object[]{products.size(), categoryID});
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving products by category: " + categoryID, e);
        }
        
        return products;
    }
    
    /**
     * Get available products only
     */
    public List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, p.SupplierID, p.CategoryID, " +
                    "p.QuantityPerUnit, p.UnitPrice, p.UnitsInStock, p.Discontinued, p.ProductImage, " +
                    "c.CategoryName, s.CompanyName as SupplierName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE p.Discontinued = 0 AND p.UnitsInStock > 0 " +
                    "ORDER BY p.ProductName";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                products.add(product);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} available products", products.size());
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving available products", e);
        }
        
        return products;
    }
    
    /**
     * Insert new product
     */
    public boolean insertProduct(Product product) {
        if (product == null || !product.isValid()) {
            LOGGER.log(Level.WARNING, "Invalid product data provided for insertion");
            return false;
        }
        
        String sql = "INSERT INTO Products (ProductName, SupplierID, CategoryID, QuantityPerUnit, " +
                    "UnitPrice, UnitsInStock, Discontinued, ProductImage) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getSupplierID());
            pstmt.setInt(3, product.getCategoryID());
            pstmt.setString(4, product.getQuantityPerUnit());
            pstmt.setBigDecimal(5, product.getUnitPrice());
            pstmt.setInt(6, product.getUnitsInStock());
            pstmt.setBoolean(7, product.isDiscontinued());
            pstmt.setString(8, product.getProductImage());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                LOGGER.log(Level.INFO, "Product inserted successfully: {0}", product.getProductName());
                return true;
            } else {
                conn.rollback();
                LOGGER.log(Level.WARNING, "Failed to insert product: {0}", product.getProductName());
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting product: " + product.getProductName(), e);
        }
        
        return false;
    }
    
    /**
     * Update product
     */
    public boolean updateProduct(Product product) {
        if (product == null || product.getProductID() <= 0 || !product.isValid()) {
            LOGGER.log(Level.WARNING, "Invalid product data provided for update");
            return false;
        }
        
        String sql = "UPDATE Products SET ProductName=?, SupplierID=?, CategoryID=?, " +
                    "QuantityPerUnit=?, UnitPrice=?, UnitsInStock=?, Discontinued=?, ProductImage=? " +
                    "WHERE ProductID=?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getSupplierID());
            pstmt.setInt(3, product.getCategoryID());
            pstmt.setString(4, product.getQuantityPerUnit());
            pstmt.setBigDecimal(5, product.getUnitPrice());
            pstmt.setInt(6, product.getUnitsInStock());
            pstmt.setBoolean(7, product.isDiscontinued());
            pstmt.setString(8, product.getProductImage());
            pstmt.setInt(9, product.getProductID());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                LOGGER.log(Level.INFO, "Product updated successfully: {0}", product.getProductName());
                return true;
            } else {
                conn.rollback();
                LOGGER.log(Level.WARNING, "No product found with ID: {0}", product.getProductID());
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product: " + product.getProductName(), e);
        }
        
        return false;
    }
    
    /**
     * Delete product
     */
    public boolean deleteProduct(int productID) {
        if (productID <= 0) {
            LOGGER.log(Level.WARNING, "Invalid product ID for deletion: {0}", productID);
            return false;
        }
        
        String sql = "DELETE FROM Products WHERE ProductID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setInt(1, productID);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                LOGGER.log(Level.INFO, "Product deleted successfully with ID: {0}", productID);
                return true;
            } else {
                conn.rollback();
                LOGGER.log(Level.WARNING, "No product found with ID: {0}", productID);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting product with ID: " + productID, e);
        }
        
        return false;
    }
    
    /**
     * Update product stock
     */
    public boolean updateProductStock(int productID, int newStock) {
        if (productID <= 0 || newStock < 0) {
            LOGGER.log(Level.WARNING, "Invalid parameters for stock update: productID={0}, stock={1}", 
                      new Object[]{productID, newStock});
            return false;
        }
        
        String sql = "UPDATE Products SET UnitsInStock = ? WHERE ProductID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newStock);
            pstmt.setInt(2, productID);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                LOGGER.log(Level.INFO, "Product stock updated: productID={0}, newStock={1}", 
                          new Object[]{productID, newStock});
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product stock", e);
        }
        
        return false;
    }
    
    /**
     * Create Product object from ResultSet
     */
    private Product createProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductID(rs.getInt("ProductID"));
        product.setProductName(rs.getString("ProductName"));
        product.setSupplierID(rs.getInt("SupplierID"));
        product.setCategoryID(rs.getInt("CategoryID"));
        product.setQuantityPerUnit(rs.getString("QuantityPerUnit"));
        product.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        product.setUnitsInStock(rs.getInt("UnitsInStock"));
        product.setDiscontinued(rs.getBoolean("Discontinued"));
        product.setProductImage(rs.getString("ProductImage"));
        product.setCategoryName(rs.getString("CategoryName"));
        product.setSupplierName(rs.getString("SupplierName"));
        return product;
    }
}