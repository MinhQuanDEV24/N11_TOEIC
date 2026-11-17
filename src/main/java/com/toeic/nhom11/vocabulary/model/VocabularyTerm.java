package com.toeic.nhom11.vocabulary.model;

public class VocabularyTerm {
    private Long id;
    private long topicId;
    private String word;
    private String ipa;
    private String meaningVi;
    private String definitionEn;
    private String exampleEn;
    private String exampleVi;
    private Long audioMediaId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getTopicId() { return topicId; }
    public void setTopicId(long topicId) { this.topicId = topicId; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getIpa() { return ipa; }
    public void setIpa(String ipa) { this.ipa = ipa; }
    public String getMeaningVi() { return meaningVi; }
    public void setMeaningVi(String meaningVi) { this.meaningVi = meaningVi; }
    public String getDefinitionEn() { return definitionEn; }
    public void setDefinitionEn(String definitionEn) { this.definitionEn = definitionEn; }
    public String getExampleEn() { return exampleEn; }
    public void setExampleEn(String exampleEn) { this.exampleEn = exampleEn; }
    public String getExampleVi() { return exampleVi; }
    public void setExampleVi(String exampleVi) { this.exampleVi = exampleVi; }
    public Long getAudioMediaId() { return audioMediaId; }
    public void setAudioMediaId(Long audioMediaId) { this.audioMediaId = audioMediaId; }
}
