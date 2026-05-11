package com.rahadtec.custardapple;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class InstraCookies extends AppCompatActivity {

    public EditText etUsername, etPassword, etCookies;
    public Button btnCopy, btnSave;
    public WebView myWebView;

    // ডাটাবেস হেল্পার ডিক্লেয়ার করা
    Cookies_DB_helper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instra_cookies);

        // ডাটাবেস হেল্পার ইনিশিয়ালাইজ করা
        dbHelper = new Cookies_DB_helper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etCookies = findViewById(R.id.etCookies);
        btnCopy = findViewById(R.id.btnCopy);
        btnSave = findViewById(R.id.SaveBtn);
        myWebView = findViewById(R.id.myWebView);

        // ওয়েবভিউ সেটআপ (লিঙ্ক লোড করার জন্য)
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl("https://www.instagram.com/accounts/login/");

        // ১. সেভ বাটনের কাজ
        btnSave.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String cookies = etCookies.getText().toString().trim();

            if (!user.isEmpty() && !pass.isEmpty() && !cookies.isEmpty()) {
                // ডাটাবেসে ইনসার্ট করা
                boolean isInserted = dbHelper.insertData(user, pass, cookies);

                if (isInserted) {
                    // সফলভাবে সেভ হলে টোটাল কয়টা ডাটা আছে তা বের করা
                    int total = dbHelper.getTotalCount();
                    Toast.makeText(this, "Saved! Total Items: " + total, Toast.LENGTH_SHORT).show();

                    // ইনপুট ফিল্ডগুলো খালি করে দেওয়া (ঐচ্ছিক)
                    etUsername.setText("");
                    etCookies.setText("");
                } else {
                    Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // ২. কপি বাটনের কাজ (ক্লিপবোর্ড লজিক)
        btnCopy.setOnClickListener(v -> {
            String cookieText = etCookies.getText().toString();
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (!username.isEmpty() && !password.isEmpty() && !cookieText.isEmpty()){

                String totalText = username+"|"+password+"|"+cookieText;
                if (!cookieText.isEmpty()) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("cookies", totalText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this, "Please enter every field", Toast.LENGTH_SHORT).show();
            }


        });
    }
}