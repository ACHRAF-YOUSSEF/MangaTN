package com.example.mangatn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangatn.R;
import com.example.mangatn.Utils;
import com.example.mangatn.interfaces.OnSignInListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.LoginModel;
import com.example.mangatn.models.SignupModel;

public class SignInActivity extends AppCompatActivity implements OnSignInListener {
    private EditText editTextEmail, editTextPassword;
    private TextView textViewSignUp;
    private Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        textViewSignUp = findViewById(R.id.textViewSignUp);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        buttonSignIn.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                RequestManager requestManager = new RequestManager(this);

                requestManager.signIn(this, new LoginModel(email, password));
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onSignInSuccess(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Get the editor to make changes to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Save the token to SharedPreferences
        editor.putString("token", Utils.getUserToken());
        editor.apply();

        finish();
    }

    @Override
    public void onSignInError(String error, Context context) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}