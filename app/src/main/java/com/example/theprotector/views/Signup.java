package com.example.theprotector.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.theprotector.R;
import com.example.theprotector.viewmodel.LoginRegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Signup extends AppCompatActivity {
    private TextInputEditText fullNameEditTxt;
    private TextInputEditText emailEditTxt;
    private TextInputEditText phoneNumberEditTxt;
    private TextInputEditText passwordEditTxt;
    private AppCompatButton registerBtn;
    private AppCompatButton loginBtn;
    private ProgressBar mProgressBarSignup;

    private LoginRegisterViewModel loginRegisterViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        fullNameEditTxt=findViewById(R.id.fullNameEditTxt);
        emailEditTxt=findViewById(R.id.emailEditTxt);
        phoneNumberEditTxt=findViewById(R.id.phoneNumberEditTxt);
        passwordEditTxt=findViewById(R.id.passwordEditTxt);
        registerBtn=findViewById(R.id.registerBtn);
        loginBtn=findViewById(R.id.registerBtn);
        mProgressBarSignup=findViewById(R.id.progressbar_signup);
        loginRegisterViewModel= ViewModelProviders.of(this).get(LoginRegisterViewModel.class);
        loginRegisterViewModel.getUserMutableLiveData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser!=null){
                    //Toast.makeText(Signup.this, "User created", Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullName=fullNameEditTxt.getText().toString();
                String email=emailEditTxt.getText().toString();
                String password=passwordEditTxt.getText().toString();
                String phoneNumber=phoneNumberEditTxt.getText().toString();
                loginRegisterViewModel.signup(fullName,email,phoneNumber,password);
                mProgressBarSignup.setVisibility(View.VISIBLE);
                registerBtn.setVisibility(View.GONE);
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+977"+phoneNumber, 60, TimeUnit.SECONDS, Signup.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        mProgressBarSignup.setVisibility(View.GONE);
                        registerBtn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        mProgressBarSignup.setVisibility(View.GONE);
                        registerBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        mProgressBarSignup.setVisibility(View.GONE);
                        registerBtn.setVisibility(View.VISIBLE);
                        Intent intent=new Intent(Signup.this,OtpCodeActivity.class);
                        intent.putExtra("phone_number",phoneNumber);
                        intent.putExtra("otp_code",s);
                        startActivity(intent);
                    }
                });
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this,Login.class));
            }
        });
    }
}