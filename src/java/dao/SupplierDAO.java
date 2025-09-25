package dao;

import entities.Supplier;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Supplier entity
 */
public class SupplierDAO {
    
    /**
     * Get all suppliers
     */
    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers ORDER BY CompanyName";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(extractSupplier(rs));
            }
        }
        return suppliers;
    }
    
    /**
     * Get supplier by ID
     */
    public Supplier getSupplierByID(int supplierID) throws SQLException {
        String sql = "SELECT * FROM Suppliers WHERE SupplierID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, supplierID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractSupplier(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create new supplier
     */
    public boolean createSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Suppliers (CompanyName, Address, Phone) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, supplier.getCompanyName());
            ps.setString(2, supplier.getAddress());
            ps.setString(3, supplier.getPhone());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Update supplier
     */
    public boolean updateSupplier(Supplier supplier) throws SQLException {
        String sql = "UPDATE Suppliers SET CompanyName = ?, Address = ?, Phone = ? WHERE SupplierID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, supplier.getCompanyName());
            ps.setString(2, supplier.getAddress());
            ps.setString(3, supplier.getPhone());
            ps.setInt(4, supplier.getSupplierID());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete supplier
     */
    public boolean deleteSupplier(int supplierID) throws SQLException {
        // Check if supplier has products
        String checkSql = "SELECT COUNT(*) FROM Products WHERE SupplierID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            
            ps.setInt(1, supplierID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Supplier has products, cannot delete
                    return false;
                }
            }
        }
        
        // Delete supplier
        String sql = "DELETE FROM Suppliers WHERE SupplierID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, supplierID);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Extract Supplier from ResultSet
     */
    private Supplier extractSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierID(rs.getInt("SupplierID"));
        supplier.setCompanyName(rs.getString("CompanyName"));
        supplier.setAddress(rs.getString("Address"));
        supplier.setPhone(rs.getString("Phone"));
        return supplier;
    }
}
