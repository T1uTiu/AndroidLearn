package com.example.learningproject.Model;

import com.example.learningproject.R;

import java.io.Serializable;

public class Book implements Serializable {
    int coverResourceId;
    String title;
    public Book(int coverResourceId, String title){
        this.coverResourceId = coverResourceId;
        this.title = title;
    }
    public Book(){
        this.coverResourceId = R.drawable.book_no_name;
        this.title = "Hello World";
    }

    public int getCoverResourceId() {
        return coverResourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
