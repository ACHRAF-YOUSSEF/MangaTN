package com.example.mangatn.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mangatn.R;
import com.example.mangatn.Utils;
import com.example.mangatn.interfaces.auth.OnSignInWithTokenListener;
import com.example.mangatn.interfaces.auth.OnUpdateUserListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.auth.UpdateModel;
import com.example.mangatn.models.auth.UserModel;
import com.google.android.material.textfield.TextInputEditText;

public class UpdateProfileActivity extends AppCompatActivity implements OnSignInWithTokenListener {
    private RequestManager requestManager;
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText emailEditText;
    private Button updateButton;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        updateButton = findViewById(R.id.updateButton);

        getCurrentUserDetails();
    }

    private void getCurrentUserDetails() {
        requestManager = new RequestManager(this);

        requestManager.signInWithToken(this);
    }

    private void logout() {
        // Perform the logout logic, such as clearing session data or revoking access tokens
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Get the editor to make changes to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Save the token to SharedPreferences
        editor.putString("token", null);
        Utils.setUserToken(null);
        editor.apply();
    }

    @Override
    public void onSignInSuccess(UserModel response, String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        userModel = response;

        emailEditText.setText(userModel.getEmail());
        usernameEditText.setText(userModel.getUserName());

        updateButton.setOnClickListener(v -> {
            String newEmail = emailEditText.getText().toString().trim();
            String newUsername = usernameEditText.getText().toString().trim();
            String currentPassword = passwordEditText.getText().toString().trim();

            if (validateInputs(newUsername, newEmail, currentPassword)) {
                requestManager.updateUser(listener, new UpdateModel(newEmail, newUsername, currentPassword));
            }
        });
    }

    private boolean validateInputs(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (!isValidEmail(email)) {
            emailEditText.setError("Invalid email address");
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        return email.matches(emailPattern);
    }

    @Override
    public void onSignInError(String error, Context context) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

    private final OnUpdateUserListener listener = new OnUpdateUserListener() {
        @Override
        public void onSuccess(ApiResponse response, String message, Context context) {
            Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

            logout();

            Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        }

        @Override
        public void onError(String message, Context context) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };
}
