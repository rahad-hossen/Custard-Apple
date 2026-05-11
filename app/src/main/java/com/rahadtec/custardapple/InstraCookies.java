package com.rahadtec.custardapple;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class InstraCookies extends AppCompatActivity {

    public EditText etUsername, etPassword, etCookies;
    public Button btnCopy, btnSave, btnViewCookies;
    public WebView myWebView;

    // ডাটাবেস হেল্পার ডিক্লেয়ার করা
    Cookies_DB_helper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instra_cookies);

        // ডাটাবেস হেল্পার ইনিশিয়ালাইজ করা
        dbHelper = new Cookies_DB_helper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etCookies = findViewById(R.id.etCookies);
        btnCopy = findViewById(R.id.btnCopy);
        btnSave = findViewById(R.id.SaveBtn);
        btnViewCookies = findViewById(R.id.btnViewCookies);
        myWebView = findViewById(R.id.myWebView);

        // ওয়েবভিউ সেটআপ
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.loadUrl("https://www.instagram.com/accounts/login/");

        // ১. View Cookies বাটনের কাজ (অটো কুকি এক্সট্র্যাক্ট)
        btnViewCookies.setOnClickListener(v -> {
            String url = myWebView.getUrl();
            CookieManager cookieManager = CookieManager.getInstance();
            String allCookies = cookieManager.getCookie(url);

            if (allCookies != null && allCookies.contains("sessionid")) {
                etCookies.setText(allCookies);
                Toast.makeText(this, "Cookies Successfully Extracted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please Login into Instagram inside Web Preview first!", Toast.LENGTH_LONG).show();
            }
        });


        btnSave.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String cookies = etCookies.getText().toString().trim();

            if (!user.isEmpty() && !pass.isEmpty() && !cookies.isEmpty()) {
                boolean isInserted = dbHelper.insertData(user, pass, cookies);

                if (isInserted) {
                    int total = dbHelper.getTotalCount();
                    Toast.makeText(this, "Saved! Total Items: " + total, Toast.LENGTH_SHORT).show();

                    // --- ফেসবুক সেশন ক্লিয়ার করার লজিক (যাতে অ্যাকাউন্ট ভুলে যায়) ---
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookies(null); // সব কুকি মুছে ফেলবে
                    cookieManager.flush();

                    myWebView.clearCache(true);
                    myWebView.clearHistory();

                    // নতুন করে ফেসবুক লগইন পেজ লোড করা
                    myWebView.loadUrl("https://m.facebook.com/login/");

                    // ফিল্ডগুলো খালি করা
                    etUsername.setText("");
                    etPassword.setText("");
                    etCookies.setText("");
                } else {
                    Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill all fields and extract cookies", Toast.LENGTH_SHORT).show();
            }
        });

        // ২. সেভ বাটনের কাজ
//        btnSave.setOnClickListener(v -> {
//            String user = etUsername.getText().toString().trim();
//            String pass = etPassword.getText().toString().trim();
//            String cookies = etCookies.getText().toString().trim();
//
//            if (!user.isEmpty() && !pass.isEmpty() && !cookies.isEmpty()) {
//                boolean isInserted = dbHelper.insertData(user, pass, cookies);
//
//                if (isInserted) {
//                    int total = dbHelper.getTotalCount();
//                    Toast.makeText(this, "Saved! Total Items: " + total, Toast.LENGTH_SHORT).show();
//
//                    // ফিল্ডগুলো খালি করা
//                    etUsername.setText("");
//                    etCookies.setText("");
//                } else {
//                    Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(this, "Please fill all fields and extract cookies", Toast.LENGTH_SHORT).show();
//            }
//        });

        // ৩. কপি বাটনের কাজ (username|password|cookies ফরম্যাটে)
        btnCopy.setOnClickListener(v -> {
            String cookieText = etCookies.getText().toString();
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (!username.isEmpty() && !password.isEmpty() && !cookieText.isEmpty()){
                String totalText = username + "|" + password + "|" + cookieText;
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("cookies", totalText);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please ensure all fields are filled", Toast.LENGTH_SHORT).show();
            }
        });
    }
}