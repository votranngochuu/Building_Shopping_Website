package dao;

import entities.Account;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Account entity
 */
public class AccountDAO {
    
    /**
     * Login authentication
     */
    public Account login(String userName, String password) throws SQLException {
        String sql = "SELECT * FROM Account WHERE UserName = ? AND Password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userName);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractAccount(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Get all accounts
     */
    public List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Account ORDER BY Type, FullName";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(extractAccount(rs));
            }
        }
        return accounts;
    }
    
    /**
     * Get account by ID
     */
    public Account getAccountByID(String accountID) throws SQLException {
        String sql = "SELECT * FROM Account WHERE AccountID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, accountID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractAccount(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Create new account
     */
    public boolean createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO Account (AccountID, UserName, Password, FullName, Type) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, account.getAccountID());
            ps.setString(2, account.getUserName());
            ps.setString(3, account.getPassword());
            ps.setString(4, account.getFullName());
            ps.setInt(5, account.getType());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Update account
     */
    public boolean updateAccount(Account account) throws SQLException {
        String sql = "UPDATE Account SET UserName = ?, Password = ?, FullName = ?, Type = ? WHERE AccountID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, account.getUserName());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getFullName());
            ps.setInt(4, account.getType());
            ps.setString(5, account.getAccountID());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete account
     */
    public boolean deleteAccount(String accountID) throws SQLException {
        String sql = "DELETE FROM Account WHERE AccountID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, accountID);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Check if username exists
     */
    public boolean isUserNameExists(String userName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Account WHERE UserName = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userName);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Generate new Account ID
     */
    public String generateAccountID() throws SQLException {
        String sql = "SELECT MAX(AccountID) FROM Account WHERE AccountID LIKE 'ACC%'";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String maxID = rs.getString(1);
                if (maxID != null) {
                    int num = Integer.parseInt(maxID.substring(3)) + 1;
                    return String.format("ACC%03d", num);
                }
            }
        }
        return "ACC001";
    }
    
    /**
     * Extract Account from ResultSet
     */
    private Account extractAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountID(rs.getString("AccountID"));
        account.setUserName(rs.getString("UserName"));
        account.setPassword(rs.getString("Password"));
        account.setFullName(rs.getString("FullName"));
        account.setType(rs.getInt("Type"));
        return account;
    }
}
