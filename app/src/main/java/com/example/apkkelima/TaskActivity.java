package com.example.apkkelima;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class TaskActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private Button btnOpen, btnClose, btnSched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        btnBack = findViewById(R.id.btnBack);
        btnOpen = findViewById(R.id.btnOpen);
        btnClose = findViewById(R.id.btnClose);
        btnSched = findViewById(R.id.btnSched);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHome();
            }
        });

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTask();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskClose();
            }
        });

        btnSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    btnSched();
            }
        });
    }

    private void btnSched() {
        Intent panggil = new Intent(TaskActivity.this, ScheduleActivity.class);
        startActivity(panggil);
    }

    public void backHome(){
        Intent intent = new Intent(TaskActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void openTask(){
        Intent intent = new Intent(TaskActivity.this, TaskOpen.class);
        startActivity(intent);
    }
    private void taskClose(){
        Intent intent = new Intent(TaskActivity.this, TaskClose.class);
        startActivity(intent);
    }
}