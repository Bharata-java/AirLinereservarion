package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

    private static Connection conn;
    
    public void init(ServletConfig config)
    {
    try {
    Class.forName("oracle.jdbc.driver.OracleDriver");
    conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost :1521:xe","system","tiger");
    } catch (Exception e) 
    {
   e.printStackTrace();
    }
    
    }
    

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String action = request.getParameter("action");
        if (action.equals("register")) {
            registerUser(request, response);
        } else if (action.equals("login")) {
            loginUser(request, response);
        }
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
     
        try {
            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                response.sendRedirect("login.html");
            } else {
                response.getWriter().println("Registration failed. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<h1 bgcolor='black'> Please provide new User this User Name All ready exist<h1>");
        }
    }

    private void loginUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try  {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("user", rs.getString("name"));
                response.sendRedirect("flights.html");
            } else {
                response.getWriter().println("Invalid email or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<h1 bgcolor='black'> Please provide valid  Credidantals <h1>");
        }
    }
}
