package com.example.apkkelima;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private ImageButton btnAbDatang, btnAbPulang, btnClaim, btnTask, btnUpload, btnLogout;
    private Button btnFotoP;
    private Bitmap bitmap;
    private ImageView FotoProfile;
    private TextView name, jabatan;
    String getId;
    //private static String URL_UPLOAD = "http://192.168.43.159/android_register_login/upload.php";
    private Menu action;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnAbDatang = findViewById(R.id.btnAbDatang);
        btnAbPulang = findViewById(R.id.btnAbPulang);
        btnClaim = findViewById(R.id.btnClaim);
        btnTask = findViewById(R.id.btnTask);
        btnUpload = findViewById(R.id.btnUpload);
        //btnFotoP = findViewById(R.id.btnFotoP);
        FotoProfile = findViewById(R.id.FotoProfile);
        name = findViewById(R.id.name);
       jabatan = findViewById(R.id.jabatan);
        btnLogout = findViewById(R.id.btnLogout);

        sharedPreferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);

        Intent intent =getIntent();
        String extraName =  sharedPreferences.getString("Name", "");
        String extraJabatan = sharedPreferences.getString("jabatan", "");

        name.setText(extraName);
        jabatan.setText(extraJabatan);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        btnAbPulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbsenPulang();

            }
        });

        btnAbDatang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbsenDatang();
            }
        });

        btnClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Claim();
            }
        });

        btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Task();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Upload();
            }
        });
    }


    private void AbsenDatang(){
        Intent panggil = new Intent(HomeActivity.this, AbsenDatang.class);
        startActivity(panggil);
    }

    private void AbsenPulang(){
        Intent panggil = new Intent(HomeActivity.this, AbsenPulang.class);
        startActivity(panggil);
    }


    private void Claim(){
        Intent panggil = new Intent(HomeActivity.this, ClaimCrud.class);
        startActivity(panggil);
    }

   private void Task(){
        Intent panggil = new Intent(HomeActivity.this, TaskActivity.class);
        startActivity(panggil);
    }

    private void Upload(){
        Intent panggil = new Intent(HomeActivity.this, UploadActivity.class);
        startActivity(panggil);
    }

}
