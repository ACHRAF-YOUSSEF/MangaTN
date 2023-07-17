package com.example.mangatn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangatn.R;
import com.example.mangatn.interfaces.OnSignupListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.SignupModel;

public class SignUpActivity extends AppCompatActivity implements OnSignupListener {
    private EditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonSignUp;
    private TextView textViewSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewSignIn = findViewById(R.id.textViewSignIn);

        textViewSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        buttonSignUp.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInputs(username, email, password)) {
                RequestManager requestManager = new RequestManager(this);

                requestManager.signup(this, new SignupModel(username, email, password));
            }
        });
    }

    private boolean validateInputs(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onSignupSuccess(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSignupError(String error, Context context) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}