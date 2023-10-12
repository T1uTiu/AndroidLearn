package com.example.learningproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.learningproject.data.Book;

public class ModifyItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_item);
        Button okBtn = findViewById(R.id.book_modify_ok);
        EditText titleEdit = findViewById(R.id.book_title_edit);
        okBtn.setOnClickListener(view -> {
            String title = titleEdit.getText().toString();
            Book book = new Book(R.drawable.book_1, title);
            Intent resIntent = new Intent();
            resIntent.putExtra("book", book);
            setResult(RESULT_OK, resIntent);
            finish();
        });
    }
}