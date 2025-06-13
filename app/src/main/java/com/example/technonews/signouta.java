package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signouta extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signouta);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find the email EditText to display the current user's email
        EditText emailEditText = findViewById(R.id.email_edit_text);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            emailEditText.setText(currentUser.getEmail());
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cancel button takes user back to profile screen
        findViewById(R.id.cancel_button).setOnClickListener(v -> {
            // Simply finish this activity to go back to the previous one (profile)
            finish();
        });

        // OK button performs the sign out and redirects to the signin screen
        findViewById(R.id.ok_button).setOnClickListener(v -> {
            // 1. Sign the user out from Firebase
            mAuth.signOut();
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();

            // 2. Create an intent to go to the signin screen
            Intent intent = new Intent(signouta.this, signin.class);

            // 3. Add flags to clear all previous activities from the back stack
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // 4. Start the signin activity and finish this one
            startActivity(intent);
            finish();
        });
    }
}