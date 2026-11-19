INSERT INTO `vocabulary_terms`
  (`topic_id`, `word`, `ipa`, `meaning_vi`, `definition_en`, `example_en`, `example_vi`, `audio_media_id`)
SELECT topic.`id`, terms.`word`, terms.`ipa`, terms.`meaning_vi`, terms.`definition_en`,
       terms.`example_en`, terms.`example_vi`, NULL
FROM `vocabulary_topics` topic
CROSS JOIN (
  SELECT 'departure' AS `word`, '/dɪˈpɑːrtʃər/' AS `ipa`, 'sự khởi hành' AS `meaning_vi`,
         'the act of leaving a place' AS `definition_en`,
         'Passengers should arrive at the terminal before departure.' AS `example_en`,
         'Hành khách nên đến nhà ga trước giờ khởi hành.' AS `example_vi`
  UNION ALL SELECT 'arrival', '/əˈraɪvəl/', 'sự đến nơi', 'the act of reaching a destination',
         'The arrival of the train was delayed by fifteen minutes.',
         'Chuyến tàu đến nơi muộn mười lăm phút.'
  UNION ALL SELECT 'destination', '/ˌdestɪˈneɪʃən/', 'điểm đến',
         'the place to which someone or something is going',
         'Our final destination is a small town near the coast.',
         'Điểm đến cuối cùng của chúng tôi là một thị trấn nhỏ gần bờ biển.'
  UNION ALL SELECT 'itinerary', '/aɪˈtɪnəreri/', 'lịch trình chuyến đi',
         'a plan of a journey, including the route and places visited',
         'The travel agent emailed us a detailed itinerary.',
         'Đại lý du lịch đã gửi cho chúng tôi lịch trình chi tiết.'
  UNION ALL SELECT 'accommodation', '/əˌkɑːməˈdeɪʃən/', 'chỗ ở',
         'a place where travelers can stay',
         'We booked affordable accommodation close to the station.',
         'Chúng tôi đã đặt chỗ ở giá hợp lý gần nhà ga.'
  UNION ALL SELECT 'reservation', '/ˌrezərˈveɪʃən/', 'sự đặt chỗ',
         'an arrangement to have a seat, room, or service kept for someone',
         'Please confirm your hotel reservation before you travel.',
         'Vui lòng xác nhận đặt phòng khách sạn trước khi đi.'
  UNION ALL SELECT 'boarding pass', '/ˈbɔːrdɪŋ pæs/', 'thẻ lên máy bay',
         'a document that allows a passenger to board an aircraft',
         'Keep your boarding pass ready at the departure gate.',
         'Hãy chuẩn bị sẵn thẻ lên máy bay tại cửa khởi hành.'
  UNION ALL SELECT 'luggage', '/ˈlʌɡɪdʒ/', 'hành lý', 'bags and cases carried by a traveler',
         'Her luggage was placed in the overhead compartment.',
         'Hành lý của cô ấy được đặt vào ngăn chứa phía trên.'
  UNION ALL SELECT 'baggage allowance', '/ˈbæɡɪdʒ əˌlaʊəns/', 'lượng hành lý được phép mang',
         'the amount of luggage a passenger may carry without an extra charge',
         'Check the baggage allowance before packing your suitcase.',
         'Hãy kiểm tra lượng hành lý được phép mang trước khi đóng vali.'
  UNION ALL SELECT 'layover', '/ˈleɪˌoʊvər/', 'thời gian quá cảnh',
         'a period of waiting between connecting parts of a journey',
         'We have a two-hour layover in Singapore.',
         'Chúng tôi quá cảnh hai giờ ở Singapore.'
  UNION ALL SELECT 'transfer', '/ˈtrænsfər/', 'sự chuyển tuyến',
         'a change from one vehicle or route to another',
         'The ticket includes a transfer to the airport bus.',
         'Vé bao gồm một lượt chuyển sang xe buýt sân bay.'
  UNION ALL SELECT 'commute', '/kəˈmjuːt/', 'đi lại thường xuyên',
         'to travel regularly between home and work or school',
         'She commutes to work by train every weekday.',
         'Cô ấy đi tàu đến nơi làm việc mỗi ngày trong tuần.'
  UNION ALL SELECT 'subway', '/ˈsʌbweɪ/', 'tàu điện ngầm',
         'an underground urban railway system',
         'The subway is the fastest way to cross the city center.',
         'Tàu điện ngầm là cách nhanh nhất để đi qua trung tâm thành phố.'
  UNION ALL SELECT 'platform', '/ˈplætfɔːrm/', 'sân ga',
         'the raised area beside a railway track where passengers wait',
         'The express train will depart from platform four.',
         'Tàu tốc hành sẽ khởi hành từ sân ga số bốn.'
  UNION ALL SELECT 'fare', '/fer/', 'giá vé',
         'the money paid for a journey on public transport',
         'You can pay the bus fare with a contactless card.',
         'Bạn có thể trả tiền vé xe buýt bằng thẻ không tiếp xúc.'
) AS terms
WHERE topic.`title`='Travel and Transport'
  AND NOT EXISTS (
    SELECT 1 FROM `vocabulary_terms` existing
    WHERE existing.`topic_id`=topic.`id` AND existing.`word`=terms.`word`
  );

UPDATE `vocabulary_lessons` lesson
JOIN `vocabulary_topics` topic ON topic.`id`=lesson.`topic_id`
SET lesson.`target_word_count`=20
WHERE topic.`title`='Travel and Transport';

INSERT INTO `vocabulary_lesson_terms` (`lesson_id`, `term_id`, `sort_order`)
SELECT lesson.`id`, term.`id`,
       COALESCE((
         SELECT MAX(existing_link.`sort_order`)
         FROM `vocabulary_lesson_terms` existing_link
         WHERE existing_link.`lesson_id`=lesson.`id`
       ), 0) + ROW_NUMBER() OVER (PARTITION BY lesson.`id` ORDER BY term.`id`)
FROM `vocabulary_lessons` lesson
JOIN `vocabulary_topics` topic ON topic.`id`=lesson.`topic_id`
JOIN `vocabulary_terms` term ON term.`topic_id`=topic.`id`
WHERE topic.`title`='Travel and Transport'
  AND NOT EXISTS (
    SELECT 1 FROM `vocabulary_lesson_terms` existing_link
    WHERE existing_link.`lesson_id`=lesson.`id` AND existing_link.`term_id`=term.`id`
  );
