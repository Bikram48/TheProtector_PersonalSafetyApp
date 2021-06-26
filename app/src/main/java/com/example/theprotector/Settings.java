package com.example.theprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.theprotector.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity implements View.OnClickListener{
    private FloatingActionButton back_btn;
    private LinearLayout mEmergencyLayout;
    private LinearLayout mLogoutLayout;
    private LinearLayout mEditProfileLayout;
    private LinearLayout mAboutLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mEmergencyLayout=findViewById(R.id.emergency_contacts);
        mEmergencyLayout.setOnClickListener(this);
        mEditProfileLayout=findViewById(R.id.profileEdit);
        back_btn=findViewById(R.id.back_btn);
        mAboutLayout=findViewById(R.id.aboutBtn);
        mAboutLayout.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        mEditProfileLayout.setOnClickListener(this);
        mLogoutLayout=findViewById(R.id.logout_layout);
        mLogoutLayout.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout_layout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Settings.this, LoginActivity.class));
                break;
            case R.id.emergency_contacts:
                startActivity(new Intent(Settings.this,AllEmergencyContacts.class));
                break;
            case R.id.profileEdit:
                startActivity(new Intent(Settings.this,EditProfileActivity.class));
                break;
            case R.id.back_btn:
                finish();
                break;
            case R.id.aboutBtn:
                startActivity(new Intent(Settings.this,AboutUs.class));
                break;
        }
    }
}