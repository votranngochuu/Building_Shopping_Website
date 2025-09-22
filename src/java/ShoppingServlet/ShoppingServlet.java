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
                case "ViewCart":
                    handleViewCart(request, response);
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
            request.setAttribute("MESSAGE", "Your cart is empty");
        } else {
            request.setAttribute("CART_ITEMS", cart);
            request.setAttribute("CART_TOTAL", calculateCartTotal(cart));
        }
        
        request.setAttribute("VIEW_MODE", "cart");
        request.getRequestDispatcher("Shopping.html").forward(request, response);
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