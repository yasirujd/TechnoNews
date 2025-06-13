package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log class
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signin extends AppCompatActivity {

    private static final String TAG = "SignInActivity"; // Tag for Logcat

    // Firebase instance
    private FirebaseAuth mAuth;

    // UI Elements
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private ImageButton togglePasswordButton;
    private TextView createAccountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI Elements by matching their IDs from activity_signin.xml
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        signInButton = findViewById(R.id.buttonSignIn);
        togglePasswordButton = findViewById(R.id.imageButtonTogglePassword);
        createAccountTextView = findViewById(R.id.textViewCreateAccount3); // "Create now" text view

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Toggle password visibility
        togglePasswordButton.setOnClickListener(v -> {
            // Check if password is currently visible
            if (passwordEditText.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                // If visible, hide it
                passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.visibility_off_icon_large_grey_outline_regular); // Change to 'visibility off' icon
            } else {
                // If hidden, make it visible
                passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.visi_off); // Change to 'visibility on' icon
            }
            // Move cursor to the end of the text
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Sign In button click listener
        signInButton.setOnClickListener(v -> {
            Log.d(TAG, "Sign In button clicked. Initiating sign-in process.");
            signInUser(); // Call the method to handle user sign-in
        });

        // "Create account" text view click listener to navigate to signup page
        createAccountTextView.setOnClickListener(v -> {
            Log.d(TAG, "Create Account text clicked. Navigating to Sign Up.");
            Intent intent = new Intent(signin.this, signup.class);
            startActivity(intent);
        });
    }

    private void signInUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Input validation for empty fields
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(signin.this, "Please enter both email and password.", Toast.LENGTH_LONG).show();
            Log.w(TAG, "Sign-in validation failed: Empty email or password.");
            return; // Stop the sign-in process if fields are empty
        }

        Log.d(TAG, "Input validation passed. Attempting Firebase sign-in for email: " + email);

        // Firebase authentication call
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success!
                            Log.d(TAG, "signInWithEmail:success. User UID: " + mAuth.getCurrentUser().getUid());
                            Toast.makeText(signin.this, "Sign In Successful! Welcome.", Toast.LENGTH_LONG).show();

                            // Navigate to News page upon successful sign-in
                            Intent intent = new Intent(signin.this, news.class);
                            // Clear the activity stack so the user cannot go back to sign-in/sign-up screens
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish(); // Finish current activity to remove it from back stack
                        } else {
                            // Sign in failed. Display an informative message.
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown authentication error.";
                            Log.w(TAG, "signInWithEmail:failure - " + errorMessage, task.getException());
                            Toast.makeText(signin.this, "Authentication failed: " + errorMessage + ". Please check your credentials.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
