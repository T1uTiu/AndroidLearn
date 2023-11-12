package com.example.learningproject.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningproject.BookDetailsActivity;
import com.example.learningproject.R;
import com.example.learningproject.Model.Book;
import com.example.learningproject.Model.BookList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListFragment extends Fragment {
    BookList bookList;
    ActivityResultLauncher<Bundle> insertBookLauncher;
    ActivityResultLauncher<Bundle> updateBookLauncher;
    View rootView;
    public ActivityResultLauncher<Bundle> getInsertBookLauncher() {
        return insertBookLauncher;
    }
    public ActivityResultLauncher<Bundle> getUpdateBookLauncher() {
        return updateBookLauncher;
    }

    public View getRootView() {
        return rootView;
    }

    public BookList getBookList() {
        return bookList;
    }

    public BookListFragment() {
        bookList = new BookList();
    }

    public static BookListFragment newInstance() {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_book_list, container, false);
        bookList.loadFileData(rootView.getContext());
        //region RecyclerView
        BookAdapter bookAdapter = new BookAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        RecyclerView recyclerView = rootView.findViewById(R.id.recycle_view_books);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(),DividerItemDecoration.VERTICAL));
        //endregion

        insertBookLauncher = registerForActivityResult(new BookDetailActivityResultContract(), result -> {
            if(result != null){
                bookList.getList().add((Book) result.getSerializable("book"));
                bookAdapter.notifyItemInserted(bookList.getList().size()-1);
                bookList.saveFileData(rootView.getContext());
            }

        });
        updateBookLauncher = registerForActivityResult(new BookDetailActivityResultContract(), result ->{
            if(result != null){
                int position = result.getInt("position");
                Book b = (Book) result.getSerializable("book");
                bookList.getList().get(position).setTitle(b.getTitle());
                bookAdapter.notifyItemChanged(position);
                bookList.saveFileData(rootView.getContext());
            }
        });
        return rootView;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(activity.getRootView().getContext());
            builder.setMessage("您确定要删除记录吗")
                    .setPositiveButton(R.string.ok_btn, (dialogInterface, i) -> {
                        bookList.getList().remove(idx);
                        notifyItemRemoved(idx);
                        bookList.saveFileData(activity.getRootView().getContext());
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
    BookListFragment activity;
    public BookAdapter(BookListFragment activity){
        this.activity = activity;
        this.bookList = activity.getBookList();
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

class BookDetailActivityResultContract extends ActivityResultContract<Bundle, Bundle> {
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