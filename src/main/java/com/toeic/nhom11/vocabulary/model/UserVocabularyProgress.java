package com.toeic.nhom11.vocabulary.model;

import java.time.LocalDateTime;

public class UserVocabularyProgress {
    private long userId;
    private long termId;
    private String learningStatus;
    private boolean favorite;
    private int reviewCount;
    private LocalDateTime nextReviewAt;
    private LocalDateTime updatedAt;

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getTermId() { return termId; }
    public void setTermId(long termId) { this.termId = termId; }
    public String getLearningStatus() { return learningStatus; }
    public void setLearningStatus(String learningStatus) { this.learningStatus = learningStatus; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public LocalDateTime getNextReviewAt() { return nextReviewAt; }
    public void setNextReviewAt(LocalDateTime nextReviewAt) { this.nextReviewAt = nextReviewAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
