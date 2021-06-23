package com.example.theprotector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theprotector.adapter.MyCustomAdapter;
import com.example.theprotector.model.Contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactListActivity extends AppCompatActivity implements MyCustomAdapter.ItemClicked {
    public static final int REQUEST_CODE=505;
    public static final String URL="https://fcm.googleapis.com/fcm/send";
    private AppCompatButton companion_confirmation_btn;
    private String phoneNumber;
    private List<Contact> contactList;
    private RecyclerView mRecyclerView;
    private Map<String,String > emergencyContacts;
    Cursor phones;
    private String receiverId;
    MyCustomAdapter contactAdapter;
    public final String TAG=ContactListActivity.this.getClass().getSimpleName();
    DatabaseReference companionRequestRef,notificationRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        companion_confirmation_btn=findViewById(R.id.companion_confirmation_btn);
        mRecyclerView=findViewById(R.id.companionRecyclerView);
        contactList=new ArrayList<>();
        companionRequestRef=FirebaseDatabase.getInstance().getReference().child("Companion Requests");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        requestPermission();

    }
    /*
    private void setClickListener(){
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

     */
    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, Constants.PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            phones = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            LoadContact loadContact=new LoadContact();
            loadContact.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                requestPermission();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {

                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                    Contact selectUser = new Contact();
                    selectUser.setName(name);
                    selectUser.setNumber(phoneNumber);
                    contactList.add(selectUser);


                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // sortContacts();
            int count=contactList.size();
            ArrayList<Contact> removed=new ArrayList<>();
            ArrayList<Contact> contacts=new ArrayList<>();
            for(int i=0;i<contactList.size();i++){
                Contact inviteFriendsProjo = contactList.get(i);

                if(inviteFriendsProjo.getName().matches("\\d+(?:\\.\\d+)?")||inviteFriendsProjo.getName().trim().length()==0){
                    removed.add(inviteFriendsProjo);
                    Log.d("Removed Contact",new Gson().toJson(inviteFriendsProjo));
                }else{
                    contacts.add(inviteFriendsProjo);
                }
            }
            contacts.addAll(removed);
            contactList=contacts;
            contactAdapter=new MyCustomAdapter(ContactListActivity.this,contactList);
            // mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerView.setAdapter(contactAdapter);
        }
    }

    public void getData(){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String values=dataSnapshot.child("cell").getValue(String.class);
                    for(Map.Entry<String,String > entry:emergencyContacts.entrySet()){
                        if(entry.getValue().equals(values)){
                            Log.d(TAG, "onDataChange: phone number matched");
                            Toast.makeText(ContactListActivity.this, "Phone number matched", Toast.LENGTH_SHORT).show();
                             receiverId=dataSnapshot.child("userId").getValue(String.class);
                            String username=dataSnapshot.child("Name").getValue(String.class);
                            String myId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String name=entry.getKey();
                            String[] split=name.split(" ");
                            String sName;
                            if(split.length>1){
                                sName=String.valueOf(split[0].charAt(0))+String.valueOf(split[1].charAt(0));
                            }else{
                                sName=String.valueOf(split[0].charAt(0));
                            }
                            add_companion_request(myId,receiverId,sName);
                           // getToken(userId,username);
                            //Toast.makeText(ContactListActivity.this, "Matched "+phoneNumber, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onItemClicked(int index,  Map<String,String > emergencyContacts) {
        if(index>0){
            this.emergencyContacts=emergencyContacts;
            companion_confirmation_btn.setVisibility(View.VISIBLE);
            companion_confirmation_btn.setText("Selected Contacts("+index+")");
            //phoneNumber=contact.getNumber();
           // Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show();
            companion_confirmation_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getData();

                  //  add_companion_request(myId,receiverId);
                    Intent intent=new Intent();
                    intent.putExtra("object", (Serializable) emergencyContacts);
                    setResult(0,intent);
                    finish();
                    //startActivity(new Intent(ContactListActivity.this,UserMapActivity.class));
                }
            });
        }else{
            companion_confirmation_btn.setVisibility(View.GONE);
        }
    }

    public void add_companion_request(String senderId,String receiverId,String sName){
        companionRequestRef.child(senderId).child(receiverId)
                .child("requestType").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            companionRequestRef.child(receiverId).child(senderId).child("requestType")
                                    .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    HashMap<String, String> companionRequestMap = new HashMap<>();
                                    companionRequestMap.put("from", senderId);
                                    companionRequestMap.put("type", "request");
                                    notificationRef.child(receiverId).push()
                                            .setValue(companionRequestMap);
                                }
                            });

                        }
                    }
                });
    }

}


