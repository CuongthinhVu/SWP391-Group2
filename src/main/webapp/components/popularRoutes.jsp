<%@page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hệ Thống Bán Vé Xe Buýt</title>
        <link rel="stylesheet" href="styles.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    </head>
    <body>
        <div class="popular-routes">
            <h2 class="text-center text-success">TUYẾN PHỔ BIẾN</h2>
            <p class="text-center text-muted">Được khách hàng tin tưởng và lựa chọn</p>
            <div class="d-flex justify-content-center">
                <div class="d-flex flex-row gap-3">
                    <c:forEach var="route" items="${popularRoutes}">
                        <div class="card" style="width: 18rem;">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <a href="filter-routes?cityName=${route.startLocation.name}" 
                                       class="text-decoration-none text-dark fw-bold">
                                        Tuyến xe từ ${route.startLocation.name}
                                    </a>
                                </h5>
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item d-flex justify-content-between">
                                        <span>${route.endLocation.name}</span>
                                        <span class="text-success">${route.basePrice}đ</span>
                                    </li>
                                    <li class="list-group-item text-muted">
                                        ${route.distance}km - ${route.estimatedDuration} giờ - 01/03/2025
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </body>
</html>
