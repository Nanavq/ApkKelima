package com.example.apkkelima;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static com.example.apkkelima.ClaimCrud.convertToBase64;

public class AbsenPulang extends AppCompatActivity {

    private ImageButton btnBack, btnFotoPulang;
    private Button btnSubmit;
    private ImageView ImHasilFoto;
    private TextView GetDate, locId;
    private Uri file;
    private ProgressBar loading;
    private static String URL_SUBMIT = "http://asik-dev.com/api/absen_pulang.php";
    private String TAG = "AbsenPulang";
    FusedLocationProviderClient fusedLocationProviderClient;
    private String Latitude = " ";
    private String Longitude = " ";
    //private String Address = " ";
    private String pathFoto = "";
    private String strDate;
    private SharedPreferences sharedPreferences;
    private String extraUser_id;
    //private String extraName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_pulang);

        btnBack = findViewById(R.id.btnBack);
        btnFotoPulang = findViewById(R.id.btnFotoPulang);
        btnSubmit = findViewById(R.id.btnSubmit);
        ImHasilFoto = findViewById(R.id.ImHasilFoto);
        locId = findViewById(R.id.locId);
        GetDate = findViewById(R.id.GetDate);

        fusedLocationProviderClient = new FusedLocationProviderClient(this);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        strDate = sdf.format(c.getTime());

        TextView textViewDate = findViewById(R.id.GetDate);
        textViewDate.setText(strDate);

        sharedPreferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
        extraUser_id =  sharedPreferences.getString("id_user", "");
        //extraName =  sharedPreferences.getString("name", "");

        //request for cammera runtime permission
        if (ContextCompat.checkSelfPermission(AbsenPulang.this, CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AbsenPulang.this, new String[]{
                    CAMERA
            }, 100);
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackHome();
            }
        });

        btnFotoPulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
        getLocation();
    }

    public void BackHome() {
        Intent panggil = new Intent(AbsenPulang.this, HomeActivity.class);
        startActivity(panggil);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (data.getExtras().get("data") != null) {
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = managedQuery(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, null, null, null);
                int column_index_data = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToLast();

                String imagePath = cursor.getString(column_index_data);
                Bitmap bitmapImage = BitmapFactory.decodeFile(imagePath );
                pathFoto  = imagePath;
                ImHasilFoto.setImageBitmap(bitmapImage );
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Latitude = String.valueOf(location.getLatitude());
                    Longitude = String.valueOf(location.getLatitude());

                    try {
                        Geocoder geocoder = new Geocoder(AbsenPulang.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(),  1
                        );
                        addresses.get(0).getAddressLine(0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }
        });


    }

    private void submitData() {
       //loading.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);


        //btnSubmit = findViewById(R.id.btnSubmit);
        Log.e(TAG, "submitData: " + ImHasilFoto);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SUBMIT,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        //Log.d(TAG, "submitData: " + jsonObject);
                        String success = jsonObject.getString("success");

                        if (success.equals("1")) {
                            Toast.makeText(AbsenPulang.this, "Submit Success!", Toast.LENGTH_SHORT).show();
                            //loading.setVisibility(View.GONE);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "submitData: " + e.getMessage());
                        Toast.makeText(AbsenPulang.this, "Submit Erorr!" + e.toString(), Toast.LENGTH_SHORT).show();
                        //loading.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(AbsenPulang.this, "Submit Erorr!" + error.toString(), Toast.LENGTH_SHORT).show();
                   //loading.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("datetime_out", strDate);
                params.put("image_out", convertToBase64(pathFoto));
                params.put("latitude_out", Latitude);
                params.put("longitude_out", Longitude);
                params.put("id_user", extraUser_id);
                //params.put("Name", extraName);
                //params.put("address", Address;
                Log.e(TAG, "getParams: " + params.toString());
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    /*public static String convertToBase64(String imagePath) {
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
        }*/
    }
