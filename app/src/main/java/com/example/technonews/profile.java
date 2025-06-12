package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.home_icon).setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, news.class);
            startActivity(intent);
        });

        findViewById(R.id.edit_profile_button).setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, editprofile.class);
            startActivity(intent);
        });

        findViewById(R.id.logout_icon).setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, signouta.class);
            startActivity(intent);
        });
    }
}
//