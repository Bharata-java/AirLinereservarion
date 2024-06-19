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

@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {
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
        String flightId = request.getParameter("flight_id");
        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("user");

        if (userName == null) {
            response.getWriter().println("Please login first.");
            return;
        }

        try  {
            String sqlUserId = "SELECT id FROM users WHERE name = ?";
            PreparedStatement pstmtUserId = conn.prepareStatement(sqlUserId);
            pstmtUserId.setString(1, userName);
            ResultSet rsUser = pstmtUserId.executeQuery();
            int userId = 0;
            if (rsUser.next()) {
                userId = rsUser.getInt("id");
            }

            String sql = "INSERT INTO bookings (user_id, flight_id) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, flightId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                response.getWriter().println("Booking successful!");
            } else {
                response.getWriter().println("Booking failed. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}

