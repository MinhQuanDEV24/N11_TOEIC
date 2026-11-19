package com.toeic.nhom11.vocabulary.dao;

import com.toeic.nhom11.vocabulary.dto.VocabularyLessonSummary;
import com.toeic.nhom11.vocabulary.dto.VocabularyTopicSummary;
import com.toeic.nhom11.vocabulary.model.*;
import java.util.*;

public interface VocabularyDao {
    List<VocabularyTopic> topics(boolean publishedOnly);
    Optional<VocabularyTopic> topic(long id);
    List<VocabularyTopicSummary> topicSummaries(long userId);
    List<VocabularyLessonSummary> lessonSummaries(long topicId, long userId);
    Optional<VocabularyLesson> lesson(long topicId, long lessonId);
    List<VocabularyWord> lessonWords(long lessonId);
    List<VocabularyWord> words(long topicId);
    List<VocabularyWord> allWords();
    Optional<VocabularyWord> word(long id);
    Optional<VocabularyWord> randomWord();
    Optional<Long> userId(String email);
    long insertAttempt(VocabularyQuizAttempt attempt);
    void insertAnswer(VocabularyQuizAttemptAnswer answer);
    Optional<VocabularyQuizAttempt> attempt(long id, long userId);
    List<VocabularyQuizAttemptAnswer> answers(long attemptId);
    List<VocabularyQuizAttempt> history(long userId);
    Map<Long, java.math.BigDecimal> bestScores(long userId);
    boolean placementPassed(long topicId, long userId);
    void updateLessonProgress(long lessonId, long userId, java.math.BigDecimal score, int stars);
    VocabularyUserPreferences preferences(long userId);
    void updatePreferences(long userId, VocabularyUserPreferences preferences);
    void updateProgress(long userId, long termId, String learningStatus);
    void markFavorite(long userId, long termId, boolean favorite);
}
