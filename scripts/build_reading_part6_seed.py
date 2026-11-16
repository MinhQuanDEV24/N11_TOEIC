"""Original N11 Part 6 starter content; additive SQL, no schema changes."""
import json
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
def q(s): return "'"+str(s).replace("'","''")+"'"
# title, bilingual paragraphs, four (choices, meanings, answer, explanation), glossary
PACK=[
('Office Welcome Session',[
('To: New employees\nSubject: Welcome session','Gửi: Nhân viên mới\nChủ đề: Buổi chào đón'),
('Our welcome session [131] at 9 a.m. every Monday. Please arrive ten minutes early.','Buổi chào đón của chúng tôi bắt đầu lúc 9 giờ sáng thứ Hai hằng tuần. Vui lòng đến sớm mười phút.'),
('The session takes place [132] Room 204. Bring your employee badge.','Buổi chào đón diễn ra tại phòng 204. Hãy mang theo thẻ nhân viên.'),
('Our team will explain the safety rules [133]. There will be time for questions at the end.','Nhóm chúng tôi sẽ giải thích rõ ràng các quy định an toàn. Cuối buổi sẽ có thời gian dành cho câu hỏi.'),
('[134] We look forward to meeting you on Monday.','Vui lòng xác nhận tham dự bằng cách trả lời email này. Chúng tôi mong được gặp bạn vào thứ Hai.')],[
(['begin','begins','beginning','to begin'],['bắt đầu (nguyên mẫu)','bắt đầu (ngôi thứ ba số ít)','đang bắt đầu','để bắt đầu'],'B','Every Monday chỉ thói quen. Chủ ngữ session số ít nên dùng begins; beginning và to begin không làm động từ chính chia thì.'),
(['on','at','in','by'],['trên','tại','trong','bên cạnh'],'C','In Room 204 chỉ vị trí bên trong phòng. On, at và by không phù hợp với cách diễn đạt vị trí phòng này.'),
(['clear','clearness','clearly','clearing'],['rõ ràng (tính từ)','sự rõ ràng','một cách rõ ràng','việc dọn dẹp'],'C','Cần trạng từ clearly bổ nghĩa cho explain. Clear là tính từ, clearness là danh từ, clearing là V-ing/danh từ.'),
(['Please confirm your attendance by replying to this email.','The cafeteria was renovated last year.','All deliveries have been canceled.','Our products are sold overseas.'],['Vui lòng trả lời email để xác nhận tham dự.','Nhà ăn đã được sửa năm ngoái.','Tất cả chuyến giao hàng đã bị hủy.','Sản phẩm của chúng tôi được bán ở nước ngoài.'],'A','Email mời nhân viên mới dự buổi chào đón nên lời nhắc xác nhận tham dự nối tiếp hợp lý trước lời kết. Các lựa chọn khác không liên quan.')],
{'session':'buổi học; phiên họp','employees':'nhân viên','badge':'thẻ nhân viên','safety':'sự an toàn','attendance':'sự tham dự','confirm':'xác nhận','arrive':'đến'}),
('Updated Delivery Schedule',[
('To: Store managers\nFrom: Distribution team','Gửi: Quản lý cửa hàng\nTừ: Nhóm phân phối'),
('Because our warehouse is being repaired, deliveries [131] from the temporary facility next week.','Vì kho đang được sửa chữa, các chuyến hàng sẽ được gửi từ cơ sở tạm thời vào tuần tới.'),
('Please check your order [132] before submitting it. Changes cannot be made after dispatch.','Vui lòng kiểm tra kỹ đơn hàng trước khi gửi. Không thể thay đổi sau khi hàng được gửi đi.'),
('We have worked with the same courier [133] 2021, and its drivers know all our stores.','Chúng tôi đã hợp tác với cùng một đơn vị chuyển phát từ năm 2021 và các tài xế biết tất cả cửa hàng của chúng tôi.'),
('[134] You can use that number to follow the progress of your delivery online.','Một mã theo dõi sẽ được gửi qua email khi đơn hàng rời kho. Bạn có thể dùng mã đó để theo dõi tiến trình giao hàng trên mạng.')],[
(['will send','will be sent','sending','to send'],['sẽ gửi','sẽ được gửi','đang gửi','để gửi'],'B','Deliveries nhận hành động gửi; next week chỉ tương lai. Cấu trúc bị động tương lai là will be sent. Will send mang nghĩa chủ động.'),
(['carefully','careful','care','caring'],['một cách cẩn thận','cẩn thận','sự chăm sóc','quan tâm'],'A','Trạng từ carefully bổ nghĩa cho check. Careful là tính từ; care là danh từ/động từ; caring không phù hợp vị trí bổ nghĩa cách thức.'),
(['during','for','while','since'],['trong suốt','trong khoảng','trong khi','kể từ'],'D','Have worked dùng hiện tại hoàn thành, kết hợp since + mốc 2021. For cần khoảng thời gian, while cần mệnh đề.'),
(['Our office closes at six.','The warehouse has several loading docks.','A tracking number will be emailed when your order leaves the warehouse.','The courier recently hired a new accountant.'],['Văn phòng đóng cửa lúc sáu giờ.','Kho có một số bến bốc hàng.','Mã theo dõi sẽ được gửi qua email khi đơn hàng rời kho.','Đơn vị chuyển phát vừa tuyển kế toán mới.'],'C','That number ở câu sau cần một mã đã được nhắc đến. Chỉ phương án C giới thiệu tracking number đúng với việc theo dõi giao hàng.')],
{'warehouse':'nhà kho','deliveries':'các chuyến giao hàng','temporary':'tạm thời','facility':'cơ sở','courier':'đơn vị chuyển phát','dispatch':'việc gửi hàng','tracking':'việc theo dõi'}),
('Conference Registration',[
('Annual Business Conference\nRegistration notice','Hội nghị kinh doanh thường niên\nThông báo đăng ký'),
('[131] the limited number of seats, early registration is strongly recommended.','Do số lượng chỗ ngồi hạn chế, việc đăng ký sớm rất được khuyến khích.'),
('Participants [132] register before June 1 will receive a discount. Payment must be completed online.','Những người tham dự đăng ký trước ngày 1 tháng 6 sẽ được giảm giá. Việc thanh toán phải hoàn tất trực tuyến.'),
('By the time the conference opens, the organizers [133] all presentation materials.','Trước lúc hội nghị khai mạc, ban tổ chức sẽ đã chuẩn bị xong toàn bộ tài liệu thuyết trình.'),
('[134] Please show this message at the registration desk to collect your badge.','Email xác nhận sẽ được gửi sau khi khoản thanh toán của bạn được xử lý. Vui lòng xuất trình email này tại bàn đăng ký để nhận thẻ.')],[
(['Because','Because of','Although','Unless'],['bởi vì (+ mệnh đề)','do (+ danh từ)','mặc dù','trừ khi'],'B','Sau chỗ trống là cụm danh từ the limited number of seats. Because of + danh từ chỉ nguyên nhân; các phương án còn lại cần mệnh đề.'),
(['who','whose','whom','which'],['người mà (chủ ngữ)','có ... (sở hữu)','người mà (tân ngữ)','cái mà'],'A','Who thay cho participants và làm chủ ngữ của register. Whose cần danh từ, whom là tân ngữ và which dùng cho vật.'),
(['had prepared','preparing','will have prepared','prepare'],['đã chuẩn bị trước mốc quá khứ','đang chuẩn bị','sẽ chuẩn bị xong trước mốc tương lai','chuẩn bị'],'C','By the time the conference opens chỉ một mốc tương lai. Hành động hoàn tất trước đó dùng will have + V3.'),
(['The venue has a large parking area.','The speakers come from several countries.','A printed catalog is available in the lobby.','A confirmation email will be sent after your payment is processed.'],['Địa điểm có bãi đỗ xe lớn.','Diễn giả đến từ nhiều nước.','Có danh mục in sẵn ở sảnh.','Email xác nhận sẽ được gửi sau khi thanh toán được xử lý.'],'D','This message trong câu sau chỉ email xác nhận ở D. Những lựa chọn khác không cung cấp thông báo để xuất trình tại bàn đăng ký.')],
{'registration':'việc đăng ký','participants':'người tham dự','discount':'giảm giá','payment':'thanh toán','organizers':'ban tổ chức','confirmation':'sự xác nhận','badge':'thẻ'}),
('Equipment Safety Review',[
('MEMORANDUM\nTo: Production staff\nSubject: Equipment inspection','BẢN GHI NHỚ\nGửi: Nhân viên sản xuất\nChủ đề: Kiểm tra thiết bị'),
('Following last week’s inspection, the supervisor recommended that every machine [131] before use.','Sau đợt kiểm tra tuần trước, người giám sát đề nghị mọi máy đều được kiểm tra trước khi sử dụng.'),
('The instructions [132] to this notice describe the new procedure. Please read them before your next shift.','Hướng dẫn được đính kèm thông báo này mô tả quy trình mới. Vui lòng đọc trước ca làm tiếp theo.'),
('The company is committed to [133] workplace accidents through regular training.','Công ty cam kết giảm tai nạn lao động thông qua đào tạo định kỳ.'),
('Only certified technicians may repair the equipment. [134] Instead, report any faults to the maintenance desk.','Chỉ kỹ thuật viên có chứng chỉ mới được sửa thiết bị. Không được tự sửa chữa. Thay vào đó, hãy báo lỗi cho bộ phận bảo trì.')],[
(['is checking','be checked','checks','was checking'],['đang kiểm tra','được kiểm tra (giả định)','kiểm tra','đã đang kiểm tra'],'B','Recommended that diễn tả đề nghị, dùng động từ nguyên mẫu dạng giả định. Machine nhận hành động nên dùng be checked, không dùng dạng chủ động.'),
(['attaching','attachment','attached','attach'],['đang đính kèm','tệp đính kèm','được đính kèm','đính kèm'],'C','Mệnh đề quan hệ bị động which are attached được rút gọn thành attached. Instructions nhận hành động đính kèm.'),
(['reducing','reduce','reduced','reduction'],['việc giảm','giảm','đã giảm','sự cắt giảm'],'A','To trong be committed to là giới từ, sau nó dùng V-ing. Reducing nhận tân ngữ accidents; reduction cần of trước tân ngữ.'),
(['The cafeteria serves lunch at noon.','New machines are available for purchase.','The company opened a second office.','Do not attempt any repairs yourself.'],['Nhà ăn phục vụ bữa trưa lúc mười hai giờ.','Có máy mới để mua.','Công ty đã mở văn phòng thứ hai.','Không được tự thực hiện sửa chữa.'],'D','Câu trước giới hạn người được sửa máy, câu sau dùng Instead để hướng dẫn cách thay thế. D tạo mạch: không tự sửa, thay vào đó báo bộ phận bảo trì.')],
{'inspection':'cuộc kiểm tra','supervisor':'người giám sát','procedure':'quy trình','shift':'ca làm việc','certified':'có chứng chỉ','faults':'các lỗi','maintenance':'bảo trì'}),
('Supplier Contract Renewal',[
('To: Procurement committee\nSubject: Renewal of supply agreement','Gửi: Hội đồng mua sắm\nChủ đề: Gia hạn thỏa thuận cung ứng'),
('The supplier’s proposal, [131] financial implications were initially unclear, has now been reviewed by the finance team.','Đề xuất của nhà cung cấp, có các tác động tài chính ban đầu chưa rõ, hiện đã được nhóm tài chính xem xét.'),
('[132] approval from both parties, the agreement may be extended for two years.','Với điều kiện được cả hai bên chấp thuận, thỏa thuận có thể được gia hạn hai năm.'),
('Had the supplier failed the audit, we [133] an alternative partner. Fortunately, all requirements were met.','Nếu nhà cung cấp không vượt qua kiểm toán, chúng ta đã phải tìm một đối tác khác. May mắn là mọi yêu cầu đều được đáp ứng.'),
('The revised agreement includes a clause allowing us to terminate it if delivery standards are repeatedly missed. [134] Please review the final draft before Friday.','Thỏa thuận sửa đổi có điều khoản cho phép chấm dứt nếu tiêu chuẩn giao hàng liên tục không được đáp ứng. Quy định này giúp bảo vệ chúng ta khỏi các gián đoạn kéo dài. Vui lòng xem bản dự thảo cuối trước thứ Sáu.')],[
(['which','whom','that','whose'],['cái mà','người mà (tân ngữ)','mà','có ... (sở hữu)'],'D','Whose chỉ quan hệ sở hữu giữa proposal và financial implications, có thể dùng cho vật. That không dùng trong mệnh đề không xác định; which không trực tiếp chỉ sở hữu.'),
(['Regardless of','Subject to','On behalf of','Instead of'],['bất kể','với điều kiện / tùy thuộc vào','thay mặt cho','thay vì'],'B','Subject to approval là cụm chỉ điều kiện phải được chấp thuận. Các cụm còn lại không diễn đạt điều kiện phê duyệt.'),
(['will seek','seek','would have sought','have sought'],['sẽ tìm','tìm','đã phải tìm (giả định quá khứ)','đã tìm'],'C','Had the supplier failed là đảo ngữ của điều kiện loại 3. Mệnh đề kết quả dùng would have + V3; quá khứ phân từ của seek là sought.'),
(['This provision helps protect us against prolonged disruptions.','The supplier’s offices are near the airport.','The committee meets in a room on the third floor.','Our previous catalog contained forty pages.'],['Quy định này giúp bảo vệ chúng ta khỏi gián đoạn kéo dài.','Văn phòng nhà cung cấp ở gần sân bay.','Hội đồng họp trong phòng ở tầng ba.','Danh mục trước có bốn mươi trang.'],'A','This provision liên kết trực tiếp với điều khoản chấm dứt hợp đồng ở câu trước và giải thích lợi ích của nó. Các lựa chọn khác làm đứt mạch nội dung.')],
{'procurement':'hoạt động mua sắm của tổ chức','proposal':'đề xuất','implications':'các hệ quả; tác động','agreement':'thỏa thuận','audit':'cuộc kiểm toán','clause':'điều khoản','terminate':'chấm dứt','disruptions':'sự gián đoạn'})]

def main():
 sql=['-- N11 Part 6: original starter pack, 5 passages / 20 questions.','SET AUTOCOMMIT=0;']
 for level,(title,lines,questions,glossary) in enumerate(PACK,1):
  marker=f'[READING_PART6_LEVEL:{level}]'
  where=f"skill='READING' AND part_no=6 AND instructions LIKE {q(marker+'%')}"
  sid=f'(SELECT id FROM practice_sets WHERE {where} ORDER BY id LIMIT 1)'
  sql.append(f"INSERT INTO practice_sets(skill,part_no,title,instructions,difficulty,is_published) SELECT 'READING',6,{q('Part 6 — Cấp '+str(level))},{q(marker+' Chọn đủ 4 câu của bài đọc để xem kết quả.')},{q(['EASY','EASY','MEDIUM','HARD','HARD'][level-1])},TRUE WHERE NOT EXISTS(SELECT 1 FROM practice_sets WHERE {where});")
  payload=json.dumps(dict(title=title,lines=[dict(en=en.replace("\n"," · "),vi=vi.replace("\n"," · ")) for en,vi in lines],glossary=glossary),ensure_ascii=False)
  sql.append(f'INSERT INTO practice_passages(practice_set_id,passage_text,sort_order) SELECT {sid},{q(payload)},1 WHERE NOT EXISTS(SELECT 1 FROM practice_passages WHERE practice_set_id={sid} AND sort_order=1);')
  pid=f'(SELECT id FROM practice_passages WHERE practice_set_id={sid} AND sort_order=1 ORDER BY id LIMIT 1)'
  for i,(options,translations,correct,explanation) in enumerate(questions,131):
   meta=json.dumps(dict(options_vi=dict(zip('ABCD',translations)),explanation=explanation),ensure_ascii=False)
   qw=f'practice_set_id={sid} AND passage_id={pid} AND sort_order={i}'
   sql.append(f'INSERT INTO practice_questions(practice_set_id,passage_id,question_text,explanation,sort_order) SELECT {sid},{pid},{q("Chọn đáp án cho chỗ trống ["+str(i)+"]")},{q(meta)},{i} WHERE NOT EXISTS(SELECT 1 FROM practice_questions WHERE {qw});')
   qid=f'(SELECT id FROM practice_questions WHERE {qw} ORDER BY id LIMIT 1)'
   for label,option in zip('ABCD',options):
    sql.append(f'INSERT INTO practice_options(question_id,option_label,option_text,is_correct) SELECT {qid},{q(label)},{q(option)},{str(label==correct).upper()} WHERE NOT EXISTS(SELECT 1 FROM practice_options WHERE question_id={qid} AND option_label={q(label)});')
 sql+=['COMMIT;','SET AUTOCOMMIT=1;','']
 output='\n'.join(sql);(ROOT/'database/updates/004_reading_part6.sql').write_text(output,encoding='utf-8')
 combined=ROOT/'database/n11_toeic.sql';marker='-- BEGIN N11 READING PART 6 STARTER PACK'
 part7=ROOT/'database/updates/005_reading_part7.sql'
 suffix='\n-- BEGIN N11 READING PART 7 STARTER PACK\n'+part7.read_text(encoding='utf-8') if part7.exists() else ''
 expansion=ROOT/'database/updates/006_reading_expansion.sql'
 if expansion.exists(): suffix += '\n-- BEGIN N11 READING EXPANSION\n'+expansion.read_text(encoding='utf-8')
 combined.write_text(combined.read_text(encoding='utf-8').split(marker)[0].rstrip()+'\n\n'+marker+'\n'+output+suffix,encoding='utf-8')
 print('Generated 5 passages and 20 Part 6 questions.')
if __name__=='__main__': main()
