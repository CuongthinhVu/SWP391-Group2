package controller;

import dao.AdminTicketDAO;
import model.Ticket;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/tickets")
public class AdminTicketServlet extends HttpServlet {
    private AdminTicketDAO ticketDAO = new AdminTicketDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Ticket> tickets = ticketDAO.getAllSoldTickets();
        request.setAttribute("tickets", tickets);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/AdminSoldTickets.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String ticketIdStr = request.getParameter("ticketId");
            String status = request.getParameter("status");

            if (ticketIdStr == null || ticketIdStr.isEmpty() || status == null || status.isEmpty()) {
                request.setAttribute("errorMessage", "Thiếu thông tin vé hoặc trạng thái.");
                doGet(request, response);
                return;
            }

            int ticketId = Integer.parseInt(ticketIdStr);

            if (ticketDAO.updateTicketStatus(ticketId, status)) {
                response.sendRedirect("tickets");
            } else {
                request.setAttribute("errorMessage", "Cập nhật trạng thái vé thất bại!");
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Lỗi: ID vé không hợp lệ.");
            doGet(request, response);
        }
    }

}
