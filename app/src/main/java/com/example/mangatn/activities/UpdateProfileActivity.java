package com.example.mangatn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.mangatn.R;
import com.example.mangatn.Utils;
import com.example.mangatn.interfaces.auth.OnSignInWithTokenListener;
import com.example.mangatn.interfaces.auth.OnUpdateUserListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.example.mangatn.models.auth.UpdateModel;
import com.example.mangatn.models.auth.UserModel;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateProfileActivity extends AppCompatActivity implements OnSignInWithTokenListener {
    private RequestManager requestManager;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private Button updateButton;
    private Button logoutButton;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().hide();

        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        updateButton = findViewById(R.id.updateButton);
        logoutButton = findViewById(R.id.logoutButton);

        getCurrentUserDetails();

        logoutButton.setOnClickListener(v -> {
            logout();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
        });
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

            requestManager.updateUser(listener, new UpdateModel(newEmail, newUsername, currentPassword));
        });
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
