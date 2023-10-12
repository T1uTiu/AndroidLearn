package com.example.learningproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningproject.data.Book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecycleViewTest extends AppCompatActivity {
    List<Book> bookList;
    public List<Book> getBookList() {
        return bookList;
    }
    ActivityResultLauncher<String> launcher;

    public ActivityResultLauncher<String> getLauncher() {
        return launcher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view_test);
        bookList = new ArrayList<>(Arrays.asList(
                new Book(R.drawable.book_1, "信息安全数学基础（第2版）"),
                new Book(R.drawable.book_no_name, "创新工程实践"),
                new Book(R.drawable.book_2, "软件项目管理案例教程（第4版）")
        ));

        BookAdapter bookAdapter = new BookAdapter(getBookList(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recycle_view_books);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        launcher = registerForActivityResult(new InsertActivityResultContract(), result -> {
            assert result != null;
            getBookList().add(result);
            bookAdapter.notifyItemInserted(getBookList().size()-1);
        });
    }
}
class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>{
    // 将数据与列表视图绑定
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
        // 每个列表Item的容器
        private final ImageView coverView;
        private final TextView titleView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.titleView = itemView.findViewById(R.id.text_view_book_title);
            this.coverView = itemView.findViewById(R.id.image_view_book_cover);
            itemView.setOnCreateContextMenuListener(this);
        }

        public ImageView getCoverView(){
            return this.coverView;
        }

        public TextView getTitleView() {
            return titleView;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            // contextMenu.add(groupID, itemID, order, title)
            MenuItem insert = contextMenu.add(Menu.NONE, 1, 1, "添加");
            MenuItem update = contextMenu.add(Menu.NONE, 2, 2, "修改");
            MenuItem delete = contextMenu.add(Menu.NONE, 3, 3, "删除");
            insert.setOnMenuItemClickListener(this);
            update.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case 1:
                    onInsertClick();
                    break;
                case 2:
                    System.out.println("Click update");
                    break;
                case 3:
                    onDeleteClick();
                    break;
            }
            return true;
        }
        void onInsertClick(){
            activity.getLauncher().launch("insert");
        }
        void onDeleteClick(){
            int idx = getAdapterPosition();
            bookList.remove(idx);
            notifyItemRemoved(idx);
        }
    }
    List<Book> bookList;
    RecycleViewTest activity;

    public BookAdapter(List<Book> bookList, RecycleViewTest activity){
        this.bookList = bookList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {
        holder.getTitleView().setText(bookList.get(position).getTitle());
        holder.getCoverView().setImageResource(bookList.get(position).getCoverResourceId());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}

class InsertActivityResultContract extends ActivityResultContract<String, Book>{

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String s) {
        Intent intent = new Intent(context, ModifyItemActivity.class);
        intent.putExtra("type", s);
        return intent;
    }

    @Override
    public Book parseResult(int i, @Nullable Intent intent) {
        assert intent != null;
        return (Book)intent.getSerializableExtra("book");
    }
}