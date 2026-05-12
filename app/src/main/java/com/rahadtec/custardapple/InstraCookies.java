package com.rahadtec.custardapple;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

public class InstraCookies extends AppCompatActivity {

    public EditText etUsername, etPassword, etCookies,TwoFA;
    public Button btnCopy, btnSave, btnViewCookies;
    public WebView myWebView, myWebView2;
    AppCompatButton getCode;

    // ডাটাবেস হেল্পার ডিক্লেয়ার করা
    Cookies_DB_helper dbHelper;
    LinearLayout parentVisible;


    private Handler handler = new Handler();
    private Runnable runnable;
    ImageView drop_down,drop_up;
    private TimeProvider timeProvider = new SystemTimeProvider();

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
        myWebView2 = findViewById(R.id.myWebView2);
        getCode = findViewById(R.id.getCode);
        TwoFA = findViewById(R.id.TwoFA);
        drop_down = findViewById(R.id.drop_down);
        drop_up = findViewById(R.id.drop_up);
        parentVisible = findViewById(R.id.parentVisible);

        // ওয়েবভিউ সেটআপ
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.loadUrl("https://www.instagram.com/accounts/login/");


        myWebView2.setWebViewClient(new WebViewClient());
        myWebView2.getSettings().setJavaScriptEnabled(true);
        myWebView2.getSettings().setDomStorageEnabled(true);
        myWebView2.loadUrl("https://submitwork.org");

//        drop_up.setOnClickListener(v -> {
//            if(parentVisible.getVisibility() == VISIBLE){
//                drop_up.setVisibility(VISIBLE);
//                drop_down.setVisibility(GONE);
//                parentVisible.setVisibility(GONE);
//            }else {
//                drop_up.setVisibility(GONE);
//                drop_down.setVisibility(VISIBLE);
//                parentVisible.setVisibility(GONE);
//            }
//        });
//
//        drop_down.setOnClickListener(v -> {
//            if(parentVisible.getVisibility() == VISIBLE){
//                drop_up.setVisibility(VISIBLE);
//                drop_down.setVisibility(GONE);
//                parentVisible.setVisibility(GONE);
//            }else {
//                drop_up.setVisibility(GONE);
//                drop_down.setVisibility(VISIBLE);
//                parentVisible.setVisibility(GONE);
//            }
//        });

        drop_down.setOnClickListener(v -> {
            // মেনু যখন বন্ধ, তখন ক্লিক করলে খুলবে
            parentVisible.setVisibility(VISIBLE);
            drop_down.setVisibility(GONE); // নিচের দিকে মুখ করা তীর লুকিয়ে যাবে
            drop_up.setVisibility(VISIBLE); // উপরের দিকে মুখ করা তীর দেখা যাবে
        });

        drop_up.setOnClickListener(v -> {
            // মেনু যখন খোলা, তখন ক্লিক করলে বন্ধ হবে
            parentVisible.setVisibility(GONE);
            drop_up.setVisibility(GONE);   // উপরের তীর লুকিয়ে যাবে
            drop_down.setVisibility(VISIBLE); // নিচের তীর আবার দেখা যাবে
        });


        getCode.setOnClickListener(v -> {
            startOTPCycle();

        });

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
//                    // --- ফেসবুক সেশন ক্লিয়ার করার লজিক (যাতে অ্যাকাউন্ট ভুলে যায়) ---
//                    CookieManager cookieManager = CookieManager.getInstance();
//                    cookieManager.removeAllCookies(null); // সব কুকি মুছে ফেলবে
//                    cookieManager.flush();
//
//                    myWebView.clearCache(true);
//                    myWebView.clearHistory();
//
//                    // নতুন করে ফেসবুক লগইন পেজ লোড করা
//                    myWebView.loadUrl("https://www.instagram.com/accounts/login/");
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

        btnSave.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String cookies = etCookies.getText().toString().trim();

            if (!user.isEmpty() && !pass.isEmpty() && !cookies.isEmpty()) {
                boolean isInserted = dbHelper.insertData(user, pass, cookies);

                if (isInserted) {
                    int total = dbHelper.getTotalCount();
                    Toast.makeText(this, "Saved! Total: " + total, Toast.LENGTH_SHORT).show();

                    // সেশন ক্লিয়ার করার জন্য callback ব্যবহার করা ভালো
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookies(value -> {
                        myWebView.setVisibility(VISIBLE);
                        myWebView2.setVisibility(View.GONE);
                        // কুকি পুরোপুরি ডিলিট হওয়ার পর নতুন করে পেজ লোড হবে
                        myWebView.clearCache(true);
                        myWebView.clearHistory();
                        myWebView.loadUrl("https://www.instagram.com/accounts/login/");
                    });
                    cookieManager.flush();

                    // ফিল্ডগুলো খালি করা
                    etUsername.setText("");// পাসওয়ার্ডও খালি করা উচিত
                    etCookies.setText("");
                } else {
                    Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "সবগুলো ঘর পূরণ করুন!", Toast.LENGTH_SHORT).show();
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

                myWebView.setVisibility(GONE);
                myWebView2.setVisibility(VISIBLE);


            } else {
                Toast.makeText(this, "Please ensure all fields are filled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startOTPCycle() {
        String secret = TwoFA.getText().toString().trim();

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

//                timeINsecend.setText(secondsRemaining + "s remaining");

                // যখনই নতুন চক্র শুরু হবে (যেমন ৩০ সেকেন্ডে), কোড আপডেট হবে
                String otp = generateOTP(secret);
//                tv_display_code.setText(otp);

                String textToCopy = otp;

                if (!textToCopy.isEmpty()) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Code", textToCopy);
                    clipboard.setPrimaryClip(clip);

                    // ইউজারকে জানানোর জন্য একটি ছোট মেসেজ (Toast)
                    Toast.makeText(InstraCookies.this, "Code copied!", Toast.LENGTH_SHORT).show();
                }



                // প্রতি ১ সেকেন্ড পর পর নিজেকে কল করবে
//                handler.postDelayed(this, 1000);
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
}