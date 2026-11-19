/* The server owns answers, grading, attempt membership and vocabulary favorites. */
(() => {
  'use strict';
  const app = document.getElementById('reading-app');
  if (!app) return;
  const $ = selector => app.querySelector(selector);
  const base = app.dataset.base;
  const endpoint = `/reading/api/attempts/${app.dataset.attempt}`;
  const dialog = $('#reading-dialog');
  let state, index = 0, busy = false, cart = [], selectedWords = new Set(), detailWords = true;
  let prefs = { bilingual: true, highlight: false, fontSize: 19, autoExplanation: false };
  let statusTimer;
  const escape = text => String(text ?? '').replace(/[&<>"']/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]));
  const current = () => state.questions[index];
  const key = suffix => `n11.reading.${state.user_id}.${suffix}`;
  function readLocal(suffix, fallback) { try { return JSON.parse(localStorage.getItem(key(suffix))) ?? fallback; } catch { return fallback; } }
  function saveLocal(suffix, value) {
    try { localStorage.setItem(key(suffix), JSON.stringify(value)); return true; }
    catch { notify('Trình duyệt không cho lưu cục bộ. Ghi chú và cài đặt chỉ giữ trong trang này.'); return false; }
  }
  const notes = new Map();
  function notify(message) {
    clearTimeout(statusTimer); $('#reading-status').textContent = message;
    statusTimer = setTimeout(() => { $('#reading-status').textContent = ''; }, 4500);
  }
  function error(message) { $('#reading-error').textContent = message; $('#reading-error').hidden = !message; }
  async function api(path, body) {
    const options = { headers: {Accept:'application/json'}, credentials:'same-origin' };
    if (body !== undefined) {
      options.method = 'POST'; options.headers['Content-Type'] = 'application/json';
      options.headers[app.dataset.csrfHeader] = app.dataset.csrf; options.body = JSON.stringify(body);
    }
    const response = await fetch(base + path, options);
    if (response.redirected || response.status === 401 || response.status === 403) throw new Error('Phiên đăng nhập đã hết hạn. Hãy tải lại trang hoặc đăng nhập lại để tiếp tục.');
    const data = await response.json().catch(() => null);
    if (!response.ok || !data) throw new Error(data?.message || 'Không thể kết nối máy chủ. Vui lòng thử lại.');
    return data;
  }
  function sentenceHtml(question) {
    if (!prefs.highlight) return escape(question.text);
    const terms = new Set(question.vocabulary.map(term => term.word.toLowerCase()));
    return question.text.split(/(\b[\w'-]+\b)/).map(token => terms.has(token.toLowerCase()) ? `<mark>${escape(token)}</mark>` : escape(token)).join('');
  }
  function render() {
    const q = current();
    if (!q) { error('Lượt luyện không có câu hỏi. Vui lòng quay lại danh sách.'); return; }
    $('#reading-loading').hidden = true; $('#reading-question').hidden = false;
    $('.reading-workspace').style.setProperty('--question-size', `${prefs.fontSize}px`);
    $('[data-action=bilingual]').setAttribute('aria-pressed', String(prefs.bilingual));
    $('[data-action=highlight]').setAttribute('aria-pressed', String(prefs.highlight));
    $('#session-progress').textContent = `${state.answered}/${state.total} câu đã trả lời · ${state.finished ? 'Đã kết thúc' : 'Tự động lưu đáp án'}`;
    $('#question-position').textContent = `${index + 1}/${state.total}`;
    $('[data-action=previous]').disabled = index === 0 || busy;
    $('[data-action=next]').disabled = index === state.questions.length - 1 || busy;
    $('[data-action=finish]').hidden = state.finished;
    $('[data-action=finish]').disabled = busy;
    const showReview = q.selected !== null || state.finished;
    $('#reading-question').innerHTML = `
      <div class="reading-question-title"><span class="reading-question-label">Câu ${index + 1}.</span><div><p class="reading-sentence">${sentenceHtml(q)}</p>${prefs.bilingual && q.translation ? `<span class="reading-translation">${escape(q.translation)}</span>` : ''}</div></div>
      <div aria-label="Chọn một đáp án">${q.options.map(option => {
        const correct = showReview && option.id === q.correct_option;
        const wrong = showReview && option.id === q.selected && !correct;
        return `<button type="button" class="reading-answer${correct ? ' correct' : wrong ? ' wrong' : ''}" data-option="${option.id}" ${busy || showReview ? 'disabled' : ''}>
          <span class="reading-answer-letter">${escape(option.label)}</span><span>${escape(option.text)}${prefs.bilingual && option.translation ? `<span class="reading-translation">${escape(option.translation)}</span>` : ''}${correct || wrong ? `<span class="reading-answer-state">${correct ? '✓ Đáp án đúng' : '✕ Bạn đã chọn'}${correct && option.id === q.selected ? ' · Bạn đã chọn' : ''}</span>` : ''}</span></button>`;
      }).join('')}</div>
      ${showReview ? `<div class="reading-feedback" role="status">${q.selected === null ? 'Bạn đã bỏ qua câu này.' : q.selected === q.correct_option ? 'Chính xác! Bạn đã chọn đúng đáp án.' : 'Chưa chính xác. Hãy đọc giải thích để hiểu cách chọn đáp án.'}</div>
      <details class="reading-explanation" ${prefs.autoExplanation ? 'open' : ''}><summary>Xem giải thích chi tiết</summary><p>${escape(q.explanation)}</p>${Object.keys(q.analysis || {}).length ? `<ul>${Object.entries(q.analysis).map(([label, text]) => `<li><strong>${escape(label)}.</strong> ${escape(text)}</li>`).join('')}</ul>` : ''}</details>` : '<p class="reading-dialog-help">Chọn một đáp án để kiểm tra. Bạn có thể chuyển câu và quay lại sau.</p>'}`;
    renderVocabulary();
    $('#reading-result').hidden = !state.finished;
    if (state.finished) $('#reading-result').innerHTML = `<div class="reading-target" aria-hidden="true">✓</div><h1>Đã hoàn thành lượt luyện</h1><p>${escape(state.title)}</p><div class="reading-result-metrics"><div><strong>${state.correct}/${state.total}</strong><span>Câu đúng</span></div><div><strong>${state.score}%</strong><span>Độ chính xác</span></div><div><strong>${state.total - state.answered}</strong><span>Chưa trả lời</span></div></div><p>Câu chưa trả lời được tính vào tổng số câu. Xem lại lời giải bên dưới để củng cố kiến thức.</p><a class="reading-primary" href="${base}/reading">Chọn cấp độ / Luyện lại →</a>`;
    saveLocal(`position.${state.id}`, index);
  }
  function renderVocabulary() {
    const words = current().vocabulary;
    $('#reading-vocabulary').hidden = words.length === 0;
    $('#reading-vocabulary').innerHTML = `<div class="reading-vocabulary-top"><h2>Từ vựng nên học <span>(${words.length})</span></h2><div class="reading-vocabulary-actions"><button type="button" data-action="select-words">${selectedWords.size === words.length ? 'Bỏ chọn tất cả' : '✓ Chọn tất cả'}</button><button type="button" class="reading-add-words" data-action="save-words" ${!selectedWords.size || busy ? 'disabled' : ''}>+ Thêm vào giỏ từ (${selectedWords.size})</button><button type="button" data-action="word-details" aria-pressed="${detailWords}">${detailWords ? 'Ẩn chi tiết' : 'Xem chi tiết'}</button></div></div>
      ${words.map(word => `<div class="reading-word"><input type="checkbox" data-word="${word.id}" aria-label="Chọn từ ${escape(word.word)}" ${selectedWords.has(word.id) ? 'checked' : ''}/><div><strong>${escape(word.word)}</strong>${word.ipa ? `<span class="reading-ipa">${escape(word.ipa)}</span>` : ''}<button type="button" data-speak="${word.id}" aria-label="Nghe từ ${escape(word.word)}">♪ Nghe</button><p>${escape(word.meaning_vi)}</p>${detailWords ? `<div class="reading-word-details">${word.definition_en ? `<p>${escape(word.definition_en)}</p>` : ''}${word.example_en ? `<blockquote><em>${escape(word.example_en)}</em>${prefs.bilingual ? `<br>${escape(word.example_vi)}` : ''}</blockquote>` : ''}</div>` : ''}${cart.some(item => item.id === word.id) ? '<small>✓ Đã có trong giỏ từ</small>' : ''}</div></div>`).join('')}`;
  }
  function showDialog(title, html) {
    $('#dialog-title').textContent = title; $('#dialog-content').innerHTML = html;
    if (!dialog.open) dialog.showModal();
  }
  function openCart() {
    showDialog('Giỏ từ của bạn', cart.length ? `<p class="reading-dialog-help">Từ đã lưu gắn với tài khoản, có thể xem lại ở các lượt luyện khác.</p>${cart.map(word => `<div class="reading-cart-row"><div><strong>${escape(word.word)}</strong><p>${escape(word.meaning_vi)}</p></div><button type="button" data-remove="${word.id}">Bỏ khỏi giỏ</button></div>`).join('')}` : '<p>Giỏ từ đang trống. Chọn từ bên dưới câu hỏi để thêm vào giỏ.</p>');
  }
  function openDictionary() {
    showDialog('Tra từ trong câu hiện tại', '<p class="reading-dialog-help">Tra các từ thuộc học liệu của câu này. Nút Nghe sử dụng giọng tiếng Anh của trình duyệt.</p><label class="reading-field">Từ cần tra<input type="search" id="dictionary-search" placeholder="Nhập từ hoặc nghĩa tiếng Việt…" autocomplete="off"></label><div id="dictionary-results"></div>');
    searchDictionary(''); $('#dictionary-search').focus();
  }
  function searchDictionary(query) {
    const words = current().vocabulary.filter(word => `${word.word} ${word.meaning_vi}`.toLowerCase().includes(query.toLowerCase().trim()));
    $('#dictionary-results').innerHTML = words.length ? words.map(word => `<div class="reading-cart-row"><div><strong>${escape(word.word)}</strong><p>${escape(word.meaning_vi)}</p><p><em>${escape(word.example_en)}</em></p></div><button type="button" data-speak="${word.id}">♪ Nghe</button></div>`).join('') : '<p>Không tìm thấy từ này trong học liệu của câu hiện tại.</p>';
  }
  function go(position) {
    if (busy || position < 0 || position >= state.questions.length) return;
    index = position; selectedWords.clear(); error(''); dialog.close(); render();
    $('#reading-question').scrollIntoView({block:'start'});
    window.scrollBy(0, -$('.reading-toolbar').offsetHeight - 20);
  }
  async function mutate(work) {
    if (busy) return;
    busy = true; error(''); render();
    try { await work(); }
    catch (failure) {
      // A retry after a lost response or another tab must reconcile with the server.
      try { state = await api(endpoint); } catch { /* Keep the visible question until reconnecting. */ }
      error(failure.message);
      if (dialog.open) notify(failure.message);
    } finally { busy = false; render(); }
  }
  const actions = {
    close: () => dialog.close(), previous: () => go(index - 1), next: () => go(index + 1),
    bilingual: () => { prefs.bilingual = !prefs.bilingual; saveLocal('preferences', prefs); render(); },
    highlight: () => { prefs.highlight = !prefs.highlight; saveLocal('preferences', prefs); render(); notify(prefs.highlight ? 'Đã đánh dấu các từ vựng nên học trong câu.' : 'Đã tắt đánh dấu từ.'); },
    'word-details': () => { detailWords = !detailWords; renderVocabulary(); },
    'select-words': () => { selectedWords = selectedWords.size === current().vocabulary.length ? new Set() : new Set(current().vocabulary.map(word => word.id)); renderVocabulary(); },
    'save-words': () => mutate(async () => { cart = await api(`${endpoint}/words`, {questionId:current().id, termIds:[...selectedWords]}); selectedWords.clear(); $('#cart-count').textContent = cart.length; notify('Đã lưu từ vào giỏ từ.'); }),
    cart: openCart, dictionary: openDictionary,
    notes: () => { const qid=current().id; showDialog(`Ghi chú · Câu ${index+1}`, `<p class="reading-dialog-help">Tự động lưu trên trình duyệt này theo tài khoản và câu hỏi.</p><label class="reading-field">Ghi chú của bạn<textarea id="question-notes" maxlength="5000" placeholder="Ghi lại cấu trúc hoặc từ vựng cần nhớ…">${escape(notes.get(qid) ?? readLocal(`note.${qid}`, ''))}</textarea></label><small id="note-status"></small>`); },
    settings: () => showDialog('Cài đặt luyện tập', `<label class="reading-field">Cỡ chữ<select id="reading-font">${[17,19,22,25].map(size => `<option value="${size}" ${size === prefs.fontSize ? 'selected' : ''}>${size}px</option>`).join('')}</select></label><label class="reading-field"><input type="checkbox" id="reading-auto-explanation" ${prefs.autoExplanation ? 'checked' : ''}> Tự mở giải thích sau khi trả lời</label>`),
    grid: () => showDialog('Danh sách câu hỏi', `<p class="reading-dialog-help">✓ Đúng · ✕ Sai · ○ Chưa trả lời</p><div class="reading-grid">${state.questions.map((q, i) => { const answered=q.selected!==null; const correct=answered && q.selected===q.correct_option; return `<button type="button" data-go="${i}" class="${i===index?'is-current ':''}${answered?(correct?'is-correct':'is-wrong'):''}" aria-label="Câu ${i+1}, ${answered?(correct?'đúng':'sai'):'chưa trả lời'}">${i+1} ${answered?(correct?'✓':'✕'):'○'}</button>`; }).join('')}</div>`),
    finish: () => showDialog('Kết thúc lượt luyện?', `<p>Bạn đã trả lời <strong>${state.answered}/${state.total}</strong> câu. ${state.total-state.answered ? `${state.total-state.answered} câu còn trống sẽ được tính là chưa đúng.` : 'Kết quả sẽ được lưu vào lịch sử luyện đọc.'}</p><p class="reading-dialog-help">Sau khi kết thúc, bạn có thể xem lời giải của toàn bộ câu hỏi hoặc bắt đầu lượt mới.</p><div class="reading-dialog-actions"><button type="button" data-action="close">Tiếp tục luyện</button><button type="button" class="reading-primary" data-action="confirm-finish">Kết thúc và xem kết quả</button></div>`),
    'confirm-finish': () => { dialog.close(); return mutate(async () => {state = await api(`${endpoint}/finish`, {}); window.scrollTo({top:0,behavior:'smooth'});}); }
  };
  app.addEventListener('click', async event => {
    const button = event.target.closest('button');
    if (!button || button.disabled || !state) return;
    if (button.dataset.action && actions[button.dataset.action]) { if (!busy || button.dataset.action === 'close') await actions[button.dataset.action](); return; }
    if (busy) return;
    if (button.dataset.option) await mutate(async () => {state = await api(`${endpoint}/answers`, {questionId:current().id,optionId:Number(button.dataset.option)});});
    if (button.dataset.go) go(Number(button.dataset.go));
    if (button.dataset.remove) await mutate(async () => {cart = await api(`/reading/api/cart/${button.dataset.remove}/remove`, {}); $('#cart-count').textContent=cart.length; openCart();});
    if (button.dataset.speak) {
      const word = current().vocabulary.find(item => item.id === Number(button.dataset.speak));
      if (!word) return;
      if (!('speechSynthesis' in window)) {notify('Trình duyệt này chưa hỗ trợ phát âm.'); return;}
      window.speechSynthesis.cancel(); const utterance = new SpeechSynthesisUtterance(word.word); utterance.lang='en-US'; utterance.rate=.85;
      utterance.onerror=()=>notify('Không phát được giọng đọc. Vui lòng kiểm tra giọng tiếng Anh trên thiết bị.'); window.speechSynthesis.speak(utterance);
    }
  });
  app.addEventListener('change', event => {
    const input=event.target;
    if (input.dataset.word) {const id=Number(input.dataset.word); input.checked?selectedWords.add(id):selectedWords.delete(id); renderVocabulary();}
    if (input.id === 'reading-font') {prefs.fontSize=Number(input.value); saveLocal('preferences',prefs); render();}
    if (input.id === 'reading-auto-explanation') {prefs.autoExplanation=input.checked; saveLocal('preferences',prefs); render();}
  });
  app.addEventListener('input', event => {
    if(event.target.id==='dictionary-search') searchDictionary(event.target.value);
    if(event.target.id==='question-notes') {notes.set(current().id,event.target.value); const saved=saveLocal(`note.${current().id}`,event.target.value); $('#note-status').textContent=saved?'Đã lưu ghi chú trên trình duyệt.':'Chỉ giữ ghi chú trong trang đang mở.';}
  });
  async function load() {
    try {
      state=await api(endpoint); prefs={...prefs,...readLocal('preferences',{})};
      if (![17,19,22,25].includes(prefs.fontSize)) prefs.fontSize=19;
      const firstUnanswered=state.questions.findIndex(q=>q.selected===null);
      const saved=readLocal(`position.${state.id}`,firstUnanswered<0?0:firstUnanswered);
      index=Number.isInteger(saved)&&saved>=0&&saved<state.questions.length?saved:0;
      render();
      try {cart=await api('/reading/api/cart'); $('#cart-count').textContent=cart.length; renderVocabulary();} catch(failure) {error(failure.message);}
    } catch(failure) {$('#reading-loading').textContent='Không tải được bài luyện. Tải lại trang để thử lại.'; error(failure.message);}
  }
  load();
})();
