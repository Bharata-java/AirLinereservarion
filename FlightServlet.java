package controller;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/FlightServlet")
public class FlightServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    static Connection conn;
    
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
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String date = request.getParameter("date");

        try {
            String sql = "SELECT * FROM flights WHERE from_location = ? AND to_location = ? AND DATE(departure_time) = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setString(3, date);
            ResultSet result = pstmt.executeQuery();
 
            PrintWriter pw=response.getWriter();
            
          response.setContentType("text/html");
      pw.println("<html><body><h2>Flight Results</h2>");
            response.getWriter().println("<table border='1'><tr><th>Flight Number</th><th>From</th><th>To</th><th>Departure Time</th><th>Arrival Time</th><th>Seats Available</th><th>Action</th></tr>");

            while (result.next()) {
             pw.println("<tr>");
               pw.println("<td>" + result.getString("flight_number") + "</td>");
      pw.println("<td>" + result.getString("from_location") + "</td>");
         pw.println("<td>" + result.getString("to_location") + "</td>");
              pw.println("<td>" + result.getTimestamp("departure_time") + "</td>");
              pw.println("<td>" + result.getTimestamp("arrival_time") + "</td>");
               pw.println("<td>" + result.getInt("seats_available") + "</td>");
            pw.println("<td><form action='BookingServlet' method='post'><input type='hidden' name='flightId' value='" + result.getInt("id") + "'/><input type='submit' value='Book'/></form></td>");
               pw.println("</tr>");
            }

            response.getWriter().println("</table></body></html>");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("An error occurred. Please try again.");
        }
    }
}
