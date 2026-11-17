ALTER TABLE `vocabulary_quiz_answers`
  ADD COLUMN `question_number` SMALLINT UNSIGNED NULL AFTER `attempt_id`;

CREATE TEMPORARY TABLE `vocabulary_quiz_answer_order` AS
SELECT `id`, ROW_NUMBER() OVER (PARTITION BY `attempt_id` ORDER BY `id`) AS `question_number`
FROM `vocabulary_quiz_answers`;

UPDATE `vocabulary_quiz_answers` answer
JOIN `vocabulary_quiz_answer_order` ordered_answers
  ON ordered_answers.`id`=answer.`id`
SET answer.`question_number`=ordered_answers.`question_number`;

ALTER TABLE `vocabulary_quiz_answers`
  MODIFY COLUMN `question_number` SMALLINT UNSIGNED NOT NULL,
  DROP INDEX `uq_vocabulary_quiz_answers_attempt_id_term_id`,
  ADD UNIQUE KEY `uq_vocabulary_quiz_answers_attempt_id_question_number` (`attempt_id`, `question_number`);

DROP TEMPORARY TABLE `vocabulary_quiz_answer_order`;
