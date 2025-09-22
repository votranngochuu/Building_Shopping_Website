package DBUtils;

/**
 * User class representing a user entity in the shopping website system.
 * Contains user information, validation methods, and utility functions.
 */
public class User {
    
    // User type constants
    public static final String TYPE_STAFF = "STAFF";
    public static final String TYPE_CUSTOMER = "CUSTOMER";
    
    // Role ID constants for convenience
    public static final String ROLE_ADMIN = "AD";
    public static final String ROLE_STAFF = "ST"; 
    public static final String ROLE_USER = "US";
    
    // Instance variables
    private String userID;
    private String fullName;
    private String roleID;
    private String password;
    private String type;
    
    /**
     * Default constructor
     */
    public User() {
        this.type = TYPE_CUSTOMER; // Default type
    }
    
    /**
     * Parameterized constructor
     * @param userID User ID
     * @param fullName Full name of the user
     * @param roleID Role ID (AD, ST, US)
     * @param password User password
     */
    public User(String userID, String fullName, String roleID, String password) {
        this.userID = userID;
        this.fullName = fullName;
        this.roleID = roleID;
        this.password = password;
        
        // Set type based on roleID
        if ("AD".equals(roleID) || "ST".equals(roleID)) {
            this.type = TYPE_STAFF;
        } else {
            this.type = TYPE_CUSTOMER;
        }
    }
    
    // Getter methods
    public String getUserID() {
        return userID;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public String getRoleID() {
        return roleID;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getType() {
        return type;
    }
    
    // Setter methods
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public void setRoleID(String roleID) {
        this.roleID = roleID;
        // Update type when roleID changes
        if ("AD".equals(roleID) || "ST".equals(roleID)) {
            this.type = TYPE_STAFF;
        } else {
            this.type = TYPE_CUSTOMER;
        }
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Validates if the user object has all required fields
     * @return true if user is valid, false otherwise
     */
    public boolean isValid() {
        return userID != null && !userID.trim().isEmpty() &&
               fullName != null && !fullName.trim().isEmpty() &&
               roleID != null && !roleID.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
    
    /**
     * Checks if the user is a staff member (Admin or Staff role)
     * @return true if user is staff, false otherwise
     */
    public boolean isStaff() {
        return TYPE_STAFF.equals(this.type) || 
               "AD".equals(this.roleID) || 
               "ST".equals(this.roleID);
    }
    
    /**
     * Checks if the user is an admin
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return "AD".equals(this.roleID);
    }
    
    /**
     * Checks if the user is a regular customer
     * @return true if user is customer, false otherwise
     */
    public boolean isCustomer() {
        return TYPE_CUSTOMER.equals(this.type) || "US".equals(this.roleID);
    }
    
    /**
     * Clears sensitive data (password) from the user object
     * Used for security purposes when sending user data to client
     */
    public void clearSensitiveData() {
        this.password = null;
    }
    
    /**
     * Creates a copy of the user without sensitive data
     * @return User object without password
     */
    public User getSecureCopy() {
        User secureCopy = new User();
        secureCopy.setUserID(this.userID);
        secureCopy.setFullName(this.fullName);
        secureCopy.setRoleID(this.roleID);
        secureCopy.setType(this.type);
        // Note: password is intentionally not copied
        return secureCopy;
    }
    
    /**
     * Validates password strength (basic validation)
     * @return true if password meets minimum requirements
     */
    public boolean isPasswordValid() {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Add more password validation rules as needed
        return true;
    }
    
    /**
     * Gets display name for the user role
     * @return Human-readable role name
     */
    public String getRoleDisplayName() {
        switch (roleID) {
            case "AD":
                return "Administrator";
            case "ST":
                return "Staff";
            case "US":
                return "Customer";
            default:
                return "Unknown";
        }
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", fullName='" + fullName + '\'' +
                ", roleID='" + roleID + '\'' +
                ", type='" + type + '\'' +
                ", password='" + (password != null ? "[PROTECTED]" : "null") + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        User user = (User) obj;
        return userID != null ? userID.equals(user.userID) : user.userID == null;
    }
    
    @Override
    public int hashCode() {
        return userID != null ? userID.hashCode() : 0;
    }
}