package controller;

import dao.UserDAO;
import model.User;
import util.PasswordUtils;
import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usernameOrEmail = request.getParameter("usernameOrEmail");
        String password = request.getParameter("password");

        UserDAO userDAO;
        try {
            userDAO = new UserDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        User user = userDAO.findByUsernameOrEmail(usernameOrEmail);

        if (user == null || !PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
            request.setAttribute("error", "Sai tài khoản hoặc mật khẩu!");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        // Kiểm tra nếu tài khoản bị khóa
        if (!user.isActive()) {
            request.setAttribute("error", "Tài khoản của bạn đã bị khóa. Lý do: " + user.getStatusReason());
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("role_id", user.getRoleId()); // Lưu role_id vào session


        if (user.getRoleId() == 1 || user.getRoleId() == 2) {
            response.sendRedirect("admin/dashboard");
        } else {
            response.sendRedirect("index.jsp");
        }

    }
}
