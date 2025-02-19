
package dao;

import model.Ticket;
import model.User;
import model.BusTrip;
import model.Seat;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TicketDAO {
    //day la ticket, bien ticket chuyen vao day de xac nhan user dat hay ko dat ve xe buyt
    public static List<Ticket> getTicketsByUser(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setUser(UserDAO.getUserById(rs.getInt("user_id")));
                ticket.setTrip(BusTripDAO.getTripById(rs.getInt("trip_id")));
                ticket.setSeat(SeatDAO.getSeatById(rs.getInt("seat_id")));
                ticket.setPrice(rs.getBigDecimal("price"));
                ticket.setStatus(rs.getString("status"));
                ticket.setPurchaseDate(rs.getTimestamp("purchase_date").toLocalDateTime());
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public static Ticket getTicketById(int ticketId) {
        String sql = "SELECT * FROM Tickets WHERE ticket_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setUser(UserDAO.getUserById(rs.getInt("user_id")));
                ticket.setTrip(BusTripDAO.getTripById(rs.getInt("trip_id")));
                ticket.setSeat(SeatDAO.getSeatById(rs.getInt("seat_id")));
                ticket.setPrice(rs.getBigDecimal("price"));
                ticket.setStatus(rs.getString("status"));
                ticket.setPurchaseDate(rs.getTimestamp("purchase_date").toLocalDateTime());
                return ticket;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateTicketStatus(int ticketId, String status) {
        String sql = "UPDATE Tickets SET status = ? WHERE ticket_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, ticketId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateTicketSeat(int ticketId, int newSeatId) {
        String sql = "UPDATE Tickets SET seat_id = ? WHERE ticket_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newSeatId);
            stmt.setInt(2, ticketId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}