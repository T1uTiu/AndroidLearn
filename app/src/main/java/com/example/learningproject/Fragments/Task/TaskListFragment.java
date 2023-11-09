package com.example.learningproject.Fragments.Task;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learningproject.R;
import com.example.learningproject.data.Task.Task;
import com.example.learningproject.data.Task.TaskManager;
import com.example.learningproject.data.Task.TaskType;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends Fragment {
    private TaskType taskType;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(),DividerItemDecoration.VERTICAL));
        TaskManager.getInstance().loadTaskList(rootView.getContext(), taskType);
        TaskAdapter adapter = new TaskAdapter(TaskManager.getInstance().getTaskList(taskType));
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    static class TaskViewHolder extends RecyclerView.ViewHolder{
        private final TextView taskNameText;
        private final TextView taskScoreText;
        private final TextView taskTimesText;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            this.taskNameText = itemView.findViewById(R.id.task_name_text);
            this.taskScoreText = itemView.findViewById(R.id.task_score_text);
            this.taskTimesText = itemView.findViewById(R.id.task_progress_text);

        }

        public TextView getTaskNameText() {
            return taskNameText;
        }

        public TextView getTaskScoreText() {
            return taskScoreText;
        }

        public TextView getTaskTimesText() {
            return taskTimesText;
        }
    }
    private List<Task> tasks;
    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }
    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_task,parent,false);
        return new TaskViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.getTaskNameText().setText(task.getName());
        holder.getTaskScoreText().setText(String.format("+%d", task.getScore()));
        holder.getTaskTimesText().setText(String.format("%d/%d", task.getCurrentTimes(), task.getTimes()));

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}