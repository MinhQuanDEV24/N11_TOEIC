-- Reading subset of team schema for isolated H2 tests.
CREATE TABLE IF NOT EXISTS `media_assets` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `media_type` ENUM('AUDIO','IMAGE','OTHER') NOT NULL,
  `file_url` VARCHAR(700) NOT NULL,
  `mime_type` VARCHAR(100) NOT NULL,
  `duration_seconds` INT NULL DEFAULT NULL,
  `created_by` BIGINT NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_media_assets_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
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
  CONSTRAINT `fk_vocabulary_terms_topic_id` FOREIGN KEY (`topic_id`) REFERENCES `vocabulary_topics` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_vocabulary_terms_audio_media_id` FOREIGN KEY (`audio_media_id`) REFERENCES `media_assets` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
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

CREATE TABLE IF NOT EXISTS `practice_sets` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `skill` ENUM('LISTENING','READING') NOT NULL,
  `part_no` TINYINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `instructions` TEXT NULL DEFAULT NULL,
  `difficulty` ENUM('EASY','MEDIUM','HARD') NOT NULL DEFAULT 'MEDIUM',
  `is_published` BOOLEAN NOT NULL DEFAULT 0,
  `created_by` BIGINT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_practice_sets_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `chk_practice_skill_part` CHECK ((`skill`='LISTENING' AND `part_no` BETWEEN 1 AND 4) OR (`skill`='READING' AND `part_no` BETWEEN 5 AND 7))
);

CREATE TABLE IF NOT EXISTS `practice_passages` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `practice_set_id` BIGINT NOT NULL,
  `passage_text` LONGTEXT NULL DEFAULT NULL,
  `audio_media_id` BIGINT NULL DEFAULT NULL,
  `image_media_id` BIGINT NULL DEFAULT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_practice_passages_practice_set_id` FOREIGN KEY (`practice_set_id`) REFERENCES `practice_sets` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_practice_passages_audio_media_id` FOREIGN KEY (`audio_media_id`) REFERENCES `media_assets` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_practice_passages_image_media_id` FOREIGN KEY (`image_media_id`) REFERENCES `media_assets` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `practice_questions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `practice_set_id` BIGINT NOT NULL,
  `passage_id` BIGINT NULL DEFAULT NULL,
  `question_text` TEXT NOT NULL,
  `explanation` TEXT NULL DEFAULT NULL,
  `audio_media_id` BIGINT NULL DEFAULT NULL,
  `image_media_id` BIGINT NULL DEFAULT NULL,
  `sort_order` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_practice_questions_practice_set_id` FOREIGN KEY (`practice_set_id`) REFERENCES `practice_sets` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_practice_questions_passage_id` FOREIGN KEY (`passage_id`) REFERENCES `practice_passages` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_practice_questions_audio_media_id` FOREIGN KEY (`audio_media_id`) REFERENCES `media_assets` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_practice_questions_image_media_id` FOREIGN KEY (`image_media_id`) REFERENCES `media_assets` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `practice_options` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `question_id` BIGINT NOT NULL,
  `option_label` CHAR(1) NOT NULL,
  `option_text` TEXT NOT NULL,
  `is_correct` BOOLEAN NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE (`question_id`, `option_label`),
  CONSTRAINT `fk_practice_options_question_id` FOREIGN KEY (`question_id`) REFERENCES `practice_questions` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `practice_attempts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `practice_set_id` BIGINT NOT NULL,
  `correct_count` SMALLINT NOT NULL DEFAULT 0,
  `total_questions` SMALLINT NOT NULL DEFAULT 0,
  `score_percent` DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  `started_at` DATETIME NOT NULL,
  `submitted_at` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_practice_attempts_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_practice_attempts_practice_set_id` FOREIGN KEY (`practice_set_id`) REFERENCES `practice_sets` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_practice_attempts_score` CHECK (`score_percent` BETWEEN 0 AND 100)
);

CREATE TABLE IF NOT EXISTS `practice_attempt_answers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `attempt_id` BIGINT NOT NULL,
  `question_id` BIGINT NOT NULL,
  `selected_option_id` BIGINT NULL DEFAULT NULL,
  `is_correct` BOOLEAN NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE (`attempt_id`, `question_id`),
  CONSTRAINT `fk_practice_attempt_answers_attempt_id` FOREIGN KEY (`attempt_id`) REFERENCES `practice_attempts` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_practice_attempt_answers_question_id` FOREIGN KEY (`question_id`) REFERENCES `practice_questions` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_practice_attempt_answers_selected_option_id` FOREIGN KEY (`selected_option_id`) REFERENCES `practice_options` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
);
