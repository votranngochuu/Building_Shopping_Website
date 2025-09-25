package dao;

import entities.Customer;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customer entity
 */
public class CustomerDAO {
    
    /**
     * Customer login
     */
    public Customer login(String customerID, String password) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE CustomerID = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, customerID);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCustomer(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers ORDER BY ContactName";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(extractCustomer(rs));
            }
        }
        return customers;
    }
    
    /**
     * Get customer by ID
     */
    public Customer getCustomerByID(String customerID) throws SQLException {
        String sql = "SELECT * FROM Customers WHERE CustomerID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, customerID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCustomer(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create new customer
     */
    public boolean createCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customers (CustomerID, Password, ContactName, Address, Phone) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, customer.getCustomerID());
            ps.setString(2, customer.getPassword());
            ps.setString(3, customer.getContactName());
            ps.setString(4, customer.getAddress());
            ps.setString(5, customer.getPhone());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Update customer
     */
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE Customers SET Password = ?, ContactName = ?, Address = ?, Phone = ? WHERE CustomerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, customer.getPassword());
            ps.setString(2, customer.getContactName());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getCustomerID());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete customer
     */
    public boolean deleteCustomer(String customerID) throws SQLException {
        // Check if customer has orders
        String checkSql = "SELECT COUNT(*) FROM Orders WHERE CustomerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            
            ps.setString(1, customerID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Customer has orders, cannot delete
                    return false;
                }
            }
        }
        
        // Delete customer
        String sql = "DELETE FROM Customers WHERE CustomerID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, customerID);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Generate new Customer ID
     */
    public String generateCustomerID() throws SQLException {
        String sql = "SELECT MAX(CustomerID) FROM Customers WHERE CustomerID LIKE 'CUST%'";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String maxID = rs.getString(1);
                if (maxID != null) {
                    int num = Integer.parseInt(maxID.substring(4)) + 1;
                    return String.format("CUST%03d", num);
                }
            }
        }
        return "CUST001";
    }
    
    /**
     * Extract Customer from ResultSet
     */
    private Customer extractCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerID(rs.getString("CustomerID"));
        customer.setPassword(rs.getString("Password"));
        customer.setContactName(rs.getString("ContactName"));
        customer.setAddress(rs.getString("Address"));
        customer.setPhone(rs.getString("Phone"));
        return customer;
    }
}
