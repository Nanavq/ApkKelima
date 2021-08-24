package com.example.apkkelima;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import java.util.List;
import java.util.Map;

import static com.example.apkkelima.R.id.view;

public class SummaryClaim extends AppCompatActivity {
    private ImageButton btnBack;
    private SharedPreferences sharedPreferences;
    private static String URL_CSUM = "http://asik-dev.com/api/claim_summary.php";
    private String extraUser_id;
    TextView status,dateClaim,type_claim,description,amount;
    private ListView listView;
    private String TAG = "sum";
    MyAdapter adapter;
    int position;
    public static ArrayList<claimDetail> claimDetailst = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_claim);

        sharedPreferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
        extraUser_id =  sharedPreferences.getString("id_user", "");

        btnBack = findViewById(R.id.btnBack);
        status = findViewById(R.id.status);
        dateClaim = findViewById(R.id.dateClaim);
        type_claim = findViewById(R.id.type_claim);
        description = findViewById(R.id.description);
        amount = findViewById(R.id.amount);

        //listView=findViewById(R.id.myList);
        //adapter = new MyAdapter(this, ClaimCrud)


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHome();
            }
        });


    }


    public void backHome() {
        Intent panggil = new Intent(SummaryClaim.this, ClaimActivity.class);
        startActivity(panggil);
    }

    private class MyAdapter {
    }
}