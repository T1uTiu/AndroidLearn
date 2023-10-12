package com.example.learningproject.data;

import java.io.Serializable;

public class Book implements Serializable {
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
