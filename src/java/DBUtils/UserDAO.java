package DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User Data Access Object - Enhanced for PizzaStore Shopping Website
 */
public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    
    // Database connection settings
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=PizzaStore;trustServerCertificate=true;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "123456";
    
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
     * Authenticate user login
     */
    public User authenticateUser(String userID, String password) {
        if (userID == null || userID.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Empty userID or password provided");
            return null;
        }
        
        String sql = "SELECT userID, fullName, roleID, password FROM Account WHERE userID = ? AND password = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userID.trim());
            pstmt.setString(2, password.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = createUserFromResultSet(rs);
                    LOGGER.log(Level.INFO, "User authenticated successfully: {0}", userID);
                    return user;
                } else {
                    LOGGER.log(Level.WARNING, "Authentication failed for user: {0}", userID);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error authenticating user: " + userID, e);
        }
        
        return null;
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT userID, fullName, roleID, password FROM Account ORDER BY fullName";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                user.clearSensitiveData(); // Remove password for security
                users.add(user);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} users from database", users.size());
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all users", e);
        }
        
        return users;
    }
    
    /**
     * Get user by ID
     */
    public User getUserByID(String userID) {
        if (userID == null || userID.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Empty userID provided");
            return null;
        }
        
        String sql = "SELECT userID, fullName, roleID, password FROM Account WHERE userID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userID.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = createUserFromResultSet(rs);
                    LOGGER.log(Level.INFO, "Retrieved user: {0}", userID);
                    return user;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user with ID: " + userID, e);
        }
        
        return null;
    }
    
    /**
     * Check if user exists
     */
    public boolean userExists(String userID) {
        if (userID == null || userID.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM Account WHERE userID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userID.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking user existence: " + userID, e);
        }
        
        return false;
    }
    
    /**
     * Insert new user
     */
    public boolean insertUser(User user) {
        if (user == null || !user.isValid()) {
            LOGGER.log(Level.WARNING, "Invalid user data provided for insertion");
            return false;
        }
        
        String sql = "INSERT INTO Account (userID, fullName, roleID, password) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setString(1, user.getUserID());
            pstmt.setString(2, user.getFullName());
            pstmt.setString(3, user.getRoleID());
            pstmt.setString(4, user.getPassword());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                LOGGER.log(Level.INFO, "User inserted successfully: {0}", user.getUserID());
                return true;
            } else {
                conn.rollback();
                LOGGER.log(Level.WARNING, "Failed to insert user: {0}", user.getUserID());
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting user: " + user.getUserID(), e);
        }
        
        return false;
    }
    
    /**
     * Update user
     */
    public boolean updateUser(User user) {
        if (user == null || !user.isValid()) {
            LOGGER.log(Level.WARNING, "Invalid user data provided for update");
            return false;
        }
        
        String sql = "UPDATE Account SET fullName = ?, roleID = ?, password = ? WHERE userID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getRoleID());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getUserID());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                LOGGER.log(Level.INFO, "User updated successfully: {0}", user.getUserID());
                return true;
            } else {
                conn.rollback();
                LOGGER.log(Level.WARNING, "No user found with ID: {0}", user.getUserID());
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user: " + user.getUserID(), e);
        }
        
        return false;
    }
    
    /**
     * Delete user
     */
    public boolean deleteUser(String userID) {
        if (userID == null || userID.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Empty userID provided for deletion");
            return false;
        }
        
        String sql = "DELETE FROM Account WHERE userID = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setString(1, userID.trim());
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                conn.commit();
                LOGGER.log(Level.INFO, "User deleted successfully: {0}", userID);
                return true;
            } else {
                conn.rollback();
                LOGGER.log(Level.WARNING, "No user found with ID: {0}", userID);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user: " + userID, e);
        }
        
        return false;
    }
    
    /**
     * Create User object from ResultSet
     */
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getString("userID"));
        user.setFullName(rs.getString("fullName"));
        user.setRoleID(rs.getString("roleID"));
        user.setPassword(rs.getString("password"));
        
        // Set type based on roleID for shopping website
        String roleID = rs.getString("roleID");
        if ("AD".equals(roleID) || "ST".equals(roleID)) {
            user.setType(User.TYPE_STAFF);
        } else {
            user.setType(User.TYPE_CUSTOMER);
        }
        
        return user;
    }
}