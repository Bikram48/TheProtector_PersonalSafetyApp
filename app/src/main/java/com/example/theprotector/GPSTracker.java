package com.example.theprotector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GPSTracker extends Service{
    private final Context mContext;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static Map<String,String> userInfo;
    public GPSTracker(Context context, Map<String,String> userInfo) {
        this.mContext = context;
        this.userInfo=userInfo;
        Log.d("GPSTracker", "GPSTracker: "+userInfo.get("senderId"));

        updateLocation();
        updateRequest();
    }

    private void updateRequest(){
        HashMap hashMap=new HashMap();
        hashMap.put("status","accepted");
        FirebaseDatabase.getInstance().getReference("request_status").child(userInfo.get("senderId")).child(userInfo.get("receiverId")).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){

                }else {
                    task.getException().getMessage();
                }
            }
        });
    }

    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent=new Intent(mContext,LocationReceiver.class);
        intent.setAction(LocationReceiver.ACTION);
        return PendingIntent.getBroadcast(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest=LocationRequest.create();
        locationRequest.setInterval(9000);
        locationRequest.setFastestInterval(6000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}