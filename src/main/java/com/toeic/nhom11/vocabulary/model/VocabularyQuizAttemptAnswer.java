package com.toeic.nhom11.vocabulary.model;

public class VocabularyQuizAttemptAnswer {
    private Long id;
    private long attemptId;
    private int questionNumber;
    private long termId;
    private Long selectedTermId;
    private String typedAnswer;
    private boolean correct;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getAttemptId() { return attemptId; }
    public void setAttemptId(long attemptId) { this.attemptId = attemptId; }
    public int getQuestionNumber() { return questionNumber; }
    public void setQuestionNumber(int questionNumber) { this.questionNumber = questionNumber; }
    public long getTermId() { return termId; }
    public void setTermId(long termId) { this.termId = termId; }
    public Long getSelectedTermId() { return selectedTermId; }
    public void setSelectedTermId(Long selectedTermId) { this.selectedTermId = selectedTermId; }
    public String getTypedAnswer() { return typedAnswer; }
    public void setTypedAnswer(String typedAnswer) { this.typedAnswer = typedAnswer; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}
