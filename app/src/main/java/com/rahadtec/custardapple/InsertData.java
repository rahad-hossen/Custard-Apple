package com.rahadtec.custardapple;

import android.os.Bundle;
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

public class InsertData extends AppCompatActivity {

    private EditText etUsername, etPassword, etSecretKey;
    private TextView tvShowCode;
    private Button btnInsert;

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

        tvShowCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String secret = etSecretKey.getText().toString();
                if (!TextUtils.isEmpty(secret)) {
                    tvShowCode.setText(secret);
                } else {
                    Toast.makeText(InsertData.this, "Please enter secret key", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDataToDatabase();
            }
        });


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