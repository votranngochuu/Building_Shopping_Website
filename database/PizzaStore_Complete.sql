-- PizzaStore Database Schema for Shopping Website
-- SQL Server 2019

USE master;
GO

-- Create database
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'PizzaStore')
    DROP DATABASE PizzaStore;
GO

CREATE DATABASE PizzaStore;
GO

USE PizzaStore;
GO

-- Create Account table (for user authentication)
CREATE TABLE Account (
    userID NVARCHAR(50) PRIMARY KEY,
    fullName NVARCHAR(200) NOT NULL,
    roleID NVARCHAR(10) NOT NULL, -- AD=Admin, ST=Staff, US=User/Customer
    password NVARCHAR(100) NOT NULL,
    email NVARCHAR(100),
    phone NVARCHAR(20),
    address NVARCHAR(500),
    createdDate DATETIME DEFAULT GETDATE(),
    isActive BIT DEFAULT 1
);
GO

-- Create Categories table
CREATE TABLE Categories (
    CategoryID INT IDENTITY(1,1) PRIMARY KEY,
    CategoryName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(500),
    ImageURL NVARCHAR(500)
);
GO

-- Create Suppliers table
CREATE TABLE Suppliers (
    SupplierID INT IDENTITY(1,1) PRIMARY KEY,
    CompanyName NVARCHAR(200) NOT NULL,
    ContactName NVARCHAR(100),
    Address NVARCHAR(500),
    Phone NVARCHAR(20),
    Email NVARCHAR(100)
);
GO

-- Create Products table
CREATE TABLE Products (
    ProductID INT IDENTITY(1,1) PRIMARY KEY,
    ProductName NVARCHAR(200) NOT NULL,
    SupplierID INT FOREIGN KEY REFERENCES Suppliers(SupplierID),
    CategoryID INT FOREIGN KEY REFERENCES Categories(CategoryID),
    QuantityPerUnit NVARCHAR(100),
    UnitPrice MONEY NOT NULL,
    UnitsInStock INT DEFAULT 0,
    Discontinued BIT DEFAULT 0,
    ProductImage NVARCHAR(500),
    CreatedDate DATETIME DEFAULT GETDATE(),
    ModifiedDate DATETIME DEFAULT GETDATE()
);
GO

-- Create Customers table (extended customer info)
CREATE TABLE Customers (
    CustomerID NVARCHAR(50) PRIMARY KEY,
    ContactName NVARCHAR(200) NOT NULL,
    Address NVARCHAR(500),
    City NVARCHAR(100),
    Phone NVARCHAR(20),
    Email NVARCHAR(100),
    RegistrationDate DATETIME DEFAULT GETDATE()
);
GO

-- Create Orders table
CREATE TABLE Orders (
    OrderID INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID NVARCHAR(50) FOREIGN KEY REFERENCES Customers(CustomerID),
    OrderDate DATETIME NOT NULL DEFAULT GETDATE(),
    RequiredDate DATETIME,
    ShippedDate DATETIME,
    Freight MONEY DEFAULT 0,
    ShipAddress NVARCHAR(500),
    OrderStatus NVARCHAR(20) DEFAULT 'Pending', -- Pending, Processing, Shipped, Delivered, Cancelled
    TotalAmount MONEY DEFAULT 0
);
GO

-- Create Order Details table
CREATE TABLE [Order Details] (
    OrderID INT FOREIGN KEY REFERENCES Orders(OrderID),
    ProductID INT FOREIGN KEY REFERENCES Products(ProductID),
    UnitPrice MONEY NOT NULL,
    Quantity INT NOT NULL,
    Discount REAL DEFAULT 0,
    PRIMARY KEY (OrderID, ProductID)
);
GO

-- Insert sample data

-- Insert Account data (users)
INSERT INTO Account (userID, fullName, roleID, password, email, phone, address) VALUES
('admin', 'System Administrator', 'AD', 'admin123', 'admin@pizzastore.com', '555-0001', '123 Admin St'),
('staff1', 'John Staff', 'ST', 'staff123', 'john.staff@pizzastore.com', '555-0002', '456 Staff Ave'),
('customer1', 'Alice Johnson', 'US', 'customer123', 'alice.johnson@email.com', '555-0003', '789 Customer Rd'),
('user1', 'Bob Smith', 'US', 'user123', 'bob.smith@email.com', '555-0004', '321 User Blvd'),
('manager1', 'Sarah Manager', 'ST', 'manager123', 'sarah.manager@pizzastore.com', '555-0005', '654 Manager Ln');
GO

-- Insert Categories
INSERT INTO Categories (CategoryName, Description, ImageURL) VALUES
('Pizza', 'Fresh and delicious pizzas with various toppings', 'pizza-category.jpg'),
('Beverages', 'Refreshing drinks to complement your meal', 'beverages-category.jpg'),
('Desserts', 'Sweet treats to end your meal perfectly', 'desserts-category.jpg'),
('Appetizers', 'Delicious starters to begin your meal', 'appetizers-category.jpg');
GO

-- Insert Suppliers
INSERT INTO Suppliers (CompanyName, ContactName, Address, Phone, Email) VALUES
('Fresh Pizza Ingredients Co.', 'Michael Fresh', '123 Ingredient St, Food City', '555-1001', 'orders@freshpizza.com'),
('Quality Beverages Ltd.', 'Lisa Drinks', '456 Beverage Ave, Drink Town', '555-1002', 'sales@qualitybev.com'),
('Sweet Desserts Inc.', 'David Sweet', '789 Dessert Rd, Sugar Valley', '555-1003', 'info@sweetdesserts.com'),
('Appetizer Masters', 'Emma Starter', '321 Appetizer Ln, Taste City', '555-1004', 'contact@appetizemasters.com');
GO

-- Insert Products
INSERT INTO Products (ProductName, SupplierID, CategoryID, QuantityPerUnit, UnitPrice, UnitsInStock, Discontinued, ProductImage) VALUES
-- Pizzas
('Margherita Pizza', 1, 1, 'Large 12" pizza with fresh mozzarella and basil', 18.99, 25, 0, 'margherita.jpg'),
('Pepperoni Pizza', 1, 1, 'Large 12" pizza with pepperoni and mozzarella', 21.99, 30, 0, 'pepperoni.jpg'),
('Supreme Pizza', 1, 1, 'Large 12" pizza with pepperoni, sausage, peppers, and mushrooms', 25.99, 20, 0, 'supreme.jpg'),
('Hawaiian Pizza', 1, 1, 'Large 12" pizza with ham and pineapple', 22.99, 15, 0, 'hawaiian.jpg'),
('Vegetarian Pizza', 1, 1, 'Large 12" pizza with fresh vegetables', 20.99, 18, 0, 'vegetarian.jpg'),
('Meat Lovers Pizza', 1, 1, 'Large 12" pizza with pepperoni, sausage, ham, and bacon', 27.99, 12, 0, 'meatlovers.jpg'),
('BBQ Chicken Pizza', 1, 1, 'Large 12" pizza with BBQ chicken and red onions', 24.99, 22, 0, 'bbqchicken.jpg'),
('White Pizza', 1, 1, 'Large 12" pizza with ricotta, mozzarella, and garlic', 19.99, 16, 0, 'white.jpg'),

-- Beverages
('Coca-Cola', 2, 2, '12 oz can', 2.49, 100, 0, 'coca-cola.jpg'),
('Pepsi', 2, 2, '12 oz can', 2.49, 80, 0, 'pepsi.jpg'),
('Sprite', 2, 2, '12 oz can', 2.49, 90, 0, 'sprite.jpg'),
('Orange Juice', 2, 2, '16 oz bottle', 3.99, 50, 0, 'orange-juice.jpg'),
('Iced Tea', 2, 2, '16 oz bottle', 2.99, 60, 0, 'iced-tea.jpg'),
('Water', 2, 2, '16 oz bottle', 1.99, 120, 0, 'water.jpg'),

-- Desserts
('Chocolate Cake', 3, 3, 'Single slice of rich chocolate cake', 6.99, 15, 0, 'chocolate-cake.jpg'),
('Cheesecake', 3, 3, 'New York style cheesecake slice', 7.99, 12, 0, 'cheesecake.jpg'),
('Tiramisu', 3, 3, 'Traditional Italian tiramisu', 8.99, 10, 0, 'tiramisu.jpg'),
('Ice Cream', 3, 3, 'Premium vanilla ice cream scoop', 4.99, 30, 0, 'ice-cream.jpg'),

-- Appetizers
('Garlic Bread', 4, 4, '6 pieces of garlic bread', 7.99, 25, 0, 'garlic-bread.jpg'),
('Mozzarella Sticks', 4, 4, '8 pieces with marinara sauce', 9.99, 20, 0, 'mozzarella-sticks.jpg'),
('Buffalo Wings', 4, 4, '10 pieces with buffalo sauce', 12.99, 18, 0, 'buffalo-wings.jpg'),
('Caesar Salad', 4, 4, 'Fresh romaine with caesar dressing', 8.99, 15, 0, 'caesar-salad.jpg');
GO

-- Insert Customer data
INSERT INTO Customers (CustomerID, ContactName, Address, City, Phone, Email) VALUES
('customer1', 'Alice Johnson', '789 Customer Rd', 'Food City', '555-0003', 'alice.johnson@email.com'),
('user1', 'Bob Smith', '321 User Blvd', 'Taste Town', '555-0004', 'bob.smith@email.com'),
('guest001', 'Charlie Guest', '555 Guest Ave', 'Order City', '555-0010', 'charlie.guest@email.com');
GO

-- Insert sample orders
INSERT INTO Orders (CustomerID, OrderDate, RequiredDate, ShipAddress, OrderStatus, TotalAmount) VALUES
('customer1', '2024-01-15 14:30:00', '2024-01-15 16:00:00', '789 Customer Rd, Food City', 'Delivered', 45.97),
('user1', '2024-01-18 19:15:00', '2024-01-18 20:45:00', '321 User Blvd, Taste Town', 'Delivered', 32.98),
('guest001', '2024-01-20 12:00:00', '2024-01-20 13:30:00', '555 Guest Ave, Order City', 'Processing', 28.98);
GO

-- Insert order details
INSERT INTO [Order Details] (OrderID, ProductID, UnitPrice, Quantity, Discount) VALUES
-- Order 1
(1, 1, 18.99, 1, 0), -- Margherita Pizza
(1, 2, 21.99, 1, 0), -- Pepperoni Pizza
(1, 9, 2.49, 2, 0),  -- Coca-Cola x2

-- Order 2
(2, 3, 25.99, 1, 0), -- Supreme Pizza
(2, 15, 6.99, 1, 0), -- Chocolate Cake

-- Order 3
(3, 4, 22.99, 1, 0), -- Hawaiian Pizza
(3, 13, 2.99, 2, 0); -- Iced Tea x2
GO

-- Create indexes for better performance
CREATE INDEX IX_Products_CategoryID ON Products(CategoryID);
CREATE INDEX IX_Products_SupplierID ON Products(SupplierID);
CREATE INDEX IX_Orders_CustomerID ON Orders(CustomerID);
CREATE INDEX IX_Orders_OrderDate ON Orders(OrderDate);
CREATE INDEX IX_OrderDetails_ProductID ON [Order Details](ProductID);
GO

-- Create views for common queries

-- View for available products
CREATE VIEW vw_AvailableProducts AS
SELECT 
    p.ProductID,
    p.ProductName,
    p.SupplierID,
    p.CategoryID,
    p.QuantityPerUnit,
    p.UnitPrice,
    p.UnitsInStock,
    p.ProductImage,
    c.CategoryName,
    s.CompanyName as SupplierName
FROM Products p
LEFT JOIN Categories c ON p.CategoryID = c.CategoryID
LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID
WHERE p.Discontinued = 0 AND p.UnitsInStock > 0;
GO

-- View for order summary
CREATE VIEW vw_OrderSummary AS
SELECT 
    o.OrderID,
    o.CustomerID,
    c.ContactName as CustomerName,
    o.OrderDate,
    o.OrderStatus,
    o.TotalAmount,
    COUNT(od.ProductID) as ItemCount
FROM Orders o
LEFT JOIN Customers c ON o.CustomerID = c.CustomerID
LEFT JOIN [Order Details] od ON o.OrderID = od.OrderID
GROUP BY o.OrderID, o.CustomerID, c.ContactName, o.OrderDate, o.OrderStatus, o.TotalAmount;
GO

-- Create stored procedures for common operations

-- Procedure to update product stock
CREATE PROCEDURE sp_UpdateProductStock
    @ProductID INT,
    @Quantity INT
AS
BEGIN
    UPDATE Products 
    SET UnitsInStock = UnitsInStock - @Quantity,
        ModifiedDate = GETDATE()
    WHERE ProductID = @ProductID AND UnitsInStock >= @Quantity;
    
    IF @@ROWCOUNT = 0
    BEGIN
        RAISERROR('Insufficient stock or invalid product ID', 16, 1);
    END
END;
GO

-- Procedure to calculate order total
CREATE PROCEDURE sp_CalculateOrderTotal
    @OrderID INT
AS
BEGIN
    UPDATE Orders 
    SET TotalAmount = (
        SELECT ISNULL(SUM(od.UnitPrice * od.Quantity * (1 - od.Discount)), 0)
        FROM [Order Details] od
        WHERE od.OrderID = @OrderID
    )
    WHERE OrderID = @OrderID;
END;
GO

PRINT 'Database created successfully!';
PRINT 'Setup completed successfully! You can now use the PizzaStore database.';
GO
