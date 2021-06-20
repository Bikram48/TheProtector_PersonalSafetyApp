package com.example.theprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.theprotector.viewmodel.LoginRegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private AppCompatButton signupSuggestion;
    private TextInputEditText emailEditTxt;
    private TextInputEditText passwordEditTxt;
    private AppCompatButton loginBtn;
    private LoginRegisterViewModel loginRegisterViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.login_background));
        FirebaseApp.initializeApp(LoginActivity.this);
        emailEditTxt=findViewById(R.id.emailEditTxt);
        passwordEditTxt=findViewById(R.id.passwordEditTxt);
        signupSuggestion=findViewById(R.id.loginBtn_Two);
        loginBtn=findViewById(R.id.loginBtn_One);
        loginRegisterViewModel= ViewModelProviders.of(this).get(LoginRegisterViewModel.class);
        loginRegisterViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null) {
                    startActivity(new Intent(LoginActivity.this, UserMapActivity.class));
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailEditTxt.getText().toString().trim();
                String password=passwordEditTxt.getText().toString().trim();
                loginRegisterViewModel.login(email,password);
            }
        });
        signupSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }
}