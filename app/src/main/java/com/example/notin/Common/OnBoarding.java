package com.example.notin.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.notin.Student.Home;
import com.example.notin.Student.UploadActivity;
import com.example.notin.Utils.SharedPrefUtil;
import com.example.notin.adapters.SliderAdapter;
import com.example.notin.R;

import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OnBoarding extends AppCompatActivity {

    //Variables
    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsGetStarted;
    Animation animation;
    int currentPos;
    SharedPrefUtil sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_boarding);

        sharedPref = new SharedPrefUtil(OnBoarding.this);
        sharedPref.saveString("teacher", String.valueOf(0));
        //Hooks
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.get_started_btn);

        //Call adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        //Dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    public void getStarted(View view) {
        //startActivity(new Intent(this, Home.class));
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }

    private void addDots(int position) {

        dots = new TextView[2];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("•"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.purple));
        }

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            Toast.makeText(OnBoarding.this, "hi" + position, Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;

//            Toast.makeText(OnBoarding.this, "hi" + position, Toast.LENGTH_SHORT ).show();
//            if (position == 0) {
//                letsGetStarted.setVisibility(View.INVISIBLE);
//            } else if (position == 1) {
//                letsGetStarted.setVisibility(View.INVISIBLE);
//            } else if (position == 2) {
//                letsGetStarted.setVisibility(View.INVISIBLE);
//            } else {
//                animation = AnimationUtils.loadAnimation(OnBoarding.this, R.anim.bottom_anim);
//                letsGetStarted.setAnimation(animation);
                sharedPref.saveString("teacher", String.valueOf(position));
                letsGetStarted.setVisibility(View.VISIBLE);
//            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}