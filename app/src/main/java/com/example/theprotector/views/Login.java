package com.example.theprotector.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.theprotector.R;
import com.example.theprotector.UserMapActivity;
import com.example.theprotector.viewmodel.LoginRegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private AppCompatButton signupSuggestion;
    private TextInputEditText emailEditTxt;
    private TextInputEditText passwordEditTxt;
    private AppCompatButton loginBtn;

    private LoginRegisterViewModel loginRegisterViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailEditTxt=findViewById(R.id.emailTxt);
        passwordEditTxt=findViewById(R.id.pwdEditText);
        signupSuggestion=findViewById(R.id.signupSuggestion);
        loginBtn=findViewById(R.id.loginBtn);
        loginRegisterViewModel= ViewModelProviders.of(this).get(LoginRegisterViewModel.class);
        loginRegisterViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
                    @Override
                    public void onChanged(FirebaseUser firebaseUser) {
                        if (firebaseUser != null) {
                            startActivity(new Intent(Login.this, UserMapActivity.class));
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
                startActivity(new Intent(Login.this,Signup.class));
            }
        });
    }
}