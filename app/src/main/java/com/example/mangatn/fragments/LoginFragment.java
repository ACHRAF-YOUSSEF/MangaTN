package com.example.mangatn.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mangatn.R;
import com.example.mangatn.Utils;
import com.example.mangatn.activities.SignInActivity;
import com.example.mangatn.interfaces.auth.OnSignInListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.auth.LoginModel;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment implements OnSignInListener {
    private TextInputEditText editTextEmail, editTextPassword;
    private TextView textViewSignUp;
    private Button buttonSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        textViewSignUp = view.findViewById(R.id.textViewSignUp);
        buttonSignIn = view.findViewById(R.id.buttonSignIn);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        Context context = container.getContext();

        textViewSignUp.setOnClickListener(v -> {
            if (getActivity() instanceof SignInActivity) {
                ((SignInActivity) getActivity()).switchToSignupFragment();
            }
        });

        buttonSignIn.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInputs(email, password, context)) {
                RequestManager requestManager = new RequestManager(context);

                requestManager.signIn(this, new LoginModel(email, password));
            }
        });

        return view;
    }

    private boolean validateInputs(String email, String password, Context context) {
        if (email.isEmpty() || password.isEmpty()) {
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
    public void onSignInSuccess(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // Get the editor to make changes to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Save the token to SharedPreferences
        editor.putString("token", Utils.getUserToken());
        editor.apply();

        if (getActivity() instanceof SignInActivity) {
            ((SignInActivity) getActivity()).finish();
        }
    }

    @Override
    public void onSignInError(String error, Context context) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}