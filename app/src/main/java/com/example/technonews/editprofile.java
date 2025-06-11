package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class editprofile extends AppCompatActivity {

    private EditText username, email, password;
    private ImageView usernameIcon, emailIcon, passwordIcon;
    private Button okButton, cancelButton;
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editprofile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);
        usernameIcon = findViewById(R.id.username_icon);
        emailIcon = findViewById(R.id.email_icon);
        passwordIcon = findViewById(R.id.password_icon);

        findViewById(R.id.editpen_icon).setOnClickListener(v -> {
            enableEditing();
        });

        okButton.setOnClickListener(v -> {
            if (isEditing) {
                // Save changes logic here
                Intent intent = new Intent(editprofile.this, profile.class);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(v -> {
            if (isEditing) {
                Intent intent = new Intent(editprofile.this, profile.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(editprofile.this, profile.class);
                startActivity(intent);
            }
        });
    }

    private void enableEditing() {
        username.setEnabled(true);
        email.setEnabled(true);
        password.setEnabled(true);
        usernameIcon.setVisibility(View.VISIBLE);
        emailIcon.setVisibility(View.VISIBLE);
        passwordIcon.setVisibility(View.VISIBLE);
        isEditing = true;
    }
}
