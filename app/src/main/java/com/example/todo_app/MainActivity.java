package com.example.todo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TASKS_KEY = "tasks";
    private static final String PREFS_NAME = "todo_prefs";
    private EditText editTextTask;
    private ArrayList<String> taskList;
    private TaskAdapter taskAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        ListView listViewTasks = findViewById(R.id.listViewTasks);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        taskList = new ArrayList<>(getSavedTasks());

        taskAdapter = new TaskAdapter(taskList);
        listViewTasks.setAdapter(taskAdapter);

        buttonAdd.setOnClickListener(v -> {
            String task = editTextTask.getText().toString().trim();
            if (!task.isEmpty()) {
                taskList.add(task);
                saveTasks();
                taskAdapter.notifyDataSetChanged();
                editTextTask.setText("");  // Clear the input field
            }
        });
    }

    // Load saved tasks from SharedPreferences
    private Set<String> getSavedTasks() {
        return sharedPreferences.getStringSet(TASKS_KEY, new HashSet<>());
    }

    // Save the current tasks in SharedPreferences
    private void saveTasks() {
        Set<String> taskSet = new HashSet<>(taskList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(TASKS_KEY, taskSet);
        editor.apply();
    }

    private class TaskAdapter extends ArrayAdapter<String> {

        public TaskAdapter(ArrayList<String> tasks) {
            super(MainActivity.this, R.layout.task_item, tasks);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
            }

            String task = getItem(position);

            TextView taskText = convertView.findViewById(R.id.textViewTask);
            Button deleteButton = convertView.findViewById(R.id.buttonDelete);

            taskText.setText(task);

            deleteButton.setOnClickListener(v -> {
                taskList.remove(position);
                saveTasks();  // Save changes when a task is deleted
                notifyDataSetChanged();
            });

            return convertView;
        }
    }
}
