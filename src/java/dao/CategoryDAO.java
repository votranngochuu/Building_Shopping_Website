package dao;

import entities.Category;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Category entity
 */
public class CategoryDAO {
    
    /**
     * Get all categories
     */
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories ORDER BY CategoryName";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(extractCategory(rs));
            }
        }
        return categories;
    }
    
    /**
     * Get category by ID
     */
    public Category getCategoryByID(int categoryID) throws SQLException {
        String sql = "SELECT * FROM Categories WHERE CategoryID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCategory(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create new category
     */
    public boolean createCategory(Category category) throws SQLException {
        String sql = "INSERT INTO Categories (CategoryName, Description) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Update category
     */
    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE Categories SET CategoryName = ?, Description = ? WHERE CategoryID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryID());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete category
     */
    public boolean deleteCategory(int categoryID) throws SQLException {
        // Check if category has products
        String checkSql = "SELECT COUNT(*) FROM Products WHERE CategoryID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            
            ps.setInt(1, categoryID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Category has products, cannot delete
                    return false;
                }
            }
        }
        
        // Delete category
        String sql = "DELETE FROM Categories WHERE CategoryID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, categoryID);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Extract Category from ResultSet
     */
    private Category extractCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryID(rs.getInt("CategoryID"));
        category.setCategoryName(rs.getString("CategoryName"));
        category.setDescription(rs.getString("Description"));
        return category;
    }
}
