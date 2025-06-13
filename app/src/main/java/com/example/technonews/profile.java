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

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(profile.this, signin.class);
            startActivity(intent);
            finish();
            return;
        }

        loadUserProfile(currentUser);

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

        logoutIcon.setOnClickListener(v -> {
            dialog_sign_out_confirm signOutDialog = new dialog_sign_out_confirm();
            signOutDialog.show(getSupportFragmentManager(), "sign_out_dialog_tag");
        });
    }

    private void loadUserProfile(FirebaseUser currentUser) {
        emailEditText.setText(currentUser.getEmail());

        String userId = currentUser.getUid();
        DocumentReference docRef = db.collection("users").document(userId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String indexNo = document.getString("indexNo");
                        String username = document.getString("username");

                        indexNoEditText.setText(indexNo);
                        usernameEditText.setText(username);
                    } else {
                        Log.d(TAG, "No such document");
                        Toast.makeText(profile.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(profile.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
