package controller;

import dao.PopularRoutesDAO;
import dao.PromotionsDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Promotion;
import model.Route;

@WebServlet(name="HomeServlet", urlPatterns="/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        PopularRoutesDAO popularRoutesDAO = new PopularRoutesDAO();
        try {
            List<Route> popularRoutes = popularRoutesDAO.getPopularRoutes();
            if (popularRoutes.isEmpty()) {
        System.out.println("⚠ Không có tuyến xe phổ biến nào để hiển thị.");
    } else {
        System.out.println("✅ Số tuyến xe phổ biến: " + popularRoutes.size());
    }
            HttpSession session = request.getSession();
            PromotionsDAO dao = new PromotionsDAO();
List<Promotion> promotions = dao.getAllPromotions();
session.setAttribute("promotions", promotions);

            session.setAttribute("popularRoutes", popularRoutes);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching popular routes.");
        }
    } 

    @Override
    public String getServletInfo() {
        return "Servlet tự động hiển thị dữ liệu các tuyến đường phổ biến khi truy cập trang chủ";
    }
}
