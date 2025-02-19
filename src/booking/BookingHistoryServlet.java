
package servlet.booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Ticket;
import dao.TicketDAO;
import java.io.IOException;
import java.util.List;

@WebServlet("/booking-history")
public class BookingHistoryServlet extends HttpServlet {
    private TicketDAO ticketDAO;

    @Override
    public void init() throws ServletException {
        ticketDAO = new TicketDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        List<Ticket> tickets = ticketDAO.getTicketsByUserId(userId);
        request.setAttribute("tickets", tickets);
        request.getRequestDispatcher("/WEB-INF/views/booking-history.jsp").forward(request, response);
    }
}
