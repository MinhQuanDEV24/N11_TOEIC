/**
 * N11 TOEIC - Mo ma giao dien bang 7 lan nhan chuot phai lien tiep.
 * CHI kich hoat tren localhost va duong dan /admin.
 * KHONG can dang ky Registry rieng tren tung may: su dung lien ket JetBrains cua IntelliJ.
 * KHONG sua HTML, KHONG gui file len server, KHONG goi MySQL/Cloudinary.
 *
 * CACH DUNG:
 *  - 7 chuot phai lien tiep cung mot vung = mo JSP trong IntelliJ (neu IDE ho tro jetbrains://).
 *  - Sau khi mo, bang goi y cho phep chon CSS lien quan.
 *  - Giữ Shift + chuot phai de hien menu trinh duyet nhu binh thuong.
 *  - Muon sua duong dan file: sua ham pageFile() ben duoi.
 */
(() => {
  'use strict';
  const local = ['localhost', '127.0.0.1', '[::1]'].includes(location.hostname);
  const path = location.pathname.replace(/^\/[^/]+(?=\/admin(?:\/|$))/, '');
  if (!local || !/^\/admin(?:\/|$)/.test(path)) return;
  // Mac dinh BAT khi chay localhost + Admin. Khong can tao Registry / cai script rieng.
  // Neu can tat rieng tren trinh duyet: mo /admin?n11code=0. Bat lai: ?n11code=1.
  let enabled = true;
  try {
    const param = new URLSearchParams(location.search).get('n11code');
    if (param === '0') localStorage.setItem('n11-dev-source-disabled', '1');
    if (param === '1') localStorage.removeItem('n11-dev-source-disabled');
    enabled = localStorage.getItem('n11-dev-source-disabled') !== '1';
  } catch (_) { enabled = true; }
  if (!enabled) return;

  // Ten project hien thi trong IntelliJ. Neu thu muc clone khac ten, sua dong nay.
  const IDE_PROJECT = 'N11_TOEIC';

  const ROOT = 'src/main/webapp/WEB-INF/views/';
  const CSS = 'src/main/resources/static/assets/css/';
  const JSP = (path) => ROOT + path;
  const STYLE = (path) => CSS + path;
  const limit = 7;
  const gapMs = 1700;
  let previous = { target: null, at: 0, count: 0 };
  let card = null;

  // Chon file JSP theo trang dang mo. Sua mapping o day neu them trang moi.
  function pageFile(url) {
    if (/^\/admin(?:\/)?$/.test(url)) return JSP('admin/index.jsp');
    if (/^\/admin\/accounts\/(?:new|\d+\/edit)(?:\/)?$/.test(url)) return JSP('admin/account-form.jsp');
    if (/^\/admin\/accounts(?:\/)?$/.test(url)) return JSP('admin/accounts.jsp');
    if (/^\/admin\/listening\/import/.test(url)) return JSP('admin/listening/import.jsp');
    if (/^\/admin\/listening\/media/.test(url)) return JSP('admin/listening/media.jsp');
    if (/^\/admin\/listening\/.*(?:question|questions)/.test(url)) return JSP('admin/listening/question-form.jsp');
    if (/^\/admin\/listening\/(?:sets\/new|sets\/\d+\/edit)/.test(url)) return JSP('admin/listening/set-form.jsp');
    if (/^\/admin\/listening\/sets\/\d+(?:\/)?$/.test(url)) return JSP('admin/listening/detail.jsp');
    if (/^\/admin\/listening(?:\/)?$/.test(url)) return JSP('admin/listening/index.jsp');
    if (/^\/admin\/grammar\/lessons\/[^/]+\/questions\//.test(url)) return JSP('grammar/admin/question.jsp');
    if (/^\/admin\/grammar\/lessons\/(?:new|\d+)(?:\/)?$/.test(url)) return JSP('grammar/admin/lesson.jsp');
    if (/^\/admin\/grammar(?:\/)?$/.test(url)) return JSP('grammar/admin/index.jsp');
    if (/^\/admin\/exams(?:\/)?$/.test(url)) return JSP('admin/exams.jsp');
    return null; // Trang chua duoc map: khong tu nhan la file JSP khac.
  }

  // CSS duoc sap xep uu tien: file cho khu vuc dang chon, sau do la CSS chung.
  function cssFiles(element, url) {
    const isSidebar = Boolean(element.closest('.admin-sidebar, .admin-nav'));
    const names = [];
    if (isSidebar) names.push('admin-polish-v13.css', 'admin-mint-hover-v12.css');
    if (url.startsWith('/admin/listening')) names.push('admin-listening-forum.css', 'admin-listening.css');
    if (url.startsWith('/admin/accounts')) names.push('admin-accounts.css');
    if (url.startsWith('/admin/grammar')) names.push('grammar.css');
    if (url === '/admin' || url === '/admin/') names.push('admin-polish-v13.css');
    names.push('admin-mint-hover-v12.css', 'admin-unified-v11.css', 'admin-shell.css');
    return [...new Set(names)].map(STYLE);
  }

  function shortText(element) {
    const own = element.closest('button, a, label, th, summary, h1, h2, h3, h4, .form-label');
    const raw = (own || element).textContent || '';
    const txt = raw.replace(/\s+/g, ' ').trim();
    return txt.slice(0, 85);
  }

  function selectorClass(element) {
    let node = element.closest('button, a, input, select, textarea, table, .admin-welcome, .admin-stat, .admin-nav, .admin-module-card, .panel-card') || element;
    const c = [...node.classList || []].find(c => /^[a-z][a-z0-9_-]{3,}$/i.test(c) && !['btn', 'row', 'col', 'd-flex', 'table'].includes(c));
    return c ? '.' + c : '';
  }

  // IntelliJ co the mo file theo DUONG DAN TUONG DOI cua project.
  // Moi may clone vao C:/, D:/ hay E:/ deu dung duoc neu project co ten N11_TOEIC.
  // Yeu cau IntelliJ da cai va co bo tich hop link JetBrains tren may do.
  function bridge(file) {
    if (!file) return;
    const relative = file.replace(/\\/g, '/');
    const url = 'jetbrains://idea/navigate/reference?project=' +
      encodeURIComponent(IDE_PROJECT) + '&path=' + encodeURIComponent(relative);
    window.location.href = url;
  }

  // Phuong an du phong: sao chep duong dan file khi may chua ho tro link IntelliJ.
  function copyPath(file) {
    if (!file) return;
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(file)
        .then(() => { window.alert('Da sao chep: ' + file + '\nTrong IntelliJ bam Ctrl+Shift+N va dan ten file.'); })
        .catch(() => { window.prompt('Copy duong dan file:', file); });
    } else {
      window.prompt('Copy duong dan file:', file);
    }
  }

  function closeCard() { if (card) { card.remove(); card = null; } }

  function showCard(jsp, css, text, cls) {
    closeCard();
    card = document.createElement('div');
    card.className = 'n11-dev-card-v14';
    card.style.cssText = 'position:fixed;z-index:2147483000;bottom:20px;right:24px;width:min(450px,calc(100vw - 48px));max-height:70vh;overflow:auto;padding:15px 17px;border-radius:16px;background:#fff;color:#064b45;font:13px/1.5 Segoe UI,Arial,sans-serif;box-shadow:0 15px 45px #003c363d;border:1px solid #bdece1;';
    const heading = document.createElement('div');
    heading.textContent = 'N11 · MỞ CODE GIAO DIỆN';
    heading.style.cssText = 'font-weight:800;letter-spacing:.05em;margin-bottom:5px';
    card.appendChild(heading);
    const desc = document.createElement('div');
    desc.textContent = (text || 'Khu vực đã chọn') + ' · Bấm CSS nếu cần sửa màu/kích thước.';
    desc.style.cssText = 'font-size:12px;color:#567f76;margin-bottom:10px';
    card.appendChild(desc);
    const button = (label, onClick) => {
      const b = document.createElement('button');
      b.type = 'button'; b.textContent = label;
      b.style.cssText = 'margin:4px 5px 4px 0;padding:7px 11px;border:1px solid #b8e8df;border-radius:10px;background:#e8f7f3;color:#034b44;font-weight:700;cursor:pointer';
      b.addEventListener('click', onClick); card.appendChild(b);
      return b;
    };
    if (jsp) {
      button('Mở JSP trong IntelliJ', () => bridge(jsp));
      button('Copy JSP', () => copyPath(jsp));
    }
    css.forEach((file) => button('CSS · ' + file.split('/').pop(), () => bridge(file)));
    button('Đóng', closeCard);
    document.body.appendChild(card);
  }

  // 7 lần nhấp phải liên tiếp vào cùng 1 phần tử trong 1 khoảng ngắn.
  // Không chặn Shift + chuột phải để người dùng vẫn có menu chuột phải thường.
  document.addEventListener('contextmenu', (event) => {
    if (event.shiftKey || event.ctrlKey || event.altKey) return;
    if (event.target.closest('.n11-dev-card-v14')) return;
    event.preventDefault();
    const target = event.target.closest('button, input, select, textarea, a, td, th, .admin-welcome, .admin-module-card, .panel-card, .admin-nav, .admin-stat, section, form') || event.target;
    const now = Date.now();
    const count = previous.target === target && now - previous.at < gapMs ? previous.count + 1 : 1;
    previous = { target, at: now, count };
    if (count < limit) {
      // Phan hoi nho chi hien khi nhan tu lan thu hai.
      if (count >= 2) {
        const old = document.querySelector('.n11-dev-counter-v14'); if (old) old.remove();
        const tip = document.createElement('div'); tip.className = 'n11-dev-counter-v14';
        tip.textContent = `Mở code: ${count}/7 · Shift + chuột phải để dùng menu thường`;
        tip.style.cssText = 'position:fixed;z-index:2147483000;bottom:12px;left:12px;padding:7px 12px;border-radius:10px;background:#004b47;color:white;font:12px Segoe UI,Arial';
        document.body.appendChild(tip); setTimeout(() => { if(tip.isConnected) tip.remove(); }, gapMs);
      }
      return;
    }
    previous = { target: null, at: 0, count: 0 };
    const old = document.querySelector('.n11-dev-counter-v14'); if (old) old.remove();
    const source = pageFile(path);
    const css = cssFiles(target, path);
    const text = shortText(target);
    const cls = selectorClass(target);
    showCard(source, css, text, cls);
    if (source) bridge(source); // 7 lan = mo JSP ngay; phia duoi co CSS/Copy.
  }, true);
})();
