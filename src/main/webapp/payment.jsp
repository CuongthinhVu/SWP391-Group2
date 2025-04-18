<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.math.BigDecimal" %>

<%
    // Lấy dữ liệu từ URL (?orderId=abc123&total=123456.78)
    String orderId = request.getParameter("orderId");
    BigDecimal totalVND = new BigDecimal(request.getParameter("total"));

    // Tỷ giá USD (1 USD = 24,000 VNĐ)
    BigDecimal exchangeRate = new BigDecimal("24000");

    // Đổi sang USD, làm tròn 2 chữ số
    BigDecimal totalUSD = totalVND.divide(exchangeRate, 2, java.math.RoundingMode.HALF_UP);
%>

<!DOCTYPE html>
<html>
<head>
    <title>Thanh toán qua PayPal</title>

    <!-- SDK của PayPal (sandbox) -->
    <script src="https://www.paypal.com/sdk/js?client-id=AWiAVAG1EPe3DOJYsCTZGJEanGJi8NFvkPKc82yDPJEjiO9TgGmVBbkhKZ8vIysUvTfTBrUNTLUIEsXz&currency=USD"></script>

    <style>
        body { font-family: Arial; padding: 30px; }
        .info { margin-bottom: 20px; }
        .info p { margin: 6px 0; }
    </style>
</head>
<body>

<h2>Xác nhận thanh toán đơn hàng</h2>

<div class="info">
    <p><strong>Mã đơn hàng:</strong> <%= orderId %></p>
    <p><strong>Tổng tiền (VNĐ):</strong> <%= totalVND.toPlainString() %> VND</p>
    <p><strong>Thành tiền (USD):</strong> <%= totalUSD.toPlainString() %> USD</p>
    <p><em>(Tỷ giá: 1 USD = 24,000 VNĐ)</em></p>
</div>

<div id="paypal-button-container"></div>

<script>
    paypal.Buttons({
        createOrder: function(data, actions) {
            return actions.order.create({
                purchase_units: [{
                    description: "Thanh toán đơn hàng #<%= orderId %>",
                    amount: {
                        value: "<%= totalUSD.toPlainString() %>"
                    }
                }]
            });
        },
        onApprove: function(data, actions) {
            return actions.order.capture().then(function(details) {
                alert('✅ Thanh toán thành công!');
                // Gửi orderId và total về server xác nhận lưu DB
                fetch('payment-confirm?orderId=<%= orderId %>&total=<%= totalVND.toPlainString() %>', { method: 'POST' })
                    .then(() => window.location.href = 'payment-success.jsp');
            });
        }
    }).render('#paypal-button-container');
</script>

</body>
</html>
