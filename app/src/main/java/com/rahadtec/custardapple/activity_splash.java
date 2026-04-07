package com.rahadtec.custardapple;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class activity_splash extends AppCompatActivity {

    private PrefaranceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        prefManager = new PrefaranceManager(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startNextActivity();
            }
        }, 2500);

    }

    private void startNextActivity() {
        if (prefManager.isLoggedIn()) {
            // যদি লগইন করা থাকে তবে সরাসরি হোম স্ক্রিনে যাবে
            Intent intent = new Intent(activity_splash.this, HomeScreen.class);
            startActivity(intent);
        } else {
            // লগইন করা না থাকলে লগইন স্ক্রিনে যাবে
//            // মনে রাখবেন, এখানে LoginScreen অথবা MainActivity (যেখানে লগইন আছে) সেটি দিবেন
            Intent intent = new Intent(activity_splash.this, MainActivity.class);
            startActivity(intent);
        }
        finish(); // স্প্ল্যাশ অ্যাক্টিভিটি ফিনিশ করে দেওয়া
    }
}
