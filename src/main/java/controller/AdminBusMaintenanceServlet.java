package controller;

import dao.AdminBusMaintenanceDAO;
import model.BusMaintenanceLog;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/admin/bus-maintenance")
public class AdminBusMaintenanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminBusMaintenanceDAO maintenanceDAO;

    @Override
    public void init() throws ServletException {
        maintenanceDAO = new AdminBusMaintenanceDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) action = "list";

        switch (action) {
            case "insert-form":
                showInsertForm(request, response);
                break;
            case "list":
                listMaintenanceLogs(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteMaintenanceLog(request, response);
                break;
            default:
                listMaintenanceLogs(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {

            case "insert":
                insertMaintenanceLog(request, response);
                break;
            case "update":
                updateMaintenanceLog(request, response);
                break;
            default:
                response.sendRedirect("bus-maintenance?action=list");
                break;
        }
    }

    // 1. Hiển thị danh sách bảo trì của một xe buýt
    private void listMaintenanceLogs(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String busIdParam = request.getParameter("busId");

        // Kiểm tra nếu busId bị null hoặc rỗng
        if (busIdParam == null || busIdParam.isEmpty()) {
            response.sendRedirect("admin/bus?action=list");  // Điều hướng về danh sách xe buýt nếu thiếu busId
            return;
        }

        int busId;
        try {
            busId = Integer.parseInt(busIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("admin/bus?action=list"); // Nếu lỗi parse, quay về danh sách xe buýt
            return;
        }

        List<BusMaintenanceLog> maintenanceLogs = maintenanceDAO.getMaintenanceLogsByBusId(busId);
        request.setAttribute("maintenanceLogs", maintenanceLogs);
        request.setAttribute("busId", busId);
        request.getRequestDispatcher("/admin/bus-maintenance.jsp").forward(request, response);
    }

    private void showInsertForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String busId = request.getParameter("busId");
        if (busId == null || busId.isEmpty()) {
            response.sendRedirect("admin/bus?action=list");
            return;
        }
        request.setAttribute("busId", busId);
        request.getRequestDispatcher("/admin/bus-maintenance-form.jsp").forward(request, response);
    }



    // 2. Hiển thị form sửa bảo trì
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String logIdStr = request.getParameter("logId");

        if (logIdStr == null || logIdStr.isEmpty()) {
            response.sendRedirect("bus-maintenance?action=list");
            return;
        }

        int logId;
        try {
            logId = Integer.parseInt(logIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("bus-maintenance?action=list");
            return;
        }

        BusMaintenanceLog log = maintenanceDAO.getMaintenanceLogById(logId);

        if (log == null) {
            response.sendRedirect("bus-maintenance?action=list");
            return;
        }

        request.setAttribute("log", log);
        request.getRequestDispatcher("/admin/bus-maintenance-form.jsp").forward(request, response);
    }


    // 3. Thêm bảo trì mới
    private void insertMaintenanceLog(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int busId = Integer.parseInt(request.getParameter("busId"));
        LocalDateTime maintenanceDate = LocalDateTime.parse(request.getParameter("maintenanceDate"));
        String description = request.getParameter("description");
        BigDecimal cost = new BigDecimal(request.getParameter("cost"));
        String status = request.getParameter("status");

        BusMaintenanceLog log = new BusMaintenanceLog(0, busId, maintenanceDate, description, cost, status);
        maintenanceDAO.insertMaintenanceLog(log);


        maintenanceDAO.updateLastMaintenanceDate(busId); // Gọi thêm dòng này


        response.sendRedirect("bus-maintenance?action=list&busId=" + busId);
    }

    // 4. Cập nhật thông tin bảo trì
    private void updateMaintenanceLog(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int logId = Integer.parseInt(request.getParameter("logId"));
        LocalDateTime maintenanceDate = LocalDateTime.parse(request.getParameter("maintenanceDate"));
        String description = request.getParameter("description");
        BigDecimal cost = new BigDecimal(request.getParameter("cost"));
        String status = request.getParameter("status");

        BusMaintenanceLog log = new BusMaintenanceLog(logId, 0, maintenanceDate, description, cost, status);
        maintenanceDAO.updateMaintenanceLog(log);


        maintenanceDAO.updateLastMaintenanceDate(log.getBusId()); // Gọi thêm dòng này


        response.sendRedirect(request.getContextPath() + "/admin/bus?action=list");

    }

    // 5. Xóa bảo trì
    private void deleteMaintenanceLog(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int logId = Integer.parseInt(request.getParameter("logId"));
        maintenanceDAO.deleteMaintenanceLog(logId);

        int busId = maintenanceDAO.getMaintenanceLogById(logId).getBusId(); // Lấy busId trước khi xóa

        maintenanceDAO.updateLastMaintenanceDate(busId); // Cập nhật sau khi xóa


        response.sendRedirect(request.getContextPath() + "/admin/bus?action=list");

    }
}
