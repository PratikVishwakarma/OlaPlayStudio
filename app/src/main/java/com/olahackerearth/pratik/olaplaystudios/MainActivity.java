package com.olahackerearth.pratik.olaplaystudios;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.olahackerearth.pratik.olaplaystudios.ui.HomeActivity;


public class MainActivity extends AppCompatActivity {

    public static final long SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

}
