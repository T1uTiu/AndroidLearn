package com.example.learningproject.Fragments.Task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.learningproject.Interface.TaskChangeObserver;
import com.example.learningproject.Manager.ScoreManager;
import com.example.learningproject.Navigation.TaskDetailResultContract;
import com.example.learningproject.R;
import com.example.learningproject.Model.Task.Task;
import com.example.learningproject.Manager.TaskManager;
import com.example.learningproject.Model.Task.TaskType;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends Fragment implements TaskChangeObserver {
    private TaskType taskType;
    View rootView;
    TaskAdapter adapter;
    ActivityResultLauncher<Bundle> taskDetailLauncher;
    public TaskListFragment() {
        // Required empty public constructor
    }


    public static TaskListFragment newInstance(int taskType) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putInt("taskType", taskType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskType = TaskType.valueOf(getArguments().getInt("taskType"));
            TaskManager.getInstance().attachTaskChangeObserver(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_task_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(),DividerItemDecoration.VERTICAL));
        adapter = new TaskAdapter(TaskManager.getInstance().getTaskList(taskType));
        recyclerView.setAdapter(adapter);

        taskDetailLauncher = registerForActivityResult(new TaskDetailResultContract(), result ->{
            if(result == Activity.RESULT_OK){
                Log.d("TaskDetail", "修改任务成功");
            }
        });

        return rootView;
    }

    @Override
    public void onTaskChange(TaskType taskType, int method, int idx) {
        if(taskType != this.taskType) return ;
        switch (method){
            case TaskChangeObserver.ADD:
                adapter.notifyItemInserted(adapter.getItemCount());
                break;
            case TaskChangeObserver.EDIT:
                adapter.notifyItemChanged(idx);
                break;
            case TaskChangeObserver.DELETE:
                adapter.notifyItemRemoved(idx);
                break;
        }
    }

    class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        public static final int TASK_VIEW = 0;
        public static final int EMPTY_VIEW = 1;
        private final List<Task> tasks;
        public TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            RecyclerView.ViewHolder holder;
            if(viewType == TASK_VIEW){
                View view = inflater.inflate(R.layout.listitem_task,parent,false);
                holder = new TaskViewHolder(view);
            }else{
                View view = inflater.inflate(R.layout.layout_empty,parent,false);
                holder = new EmptyViewHolder(view);
            }
            return holder;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof EmptyViewHolder){
                return;
            }
            Task task = tasks.get(position);
            ((TaskViewHolder)holder).taskNameText.setText(task.getName());
            ((TaskViewHolder)holder).taskScoreText.setText(String.format("+%d", task.getScore()));
            ((TaskViewHolder)holder).taskTimesText.setText(String.format("%d/%d", task.getCurrentTimes(), task.getTotalTimes()));
        }

        @Override
        public int getItemCount() {
            return tasks.size()==0 ? 1 : tasks.size();
        }

        @Override
        public int getItemViewType(int position) {
            return tasks.size() == 0 ? EMPTY_VIEW : TASK_VIEW;
        }

        class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{
            private final TextView taskNameText;
            private final TextView taskScoreText;
            private final TextView taskTimesText;
            CheckBox taskCheckBox;

            public TaskViewHolder(@NonNull View itemView) {
                super(itemView);
                this.taskNameText = itemView.findViewById(R.id.task_name_text);
                this.taskScoreText = itemView.findViewById(R.id.task_score_text);
                this.taskTimesText = itemView.findViewById(R.id.task_progress_text);
                this.taskCheckBox = itemView.findViewById(R.id.task_check);
                this.taskCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
                    int idx = getAdapterPosition();
                    Task task = tasks.get(idx);
                    if(b){
                        if(TaskManager.getInstance().finishTask(task)){
                            TaskManager.getInstance().deleteTask(task.getType(), idx);
                            ScoreManager.getInstance().addScoreLog(task.getScore(), task.getName());
                        }else{
                            notifyItemChanged(idx);
                        }
                        this.taskCheckBox.setChecked(false);

                    }
                });
                itemView.setOnCreateContextMenuListener(this);
                itemView.setOnClickListener(view -> {
                    int idx = getAdapterPosition();
                    Task task = tasks.get(idx);
                    Bundle bundle = new Bundle();
                    bundle.putInt("method", 1);
                    bundle.putInt("idx", idx);
                    bundle.putInt("taskType", task.getType().value());
                    bundle.putInt("editType", 0);
                    taskDetailLauncher.launch(bundle);
                });
            }

            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                int idx = getAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                builder.setMessage("确定删除任务吗？")
                        .setPositiveButton("确定", (dialogInterface, i) -> TaskManager.getInstance().deleteTask(taskType, idx))
                        .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss());
                builder.show();
                return true;
            }

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuItem delete = contextMenu.add(Menu.NONE, 1,1, "删除");
                delete.setOnMenuItemClickListener(this);
            }

        }
        class EmptyViewHolder extends RecyclerView.ViewHolder{
            public EmptyViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
