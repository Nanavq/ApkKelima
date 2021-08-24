package com.example.apkkelima;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    ImageView imglogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //inisialisasi object ---> pengkoneksian variable widget dengan object sesungguhnya
        imglogo = (ImageView) findViewById(R.id.splash_imglogo);

        //sumber animasinya
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        //implementasikan animasinya/memulai animasi
        imglogo.startAnimation(animation);


        //Menjalankan Splash Activity dalam beberapa detik
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //apa yang terjadi->memanggil main activity
                Intent panggil = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(panggil);

                //splash screen hilang
                finish();

            }
        }, 5000);
    }
}
