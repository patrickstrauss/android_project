package com.example.project_test6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        boolean test = false;

        if (test == true){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent homeIntent = new Intent(Splash.this, MainPage.class);
                    startActivity(homeIntent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent homeIntent = new Intent(Splash.this, Login.class);
                    startActivity(homeIntent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }



    }
}
