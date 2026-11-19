package com.toeic.nhom11.vocabulary.dto;

import com.toeic.nhom11.vocabulary.model.VocabularyLesson;
import java.math.BigDecimal;

public class VocabularyLessonSummary {
    private VocabularyLesson lesson;
    private int wordCount;
    private String state;
    private BigDecimal bestScore = BigDecimal.ZERO;
    private int stars;

    public VocabularyLesson getLesson() { return lesson; }
    public void setLesson(VocabularyLesson lesson) { this.lesson = lesson; }
    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public BigDecimal getBestScore() { return bestScore; }
    public void setBestScore(BigDecimal bestScore) { this.bestScore = bestScore; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
}
