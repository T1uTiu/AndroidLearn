package com.example.learningproject;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
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
import com.example.learningproject.data.BookList;


public class RecycleViewTest extends AppCompatActivity {

    BookList bookList;
    ActivityResultLauncher<Bundle> insertBookLauncher;
    ActivityResultLauncher<Bundle> updateBookLauncher;

    public ActivityResultLauncher<Bundle> getInsertBookLauncher() {
        return insertBookLauncher;
    }
    public ActivityResultLauncher<Bundle> getUpdateBookLauncher() {
        return updateBookLauncher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view_test);
        bookList = new BookList();
        bookList.loadFileData(this);

        //region RecyclerView
        BookAdapter bookAdapter = new BookAdapter(bookList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recycle_view_books);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        //endregion

        insertBookLauncher = registerForActivityResult(new BookDetailActivityResultContract(), result -> {
            if(result != null){
                bookList.getList().add((Book) result.getSerializable("book"));
                bookAdapter.notifyItemInserted(bookList.getList().size()-1);
                bookList.saveFileData(this);
            }

        });
        updateBookLauncher = registerForActivityResult(new BookDetailActivityResultContract(), result ->{
            if(result != null){
                int position = result.getInt("position");
                Book b = (Book) result.getSerializable("book");
                bookList.getList().get(position).setTitle(b.getTitle());
                bookAdapter.notifyItemChanged(position);
                bookList.saveFileData(this);
            }
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
                    onUpdateClick();
                    break;
                case 3:
                    onDeleteClick();
                    break;
            }
            return true;
        }
        void onInsertClick(){
            Bundle param = new Bundle();
            param.putInt("type", 0);
            activity.getInsertBookLauncher().launch(param);
        }
        void onDeleteClick(){
            int idx = getAdapterPosition();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("您确定要删除记录吗")
                    .setPositiveButton(R.string.ok_btn, (dialogInterface, i) -> {
                        bookList.getList().remove(idx);
                        notifyItemRemoved(idx);
                        bookList.saveFileData(activity);
                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
            builder.show();

        }
        void onUpdateClick(){
            int position = getAdapterPosition();
            Bundle param = new Bundle();
            param.putInt("type", 1);
            param.putInt("position", position);
            param.putSerializable("book", bookList.getList().get(position));
            activity.getUpdateBookLauncher().launch(param);
        }
    }
    BookList bookList;
    RecycleViewTest activity;
    public BookAdapter(BookList bookList, RecycleViewTest activity){
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
        holder.getTitleView().setText(bookList.getList().get(position).getTitle());
        holder.getCoverView().setImageResource(bookList.getList().get(position).getCoverResourceId());
    }

    @Override
    public int getItemCount() {
        return bookList.getList().size();
    }
}

class BookDetailActivityResultContract extends ActivityResultContract<Bundle, Bundle>{
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Bundle param) {
        Intent intent = new Intent(context, BookDetailsActivity.class);
        intent.putExtra("param", param);
        return intent;
    }

    @Override
    public Bundle parseResult(int i, @Nullable Intent intent) {
        if(i == RESULT_OK && intent != null ){
            return intent.getBundleExtra("res");
        }
        return null;
    }
}
