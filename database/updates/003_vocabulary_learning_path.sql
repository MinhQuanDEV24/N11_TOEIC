CREATE TABLE IF NOT EXISTS `vocabulary_lessons` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `topic_id` BIGINT UNSIGNED NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `sort_order` INT UNSIGNED NOT NULL DEFAULT 0,
  `target_word_count` SMALLINT UNSIGNED NOT NULL DEFAULT 10,
  `is_published` BOOLEAN NOT NULL DEFAULT 0,
  `created_by` BIGINT UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_vocabulary_lessons_topic_order` (`topic_id`, `sort_order`),
  CONSTRAINT `chk_vocabulary_lessons_word_count` CHECK (`target_word_count` BETWEEN 10 AND 36),
  CONSTRAINT `fk_vocabulary_lessons_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `vocabulary_topics` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_vocabulary_lessons_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `vocabulary_lesson_terms` (
  `lesson_id` BIGINT UNSIGNED NOT NULL,
  `term_id` BIGINT UNSIGNED NOT NULL,
  `sort_order` SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`lesson_id`, `term_id`),
  UNIQUE KEY `uq_vocabulary_lesson_terms_order` (`lesson_id`, `sort_order`),
  CONSTRAINT `fk_vocabulary_lesson_terms_lesson_id` FOREIGN KEY (`lesson_id`) REFERENCES `vocabulary_lessons` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_vocabulary_lesson_terms_term_id` FOREIGN KEY (`term_id`) REFERENCES `vocabulary_terms` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user_vocabulary_lesson_progress` (
  `user_id` BIGINT UNSIGNED NOT NULL,
  `lesson_id` BIGINT UNSIGNED NOT NULL,
  `status` ENUM('IN_PROGRESS','COMPLETED') NOT NULL DEFAULT 'IN_PROGRESS',
  `best_score` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  `stars` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `lesson_id`),
  CONSTRAINT `chk_user_vocabulary_lesson_stars` CHECK (`stars` BETWEEN 0 AND 3),
  CONSTRAINT `fk_user_vocabulary_lesson_progress_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_user_vocabulary_lesson_progress_lesson_id` FOREIGN KEY (`lesson_id`) REFERENCES `vocabulary_lessons` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user_vocabulary_preferences` (
  `user_id` BIGINT UNSIGNED NOT NULL,
  `theme` ENUM('LIGHT','DARK') NOT NULL DEFAULT 'LIGHT',
  `font_size` ENUM('SMALL','MEDIUM','LARGE') NOT NULL DEFAULT 'MEDIUM',
  `language` ENUM('VI','EN') NOT NULL DEFAULT 'VI',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_vocabulary_preferences_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `vocabulary_quiz_attempts`
  ADD COLUMN `lesson_id` BIGINT UNSIGNED NULL DEFAULT NULL AFTER `topic_id`,
  ADD COLUMN `quiz_type` ENUM('TOPIC','LESSON','PLACEMENT') NOT NULL DEFAULT 'TOPIC' AFTER `lesson_id`,
  ADD COLUMN `passed` BOOLEAN NOT NULL DEFAULT 0 AFTER `correct_count`,
  ADD KEY `idx_vocabulary_quiz_attempts_lesson_id` (`lesson_id`),
  ADD KEY `idx_vocabulary_quiz_attempts_type` (`quiz_type`),
  ADD CONSTRAINT `fk_vocabulary_quiz_attempts_lesson_id` FOREIGN KEY (`lesson_id`) REFERENCES `vocabulary_lessons` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `vocabulary_quiz_answers`
  ADD COLUMN `typed_answer` VARCHAR(500) NULL DEFAULT NULL AFTER `selected_term_id`;

INSERT INTO `vocabulary_topics` (`title`, `description`, `level`, `sort_order`, `is_published`)
SELECT curriculum.title, curriculum.description, curriculum.level, curriculum.sort_order, 1
FROM (
  SELECT 'Education' AS title, 'Từ vựng về trường học, học tập và giáo dục suốt đời.' AS description, 'BASIC' AS level, 4 AS sort_order
  UNION ALL SELECT 'Environment', 'Từ vựng về môi trường, bảo tồn và biến đổi khí hậu.', 'BASIC', 5
  UNION ALL SELECT 'Health and Well-being', 'Từ vựng về sức khỏe, lối sống và chăm sóc y tế.', 'BASIC', 6
  UNION ALL SELECT 'Technology', 'Từ vựng về công nghệ, internet và đổi mới.', 'BASIC', 7
  UNION ALL SELECT 'Society', 'Từ vựng về cộng đồng, dân số và các vấn đề xã hội.', 'INTERMEDIATE', 8
  UNION ALL SELECT 'Culture and Traditions', 'Từ vựng về văn hóa, bản sắc và truyền thống.', 'INTERMEDIATE', 9
  UNION ALL SELECT 'Media and Communication', 'Từ vựng về truyền thông, báo chí và giao tiếp.', 'INTERMEDIATE', 10
  UNION ALL SELECT 'Food and Agriculture', 'Từ vựng về thực phẩm, dinh dưỡng và nông nghiệp.', 'BASIC', 11
  UNION ALL SELECT 'Government and Public Services', 'Từ vựng về chính phủ, chính sách và dịch vụ công.', 'ADVANCED', 12
  UNION ALL SELECT 'Economy and Finance', 'Từ vựng về kinh tế, tài chính và thương mại.', 'INTERMEDIATE', 13
  UNION ALL SELECT 'Science and Research', 'Từ vựng về khoa học, nghiên cứu và phát minh.', 'ADVANCED', 14
  UNION ALL SELECT 'Crime and Law', 'Từ vựng về luật pháp, an ninh và hệ thống tư pháp.', 'ADVANCED', 15
  UNION ALL SELECT 'Globalization', 'Từ vựng về hội nhập, thương mại và hợp tác quốc tế.', 'ADVANCED', 16
  UNION ALL SELECT 'Urbanization and Housing', 'Từ vựng về đô thị hóa, nhà ở và quy hoạch thành phố.', 'INTERMEDIATE', 17
  UNION ALL SELECT 'Family and Relationships', 'Từ vựng về gia đình, các thế hệ và quan hệ xã hội.', 'BASIC', 18
  UNION ALL SELECT 'Sports and Recreation', 'Từ vựng về thể thao, giải trí và hoạt động ngoài trời.', 'BASIC', 19
  UNION ALL SELECT 'Tourism', 'Từ vựng về du lịch, di sản và dịch vụ lưu trú.', 'INTERMEDIATE', 20
  UNION ALL SELECT 'Energy and Natural Resources', 'Từ vựng về năng lượng, tài nguyên và phát triển bền vững.', 'ADVANCED', 21
  UNION ALL SELECT 'Animals and Wildlife', 'Từ vựng về động vật, đa dạng sinh học và bảo tồn.', 'INTERMEDIATE', 22
  UNION ALL SELECT 'Art and Architecture', 'Từ vựng về nghệ thuật, thiết kế và kiến trúc.', 'INTERMEDIATE', 23
  UNION ALL SELECT 'Language and Linguistics', 'Từ vựng về ngôn ngữ, giao tiếp và học ngoại ngữ.', 'ADVANCED', 24
  UNION ALL SELECT 'Climate Change', 'Từ vựng về khí hậu, phát thải và giải pháp ứng phó.', 'ADVANCED', 25
) AS curriculum
WHERE NOT EXISTS (
  SELECT 1 FROM `vocabulary_topics` existing WHERE existing.title=curriculum.title
);

INSERT INTO `vocabulary_lessons` (`topic_id`, `title`, `description`, `sort_order`, `target_word_count`, `is_published`)
SELECT t.id, CONCAT(t.title, ' — Nền tảng'), 'Học nhóm từ vựng cốt lõi và ví dụ theo chủ đề.', 1, 10, 1
FROM `vocabulary_topics` t
WHERE t.is_published=1 AND NOT EXISTS (
  SELECT 1 FROM `vocabulary_lessons` l WHERE l.topic_id=t.id AND l.sort_order=1
);
INSERT INTO `vocabulary_lessons` (`topic_id`, `title`, `description`, `sort_order`, `target_word_count`, `is_published`)
SELECT t.id, CONCAT(t.title, ' — Mở rộng'), 'Mở rộng vốn từ và luyện tập theo ngữ cảnh.', 2, 20, 1
FROM `vocabulary_topics` t
WHERE t.is_published=1 AND NOT EXISTS (
  SELECT 1 FROM `vocabulary_lessons` l WHERE l.topic_id=t.id AND l.sort_order=2
);
INSERT INTO `vocabulary_lessons` (`topic_id`, `title`, `description`, `sort_order`, `target_word_count`, `is_published`)
SELECT t.id, CONCAT(t.title, ' — Nâng cao'), 'Ôn tập chuyên sâu với bộ từ vựng nâng cao.', 3, 36, 1
FROM `vocabulary_topics` t
WHERE t.is_published=1 AND NOT EXISTS (
  SELECT 1 FROM `vocabulary_lessons` l WHERE l.topic_id=t.id AND l.sort_order=3
);

INSERT INTO `vocabulary_lesson_terms` (`lesson_id`, `term_id`, `sort_order`)
SELECT l.id, t.id, ROW_NUMBER() OVER (PARTITION BY l.id ORDER BY t.id)
FROM `vocabulary_lessons` l
JOIN `vocabulary_terms` t ON t.topic_id=l.topic_id
WHERE NOT EXISTS (
  SELECT 1 FROM `vocabulary_lesson_terms` lt
  WHERE lt.lesson_id=l.id AND lt.term_id=t.id
);
