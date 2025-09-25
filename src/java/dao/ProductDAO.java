package dao;

import entities.Product;
import utils.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product entity
 */
public class ProductDAO {
    
    /**
     * Get all products
     */
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName, s.CompanyName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "ORDER BY p.ProductName";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                products.add(extractProduct(rs));
            }
        }
        return products;
    }
    
    /**
     * Get product by ID
     */
    public Product getProductByID(int productID) throws SQLException {
        String sql = "SELECT p.*, c.CategoryName, s.CompanyName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE p.ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractProduct(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Search products by name (case insensitive)
     */
    public List<Product> searchByName(String keyword) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName, s.CompanyName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE LOWER(p.ProductName) LIKE LOWER(?) " +
                    "ORDER BY p.ProductName";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(extractProduct(rs));
                }
            }
        }
        return products;
    }
    
    /**
     * Search products by price range
     */
    public List<Product> searchByPrice(BigDecimal minPrice, BigDecimal maxPrice) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName, s.CompanyName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE p.UnitPrice BETWEEN ? AND ? " +
                    "ORDER BY p.UnitPrice";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, minPrice);
            ps.setBigDecimal(2, maxPrice);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(extractProduct(rs));
                }
            }
        }
        return products;
    }
    
    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(int categoryID) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName, s.CompanyName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE p.CategoryID = ? " +
                    "ORDER BY p.ProductName";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(extractProduct(rs));
                }
            }
        }
        return products;
    }
    
    /**
     * Create new product
     */
    public boolean createProduct(Product product) throws SQLException {
        String sql = "INSERT INTO Products (ProductName, SupplierID, CategoryID, QuantityPerUnit, UnitPrice, ProductImage) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getProductName());
            ps.setInt(2, product.getSupplierID());
            ps.setInt(3, product.getCategoryID());
            ps.setString(4, product.getQuantityPerUnit());
            ps.setBigDecimal(5, product.getUnitPrice());
            ps.setString(6, product.getProductImage());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Update product
     */
    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE Products SET ProductName = ?, SupplierID = ?, CategoryID = ?, " +
                    "QuantityPerUnit = ?, UnitPrice = ?, ProductImage = ? " +
                    "WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, product.getProductName());
            ps.setInt(2, product.getSupplierID());
            ps.setInt(3, product.getCategoryID());
            ps.setString(4, product.getQuantityPerUnit());
            ps.setBigDecimal(5, product.getUnitPrice());
            ps.setString(6, product.getProductImage());
            ps.setInt(7, product.getProductID());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete product
     */
    public boolean deleteProduct(int productID) throws SQLException {
        // Check if product is in any orders
        String checkSql = "SELECT COUNT(*) FROM OrderDetails WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            
            ps.setInt(1, productID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Product is in orders, cannot delete
                    return false;
                }
            }
        }
        
        // Delete product
        String sql = "DELETE FROM Products WHERE ProductID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productID);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Extract Product from ResultSet
     */
    private Product extractProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductID(rs.getInt("ProductID"));
        product.setProductName(rs.getString("ProductName"));
        product.setSupplierID(rs.getInt("SupplierID"));
        product.setCategoryID(rs.getInt("CategoryID"));
        product.setQuantityPerUnit(rs.getString("QuantityPerUnit"));
        product.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        product.setProductImage(rs.getString("ProductImage"));
        
        // Additional fields if available
        try {
            product.setCategoryName(rs.getString("CategoryName"));
        } catch (SQLException e) {
            // Column may not exist in some queries
        }
        
        try {
            product.setSupplierName(rs.getString("CompanyName"));
        } catch (SQLException e) {
            // Column may not exist in some queries
        }
        
        return product;
    }
}
