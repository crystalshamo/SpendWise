package com.example.spendwise;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;
    public FirebaseFirestore db;
    public EditText email, password, firstName, lastName;
    public Button btnRegister;
    public TextView textLogin;
    public ImageView viewPass;
    public boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstName = findViewById(R.id.signup_first_name);
        lastName = findViewById(R.id.signup_last_name);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        btnRegister = findViewById(R.id.signup_button);
        textLogin = findViewById(R.id.loginRedirectText);
        viewPass = findViewById(R.id.viewPass);

        viewPass.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordVisible = true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordVisible = false;
                    break;
            }
            return true;
        });

        btnRegister.setOnClickListener(v -> registerUser());

        textLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }


    public void registerUser() {
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        if (fName.isEmpty() || lName.isEmpty() || userEmail.isEmpty() || userPass.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("firstName", fName);
                            userData.put("lastName", lName);
                            userData.put("email", userEmail);

                            db.collection("users").document(uid)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(this, MainActivity.class));
                                        finish();
                                    });
                        }
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(this, "Password is too weak", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
