<%@ page import="model.User" %>
<%--
  Created by IntelliJ IDEA.
  User: LONG
  Date: 3/5/2025
  Time: 11:12 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<!-- Bootstrap Bundle (bao gồm Popper.js) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- Bootstrap Icons (nếu bạn dùng icon như `bi bi-person`) -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User Profile</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    body {
      background-color: #f8f9fa;
    }
    .profile-container {
      max-width: 900px;
      margin: 50px auto;
    }
    .card {
      border-radius: 10px;
    }
    .profile-img {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      margin: auto;
      display: block;
    }
    .password-fields {
      display: none; /* Ẩn phần đổi mật khẩu ban đầu */
    }
  </style>
</head>
<body>
<jsp:include page="/components/header.jsp" />
<div class="container profile-container">
  <div class="card p-4">
    <div class="text-center">
      <img src="https://bootdey.com/img/Content/avatar/avatar7.png" class="profile-img" alt="User Avatar">
    </div>
  </div>

  <div class="card mt-4 p-4">
    <h5>Thông tin cá nhân người dùng</h5>
    <form id="profileForm" action="profile" method="post">
      <div class="row mt-3">
        <div class="col-md-6">
          <label class="form-label">Họ và Tên</label>
          <input type="text" class="form-control" id="fullName" name="fullName" value="${user.fullName}" readonly>
        </div>
        <div class="col-md-6">
          <label class="form-label">Email</label>
          <input type="text" class="form-control" id="email" name="email" value="${user.email}" readonly>
        </div>
      </div>
      <div class="row mt-3">
        <div class="col-md-6">
          <label class="form-label">Số điện thoại</label>
          <input type="text" class="form-control" id="phone" name="phone" value="${user.phone}" readonly>
        </div>
      </div>

      <div class="text-center mt-3">
        <button type="button" class="btn btn-primary" id="editBtn">Chỉnh sửa</button>
        <button type="button" class="btn btn-success d-none" id="saveBtn">Lưu</button>
        <button type="button" class="btn btn-secondary d-none" id="cancelBtn">Hủy</button>
      </div>
      <div class="text-center mt-3">
        <a href="booking-history" class="btn btn-info">Xem lịch sử đặt vé</a>
      </div>
    </form>
  </div>
</div>

<script>
  document.getElementById("editBtn").addEventListener("click", function() {
    // Bật chế độ chỉnh sửa
    let inputs = document.querySelectorAll("#profileForm input");
    inputs.forEach(input => input.removeAttribute("readonly"));

    // Hiển thị nút Save & Cancel, ẩn nút Edit
    document.getElementById("saveBtn").classList.remove("d-none");
    document.getElementById("cancelBtn").classList.remove("d-none");
    this.classList.add("d-none");
  });

  document.getElementById("cancelBtn").addEventListener("click", function() {
    location.reload(); // Tải lại trang để khôi phục dữ liệu cũ
  });

  document.getElementById("saveBtn").addEventListener("click", function() {
    // Lấy dữ liệu từ form
    let form = document.getElementById("profileForm");

    console.log("Gửi dữ liệu lên server...");
    form.submit();


    alert("Cập nhật thông tin thành công!");

    // Ẩn nút Save & Cancel, hiện lại nút Edit
    document.getElementById("editBtn").classList.remove("d-none");
    this.classList.add("d-none");
    document.getElementById("cancelBtn").classList.add("d-none");

    // Đặt lại input về readonly
    let inputs = document.querySelectorAll("#profileForm input:not([type=password])");
    inputs.forEach(input => input.setAttribute("readonly", "true"));

  });
</script>

</body>
</html>
