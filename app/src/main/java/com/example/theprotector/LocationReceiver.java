package com.example.theprotector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LocationReceiver extends BroadcastReceiver {
    public static final String ACTION="com.example.theprotector.UPDATE_LOCATION";
    private static final String TAG = "LocationRecevier";
    DatabaseReference userlocation;
    public LocationReceiver(){
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: " + GPSTracker.userInfo.get("senderId") + "   " + GPSTracker.userInfo.get("receiverId"));
            userlocation = FirebaseDatabase.getInstance().getReference("location").child(GPSTracker.userInfo.get("senderId"));
            if (ACTION.equals(action)) {
                LocationResult locationResult = LocationResult.extractResult(intent);
                if (locationResult != null) {
                    UpdateLocation updateLocation = new UpdateLocation(locationResult);
                    updateLocation.execute();
                }
            }
        }
    }
    class UpdateLocation extends AsyncTask<Void,Integer,Void>{
        LocationResult locationResult;
        UpdateLocation(LocationResult locationResult){
            this.locationResult=locationResult;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Location location=locationResult.getLastLocation();
            HashMap hashMap=new HashMap();
            hashMap.put("latitude",location.getLatitude());
            hashMap.put("longitude",location.getLongitude());
            Log.d("location", "location: "+location);

            userlocation.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "Updated");
                    }else{
                        task.getException().getMessage();
                    }
                }
            });
            return null;
        }
    }
}
