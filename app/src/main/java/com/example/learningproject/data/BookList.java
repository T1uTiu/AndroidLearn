package com.example.learningproject.data;

import android.content.Context;

import com.example.learningproject.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookList{
    List<Book> list;

    public List<Book> getList() {
        return list;
    }

    final String fileName = "book_list_data";

    public BookList(){
        list = new ArrayList<>(Arrays.asList(
            new Book(R.drawable.book_1, "信息安全数学基础（第2版）"),
            new Book(R.drawable.book_no_name, "创新工程实践"),
            new Book(R.drawable.book_2, "软件项目管理案例教程（第4版）")
        ));
    }
    public void loadFileData(Context context){
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (ArrayList<Book>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void saveFileData(Context context){
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
