package com.example.apkkelima;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.gzeinnumer.eeda.helper.FGFile;
import com.gzeinnumer.eeda.helper.imagePicker.FileCompressor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rebus.permissionutils.PermissionEnum;

import static android.Manifest.permission.CAMERA;
import static com.example.apkkelima.ClaimCrud.convertToBase64;

public class AbsenDatang extends AppCompatActivity {
//    private PermissionEnum[] permissions = new PermissionEnum[]{
//            PermissionEnum.READ_EXTERNAL_STORAGE,
//            PermissionEnum.WRITE_EXTERNAL_STORAGE,
//            PermissionEnum.CAMERA,
//            PermissionEnum.ACCESS_COARSE_LOCATION,
//            PermissionEnum.ACCESS_FINE_LOCATION
//    };
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static File mPhotoFile;
    private FileCompressor mCompressor;
    private Button btnCamera;
    private ImageView imageView;

    private ImageButton btnBack, btnFotoDatang;
    private Button btnSubmit;
    private ImageView ImHasilFoto;
    private TextView GetDate, locId;
    private Uri file;
    private ProgressBar loading;
    private static String URL_SUBMIT = "https://asik-dev.com/api/absen_datang.php";
    private String TAG = "AbsenDatang";
    FusedLocationProviderClient fusedLocationProviderClient;
    private String Latitude = " ";
    private String Longitude = " ";
    //private String Address = " ";
    private String pathFoto = "";
    private String StrDate;
    private SharedPreferences sharedPreferences;
    private String extraUser_id;
    //private String extraName;
    String encodedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_datang);

        btnBack = findViewById(R.id.btnBack);
        btnFotoDatang = findViewById(R.id.btnFotoDatang);
        btnSubmit = findViewById(R.id.btnSubmit);
        ImHasilFoto = findViewById(R.id.ImHasilFoto);
        locId = findViewById(R.id.locId);
        GetDate = findViewById(R.id.GetDate);
        loading = findViewById(R.id.loading);

        fusedLocationProviderClient = new FusedLocationProviderClient(this);


        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        StrDate = sdf.format(c.getTime());

        TextView textViewDate = findViewById(R.id.GetDate);
        textViewDate.setText(StrDate);

        sharedPreferences = getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
        extraUser_id = sharedPreferences.getString("user_id", "");
        //extraName =  sharedPreferences.getString("name", "");

        //request for cammera runtime permission
        if (ContextCompat.checkSelfPermission(AbsenDatang.this, CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AbsenDatang.this, new String[]{
                    CAMERA
            }, 100);
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackHome();
            }
        });

//        btnFotoDatang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 100);
//            }
//        });

        btnFotoDatang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] per = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA};
                requestPermission(
                       per,
                        new PermissionCallBack() {
                            @Override
                            public void isGranted(boolean isGranted) {
                                if (isGranted)
                                    dispatchTakePictureIntent();
                            }
                        });
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
        Intent panggil = new Intent(AbsenDatang.this, HomeActivity.class);
        startActivity(panggil);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 100) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            ImHasilFoto.setImageBitmap(bitmap);
//            ImHasilFoto(bitmap);
//        }
//    }

    private void ImHasilFoto(Bitmap bitmap) {
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageType = byteArrayOutputStream.toByteArray();
            encodedImage = android.util.Base64.encodeToString(imageType, Base64.DEFAULT);


        }

    }
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        try {
//            if (data.getExtras().get("data") != null) {
//                String[] projection = {MediaStore.Images.Media.DATA};
//                Cursor cursor = managedQuery(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        projection, null, null, null);
//                int column_index_data = cursor
//                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToLast();
//
//                String imagePath = cursor.getString(column_index_data);
//                Bitmap bitmapImage = BitmapFactory.decodeFile(imagePath);
//                pathFoto = imagePath;
//                ImHasilFoto.setImageBitmap(bitmapImage);
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
//    }

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
                        Geocoder geocoder = new Geocoder(AbsenDatang.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
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
        loading.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);
        //final String GetDate =this.GetDate.getText().toString().trim();
        final String ImHasilFoto = this.ImHasilFoto.getDrawable().toString().trim();

        //btnSubmit = findViewById(R.id.btnSubmit);
        Log.e(TAG, "submitData: " + ImHasilFoto);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SUBMIT,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d(TAG, "submitData: " + jsonObject);
                        String success = jsonObject.getString("success");

                        if (success.equals("1")) {
                            Toast.makeText(AbsenDatang.this, "Submit Success!", Toast.LENGTH_SHORT).show();
                            //loading.setVisibility(View.GONE);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "submitData: " + e.getMessage());
                        Toast.makeText(AbsenDatang.this, "Submit Erorr!" + e.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(AbsenDatang.this, "Submit Erorr!" + error.toString(), Toast.LENGTH_SHORT).show();
                    loading.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", extraUser_id);
                params.put("latitude", Latitude);
                params.put("longitude", Longitude);
                params.put("image1", convertToBase64(encodedImage));
                params.put("datetimein", StrDate);
                //params.put("image1", convertToBase64(pathFoto));
                //params.put("image1", encodedImage);
                //params.put("date", GetDate);
                //params.put("location", locId);
//                params.put("file", encodedImage);
//                params.put("file", ImHasilFoto);
//                params.put("name", extraName);
//                params.put("jabatan", jabatan);
//                params.put("bagian", bagian);
                Log.e(TAG, "getParams: " + params.toString());
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public static String convertToBase64(String pathFoto) {
        try {
            Bitmap bm = BitmapFactory.decodeFile(pathFoto);
            //    bm = Bitmap.createScaledBitmap(bm, 1024, 1024, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] byteArrayImage = baos.toByteArray();
            return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("", "convertToBase64: " + e.getMessage());
            return "";


        }
    }
    //3
    //jalankan intent untuk membuka kamera
    private void dispatchTakePictureIntent() {
        mCompressor = new FileCompressor(this);
        // int quality = 50;
        // mCompressor = new FileCompressor(this, quality);
        //   /storage/emulated/0/MyLibsTesting/Foto
        mCompressor.setDestinationDirectoryPath("/Foto");
        //diretori yang dibutuhkan akan lansung dibuatkan oleh fitur ini

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                photoFile = FGFile.createImageFile(getApplicationContext(), fileName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);

                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //4
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    //setelah foto diambil, dan tampil di preview maka akan lansung disimpan ke folder yang di sudah diset sebelumnya
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                    Glide.with(AbsenDatang.this).load(mPhotoFile).into(ImHasilFoto);
                    Toast.makeText(this, "Image Path : "+mPhotoFile.toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void requestPermission(String[] single, PermissionCallBack permissionCallBack) {
        Dexter.withActivity(this)
                .withPermissions(single)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        permissionCallBack.isGranted(report.areAllPermissionsGranted());
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public interface PermissionCallBack {
        void isGranted(boolean isGranted);
    }
}