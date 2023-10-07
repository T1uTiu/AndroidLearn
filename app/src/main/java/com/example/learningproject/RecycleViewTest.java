package com.example.learningproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.learningproject.data.Book;

public class RecycleViewTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view_test);
        Book[] books = new Book[]{
                new Book(R.drawable.book_1,"信息安全数学基础（第2版）"),
                new Book(R.drawable.book_no_name, "创新工程实践"),
                new Book(R.drawable.book_2, "软件项目管理案例教程（第4版）"),

        };
        BookAdapter bookAdapter = new BookAdapter(books);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recycle_view_books);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }
}
class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{
    Book[] bookList;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView coverView;
        private final TextView titleView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.titleView = (TextView) itemView.findViewById(R.id.text_view_book_title);
            this.coverView = (ImageView) itemView.findViewById(R.id.image_view_book_cover);
        }
        public ImageView getCoverView(){
            return this.coverView;
        }

        public TextView getTitleView() {
            return titleView;
        }
    }
    public BookAdapter(Book[] bookList){
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
        holder.getTitleView().setText(bookList[position].getTitle());
        holder.getCoverView().setImageResource(bookList[position].getCoverResourceId());
    }

    @Override
    public int getItemCount() {
        return bookList.length;
    }
}