package com.example.learningproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.learningproject.Model.Book;

public class BookDetailsActivity extends AppCompatActivity {
    int position;
    Book book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Button okBtn = findViewById(R.id.book_modify_ok);
        Button cancelBtn = findViewById(R.id.book_modify_cancel);
        EditText titleEdit = findViewById(R.id.book_title_edit);

        Intent intent = getIntent();
        Bundle param = intent.getBundleExtra("param");
        int type = param.getInt("type");
        position = -1;
        book = new Book();
        if(type == 1){
            position = param.getInt("position");
            book = (Book)param.getSerializable("book");
            titleEdit.setText(book.getTitle());
        }

        okBtn.setOnClickListener(view -> {
            Bundle res = new Bundle();
            res.putInt("position", position);

            book.setTitle(titleEdit.getText().toString());
            res.putSerializable("book", book);

            Intent resIntent = new Intent();
            resIntent.putExtra("res", res);
            setResult(RESULT_OK, resIntent);
            finish();
        });
        cancelBtn.setOnClickListener(view ->{
            Intent resIntent = new Intent();
            setResult(RESULT_CANCELED, resIntent);
            finish();
        });
    }
}