
package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "UserServlet", urlPatterns = {"/user/*"})
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            action = "/profile";
        }

        switch (action) {
            case "/profile":
                viewProfile(request, response);
                break;
            case "/edit":
                showEditForm(request, response);
                break;
            case "/changePassword":
                showChangePasswordForm(request, response);
                break;
            case "/logout":
                logout(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/user/profile");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/user/profile");
            return;
        }

        switch (action) {
            case "/update":
                updateProfile(request, response);
                break;
            case "/changePassword":
                changePassword(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/user/profile");
                break;
        }
    }

    private void viewProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/user/profile.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/user/edit.jsp").forward(request, response);
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE User SET fullName = ?, email = ?, phone = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, user.getId());

            int result = stmt.executeUpdate();
            if (result > 0) {
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhone(phone);
                session.setAttribute("user", user);
                request.setAttribute("message", "Profile updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update profile!");
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/user/profile");
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New passwords do not match!");
            request.getRequestDispatcher("/WEB-INF/views/user/changePassword.jsp").forward(request, response);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verify current password
            String verifySQL = "SELECT password FROM User WHERE id = ? AND password = ?";
            PreparedStatement verifyStmt = conn.prepareStatement(verifySQL);
            verifyStmt.setInt(1, user.getId());
            verifyStmt.setString(2, currentPassword);
            ResultSet rs = verifyStmt.executeQuery();

            if (!rs.next()) {
                request.setAttribute("error", "Current password is incorrect!");
                request.getRequestDispatcher("/WEB-INF/views/user/changePassword.jsp").forward(request, response);
                return;
            }

            // Update password
            String updateSQL = "UPDATE User SET password = ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
            updateStmt.setString(1, newPassword);
            updateStmt.setInt(2, user.getId());

            int result = updateStmt.executeUpdate();
            if (result > 0) {
                request.setAttribute("message", "Password changed successfully!");
            } else {
                request.setAttribute("error", "Failed to change password!");
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/user/profile");
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private void showChangePasswordForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/user/changePassword.jsp").forward(request, response);
    }
}