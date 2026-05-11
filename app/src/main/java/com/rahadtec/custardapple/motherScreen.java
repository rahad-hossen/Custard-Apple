package com.rahadtec.custardapple;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class motherScreen extends AppCompatActivity {

    TextView usernameDisplay;
    LinearLayout TwoFA,InstraCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mother_screen);

        usernameDisplay= findViewById(R.id.usernameDisplay);
        TwoFA = findViewById(R.id.TwoFA);
        InstraCookie = findViewById(R.id.InstraCookie);

        PrefaranceManager manager = new PrefaranceManager(motherScreen.this);
        String username = manager.getUsername();
        if (!username.isEmpty()){
            usernameDisplay.setText(username);
        }else {
            Toast.makeText(motherScreen.this, "Please re-enter.",Toast.LENGTH_LONG).show();
            finishAffinity();
        }

        TwoFA.setOnClickListener(v -> {

            startActivity(new Intent(motherScreen.this, HomeScreen.class));

        });
        InstraCookie.setOnClickListener(v -> {
            startActivity(new Intent(motherScreen.this, Cookies_main.class));
        });

    }
}