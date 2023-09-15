package com.example.mangatn.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mangatn.R;
import com.example.mangatn.activities.SignInActivity;
import com.example.mangatn.interfaces.auth.OnSignupListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.auth.SignupModel;
import com.google.android.material.textfield.TextInputEditText;

public class SignupFragment extends Fragment implements OnSignupListener {
    private TextInputEditText editTextUsername, editTextEmail, editTextPassword;
    private Button buttonSignUp;
    private TextView textViewSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonSignUp = view.findViewById(R.id.buttonSignUp);
        textViewSignIn = view.findViewById(R.id.textViewSignIn);

        Context context = container.getContext();

        textViewSignIn.setOnClickListener(v -> switchToSignInScreen());

        buttonSignUp.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInputs(username, email, password, context)) {
                RequestManager requestManager = new RequestManager(context);

                requestManager.signup(this, new SignupModel(username, email, password, "ROLE_USER"));
            }
        });

        return view;
    }

    private void switchToSignInScreen() {
        if (getActivity() instanceof SignInActivity) {
            ((SignInActivity) getActivity()).switchToSignInFragment();
        }
    }

    private boolean validateInputs(String username, String email, String password, Context context) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (!isValidEmail(email)) {
            editTextEmail.setError("Invalid email address");
            Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        return email.matches(emailPattern);
    }

    @Override
    public void onSignupSuccess(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        switchToSignInScreen();
    }

    @Override
    public void onSignupError(String error, Context context) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}