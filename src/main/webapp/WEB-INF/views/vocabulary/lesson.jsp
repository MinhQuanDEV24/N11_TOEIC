<%@ page pageEncoding="UTF-8" %>
<%@ include file="../fragments/head.jspf" %>
<%@ include file="../fragments/header.jspf" %>

<main class="vocabulary-page">
  <div class="container container-n11 vocab-wrap vocab-detail-wrap">
    <%@ include file="../fragments/vocabulary-settings.jspf" %>
    <a class="vocab-back-link" href="${pageContext.request.contextPath}/vocabulary/${topic.id}" data-i18n="backTopic">← Quay lại chủ đề</a>

    <header class="vocab-detail-heading">
      <span class="vocab-eyebrow"><c:out value="${topic.title}"/> · LESSON ${lesson.sortOrder}</span>
      <h1><c:out value="${lesson.title}"/></h1>
      <p><c:out value="${lesson.description}"/></p>
      <div class="vocab-detail-meta">
        <span>${words.size()} <span data-i18n="words">từ</span></span>
        <span data-i18n="learnAtYourPace">Học thẻ, làm bài tập và luyện nghe viết.</span>
      </div>
    </header>

    <nav class="vocab-mode-tabs" aria-label="Chế độ học">
      <a class="active" href="#flashcard-mode" data-i18n="flashcards">Học thẻ</a>
      <a href="${pageContext.request.contextPath}/vocabulary/${topic.id}/lessons/${lesson.id}/quiz/exercise" data-i18n="exercises">Bài tập</a>
      <a href="${pageContext.request.contextPath}/vocabulary/${topic.id}/lessons/${lesson.id}/quiz/dictation" data-i18n="dictation">Nghe viết</a>
      <a href="#pronunciation-mode" data-i18n="pronunciation">Luyện phát âm</a>
    </nav>

    <section class="vocab-study-panel" id="flashcard-mode">
      <div class="vocab-study-heading">
        <div>
          <span class="vocab-eyebrow" data-i18n="flashcardPractice">FLASHCARD PRACTICE</span>
          <h2 data-i18n="reviewWords">Ôn tập từ vựng</h2>
        </div>
        <span class="vocab-word-count">${words.size()} <span data-i18n="words">từ</span></span>
      </div>
      <c:choose>
        <c:when test="${not empty words}">
          <div class="vocab-study-card" id="vocab-study-card" role="button" tabindex="0"
               aria-label="Lật thẻ để xem nghĩa">
            <span class="vocab-study-counter"></span>
            <strong class="vocab-study-word"></strong>
            <span class="vocab-study-phonetic"></span>
            <span class="vocab-study-meaning"></span>
            <span class="vocab-study-example"></span>
            <span class="vocab-study-example-meaning"></span>
            <span class="vocab-study-hint" data-i18n="flipCardHint">Nhấn để lật thẻ</span>
          </div>
          <div class="vocab-study-controls">
            <button class="vocab-outline-btn" id="vocab-study-prev" type="button" data-i18n="previous">← Trước</button>
            <div class="d-flex flex-wrap gap-2">
              <button class="vocab-soft-btn" id="vocab-study-speak" type="button" data-i18n="listen">🔊 Nghe</button>
              <button class="vocab-soft-btn" id="vocab-mark-mastered" type="button"
                      data-signed-in="${not empty userEmail}"
                      data-csrf-header="${_csrf.headerName}" data-csrf-token="${_csrf.token}"
                      data-progress-url="${pageContext.request.contextPath}/vocabulary/__TERM_ID__/progress"
                      data-i18n="markMastered">Đã thuộc</button>
            </div>
            <button class="vocab-outline-btn" id="vocab-study-next" type="button" data-i18n="next">Tiếp →</button>
          </div>
          <p class="vocab-flashcard-status" id="vocab-flashcard-status" role="status" aria-live="polite"></p>
        </c:when>
        <c:otherwise>
          <div class="vocab-empty">
            <h2 data-i18n="wordsPreparing">Từ vựng đang được bổ sung</h2>
            <p data-i18n="wordsPreparingText">Bài học này chưa có từ để luyện tập.</p>
          </div>
        </c:otherwise>
      </c:choose>
    </section>

    <section class="vocab-pronunciation-panel" id="pronunciation-mode">
      <div>
        <span class="vocab-eyebrow" data-i18n="speakPractice">SPEAKING PRACTICE</span>
        <h2 data-i18n="pronunciationTitle">Luyện phát âm bằng giọng nói</h2>
        <p data-i18n="pronunciationDescription">Nghe từ mẫu, nhấn nút micro và đọc lại. Trình duyệt sẽ so sánh câu nhận diện được.</p>
      </div>
      <button class="vocab-primary-btn" id="vocab-pronunciation-check" type="button"
              data-signed-in="${not empty userEmail}" data-i18n="startSpeaking">🎙 Luyện từ hiện tại</button>
      <p class="vocab-flashcard-status" id="vocab-pronunciation-status" role="status" aria-live="polite"></p>
    </section>

    <div class="vocab-word-source" aria-hidden="true">
      <c:forEach items="${words}" var="word">
        <article class="vocab-word-card"
                 data-id="${word.id}"
                 data-word="<c:out value='${word.word}'/>"
                 data-phonetic="<c:out value='${word.ipa}'/>"
                 data-meaning="<c:out value='${word.meaningVi}'/>"
                 data-example="<c:out value='${word.exampleEn}'/>"
                 data-example-meaning="<c:out value='${word.exampleVi}'/>"
                 data-audio-url="<c:out value='${word.audioUrl}'/>">
        </article>
      </c:forEach>
    </div>
  </div>
</main>

<script src="${pageContext.request.contextPath}/assets/js/vocabulary.js"></script>
<%@ include file="../fragments/footer.jspf" %>
<%@ include file="../fragments/scripts.jspf" %>
