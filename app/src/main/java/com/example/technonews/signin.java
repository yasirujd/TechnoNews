package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class signin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        Button signInButton = findViewById(R.id.buttonSignIn);
        ImageButton togglePasswordButton = findViewById(R.id.imageButtonTogglePassword);

        togglePasswordButton.setOnClickListener(v -> {
            if (passwordEditText.getInputType() == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                passwordEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.visi_off);  // Ensure icon exists in the drawable folder
            } else {
                passwordEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.visibility_off_icon_large_grey_outline_regular);  // Ensure icon exists in the drawable folder
            }
        });

        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                Intent intent = new Intent(signin.this, news.class);
                startActivity(intent);
            } else {
                Toast.makeText(signin.this, "Please fill both email and password", Toast.LENGTH_SHORT).show();
            }
        });

        TextView createAccountTextView = findViewById(R.id.textViewCreateAccount3);
        createAccountTextView.setOnClickListener(v -> {
            Intent intent = new Intent(signin.this, signup.class);
            startActivity(intent);
        });
    }
}
