package com.example.mangatn.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mangatn.R;
import com.example.mangatn.interfaces.auth.OnForgotPasswordListener;
import com.example.mangatn.interfaces.auth.OnResetPasswordListener;
import com.example.mangatn.manager.RequestManager;
import com.example.mangatn.models.ApiResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextInputEditText editTextEmail;
    private Button buttonVerifyEmail;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editTextEmail = findViewById(R.id.editTextEmail);
        buttonVerifyEmail = findViewById(R.id.buttonVerifyEmail);

        buttonVerifyEmail.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();

            if (isValidEmail(email)) {
                RequestManager requestManager = new RequestManager(this);
                requestManager.forgotPassword(new OnForgotPasswordListener<ApiResponse>() {
                    @Override
                    public void onFetchData(ApiResponse apiResponse, String message, Context context) {
                        Snackbar.make(view, apiResponse.getMessage(), Snackbar.LENGTH_SHORT).show();

                        showVerificationCodeDialog();
                    }

                    @Override
                    public void onError(String message, Context context) {
                        Snackbar.make(view, "Error has occured!", Snackbar.LENGTH_SHORT).show();
                    }
                }, email);
            } else {
                String invalidEmailAddress = "Invalid email address";

                editTextEmail.setError(invalidEmailAddress);
                Snackbar.make(view, invalidEmailAddress, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void showVerificationCodeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_verification_code, null);
        TextInputEditText editTextVerificationCode = dialogView.findViewById(R.id.editTextVerificationCode);
        Button buttonConfirmVerification = dialogView.findViewById(R.id.buttonConfirmVerification);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setTitle("Enter Verification Code");

        final AlertDialog alertDialog = dialogBuilder.show();

        buttonConfirmVerification.setOnClickListener(view -> {
            verificationCode = editTextVerificationCode.getText().toString().trim();

            if (isValidVerificationCode(verificationCode)) {
                alertDialog.dismiss();

                showPasswordChangeDialog();
            } else {
                String invalidVerificationCode = "Invalid verification code";

                editTextVerificationCode.setError(invalidVerificationCode);
                Snackbar.make(view, invalidVerificationCode, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    }

    private boolean isValidVerificationCode(String verificationCode) {
        return verificationCode.length() == 6;
    }

    private void showPasswordChangeDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_password_change, null);
        TextInputEditText editTextNewPassword = dialogView.findViewById(R.id.editTextNewPassword);
        Button buttonChangePassword = dialogView.findViewById(R.id.buttonChangePassword);

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setTitle("Change Password");

        final AlertDialog alertDialog = dialogBuilder.show();

        buttonChangePassword.setEnabled(false);
        editTextNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isPasswordEmpty = charSequence.toString().trim().isEmpty();

                buttonChangePassword.setEnabled(!isPasswordEmpty);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        buttonChangePassword.setOnClickListener(view -> {
            String newPassword = editTextNewPassword.getText().toString().trim();

            RequestManager requestManager = new RequestManager(this);
            requestManager.resetPassword(new OnResetPasswordListener<ApiResponse>() {
                @Override
                public void onFetchData(ApiResponse apiResponse, String message, Context context) {
                    alertDialog.dismiss();

                    Toast.makeText(ResetPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();

                    finish();
                }

                @Override
                public void onError(String message, Context context) {
                    alertDialog.dismiss();

                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                    finish();
                }
            }, verificationCode, newPassword);
        });
    }
}