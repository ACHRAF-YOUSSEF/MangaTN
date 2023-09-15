package com.example.mangatn.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mangatn.R;
import com.example.mangatn.fragments.LoginFragment;
import com.example.mangatn.fragments.SignupFragment;

public class SignInActivity extends AppCompatActivity {
    private final LoginFragment loginFragment = new LoginFragment();
    private final SignupFragment signupFragment = new SignupFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, loginFragment)
                    .commit();
        }
    }

    public void switchToSignInFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, loginFragment)
                .commit();
    }

    public void switchToSignupFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.fragment_container, signupFragment)
                .commit();
    }
}
