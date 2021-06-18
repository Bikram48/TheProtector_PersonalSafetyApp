package com.example.theprotector.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.theprotector.model.Repository;
import com.google.firebase.auth.FirebaseUser;

public class LoginRegisterViewModel extends AndroidViewModel {
    private Repository repository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    public LoginRegisterViewModel(@NonNull Application application) {
        super(application);
        repository=new Repository(application);
        userMutableLiveData=repository.getUserMutableLiveData();
    }

    public void signup(String fullname,String email,String phone_number,String password){
        repository.signup(fullname,email,phone_number,password);
    }

    public void login(String email,String password){
        repository.login(email,password);
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
            return userMutableLiveData;
    }
}
