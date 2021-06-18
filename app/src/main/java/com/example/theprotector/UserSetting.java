package com.example.theprotector;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.theprotector.views.Login;
import com.google.firebase.auth.FirebaseAuth;


public class UserSetting extends Fragment implements View.OnClickListener{
    private LinearLayout mLogoutLayout;
    public UserSetting() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_user_setting, container, false);
        mLogoutLayout=(LinearLayout)view.findViewById(R.id.logout_layout);
        mLogoutLayout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout_layout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), Login.class));
                break;
        }
    }
}