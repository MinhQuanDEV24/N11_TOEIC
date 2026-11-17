package com.toeic.nhom11.vocabulary.model;

public class VocabularyLesson {
    private Long id;
    private long topicId;
    private String title;
    private String description;
    private int sortOrder;
    private int targetWordCount;
    private boolean published;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getTopicId() { return topicId; }
    public void setTopicId(long topicId) { this.topicId = topicId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public int getTargetWordCount() { return targetWordCount; }
    public void setTargetWordCount(int targetWordCount) { this.targetWordCount = targetWordCount; }
    public boolean getPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}
