package com.example.theprotector.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.theprotector.R;
import com.example.theprotector.SignupActivity;
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
    private String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_code);
        mOtpCode=findViewById(R.id.otpcode);
        phoneNumber=getIntent().getStringExtra("phone_number");
        Log.d("practice", "onCreate: "+phoneNumber);
        otpcode=getIntent().getStringExtra("otp_code");
        status=getIntent().getStringExtra("page");
        mSubmitBtn=findViewById(R.id.submitBtn);
        
        mSubmitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(OtpCodeActivity.this,SignupActivity.class);
        String getOtpcode=mOtpCode.getText().toString();
     //   Toast.makeText(this, mOtpCode.getText().toString(), Toast.LENGTH_SHORT).show();
        if(otpcode!=null){
            PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(otpcode,getOtpcode);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if(status.equals("login")) {
                            startActivity(new Intent(OtpCodeActivity.this, UserMapActivity.class));
                        }
                        if(status.equals("signup")) {
                            intent.putExtra("phone", phoneNumber);
                            startActivity(intent);
                        }
                    }else{
                        Toast.makeText(OtpCodeActivity.this, "Enter the correct otp code", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}