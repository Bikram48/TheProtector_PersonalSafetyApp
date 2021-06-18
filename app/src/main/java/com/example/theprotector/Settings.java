package com.example.theprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.theprotector.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view,UserSetting.class,null)
                .commit();
    }
}