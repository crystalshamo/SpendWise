package com.example.spendwise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    public EditText emailEditText;
    public Button restPasswordBtn;
    public ProgressBar progressBar;
    public TextView back;
    public FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.ForgotEmail);
        restPasswordBtn = findViewById(R.id.resetPass);
        progressBar = findViewById(R.id.progBar);
        back = findViewById(R.id.backLog);

        auth = FirebaseAuth.getInstance();

        back.setOnClickListener(v -> finish());

        restPasswordBtn.setOnClickListener(v -> restPassword());
    }

    public void restPassword() {
        String emailAddress = emailEditText.getText().toString().trim();

        if (emailAddress.isEmpty()) {
            Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPassword.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ForgotPassword.this, "Invalid email or user not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
