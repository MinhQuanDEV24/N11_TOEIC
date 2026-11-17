<%@ page pageEncoding="UTF-8" %>
<%@ include file="../fragments/head.jspf" %>
<%@ include file="../fragments/header.jspf" %>
<main class="auth-section">
  <div class="container container-n11">
    <div class="auth-card">
      <span class="section-caption">KHÔI PHỤC TÀI KHOẢN</span>
      <h1>Quên mật khẩu?</h1>
      <p>Nhập email đã đăng ký. Nếu tài khoản tồn tại, chúng tôi sẽ gửi liên kết đặt lại mật khẩu.</p>

      <c:if test="${not empty resetRequestSent}">
        <div class="alert alert-success" role="status">
          Yêu cầu đã được xử lý. Nếu email thuộc tài khoản đang hoạt động, hãy kiểm tra hộp thư đến và thư mục spam.
          Nếu chưa nhận được, bạn có thể thử lại sau ít phút.
        </div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/forgot-password">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <div class="mb-4">
          <label for="resetEmail" class="form-label">Email</label>
          <input id="resetEmail" name="email"
                 class="form-control ${not empty fieldErrors.email ? 'is-invalid' : ''}"
                 type="email" maxlength="191" autocomplete="email" value="<c:out value='${requestForm.email}'/>" required>
          <div class="invalid-feedback"><c:out value="${fieldErrors.email}"/></div>
        </div>
        <button class="btn btn-primary w-100" type="submit">Gửi liên kết đặt lại</button>
      </form>

      <p class="small text-muted mt-4 mb-0"><a href="${pageContext.request.contextPath}/login">← Quay lại đăng nhập</a></p>
    </div>
  </div>
</main>
<%@ include file="../fragments/footer.jspf" %>
<%@ include file="../fragments/scripts.jspf" %>
