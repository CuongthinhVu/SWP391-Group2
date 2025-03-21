package controller;

import dao.BookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Ticket;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/modify-booking")
public class ModifyBookingServlet extends HttpServlet {
    private BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy tham số từ request
        String orderDetailIdStr = request.getParameter("orderDetailId");
        String newSeatIdStr = request.getParameter("newSeatId");
        String newTripIdStr = request.getParameter("newTripId");
        String newPriceStr = request.getParameter("newPrice");

        int orderDetailId;
        Integer newSeatId = null;
        Integer newTripId = null;
        BigDecimal newPrice = null;

        try {
            orderDetailId = Integer.parseInt(orderDetailIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid orderDetailId");
            return;
        }
        if (newSeatIdStr != null && !newSeatIdStr.trim().isEmpty()) {
            try {
                newSeatId = Integer.parseInt(newSeatIdStr);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid newSeatId");
                return;
            }
        }
        if (newTripIdStr != null && !newTripIdStr.trim().isEmpty()) {
            try {
                newTripId = Integer.parseInt(newTripIdStr);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid newTripId");
                return;
            }
        }
        if (newPriceStr != null && !newPriceStr.trim().isEmpty()) {
            try {
                newPrice = new BigDecimal(newPriceStr);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid newPrice");
                return;
            }
        }

        try {
            // Gọi hàm DAO để sửa đổi booking
            Ticket updatedTicket = bookingDAO.modifyBooking(orderDetailId, newSeatId, newTripId, newPrice);

            // Trả về kết quả dưới dạng JSON chỉ với thông tin cơ bản của Ticket
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = String.format("{\"ticketId\": %d, \"price\": \"%s\", \"status\": \"%s\"}",
                    updatedTicket.getTicketId(),
                    updatedTicket.getPrice().toString(),
                    updatedTicket.getStatus());
            response.getWriter().write(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error modifying booking: " + e.getMessage());
        }
    }
}