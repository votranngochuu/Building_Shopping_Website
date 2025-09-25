package entities;

/**
 * Customer entity class representing the Customers table
 */
public class Customer {
    private String customerID;
    private String password;
    private String contactName;
    private String address;
    private String phone;
    
    // Constructors
    public Customer() {
    }
    
    public Customer(String customerID, String password, String contactName, String address, String phone) {
        this.customerID = customerID;
        this.password = password;
        this.contactName = contactName;
        this.address = address;
        this.phone = phone;
    }
    
    // Getters and Setters
    public String getCustomerID() {
        return customerID;
    }
    
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getContactName() {
        return contactName;
    }
    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "customerID='" + customerID + '\'' +
                ", contactName='" + contactName + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
