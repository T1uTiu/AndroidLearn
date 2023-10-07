package com.example.learningproject.data;

public class Book {
    int coverResourceId;
    String title;
    public Book(int coverResourceId, String title){
        this.coverResourceId = coverResourceId;
        this.title = title;
    }

    public int getCoverResourceId() {
        return coverResourceId;
    }

    public String getTitle() {
        return title;
    }
}
