package com.fepocket.model;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private String icon;
    private int lessonCount;

    public Category() {}

    public Category(String name, String icon, int lessonCount) {
        this.name = name;
        this.icon = icon;
        this.lessonCount = lessonCount;
    }

    public Category(int id, String name, String icon, int lessonCount) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.lessonCount = lessonCount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public int getLessonCount() { return lessonCount; }
    public void setLessonCount(int lessonCount) { this.lessonCount = lessonCount; }
}
