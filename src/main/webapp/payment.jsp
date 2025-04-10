<<<<<<< Updated upstream
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.math.BigDecimal, dao.OrderDAO, java.util.*, model.Ticket" %>

<%
    int orderId = Integer.parseInt(request.getParameter("orderId"));
    List<Ticket> tickets = OrderDAO.getTicketsByOrder(orderId);
    BigDecimal total = BigDecimal.ZERO;
    for (Ticket t : tickets) {
        total = total.add(t.getPrice());
    }
%>

<html>
<head>
    <title>Thanh toán</title>
    <style>
        body { font-family: Arial; text-align: center; margin-top: 50px; }
        .box { display: inline-block; padding: 20px; border: 1px solid #ccc; border-radius: 8px; }
        .btn {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background: #28a745;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
    </style>
</head>
<body>

<div class="box">
    <h2>Thanh toán đơn hàng #<%= orderId %></h2>
    <p>Tổng tiền cần thanh toán: <strong><%= total %> đ</strong></p>
    <p>Phương thức: <em>Chuyển khoản / Ví điện tử / Tiền mặt</em></p>

    <!-- Nút giả lập đã thanh toán -->
    <a class="btn" href="confirmBooking.jsp?orderId=<%= orderId %>">Tôi đã thanh toán</a>
</div>
=======
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 4/8/2025
  Time: 11:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<form method="post" action="vnpay_payment">
    <input type="hidden" name="amount" value="50000" />
    <button type="submit">Thanh toán qua VNPAY</button>
</form>

>>>>>>> Stashed changes

</body>
</html>
