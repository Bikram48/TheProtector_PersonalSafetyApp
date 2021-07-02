package com.example.theprotector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.theprotector.SingletonPattern.RequestQueueInstance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FCMMessageReceiverService extends FirebaseMessagingService {
    public static final String TAG="MyTag";
    public static final String URL="https://fcm.googleapis.com/fcm/send";
    public static DatabaseReference databaseReference,registrationTokenRef;

    public FCMMessageReceiverService(){

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        databaseReference= FirebaseDatabase.getInstance().getReference("Requests");
        if(remoteMessage.getNotification()!=null){

            Intent intent=new Intent(getApplicationContext(),UserMapActivity.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            String title=remoteMessage.getNotification().getTitle();
            String body=remoteMessage.getNotification().getBody();
            Notification notification=new NotificationCompat.Builder(this,App.FCM_CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1002,notification);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken called");
        //sendRegistrationToServer(token);
    }
/*
    public void sendRegistrationToServer(String token){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            registrationTokenRef = FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            registrationTokenRef.setValue(token.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Token is updated successfully ");
                    }else {
                        Log.d(TAG, "onComplete: "+task.getException().getMessage());
                    }
                }
            });
        }
    }

 */
     //This callback is called when new FCM registration token is regenerated.
    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG, "onDeletedMessages called ");
    }

    public static void sendNotification(String token,String body,Context context){
        RequestQueue requestQueue= RequestQueueInstance.getInstance(context).getRequestQueue();
        JSONObject mainObj=new JSONObject();
        try {
            //Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
            mainObj.put("to",token);
            JSONObject notificationObject=new JSONObject();
            notificationObject.put("title","The Protector");
            notificationObject.put("body",body);
            mainObj.put("notification",notificationObject);

            JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, URL, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header=new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAA0vqtccs:APA91bG4lXHDlK3a0B7T8nXemDVfOFYPnXJIQ05xONeHVrJgyS4J6vEU6LY-wb9ZlF3tSt4kOv53pGwqAJ03m6cPx1hCbkymqGy899HYn_L55FJiPZ9ysjbplVz7Ga9Sdl0CJacRS4D_");
                    return header;
                }


            };

            //requestQueue.getCache().clear();
            RequestQueueInstance.getInstance(context).addRequest(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static  void getToken(String userId,String username,Context context,String body){

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Tokens").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tokenKey =snapshot.getValue(String.class);
                //  Log.d(TAG, "onDataChange: "+tokenKey);
                //Toast.makeText(ContactListActivity.this, "Tokens "+tokenKey, Toast.LENGTH_SHORT).show();
                if(tokenKey!=null) {

                    FCMMessageReceiverService.sendNotification(tokenKey,body,context);
                    //startActivity(new Intent(ContactListActivity.this,UserMapActivity.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

}
