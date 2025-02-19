
package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Ticket;
import model.User;
import java.io.IOException;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebServlet(name = "TicketServlet", urlPatterns = {"/ticket/*"})
public class TicketServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();

        switch (action) {
            case "/history":
                showBookingHistory(request, response);
                break;
            case "/view":
                viewTicket(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();

        switch (action) {
            case "/cancel":
                cancelTicket(request, response);
                break;
            case "/modify":
                modifyTicket(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showBookingHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        List<Ticket> tickets = TicketDAO.getTicketsByUser(user.getUserId());
        request.setAttribute("tickets", tickets);
        request.getRequestDispatcher("/WEB-INF/views/ticket/history.jsp").forward(request, response);
    }

    private void viewTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ticketId = Integer.parseInt(request.getParameter("id"));
        Ticket ticket = TicketDAO.getTicketById(ticketId);
        request.setAttribute("ticket", ticket);
        request.getRequestDispatcher("/WEB-INF/views/ticket/view.jsp").forward(request, response);
    }

    private void cancelTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ticketId = Integer.parseInt(request.getParameter("id"));
        TicketDAO.updateTicketStatus(ticketId, "Cancelled");
        response.sendRedirect(request.getContextPath() + "/ticket/history");
    }

    private void modifyTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int ticketId = Integer.parseInt(request.getParameter("id"));
        int newSeatId = Integer.parseInt(request.getParameter("seatId"));
        TicketDAO.updateTicketSeat(ticketId, newSeatId);
        response.sendRedirect(request.getContextPath() + "/ticket/view?id=" + ticketId);
    }
}