package com.example.theprotector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.usage.UsageStats;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.theprotector.adapter.ContactAdapter;
import com.example.theprotector.adapter.EmergencyContactAdapter;
import com.example.theprotector.model.Contact;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllEmergencyContacts extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private List<Contact> contactList;
    private ImageView addContact;
    public final String TAG=AllEmergencyContacts.this.getClass().getSimpleName();
    EmergencyContactAdapter contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_emergency_contacts);
        recyclerView=(RecyclerView) findViewById(R.id.savedContactsRecyclerView);
        contactList=new ArrayList<>();
        addContact=(ImageView) findViewById(R.id.addContact);
        addContact.setOnClickListener(this);
        extractData();
    }

    private void extractData() {

        SharedPreferences sharedPref = getSharedPreferences("emergency_contacts", MODE_PRIVATE);
            Map<String, ?> allEntries = sharedPref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                Contact contact = new Contact();
                Log.d(TAG, "extractData: "+entry.getKey());
                contact.setName(entry.getKey().toString());
                contact.setNumber(entry.getValue().toString());
                contactList.add(contact);
            }

            contactAdapter = new EmergencyContactAdapter(AllEmergencyContacts.this, contactList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(contactAdapter);
            contactAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        startActivity(new Intent(AllEmergencyContacts.this,ContactPicker.class));
    }
}