package dao;

import entities.Order;
import entities.OrderDetail;
import utils.DBConnection;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Order entity
 */
public class OrderDAO {
    
    /**
     * Get all orders
     */
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.ContactName " +
                    "FROM Orders o " +
                    "INNER JOIN Customers c ON o.CustomerID = c.CustomerID " +
                    "ORDER BY o.OrderDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                orders.add(extractOrder(rs));
            }
        }
        return orders;
    }
    
    /**
     * Get order by ID
     */
    public Order getOrderByID(int orderID) throws SQLException {
        String sql = "SELECT o.*, c.ContactName " +
                    "FROM Orders o " +
                    "INNER JOIN Customers c ON o.CustomerID = c.CustomerID " +
                    "WHERE o.OrderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderID);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = extractOrder(rs);
                    // Load order details
                    order.setOrderDetails(getOrderDetails(orderID));
                    return order;
                }
            }
        }
        return null;
    }
    
    /**
     * Get orders by customer
     */
    public List<Order> getOrdersByCustomer(String customerID) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.ContactName " +
                    "FROM Orders o " +
                    "INNER JOIN Customers c ON o.CustomerID = c.CustomerID " +
                    "WHERE o.CustomerID = ? " +
                    "ORDER BY o.OrderDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, customerID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(extractOrder(rs));
                }
            }
        }
        return orders;
    }
    
    /**
     * Get orders by date range (for sales report)
     */
    public List<Order> getOrdersByDateRange(Date startDate, Date endDate) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.ContactName " +
                    "FROM Orders o " +
                    "INNER JOIN Customers c ON o.CustomerID = c.CustomerID " +
                    "WHERE o.OrderDate BETWEEN ? AND ? " +
                    "ORDER BY o.OrderDate";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new Timestamp(startDate.getTime()));
            ps.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = extractOrder(rs);
                    // Load order details for report
                    order.setOrderDetails(getOrderDetails(order.getOrderID()));
                    orders.add(order);
                }
            }
        }
        return orders;
    }
    
    /**
     * Create new order
     */
    public int createOrder(Order order) throws SQLException {
        String sql = "INSERT INTO Orders (CustomerID, OrderDate, RequiredDate, ShippedDate, Freight, ShipAddress) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, order.getCustomerID());
            ps.setTimestamp(2, new Timestamp(order.getOrderDate().getTime()));
            ps.setTimestamp(3, order.getRequiredDate() != null ? 
                new Timestamp(order.getRequiredDate().getTime()) : null);
            ps.setTimestamp(4, order.getShippedDate() != null ? 
                new Timestamp(order.getShippedDate().getTime()) : null);
            ps.setBigDecimal(5, order.getFreight());
            ps.setString(6, order.getShipAddress());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }
    
    /**
     * Update order
     */
    public boolean updateOrder(Order order) throws SQLException {
        String sql = "UPDATE Orders SET CustomerID = ?, RequiredDate = ?, ShippedDate = ?, " +
                    "Freight = ?, ShipAddress = ? WHERE OrderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, order.getCustomerID());
            ps.setTimestamp(2, order.getRequiredDate() != null ? 
                new Timestamp(order.getRequiredDate().getTime()) : null);
            ps.setTimestamp(3, order.getShippedDate() != null ? 
                new Timestamp(order.getShippedDate().getTime()) : null);
            ps.setBigDecimal(4, order.getFreight());
            ps.setString(5, order.getShipAddress());
            ps.setInt(6, order.getOrderID());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete order (and its details)
     */
    public boolean deleteOrder(int orderID) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete order details first
            String detailsSql = "DELETE FROM OrderDetails WHERE OrderID = ?";
            try (PreparedStatement ps = conn.prepareStatement(detailsSql)) {
                ps.setInt(1, orderID);
                ps.executeUpdate();
            }
            
            // Delete order
            String orderSql = "DELETE FROM Orders WHERE OrderID = ?";
            try (PreparedStatement ps = conn.prepareStatement(orderSql)) {
                ps.setInt(1, orderID);
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    /**
     * Get order details for an order
     */
    public List<OrderDetail> getOrderDetails(int orderID) throws SQLException {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT od.*, p.ProductName " +
                    "FROM OrderDetails od " +
                    "INNER JOIN Products p ON od.ProductID = p.ProductID " +
                    "WHERE od.OrderID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, orderID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderID(rs.getInt("OrderID"));
                    detail.setProductID(rs.getInt("ProductID"));
                    detail.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    detail.setQuantity(rs.getInt("Quantity"));
                    detail.setProductName(rs.getString("ProductName"));
                    details.add(detail);
                }
            }
        }
        return details;
    }
    
    /**
     * Add order detail
     */
    public boolean addOrderDetail(OrderDetail detail) throws SQLException {
        String sql = "INSERT INTO OrderDetails (OrderID, ProductID, UnitPrice, Quantity) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, detail.getOrderID());
            ps.setInt(2, detail.getProductID());
            ps.setBigDecimal(3, detail.getUnitPrice());
            ps.setInt(4, detail.getQuantity());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Get sales report data
     */
    public BigDecimal getTotalSales(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT SUM(od.UnitPrice * od.Quantity) AS TotalSales " +
                    "FROM Orders o " +
                    "INNER JOIN OrderDetails od ON o.OrderID = od.OrderID " +
                    "WHERE o.OrderDate BETWEEN ? AND ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new Timestamp(startDate.getTime()));
            ps.setTimestamp(2, new Timestamp(endDate.getTime()));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("TotalSales");
                }
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Extract Order from ResultSet
     */
    private Order extractOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(rs.getInt("OrderID"));
        order.setCustomerID(rs.getString("CustomerID"));
        order.setOrderDate(rs.getTimestamp("OrderDate"));
        order.setRequiredDate(rs.getTimestamp("RequiredDate"));
        order.setShippedDate(rs.getTimestamp("ShippedDate"));
        order.setFreight(rs.getBigDecimal("Freight"));
        order.setShipAddress(rs.getString("ShipAddress"));
        
        // Additional field if available
        try {
            order.setCustomerName(rs.getString("ContactName"));
        } catch (SQLException e) {
            // Column may not exist in some queries
        }
        
        return order;
    }
}
