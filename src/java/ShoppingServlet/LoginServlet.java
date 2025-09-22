package ShoppingServlet;

import DBUtils.User;
import DBUtils.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Login Servlet - Enhanced for PizzaStore Shopping Website
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        LOGGER.info("LoginServlet initialized successfully");
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
        
        String action = request.getParameter("action");
        
        try {
            if ("Login".equals(action)) {
                handleLogin(request, response);
            } else if ("Register".equals(action)) {
                handleRegistration(request, response);
            } else if ("Logout".equals(action)) {
                handleLogout(request, response);
            } else {
                // Default: show login page
                showLoginPage(request, response);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in LoginServlet", e);
            request.setAttribute("ERROR", "System error occurred. Please try again.");
            showLoginPage(request, response);
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String userID = request.getParameter("userID");
        String password = request.getParameter("password");
        
        // Validate input
        if (!isValidLoginInput(userID, password)) {
            request.setAttribute("ERROR", "User ID and password are required");
            showLoginPage(request, response);
            return;
        }
        
        // Clean input
        userID = userID.trim();
        password = password.trim();
        
        // Authenticate user
        User user = userDAO.authenticateUser(userID, password);
        
        if (user != null) {
            // Login successful
            LOGGER.log(Level.INFO, "User logged in successfully: {0}", userID);
            
            // Create session
            HttpSession session = request.getSession(true);
            user.clearSensitiveData(); // Remove password for security
            session.setAttribute("LOGIN_USER", user);
            session.setAttribute("LOGIN_TIME", System.currentTimeMillis());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            // Redirect based on user type
            if (user.isStaff()) {
                // Staff/Admin goes to product management
                response.sendRedirect("Shopping.html");
            } else {
                // Customers go to shopping page
                response.sendRedirect("Shopping.html");
            }
            
        } else {
            // Login failed
            LOGGER.log(Level.WARNING, "Failed login attempt for userID: {0}", userID);
            request.setAttribute("ERROR", "Invalid User ID or Password");
            showLoginPage(request, response);
        }
    }
    
    private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String userID = request.getParameter("userID");
        String fullName = request.getParameter("fullName");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        
        // Validate input
        String validationError = validateRegistrationInput(userID, fullName, password, confirmPassword, email);
        if (validationError != null) {
            request.setAttribute("ERROR", validationError);
            preserveRegistrationInput(request, userID, fullName, email);
            showLoginPage(request, response);
            return;
        }
        
        // Clean input
        userID = userID.trim();
        fullName = fullName.trim();
        password = password.trim();
        email = email.trim();
        
        // Check if user ID already exists
        if (userDAO.userExists(userID)) {
            request.setAttribute("ERROR", "User ID already exists. Please choose a different one.");
            preserveRegistrationInput(request, userID, fullName, email);
            showLoginPage(request, response);
            return;
        }
        
        // Create new user (default role = "US" for customer)
        User newUser = new User(userID, fullName, "US", password);
        
        if (userDAO.insertUser(newUser)) {
            LOGGER.log(Level.INFO, "New user registered successfully: {0}", userID);
            request.setAttribute("MESSAGE", "Registration successful! Please login with your credentials.");
            showLoginPage(request, response);
        } else {
            request.setAttribute("ERROR", "Registration failed. Please try again.");
            preserveRegistrationInput(request, userID, fullName, email);
            showLoginPage(request, response);
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("LOGIN_USER");
            if (user != null) {
                LOGGER.log(Level.INFO, "User logged out: {0}", user.getUserID());
            }
            session.invalidate();
        }
        
        request.setAttribute("MESSAGE", "You have been logged out successfully");
        showLoginPage(request, response);
    }
    
    private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("Login.html").forward(request, response);
    }
    
    private boolean isValidLoginInput(String userID, String password) {
        return userID != null && !userID.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
    
    private String validateRegistrationInput(String userID, String fullName, 
                                           String password, String confirmPassword, String email) {
        
        // Check for null or empty fields
        if (userID == null || userID.trim().isEmpty()) {
            return "User ID is required";
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name is required";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password is required";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        
        // Check field lengths
        if (userID.trim().length() > 50) {
            return "User ID must be 50 characters or less";
        }
        if (fullName.trim().length() > 200) {
            return "Full name must be 200 characters or less";
        }
        
        // Check password strength
        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }
        if (password.length() > 100) {
            return "Password must be 100 characters or less";
        }
        
        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        
        // Check email format (basic validation)
        if (!email.contains("@") || !email.contains(".")) {
            return "Please enter a valid email address";
        }
        
        // Check for invalid characters in userID
        if (!userID.matches("^[a-zA-Z0-9_-]+$")) {
            return "User ID can only contain letters, numbers, underscore, and hyphen";
        }
        
        return null; // No validation errors
    }
    
    private void preserveRegistrationInput(HttpServletRequest request, String userID, 
                                         String fullName, String email) {
        request.setAttribute("regUserID", userID);
        request.setAttribute("regFullName", fullName);
        request.setAttribute("regEmail", email);
    }
    
    @Override
    public void destroy() {
        super.destroy();
        LOGGER.info("LoginServlet destroyed");
    }
}