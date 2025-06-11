package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class news extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView avatarIcon = findViewById(R.id.avatar_icon);
        avatarIcon.setOnClickListener(v -> {
            Intent intent = new Intent(news.this, profile.class);
            startActivity(intent);
        });

        ImageView developerIcon = findViewById(R.id.developer_icon);
        developerIcon.setOnClickListener(v -> {
            Intent intent = new Intent(news.this, devinfo.class);
            startActivity(intent);
        });
    }
}
