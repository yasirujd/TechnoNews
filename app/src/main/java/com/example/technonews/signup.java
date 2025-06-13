package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log class
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    private static final String TAG = "SignUpActivity"; // Tag for Logcat

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // UI Elements
    private EditText indexNo, username, email, password, confirmPassword;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI Elements
        indexNo = findViewById(R.id.index_no);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        signUpButton = findViewById(R.id.sign_up_button);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the registration method
                Log.d(TAG, "Sign Up button clicked. Initiating registration.");
                registerUser();
            }
        });

        // Back Button to Sign In Page
        findViewById(R.id.back_button_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Sign In page when the back button is clicked
                Log.d(TAG, "Back button clicked. Navigating to Sign In.");
                Intent intent = new Intent(signup.this, signin.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        // Get text from EditTexts and trim whitespace
        final String indexNoStr = indexNo.getText().toString().trim();
        final String usernameStr = username.getText().toString().trim();
        final String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        String confirmPasswordStr = confirmPassword.getText().toString().trim();

        // --- Input Validation ---
        if (indexNoStr.isEmpty() || usernameStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty() || confirmPasswordStr.isEmpty()) {
            Toast.makeText(signup.this, "Please fill out all required fields.", Toast.LENGTH_LONG).show(); // Increased duration
            Log.w(TAG, "Input validation failed: Empty fields detected.");
            return;
        }

        if (!passwordStr.equals(confirmPasswordStr)) {
            Toast.makeText(signup.this, "Passwords do not match.", Toast.LENGTH_LONG).show(); // Increased duration
            confirmPassword.setError("Passwords do not match");
            confirmPassword.requestFocus();
            Log.w(TAG, "Input validation failed: Passwords mismatch.");
            return;
        }

        if (passwordStr.length() < 6) {
            Toast.makeText(signup.this, "Password must be at least 6 characters.", Toast.LENGTH_LONG).show(); // Increased duration
            password.setError("Password too weak");
            password.requestFocus();
            Log.w(TAG, "Input validation failed: Password too short.");
            return;
        }

        Log.d(TAG, "All input validations passed. Attempting Firebase user creation for email: " + emailStr);

        // --- Firebase User Creation ---
        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, now save user data to Firestore
                            Log.d(TAG, "Firebase user created successfully. User UID: " + mAuth.getCurrentUser().getUid());
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                Log.d(TAG, "User ID obtained. Proceeding to save user data to Firestore.");
                                saveUserDataToFirestore(userId, indexNoStr, usernameStr, emailStr);
                            } else {
                                Log.e(TAG, "FirebaseUser is null immediately after successful creation. This is unexpected.");
                                Toast.makeText(signup.this, "User creation successful but failed to get user data.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign up fails, display a message to the user.
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown authentication error.";
                            Toast.makeText(signup.this, "Authentication failed: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Firebase authentication failed: " + errorMessage, task.getException());
                        }
                    }
                });
    }

    private void saveUserDataToFirestore(String userId, String indexNo, String username, String email) {
        // Create a new user with a map
        Map<String, Object> user = new HashMap<>();
        user.put("indexNo", indexNo);
        user.put("username", username);
        user.put("email", email);

        Log.d(TAG, "Attempting to save user data to Firestore for userId: " + userId + " with data: " + user.toString());

        // Add a new document with the user's UID as the document ID
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(signup.this, "Registration Successful! Redirecting...", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "User data successfully saved to Firestore. Navigating to News page.");

                        // Proceed to the news page after successful registration
                        Intent intent = new Intent(signup.this, news.class);
                        // Clear the activity stack to prevent going back to sign-up/sign-in
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Finish the current SignUpActivity to remove it from the back stack
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error of saving data
                        Toast.makeText(signup.this, "Error saving user data: " + e.getMessage() + ". Please check Firebase Rules.",
                                Toast.LENGTH_LONG).show(); // Increased duration and added hint
                        Log.e(TAG, "Error saving user data to Firestore: " + e.getMessage(), e);
                    }
                });
    }
}
