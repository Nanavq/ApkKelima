package com.example.apkkelima;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ClaimActivity extends AppCompatActivity {

    private Button btnSumClaim, btnCRUD;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim);

        btnBack = findViewById(R.id.btnBack);
        btnCRUD = findViewById(R.id.btnCRUD);
        btnSumClaim = findViewById(R.id.btnSumClaim);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackHome();
            }
        });

        btnCRUD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNew();
            }
        });

        btnSumClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                summary();
            }
        });

    }

    public void BackHome() {
        Intent panggil = new Intent(ClaimActivity.this, HomeActivity.class);
        startActivity(panggil);
    }

    public void AddNew(){
        Intent intent = new Intent(ClaimActivity.this, ClaimCrud.class);
        startActivity(intent);
    }

    public void summary(){
        Intent intent = new Intent(ClaimActivity.this, SummaryClaim.class);
        startActivity(intent);
    }
}