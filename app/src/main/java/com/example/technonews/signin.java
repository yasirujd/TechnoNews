package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class signin extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private ImageButton togglePasswordButton;
    private TextView createAccountTextView;
    private TextView forgotPasswordTextView;
    private LinearLayout googleSignInLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        signInButton = findViewById(R.id.buttonSignIn);
        togglePasswordButton = findViewById(R.id.imageButtonTogglePassword);
        createAccountTextView = findViewById(R.id.textViewCreateAccount3);
        forgotPasswordTextView = findViewById(R.id.textViewForgotPassword);
        googleSignInLayout = findViewById(R.id.googleSignInCustomLayout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        togglePasswordButton.setOnClickListener(v -> {
            if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.visibility_off_icon_large_grey_outline_regular);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.visi_off);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        signInButton.setOnClickListener(v -> {
            signInUser();
        });

        createAccountTextView.setOnClickListener(v -> {
            Intent intent = new Intent(signin.this, signup.class);
            startActivity(intent);
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            String emailAddress = emailEditText.getText().toString().trim();

            if (TextUtils.isEmpty(emailAddress)) {
                Toast.makeText(getApplicationContext(), "Enter your email address to reset password.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(signin.this, "Password reset email sent. Check your email.", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email.";
                            Toast.makeText(signin.this, "Failed to send reset email: " + errorMessage, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Password reset failed: " + errorMessage, task.getException());
                        }
                    });
        });

        googleSignInLayout.setOnClickListener(v -> {
            signInWithGoogle();
        });
    }

    private void signInUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(signin.this, "Please enter both email and password.", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        Toast.makeText(signin.this, "Sign In Successful! Welcome.", Toast.LENGTH_LONG).show();
                        navigateToNewsActivity();
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown authentication error.";
                        Toast.makeText(signin.this, "Authentication failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Google sign in success, ID Token: " + account.getIdToken());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(signin.this, "Google Sign In failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Firebase Google sign in success");
                        Toast.makeText(signin.this, "Google Sign In Successful! Welcome.", Toast.LENGTH_LONG).show();
                        navigateToNewsActivity();
                    } else {
                        Log.w(TAG, "Firebase Google sign in failure", task.getException());
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown Google Firebase authentication error.";
                        Toast.makeText(signin.this, "Google Sign In failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToNewsActivity() {
        Intent intent = new Intent(signin.this, news.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
