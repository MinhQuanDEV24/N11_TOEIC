/* Part 7: feedback is unlocked by the server only after a whole passage is answered. */
(() => {
 'use strict';
 const app=document.getElementById('reading-app'); if(!app)return;
 const $=s=>app.querySelector(s), base=app.dataset.base, endpoint=`/reading/api/part7/attempts/${app.dataset.attempt}`;
 const esc=v=>String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
 let state,index=0,busy=false,bilingual=false,dictionary=false;
 const passage=()=>state.passages[index], questions=()=>state.questions.filter(q=>q.passage_id===passage().id);
 const dialog=$('#reading-dialog');
 function error(message){$('#reading-error').textContent=message;$('#reading-error').hidden=!message;}
 function notify(message){$('#reading-status').textContent=message;setTimeout(()=>{$('#reading-status').textContent='';},5000);}
 async function api(path,body){
  const options={credentials:'same-origin',headers:{Accept:'application/json'}};
  if(body!==undefined){options.method='POST';options.headers['Content-Type']='application/json';options.headers[app.dataset.csrfHeader]=app.dataset.csrf;options.body=JSON.stringify(body);}
  const r=await fetch(base+path,options);
  if(r.redirected||r.status===401||r.status===403)throw Error('Phiên đăng nhập đã hết hạn. Hãy tải lại trang và đăng nhập để tiếp tục.');
  const result=await r.json().catch(()=>null);if(!r.ok||!result)throw Error(result?.message||'Không thể kết nối máy chủ. Vui lòng thử lại.');return result;
 }
 function english(text){
  return String(text).split(/(\[\d+\]|\b[A-Za-z]+(?:['’-][A-Za-z]+)*\b)/).map(token=>{
   if(/^\[\d+\]$/.test(token))return `<span class="part7-blank">(${esc(token.slice(1,-1))}) _____</span>`;
   if(dictionary&&/^[A-Za-z]/.test(token))return `<span class="part7-word" data-lookup="${esc(token)}">${esc(token)}</span>`;
   return esc(token);
  }).join('');
 }
 function render(){
  if(!state.passages.length){error('Bộ luyện chưa có bài đọc.');return;}
  const qs=questions(),done=qs.every(q=>q.reviewed),count=qs.filter(q=>q.selected!==null).length;
  const expanded=new Set([...app.querySelectorAll("details[data-explanation][open]")].map(el=>el.dataset.explanation));
  const left=$('#part7-passage').scrollTop,right=$('#reading-question').scrollTop;
  $('#reading-loading').hidden=true;$('#reading-question').hidden=false;
  $('[data-action=bilingual]').setAttribute('aria-pressed',String(bilingual));$('[data-action=dictionary]').setAttribute('aria-pressed',String(dictionary));
  app.classList.toggle('part7-dictionary-on',dictionary);
  $('#session-progress').textContent=`${state.answered}/${state.total} câu đã chọn · ${state.finished?'Đã kết thúc':'Tự động lưu'}`;
  $('#question-position').textContent=`Bài ${index+1}/${state.passages.length}`;
  $('[data-action=previous]').disabled=busy||index===0;$('[data-action=next]').disabled=busy||index===state.passages.length-1;
  $('[data-action=finish]').hidden=state.finished;$('[data-action=finish]').disabled=busy;
  $('#part7-passage').innerHTML=`<h1>${esc(passage().title)}</h1>${passage().documents.map(doc=>`<section class="part7-document"><h2>${english(doc.title)}</h2>${bilingual?`<p class="reading-translation">${esc(doc.title_vi)}</p>`:''}${doc.lines.map(line=>`<p>${english(line.en)}${bilingual?`<span class="reading-translation">${esc(line.vi)}</span>`:''}</p>`).join('')}</section>`).join('')}${dictionary?'<p class="reading-dialog-help">Nháy đúp một từ để tra nghĩa trong học liệu của bài đọc.</p>':''}`;
  $('#reading-question').innerHTML=`<p class="part7-hint" role="status">${done?'Đã chọn đủ câu. Kết quả được hiển thị bên dưới; nhấn Giải thích chi tiết để xem lời giải.':`Đã chọn ${count}/${qs.length} câu. Chọn đủ tất cả câu trong bài đọc để xem đúng/sai. Bạn có thể đổi lựa chọn trước khi hoàn tất.`}</p>`+qs.map((q,i)=>`<section class="part7-question" aria-label="Câu ${state.questions.indexOf(q)+147}"><h2>Câu ${state.questions.indexOf(q)+147}. ${english(q.text)}</h2>${bilingual?`<p class="reading-translation">${esc(q.translation)}</p>`:''}${q.options.map(o=>{
   const correct=q.reviewed&&q.correct_option===o.id,wrong=q.reviewed&&q.selected===o.id&&!correct,selected=!q.reviewed&&q.selected===o.id;
   return `<button class="reading-answer${correct?' correct':wrong?' wrong':selected?' selected':''}" type="button" data-question="${q.id}" data-option="${o.id}" aria-pressed="${q.selected===o.id}" ${busy||q.reviewed||state.finished?'disabled':''}><span class="reading-answer-letter">${esc(o.label)}</span><span>${english(o.text)}${bilingual?`<span class="reading-translation">${esc(o.translation)}</span>`:''}${correct?'<span class="reading-answer-state">✓ Đáp án đúng</span>':wrong?'<span class="reading-answer-state">✕ Bạn đã chọn sai</span>':selected?'<span class="reading-answer-state">Đã chọn · Chưa chấm</span>':''}</span></button>`;
  }).join('')}${q.reviewed?`<details class="reading-explanation" data-explanation="${q.id}" ${expanded.has(String(q.id))?'open':''}><summary>Giải thích chi tiết</summary><blockquote><strong>Câu dẫn chứng</strong><p>${esc(q.evidence)}</p></blockquote><p>${esc(q.explanation)}</p><h3>Phân tích từng đáp án</h3>${q.options.map(o=>`<p><strong>${esc(o.label)}.</strong> ${esc(q.analysis[o.label]||'')}</p>`).join('')}</details>`:''}</section>`).join('');
  $('#part7-passage').scrollTop=left;$('#reading-question').scrollTop=right;
  $('#reading-result').hidden=!state.finished;
  if(state.finished)$('#reading-result').innerHTML=`<h1>Đã hoàn thành lượt luyện Part 7</h1><div class="reading-result-metrics"><div><strong>${state.correct}/${state.total}</strong><span>Câu đúng</span></div><div><strong>${state.score}%</strong><span>Độ chính xác</span></div></div><a class="reading-primary" href="${base}/reading/part7">Chọn cấp độ / Luyện lại →</a>`;
 }
 function popup(){
  if(!dictionary)return;
  let panel=$('#part7-lookup');if(!panel){panel=document.createElement('aside');panel.id='part7-lookup';panel.className='part7-lookup';panel.setAttribute('aria-label','Tra từ');app.append(panel);}
  panel.innerHTML='<header><h2>▤ Tra từ</h2><button type="button" data-action="hide-lookup" aria-label="Đóng tra từ">×</button></header><label class="reading-field">Từ cần tra<input id="part7-search" type="search" placeholder="Nhập từ tiếng Anh hoặc nghĩa tiếng Việt"></label><p class="reading-dialog-help">Nháy đúp từ trong bài hoặc đáp án. Từ điển học liệu của bài hiện tại; không dùng dịch vụ bên ngoài.</p><div id="part7-definition" role="status"></div>';
 }
 function lookup(value){
  if(!dictionary)return;popup();$('#part7-search').value=value;search(value);$('#part7-search').focus();
 }
 function search(value){
  const term=value.trim().toLowerCase();const entries=Object.entries(passage().glossary||{}).filter(([en,vi])=>!term||en.toLowerCase().includes(term)||vi.toLowerCase().includes(term));
  $('#part7-definition').innerHTML=entries.length?entries.map(([en,vi])=>`<div class="reading-cart-row"><div><strong>${esc(en)}</strong><p>${esc(vi)}</p></div><button type="button" data-speak="${esc(en)}" aria-label="Nghe ${esc(en)}">♪</button></div>`).join(''):'<p>Từ này chưa có trong từ điển học liệu của bài. Hãy bật Song ngữ để xem nghĩa cả câu.</p>';
 }
 function show(title,html){$('#dialog-title').textContent=title;$('#dialog-content').innerHTML=html;if(!dialog.open)dialog.showModal();}
 function go(i){if(busy||i<0||i>=state.passages.length)return;index=i;dialog.close();$('#part7-lookup')?.remove();render();$('#part7-passage').scrollTop=0;$('#reading-question').scrollTop=0;window.scrollTo(0,0);}
 async function mutate(work){if(busy)return;busy=true;error('');render();try{state=await work();}catch(e){try{state=await api(endpoint);}catch{}error(e.message);}finally{busy=false;render();}}
 const actions={
  bilingual:()=>{bilingual=!bilingual;render();},dictionary:()=>{dictionary=!dictionary;render();if(dictionary){popup();search('');}else $('#part7-lookup')?.remove();},
  'hide-lookup':()=>$('#part7-lookup')?.remove(),close:()=>dialog.close(),previous:()=>go(index-1),next:()=>go(index+1),
  grid:()=>show('Danh sách bài đọc','<div class="reading-passage-list">'+state.passages.map((p,i)=>{const qs=state.questions.filter(q=>q.passage_id===p.id);return `<p><button type="button" class="reading-primary" data-go="${i}">Bài ${i+1}: ${esc(p.title)} · ${qs.filter(q=>q.selected!==null).length}/${qs.length}</button></p>`;}).join('')+'</div>'),
  finish:()=>{if(state.answered!==state.total){show('Bài luyện chưa hoàn tất','<p>Hãy chọn đủ các câu trước khi kết thúc để xem kết quả. Bạn có thể bấm Thoát; các lựa chọn đã được lưu để tiếp tục sau.</p><div class="reading-dialog-actions"><button type="button" data-action="close">Tiếp tục luyện</button></div>');return;}show('Kết thúc lượt luyện?','<p>Kết quả sẽ được lưu vào lịch sử. Bạn vẫn có thể xem lại các bài đọc và lời giải.</p><div class="reading-dialog-actions"><button type="button" data-action="close">Xem lại bài</button><button type="button" class="reading-primary" data-action="confirm-finish">Kết thúc và lưu kết quả</button></div>');},
  'confirm-finish':()=>{dialog.close();return mutate(()=>api(endpoint+'/finish',{}));}
 };
 app.addEventListener('click',async event=>{if(dictionary&&event.target.closest('[data-lookup]'))return;const b=event.target.closest('button');if(!b||b.disabled||!state||busy)return;if(b.dataset.action){await actions[b.dataset.action]?.();return;}if(b.dataset.option)await mutate(()=>api(endpoint+'/answers',{questionId:Number(b.dataset.question),optionId:Number(b.dataset.option)}));if(b.dataset.go!==undefined)go(Number(b.dataset.go));if(b.dataset.speak){if(!('speechSynthesis'in window)){notify('Trình duyệt chưa hỗ trợ phát âm.');return;}speechSynthesis.cancel();const voice=new SpeechSynthesisUtterance(b.dataset.speak);voice.lang='en-US';voice.onerror=()=>notify('Không thể phát âm trên thiết bị này.');speechSynthesis.speak(voice);}});
 app.addEventListener('dblclick',event=>{if(!dictionary||busy)return;const word=event.target.closest('[data-lookup]');if(word)lookup(word.dataset.lookup);});
 app.addEventListener('input',event=>{if(event.target.id==='part7-search')search(event.target.value);});
 api(endpoint).then(data=>{state=data;const first=state.passages.findIndex(p=>state.questions.some(q=>q.passage_id===p.id&&!q.reviewed));index=first<0?0:first;render();}).catch(e=>{error(e.message);$('#reading-loading').textContent='Không tải được bài luyện. Hãy tải lại trang để thử lại.';});
})();
