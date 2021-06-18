package com.example.theprotector.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.theprotector.R;
import com.example.theprotector.UserMapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpCodeActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatButton mSubmitBtn;
    private PinView mOtpCode;
    private String phoneNumber;
    private String otpcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_code);
        mOtpCode=findViewById(R.id.otpcode);
        phoneNumber=getIntent().getStringExtra("phoneNumber");
        otpcode=getIntent().getStringExtra("otp_code");
        mSubmitBtn=findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String getOtpcode=mOtpCode.getText().toString();
        Toast.makeText(this, mOtpCode.getText().toString(), Toast.LENGTH_SHORT).show();
        if(otpcode!=null){
            PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(otpcode,getOtpcode);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(OtpCodeActivity.this, UserMapActivity.class));
                    }else{
                        Toast.makeText(OtpCodeActivity.this, "Enter the correct otp code", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}