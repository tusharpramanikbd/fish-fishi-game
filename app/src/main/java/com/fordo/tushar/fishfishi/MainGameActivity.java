package com.fordo.tushar.fishfishi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainGameActivity extends AppCompatActivity {
    public static final long START_TIME_IN_MILLIS = 30000;
    private boolean threadFlag1, threadFlag2, threadFlag3, threadFlag4;
    public TextView mTextViewCountDown, tv_coin, tv_medicine, tv_medicine_coin;
    public Button btn_feed_fish, btn_sell_fish, btn_medicine , btn_medicine_coin;
    public int growth_percentage = 0, health_percentage = 100;
    int coin = 0;
    ProgressBar growth_progressBar, health_progressBar;


    private CountDownTimer mCountDownTimer;

    public static boolean isTimerRunning, isDeadFishClean, isSold, isMedicineBtnVisible = false;

    private long mTimeLeftInMillis;
    private long mEndTime;
    private long timeLeftForHealthReduce;
    Thread t = null;

    LottieAnimationView backgroundView, foodView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lobster_1.3.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        growth_progressBar = findViewById(R.id.groth_progress);
        Drawable progressDrawableForGrowth = growth_progressBar.getProgressDrawable().mutate();
        progressDrawableForGrowth.setColorFilter(Color.parseColor("#6D4C03"), android.graphics.PorterDuff.Mode.SRC_IN);
        growth_progressBar.setProgressDrawable(progressDrawableForGrowth);

        health_progressBar = findViewById(R.id.health_progress);
        Drawable progressDrawableForHealth = health_progressBar.getProgressDrawable().mutate();
        progressDrawableForHealth.setColorFilter(Color.parseColor("#32AD61"), android.graphics.PorterDuff.Mode.SRC_IN);
        health_progressBar.setProgressDrawable(progressDrawableForHealth);
        health_progressBar.setProgress(health_percentage);

        tv_coin = findViewById(R.id.text_view_coin);
        tv_medicine = findViewById(R.id.text_view_medicine);
        tv_medicine_coin = findViewById(R.id.text_view_medicine_coin);

        btn_medicine = findViewById(R.id.button_medicine);
        btn_medicine_coin = findViewById(R.id.button_medicine_coin);

        backgroundView = findViewById(R.id.background_view);
        backgroundView.setAnimation("lvl1.json");
        backgroundView.playAnimation();

        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        btn_feed_fish = findViewById(R.id.button_start_pause);
        btn_sell_fish = findViewById(R.id.button_sellfish);

        btn_feed_fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isTimerRunning) {
                Toast.makeText(MainGameActivity.this,"You Have To Wait Till Countdown Finish", Toast.LENGTH_LONG).show();
            }
            else {
                if(isMedicineBtnVisible){
                    Toast.makeText(MainGameActivity.this,"You Have To Give Medicine First", Toast.LENGTH_LONG).show();
                }
                else {
                    startTimer();

                    foodView = (LottieAnimationView) findViewById(R.id.food_view);
                    foodView.setAnimation("NewfoodDropandCoin.json");
                    foodView.playAnimation();

                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("timeLeftForHealthReduce", (START_TIME_IN_MILLIS * 2) + System.currentTimeMillis());
                    editor.putBoolean("isDeadFishClean", false);


                    growth_percentage = growth_percentage + 20;
                    growth_progressBar.setProgress(growth_percentage);
                    editor.putInt("GrothPercentage", growth_percentage);
                    editor.apply();

                    coin = coin + 10;
                    tv_coin.setText(Integer.toString(coin));

                    if(growth_percentage == 20){
                        backgroundView.setAnimation("lvl2.json");
                        backgroundView.playAnimation();
                    }
                    else if(growth_percentage == 40){
                        backgroundView.setAnimation("lvl3.json");
                        backgroundView.playAnimation();
                    }
                    else if(growth_percentage == 60){
                        backgroundView.setAnimation("lvl4.json");
                        backgroundView.playAnimation();
                    }
                    else if(growth_percentage == 80){
                        backgroundView.setAnimation("lvl5.json");
                        backgroundView.playAnimation();
                    }
                    else if(growth_percentage == 100){
                        backgroundView.setAnimation("lvl6.json");
                        backgroundView.playAnimation();
                        btn_sell_fish.setVisibility(View.VISIBLE);
                        btn_feed_fish.setEnabled(false);
                    }

                    threadFlag1 = true;
                    threadFlag2 = true;
                    threadFlag3 = true;
                    threadFlag4 = true;
                }
            }
            }
        });

        btn_sell_fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(health_percentage < 100){
                    Toast.makeText(getApplicationContext(),"You Can Not Sell Sick Fish\nPlease Give Medicine First",Toast.LENGTH_LONG).show();
                }
                else {
                    coin = coin + 100;
                    growth_percentage = 0;
                    resetTimer();

                    //By setting false, the thread will not execute after goto sell fish Activity
                    threadFlag1 = false;
                    threadFlag2 = false;
                    threadFlag3 = false;
                    threadFlag4 = false;

                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isReadyToSell", true);
                    editor.apply();

                    Intent gotoSellFishPage = new Intent(getApplicationContext(), SellFishActivity.class);
                    startActivity(gotoSellFishPage);
                    finish();
                }

            }
        });

        btn_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                health_percentage = 100;
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("healthPercentage", health_percentage);
                editor.putLong("timeLeftForHealthReduce", START_TIME_IN_MILLIS + System.currentTimeMillis());
                editor.apply();

                threadFlag1 = true;
                threadFlag2 = true;
                threadFlag3 = true;
                threadFlag4 = true;

                Drawable progressDrawable = health_progressBar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(Color.parseColor("#32AD61"), android.graphics.PorterDuff.Mode.SRC_IN);
                health_progressBar.setProgressDrawable(progressDrawable);

                health_progressBar.setProgress(health_percentage);
                coin = coin - 5;
                tv_coin.setText(Integer.toString(coin));

                if(growth_percentage == 20){
                    backgroundView.setAnimation("lvl2.json");
                    backgroundView.playAnimation();
                }
                else if(growth_percentage == 40){
                    backgroundView.setAnimation("lvl3.json");
                    backgroundView.playAnimation();
                }
                else if(growth_percentage == 60){
                    backgroundView.setAnimation("lvl4.json");
                    backgroundView.playAnimation();
                }
                else if(growth_percentage == 80){
                    backgroundView.setAnimation("lvl5.json");
                    backgroundView.playAnimation();
                }
                else if(growth_percentage == 100){
                    backgroundView.setAnimation("lvl6.json");
                    backgroundView.playAnimation();
                    btn_sell_fish.setVisibility(View.VISIBLE);
                    btn_feed_fish.setEnabled(false);
                }

                tv_medicine.setVisibility(View.INVISIBLE);
                tv_medicine_coin.setVisibility(View.INVISIBLE);
                btn_medicine.setVisibility(View.INVISIBLE);
                isMedicineBtnVisible = false;
                btn_medicine_coin.setVisibility(View.INVISIBLE);
                btn_feed_fish.setEnabled(true);
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {

                Log.d("Tushar","Thread Running");

                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                timeLeftForHealthReduce = prefs.getLong("timeLeftForHealthReduce", System.currentTimeMillis()+10000);
                isDeadFishClean = prefs.getBoolean("isDeadFishClean", true);
                health_percentage = prefs.getInt("healthPercentage", 100);

                long checkTimeForHealth = timeLeftForHealthReduce - System.currentTimeMillis();

                if(checkTimeForHealth < 0){

                    checkTimeForHealth = Math.abs(checkTimeForHealth);

                    if(checkTimeForHealth < START_TIME_IN_MILLIS && threadFlag1){
                        unhealthyFishControl();

                        threadFlag1 = false;
                        health_percentage = health_percentage - 33;
                        editor.putInt("healthPercentage", health_percentage);
                        editor.apply();
                        health_progressBar.setProgress(health_percentage);
                        tv_medicine.setVisibility(View.VISIBLE);
                        tv_medicine_coin.setVisibility(View.VISIBLE);
                        btn_medicine.setVisibility(View.VISIBLE);
                        isMedicineBtnVisible = true;
                        btn_medicine_coin.setVisibility(View.VISIBLE);
                    }
                    else if(checkTimeForHealth > START_TIME_IN_MILLIS && checkTimeForHealth < (START_TIME_IN_MILLIS * 2) && threadFlag2 == true){
                        unhealthyFishControl();

                        threadFlag2 = false;

                        Drawable progressDrawable = health_progressBar.getProgressDrawable().mutate();
                        progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                        health_progressBar.setProgressDrawable(progressDrawable);

                        health_percentage = health_percentage - 33;
                        editor.putInt("healthPercentage", health_percentage);
                        editor.apply();
                        health_progressBar.setProgress(health_percentage);
                        tv_medicine.setVisibility(View.VISIBLE);
                        tv_medicine_coin.setVisibility(View.VISIBLE);
                        btn_medicine.setVisibility(View.VISIBLE);
                        isMedicineBtnVisible = true;
                        btn_medicine_coin.setVisibility(View.VISIBLE);
                    }
                    else if(checkTimeForHealth > (START_TIME_IN_MILLIS * 2) && checkTimeForHealth < (START_TIME_IN_MILLIS * 3) && threadFlag3 == true){
                        unhealthyFishControl();

                        threadFlag3 = false;

                        Drawable progressDrawable = health_progressBar.getProgressDrawable().mutate();
                        progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                        health_progressBar.setProgressDrawable(progressDrawable);

                        health_percentage = health_percentage - 29;
                        editor.putInt("healthPercentage", health_percentage);
                        editor.apply();
                        health_progressBar.setProgress(health_percentage);
                        tv_medicine.setVisibility(View.VISIBLE);
                        tv_medicine_coin.setVisibility(View.VISIBLE);
                        btn_medicine.setVisibility(View.VISIBLE);
                        isMedicineBtnVisible = true;
                        btn_medicine_coin.setVisibility(View.VISIBLE);
                    }
                    else if(checkTimeForHealth > (START_TIME_IN_MILLIS * 3) && !isDeadFishClean && threadFlag4){
                        health_progressBar.setProgress(0);
                        threadFlag4 = false;

                        editor.putBoolean("isDead", true);
                        editor.apply();

                        Intent gotoDeadFishPage = new Intent(getApplicationContext(), DeadFishActivity.class);
                        startActivity(gotoDeadFishPage);
                        finish();
                    }
                }

                handler.postDelayed(this, 2000);
            }
        };

        handler.postDelayed(r, 2000);
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                resetTimer();
            }
        }.start();

        isTimerRunning = true;

    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();

    }

    private void updateCountDownText() {

        if(mTimeLeftInMillis == START_TIME_IN_MILLIS){
            mTextViewCountDown.setText("00:00:00");
        }
        else {
            int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
            int mod = (int) (mTimeLeftInMillis / 1000) % 3600;
            int minutes = mod / 60;
            int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

            mTextViewCountDown.setText(timeLeftFormatted);
        }

    }

    private void unhealthyFishControl(){

        if(growth_percentage == 20){
            backgroundView.setAnimation("unlvl1.json");
            backgroundView.playAnimation();
        }
        else if(growth_percentage == 40){
            backgroundView.setAnimation("unlvl2.json");
            backgroundView.playAnimation();
        }
        else if(growth_percentage == 60){
            backgroundView.setAnimation("unlvl3.json");
            backgroundView.playAnimation();
        }
        else if(growth_percentage == 80){
            backgroundView.setAnimation("unlvl4.json");
            backgroundView.playAnimation();
        }
        else if(growth_percentage == 100){
            backgroundView.setAnimation("unlvl5.json");
            backgroundView.playAnimation();
            btn_sell_fish.setVisibility(View.VISIBLE);
            btn_feed_fish.setEnabled(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("isTimerRunning", isTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.putInt("GrothPercentage", growth_percentage);
        editor.putInt("coin", coin);
        editor.putBoolean("isSold", false);
        editor.putBoolean("isGameRunning", true);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        //Initializing SharedPreference and editor object..............................
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        //getting and setting the coin value........................................
        coin = prefs.getInt("coin", 0);
        tv_coin.setText(Integer.toString(coin));

        //getting the sharedPreference value of isTimerRunning...........................
        isTimerRunning = prefs.getBoolean("isTimerRunning", false);
        //Log.d("isTimerRunning:", Boolean.toString(isTimerRunning));

        //checking countdown running or stopped........................................
        if (isTimerRunning)//if still running or was running when last stopped..........................................
        {
            //getting the time when countdown will end.................................
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            //if the time stopped during the app was closed.....................
            if (mTimeLeftInMillis < 0)
            {
                mTimeLeftInMillis = 0;
                isTimerRunning = false;
                resetTimer();
                updateCountDownText();
            }
            else
            {
                //if countdown still running...................................
                startTimer();
            }
        }
        else
        {
            //setting the countdown to 00:00:00
            resetTimer();
            updateCountDownText();
        }

        //getting growth percentage and setting to the progressbar............
        growth_percentage = prefs.getInt("GrothPercentage", 0);
        growth_progressBar.setProgress(growth_percentage);

        //according to the growth percentage setting the animation............
        if(growth_percentage == 20){
            backgroundView.setAnimation("lvl2.json");
            backgroundView.playAnimation();
        }
        else if(growth_percentage == 40){
            backgroundView.setAnimation("lvl3.json");
            backgroundView.playAnimation();
        }
        else if(growth_percentage == 60){
            backgroundView.setAnimation("lvl4.json");
            backgroundView.playAnimation();
        }
        else if(growth_percentage == 80){
            backgroundView.setAnimation("lvl5.json");
            backgroundView.playAnimation();
        }
        else if(growth_percentage == 100){
            backgroundView.setAnimation("lvl6.json");
            backgroundView.playAnimation();
            btn_sell_fish.setVisibility(View.VISIBLE);
            btn_feed_fish.setEnabled(false);
        }




        //getting the health percentage and setting to the progressbar...............
        health_percentage = prefs.getInt("healthPercentage", 100);
        health_progressBar.setProgress(health_percentage);

        timeLeftForHealthReduce = prefs.getLong("timeLeftForHealthReduce", System.currentTimeMillis()+10000);
        isDeadFishClean = prefs.getBoolean("isDeadFishClean", true);
        isSold = prefs.getBoolean("isSold", true);
        if(isDeadFishClean == true || isSold == true){
            //SharedPreferences.Editor editor = prefs.edit();

            editor.putLong("millisLeft", 0);
            editor.putBoolean("timerRunning", false);
            editor.putLong("endTime", mEndTime);
            isMedicineBtnVisible = false;

            editor.apply();
        }



        long checkTimeForHealth = timeLeftForHealthReduce - System.currentTimeMillis();
        if(checkTimeForHealth < 0){

            checkTimeForHealth = Math.abs(checkTimeForHealth);

            if(checkTimeForHealth < START_TIME_IN_MILLIS){

                unhealthyFishControl();

                threadFlag1 = false;
                threadFlag2 = true;
                threadFlag3 = true;

                health_progressBar.setProgress(health_percentage);
                editor.putInt("healthPercentage", health_percentage);
                editor.apply();
                tv_medicine.setVisibility(View.VISIBLE);
                tv_medicine_coin.setVisibility(View.VISIBLE);
                btn_medicine.setVisibility(View.VISIBLE);
                isMedicineBtnVisible = true;
                btn_medicine_coin.setVisibility(View.VISIBLE);
            }
            else if(checkTimeForHealth > START_TIME_IN_MILLIS && checkTimeForHealth < (START_TIME_IN_MILLIS * 2)){

                unhealthyFishControl();

                threadFlag1 = false;
                threadFlag2 = false;
                threadFlag3 = true;

                Drawable progressDrawable = health_progressBar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                health_progressBar.setProgressDrawable(progressDrawable);

                health_progressBar.setProgress(health_percentage);
                editor.putInt("healthPercentage", health_percentage);
                editor.apply();
                tv_medicine.setVisibility(View.VISIBLE);
                tv_medicine_coin.setVisibility(View.VISIBLE);
                btn_medicine.setVisibility(View.VISIBLE);
                isMedicineBtnVisible = true;
                btn_medicine_coin.setVisibility(View.VISIBLE);
            }
            else if(checkTimeForHealth > (START_TIME_IN_MILLIS * 2) && checkTimeForHealth < (START_TIME_IN_MILLIS * 3)){

                unhealthyFishControl();

                threadFlag1 = false;
                threadFlag2 = false;
                threadFlag3 = false;

                Drawable progressDrawable = health_progressBar.getProgressDrawable().mutate();
                progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                health_progressBar.setProgressDrawable(progressDrawable);

                health_progressBar.setProgress(health_percentage);
                editor.putInt("healthPercentage", health_percentage);
                editor.apply();
                tv_medicine.setVisibility(View.VISIBLE);
                tv_medicine_coin.setVisibility(View.VISIBLE);
                btn_medicine.setVisibility(View.VISIBLE);
                isMedicineBtnVisible = true;
                btn_medicine_coin.setVisibility(View.VISIBLE);
            }
            else if(checkTimeForHealth > (START_TIME_IN_MILLIS * 3) && isDeadFishClean == false && isSold == false){
                health_progressBar.setProgress(0);
                Intent gotoDeadFishPage = new Intent(getApplicationContext(), DeadFishActivity.class);
                startActivity(gotoDeadFishPage);
                finish();
            }

            threadFlag4 = true;
        }
        else {
            threadFlag1 = true;
            threadFlag2 = true;
            threadFlag3 = true;
            threadFlag4 = true;
        }
    }

}
