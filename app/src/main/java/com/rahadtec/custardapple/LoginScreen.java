package com.rahadtec.custardapple;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    TextInputEditText usernameInput, passwordInput;
    Button loginBtn;
    ProgressBar pro_bar;

    // Volley singleton queue
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);

        View mainView = findViewById(android.R.id.content);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginBtn     = findViewById(R.id.loginBtn);
        pro_bar      = findViewById(R.id.pro_bar);

        // Singleton queue একবার তৈরি করো
        requestQueue = Volley.newRequestQueue(this);

        loginBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter Username & Password", Toast.LENGTH_SHORT).show();
                return;
            }

            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            if (androidId == null || androidId.isEmpty()) {
                Toast.makeText(this, "Sorry, Device ID পাওয়া যাচ্ছে না", Toast.LENGTH_SHORT).show();
                return;
            }

            pro_bar.setVisibility(VISIBLE);
            checkUserAvailable(username, password, androidId, false);
        });
    }

    public void checkUserAvailable(String username, String password, String deviceId, boolean forceLogin) {
        String url = "https://rhrahadtec.xyz/custard_apple/login_user.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String cleanResponse = response.trim();
                        pro_bar.setVisibility(GONE);

                        if (cleanResponse.equals("User Valid")) {
                            PrefaranceManager manager = new PrefaranceManager(LoginScreen.this);
                            manager.saveUserLogin(username);
                            Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginScreen.this, HomeScreen.class));
                            finish();

                        } else if (cleanResponse.equals("Wrong Username & Password")) {
                            Toast.makeText(LoginScreen.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();

                        } else if (cleanResponse.equals("Already logged in on another device")) {
                            // Dialog দেখাও
                            new AlertDialog.Builder(LoginScreen.this)
                                    .setTitle("অন্য Device এ Login আছে")
                                    .setMessage("এই device এ login করলে অন্য device টি logout হয়ে যাবে। চালিয়ে যাবেন?")
                                    .setPositiveButton("হ্যাঁ, Login করো", (dialog, which) -> {
                                        pro_bar.setVisibility(VISIBLE);
                                        checkUserAvailable(username, password, deviceId, true);
                                    })
                                    .setNegativeButton("বাতিল", null)
                                    .show();

                        } else {
                            Toast.makeText(LoginScreen.this, "Server: " + cleanResponse, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pro_bar.setVisibility(GONE);
                        Toast.makeText(LoginScreen.this, "Network error, আবার চেষ্টা করুন", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("password", password);
                map.put("key",      "Rahad#811439");
                map.put("device_id", deviceId);
                map.put("force_login", forceLogin ? "1" : "0");
                return map;
            }
        };

        requestQueue.add(stringRequest);
    }
}