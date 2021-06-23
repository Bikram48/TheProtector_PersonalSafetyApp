package com.example.theprotector.Utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Utilities {
    public static void callNumber(String number, Context context){
        Intent intent=new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+number));
        context.startActivity(intent);
    }

    public static void sendMessage(String address,String body,Context context){
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", address);
        smsIntent.putExtra("sms_body","Hi");
        context.startActivity(smsIntent);
    }
}
