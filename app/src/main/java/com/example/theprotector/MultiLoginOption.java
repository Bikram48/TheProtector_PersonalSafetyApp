package com.example.theprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theprotector.viewmodel.LoginRegisterViewModel;
import com.example.theprotector.views.OtpCodeActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MultiLoginOption extends AppCompatActivity implements View.OnClickListener{
    private EditText mPhoneNumber;
    private AppCompatButton secretCodeSenderBtn,emailBtn;
    private ProgressBar progressBar;
    private TextView title;
    public final String TAG=MultiLoginOption.this.getClass().getSimpleName();
    private LoginRegisterViewModel loginRegisterViewModel;
    private boolean isNumberExist,isLogin;
    CountryCodePicker ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_login_option);
        mPhoneNumber=findViewById(R.id.phone_number);
        secretCodeSenderBtn=findViewById(R.id.loginBtn_Two);
        progressBar=findViewById(R.id.progress_bar);
        emailBtn=findViewById(R.id.email_login_btn);
        title=findViewById(R.id.textView5);
        ccp=findViewById(R.id.countryCodePicker);
        ccp.registerCarrierNumberEditText(mPhoneNumber);
        loginRegisterViewModel= ViewModelProviders.of(this).get(LoginRegisterViewModel.class);
        if(getIntent().getStringExtra("page").equals("signup")){
            title.setText("CREATE YOUR \nACCOUNT");
            emailBtn.setVisibility(View.INVISIBLE);
        }
        if(getIntent().getStringExtra("page").equals("login")){
            emailBtn.setVisibility(View.VISIBLE);
            emailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MultiLoginOption.this,LoginActivity.class));
                }
            });
            if(mPhoneNumber.getText()!=null) {
                isLogin=true;
            }
        }

        secretCodeSenderBtn.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        String phone_number=mPhoneNumber.getText().toString();
        String phone=phone_number.replace("-","");
        Log.d("phone_number", "onCodeSent: "+phone);
        if(isLogin) {
            checkPhoneNumberExistence(phone);
            Log.d("existence", "onClick: "+isNumberExist);
        }else{
            sendOtpCode();
        }

    }

    public void sendOtpCode(){
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(ccp.getFullNumberWithPlus().replace(" ",""), 60, TimeUnit.SECONDS, MultiLoginOption.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                progressBar.setVisibility(View.GONE);
                secretCodeSenderBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressBar.setVisibility(View.GONE);
                secretCodeSenderBtn.setVisibility(View.VISIBLE);
                Toast.makeText(MultiLoginOption.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressBar.setVisibility(View.GONE);
                secretCodeSenderBtn.setVisibility(View.VISIBLE);
                Intent intent=new Intent(MultiLoginOption.this, OtpCodeActivity.class);
                String phone_number=mPhoneNumber.getText().toString();
                String phone=phone_number.replace("-","");
                Log.d(TAG, "onCodeSent: "+phone_number);
                //String number=phone_number.replace("-")
                intent.putExtra("phone_number",phone);
                intent.putExtra("otp_code",s);
                if(isLogin) {
                    intent.putExtra("page", "login");
                    Log.d("Hello", "onCodeSent: Hello");
                }
                else {
                    intent.putExtra("page", "signup");
                }
                startActivity(intent);
            }
        });
    }


    public void checkPhoneNumberExistence(String phone_number){

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Users").orderByChild("cell").equalTo(phone_number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    sendOtpCode();
                }else {
                    Toast.makeText(MultiLoginOption.this, "Please Create Your Account First!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}