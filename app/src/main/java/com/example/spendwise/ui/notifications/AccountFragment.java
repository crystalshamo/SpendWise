package com.example.spendwise.ui.notifications;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.spendwise.R;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {

    public EditText firstNameField, lastNameField, emailField, passwordField;
    public ImageView editFirstName, editLastName, editPassword;
    public Button updateBtn;

    public FirebaseAuth mAuth;
    public FirebaseFirestore db;
    public DocumentReference userRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstNameField = root.findViewById(R.id.firstNameField);
        lastNameField = root.findViewById(R.id.lastNameField);
        emailField = root.findViewById(R.id.emailField);
        passwordField = root.findViewById(R.id.passwordField);

        editFirstName = root.findViewById(R.id.editFirstName);
        editLastName = root.findViewById(R.id.editLastName);
        editPassword = root.findViewById(R.id.editPassword);

        updateBtn = root.findViewById(R.id.updateBtn);

        loadUserData();

        editFirstName.setOnClickListener(v -> firstNameField.setEnabled(true));
        editLastName.setOnClickListener(v -> lastNameField.setEnabled(true));

        editPassword.setOnClickListener(v -> passwordChangeDialog());

        updateBtn.setOnClickListener(v -> updateUserProfile());

        return root;
    }

    // Loa user data from firebase and displays them
    public void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailField.setText(user.getEmail());
            passwordField.setText("********");

            userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    firstNameField.setText(documentSnapshot.getString("firstName"));
                    lastNameField.setText(documentSnapshot.getString("lastName"));
                }
            });
        }
    }

    // Updates user information in database if updated on page
    public void updateUserProfile() {
        String firstName = firstNameField.getText().toString().trim();
        String lastName = lastNameField.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) return;

        if (userRef != null) {
            userRef.update(
                    "firstName", firstName,
                    "lastName", lastName
            ).addOnSuccessListener(aVoid -> {
                firstNameField.setEnabled(false);
                lastNameField.setEnabled(false);
            });
        }
    }

    // Shows password change dialog
    public void passwordChangeDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_password, null);
        EditText currentPass = dialogView.findViewById(R.id.currentPasswordInput);
        EditText newPass = dialogView.findViewById(R.id.newPasswordInput);

        new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String current = currentPass.getText().toString();
                    String newP = newPass.getText().toString();

                    if (newP.length() < 6) return;

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && user.getEmail() != null) {
                        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), current))
                                .addOnSuccessListener(unused -> {
                                    user.updatePassword(newP)
                                            .addOnSuccessListener(aVoid -> passwordField.setText("********"));
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
