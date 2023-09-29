package com.example.learningproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView1 = findViewById(R.id.textView1);
        TextView textView2 = findViewById(R.id.textView2);
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {
            CharSequence tmp = textView1.getText();
            textView1.setText(textView2.getText());
            textView2.setText(tmp);
            Toast.makeText(this, "交换成功", Toast.LENGTH_LONG).show();
            new AlertDialog.Builder(this).setTitle("交换成功").setPositiveButton("确定",null).show();
        });

    }
}