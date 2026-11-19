"""Generate an additive, rerunnable Part 5 starter pack. No third-party questions.
Run from any directory: python scripts/build_reading_seed.py
Difficulty is editorial (1-5), not calibrated from learner statistics.
"""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
# level, sentence, translation, choices, choice meanings, correct label, explanation, vocabulary
QUESTIONS = [
    (1, 'All employees must finish safety training before _____ the machinery.',
     'Tất cả nhân viên phải hoàn thành khóa đào tạo an toàn trước khi vận hành máy móc.',
     ['operate', 'operating', 'to operate', 'operation'], ['vận hành (nguyên mẫu)', 'vận hành (V-ing)', 'vận hành (to V)', 'sự vận hành'], 'B',
     'Sau giới từ before, dùng danh từ hoặc V-ing. Ở đây cần động từ có tân ngữ the machinery, nên chọn operating. Operate và to operate không phù hợp sau giới từ; operation là danh từ và không trực tiếp nhận tân ngữ theo cách này.', ['employees', 'machinery']),
    (1, 'Ms. Lan is a very _____ employee who always meets deadlines.',
     'Cô Lan là một nhân viên rất đáng tin cậy, luôn hoàn thành đúng hạn.',
     ['reliability', 'rely', 'reliably', 'reliable'], ['sự đáng tin cậy', 'dựa vào', 'một cách đáng tin cậy', 'đáng tin cậy'], 'D',
     'Giữa very và danh từ employee cần tính từ reliable. Reliability là danh từ, rely là động từ, còn reliably là trạng từ nên không bổ nghĩa trực tiếp cho employee.', ['employee', 'deadlines']),
    (1, 'The reception desk _____ at 8 a.m. every weekday.',
     'Quầy lễ tân mở cửa lúc 8 giờ sáng mỗi ngày trong tuần.',
     ['opens', 'open', 'opening', 'to open'], ['mở cửa (ngôi thứ ba số ít)', 'mở cửa (nguyên mẫu)', 'đang mở / việc mở', 'để mở'], 'A',
     'Every weekday diễn tả thói quen nên dùng hiện tại đơn. Chủ ngữ the reception desk số ít, động từ thêm -s: opens. Open không hòa hợp chủ ngữ; opening và to open không làm động từ chính chia thì trong câu này.', ['reception']),
    (1, 'Please send _____ application before Friday.',
     'Vui lòng gửi đơn ứng tuyển của bạn trước thứ Sáu.',
     ['you', 'yours', 'your', 'yourself'], ['bạn', 'của bạn (đại từ)', 'của bạn (trước danh từ)', 'chính bạn'], 'C',
     'Trước danh từ application cần tính từ sở hữu your. You là đại từ chủ ngữ/tân ngữ; yours thay thế cả cụm danh từ; yourself là đại từ phản thân.', ['application']),
    (1, 'The training session begins _____ Monday morning.',
     'Buổi đào tạo bắt đầu vào sáng thứ Hai.',
     ['at', 'on', 'in', 'by'], ['vào (giờ cụ thể)', 'vào (ngày cụ thể)', 'trong / vào (tháng, năm)', 'chậm nhất vào'], 'B',
     'Dùng on với ngày cụ thể, kể cả Monday morning. At đi với giờ, in đi với buổi nói chung như in the morning. By nhấn mạnh hạn chót, không phù hợp với thời điểm bắt đầu được thông báo ở đây.', ['training']),
    (1, 'We need three more _____ for the meeting room.',
     'Chúng ta cần thêm ba chiếc ghế cho phòng họp.',
     ['chair', 'chairs', 'seated', 'sitting'], ['chiếc ghế', 'những chiếc ghế', 'được bố trí chỗ ngồi', 'đang ngồi'], 'B',
     'Three more cần danh từ đếm được số nhiều: chairs. Chair là số ít. Seated là phân từ/tính từ; sitting không chỉ đồ vật cần thêm cho phòng họp.', ['meeting']),
    (2, 'The manager spoke _____ about the new training program.',
     'Người quản lý nói một cách nhiệt tình về chương trình đào tạo mới.',
     ['enthusiasm', 'enthusiastic', 'enthusiast', 'enthusiastically'], ['sự nhiệt tình', 'nhiệt tình', 'người nhiệt tình với một lĩnh vực', 'một cách nhiệt tình'], 'D',
     'Cần trạng từ bổ nghĩa cho spoke nên chọn enthusiastically. Enthusiasm và enthusiast là danh từ; enthusiastic là tính từ.', ['manager', 'training']),
    (2, 'The report must _____ by the supervisor before publication.',
     'Báo cáo phải được người giám sát xem xét trước khi công bố.',
     ['review', 'be reviewed', 'reviewing', 'reviewed'], ['xem xét', 'được xem xét', 'đang xem xét', 'đã xem xét'], 'B',
     'Report là đối tượng được xem xét, và by the supervisor chỉ người thực hiện. Bị động sau must có dạng must be + V3: must be reviewed. Các phương án khác thiếu cấu trúc bị động đầy đủ.', ['report', 'supervisor']),
    (2, 'This printer is _____ than the one in the reception area.',
     'Máy in này nhanh hơn chiếc ở khu vực lễ tân.',
     ['fast', 'fastest', 'faster', 'fastly'], ['nhanh', 'nhanh nhất', 'nhanh hơn', 'dạng không chuẩn của fast'], 'C',
     'Than báo hiệu so sánh hơn. Tính từ ngắn fast đổi thành faster. Fast chưa có dạng so sánh; fastest là so sánh nhất; không dùng fastly.', ['printer', 'reception']),
    (2, 'The employees _____ completed the course will receive certificates.',
     'Những nhân viên đã hoàn thành khóa học sẽ nhận được chứng chỉ.',
     ['who', 'whose', 'which', 'whom'], ['người mà (chủ ngữ)', 'có ... (sở hữu)', 'cái mà', 'người mà (tân ngữ)'], 'A',
     'Đại từ quan hệ thay cho employees và làm chủ ngữ của completed, nên dùng who. Whose diễn tả sở hữu và cần danh từ theo sau; which dùng cho vật; whom là tân ngữ.', ['employees', 'certificates']),
    (2, 'We have used this supplier _____ 2022.',
     'Chúng tôi đã sử dụng nhà cung cấp này từ năm 2022.',
     ['for', 'during', 'since', 'while'], ['trong khoảng', 'trong suốt', 'kể từ', 'trong khi'], 'C',
     'Hiện tại hoàn thành kết hợp since + mốc thời gian: since 2022. For đi với khoảng thời gian; during không diễn tả điểm bắt đầu kéo dài đến hiện tại; while đi với mệnh đề.', ['supplier']),
    (2, 'Please contact the manager if you need _____ information.',
     'Vui lòng liên hệ người quản lý nếu bạn cần thêm thông tin.',
     ['many', 'a few', 'several', 'additional'], ['nhiều (đếm được)', 'một vài (đếm được)', 'một số (đếm được)', 'bổ sung'], 'D',
     'Information là danh từ không đếm được. Additional information nghĩa là thông tin bổ sung. Many, a few và several đòi hỏi danh từ đếm được số nhiều.', ['manager', 'information']),
    (3, '_____ the heavy rain, all deliveries arrived on time.',
     'Mặc dù mưa lớn, tất cả các chuyến giao hàng đều đến đúng giờ.',
     ['Although', 'Despite', 'Because', 'Unless'], ['mặc dù (+ mệnh đề)', 'mặc dù (+ danh từ)', 'bởi vì', 'trừ khi'], 'B',
     'Sau chỗ trống là cụm danh từ the heavy rain. Despite + cụm danh từ diễn tả nhượng bộ. Although, because và unless cần mệnh đề có chủ ngữ, động từ.', ['deliveries']),
    (3, 'By the time the meeting starts tomorrow, we _____ the report.',
     'Trước lúc cuộc họp bắt đầu vào ngày mai, chúng tôi sẽ hoàn thành báo cáo.',
     ['completed', 'had completed', 'will have completed', 'complete'], ['đã hoàn thành (quá khứ đơn)', 'đã hoàn thành (quá khứ hoàn thành)', 'sẽ hoàn thành trước mốc tương lai', 'hoàn thành (hiện tại)'], 'C',
     'Hành động hoàn thành báo cáo xảy ra trước một mốc tương lai, nên dùng tương lai hoàn thành will have + V3. Completed và had completed thuộc quá khứ; complete không thể hiện quan hệ hoàn tất trước mốc tương lai này.', ['meeting', 'report']),
    (3, 'The supervisor asked the team to _____ the cause of the delay.',
     'Người giám sát yêu cầu nhóm điều tra nguyên nhân chậm trễ.',
     ['investigation', 'investigative', 'investigated', 'investigate'], ['cuộc điều tra', 'mang tính điều tra', 'đã điều tra', 'điều tra'], 'D',
     'Ask someone to + động từ nguyên mẫu, nên chọn investigate. Investigation là danh từ, investigative là tính từ, investigated là dạng quá khứ/phân từ.', ['supervisor', 'delay']),
    (3, 'No refunds will be issued _____ customers present a receipt.',
     'Sẽ không hoàn tiền trừ khi khách hàng xuất trình hóa đơn.',
     ['unless', 'despite', 'because of', 'during'], ['trừ khi', 'mặc dù', 'bởi vì (+ danh từ)', 'trong suốt'], 'A',
     'Sau chỗ trống là mệnh đề customers present a receipt. Unless diễn đạt điều kiện ngoại lệ: không hoàn tiền nếu không có hóa đơn. Despite, because of và during không đứng trực tiếp trước mệnh đề này.', ['refunds', 'receipt']),
    (3, 'The equipment requires regular _____ to remain safe.',
     'Thiết bị cần được bảo trì định kỳ để duy trì sự an toàn.',
     ['maintain', 'maintained', 'maintenance', 'maintaining'], ['bảo trì', 'được bảo trì', 'sự bảo trì', 'việc đang bảo trì'], 'C',
     'Sau tính từ regular cần danh từ, cụm tự nhiên là regular maintenance. Maintain là động từ nguyên mẫu; maintained là phân từ; maintaining không phải danh từ chỉ hoạt động bảo trì thường dùng trong cụm này.', ['equipment', 'maintenance']),
    (3, 'The new supplier offers a _____ range of products than our previous partner.',
     'Nhà cung cấp mới cung cấp danh mục sản phẩm đa dạng hơn đối tác trước của chúng tôi.',
     ['widely', 'widest', 'width', 'wider'], ['một cách rộng rãi', 'rộng nhất', 'chiều rộng', 'rộng hơn'], 'D',
     'Chỗ trống bổ nghĩa cho range và được so sánh bằng than, nên dùng tính từ so sánh hơn wider. Widely là trạng từ; widest là so sánh nhất; width là danh từ.', ['supplier', 'products']),
    (4, 'Had the shipment arrived earlier, we _____ the deadline.',
     'Nếu lô hàng đã đến sớm hơn, chúng tôi đã có thể đáp ứng hạn chót.',
     ['will meet', 'would have met', 'have met', 'meet'], ['sẽ đáp ứng', 'đã có thể đáp ứng (giả định)', 'đã đáp ứng', 'đáp ứng'], 'B',
     'Had the shipment arrived là đảo ngữ của If the shipment had arrived. Điều kiện loại 3 dùng would have + V3 trong mệnh đề chính để diễn tả kết quả trái với quá khứ.', ['shipment', 'deadline']),
    (4, 'The documents _____ to the application must be signed.',
     'Các tài liệu được đính kèm đơn phải có chữ ký.',
     ['attaching', 'attachment', 'attach', 'attached'], ['đang đính kèm', 'tệp đính kèm', 'đính kèm', 'được đính kèm'], 'D',
     'Documents nhận hành động đính kèm. Mệnh đề quan hệ bị động which are attached to được rút gọn thành attached to. Attaching mang nghĩa chủ động; attachment là danh từ; attach không tạo được cấu trúc bổ nghĩa này.', ['documents', 'application']),
    (4, 'The manager recommended that the policy _____ immediately.',
     'Người quản lý đề nghị chính sách được sửa đổi ngay lập tức.',
     ['be revised', 'is revising', 'revises', 'was revising'], ['được sửa đổi (giả định)', 'đang sửa đổi', 'sửa đổi', 'đã đang sửa đổi'], 'A',
     'Recommend that diễn đạt đề nghị, dùng dạng giả định với động từ nguyên mẫu. Policy nhận hành động sửa đổi nên dùng be revised. Các phương án còn lại đều chủ động và không phù hợp với cấu trúc đề nghị.', ['manager', 'policy']),
    (4, 'Only after the inspection _____ allowed to use the equipment.',
     'Chỉ sau cuộc kiểm tra, các nhân viên mới được phép sử dụng thiết bị.',
     ['the employees were', 'the employees', 'were the employees', 'being the employees'], ['nhân viên đã được (trật tự thường)', 'các nhân viên', 'nhân viên đã được (đảo ngữ)', 'cấu trúc V-ing không phù hợp'], 'C',
     'Only after + cụm danh từ đặt đầu câu yêu cầu đảo trợ động từ lên trước chủ ngữ trong mệnh đề chính: were the employees allowed. A không đảo ngữ; B thiếu động từ; D không tạo thành mệnh đề hoàn chỉnh.', ['inspection', 'equipment']),
    (4, 'The contract will remain valid _____ both parties agree to terminate it.',
     'Hợp đồng sẽ vẫn có hiệu lực trừ khi cả hai bên đồng ý chấm dứt.',
     ['unless', 'in spite of', 'whereas', 'so that'], ['trừ khi', 'mặc dù (+ danh từ)', 'trong khi (đối lập)', 'để mà'], 'A',
     'Unless đưa ra điều kiện khiến hợp đồng không còn hiệu lực. In spite of cần danh từ hoặc V-ing; whereas diễn tả đối lập, không phải ngoại lệ; so that diễn tả mục đích, không hợp logic câu.', ['contract', 'parties']),
    (4, 'The supplier is committed to _____ packaging waste.',
     'Nhà cung cấp cam kết giảm rác thải bao bì.',
     ['reduce', 'reduction', 'reduced', 'reducing'], ['giảm', 'sự cắt giảm', 'đã giảm', 'việc giảm'], 'D',
     'Trong be committed to, to là giới từ nên theo sau bằng danh từ hoặc V-ing. Reducing nhận tân ngữ packaging waste. Reduction cần of trước tân ngữ; reduce và reduced không phù hợp ở đây.', ['supplier', 'packaging']),
    (5, 'Not until all documents had been checked _____ the shipment.',
     'Mãi đến khi tất cả tài liệu đã được kiểm tra, nhân viên mới cho xuất lô hàng.',
     ['the staff released', 'did the staff release', 'the staff had release', 'had released the staff'], ['nhân viên đã cho xuất (trật tự thường)', 'nhân viên mới cho xuất (đảo ngữ)', 'cấu trúc sai phân từ', 'trật tự đảo ngữ không phù hợp'], 'B',
     'Not until + mệnh đề đứng đầu yêu cầu đảo ngữ ở mệnh đề chính. Với quá khứ đơn, dùng did + chủ ngữ + nguyên mẫu: did the staff release. A không đảo ngữ; C sai had + V3; D đảo sai trật tự và sai quan hệ thời gian.', ['documents', 'shipment']),
    (5, 'The proposal, _____ financial implications were initially overlooked, was later rejected.',
     'Đề xuất, mà các tác động tài chính ban đầu đã bị bỏ qua, sau đó bị bác bỏ.',
     ['which', 'whom', 'whose', 'that'], ['cái mà', 'người mà (tân ngữ)', 'có ... (sở hữu)', 'mà'], 'C',
     'Cần quan hệ sở hữu giữa proposal và financial implications, nên dùng whose. Whose có thể chỉ sở hữu của vật. Which không trực tiếp bổ nghĩa sở hữu cho danh từ; whom dùng cho người; that không dùng trong mệnh đề quan hệ không xác định sau dấu phẩy.', ['proposal', 'implications']),
    (5, 'Were the company to expand overseas, it _____ additional legal advice.',
     'Nếu công ty mở rộng ra nước ngoài, công ty sẽ cần thêm tư vấn pháp lý.',
     ['would need', 'had needed', 'will have needed', 'needs'], ['sẽ cần (giả định)', 'đã cần trước một mốc quá khứ', 'sẽ đã cần', 'cần (hiện tại)'], 'A',
     'Were the company to expand là dạng đảo của If the company were to expand, nêu giả định loại 2. Kết quả dùng would + nguyên mẫu: would need. Các dạng thì còn lại không khớp cấu trúc điều kiện giả định này.', ['company', 'advice']),
    (5, 'The revised policy is intended to _____ the risk of unauthorized access.',
     'Chính sách đã sửa đổi nhằm giảm thiểu nguy cơ truy cập trái phép.',
     ['retain', 'exceed', 'resume', 'mitigate'], ['giữ lại', 'vượt quá', 'tiếp tục lại', 'giảm thiểu'], 'D',
     'Cả bốn phương án đều là động từ nguyên mẫu, vì vậy cần xét nghĩa và kết hợp từ. Mitigate the risk nghĩa là giảm thiểu rủi ro. Retain là giữ lại, exceed là vượt quá, resume là tiếp tục lại; không phù hợp mục đích chính sách bảo mật.', ['policy', 'access']),
    (5, 'The figures in the report are _____ with the results of the independent audit.',
     'Các số liệu trong báo cáo nhất quán với kết quả của cuộc kiểm toán độc lập.',
     ['considerate', 'consistent', 'considerable', 'consistently'], ['chu đáo', 'nhất quán', 'đáng kể', 'một cách nhất quán'], 'B',
     'Cụm be consistent with nghĩa là nhất quán với. Considerate chỉ sự chu đáo; considerable nghĩa là đáng kể; consistently là trạng từ, không làm bổ ngữ tính từ sau are trong cấu trúc này.', ['report', 'audit']),
    (5, '_____ approval from both parties, the contract may be extended for another year.',
     'Tùy thuộc vào sự chấp thuận của cả hai bên, hợp đồng có thể được gia hạn thêm một năm.',
     ['Regardless of', 'On behalf of', 'Subject to', 'Instead of'], ['bất kể', 'thay mặt cho', 'tùy thuộc vào / với điều kiện', 'thay vì'], 'C',
     'Subject to approval diễn tả điều kiện phải được chấp thuận trước khi gia hạn. Regardless of là bất chấp, trái với điều kiện phê duyệt; on behalf of là thay mặt; instead of là thay vì.', ['approval', 'contract']),
]

# word, IPA, meaning, example EN, example VI; original workplace examples.
VOCABULARY = [
 ('employees','/ɪmˈplɔɪiːz/','nhân viên (số nhiều)','Employees attend training every month.','Nhân viên tham gia đào tạo hằng tháng.'),
 ('employee','/ɪmˈplɔɪiː/','nhân viên','Each employee receives a badge.','Mỗi nhân viên nhận một thẻ.'),
 ('machinery','/məˈʃiːnəri/','máy móc (danh từ không đếm được)','Keep the machinery clean.','Giữ máy móc sạch sẽ.'),
 ('deadlines','/ˈdedlaɪnz/','các hạn chót','The team meets all project deadlines.','Nhóm đáp ứng mọi hạn chót của dự án.'),
 ('deadline','/ˈdedlaɪn/','hạn chót','The deadline is next Tuesday.','Hạn chót là thứ Ba tới.'),
 ('reception','/rɪˈsepʃən/','khu vực / bộ phận lễ tân','Please leave the package at reception.','Vui lòng để gói hàng ở lễ tân.'),
 ('application','/ˌæplɪˈkeɪʃən/','đơn ứng tuyển; đơn đăng ký','We received her application yesterday.','Chúng tôi đã nhận đơn của cô ấy hôm qua.'),
 ('training','/ˈtreɪnɪŋ/','sự đào tạo; khóa đào tạo','The training covers safety procedures.','Khóa đào tạo bao gồm các quy trình an toàn.'),
 ('meeting','/ˈmiːtɪŋ/','cuộc họp','The meeting lasts one hour.','Cuộc họp kéo dài một giờ.'),
 ('manager','/ˈmænɪdʒər/','người quản lý','Ask the manager for assistance.','Hãy nhờ người quản lý hỗ trợ.'),
 ('report','/rɪˈpɔːrt/','báo cáo','This report summarizes our progress.','Báo cáo này tóm tắt tiến độ của chúng ta.'),
 ('supervisor','/ˈsuːpərvaɪzər/','người giám sát','The supervisor checks every order.','Người giám sát kiểm tra từng đơn hàng.'),
 ('printer','/ˈprɪntər/','máy in','The printer needs more paper.','Máy in cần thêm giấy.'),
 ('certificates','/sərˈtɪfɪkəts/','chứng chỉ; giấy chứng nhận','Participants collect their certificates after the course.','Người tham gia nhận chứng chỉ sau khóa học.'),
 ('supplier','/səˈplaɪər/','nhà cung cấp','Our supplier delivers twice a week.','Nhà cung cấp của chúng tôi giao hàng hai lần mỗi tuần.'),
 ('information','/ˌɪnfərˈmeɪʃən/','thông tin (không đếm được)','Further information is available online.','Có thể tìm thêm thông tin trên mạng.'),
 ('deliveries','/dɪˈlɪvəriz/','các chuyến giao hàng','Deliveries stop at five o’clock.','Các chuyến giao hàng kết thúc lúc năm giờ.'),
 ('delay','/dɪˈleɪ/','sự chậm trễ','We apologize for the delay.','Chúng tôi xin lỗi vì sự chậm trễ.'),
 ('refunds','/ˈriːfʌndz/','các khoản hoàn tiền','Refunds take three business days.','Việc hoàn tiền mất ba ngày làm việc.'),
 ('receipt','/rɪˈsiːt/','biên lai; hóa đơn thanh toán','Keep your receipt for returns.','Giữ hóa đơn để đổi trả hàng.'),
 ('equipment','/ɪˈkwɪpmənt/','thiết bị (không đếm được)','This equipment is easy to use.','Thiết bị này dễ sử dụng.'),
 ('maintenance','/ˈmeɪntənəns/','việc bảo trì','Regular maintenance prevents breakdowns.','Bảo trì định kỳ giúp ngăn ngừa hỏng hóc.'),
 ('products','/ˈprɑːdʌkts/','sản phẩm (số nhiều)','We inspect products before shipping.','Chúng tôi kiểm tra sản phẩm trước khi vận chuyển.'),
 ('shipment','/ˈʃɪpmənt/','lô hàng','The shipment arrived this morning.','Lô hàng đã đến sáng nay.'),
 ('documents','/ˈdɑːkjumənts/','tài liệu; giấy tờ','Please sign these documents.','Vui lòng ký các tài liệu này.'),
 ('policy','/ˈpɑːləsi/','chính sách','The policy applies to all staff.','Chính sách áp dụng cho toàn bộ nhân viên.'),
 ('inspection','/ɪnˈspekʃən/','cuộc kiểm tra','An inspection is scheduled for Friday.','Một cuộc kiểm tra được lên lịch vào thứ Sáu.'),
 ('contract','/ˈkɑːntrækt/','hợp đồng','The contract expires in June.','Hợp đồng hết hạn vào tháng Sáu.'),
 ('parties','/ˈpɑːrtiz/','các bên (trong thỏa thuận)','Both parties signed the agreement.','Cả hai bên đã ký thỏa thuận.'),
 ('packaging','/ˈpækɪdʒɪŋ/','bao bì; vật liệu đóng gói','We use recyclable packaging.','Chúng tôi sử dụng bao bì có thể tái chế.'),
 ('proposal','/prəˈpoʊzəl/','đề xuất','The board accepted the proposal.','Hội đồng đã chấp nhận đề xuất.'),
 ('implications','/ˌɪmplɪˈkeɪʃənz/','những hệ quả; tác động','Consider the financial implications first.','Hãy cân nhắc các tác động tài chính trước.'),
 ('company','/ˈkʌmpəni/','công ty','The company opened a new office.','Công ty đã mở một văn phòng mới.'),
 ('advice','/ədˈvaɪs/','lời khuyên; sự tư vấn (không đếm được)','We need professional advice.','Chúng tôi cần sự tư vấn chuyên môn.'),
 ('access','/ˈækses/','quyền truy cập; sự tiếp cận','Only staff have access to this room.','Chỉ nhân viên mới có quyền vào phòng này.'),
 ('audit','/ˈɔːdɪt/','cuộc kiểm toán','The annual audit begins in May.','Cuộc kiểm toán hằng năm bắt đầu vào tháng Năm.'),
 ('approval','/əˈpruːvəl/','sự chấp thuận','We are waiting for final approval.','Chúng tôi đang chờ sự chấp thuận cuối cùng.'),
]

def quote(value):
    return "'" + str(value).replace("'", "''") + "'"

def main():
    labels = ['Rất dễ', 'Dễ', 'Trung bình', 'Khó', 'Rất khó']
    sql = ['-- N11 original Part 5 starter pack: 30 questions / 5 editorial levels.',
           '-- Run after the team schema. Additive: does not replace existing questions or results.',
           '-- Generated by scripts/build_reading_seed.py; UTF-8. Run sequentially, not concurrently.',
           'SET AUTOCOMMIT=0;']
    set_queries = {}
    for level, label in enumerate(labels, 1):
        marker = f'[READING_PART5_LEVEL:{level}]'
        where = f"skill='READING' AND part_no=5 AND instructions LIKE {quote(marker+'%')}"
        set_queries[level] = f'(SELECT id FROM practice_sets WHERE {where} ORDER BY id LIMIT 1)'
        difficulty = ['EASY', 'EASY', 'MEDIUM', 'HARD', 'HARD'][level-1]
        sql.append(f"INSERT INTO practice_sets(skill,part_no,title,instructions,difficulty,is_published) SELECT 'READING',5,{quote('Part 5 — Cấp '+str(level)+' · '+label)},{quote(marker+' Học liệu N11 biên soạn. Chọn đáp án đúng để hoàn thành câu.')},{quote(difficulty)},TRUE WHERE NOT EXISTS (SELECT 1 FROM practice_sets WHERE {where});")
    title = quote('N11 Reading Part 5')
    topic = f'(SELECT id FROM vocabulary_topics WHERE title={title} ORDER BY id LIMIT 1)'
    sql.append(f"INSERT INTO vocabulary_topics(title,description,level,is_published) SELECT {title},'Từ vựng trong bộ luyện Reading Part 5 của N11.','INTERMEDIATE',FALSE WHERE NOT EXISTS(SELECT 1 FROM vocabulary_topics WHERE title={title});")
    sql.append(f"UPDATE vocabulary_topics SET is_published=FALSE WHERE title={title} AND description='Từ vựng trong bộ luyện Reading Part 5 của N11.' AND is_published=TRUE;")
    for word, ipa, meaning, en, vi in VOCABULARY:
        sql.append(f"INSERT INTO vocabulary_terms(topic_id,word,ipa,meaning_vi,example_en,example_vi) SELECT {topic},{quote(word)},{quote(ipa)},{quote(meaning)},{quote(en)},{quote(vi)} WHERE NOT EXISTS(SELECT 1 FROM vocabulary_terms WHERE topic_id={topic} AND word={quote(word)});")
    vocabulary = {v[0] for v in VOCABULARY}
    counters = {}
    for level, sentence, translation, options, meanings, correct, explanation, words in QUESTIONS:
        assert len(options) == len(set(options)) == len(meanings) == 4
        assert correct in 'ABCD' and set(words) <= vocabulary
        assert all(word.lower() in (sentence+' '+' '.join(options)).lower() for word in words)
        counters[level] = counters.get(level, 0) + 1
        order = counters[level]
        set_id = set_queries[level]
        meta = json.dumps(dict(translation=translation, options_vi=dict(zip('ABCD', meanings)), explanation=explanation, words=words), ensure_ascii=False)
        # Text identifies starter questions; existing unrelated questions are never overwritten.
        where = f'practice_set_id={set_id} AND question_text={quote(sentence)}'
        sql.append(f'INSERT INTO practice_questions(practice_set_id,question_text,explanation,sort_order) SELECT {set_id},{quote(sentence)},{quote(meta)},{order} WHERE NOT EXISTS(SELECT 1 FROM practice_questions WHERE {where});')
        qid = f'(SELECT id FROM practice_questions WHERE {where} ORDER BY id LIMIT 1)'
        for label, text in zip('ABCD', options):
            sql.append(f'INSERT INTO practice_options(question_id,option_label,option_text,is_correct) SELECT {qid},{quote(label)},{quote(text)},{str(label==correct).upper()} WHERE NOT EXISTS(SELECT 1 FROM practice_options WHERE question_id={qid} AND option_label={quote(label)});')
    assert counters == {i: 6 for i in range(1, 6)}
    sql += ['COMMIT;', 'SET AUTOCOMMIT=1;', '']
    output = '\n'.join(sql)
    (ROOT/'database/updates/003_reading_part5.sql').write_text(output, encoding='utf-8')
    combined = ROOT/'database/n11_toeic.sql'
    marker = '-- BEGIN N11 READING PART 5 STARTER PACK'
    original = combined.read_text(encoding='utf-8').split(marker)[0].rstrip()
    part6 = ROOT/'database/updates/004_reading_part6.sql'
    suffix = '\n-- BEGIN N11 READING PART 6 STARTER PACK\n'+part6.read_text(encoding='utf-8') if part6.exists() else ''
    part7 = ROOT/'database/updates/005_reading_part7.sql'
    if part7.exists(): suffix += '\n-- BEGIN N11 READING PART 7 STARTER PACK\n'+part7.read_text(encoding='utf-8')
    expansion = ROOT/'database/updates/006_reading_expansion.sql'
    if expansion.exists(): suffix += '\n-- BEGIN N11 READING EXPANSION\n'+expansion.read_text(encoding='utf-8')
    combined.write_text(original+'\n\n'+marker+'\n'+output+suffix, encoding='utf-8')
    print(f'Generated {len(QUESTIONS)} questions and {len(VOCABULARY)} vocabulary terms.')

if __name__ == '__main__':
    main()
