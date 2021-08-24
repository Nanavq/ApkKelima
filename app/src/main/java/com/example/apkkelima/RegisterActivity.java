package com.example.apkkelima;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class
RegisterActivity extends AppCompatActivity {
    private EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister, btnLogin;
    private ProgressBar loading;
    private static String URL_REGIST = "http://asik-dev.com/api/register.php";
    private String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = findViewById(R.id.loading);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);  
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Regist();
            }
        });
    }

    private void Login(){
        Intent panggil = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(panggil);

    }

    private void Regist(){
        loading.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.GONE);

        final String edtUsername =this.edtUsername.getText().toString().trim();
        final String edtEmail = this.edtEmail.getText().toString().trim();
        final String edtPassword = this.edtPassword.getText().toString().trim();
        btnRegister = findViewById(R.id.btnRegister);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d(TAG, "Regist: " +jsonObject);
                        String success = jsonObject.getString("success");

                        if (success.equals("1")){
                            Toast.makeText(RegisterActivity.this, "Register Success!", Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            Intent panggil = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(panggil);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Register Erorr!" +e.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btnRegister.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(RegisterActivity.this, "Register Erorr!" +error.toString(), Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                    btnRegister.setVisibility(View.VISIBLE);
                })
            
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("name", edtUsername);
                params.put("email", edtEmail);
                params.put("password", edtPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}