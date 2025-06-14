package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText emailEditText, indexNoEditText, usernameEditText;
    private ImageView logoutIcon;

    private String currentIndexNo;
    private String currentUsername;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.email);
        indexNoEditText = findViewById(R.id.index_no);
        usernameEditText = findViewById(R.id.username);
        logoutIcon = findViewById(R.id.logout_icon);

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
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DocumentReference docRef = db.collection("users").document(userId);

                Toast.makeText(profile.this, "Loading profile for editing...", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Attempting to fetch profile data for editprofile.");

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String fetchedIndexNo = document.getString("indexNo");
                                String fetchedUsername = document.getString("username");
                                String fetchedEmail = currentUser.getEmail();

                                Log.d(TAG, "Fetched data for editprofile: IndexNo=" + fetchedIndexNo + ", Username=" + fetchedUsername + ", Email=" + fetchedEmail);

                                Intent intent = new Intent(profile.this, editprofile.class);
                                intent.putExtra("indexNo", fetchedIndexNo);
                                intent.putExtra("username", fetchedUsername);
                                intent.putExtra("email", fetchedEmail);
                                startActivity(intent);
                            } else {
                                Log.d(TAG, "No such document found for user: " + userId + " when preparing for editprofile.");
                                Toast.makeText(profile.this, "User data not found for editing. Please ensure your profile is complete.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.e(TAG, "Failed to load profile for editing: " + task.getException().getMessage(), task.getException());
                            Toast.makeText(profile.this, "Failed to load profile for editing.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "No user logged in to edit profile.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Edit profile button clicked but no user is logged in.");
            }
        });

        logoutIcon.setOnClickListener(v -> {
            dialog_sign_out_confirm signOutDialog = new dialog_sign_out_confirm();
            signOutDialog.show(getSupportFragmentManager(), "sign_out_dialog_tag");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in. Redirecting to Sign In.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(profile.this, signin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // This loads data for the current profile page display
            loadUserProfile(currentUser);
        }
    }

    private void loadUserProfile(FirebaseUser currentUser) {
        currentEmail = currentUser.getEmail();
        emailEditText.setText(currentEmail);
        Log.d(TAG, "Displayed email on profile page: " + currentEmail);


        String userId = currentUser.getUid();
        DocumentReference docRef = db.collection("users").document(userId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        currentIndexNo = document.getString("indexNo");
                        currentUsername = document.getString("username");

                        indexNoEditText.setText(currentIndexNo);
                        usernameEditText.setText(currentUsername);
                        Log.d(TAG, "Displayed Firestore data on profile page: IndexNo=" + currentIndexNo + ", Username=" + currentUsername);

                    } else {
                        Log.d(TAG, "No such document in Firestore for user: " + userId);
                        Toast.makeText(profile.this, "User data not found in Firestore.", Toast.LENGTH_SHORT).show();
                        currentIndexNo = "";
                        currentUsername = "";
                        indexNoEditText.setText(""); // Clear if no data
                        usernameEditText.setText(""); // Clear if no data
                    }
                } else {
                    Log.e(TAG, "Failed to load profile data from Firestore for display: " + task.getException().getMessage(), task.getException());
                    Toast.makeText(profile.this, "Failed to load profile for display.", Toast.LENGTH_SHORT).show();
                    currentIndexNo = "";
                    currentUsername = "";
                    indexNoEditText.setText(""); // Clear on failure
                    usernameEditText.setText(""); // Clear on failure
                }
            }
        });
    }
}
