package com.example.apkkelima;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.apkkelima.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimCrud extends AppCompatActivity {

    public Spinner spTC;
    private EditText edtTanggal, edtAmount, edtDesc;
    private ImageButton btnAddImage, btnBack;
    private Button btnSubmit;
    private ImageView ImHasilFoto;
    private DatePickerDialog datePickerDialog;
    SimpleDateFormat dateFormat;
   // ActivityMainBinding binding;
   private static final int IMAGE_PICK_CODE = 1000;
   private static final int PERMISSION_CODE = 1001;
    private ProgressBar loading;
    private static String URL_CLAIM = "http://asik-dev.com/api/claim_crud.php";
    private String extraUser_id;
    private String TAG = "ClaimCrud";
    private String pathFoto = "";
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding=ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_claim_crud);

        sharedPreferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
        extraUser_id =  sharedPreferences.getString("id_user", "");

        spTC = findViewById(R.id.spTC);
        edtTanggal = findViewById(R.id.edtTanggal);
        edtAmount = findViewById(R.id.edtAmount);
        edtDesc = findViewById(R.id.edtDesc);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnBack = findViewById(R.id.btnBack);
        btnSubmit = findViewById(R.id.btnSubmit);
        ImHasilFoto = findViewById(R.id.ImHasilFoto);

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        //membuat list spinner
        List<String> item = new ArrayList<>();
        //item.add("Type Claim");
        item.add("Pulsa");
        item.add("Kuota Internet");

        //untuk membuat adapter list kota
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClaimCrud.this, android.R.layout.simple_spinner_dropdown_item, item);

        //untuk menentukan model adsapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //menerapkan adapter
        spTC.setAdapter(adapter);

        edtTanggal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);

                    } else {
                        pickImageFromGallery();

                    }
                } else {
                    pickImageFromGallery();

                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackHome();
            }
        });


    }
    public void BackHome(){
        Intent intent = new Intent(ClaimCrud.this, ClaimActivity.class);
        startActivity(intent);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0]==
                        PackageManager.PERMISSION_GRANTED){

                    //permission was granted
                    pickImageFromGallery();
                }
                else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
                }
            }
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE)) {
            ImHasilFoto.setImageURI(data.getData());

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    pathFoto = cursor.getString(columnIndex);
                    Log.e(TAG, "onActivityResult: "+pathFoto.toString() );
                    cursor.close();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDateDialog(){
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(ClaimCrud.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                edtTanggal.setText(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void submitData(){
        //loading.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);


        final String edtTanggal =this.edtTanggal.getText().toString().trim();
        final String spTC = this.spTC.getSelectedItem().toString().trim();
        final String edtAmount = this.edtAmount.getText().toString().trim();
        final String ImHasilFoto = this.ImHasilFoto.getDrawable().toString().trim();
        final String edtDesc = this.edtDesc.getText().toString().trim();
        btnSubmit = findViewById(R.id.btnSubmit);
        Log.e(TAG, "submitData: "+ ImHasilFoto );

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CLAIM,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d(TAG, "submitData: " +jsonObject);
                        String success = jsonObject.getString("success");

                        if (success.equals("1")){
                            Toast.makeText(ClaimCrud.this, "Submit Success!", Toast.LENGTH_SHORT).show();
                            //loading.setVisibility(View.GONE);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "submitData: "+e.getMessage() );
                        Toast.makeText(ClaimCrud.this, "Submit Erorr!" +e.toString(), Toast.LENGTH_SHORT).show();
                        //loading.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(ClaimCrud.this, "Submit Erorr!" +error.toString(), Toast.LENGTH_SHORT).show();
                    //loading.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("dates", edtTanggal);
                params.put("id_user", extraUser_id);
                params.put("type_claim", spTC);
                params.put("amount", edtAmount);
                params.put("image_upload", convertToBase64(pathFoto));
                params.put("description", edtDesc);
                Log.e(TAG, "getParams: "+ params.toString());
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
