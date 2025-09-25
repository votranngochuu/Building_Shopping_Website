package ShoppingServlet;

import DBUtils.Product;
import DBUtils.ProductDAO;
import DBUtils.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shopping Servlet - Main controller for product shopping operations
 */
@WebServlet(name = "ShoppingServlet", urlPatterns = {"/ShoppingServlet"})
public class ShoppingServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ShoppingServlet.class.getName());
    private ProductDAO productDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        productDAO = new ProductDAO();
        LOGGER.info("ShoppingServlet initialized successfully");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        
        // Check authentication
        if (!isUserLoggedIn(request)) {
            response.sendRedirect("LoginServlet");
            return;
        }
        
        String action = request.getParameter("action");
        
        try {
            switch (action != null ? action : "") {
                // Admin Functions (Functions 02-07)
                case "CreatePizza":
                    handleCreatePizza(request, response);
                    break;
                case "DeletePizza":
                    handleDeletePizza(request, response);
                    break;
                case "UpdatePizza":
                    handleUpdatePizza(request, response);
                    break;
                case "ViewPizzaList":
                    handleViewPizzaList(request, response);
                    break;
                case "ViewPizzaDetails":
                    handleViewPizzaDetails(request, response);
                    break;
                case "SalesReport":
                    handleSalesReport(request, response);
                    break;
                
                // User Functions (Functions 08-10)
                case "SearchPizza":
                    handleSearchPizza(request, response);
                    break;
                case "ViewPizzaListUser":
                    handleViewPizzaListUser(request, response);
                    break;
                case "ViewCart":
                    handleViewCart(request, response);
                    break;
                case "UpdateCartItem":
                    handleUpdateCartItem(request, response);
                    break;
                case "RemoveCartItem":
                    handleRemoveCartItem(request, response);
                    break;
                
                // Legacy functions for backward compatibility
                case "Search":
                    handleSearch(request, response);
                    break;
                case "SearchByPrice":
                    handleSearchByPrice(request, response);
                    break;
                case "SearchByCategory":
                    handleSearchByCategory(request, response);
                    break;
                case "LoadAll":
                    handleLoadAll(request, response);
                    break;
                case "ViewDetails":
                    handleViewDetails(request, response);
                    break;
                case "AddToCart":
                    handleAddToCart(request, response);
                    break;
                case "Checkout":
                    handleCheckout(request, response);
                    break;
                default:
                    // Default: load all available products
                    handleLoadAll(request, response);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ShoppingServlet", e);
            request.setAttribute("ERROR", "System error occurred. Please try again.");
            request.getRequestDispatcher("Shopping.html").forward(request, response);
        }
    }
    
    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String searchName = request.getParameter("searchName");
        
        if (searchName == null || searchName.trim().isEmpty()) {
            request.setAttribute("ERROR", "Please enter a product name to search");
            request.getRequestDispatcher("Shopping.html").forward(request, response);
            return;
        }
        
        searchName = searchName.trim();
        
        List<Product> products = productDAO.searchProductsByName(searchName);
        
        if (products.isEmpty()) {
            request.setAttribute("MESSAGE", "No products found with name containing: " + searchName);
        } else {
            request.setAttribute("LIST_PRODUCT", products);
            request.setAttribute("SEARCH_TERM", searchName);
            LOGGER.log(Level.INFO, "Search by name completed: {0} products found for: {1}", 
                      new Object[]{products.size(), searchName});
        }
        
        request.getRequestDispatcher("Shopping.html").forward(request, response);
    }
    
    private void handleSearchByPrice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        
        if (minPriceStr == null || minPriceStr.trim().isEmpty() ||
            maxPriceStr == null || maxPriceStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Please enter both minimum and maximum prices");
            request.getRequestDispatcher("Shopping.html").forward(request, response);
            return;
        }
        
        try {
            BigDecimal minPrice = new BigDecimal(minPriceStr.trim());
            BigDecimal maxPrice = new BigDecimal(maxPriceStr.trim());
            
            if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(minPrice) < 0) {
                request.setAttribute("ERROR", "Invalid price range. Please check your values.");
                request.getRequestDispatcher("Shopping.html").forward(request, response);
                return;
            }
            
            List<Product> products = productDAO.searchProductsByPrice(minPrice, maxPrice);
            
            if (products.isEmpty()) {
                request.setAttribute("MESSAGE", "No products found in price range: $" + minPrice + " - $" + maxPrice);
            } else {
                request.setAttribute("LIST_PRODUCT", products);
                request.setAttribute("SEARCH_TERM", "Price Range: $" + minPrice + " - $" + maxPrice);
                LOGGER.log(Level.INFO, "Search by price completed: {0} products found", products.size());
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid price format. Please enter valid numbers.");
        }
        
        request.getRequestDispatcher("Shopping.html").forward(request, response);
    }
    
    private void handleSearchByCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String categoryIDStr = request.getParameter("categoryID");
        
        if (categoryIDStr == null || categoryIDStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Please select a category");
            request.getRequestDispatcher("Shopping.html").forward(request, response);
            return;
        }
        
        try {
            int categoryID = Integer.parseInt(categoryIDStr.trim());
            
            List<Product> products = productDAO.getProductsByCategory(categoryID);
            
            if (products.isEmpty()) {
                request.setAttribute("MESSAGE", "No products found in selected category");
            } else {
                request.setAttribute("LIST_PRODUCT", products);
                request.setAttribute("SEARCH_TERM", "Category: " + products.get(0).getCategoryName());
                LOGGER.log(Level.INFO, "Search by category completed: {0} products found", products.size());
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid category selection");
        }
        
        request.getRequestDispatcher("Shopping.html").forward(request, response);
    }
    
    private void handleLoadAll(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User currentUser = getCurrentUser(request);
        List<Product> products;
        
        // If staff, show all products; if customer, show only available products
        if (currentUser != null && currentUser.isStaff()) {
            products = productDAO.getAllProducts();
            request.setAttribute("SEARCH_TERM", "All Products (Staff View)");
        } else {
            products = productDAO.getAvailableProducts();
            request.setAttribute("SEARCH_TERM", "Available Products");
        }
        
        if (products.isEmpty()) {
            request.setAttribute("MESSAGE", "No products found in the system");
        } else {
            request.setAttribute("LIST_PRODUCT", products);
            LOGGER.log(Level.INFO, "Loaded all products: {0} products", products.size());
        }
        
        request.getRequestDispatcher("Shopping.html").forward(request, response);
    }
    
    private void handleViewDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String productIDStr = request.getParameter("productID");
        
        if (productIDStr == null || productIDStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Product ID is required");
            handleLoadAll(request, response);
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDStr.trim());
            Product product = productDAO.getProductById(productID);
            
            if (product == null) {
                request.setAttribute("ERROR", "Product not found");
                handleLoadAll(request, response);
                return;
            }
            
            request.setAttribute("SELECTED_PRODUCT", product);
            request.setAttribute("VIEW_MODE", "details");
            
            // Also load all products for the main list
            handleLoadAll(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid product ID format");
            handleLoadAll(request, response);
        }
    }
    
    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String productIDStr = request.getParameter("productID");
        String quantityStr = request.getParameter("quantity");
        
        if (productIDStr == null || productIDStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Product ID is required");
            handleLoadAll(request, response);
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDStr.trim());
            int quantity = 1; // Default quantity
            
            if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                quantity = Integer.parseInt(quantityStr.trim());
            }
            
            if (quantity <= 0) {
                request.setAttribute("ERROR", "Quantity must be greater than 0");
                handleLoadAll(request, response);
                return;
            }
            
            Product product = productDAO.getProductById(productID);
            
            if (product == null) {
                request.setAttribute("ERROR", "Product not found");
                handleLoadAll(request, response);
                return;
            }
            
            if (!product.isAvailable()) {
                request.setAttribute("ERROR", "Product is not available for purchase");
                handleLoadAll(request, response);
                return;
            }
            
            if (product.getUnitsInStock() < quantity) {
                request.setAttribute("ERROR", "Insufficient stock. Available: " + product.getUnitsInStock());
                handleLoadAll(request, response);
                return;
            }
            
            // Add to cart (using session)
            addToCart(request, product, quantity);
            request.setAttribute("MESSAGE", "Product added to cart successfully!");
            
            LOGGER.log(Level.INFO, "Product added to cart: {0}, quantity: {1}", 
                      new Object[]{product.getProductName(), quantity});
            
            handleLoadAll(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid number format");
            handleLoadAll(request, response);
        }
    }
    
    private void handleViewCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("SHOPPING_CART");
        
        if (cart == null || cart.isEmpty()) {
            // Return HTML for empty cart
            response.setContentType("text/html");
            response.getWriter().write(
                "<div class='text-center py-4'>" +
                "<i class='fas fa-shopping-cart fa-3x text-muted mb-3'></i>" +
                "<h5 class='text-muted'>Your cart is empty</h5>" +
                "<p class='text-muted'>Add some delicious pizzas to get started!</p>" +
                "</div>"
            );
            return;
        }
        
        // Generate cart HTML
        StringBuilder cartHtml = new StringBuilder();
        BigDecimal total = BigDecimal.ZERO;
        
        cartHtml.append("<div class='cart-items'>");
        
        for (CartItem item : cart) {
            BigDecimal itemTotal = item.getSubtotal();
            total = total.add(itemTotal);
            
            cartHtml.append("<div class='cart-item border-bottom py-3'>");
            cartHtml.append("<div class='row align-items-center'>");
            cartHtml.append("<div class='col-md-6'>");
            cartHtml.append("<h6 class='mb-1'>").append(item.getProductName()).append("</h6>");
            cartHtml.append("<small class='text-muted'>").append(item.getFormattedPrice()).append(" each</small>");
            cartHtml.append("</div>");
            cartHtml.append("<div class='col-md-3'>");
            cartHtml.append("<div class='input-group input-group-sm'>");
            cartHtml.append("<button class='btn btn-outline-secondary' onclick='updateCartItem(")
                   .append(item.getProductID()).append(", ").append(item.getQuantity() - 1).append(")'>-</button>");
            cartHtml.append("<input type='number' class='form-control text-center' value='")
                   .append(item.getQuantity()).append("' min='1' max='99' ")
                   .append("onchange='updateCartItem(").append(item.getProductID())
                   .append(", this.value)'>");
            cartHtml.append("<button class='btn btn-outline-secondary' onclick='updateCartItem(")
                   .append(item.getProductID()).append(", ").append(item.getQuantity() + 1).append(")'>+</button>");
            cartHtml.append("</div>");
            cartHtml.append("</div>");
            cartHtml.append("<div class='col-md-2'>");
            cartHtml.append("<strong>").append(String.format("$%.2f", itemTotal)).append("</strong>");
            cartHtml.append("</div>");
            cartHtml.append("<div class='col-md-1'>");
            cartHtml.append("<button class='btn btn-outline-danger btn-sm' onclick='removeCartItem(")
                   .append(item.getProductID()).append(")' title='Remove'>");
            cartHtml.append("<i class='fas fa-trash'></i>");
            cartHtml.append("</button>");
            cartHtml.append("</div>");
            cartHtml.append("</div>");
            cartHtml.append("</div>");
        }
        
        cartHtml.append("</div>");
        
        // Add total
        cartHtml.append("<div class='cart-total border-top pt-3 mt-3'>");
        cartHtml.append("<div class='row'>");
        cartHtml.append("<div class='col-8'><h5>Total:</h5></div>");
        cartHtml.append("<div class='col-4 text-end'><h5 class='text-success'>")
               .append(String.format("$%.2f", total)).append("</h5></div>");
        cartHtml.append("</div>");
        cartHtml.append("</div>");
        
        response.setContentType("text/html");
        response.getWriter().write(cartHtml.toString());
    }
    
    private void handleCheckout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("SHOPPING_CART");
        
        if (cart == null || cart.isEmpty()) {
            request.setAttribute("ERROR", "Your cart is empty");
            handleLoadAll(request, response);
            return;
        }
        
        // Simulate checkout process
        User currentUser = getCurrentUser(request);
        BigDecimal total = calculateCartTotal(cart);
        
        // Clear cart after successful checkout
        session.removeAttribute("SHOPPING_CART");
        
        request.setAttribute("MESSAGE", "Order placed successfully! Total: " + String.format("$%.2f", total));
        
        LOGGER.log(Level.INFO, "Order placed by user: {0}, total: {1}", 
                  new Object[]{currentUser.getUserID(), total});
        
        handleLoadAll(request, response);
    }
    
    // ==================== ADMIN FUNCTIONS (Functions 02-07) ====================
    
    /**
     * Function 02: Admin - Create a pizza
     */
    private void handleCreatePizza(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!isAdminUser(request)) {
            request.setAttribute("ERROR", "Access denied. Admin privileges required.");
            handleLoadAll(request, response);
            return;
        }
        
        String productName = request.getParameter("productName");
        String supplierIDStr = request.getParameter("supplierID");
        String categoryIDStr = request.getParameter("categoryID");
        String quantityPerUnit = request.getParameter("quantityPerUnit");
        String unitPriceStr = request.getParameter("unitPrice");
        String unitsInStockStr = request.getParameter("unitsInStock");
        String productImage = request.getParameter("productImage");
        
        // Validate input
        if (!isValidProductInput(productName, supplierIDStr, categoryIDStr, unitPriceStr, unitsInStockStr)) {
            request.setAttribute("ERROR", "All fields are required and must be valid");
            request.setAttribute("LIST_CATEGORIES", productDAO.getAllCategories());
            request.setAttribute("LIST_SUPPLIERS", productDAO.getAllSuppliers());
            request.getRequestDispatcher("AdminCreatePizza.html").forward(request, response);
            return;
        }
        
        try {
            Product newProduct = new Product();
            newProduct.setProductName(productName.trim());
            newProduct.setSupplierID(Integer.parseInt(supplierIDStr.trim()));
            newProduct.setCategoryID(Integer.parseInt(categoryIDStr.trim()));
            newProduct.setQuantityPerUnit(quantityPerUnit != null ? quantityPerUnit.trim() : "");
            newProduct.setUnitPrice(new BigDecimal(unitPriceStr.trim()));
            newProduct.setUnitsInStock(Integer.parseInt(unitsInStockStr.trim()));
            newProduct.setProductImage(productImage != null ? productImage.trim() : "");
            newProduct.setDiscontinued(false);
            
            if (productDAO.insertProduct(newProduct)) {
                request.setAttribute("MESSAGE", "Pizza created successfully!");
                LOGGER.log(Level.INFO, "Pizza created: {0}", productName);
            } else {
                request.setAttribute("ERROR", "Failed to create pizza. Please try again.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid number format in input fields");
        }
        
        handleViewPizzaList(request, response);
    }
    
    /**
     * Function 03: Admin - Delete a pizza
     */
    private void handleDeletePizza(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!isAdminUser(request)) {
            request.setAttribute("ERROR", "Access denied. Admin privileges required.");
            handleLoadAll(request, response);
            return;
        }
        
        String productIDStr = request.getParameter("productID");
        
        if (productIDStr == null || productIDStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Product ID is required");
            handleViewPizzaList(request, response);
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDStr.trim());
            
            if (productDAO.deleteProduct(productID)) {
                request.setAttribute("MESSAGE", "Pizza deleted successfully!");
                LOGGER.log(Level.INFO, "Pizza deleted with ID: {0}", productID);
            } else {
                request.setAttribute("ERROR", "Failed to delete pizza. Product may not exist.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid product ID format");
        }
        
        handleViewPizzaList(request, response);
    }
    
    /**
     * Function 04: Admin - Update a pizza
     */
    private void handleUpdatePizza(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!isAdminUser(request)) {
            request.setAttribute("ERROR", "Access denied. Admin privileges required.");
            handleLoadAll(request, response);
            return;
        }
        
        String productIDStr = request.getParameter("productID");
        String productName = request.getParameter("productName");
        String supplierIDStr = request.getParameter("supplierID");
        String categoryIDStr = request.getParameter("categoryID");
        String quantityPerUnit = request.getParameter("quantityPerUnit");
        String unitPriceStr = request.getParameter("unitPrice");
        String unitsInStockStr = request.getParameter("unitsInStock");
        String productImage = request.getParameter("productImage");
        String discontinuedStr = request.getParameter("discontinued");
        
        if (productIDStr == null || productIDStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Product ID is required");
            handleViewPizzaList(request, response);
            return;
        }
        
        if (!isValidProductInput(productName, supplierIDStr, categoryIDStr, unitPriceStr, unitsInStockStr)) {
            request.setAttribute("ERROR", "All fields are required and must be valid");
            handleViewPizzaList(request, response);
            return;
        }
        
        try {
            Product product = new Product();
            product.setProductID(Integer.parseInt(productIDStr.trim()));
            product.setProductName(productName.trim());
            product.setSupplierID(Integer.parseInt(supplierIDStr.trim()));
            product.setCategoryID(Integer.parseInt(categoryIDStr.trim()));
            product.setQuantityPerUnit(quantityPerUnit != null ? quantityPerUnit.trim() : "");
            product.setUnitPrice(new BigDecimal(unitPriceStr.trim()));
            product.setUnitsInStock(Integer.parseInt(unitsInStockStr.trim()));
            product.setProductImage(productImage != null ? productImage.trim() : "");
            product.setDiscontinued("true".equalsIgnoreCase(discontinuedStr) || "1".equals(discontinuedStr));
            
            if (productDAO.updateProduct(product)) {
                request.setAttribute("MESSAGE", "Pizza updated successfully!");
                LOGGER.log(Level.INFO, "Pizza updated: {0}", productName);
            } else {
                request.setAttribute("ERROR", "Failed to update pizza. Please try again.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid number format in input fields");
        }
        
        handleViewPizzaList(request, response);
    }
    
    /**
     * Function 05: Admin - View pizza list
     */
    private void handleViewPizzaList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!isAdminUser(request)) {
            request.setAttribute("ERROR", "Access denied. Admin privileges required.");
            handleLoadAll(request, response);
            return;
        }
        
        List<Product> products = productDAO.getAllProducts();
        request.setAttribute("LIST_PRODUCT", products);
        request.setAttribute("VIEW_MODE", "admin_list");
        request.setAttribute("LIST_CATEGORIES", productDAO.getAllCategories());
        request.setAttribute("LIST_SUPPLIERS", productDAO.getAllSuppliers());
        
        LOGGER.log(Level.INFO, "Admin viewed pizza list: {0} products", products.size());
        request.getRequestDispatcher("AdminPizzaManagement.html").forward(request, response);
    }
    
    /**
     * Function 06: Admin - View pizza details
     */
    private void handleViewPizzaDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!isAdminUser(request)) {
            request.setAttribute("ERROR", "Access denied. Admin privileges required.");
            handleLoadAll(request, response);
            return;
        }
        
        String productIDStr = request.getParameter("productID");
        
        if (productIDStr == null || productIDStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Product ID is required");
            handleViewPizzaList(request, response);
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDStr.trim());
            Product product = productDAO.getProductById(productID);
            
            if (product == null) {
                request.setAttribute("ERROR", "Product not found");
                handleViewPizzaList(request, response);
                return;
            }
            
            request.setAttribute("SELECTED_PRODUCT", product);
            request.setAttribute("VIEW_MODE", "admin_details");
            request.setAttribute("LIST_CATEGORIES", productDAO.getAllCategories());
            request.setAttribute("LIST_SUPPLIERS", productDAO.getAllSuppliers());
            
            LOGGER.log(Level.INFO, "Admin viewed pizza details: {0}", product.getProductName());
            request.getRequestDispatcher("AdminPizzaDetails.html").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid product ID format");
            handleViewPizzaList(request, response);
        }
    }
    
    /**
     * Function 07: Admin - Sales report by period
     */
    private void handleSalesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if (!isAdminUser(request)) {
            request.setAttribute("ERROR", "Access denied. Admin privileges required.");
            handleLoadAll(request, response);
            return;
        }
        
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        
        if (startDateStr == null || startDateStr.trim().isEmpty() ||
            endDateStr == null || endDateStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Start date and end date are required");
            request.getRequestDispatcher("AdminSalesReport.html").forward(request, response);
            return;
        }
        
        try {
            java.util.Date startDate = java.sql.Date.valueOf(startDateStr.trim());
            java.util.Date endDate = java.sql.Date.valueOf(endDateStr.trim());
            
            if (startDate.after(endDate)) {
                request.setAttribute("ERROR", "Start date must be before end date");
                request.getRequestDispatcher("AdminSalesReport.html").forward(request, response);
                return;
            }
            
            List<ProductDAO.SalesReport> salesReports = productDAO.getSalesReportByPeriod(startDate, endDate);
            BigDecimal totalSales = productDAO.getTotalSalesByPeriod(startDate, endDate);
            
            request.setAttribute("SALES_REPORTS", salesReports);
            request.setAttribute("TOTAL_SALES", totalSales);
            request.setAttribute("START_DATE", startDateStr);
            request.setAttribute("END_DATE", endDateStr);
            request.setAttribute("VIEW_MODE", "sales_report");
            
            LOGGER.log(Level.INFO, "Sales report generated for period: {0} to {1}", 
                      new Object[]{startDateStr, endDateStr});
            request.getRequestDispatcher("AdminSalesReport.html").forward(request, response);
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("ERROR", "Invalid date format. Please use YYYY-MM-DD format");
            request.getRequestDispatcher("AdminSalesReport.html").forward(request, response);
        }
    }
    
    // ==================== USER FUNCTIONS (Functions 08-10) ====================
    
    /**
     * Function 08: User - Search pizza by name and price
     */
    private void handleSearchPizza(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String searchName = request.getParameter("searchName");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        
        List<Product> products = new ArrayList<>();
        
        // Search by name
        if (searchName != null && !searchName.trim().isEmpty()) {
            List<Product> nameResults = productDAO.searchProductsByName(searchName.trim());
            products.addAll(nameResults);
        }
        
        // Search by price range
        if (minPriceStr != null && !minPriceStr.trim().isEmpty() &&
            maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                BigDecimal minPrice = new BigDecimal(minPriceStr.trim());
                BigDecimal maxPrice = new BigDecimal(maxPriceStr.trim());
                
                if (minPrice.compareTo(BigDecimal.ZERO) >= 0 && maxPrice.compareTo(minPrice) >= 0) {
                    List<Product> priceResults = productDAO.searchProductsByPrice(minPrice, maxPrice);
                    
                    // If we have both name and price search, find intersection
                    if (searchName != null && !searchName.trim().isEmpty()) {
                        products.retainAll(priceResults);
                    } else {
                        products.addAll(priceResults);
                    }
                }
            } catch (NumberFormatException e) {
                request.setAttribute("ERROR", "Invalid price format");
            }
        }
        
        // Remove duplicates and filter available products for customers
        User currentUser = getCurrentUser(request);
        if (currentUser != null && !currentUser.isStaff()) {
            products = products.stream()
                    .filter(Product::isAvailable)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (products.isEmpty()) {
            request.setAttribute("MESSAGE", "No pizzas found matching your search criteria");
        } else {
            request.setAttribute("LIST_PRODUCT", products);
            request.setAttribute("SEARCH_TERM", "Search Results");
        }
        
        LOGGER.log(Level.INFO, "User searched pizzas: {0} results", products.size());
        request.getRequestDispatcher("Shopping.html").forward(request, response);
    }
    
    /**
     * Function 09: User - View pizza list
     */
    private void handleViewPizzaListUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User currentUser = getCurrentUser(request);
        List<Product> products;
        
        // If staff, show all products; if customer, show only available products
        if (currentUser != null && currentUser.isStaff()) {
            products = productDAO.getAllProducts();
            request.setAttribute("SEARCH_TERM", "All Pizzas (Staff View)");
        } else {
            products = productDAO.getAvailableProducts();
            request.setAttribute("SEARCH_TERM", "Available Pizzas");
        }
        
        if (products.isEmpty()) {
            request.setAttribute("MESSAGE", "No pizzas available");
        } else {
            request.setAttribute("LIST_PRODUCT", products);
        }
        
        LOGGER.log(Level.INFO, "User viewed pizza list: {0} products", products.size());
        request.getRequestDispatcher("Shopping.html").forward(request, response);
    }
    
    /**
     * Function 10: User - Cart functionality (view, update, remove)
     */
    private void handleUpdateCartItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String productIDStr = request.getParameter("productID");
        String quantityStr = request.getParameter("quantity");
        
        if (productIDStr == null || productIDStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Product ID is required");
            handleViewCart(request, response);
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDStr.trim());
            int quantity = Integer.parseInt(quantityStr != null ? quantityStr.trim() : "1");
            
            if (quantity <= 0) {
                request.setAttribute("ERROR", "Quantity must be greater than 0");
                handleViewCart(request, response);
                return;
            }
            
            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("SHOPPING_CART");
            
            if (cart != null) {
                for (CartItem item : cart) {
                    if (item.getProductID() == productID) {
                        item.setQuantity(quantity);
                        break;
                    }
                }
                session.setAttribute("SHOPPING_CART", cart);
                request.setAttribute("MESSAGE", "Cart item updated successfully!");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid number format");
        }
        
        handleViewCart(request, response);
    }
    
    private void handleRemoveCartItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String productIDStr = request.getParameter("productID");
        
        if (productIDStr == null || productIDStr.trim().isEmpty()) {
            request.setAttribute("ERROR", "Product ID is required");
            handleViewCart(request, response);
            return;
        }
        
        try {
            int productID = Integer.parseInt(productIDStr.trim());
            
            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("SHOPPING_CART");
            
            if (cart != null) {
                cart.removeIf(item -> item.getProductID() == productID);
                session.setAttribute("SHOPPING_CART", cart);
                request.setAttribute("MESSAGE", "Item removed from cart successfully!");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR", "Invalid product ID format");
        }
        
        handleViewCart(request, response);
    }
    
    // ==================== HELPER METHODS ====================
    
    private boolean isAdminUser(HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        return currentUser != null && currentUser.isAdmin();
    }
    
    private boolean isValidProductInput(String productName, String supplierIDStr, 
                                      String categoryIDStr, String unitPriceStr, String unitsInStockStr) {
        return productName != null && !productName.trim().isEmpty() &&
               supplierIDStr != null && !supplierIDStr.trim().isEmpty() &&
               categoryIDStr != null && !categoryIDStr.trim().isEmpty() &&
               unitPriceStr != null && !unitPriceStr.trim().isEmpty() &&
               unitsInStockStr != null && !unitsInStockStr.trim().isEmpty();
    }
    
    private void addToCart(HttpServletRequest request, Product product, int quantity) {
        HttpSession session = request.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("SHOPPING_CART");
        
        if (cart == null) {
            cart = new ArrayList<>();
        }
        
        // Check if product already in cart
        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProductID() == product.getProductID()) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        
        if (!found) {
            CartItem newItem = new CartItem();
            newItem.setProductID(product.getProductID());
            newItem.setProductName(product.getProductName());
            newItem.setUnitPrice(product.getUnitPrice());
            newItem.setQuantity(quantity);
            cart.add(newItem);
        }
        
        session.setAttribute("SHOPPING_CART", cart);
    }
    
    private BigDecimal calculateCartTotal(List<CartItem> cart) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (CartItem item : cart) {
            BigDecimal itemTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
            total = total.add(itemTotal);
        }
        
        return total;
    }
    
    private boolean isUserLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User loginUser = (User) session.getAttribute("LOGIN_USER");
            return loginUser != null;
        }
        return false;
    }
    
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("LOGIN_USER");
        }
        return null;
    }
    
    @Override
    public void destroy() {
        super.destroy();
        LOGGER.info("ShoppingServlet destroyed");
    }
    
    // Inner class for cart items
    public static class CartItem implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        
        private int productID;
        private String productName;
        private BigDecimal unitPrice;
        private int quantity;
        
        public CartItem() {
            this.unitPrice = BigDecimal.ZERO;
        }
        
        public int getProductID() { 
            return productID; 
        }
        
        public void setProductID(int productID) { 
            this.productID = productID; 
        }
        
        public String getProductName() { 
            return productName; 
        }
        
        public void setProductName(String productName) { 
            this.productName = productName; 
        }
        
        public BigDecimal getUnitPrice() { 
            return unitPrice; 
        }
        
        public void setUnitPrice(BigDecimal unitPrice) { 
            this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO; 
        }
        
        public int getQuantity() { 
            return quantity; 
        }
        
        public void setQuantity(int quantity) { 
            this.quantity = quantity; 
        }
        
        public BigDecimal getSubtotal() {
            return unitPrice.multiply(new BigDecimal(quantity));
        }
        
        public String getFormattedPrice() {
            return unitPrice != null ? String.format("$%.2f", unitPrice) : "$0.00";
        }
        
        @Override
        public String toString() {
            return "CartItem{" +
                    "productID=" + productID +
                    ", productName='" + productName + '\'' +
                    ", quantity=" + quantity +
                    ", subtotal=" + getSubtotal() +
                    '}';
        }
    }
}