package com.example.theprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.RequestQueue;

public class MainActivity extends AppCompatActivity{
    private RequestQueue mRequestQue;
    public static final String URL="https://fcm.googleapis.com/fcm/send";
    //Button btn;
    public final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(this,UserMapActivity.class));
        }else {
            startActivity(new Intent(this, Login.class));
        }

         */
        startActivity(new Intent(this, LoginActivity.class));
    }

}