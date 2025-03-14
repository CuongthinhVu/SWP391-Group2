package dao;

import model.BusTrip;
import model.Route;
import model.Bus;
import model.User;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminBusTripDAO {

    // Lấy danh sách tất cả chuyến xe
    public List<BusTrip> getAllBusTrips() {
        List<BusTrip> busTrips = new ArrayList<>();
        String sql = "SELECT * FROM BusTrips";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BusTrip trip = extractBusTripFromResultSet(rs);
                busTrips.add(trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busTrips;
    }

    // Lấy thông tin một chuyến xe theo ID
    public BusTrip getBusTripById(int tripId) {
        String sql = "SELECT * FROM BusTrips WHERE trip_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBusTripFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm một chuyến xe mới
    public boolean addBusTrip(BusTrip trip) {
        String sql = "INSERT INTO BusTrips (route_id, bus_id, driver_id, departure_time, arrival_time, status, available_seats, current_price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trip.getRoute().getRouteId());
            ps.setInt(2, trip.getBus().getBusId());
            ps.setInt(3, trip.getDriver().getUserId());
            ps.setTimestamp(4, Timestamp.valueOf(trip.getDepartureTime()));
            ps.setTimestamp(5, trip.getArrivalTime() != null ? Timestamp.valueOf(trip.getArrivalTime()) : null);
            ps.setString(6, trip.getStatus());
            ps.setInt(7, trip.getAvailableSeats());
            ps.setBigDecimal(8, trip.getCurrentPrice());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thông tin chuyến xe
    public boolean updateBusTrip(BusTrip trip) {
        String sql = "UPDATE BusTrips SET route_id = ?, bus_id = ?, driver_id = ?, departure_time = ?, arrival_time = ?, status = ?, " +
                "available_seats = ?, current_price = ? WHERE trip_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trip.getRoute().getRouteId());
            ps.setInt(2, trip.getBus().getBusId());
            ps.setInt(3, trip.getDriver().getUserId());
            ps.setTimestamp(4, Timestamp.valueOf(trip.getDepartureTime()));
            ps.setTimestamp(5, trip.getArrivalTime() != null ? Timestamp.valueOf(trip.getArrivalTime()) : null);
            ps.setString(6, trip.getStatus());
            ps.setInt(7, trip.getAvailableSeats());
            ps.setBigDecimal(8, trip.getCurrentPrice());
            ps.setInt(9, trip.getTripId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa một chuyến xe
    public boolean deleteBusTrip(int tripId) {
        String sql = "DELETE FROM BusTrips WHERE trip_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Phương thức hỗ trợ để tạo đối tượng BusTrip từ ResultSet
    private BusTrip extractBusTripFromResultSet(ResultSet rs) throws SQLException {
        BusTrip trip = new BusTrip();
        trip.setTripId(rs.getInt("trip_id"));

        Route route = new Route();
        route.setRouteId(rs.getInt("route_id"));
        trip.setRoute(route);

        Bus bus = new Bus();
        bus.setBusId(rs.getInt("bus_id"));
        trip.setBus(bus);

        User driver = new User();
        driver.setUserId(rs.getInt("driver_id"));
        trip.setDriver(driver);

        trip.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        if (rs.getTimestamp("arrival_time") != null) {
            trip.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
        }
        trip.setStatus(rs.getString("status"));
        trip.setCurrentPrice(rs.getBigDecimal("current_price"));
        trip.setDelayReason(rs.getString("delay_reason"));

        // Tính số ghế còn lại
        trip.setAvailableSeats(getAvailableSeats(trip.getTripId(), trip.getBus().getBusId()));

        return trip;
    }


    public List<User> getAllDrivers() {
        List<User> drivers = new ArrayList<>();
        String sql = "SELECT user_id, full_name FROM Users WHERE role_id = 4 AND is_active = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User driver = new User();
                driver.setUserId(rs.getInt("user_id"));
                driver.setFullName(rs.getString("full_name"));
                drivers.add(driver);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
    }


    public List<BusTrip> searchBusTrips(String route, String driver, int page, int pageSize) {
        List<BusTrip> busTrips = new ArrayList<>();
        String sql = "SELECT * FROM BusTrips WHERE 1=1";

        if (route != null && !route.isEmpty()) {
            sql += " AND route_id LIKE ?";
        }
        if (driver != null && !driver.isEmpty()) {
            sql += " AND driver_id IN (SELECT user_id FROM Users WHERE full_name LIKE ?)";
        }
        sql += " LIMIT ?, ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int index = 1;
            if (route != null && !route.isEmpty()) {
                ps.setString(index++, "%" + route + "%");
            }
            if (driver != null && !driver.isEmpty()) {
                ps.setString(index++, "%" + driver + "%");
            }
            ps.setInt(index++, (page - 1) * pageSize);
            ps.setInt(index, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    busTrips.add(extractBusTripFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busTrips;
    }

    public int countBusTrips(String route, String driver) {
        String sql = "SELECT COUNT(*) FROM BusTrips WHERE 1=1";

        if (route != null && !route.isEmpty()) {
            sql += " AND route_id LIKE ?";
        }
        if (driver != null && !driver.isEmpty()) {
            sql += " AND driver_id IN (SELECT user_id FROM Users WHERE full_name LIKE ?)";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int index = 1;
            if (route != null && !route.isEmpty()) {
                ps.setString(index++, "%" + route + "%");
            }
            if (driver != null && !driver.isEmpty()) {
                ps.setString(index++, "%" + driver + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT route_id, route_name FROM Routes";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Route route = new Route();
                route.setRouteId(rs.getInt("route_id"));
                route.setRouteName(rs.getString("route_name"));
                routes.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public List<Bus> getAllBuses() {
        List<Bus> buses = new ArrayList<>();
        String sql = "SELECT bus_id, plate_number, bus_type, capacity FROM Bus WHERE is_active = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Bus bus = new Bus();
                bus.setBusId(rs.getInt("bus_id"));
                bus.setPlateNumber(rs.getString("plate_number"));
                bus.setBusType(rs.getString("bus_type"));
                bus.setCapacity(rs.getInt("capacity"));
                buses.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }

    // Lấy số ghế còn lại của chuyến xe
    private int getAvailableSeats(int tripId, int busId) {
        int totalSeats = 0;
        int bookedSeats = 0;

        String queryTotalSeats = "SELECT capacity FROM Bus WHERE bus_id = ?";
        String queryBookedSeats = "SELECT COUNT(*) FROM Tickets WHERE trip_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psTotal = conn.prepareStatement(queryTotalSeats);
             PreparedStatement psBooked = conn.prepareStatement(queryBookedSeats)) {

            // Lấy tổng số ghế của xe
            psTotal.setInt(1, busId);
            ResultSet rsTotal = psTotal.executeQuery();
            if (rsTotal.next()) {
                totalSeats = rsTotal.getInt("capacity");
            }

            // Lấy số ghế đã được đặt
            psBooked.setInt(1, tripId);
            ResultSet rsBooked = psBooked.executeQuery();
            if (rsBooked.next()) {
                bookedSeats = rsBooked.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalSeats - bookedSeats;
    }



}
