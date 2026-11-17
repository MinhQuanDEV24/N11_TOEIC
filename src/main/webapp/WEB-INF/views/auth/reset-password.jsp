<%@ page pageEncoding="UTF-8" %>
<%@ include file="../fragments/head.jspf" %>
<%@ include file="../fragments/header.jspf" %>
<main class="auth-section">
  <div class="container container-n11">
    <div class="auth-card">
      <span class="section-caption">BẢO MẬT TÀI KHOẢN</span>
      <h1>Tạo mật khẩu mới</h1>
      <p>Chọn mật khẩu mạnh mà bạn chưa sử dụng cho tài khoản này.</p>

      <c:if test="${not empty formError}">
        <div class="alert alert-danger" role="alert"><c:out value="${formError}"/></div>
      </c:if>

      <form method="post" action="${pageContext.request.contextPath}/reset-password" autocomplete="on">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="hidden" name="token" value="<c:out value='${resetForm.token}'/>">
        <div class="mb-3">
          <label for="newPassword" class="form-label">Mật khẩu mới</label>
          <input id="newPassword" name="password"
                 class="form-control ${not empty fieldErrors.password ? 'is-invalid' : ''}"
                 type="password" minlength="8" maxlength="64" autocomplete="new-password" required>
          <div class="invalid-feedback"><c:out value="${fieldErrors.password}"/></div>
        </div>
        <div class="mb-4">
          <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
          <input id="confirmPassword" name="confirmPassword"
                 class="form-control ${not empty fieldErrors.confirmPassword ? 'is-invalid' : ''}"
                 type="password" maxlength="64" autocomplete="new-password" required>
          <div class="invalid-feedback"><c:out value="${fieldErrors.confirmPassword}"/></div>
        </div>
        <button class="btn btn-primary w-100" type="submit">Lưu mật khẩu mới</button>
      </form>

      <p class="small text-muted mt-4 mb-0"><a href="${pageContext.request.contextPath}/forgot-password">Yêu cầu liên kết mới</a></p>
    </div>
  </div>
</main>
<%@ include file="../fragments/footer.jspf" %>
<%@ include file="../fragments/scripts.jspf" %>
