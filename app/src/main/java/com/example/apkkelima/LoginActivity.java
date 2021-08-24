package com.example.apkkelima;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView link_regist, edtJabatan;
    private ProgressBar loading;
    private static String URL_LOGIN= "http://asik-dev.com/api/login.php";
    private SharedPreferences sharedPreferences;
    //private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //sessionManager = new SessionManager(this);

        loading = findViewById(R.id.loading);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        //link_regist = findViewById(R.id.link_regist);
        sharedPreferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);


//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//            }
//        });


//    public void Home() {
//        Intent panggil = new Intent(LoginActivity.this, HomeActivity.class);
//        startActivity(panggil);
//    }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mUsername = edtUsername.getText().toString().trim();
                String mPassword = edtPassword.getText().toString().trim();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Name", mUsername);
                editor.apply();

                if (!mUsername.isEmpty() || !mPassword.isEmpty()) {
                    Login(mUsername, mPassword);
                }
                else {
                    edtUsername.setError("Please insert the Username");
                    edtPassword.setError("Please insert password");
                }
            }
        });
//        link_regist.setOnClickListener(new View.OnClickListener() {
//         @Override
//        public void onClick(View v) {
//        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//        }
//        });
    }

    private void Login (String edtUsername, String edtPassword) {
        loading.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("respon login", "onResponse: "+jsonObject.toString() );
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                String mJabatan=jsonObject1.getString("jabatan");
                                //String muser_id=jsonObject1.getString("user_id");
                                Log.e("respon login", "onResponse: "+mJabatan );


                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("jabatan", mJabatan);
                                //editor.putString("user_id", muser_id);
                                editor.apply();

                                Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                Intent panggil = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(panggil);

                            }
                            else{
                                loading.setVisibility(View.GONE);
                                btnLogin.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Login Gagal", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            btnLogin.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        }


                    },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Error" + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", edtUsername);
                params.put("password", edtPassword);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
