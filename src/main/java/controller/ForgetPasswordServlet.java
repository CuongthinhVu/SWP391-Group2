package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;
import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgetPasswordServlet extends HttpServlet {
    private UserService userService = new UserService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("sendCode".equals(action)) {
            sendResetCode(request, response);
        } else if ("resetPassword".equals(action)) {
            resetPassword(request, response);
        }
    }

    private void sendResetCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");

        if (userService.sendPasswordResetCode(email)) {
            request.setAttribute("message", "Mã xác nhận đã được gửi đến email của bạn. Có hiệu lực trong 10 phút.");
        } else {
            request.setAttribute("message", "Email không tồn tại trong hệ thống.");
        }
        request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
    }

    private void resetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String code = request.getParameter("code");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("message", "Mật khẩu nhập lại không khớp.");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            return;
        }

        if (userService.resetPassword(email, code, newPassword)) {
            request.setAttribute("message", "Mật khẩu đã được cập nhật thành công.");
        } else {
            request.setAttribute("message", "Mã xác nhận không đúng hoặc đã hết hạn.");
        }
        request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
    }
}
