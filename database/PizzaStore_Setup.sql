-- Create Database PizzaStore
CREATE DATABASE PizzaStore;
GO

USE PizzaStore;
GO

-- Create Account table for users
CREATE TABLE Account (
    userID NVARCHAR(50) PRIMARY KEY,
    fullName NVARCHAR(200) NOT NULL,
    roleID NVARCHAR(10) NOT NULL,
    password NVARCHAR(100) NOT NULL
);
GO

-- Create Product table for pizzas
CREATE TABLE Product (
    productID NVARCHAR(50) PRIMARY KEY,
    productName NVARCHAR(200) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    category NVARCHAR(100),
    description NVARCHAR(MAX),
    imageURL NVARCHAR(500)
);
GO

-- Insert sample users
INSERT INTO Account (userID, fullName, roleID, password) VALUES
('admin', 'Administrator', 'AD', 'admin123'),
('staff1', 'Staff Member 1', 'ST', 'staff123'),
('user1', 'John Doe', 'US', 'user123'),
('user2', 'Jane Smith', 'US', 'user123');
GO

-- Insert sample products (pizzas)
INSERT INTO Product (productID, productName, price, quantity, category, description, imageURL) VALUES
('P001', 'Margherita Pizza', 8.99, 50, 'Classic', 'Traditional pizza with tomato sauce, mozzarella, and basil', 'margherita.jpg'),
('P002', 'Pepperoni Pizza', 10.99, 45, 'Classic', 'Classic pepperoni with mozzarella cheese', 'pepperoni.jpg'),
('P003', 'Hawaiian Pizza', 11.99, 40, 'Special', 'Ham and pineapple with mozzarella', 'hawaiian.jpg'),
('P004', 'Meat Lovers Pizza', 14.99, 35, 'Premium', 'Pepperoni, sausage, bacon, and ham', 'meatlovers.jpg'),
('P005', 'Vegetarian Pizza', 9.99, 30, 'Vegetarian', 'Fresh vegetables with mozzarella', 'vegetarian.jpg'),
('P006', 'BBQ Chicken Pizza', 12.99, 25, 'Special', 'BBQ sauce, chicken, red onions, and cilantro', 'bbqchicken.jpg'),
('P007', 'Four Cheese Pizza', 13.99, 20, 'Premium', 'Mozzarella, cheddar, parmesan, and gorgonzola', 'fourcheese.jpg'),
('P008', 'Seafood Pizza', 15.99, 15, 'Premium', 'Shrimp, calamari, and mussels with garlic', 'seafood.jpg');
GO

-- Create indexes for better performance
CREATE INDEX idx_account_login ON Account(userID, password);
CREATE INDEX idx_product_category ON Product(category);
GO

PRINT 'Database PizzaStore created successfully!';
PRINT 'Sample data inserted successfully!';
PRINT '';
PRINT 'Test accounts:';
PRINT '  Admin: admin / admin123';
PRINT '  Staff: staff1 / staff123';
PRINT '  Users: user1 / user123, user2 / user123';
GO
