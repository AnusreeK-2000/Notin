package com.example.notin.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.notin.R;
import com.example.notin.Student.Home;

import pl.droidsonroids.gif.GifImageView;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER = 3000;

    GifImageView backgroundImage;
    TextView appname;
    TextView slogan;

    //Animations
    Animation sideAnim, bottomAnim;

    SharedPreferences sharedPreferences;
    Boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        firstTime = sharedPreferences.getBoolean("firstTime", true);
        //Hooks
        backgroundImage = findViewById(R.id.backgroundImage);
        appname = findViewById(R.id.app_name);
        slogan = findViewById(R.id.slogan);

        //Animations
        sideAnim = AnimationUtils.loadAnimation(this,R.anim.side_anim);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        //set animations
        backgroundImage.setAnimation(sideAnim);
        appname.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        if(firstTime){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    firstTime = false;

                    editor.putBoolean("firstTime", firstTime);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), OnBoarding.class);
                    startActivity(intent);
                    finish();
                }
            },SPLASH_TIMER);

        }
        else{
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
        }



    }
}