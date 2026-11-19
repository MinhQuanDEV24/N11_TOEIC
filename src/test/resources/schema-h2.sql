CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(191) NOT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `role` ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
  `status` ENUM('ACTIVE','LOCKED') NOT NULL DEFAULT 'ACTIVE',
  `last_login_at` DATETIME NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE (`email`)
);

CREATE TABLE IF NOT EXISTS `grammar_topics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(180) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `is_published` BOOLEAN NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE (`title`)
);

CREATE TABLE IF NOT EXISTS `grammar_lessons` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `topic_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content_html` LONGTEXT NOT NULL,
  `level` ENUM('BASIC','INTERMEDIATE','ADVANCED') NOT NULL DEFAULT 'BASIC',
  `sort_order` INT NOT NULL DEFAULT 0,
  `is_published` BOOLEAN NOT NULL DEFAULT 0,
  `created_by` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_grammar_lessons_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `grammar_topics` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_grammar_lessons_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `grammar_questions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `lesson_id` BIGINT NOT NULL,
  `question_text` TEXT NOT NULL,
  `explanation` TEXT NULL DEFAULT NULL,
  `difficulty` TINYINT NOT NULL DEFAULT 1,
  `sort_order` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_grammar_questions_lesson_id` FOREIGN KEY (`lesson_id`) REFERENCES `grammar_lessons` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_grammar_difficulty` CHECK (`difficulty` BETWEEN 1 AND 3)
);

CREATE TABLE IF NOT EXISTS `grammar_options` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `question_id` BIGINT NOT NULL,
  `option_label` CHAR(1) NOT NULL,
  `option_text` TEXT NOT NULL,
  `is_correct` BOOLEAN NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE (`question_id`, `option_label`),
  CONSTRAINT `fk_grammar_options_question_id` FOREIGN KEY (`question_id`) REFERENCES `grammar_questions` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `grammar_attempts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `lesson_id` BIGINT NOT NULL,
  `correct_count` SMALLINT NOT NULL DEFAULT 0,
  `total_questions` SMALLINT NOT NULL DEFAULT 0,
  `score_percent` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  `submitted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_grammar_attempts_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_grammar_attempts_lesson_id` FOREIGN KEY (`lesson_id`) REFERENCES `grammar_lessons` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_grammar_attempts_score` CHECK (`score_percent` BETWEEN 0 AND 100)
);

CREATE TABLE IF NOT EXISTS `grammar_attempt_answers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `attempt_id` BIGINT NOT NULL,
  `question_id` BIGINT NOT NULL,
  `selected_option_id` BIGINT NULL DEFAULT NULL,
  `is_correct` BOOLEAN NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE (`attempt_id`, `question_id`),
  CONSTRAINT `fk_grammar_attempt_answers_attempt_id` FOREIGN KEY (`attempt_id`) REFERENCES `grammar_attempts` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_grammar_attempt_answers_question_id` FOREIGN KEY (`question_id`) REFERENCES `grammar_questions` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_grammar_attempt_answers_selected_option_id` FOREIGN KEY (`selected_option_id`) REFERENCES `grammar_options` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `vocabulary_topics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(180) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `level` ENUM('BASIC','INTERMEDIATE','ADVANCED') NOT NULL DEFAULT 'BASIC',
  `sort_order` INT NOT NULL DEFAULT 0,
  `is_published` BOOLEAN NOT NULL DEFAULT 0,
  `created_by` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_vocabulary_topics_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `vocabulary_lessons` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `topic_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT NULL DEFAULT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  `target_word_count` SMALLINT NOT NULL DEFAULT 10,
  `is_published` BOOLEAN NOT NULL DEFAULT 0,
  `created_by` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`topic_id`, `sort_order`),
  CONSTRAINT `chk_vocabulary_lessons_word_count` CHECK (`target_word_count` BETWEEN 10 AND 36),
  CONSTRAINT `fk_vocabulary_lessons_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `vocabulary_topics` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_vocabulary_lessons_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `media_assets` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `file_url` VARCHAR(700) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `vocabulary_terms` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `topic_id` BIGINT NOT NULL,
  `word` VARCHAR(160) NOT NULL,
  `ipa` VARCHAR(160) NULL DEFAULT NULL,
  `meaning_vi` VARCHAR(500) NOT NULL,
  `definition_en` TEXT NULL DEFAULT NULL,
  `example_en` TEXT NULL DEFAULT NULL,
  `example_vi` TEXT NULL DEFAULT NULL,
  `audio_media_id` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`topic_id`, `word`),
  CONSTRAINT `fk_vocabulary_terms_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `vocabulary_topics` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `vocabulary_lesson_terms` (
  `lesson_id` BIGINT NOT NULL,
  `term_id` BIGINT NOT NULL,
  `sort_order` SMALLINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`lesson_id`, `term_id`),
  UNIQUE (`lesson_id`, `sort_order`),
  CONSTRAINT `fk_vocabulary_lesson_terms_lesson_id` FOREIGN KEY (`lesson_id`) REFERENCES `vocabulary_lessons` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_vocabulary_lesson_terms_term_id` FOREIGN KEY (`term_id`) REFERENCES `vocabulary_terms` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `user_vocabulary_progress` (
  `user_id` BIGINT NOT NULL,
  `term_id` BIGINT NOT NULL,
  `learning_status` ENUM('NEW','LEARNING','MASTERED') NOT NULL DEFAULT 'NEW',
  `is_favorite` BOOLEAN NOT NULL DEFAULT 0,
  `review_count` INT NOT NULL DEFAULT 0,
  `next_review_at` DATETIME NULL DEFAULT NULL,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `term_id`),
  CONSTRAINT `fk_user_vocabulary_progress_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_user_vocabulary_progress_term_id` FOREIGN KEY (`term_id`) REFERENCES `vocabulary_terms` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `user_vocabulary_lesson_progress` (
  `user_id` BIGINT NOT NULL,
  `lesson_id` BIGINT NOT NULL,
  `status` ENUM('IN_PROGRESS','COMPLETED') NOT NULL DEFAULT 'IN_PROGRESS',
  `best_score` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  `stars` TINYINT NOT NULL DEFAULT 0,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`, `lesson_id`),
  CONSTRAINT `chk_user_vocabulary_lesson_stars` CHECK (`stars` BETWEEN 0 AND 3),
  CONSTRAINT `fk_user_vocabulary_lesson_progress_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_user_vocabulary_lesson_progress_lesson_id` FOREIGN KEY (`lesson_id`) REFERENCES `vocabulary_lessons` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `user_vocabulary_preferences` (
  `user_id` BIGINT NOT NULL,
  `theme` ENUM('LIGHT','DARK') NOT NULL DEFAULT 'LIGHT',
  `font_size` ENUM('SMALL','MEDIUM','LARGE') NOT NULL DEFAULT 'MEDIUM',
  `language` ENUM('VI','EN') NOT NULL DEFAULT 'VI',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_vocabulary_preferences_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `vocabulary_quiz_attempts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `topic_id` BIGINT NOT NULL,
  `lesson_id` BIGINT NULL DEFAULT NULL,
  `quiz_type` ENUM('TOPIC','LESSON','PLACEMENT') NOT NULL DEFAULT 'TOPIC',
  `question_count` SMALLINT NOT NULL DEFAULT 0,
  `correct_count` SMALLINT NOT NULL DEFAULT 0,
  `passed` BOOLEAN NOT NULL DEFAULT 0,
  `submitted_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_vocabulary_quiz_attempts_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_vocabulary_quiz_attempts_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `vocabulary_topics` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_vocabulary_quiz_attempts_lesson_id` FOREIGN KEY (`lesson_id`) REFERENCES `vocabulary_lessons` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `vocabulary_quiz_answers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `attempt_id` BIGINT NOT NULL,
  `question_number` SMALLINT NOT NULL,
  `term_id` BIGINT NOT NULL,
  `selected_term_id` BIGINT NULL DEFAULT NULL,
  `typed_answer` VARCHAR(500) NULL DEFAULT NULL,
  `is_correct` BOOLEAN NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE (`attempt_id`, `question_number`),
  CONSTRAINT `fk_vocabulary_quiz_answers_attempt_id` FOREIGN KEY (`attempt_id`) REFERENCES `vocabulary_quiz_attempts` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_vocabulary_quiz_answers_term_id` FOREIGN KEY (`term_id`) REFERENCES `vocabulary_terms` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_vocabulary_quiz_answers_selected_term_id` FOREIGN KEY (`selected_term_id`) REFERENCES `vocabulary_terms` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS `user_profiles` (
  `user_id` BIGINT NOT NULL,
  `full_name` VARCHAR(120) NOT NULL,
  `phone` VARCHAR(20) NULL DEFAULT NULL,
  `avatar_url` VARCHAR(500) NULL DEFAULT NULL,
  `target_toeic_score` SMALLINT NULL DEFAULT NULL,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_profiles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `password_reset_tokens` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `token_hash` VARCHAR(255) NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `used_at` DATETIME NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE (`token_hash`),
  CONSTRAINT `fk_password_reset_tokens_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);
