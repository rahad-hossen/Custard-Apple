package com.rahadtec.custardapple;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class fb_home extends AppCompatActivity {

    public EditText etUsername, etPassword, etCookies;
    public Button  btnSave, btnViewCookies;
    public WebView myWebView;
    ImageView backBtn;
    // ডাটাবেস হেল্পার
    fb_Cookies dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fb_home);

        // ডাটাবেস ইনিশিয়ালাইজেশন
        dbHelper = new fb_Cookies(this);

        // আইডিগুলো কানেক্ট করা
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etCookies = findViewById(R.id.etCookies);
        btnSave = findViewById(R.id.SaveBtn);
        btnViewCookies = findViewById(R.id.btnViewCookies);
        myWebView = findViewById(R.id.myWebView);
        backBtn = findViewById(R.id.backBtn);




        backBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        // --- ওয়েবভিউ সেটিংস শুরু ---
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // ফেসবুকের জন্য একটি মোবাইল ইউজার এজেন্ট সেট করা ভালো
        String mobileUA = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Mobile Safari/537.36";
        webSettings.setUserAgentString(mobileUA);

        myWebView.setWebViewClient(new WebViewClient());

        // ফেসবুক মোবাইল লগইন পেজ লোড করা
        myWebView.loadUrl("https://m.facebook.com/login/");
        // --- ওয়েবভিউ সেটিংস শেষ ---

        // ১. View Cookies বাটনের কাজ (ফেসবুক কুকি এক্সট্র্যাক্ট)
        btnViewCookies.setOnClickListener(v -> {
            String url = myWebView.getUrl();
            CookieManager cookieManager = CookieManager.getInstance();
            String allCookies = cookieManager.getCookie(url);

            // ফেসবুকের জন্য "c_user" থাকা মানে সাকসেসফুল লগইন
            if (allCookies != null && allCookies.contains("c_user")) {
                etCookies.setText(allCookies);
                Toast.makeText(this, "Facebook Cookies Extracted! ✅", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please Login to Facebook first!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this, "Saved! Total: " + total, Toast.LENGTH_SHORT).show();

                    // ১. কুকি এবং সেশন ক্লিয়ার করা (মূল কাজ এখানে)
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookies(null);
                    cookieManager.flush();

                    myWebView.clearCache(true);
                    myWebView.clearHistory();

                    // ২. ইনপুট ফিল্ড খালি করা
                    etUsername.setText("");
                    etCookies.setText("");

                    // ৩. ফেসবুক লগইন পেজ রিলোড করা (যাতে নতুন আইডি লগইন করা যায়)
                    myWebView.loadUrl("https://m.facebook.com/login/");

                } else {
                    Toast.makeText(this, "Failed to save data!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(this, "Saved! Total: " + total, Toast.LENGTH_SHORT).show();
//
//                    // ইনপুট ফিল্ড খালি করা (পাসওয়ার্ড রেখে ইউজার আর কুকি ক্লিয়ার করা)
//                    etUsername.setText("");
//                    etCookies.setText("");
//                } else {
//                    Toast.makeText(this, "Failed to save data!", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
//            }
//        });

        // ৩. কপি বাটনের কাজ (Format: username|password|cookies)
//        btnCopy.setOnClickListener(v -> {
//            String username = etUsername.getText().toString().trim();
//            String password = etPassword.getText().toString().trim();
//            String cookieText = etCookies.getText().toString().trim();
//
//            if (!username.isEmpty() && !password.isEmpty() && !cookieText.isEmpty()){
//                String finalData = username + "|" + password + "|" + cookieText;
//
//                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                ClipData clip = ClipData.newPlainText("fb_data", finalData);
//                clipboard.setPrimaryClip(clip);
//
//                Toast.makeText(this, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Fields are empty!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}