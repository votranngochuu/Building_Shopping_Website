-- Drop database if exists and create new one
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'PizzaStore')
BEGIN
    ALTER DATABASE PizzaStore SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE PizzaStore;
END
GO

CREATE DATABASE PizzaStore;
GO

USE PizzaStore;
GO

-- 1. Create Account table
CREATE TABLE Account (
    AccountID NVARCHAR(50) PRIMARY KEY,
    UserName NVARCHAR(50) UNIQUE NOT NULL,
    Password NVARCHAR(100) NOT NULL,
    FullName NVARCHAR(200) NOT NULL,
    Type INT NOT NULL CHECK (Type IN (1, 2)) -- 1: Staff, 2: Normal User
);
GO

-- 2. Create Categories table
CREATE TABLE Categories (
    CategoryID INT PRIMARY KEY IDENTITY(1,1),
    CategoryName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(500)
);
GO

-- 3. Create Suppliers table
CREATE TABLE Suppliers (
    SupplierID INT PRIMARY KEY IDENTITY(1,1),
    CompanyName NVARCHAR(200) NOT NULL,
    Address NVARCHAR(500),
    Phone NVARCHAR(20)
);
GO

-- 4. Create Products table
CREATE TABLE Products (
    ProductID INT PRIMARY KEY IDENTITY(1,1),
    ProductName NVARCHAR(200) NOT NULL,
    SupplierID INT,
    CategoryID INT,
    QuantityPerUnit NVARCHAR(100),
    UnitPrice DECIMAL(10, 2) NOT NULL,
    ProductImage NVARCHAR(500),
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID),
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID)
);
GO

-- 5. Create Customers table
CREATE TABLE Customers (
    CustomerID NVARCHAR(50) PRIMARY KEY,
    Password NVARCHAR(100) NOT NULL,
    ContactName NVARCHAR(200) NOT NULL,
    Address NVARCHAR(500),
    Phone NVARCHAR(20)
);
GO

-- 6. Create Orders table
CREATE TABLE Orders (
    OrderID INT PRIMARY KEY IDENTITY(1,1),
    CustomerID NVARCHAR(50),
    OrderDate DATETIME DEFAULT GETDATE(),
    RequiredDate DATETIME,
    ShippedDate DATETIME,
    Freight DECIMAL(10, 2),
    ShipAddress NVARCHAR(500),
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);
GO

-- 7. Create Order Details table
CREATE TABLE OrderDetails (
    OrderID INT,
    ProductID INT,
    UnitPrice DECIMAL(10, 2) NOT NULL,
    Quantity INT NOT NULL,
    PRIMARY KEY (OrderID, ProductID),
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);
GO

-- Insert sample data for Categories
INSERT INTO Categories (CategoryName, Description) VALUES
('Classic', 'Traditional pizza recipes'),
('Premium', 'Premium ingredients and special recipes'),
('Vegetarian', 'Vegetarian friendly pizzas'),
('Special', 'House special pizzas'),
('Beverages', 'Soft drinks and beverages'),
('Desserts', 'Sweet treats and desserts');
GO

-- Insert sample data for Suppliers
INSERT INTO Suppliers (CompanyName, Address, Phone) VALUES
('Italian Foods Co.', '123 Rome Street, Italy', '123-456-7890'),
('Fresh Produce Ltd.', '456 Farm Road, California', '098-765-4321'),
('Cheese Masters', '789 Dairy Lane, Wisconsin', '555-123-4567'),
('Meat Suppliers Inc.', '321 Butcher Street, Texas', '777-888-9999');
GO

-- Insert sample data for Products (Pizzas)
INSERT INTO Products (ProductName, SupplierID, CategoryID, QuantityPerUnit, UnitPrice, ProductImage) VALUES
('Margherita Pizza', 1, 1, '12 inch', 65.00, 'images/margherita.jpg'),
('Pepperoni Pizza', 4, 1, '12 inch', 70.00, 'images/pepperoni.jpg'),
('Hawaiian Pizza', 2, 4, '12 inch', 75.00, 'images/hawaiian.jpg'),
('Meat Lovers Pizza', 4, 2, '12 inch', 85.00, 'images/meatlovers.jpg'),
('Vegetarian Pizza', 2, 3, '12 inch', 65.00, 'images/vegetarian.jpg'),
('BBQ Chicken Pizza', 4, 4, '12 inch', 80.00, 'images/bbqchicken.jpg'),
('Four Cheese Pizza', 3, 2, '12 inch', 80.00, 'images/fourcheese.jpg'),
('Seafood Pizza', 1, 2, '12 inch', 95.00, 'images/seafood.jpg'),
('Capricciosa Pizza', 1, 1, '12 inch', 70.00, 'images/capricciosa.jpg'),
('Calzone', 1, 4, '1 piece', 55.00, 'images/calzone.jpg'),
('Coca Cola', 2, 5, '330ml can', 15.00, 'images/cocacola.jpg'),
('Pepsi', 2, 5, '330ml can', 15.00, 'images/pepsi.jpg'),
('Orange Juice', 2, 5, '250ml glass', 25.00, 'images/orange.jpg'),
('Tiramisu', 1, 6, '1 slice', 35.00, 'images/tiramisu.jpg'),
('Ice Cream', 3, 6, '3 scoops', 30.00, 'images/icecream.jpg');
GO

-- Insert sample data for Account (Staff and Users)
INSERT INTO Account (AccountID, UserName, Password, FullName, Type) VALUES
('ACC001', 'admin', 'admin123', 'Administrator', 1),
('ACC002', 'staff1', 'staff123', 'John Staff', 1),
('ACC003', 'staff2', 'staff123', 'Jane Staff', 1),
('ACC004', 'user1', 'user123', 'David Customer', 2),
('ACC005', 'user2', 'user123', 'Sarah Customer', 2);
GO

-- Insert sample data for Customers
INSERT INTO Customers (CustomerID, Password, ContactName, Address, Phone) VALUES
('CUST001', 'pass123', 'David Brown', '123 Main St, New York', '555-0001'),
('CUST002', 'pass123', 'Sarah Wilson', '456 Oak Ave, Los Angeles', '555-0002'),
('CUST003', 'pass123', 'Michael Johnson', '789 Pine Rd, Chicago', '555-0003'),
('CUST004', 'pass123', 'Emily Davis', '321 Elm St, Houston', '555-0004'),
('CUST005', 'pass123', 'Robert Miller', '654 Maple Dr, Phoenix', '555-0005');
GO

-- Insert sample Orders
INSERT INTO Orders (CustomerID, OrderDate, RequiredDate, ShippedDate, Freight, ShipAddress) VALUES
('CUST001', '2024-01-15', '2024-01-15', '2024-01-15', 20.00, '123 Main St, New York'),
('CUST002', '2024-01-16', '2024-01-16', '2024-01-16', 25.00, '456 Oak Ave, Los Angeles'),
('CUST001', '2024-01-17', '2024-01-17', '2024-01-17', 20.00, '123 Main St, New York'),
('CUST003', '2024-01-18', '2024-01-18', NULL, 30.00, '789 Pine Rd, Chicago'),
('CUST004', '2024-01-19', '2024-01-19', NULL, 25.00, '321 Elm St, Houston');
GO

-- Insert sample Order Details
INSERT INTO OrderDetails (OrderID, ProductID, UnitPrice, Quantity) VALUES
(1, 1, 65.00, 2),
(1, 11, 15.00, 4),
(2, 3, 75.00, 1),
(2, 4, 85.00, 1),
(2, 12, 15.00, 2),
(3, 2, 70.00, 3),
(3, 14, 35.00, 2),
(4, 5, 65.00, 1),
(4, 6, 80.00, 1),
(5, 7, 80.00, 2),
(5, 13, 25.00, 3);
GO

-- Create indexes for better performance
CREATE INDEX idx_products_category ON Products(CategoryID);
CREATE INDEX idx_products_supplier ON Products(SupplierID);
CREATE INDEX idx_orders_customer ON Orders(CustomerID);
CREATE INDEX idx_orders_date ON Orders(OrderDate);
CREATE INDEX idx_account_login ON Account(UserName, Password);
CREATE INDEX idx_customer_login ON Customers(CustomerID, Password);
GO

-- Create view for sales report
CREATE VIEW SalesReport AS
SELECT 
    o.OrderID,
    o.OrderDate,
    c.ContactName AS CustomerName,
    p.ProductName,
    od.Quantity,
    od.UnitPrice,
    (od.Quantity * od.UnitPrice) AS TotalAmount
FROM Orders o
INNER JOIN Customers c ON o.CustomerID = c.CustomerID
INNER JOIN OrderDetails od ON o.OrderID = od.OrderID
INNER JOIN Products p ON od.ProductID = p.ProductID;
GO

PRINT 'Database PizzaStore created successfully with complete schema!';
PRINT '';
PRINT 'Test accounts:';
PRINT '  Staff accounts (Type=1):';
PRINT '    - admin / admin123';
PRINT '    - staff1 / staff123';
PRINT '    - staff2 / staff123';
PRINT '  User accounts (Type=2):';
PRINT '    - user1 / user123';
PRINT '    - user2 / user123';
PRINT '';
PRINT 'Customer accounts:';
PRINT '    - CUST001 / pass123';
PRINT '    - CUST002 / pass123';
GO
