package com.example.theprotector.model;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class Repository{
    private Application application;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String token;

    Map<String, Object> reg_entry;
    public Repository(Application application){
        this.application=application;
        firebaseAuth=FirebaseAuth.getInstance();
        reg_entry = new HashMap<>();
        firebaseFirestore=FirebaseFirestore.getInstance();
        userMutableLiveData=new MutableLiveData<>();
    }

    public void signup(String fullname,String email,String phone_number,String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   userMutableLiveData.postValue(firebaseAuth.getCurrentUser());

                   String userId=firebaseAuth.getCurrentUser().getUid();
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(fullname).build();
                    Log.d("signup", "user name: "+ FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("MyTag", "User profile updated.");
                            }else{
                                task.getException().getMessage();
                            }
                        }
                    });
                   reg_entry.put("userId",userId);
                   reg_entry.put("Name",fullname.toString());
                   reg_entry.put("Email",email);
                   reg_entry.put("cell",phone_number);
                   reg_entry.put("password",password);
                    storeToken();
                   FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(reg_entry).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               //Toast.makeText(application, "User has been registered successfully", Toast.LENGTH_SHORT).show();
                           }else{
                               Toast.makeText(application, "Failed to register", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });

                }else{
                    Toast.makeText(application, "Registration Failed "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void login(String email,String password){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                }else{
                    Toast.makeText(application, "Login Failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void storeToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String userId=firebaseAuth.getCurrentUser().getUid();
                    token=task.getResult().toString();
                    FirebaseDatabase.getInstance().getReference("Tokens").child(userId).setValue(token);
                }else{
                    task.getException().getMessage();
                }
            }
        });

    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
