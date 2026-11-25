package com.fepocket.model;

import java.io.Serializable;

public class Lesson implements Serializable {
    private int id;
    private int categoryId;
    private String title;
    private String description;
    private String explanation;
    private String codeExample;
    private String quizQuestion;
    private String personalNotes;
    private String documentationUrl;
    private String difficulty;
    private boolean isFavorite;

    public Lesson() {}

    public Lesson(int categoryId, String title, String description, String explanation,
                  String codeExample, String quizQuestion, String personalNotes,
                  String documentationUrl, String difficulty, boolean isFavorite) {
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.explanation = explanation;
        this.codeExample = codeExample;
        this.quizQuestion = quizQuestion;
        this.personalNotes = personalNotes;
        this.documentationUrl = documentationUrl;
        this.difficulty = difficulty;
        this.isFavorite = isFavorite;
    }

    public Lesson(int id, int categoryId, String title, String description, String explanation,
                  String codeExample, String quizQuestion, String personalNotes,
                  String documentationUrl, String difficulty, boolean isFavorite) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.explanation = explanation;
        this.codeExample = codeExample;
        this.quizQuestion = quizQuestion;
        this.personalNotes = personalNotes;
        this.documentationUrl = documentationUrl;
        this.difficulty = difficulty;
        this.isFavorite = isFavorite;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getCodeExample() { return codeExample; }
    public void setCodeExample(String codeExample) { this.codeExample = codeExample; }

    public String getQuizQuestion() { return quizQuestion; }
    public void setQuizQuestion(String quizQuestion) { this.quizQuestion = quizQuestion; }

    public String getPersonalNotes() { return personalNotes; }
    public void setPersonalNotes(String personalNotes) { this.personalNotes = personalNotes; }

    public String getDocumentationUrl() { return documentationUrl; }
    public void setDocumentationUrl(String documentationUrl) { this.documentationUrl = documentationUrl; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
