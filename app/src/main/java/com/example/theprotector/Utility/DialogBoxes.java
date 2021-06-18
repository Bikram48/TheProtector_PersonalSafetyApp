package com.example.theprotector.Utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.theprotector.FCMMessageReceiverService;
import com.example.theprotector.R;
import com.example.theprotector.UserMapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DialogBoxes {
    Dialog dialog;
    Context context;
    DatabaseReference requestref;
    public DialogBoxes(Context context){
        this.context=context;
        dialog = new Dialog(context);
        requestref = FirebaseDatabase.getInstance().getReference("request_status");
    }
    public void showDialog(Map<String,String> userInfo) {
        //body_message.setText("Hey "+", "+" has requested that you to be his companion. Keep an eye on her as she is on the move");
        // DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Request").child()
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_custom);
        TextView cancel_btn = dialog.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequest(userInfo);
                dialog.dismiss();
            }
        });
        TextView ok_btn = dialog.findViewById(R.id.ok_btn);
        TextView body_message = dialog.findViewById(R.id.body_message);
        body_message.setText("Hey " + userInfo.get("receiverUsername") + ", " + userInfo.get("senderUsername") + " has requested that you be his companion. Keep an eye on him as he is on the move.");
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptRequest(userInfo);
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    public void cancelRequest(Map<String,String> userInfo) {
        requestref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens").child(userInfo.get("senderId"));
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String tokenKey = snapshot.getValue(String.class);
                           // Log.d(TAG, "sendertokenKey: " + tokenKey);
                            String senderUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String body = senderUsername + " has declined your companion request";
                            FCMMessageReceiverService.sendNotification(tokenKey, senderUsername, senderId, userInfo.get("senderUsername"), userInfo.get("senderId"), body, "declined", context);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            error.getMessage();
                        }
                    });
                } else {
                    task.getException().getMessage();
                }
            }
        });
    }

    public void acceptRequest(Map<String,String> userInfo) {

        HashMap hashMap = new HashMap();
        hashMap.put("status", "pending");
        requestref.child(userInfo.get("senderId")).child(userInfo.get("receiverId")).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens").child(userInfo.get("senderId"));
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String tokenKey = snapshot.getValue(String.class);
                           // Log.d(TAG, "sendertokenKey: " + tokenKey);
                            String senderUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                            String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String body = senderUsername + " has accepted your companion request";
                            FCMMessageReceiverService.sendNotification(tokenKey, senderUsername, senderId, userInfo.get("senderUsername"), userInfo.get("senderId"), body, "accepted", context);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            error.getMessage();
                        }
                    });
                    //showAcceptedDialog();
                } else {
                    task.getException().getMessage();
                }
            }
        });
    }


    public void showAcceptedDialog(Map<String,String> userInfo) {
        //String senderUsername= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.acceptandcancel_dialog_custom);
        TextView ok_btn = dialog.findViewById(R.id.ok_btn);
        TextView body_message = dialog.findViewById(R.id.body_message);
        body_message.setText(userInfo.get("receiverUsername") + " has accepted your companion request");
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void showCancelledDialog(Map<String,String> userInfo) {
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.acceptandcancel_dialog_custom);
        TextView ok_btn = dialog.findViewById(R.id.ok_btn);
        TextView body_message = dialog.findViewById(R.id.body_message);
        body_message.setText(userInfo.get("receiverUsername") + " has declined your companion request");
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInfo.clear();
                dialog.dismiss();
            }
        });
        dialog.show();
    }



}
