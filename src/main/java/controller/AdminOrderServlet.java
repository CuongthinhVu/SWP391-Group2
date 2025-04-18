package controller;

import dao.AdminOrderDAO;
import model.Order;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {
    private AdminOrderDAO orderDAO = new AdminOrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String search = request.getParameter("search");
        List<Order> orders;

        if (search != null && !search.trim().isEmpty()) {
            orders = orderDAO.searchOrdersByCustomerName(search);
        } else {
            orders = orderDAO.getAllOrders();
        }

        request.setAttribute("orders", orders);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/AdminOrders.jsp");
        dispatcher.forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        String status = request.getParameter("status");

        if (orderDAO.updateOrderStatus(orderId, status)) {
            response.sendRedirect("orders");
        } else {
            request.setAttribute("errorMessage", "Cập nhật trạng thái đơn hàng thất bại!");
            doGet(request, response);
        }
    }
}
