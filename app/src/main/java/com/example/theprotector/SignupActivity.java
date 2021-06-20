package com.example.theprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.theprotector.viewmodel.LoginRegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private TextInputEditText fullNameEditTxt;
    private TextInputEditText emailEditTxt;
    private TextInputEditText passwordEditTxt;
    private AppCompatButton registerBtn;
    private AppCompatButton loginBtn;
    private ProgressBar mProgressBarSignup;
    private LoginRegisterViewModel loginRegisterViewModel;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.login_background));
        fullNameEditTxt=findViewById(R.id.fullNameEditTxt);
        emailEditTxt=findViewById(R.id.emailEditTxt);
        passwordEditTxt=findViewById(R.id.passwordEditTxt);
        registerBtn=findViewById(R.id.loginBtn_Two);
        loginBtn=findViewById(R.id.loginBtn_Two);
        phoneNumber=getIntent().getStringExtra("phone");
        mProgressBarSignup=findViewById(R.id.progressBar2);
        loginRegisterViewModel= ViewModelProviders.of(this).get(LoginRegisterViewModel.class);
        loginRegisterViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser!=null){
                    //Toast.makeText(Signup.this, "User created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this,UserMapActivity.class));
                }
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName=fullNameEditTxt.getText().toString();
                String email=emailEditTxt.getText().toString();
                String password=passwordEditTxt.getText().toString();
                loginRegisterViewModel.signup(fullName,email,phoneNumber,password);
               // mProgressBarSignup.setVisibility(View.VISIBLE);
               // registerBtn.setVisibility(View.GONE);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }
}