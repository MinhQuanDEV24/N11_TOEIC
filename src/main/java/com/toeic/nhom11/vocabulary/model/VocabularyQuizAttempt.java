package com.toeic.nhom11.vocabulary.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VocabularyQuizAttempt {
    private Long id;
    private long userId;
    private long topicId;
    private Long lessonId;
    private String topicTitle;
    private String lessonTitle;
    private String quizType = "TOPIC";
    private int questionCount;
    private int correctCount;
    private boolean passed;
    private LocalDateTime submittedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getTopicId() { return topicId; }
    public void setTopicId(long topicId) { this.topicId = topicId; }
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public String getTopicTitle() { return topicTitle; }
    public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
    public String getLessonTitle() { return lessonTitle; }
    public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }
    public String getQuizType() { return quizType; }
    public void setQuizType(String quizType) { this.quizType = quizType; }
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public BigDecimal getScorePercent() {
        if (questionCount == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(correctCount * 100.0 / questionCount).setScale(1, java.math.RoundingMode.HALF_UP);
    }
}
