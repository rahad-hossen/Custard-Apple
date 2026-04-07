package com.rahadtec.custardapple;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class HomeScreen extends AppCompatActivity {


    MaterialCardView card_add_data;
    TextView usernameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);

        card_add_data = findViewById(R.id.card_add_data);
        usernameDisplay = findViewById(R.id.usernameDisplay);

        card_add_data.setOnClickListener(v -> {
            startActivity(new Intent(HomeScreen.this, InsertData.class));
        });

        PrefaranceManager manager = new PrefaranceManager(HomeScreen.this);
        String username = manager.getUsername();
        if (!username.isEmpty()){
            usernameDisplay.setText(username);
        }else {
            Toast.makeText(HomeScreen.this, "Please re-enter.",Toast.LENGTH_LONG).show();
            finishAffinity();
        }





    }
}