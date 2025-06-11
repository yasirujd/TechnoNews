package com.example.technonews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button signUpButton = findViewById(R.id.sign_up_button);
        final EditText indexNo = findViewById(R.id.index_no);
        final EditText username = findViewById(R.id.username);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final EditText confirmPassword = findViewById(R.id.confirm_password);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if any of the fields are empty
                if (indexNo.getText().toString().isEmpty() ||
                        username.getText().toString().isEmpty() ||
                        email.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty() ||
                        confirmPassword.getText().toString().isEmpty()) {

                    // Show an error message if any field is empty
                    Toast.makeText(signup.this, "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
                } else {
                    // If all fields are filled, proceed to the news page
                    Intent intent = new Intent(signup.this, news.class);
                    startActivity(intent);
                }
            }
        });

        // Back Button to Sign In Page
        findViewById(R.id.back_button_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Sign In page when the back button is clicked
                Intent intent = new Intent(signup.this, signin.class);
                startActivity(intent);
            }
        });
    }
}
