package com.example.theprotector;

import android.Manifest;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Constants {
    public static final String preferenceName="emergency_contacts";
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String CALL_PHONE=Manifest.permission.CALL_PHONE;
    public static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String SEND_SMS = Manifest.permission.SEND_SMS;
    public static final int LOCATION_REQUEST_CODE = 101;
    public static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));
    public static final int SMS_REQUEST_CODE = 101;
    public static final int CONTACT_REQUEST_CODE = 61;
    public static final String DELIMITER = "/";
    public static final String PREF_FILE_NAME="com.example.theprotector.companion_contact";
    public static final String KEY_CON1 = "con1";
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static final String EXTRA_STARTED_FROM_NOTIFICATION = "com.example.theprotector.started_from_notification";
    public static final String CHANNEL_ID = "channel_01";
    public static final int NOTIFICATION_ID = 12345678;
    static final String EXTRA_LOCATION = "com.example.theprotector.location";
}
