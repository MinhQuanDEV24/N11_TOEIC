package com.toeic.nhom11.vocabulary.dto;

import com.toeic.nhom11.vocabulary.model.VocabularyWord;
import java.util.List;

public class VocabularyQuiz {
    private String token;
    private String type;
    private long topicId;
    private Long lessonId;
    private String title;
    private List<Question> questions;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public long getTopicId() { return topicId; }
    public void setTopicId(long topicId) { this.topicId = topicId; }
    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public static class Question {
        private int number;
        private String kind;
        private VocabularyWord term;
        private List<VocabularyWord> options;

        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
        public String getKind() { return kind; }
        public void setKind(String kind) { this.kind = kind; }
        public VocabularyWord getTerm() { return term; }
        public void setTerm(VocabularyWord term) { this.term = term; }
        public List<VocabularyWord> getOptions() { return options; }
        public void setOptions(List<VocabularyWord> options) { this.options = options; }
    }
}
