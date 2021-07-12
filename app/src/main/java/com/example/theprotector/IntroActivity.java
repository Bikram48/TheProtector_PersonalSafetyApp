package com.example.theprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.theprotector.adapter.IntroViewPagerAdapter;
import com.example.theprotector.model.ScreenItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    private ViewPager screenPager;
    private Button mButtonNext;
    TextView textView;
    TabLayout tabLayout;
    IntroViewPagerAdapter introViewPagerAdapter;
    Button btnGetStarted;
    Animation btnAnim;
    int position=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseApp.initializeApp(IntroActivity.this);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(IntroActivity.this,UserMapActivity.class));
        }
        setContentView(R.layout.activity_intro);
        tabLayout=findViewById(R.id.tabLayout);
        btnGetStarted=findViewById(R.id.btn_get_started);
        mButtonNext=findViewById(R.id.btn_next);
        textView=findViewById(R.id.textView13);
        btnAnim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        List<ScreenItem> mList=new ArrayList<>();
        mList.add(new ScreenItem("FOLLOW ME","Allow contacts to track your journey and notify them when you arrive safely",R.drawable.screenone,R.drawable.cursor));
        mList.add(new ScreenItem("SOS","Quickly alert contacts with SOS button, and click of power button four times",R.drawable.screentwo,R.drawable.ic_baseline_emergency));
        screenPager=findViewById(R.id.viewPager);
        introViewPagerAdapter=new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        tabLayout.setupWithViewPager(screenPager);

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position=screenPager.getCurrentItem();
                if(position<mList.size()){
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if(position==mList.size()-1){
                    loadLastScreen();
                }
            }
        });
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(IntroActivity.this,MultiLoginOption.class);
                intent.putExtra("page","signup");
                startActivity(intent);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(IntroActivity.this,MultiLoginOption.class);
                intent.putExtra("page","login");
                startActivity(intent);
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==mList.size()-1){
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadLastScreen() {
        mButtonNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);
    }
}