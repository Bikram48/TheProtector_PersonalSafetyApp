package com.example.theprotector.Utility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.theprotector.FCMMessageReceiverService;
import com.example.theprotector.MyReceiver;
import com.example.theprotector.SharedPreference;
import com.example.theprotector.UserMapActivity;
import com.example.theprotector.model.Contact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AlertUtility {
    private static final String TAG = "AlertUtility";
    Context context;
    List<Address> addresses;
    double latitude,longitude;
    public AlertUtility(Context context){
        this.context=context;
    }
    public class SendMessageTask extends AsyncTask<Void,Integer,Void> {
        boolean gps_enabled = false;
        boolean network_enabled = false;
        String provider;
        LocationManager location_manager;
        Location location;

        @Override
        protected Void doInBackground(Void... voids) {
            location_manager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            gps_enabled = location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            SmsManager smsManager = SmsManager.getDefault();
            SharedPreferences sharedPreference = SharedPreference.getInstance(context);
            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 20, new Intent(MyReceiver.ACTION_SMS_SENT),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 73, new Intent(MyReceiver.ACTION_SMS_DELIVERED),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            if(gps_enabled) {
                getLatAndLong(1);
            }else if(network_enabled){
                getLatAndLong(2);
            }else {
                //publishProgress();
            }
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();


            String url = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f",
                    latitude, longitude);

            String msgBody = "Hey.. I am in Danger. Please, help me ASAP!! Latitudes : " + latitude + " Longitudes : " + longitude + " and my location is : " + address + " " + city + " " + state + " " + country + " " + postalCode + "" + knownName+" \n"+url;
            ArrayList<String> parts = smsManager.divideMessage(msgBody);
           // String msgBody="Hey.. I am in Danger. Please, help me ASAP!! Latitudes : " + latitude + " Longitudes : " + longitude + " and my location is :"+url;
            Log.d(TAG, "" + msgBody);
            for (Map.Entry<String, ?> contacts : SharedPreference.getData(context).entrySet()) {
                smsManager.sendMultipartTextMessage(contacts.getValue().toString(), null, parts, null, null);
            }
                SharedPreferences sharedPref = context.getSharedPreferences("accept_comapnion", context.MODE_PRIVATE);
                Map<String, ?> allEntries = sharedPref.getAll();
                if(allEntries.size()>0) {
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        String[] nameandcontact = entry.getValue().toString().split("/");
                        String requestUsername = nameandcontact[0];
                        String phone = nameandcontact[1];
                        Log.d("valueshai", requestUsername + ":phone " + phone);
                        FCMMessageReceiverService.getToken(entry.getKey(),requestUsername,context,"Your companion is in emergency please help him!! Tap here to view his last location");
                        smsManager.sendMultipartTextMessage(phone, null, parts, null, null);
                    }
                }
            return null;
        }

        void getLatAndLong(int type) {

            if (type == 1) {//GPS provider
                provider = LocationManager.GPS_PROVIDER;
            } else {
                provider = LocationManager.NETWORK_PROVIDER;
            }

            try {
               // publishProgress(2);
                if (location_manager != null) {
                    location = location_manager.getLastKnownLocation(provider);
                } else
                    Log.d(TAG, "Location Manager is null");
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.d(TAG, "" + latitude + " " + longitude);
                } else
                    Log.d(TAG, "location is null");
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}
