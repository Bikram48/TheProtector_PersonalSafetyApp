package com.example.theprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view,new login_options())
                .commit();
    }
}