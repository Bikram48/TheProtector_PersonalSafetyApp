package com.example.theprotector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import mehdi.sakout.aboutpage.AboutPage;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Element adsElement = new Element();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(getResources().getString(R.string.app_info))
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("CONNECT WITH US!")
                .addEmail("chandbikram001@gmail.com")
                .addWebsite("Your website/")
                .addYoutube("youtube.com")   //Enter your youtube link here (replace with my channel link)
                .addPlayStore("com.example.theprotector")   //Replace all this with your package name
                .addInstagram("https://www.instagram.com/thisisbikram_/")    //Your instagram id
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);
    }
    private Element createCopyright()
    {
        Element copyright = new Element();
        @SuppressLint("DefaultLocale") final String copyrightString = String.format("Copyright %d by The Protector", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        // copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUs.this,copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
}