package com.fordo.tushar.fishfishi;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class StartScreenActivity extends AppCompatActivity {
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if(!prefs.getBoolean("firstTime",false)){
                    editor.putBoolean("firstTime", true);
                    editor.apply();
                    Intent gotoAddFishPage = new Intent(getApplicationContext(), AddFishActivity.class);
                    startActivity(gotoAddFishPage);
                    finish();
                }
                else if(prefs.getBoolean("isReadyToSell",false)){
                    Intent gotoSellFishPage = new Intent(getApplicationContext(), SellFishActivity.class);
                    startActivity(gotoSellFishPage);
                    finish();
                }
                else if(prefs.getBoolean("isSold",false)){
                    Intent gotoAddFishPage = new Intent(getApplicationContext(), AddFishActivity.class);
                    startActivity(gotoAddFishPage);
                    finish();
                }

                else if(prefs.getBoolean("isDead",false)){
                    Intent gotoDeadFishPage = new Intent(getApplicationContext(), DeadFishActivity.class);
                    startActivity(gotoDeadFishPage);
                    finish();
                }
                else if(prefs.getBoolean("isDeadFishClean",false)){
                    Intent gotoAddFishPage = new Intent(getApplicationContext(), AddFishActivity.class);
                    startActivity(gotoAddFishPage);
                    finish();
                }
                else if(prefs.getBoolean("isGameRunning",false)) {
                    Intent gotoMainFishGamePage = new Intent(getApplicationContext(), MainGameActivity.class);
                    startActivity(gotoMainFishGamePage);
                    finish();
                }
                else {
                    Intent gotoAddFishPage = new Intent(getApplicationContext(), AddFishActivity.class);
                    startActivity(gotoAddFishPage);
                    finish();
                }
            }
        });
    }
}
