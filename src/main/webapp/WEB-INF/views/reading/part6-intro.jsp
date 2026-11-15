<%@ page pageEncoding="UTF-8" %>
<%@ include file="../fragments/head.jspf" %>
<header class="reading-toolbar"><a class="reading-back" href="${pageContext.request.contextPath}/reading/part6">← <span>Quay lại</span></a><a class="reading-brand" href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/assets/img/logo.svg" width="40" height="40" alt="N11 TOEIC"/></a></header>
<main class="reading-intro-wrap"><section class="reading-intro">
  <div class="reading-target" aria-hidden="true">◎</div><span class="reading-eyebrow">SẴN SÀNG CHINH PHỤC</span>
  <h1>Part 6 — Cấp ${level.level}</h1><p>Chế độ Luyện tập · Chọn đủ các câu trong bài đọc để xem đúng/sai; nhấn giải thích để xem lời giải.</p>
  <div class="reading-intro-stats"><div><small>Số câu</small><strong>${level.total}</strong></div><div><small>Độ khó</small><strong><c:out value="${level.label}"/></strong></div></div>
  <c:choose><c:when test="${level.total eq 0}"><p>Bộ luyện này chưa có học liệu. Vui lòng chọn cấp độ khác.</p></c:when><c:when test="${empty pageContext.request.userPrincipal}"><a class="reading-primary" href="${pageContext.request.contextPath}/reading/part6/${level.level}/ready">Đăng nhập để luyện tập →</a></c:when><c:otherwise>
    <form method="post" action="${pageContext.request.contextPath}/reading/part6/${level.level}/start"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/><button class="reading-primary" type="submit">▷ Bắt đầu / Tiếp tục</button></form>
    <small class="reading-intro-note">Lượt đang làm sẽ được tiếp tục. Có thể đổi đáp án trước khi chọn đủ 4 câu của bài đọc.</small>
  </c:otherwise></c:choose>
</section></main>
<%@ include file="../fragments/scripts.jspf" %>
