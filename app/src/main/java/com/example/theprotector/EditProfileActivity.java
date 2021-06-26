package com.example.theprotector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText fullName;
    private TextInputEditText emailTxt;
    private TextInputEditText phoneTxt;
    private AppCompatButton saveBtn;
    private ImageView backIcon;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private FirebaseUser user=auth.getCurrentUser();
    DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        fullName=findViewById(R.id.fullNameEditTxt);
        emailTxt=findViewById(R.id.emailEditTxt);
        saveBtn=findViewById(R.id.profile_save);
        phoneTxt=findViewById(R.id.phone_number_txt);
        backIcon=findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        userRef= FirebaseDatabase.getInstance().getReference("Users");
        extractData();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        UserProfileChangeRequest updateProfile=new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName.getText().toString())
                .build();
        user.updateProfile(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    userRef.child(user.getUid()).child("Name").setValue(fullName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                              //  Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String password=snapshot.child("password").getValue().toString();
                Log.d("EditProfile", "onDataChange: "+password);
                AuthCredential credential= EmailAuthProvider.getCredential(user.getEmail(),password);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.updateEmail(emailTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            userRef.child(user.getUid()).child("Email").setValue(emailTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                      //  Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userRef.child(user.getUid()).child("cell").setValue(phoneTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }
            }
        });


    /*
        user.updatePhoneNumber(phoneTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    userRef.child(user.getUid()).child("Email").setValue(emailTxt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        })

     */
    }

    private void extractData() {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String fullname=snapshot.child("Name").getValue().toString();
                final String email=snapshot.child("Email").getValue().toString();
                final String cell=snapshot.child("cell").getValue().toString();
                fullName.setText(fullname);
                emailTxt.setText(email);
                phoneTxt.setText(cell);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}