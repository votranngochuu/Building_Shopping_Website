package entities;

/**
 * Account entity class representing the Account table
 */
public class Account {
    private String accountID;
    private String userName;
    private String password;
    private String fullName;
    private int type; // 1: Staff, 2: Normal User
    
    // Constants for user types
    public static final int TYPE_STAFF = 1;
    public static final int TYPE_USER = 2;
    
    // Constructors
    public Account() {
    }
    
    public Account(String accountID, String userName, String password, String fullName, int type) {
        this.accountID = accountID;
        this.userName = userName;
        this.password = password;
        this.fullName = fullName;
        this.type = type;
    }
    
    // Getters and Setters
    public String getAccountID() {
        return accountID;
    }
    
    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    // Utility methods
    public boolean isStaff() {
        return this.type == TYPE_STAFF;
    }
    
    public boolean isUser() {
        return this.type == TYPE_USER;
    }
    
    public String getTypeString() {
        return type == TYPE_STAFF ? "Staff" : "User";
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "accountID='" + accountID + '\'' +
                ", userName='" + userName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", type=" + type +
                '}';
    }
}
