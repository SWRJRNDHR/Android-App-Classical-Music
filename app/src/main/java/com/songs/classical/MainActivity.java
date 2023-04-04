package com.songs.classical;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends ActionBarActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);



        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                Intent homeIntent = new Intent(MainActivity.this, FirstPage.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);





    }





}