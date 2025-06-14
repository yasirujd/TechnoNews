package com.example.technonews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class editprofile extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private EditText indexNoEditText, usernameEditText, emailEditText, passwordEditText;
    private ImageView indexNoIcon, usernameIcon, emailIcon, passwordIcon;
    private Button okButton, cancelButton;
    private ImageView editPenIcon; // Main profile picture edit icon

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "No user logged in. Redirecting to Sign In.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(editprofile.this, signin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        indexNoEditText = findViewById(R.id.index_no);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        indexNoIcon = findViewById(R.id.index_no_icon);
        usernameIcon = findViewById(R.id.username_icon);
        emailIcon = findViewById(R.id.email_icon);
        passwordIcon = findViewById(R.id.password_icon);

        okButton = findViewById(R.id.ok_button);
        cancelButton = findViewById(R.id.cancel_button);
        editPenIcon = findViewById(R.id.editpen_icon); // Main profile picture edit icon

        loadUserDataFromIntent(); // Load initial data

        // Set EditTexts to non-editable but displayable
        setFieldInitialState(indexNoEditText);
        setFieldInitialState(usernameEditText);
        setFieldInitialState(emailEditText);
        setFieldInitialState(passwordEditText); // Password needs special input type for initial display

        // Set up individual pencil icon click listeners for granular editing
        indexNoIcon.setOnClickListener(v -> enableFieldEditing(indexNoEditText, false));
        usernameIcon.setOnClickListener(v -> enableFieldEditing(usernameEditText, false));
        emailIcon.setOnClickListener(v -> enableFieldEditing(emailEditText, false));
        passwordIcon.setOnClickListener(v -> enableFieldEditing(passwordEditText, true)); // Special handling for password

        // The main editPenIcon (next to profile picture) is now solely for profile picture related actions,
        // it no longer enables all text fields for editing. Its click listener can be implemented later
        // for profile picture changes if needed. For now, it does nothing or logs a message.
        editPenIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Profile picture editing not yet implemented.", Toast.LENGTH_SHORT).show();
        });

        okButton.setOnClickListener(v -> saveProfileChanges());

        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadUserDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String indexNo = intent.getStringExtra("indexNo");
            String username = intent.getStringExtra("username");
            String email = intent.getStringExtra("email");

            indexNoEditText.setText(indexNo);
            usernameEditText.setText(username);
            emailEditText.setText(email);
            passwordEditText.setText("******"); // Display placeholder for password
        }
    }

    private void setFieldInitialState(EditText editText) {
        editText.setEnabled(false); // Make it non-editable
        editText.setFocusable(false); // Remove focus capability
        editText.setFocusableInTouchMode(false); // Remove focus in touch mode
    }

    private void enableFieldEditing(EditText editText, boolean isPasswordField) {
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        if (isPasswordField) {
            editText.setText(""); // Clear password field for new input
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_NORMAL);
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void saveProfileChanges() {
        final String newIndexNo = indexNoEditText.getText().toString().trim();
        final String newUsername = usernameEditText.getText().toString().trim();
        final String newEmail = emailEditText.getText().toString().trim();
        final String newPassword = passwordEditText.getText().toString().trim();

        if (newIndexNo.isEmpty() || newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Index No, Username, and Email cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put("indexNo", newIndexNo);
        updates.put("username", newUsername);

        userDocRef.update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firestore user data updated successfully.");
                        updateEmailAndPassword(newEmail, newPassword);
                    } else {
                        Log.e(TAG, "Error updating Firestore user data: ", task.getException());
                        Toast.makeText(editprofile.this, "Failed to update profile data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmailAndPassword(String newEmail, String newPassword) {
        boolean emailUpdateNeeded = !newEmail.equals(currentUser.getEmail());
        boolean passwordUpdateNeeded = !newPassword.isEmpty() && !newPassword.equals("******");

        if (emailUpdateNeeded) {
            currentUser.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                            Toast.makeText(editprofile.this, "Email updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error updating email: ", task.getException());
                            Toast.makeText(editprofile.this, "Failed to update email. Please re-login and try again.", Toast.LENGTH_LONG).show();
                        }
                        if (!passwordUpdateNeeded) {
                            finalizeSave();
                        }
                    });
        }

        if (passwordUpdateNeeded) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                            Toast.makeText(editprofile.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error updating password: ", task.getException());
                            Toast.makeText(editprofile.this, "Failed to update password. Please re-login and try again.", Toast.LENGTH_LONG).show();
                        }
                        if (!emailUpdateNeeded) {
                            finalizeSave();
                        }
                    });
        }

        if (!emailUpdateNeeded && !passwordUpdateNeeded) {
            finalizeSave();
        }
    }

    private void finalizeSave() {
        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
