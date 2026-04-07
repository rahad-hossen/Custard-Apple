package com.rahadtec.custardapple;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

public class InsertData extends AppCompatActivity {

    private EditText etUsername, etPassword, etSecretKey;
    private TextView tvShowCode;
    private Button btnInsert;
    TextView tv_display_code,timeINsecend;


    private Handler handler = new Handler();
    private Runnable runnable;
    private TimeProvider timeProvider = new SystemTimeProvider();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insert_data);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etSecretKey = findViewById(R.id.et_secret_key);
        tvShowCode = findViewById(R.id.tv_show_code);
        btnInsert = findViewById(R.id.btn_insert);
        tv_display_code = findViewById(R.id.tv_display_code);
        timeINsecend = findViewById(R.id.timeINsecend);




//        tvShowCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String secret = etSecretKey.getText().toString();
//                if (!TextUtils.isEmpty(secret)) {
//                    String otp = generateOTP(secret);
//                    if (otp != null || !otp.isEmpty()){
//                        tv_display_code.setText(otp);
//                    }else {
//                        tv_display_code.setText("");
//                    }
//
//                } else {
//                    Toast.makeText(InsertData.this, "Please enter secret key", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        tvShowCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOTPCycle();
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDataToDatabase();
            }
        });


    }

//    public String generateOTP(String secretKey) {
//        try {
//            TimeProvider timeProvider = new SystemTimeProvider();
//            CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
//
//            // বর্তমান সময়ের ওপর ভিত্তি করে কোড তৈরি (প্রতি ৩০ সেকেন্ডে পরিবর্তন হবে)
//            long counter = Math.floorDiv(timeProvider.getTime(), 30);
//
//            return codeGenerator.generate(secretKey, counter);
//        } catch (CodeGenerationException e) {
//            e.printStackTrace();
//            return "Error";
//        }
//    }

    private void startOTPCycle() {
        String secret = etSecretKey.getText().toString().trim();

        if (TextUtils.isEmpty(secret)) {
            Toast.makeText(this, "Please enter secret key", Toast.LENGTH_SHORT).show();
            return;
        }

        // আগের কোনো টাইমার চললে সেটা বন্ধ করা
        if (runnable != null) handler.removeCallbacks(runnable);

        runnable = new Runnable() {
            @Override
            public void run() {
                long currentTimeSeconds = timeProvider.getTime();
                // ৩০ সেকেন্ডের চক্রে কত সেকেন্ড বাকি আছে বের করা
                int secondsRemaining = 30 - (int) (currentTimeSeconds % 30);

                timeINsecend.setText(secondsRemaining + "s remaining");

                // যখনই নতুন চক্র শুরু হবে (যেমন ৩০ সেকেন্ডে), কোড আপডেট হবে
                String otp = generateOTP(secret);
                tv_display_code.setText(otp);

                // প্রতি ১ সেকেন্ড পর পর নিজেকে কল করবে
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);
    }

    public String generateOTP(String secretKey) {
        try {
            CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
            long counter = Math.floorDiv(timeProvider.getTime(), 30);
            return codeGenerator.generate(secretKey, counter);
        } catch (Exception e) {
            return "Error";
        }
    }

    // অ্যাক্টিভিটি বন্ধ হলে টাইমার বন্ধ করা (মেমোরি লিক রোধে)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void insertDataToDatabase() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String secretKey = etSecretKey.getText().toString().trim();

        // ইনপুট ভ্যালিডেশন
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(secretKey)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // এখানে আপনার ডেটাবেস (Firebase/SQLite) ইনসার্ট লজিক লিখবেন
        String successMessage = "ডেটা সেভ হয়েছে: " + username;
        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

        // ইনপুট ফিল্ড খালি করে দেওয়া
        etUsername.setText("");
        etPassword.setText("");
        etSecretKey.setText("");
    }
}