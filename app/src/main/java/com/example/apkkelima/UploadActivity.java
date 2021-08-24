package com.example.apkkelima;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.example.apkkelima.ClaimCrud.convertToBase64;

public class UploadActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    Intent myFileIntent;
    private TextView txt2;

    private ImageButton btnBack, btnAddFile;
    private Button btnSubmit;
    private EditText edtDesc;
    String strDate;
    private SharedPreferences sharedPreferences;
    private String extraUser_id;
    private static String URL_UPLOAD = "http://asik-dev.com/api/upload1.php";
    private String TAG = "upload";
    private String pathFile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        btnBack = findViewById(R.id.btnBack);
        btnAddFile = findViewById(R.id.btnAddFile);
        btnSubmit = findViewById(R.id.btnSubmit);
        edtDesc = findViewById(R.id.edtDesc);
        txt2=findViewById(R.id.txt2);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        strDate = sdf.format(c.getTime());


        sharedPreferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
        extraUser_id =  sharedPreferences.getString("id_user", "");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHome();
            }
        });

        btnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFileIntent=new Intent(Intent.ACTION_GET_CONTENT);
                myFileIntent.setType("*/*");
                startActivityForResult(myFileIntent, 10);

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }
    public void backHome() {
        Intent intent = new Intent(UploadActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 10:
                if (resultCode==RESULT_OK){
                    String string = data.getData().getPath();
                    txt2.setText(string);

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (selectedImage != null) {
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            pathFile = cursor.getString(columnIndex);
                            Log.e(TAG, "onActivityResult: " + pathFile.toString());
                            cursor.close();
                        }
                    }
                }
                break;
        }

    }
    private void submitData(){
        //loading.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);
        //final String edtDesc = this.edtDesc.getText().toString().trim();
        //btnSubmit = findViewById(R.id.btnSubmit);
        //Log.e(TAG, "submitData: " + ImHasilFoto);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d(TAG, "submitData: " + jsonObject);
                        String success = jsonObject.getString("success");

                        if (success.equals("1")) {
                            Toast.makeText(UploadActivity.this, "Submit Success!", Toast.LENGTH_SHORT).show();
                            //loading.setVisibility(View.GONE);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "submitData: " + e.getMessage());
                        Toast.makeText(UploadActivity.this, "Submit Erorr!" + e.toString(), Toast.LENGTH_SHORT).show();
                        //loading.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(UploadActivity.this, "Submit Erorr!" + error.toString(), Toast.LENGTH_SHORT).show();
                    //loading.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                /**params.put("datetime", strDate);
                params.put("id_user", extraUser_id);
                params.put("description", edtDesc.getText().toString());
                params.put("filename", txt2.getText().toString());*/
                params.put("file_name", convertToBase64(pathFile));

                Log.e(TAG, "getParams: " + params.toString());
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public static String convertToBase64(String imagePath) {
        try {
            Bitmap bm = BitmapFactory.decodeFile(imagePath);
            //    bm = Bitmap.createScaledBitmap(bm, 1024, 1024, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] byteArrayImage = baos.toByteArray();
            return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("", "convertToBase64: "+e.getMessage());
            return "";
        }
    }
}