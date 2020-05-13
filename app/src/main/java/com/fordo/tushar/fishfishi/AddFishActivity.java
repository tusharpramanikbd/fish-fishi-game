package com.fordo.tushar.fishfishi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddFishActivity extends AppCompatActivity {
    Button btn_addFish;
    LottieAnimationView backgroundView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfish_screen);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lobster_1.3.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        backgroundView = findViewById(R.id.background_view);
        backgroundView.playAnimation();

        btn_addFish = findViewById(R.id.button_addFish);
        btn_addFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putBoolean("isSold", false);
                editor.putBoolean("isTimerRunning", false);
                editor.putInt("GrothPercentage", 0);
                editor.putInt("healthPercentage", 100);
                editor.apply();

                Intent gotoFishGamePage = new Intent(getApplicationContext(), MainGameActivity.class);
                startActivity(gotoFishGamePage);
                finish();
            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
