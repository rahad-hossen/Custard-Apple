package com.rahadtec.custardapple;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

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

        // ২.৫ সেকেন্ড স্প্ল্যাশ স্ক্রিন দেখানোর পর রান হবে
        new Handler().postDelayed(() -> {
            if (prefManager.isLoggedIn()) {
                // ইউজার লগইন থাকলে সার্ভারে স্ট্যাটাস চেক করবে
                checkStatusFromServer();
            } else {
                // লগইন না থাকলে সরাসরি সাইন-ইন পেজে
                goToLogin();
            }
        }, 2500);
    }

    private void checkStatusFromServer() {
        String url = "https://rhrahadtec.xyz/custard_apple/check_subscription.php"; // আপনার লিঙ্কটি এখানে দিন
        String username = prefManager.getUsername();
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    String cleanResponse = response.trim();

                    if (cleanResponse.equals("Valid")) {
                        // সবকিছু ঠিক থাকলে হোম স্ক্রিনে যাবে
                        startActivity(new Intent(activity_splash.this, HomeScreen.class));
                        finish();
                    } else if (cleanResponse.equals("Expired")) {
                        showErrorDialog("Your Subscription Ended.");
                    } else if (cleanResponse.equals("Device Mismatch")) {
                        showErrorDialog("Account login in another device");
                    } else {
                        // যদি ইউজার খুঁজে না পাওয়া যায় বা অন্য কোনো সমস্যা হয়
                        goToLogin();
                    }
                },
                error -> {
                    // ইন্টারনেট না থাকলে বা সার্ভার এরর হলে
                    Toast.makeText(this, "Internet Connection faild or Something worng.", Toast.LENGTH_SHORT).show();
                    // আপনি চাইলে এখানে finish() দিতে পারেন যাতে অ্যাপ বন্ধ হয়ে যায়
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("device_id", deviceId);
                params.put("key", "Rahad#811439"); // আপনার সার্ভার সিক্রেট কি
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("সতর্কবার্তা!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("লগইন পেজে যান", (dialog, which) -> {
                    prefManager.logoutUser(); // লোকাল ডেটা ক্লিয়ার করবে
                    goToLogin();
                })
                .show();
    }

    private void goToLogin() {
        Intent intent = new Intent(activity_splash.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}