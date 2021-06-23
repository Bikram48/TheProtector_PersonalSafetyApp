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
    public static DatabaseReference databaseReference;

    public FCMMessageReceiverService(){

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> userInfo=new HashMap<>();
        for(String key:remoteMessage.getData().keySet()){
            userInfo.put(key,remoteMessage.getData().get(key));
            //Log.d(TAG, "onMessageReceived key: "+key+" Data: "+remoteMessage.getData().get(key));
        }


        databaseReference= FirebaseDatabase.getInstance().getReference("Requests");
        if(remoteMessage.getNotification()!=null&&remoteMessage.getData().size()>0){
            Log.d(TAG, "Username received: "+userInfo.get("senderUsername"));
            Intent intent=new Intent(getApplicationContext(),UserMapActivity.class);
            if(userInfo.get("status").equals("companion_request")) {
                intent.putExtra("from", "notification");
            }
            if(userInfo.get("status").equals("accepted")) {
                intent.putExtra("status", "accepted");
            }
            if(userInfo.get("status").equals("declined")) {
                intent.putExtra("status", "declined");
            }
            intent.putExtra("userinfo",(Serializable)userInfo);
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

        if(remoteMessage.getData().size()>0){


        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken called");
    }

     //This callback is called when new FCM registration token is regenerated.
    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.d(TAG, "onDeletedMessages called ");
    }

    public static void sendNotification(String token, String receiverUsername,String receiverUserid,String senderUsername, String senderId,String body,String status, Context context){
        RequestQueue requestQueue= RequestQueueInstance.getInstance(context).getRequestQueue();
        JSONObject mainObj=new JSONObject();
        try {
            //Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
            mainObj.put("to",token);
            JSONObject notificationObject=new JSONObject();
            notificationObject.put("title","The Protector");
            notificationObject.put("body",body);
            mainObj.put("notification",notificationObject);
            JSONObject dataObject=new JSONObject();
            dataObject.put("senderUsername",senderUsername);
            dataObject.put("senderId",senderId);
            dataObject.put("receiverUsername",receiverUsername);
            dataObject.put("receiverId",receiverUserid);
            dataObject.put("status",status);
            mainObj.put("data",dataObject);
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
    public static  void getToken(String userId,String username,Context context){

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Tokens").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tokenKey =snapshot.getValue(String.class);
                //  Log.d(TAG, "onDataChange: "+tokenKey);
                //Toast.makeText(ContactListActivity.this, "Tokens "+tokenKey, Toast.LENGTH_SHORT).show();
                if(tokenKey!=null) {
                    String senderUsername= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String senderId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    // add_companion_request(senderId,userId);
                    String body=senderUsername+" has invited you to be his companion";
                    FCMMessageReceiverService.sendNotification(tokenKey,username,userId,senderUsername,senderId,body,"companion_request",context);
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
