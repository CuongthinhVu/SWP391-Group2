package controller;

import dao.AdminPaymentDAO;
import model.Payment;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/refunds")
public class AdminRefundServlet extends HttpServlet {
    private AdminPaymentDAO paymentDAO = new AdminPaymentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filterStatus = request.getParameter("status");

        List<Payment> refunds;
        if (filterStatus != null && !filterStatus.isEmpty()) {
            refunds = paymentDAO.getRefundsByStatus(filterStatus);
            request.setAttribute("selectedStatus", filterStatus);
        } else {
            refunds = paymentDAO.getAllRefunds();
        }

        request.setAttribute("pendingRefunds", refunds);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/AdminRefund.jsp");
        dispatcher.forward(request, response);
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int paymentId = Integer.parseInt(request.getParameter("paymentId"));
        String refundStatus = request.getParameter("refundStatus");

        if (paymentDAO.updateRefundStatus(paymentId, refundStatus)) {
            response.sendRedirect("refunds");
        } else {
            request.setAttribute("errorMessage", "Cập nhật trạng thái hoàn tiền thất bại!");
            doGet(request, response);
        }
    }
}
