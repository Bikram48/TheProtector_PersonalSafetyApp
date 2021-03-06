package com.example.theprotector;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theprotector.Constants;
import com.example.theprotector.ContactListActivity;
import com.example.theprotector.FallDetectAlgo;
import com.example.theprotector.LocationUpdatesService;
import com.example.theprotector.PowerButtonService;
import com.example.theprotector.R;
import com.example.theprotector.Utility.AlertUtility;
import com.example.theprotector.Utility.Utilities;
import com.example.theprotector.adapter.CompanionAdapter;
import com.example.theprotector.model.CompanionInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,View.OnClickListener,  SharedPreferences.OnSharedPreferenceChangeListener {
    private RecyclerView companionDisplay;
    private ImageView mSettingIcon,mGps,emergencyIcon;
    private FloatingActionButton mCompanionAddBtn;
    private String TAG = "MapDemo";
    private boolean isClicked=false,isEmergency=false;
    private PopupWindow mPopupWindow,invitePopWindow;
    private static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AutoCompleteTextView mSearchLocation;
    private FloatingActionButton location_sharing_btn,alert_btn;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private RelativeLayout mRelativeLayout;
    private Map<String, String> userInfo;
    private TextView companion_name,emergency_timer,tap_cancel,watch_over,end_trip;
    public static final boolean SERVERTRACE = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private FallDetectAlgo fallDetectAlgo;
    private boolean flag_fall = false,flag_buffer_ready = false,f_send_msg = true,permissionGranted, mBound = false;
    private MyReceiver myReceiver;
    private LocationUpdatesService mService = null;
    private RelativeLayout relativeLayout;
    private Map<String,String> emergencyContacts;;
    private CardView panicBtn,locationshare_btn;
    private AppCompatButton feeling_safe_btn;
    private List<CompanionInfo> companionInfoList;
    private  CountDownTimer countDownTimer;
    CompanionAdapter companionAdapter;
    //private BottomNavigationView bottomNavigationView;
    String request_status = "request_pending";
    DatabaseReference requestref;
    List<Place.Field> fields;
    View mapView;
    ContactListActivity contactListActivity;
    DatabaseReference userlocation,companionRequestRef,userRef,contactsRef,destinationRef,userloc;
    Map<String, String> myContacts = new HashMap<>(1);
    SharedPreferences sh;
    MediaPlayer player;
    SharedPreferences emergencyList;

    // Monitors the state of the connection to the service.
    public ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;

            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
       // sensorManager.unregisterListener(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(UserMapActivity.this)
                .unregisterOnSharedPreferenceChangeListener( this);
        super.onStop();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        Log.d("Thread", "Thread id: "+Thread.currentThread().getId());
        if (SERVERTRACE) enableStrictMode();
        myReceiver = new MyReceiver();
        contactListActivity = new ContactListActivity();
        mCompanionAddBtn = findViewById(R.id.add_companion);
//        companion_name=(TextView) findViewById(R.id.companionName);
        location_sharing_btn=findViewById(R.id.location_share_btn);
        mCompanionAddBtn.setOnClickListener(this::onClick);
        relativeLayout=findViewById(R.id.relative);
        mSettingIcon = findViewById(R.id.setting_icon);
        mSettingIcon.setOnClickListener(this::onClick);
        emergencyContacts=new HashMap<>();
       // bottomNavigationView=findViewById(R.id.bottomNavigationView);
        sh = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
        fallDetectAlgo = new FallDetectAlgo();
        fallDetectAlgo.setDaemon(true);
        fallDetectAlgo.start();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        feeling_safe_btn=findViewById(R.id.feeling_safet_btn);
        watch_over=findViewById(R.id.watch_over);
        end_trip=findViewById(R.id.end_trip);
        //sh.edit().clear().commit();
        mGps = findViewById(R.id.ic_gps);
        companionDisplay=findViewById(R.id.companionRecyclerView);
        companionDisplay.setLayoutManager(new LinearLayoutManager(UserMapActivity.this,LinearLayoutManager.HORIZONTAL,false));
        mGps.setOnClickListener(this::onClick);
        emergency_timer=findViewById(R.id.emergency_timer);
        alert_btn=findViewById(R.id.alert_btn);
        emergencyList=getSharedPreferences("accept_comapnion",Context.MODE_PRIVATE);
       // emergencyIcon=(ImageView) findViewById(R.id.emergency_icon);
        //emergencyIcon.setOnClickListener(this::onClick);
        panicBtn=(CardView) findViewById(R.id.sos_btn);
        locationshare_btn=(CardView) findViewById(R.id.cardView5);
        panicBtn.setOnClickListener(this);
        tap_cancel=findViewById(R.id.tap_cancel);
        companionRequestRef=FirebaseDatabase.getInstance().getReference().child("Companion Requests");
        contactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        destinationRef=FirebaseDatabase.getInstance().getReference("destination");
        userloc=FirebaseDatabase.getInstance().getReference("location");
        userRef=FirebaseDatabase.getInstance().getReference("Users");
        requestPermission();
        startService(new Intent(this, PowerButtonService.class));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSearchLocation = findViewById(R.id.search_location);
        mSearchLocation.setOnClickListener(this::onClick);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyB2HtjfqryePz3oi2QuOlI2OAhylzNYs_c");
        }
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        mSearchLocation.setFocusable(false);
        myContacts.put(Constants.KEY_CON1, sh.getString(Constants.KEY_CON1, ""));
        fallDetectAlgo = new FallDetectAlgo();
        fallDetectAlgo.setDaemon(true);
        fallDetectAlgo.start();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            FallDetector fallDetector = new FallDetector();
            fallDetector.execute();
        }


        init();
        companionRequest();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                inviteCompanionBox();
            }
        },100);

//

    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    protected void companionRequest() {
        FirebaseRecyclerOptions<CompanionInfo> options =
                new FirebaseRecyclerOptions.Builder<CompanionInfo>()
                        .setQuery(companionRequestRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()), CompanionInfo.class)
                        .build();
        final FirebaseRecyclerAdapter<CompanionInfo,RequestViewHolder> adapter=new FirebaseRecyclerAdapter<CompanionInfo, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder requestViewHolder, int position, @NonNull CompanionInfo companionInfo) {
                 String listUserId = getRef(position).getKey();
                Log.d(TAG, "onBindViewHolder: "+listUserId);
                DatabaseReference getTypeRef = getRef(position).child("requestType").getRef();
                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String type = dataSnapshot.getValue().toString();
                            Log.d(TAG, "onDataChange: "+type);
                            if(type.equals("received")){
                                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String sName;
                                        requestViewHolder.companion_request_btn.setVisibility(View.VISIBLE);
                                        requestViewHolder.companion_request_btn.setForeground(getDrawable(R.drawable.ic_baseline_person_add_alt_1_24));
                                        final String requestUsername=snapshot.child("Name").getValue().toString();
                                        final String number=snapshot.child("cell").getValue().toString();
                                        ContactListActivity contactListActivity=new ContactListActivity();
                                        Log.d(TAG, "onDataChange: "+requestUsername);
                                        String[] split=requestUsername.split(" ");
                                        if(split.length>1){
                                            sName=String.valueOf(split[0].charAt(0))+String.valueOf(split[1].charAt(0));
                                        }else{
                                            sName=String.valueOf(split[0].charAt(0));
                                        }
                                        FCMMessageReceiverService.getToken(FirebaseAuth.getInstance().getCurrentUser().getUid(),requestUsername,UserMapActivity.this,requestUsername+" has invited you to be their companion. Tap here to accept the request");
                                        requestViewHolder.name.setText(sName);
                                        requestViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(UserMapActivity.this
                                                ,R.style.BottomSheeDialogTheme);
                                                View bottomSheetView=LayoutInflater.from(getApplicationContext()).inflate(
                                                        R.layout.layout_bottom_sheet, findViewById(R.id.bottomSheetContainer)
                                                );
                                                TextView textView=bottomSheetView.findViewById(R.id.shortcutName);
                                                TextView mNumber=bottomSheetView.findViewById(R.id.number_companion);
                                                TextView invite=bottomSheetView.findViewById(R.id.textView);
                                                textView.setText(sName);
                                                mNumber.setText(number);
                                                invite.setText("Invite from "+requestUsername);
                                                bottomSheetView.findViewById(R.id.accept_btn).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        requestViewHolder.companion_request_btn.setForeground(getDrawable(R.drawable.ic_connected));

                                                        companionRequestRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(listUserId).child("requestType").setValue("lets_track").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                   //
                                                                    companionRequestRef.child(listUserId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("requestType").setValue("accepted");
                                                                    bottomSheetDialog.dismiss();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                                bottomSheetView.findViewById(R.id.message_btn).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        companionRequestRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(listUserId)
                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    companionRequestRef.child(listUserId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                               // Toast.makeText(UserMapActivity.this, "Contact Deleted",Toast.LENGTH_SHORT).show();
                                                                                bottomSheetDialog.dismiss();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                                bottomSheetDialog.setContentView(bottomSheetView);
                                                bottomSheetDialog.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else if(type.equals("sent")){
                                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String sName;
                                        requestViewHolder.companion_request_btn.setVisibility(View.VISIBLE);
                                        requestViewHolder.companion_request_btn.setForeground(getDrawable(R.drawable.ic_baseline_access_time_24));
                                        final String requestUsername=snapshot.child("Name").getValue().toString();
                                        final String number=snapshot.child("cell").getValue().toString();
                                        ContactListActivity contactListActivity=new ContactListActivity();

                                        //FCMMessageReceiverService.getToken(listUserId,requestUsername,UserMapActivity.this,requestUsername+" has invited you to be their comapnion");
                                        Log.d(TAG, "onDataChange: "+requestUsername);
                                        String[] split=requestUsername.split(" ");
                                        if(split.length>1){
                                            sName=String.valueOf(split[0].charAt(0))+String.valueOf(split[1].charAt(0));
                                        }else{
                                            sName=String.valueOf(split[0].charAt(0));
                                        }
                                        requestViewHolder.name.setText(sName);
                                        requestViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(UserMapActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                                                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(UserMapActivity.this
                                                        ,R.style.BottomSheeDialogTheme);
                                                View bottomSheetView=LayoutInflater.from(getApplicationContext()).inflate(
                                                        R.layout.layout_bottom_sheet_companionrequest, findViewById(R.id.bottomSheetContainer)
                                                );
                                                TextView textView=bottomSheetView.findViewById(R.id.shortcutName);
                                                TextView mNumber=bottomSheetView.findViewById(R.id.number_companion);
                                                TextView invite=bottomSheetView.findViewById(R.id.textView);
                                                textView.setText(sName);
                                                mNumber.setText(number);
                                                invite.setText(requestUsername);
                                                bottomSheetDialog.setContentView(bottomSheetView);
                                                bottomSheetDialog.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else if(type.equals("accepted")){
                                end_trip.setVisibility(View.VISIBLE);
                                isEmergency=true;
                                location_sharing_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(isClicked) {
                                            watch_over.setText("Watch over me");
                                            location_sharing_btn.setImageResource(R.drawable.ic_walk);
                                            location_sharing_btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(UserMapActivity.this,R.color.text_color)));
                                            mService.removeLocationUpdates();
                                        }else{

                                            // Initialize a new instance of LayoutInflater service
                                            LayoutInflater inflater = (LayoutInflater) UserMapActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);

                                            // Inflate the custom layout/view
                                            View customView = inflater.inflate(R.layout.popup_window,null);
                                            mRelativeLayout = (RelativeLayout) customView.findViewById(R.id.rl_custom_layout);
                                            TextView textView=customView.findViewById(R.id.tv);
                                            textView.setText(getResources().getString(R.string.stop_location_text));
                                            mPopupWindow = new PopupWindow(
                                                    customView,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                            );

                                            // Set an elevation value for popup window
                                            // Call requires API level 21
                                            if(Build.VERSION.SDK_INT>=21){
                                                mPopupWindow.setElevation(5.0f);
                                            }

                                            // Get a reference for the custom view close button
                                            ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                                            // Set a click listener for the popup window close button
                                            closeButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // Dismiss the popup window
                                                    mPopupWindow.dismiss();
                                                }
                                            });
                                            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,40);

                                            location_sharing_btn.setImageResource(R.drawable.ic_location_disabled);
                                            mService.requestLocationUpdates();
                                            location_sharing_btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_color)));
                                            watch_over.setText("Stop Sharing");
                                        }
                                        isClicked=!isClicked;
                                    }
                                });
                                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       // Log.d(TAG, "SenderId check: "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        updatedUserLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        String sName;
                                        requestViewHolder.companion_request_btn.setVisibility(View.VISIBLE);
                                        requestViewHolder.companion_request_btn.setForeground(getDrawable(R.drawable.ic_connected));
                                        final String requestUsername=snapshot.child("Name").getValue().toString();
                                        final String number=snapshot.child("cell").getValue().toString();
                                        Log.d("companions_id", ": "+listUserId);
                                        String[] userId=new String[]{listUserId};
                                        Log.d(TAG, "current id: "+FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        end_trip.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                 Dialog dialog=new Dialog(UserMapActivity.this);
                                                dialog.setCancelable(false);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog.setContentView(R.layout.dialog_custom);
                                                TextView cancel_btn = dialog.findViewById(R.id.cancel_btn);
                                                cancel_btn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();

                                                    }
                                                });
                                                TextView ok_btn = dialog.findViewById(R.id.ok_btn);
                                                TextView body_message = dialog.findViewById(R.id.body_message);
                                                body_message.setText("Are you sure you want to end the trip?");

                                                ok_btn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        for(String id:userId) {
                                                            FCMMessageReceiverService.getToken(id, requestUsername, UserMapActivity.this, "Your companion has ended the trip");
                                                        }
                                                        mService.removeLocationUpdates();

                                                        companionRequestRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()) {
                                                                    for (String id : userId) {
                                                                        FirebaseDatabase.getInstance().getReference().child("Companion Requests").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    userloc.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                destinationRef.child((FirebaseAuth.getInstance().getCurrentUser().getUid())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        emergencyList.edit().clear().apply();
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        });
                                                        end_trip.setVisibility(View.GONE);
                                                        dialog.dismiss();
                                                    }
                                                });
                                                dialog.show();
                                            }
                                        });
                                        FCMMessageReceiverService.getToken(FirebaseAuth.getInstance().getCurrentUser().getUid(),requestUsername,UserMapActivity.this,requestUsername+ " has accepted your request. ");
                                        ContactListActivity contactListActivity=new ContactListActivity();
                                        emergencyContacts.put(listUserId,requestUsername+"/"+number);
                                        SharedPreferences.Editor editor = emergencyList.edit();
                                        for (String s : emergencyContacts.keySet()) {
                                            editor.putString(s, emergencyContacts.get(s));
                                        }
                                        editor.commit();
                                        //FCMMessageReceiverService.getToken(listUserId,requestUsername,UserMapActivity.this,requestUsername+" is now your companion");
                                        Log.d(TAG, "onDataChange: "+requestUsername);
                                        String[] split=requestUsername.split(" ");
                                        if(split.length>1){
                                            sName=String.valueOf(split[0].charAt(0))+String.valueOf(split[1].charAt(0));
                                        }else{
                                            sName=String.valueOf(split[0].charAt(0));
                                        }
                                        requestViewHolder.name.setText(sName);
                                        requestViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(UserMapActivity.this
                                                        ,R.style.BottomSheeDialogTheme);
                                                View bottomSheetView=LayoutInflater.from(UserMapActivity.this).inflate(
                                                        R.layout.accept_bottom_sheet_dialog, findViewById(R.id.bottomSheetContainer)
                                                );
                                                TextView textView=bottomSheetView.findViewById(R.id.shortcutName);
                                                TextView mNumber=bottomSheetView.findViewById(R.id.number_companion);
                                                TextView invite=bottomSheetView.findViewById(R.id.textView);
                                                AppCompatButton removebtn=bottomSheetView.findViewById(R.id.appCompatButton);
                                                removebtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        companionRequestRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    bottomSheetDialog.dismiss();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                                textView.setText(sName);
                                                mNumber.setText(number);
                                                invite.setText(requestUsername);
                                                bottomSheetView.findViewById(R.id.message_btn).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Utilities.sendMessage(number,"Hello",UserMapActivity.this);
                                                    }
                                                });
                                                bottomSheetView.findViewById(R.id.call_btn).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Utilities.callNumber(number,UserMapActivity.this);
                                                    }
                                                });
                                                bottomSheetDialog.setContentView(bottomSheetView);
                                                bottomSheetDialog.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else if(type.equals("lets_track")){
                                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        updatedUserLocation(listUserId);
                                        Log.d(TAG, "SenderId check: "+listUserId);
                                        String sName;
                                        requestViewHolder.companion_request_btn.setVisibility(View.VISIBLE);
                                        requestViewHolder.companion_request_btn.setForeground(getDrawable(R.drawable.ic_connected));
                                        final String requestUsername=snapshot.child("Name").getValue().toString();
                                        final String number=snapshot.child("cell").getValue().toString();
                                        String[] split=requestUsername.split(" ");
                                        if(split.length>1){
                                            sName=String.valueOf(split[0].charAt(0))+String.valueOf(split[1].charAt(0));
                                        }else{
                                            sName=String.valueOf(split[0].charAt(0));
                                        }
                                        requestViewHolder.name.setText(sName);
                                        requestViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(UserMapActivity.this
                                                        ,R.style.BottomSheeDialogTheme);
                                                View bottomSheetView=LayoutInflater.from(UserMapActivity.this).inflate(
                                                        R.layout.accept_bottom_sheet_dialog, findViewById(R.id.bottomSheetContainer)
                                                );
                                                TextView textView=bottomSheetView.findViewById(R.id.shortcutName);
                                                TextView mNumber=bottomSheetView.findViewById(R.id.number_companion);
                                                TextView invite=bottomSheetView.findViewById(R.id.textView);
                                                textView.setText(sName);
                                                mNumber.setText(number);
                                                invite.setText(requestUsername);
                                                AppCompatButton removebtn=bottomSheetView.findViewById(R.id.appCompatButton);
                                                removebtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        companionRequestRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    bottomSheetDialog.dismiss();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                                bottomSheetView.findViewById(R.id.message_btn).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Utilities.sendMessage(number,"Hello",UserMapActivity.this);
                                                    }
                                                });

                                                bottomSheetView.findViewById(R.id.call_btn).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Utilities.callNumber(number,UserMapActivity.this);
                                                    }
                                                });
                                                bottomSheetDialog.setContentView(bottomSheetView);
                                                bottomSheetDialog.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.companion_items, parent, false);
                RequestViewHolder holder = new RequestViewHolder(view);
                return holder;
            }
        };
        companionDisplay.setAdapter(adapter);
        adapter.startListening();

    }
    public static class RequestViewHolder extends  RecyclerView.ViewHolder{
        private ImageView imageView;
        private FloatingActionButton companion_request_btn;
        private TextView name;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.add_companion);
            name=itemView.findViewById(R.id.companion_nickname);
            companion_request_btn=itemView.findViewById(R.id.companion_request);

        }
    }


    private void init(){
        companionInfoList=new ArrayList<>();
        Log.d(TAG, "check: "+sh.getString(Constants.KEY_CON1,null));

    }

    public void inviteCompanionBox(){
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) UserMapActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.invite_guardian,null);
        ConstraintLayout constraintLayout = (ConstraintLayout) customView.findViewById(R.id.invite_layout);
      //  TextView textView=customView.findViewById(R.id.tv);
        //textView.setText(getResources().getString(R.string.alert_mode_text));
        invitePopWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            invitePopWindow.setElevation(5.0f);
        }
        customView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invitePopWindow.dismiss();
            }
        });

        invitePopWindow.showAtLocation(constraintLayout, Gravity.BOTTOM,0,300);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ic_gps:
                getCurrentLocation();
                break;
            case R.id.setting_icon:
                startActivity(new Intent(UserMapActivity.this, Settings.class));
                break;
            case R.id.add_companion:
                Intent intent = new Intent(UserMapActivity.this, ContactListActivity.class);
                startActivityForResult(intent,0);
                break;
            case R.id.search_location:
                Intent inten = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(UserMapActivity.this);
                startActivityForResult(inten, AUTOCOMPLETE_REQUEST_CODE);
                break;
           /*case R.id.emergency_icon:
                startActivity(new Intent(UserMapActivity.this,AllEmergencyContacts.class));
                break;


            case R.id.add_companion:
                messageandCallDialog();
                break;

            */

            case R.id.sos_btn:

                if(isClicked){
                    alert_btn.setImageResource(R.drawable.alert_icon);
                    tap_cancel.setText("I need help!");
                    if(countDownTimer!=null) {
                        countDownTimer.cancel();
                    }
                    emergency_timer.setVisibility(View.GONE);
                }else {
                    //Toast.makeText(mService, "sos button clicked", Toast.LENGTH_SHORT).show();
                    alert_btn.setImageResource(0);
                    emergency_timer.setVisibility(View.VISIBLE);
                    countDownTimer=new CountDownTimer(5000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            emergency_timer.setText(millisUntilFinished / 1000 + "");
                            tap_cancel.setText("Tap to cancel");
                        }

                        public void onFinish() {
                            tap_cancel.setText("Alerting");
                            feeling_safe_btn.setVisibility(View.VISIBLE);
                            locationshare_btn.setVisibility(View.GONE);
                            panicBtn.setVisibility(View.GONE);
                            relativeLayout.setBackground(getResources().getDrawable(R.drawable.alert_btn));
                            mSearchLocation.setHint("Alert Mode");
                            mSearchLocation.setGravity(Gravity.CENTER);
                            // Initialize a new instance of LayoutInflater service
                            LayoutInflater inflater = (LayoutInflater) UserMapActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);

                            // Inflate the custom layout/view
                            View customView = inflater.inflate(R.layout.popup_window,null);
                            mRelativeLayout = (RelativeLayout) customView.findViewById(R.id.rl_custom_layout);
                            TextView textView=customView.findViewById(R.id.tv);
                            textView.setText(getResources().getString(R.string.alert_mode_text));
                            mPopupWindow = new PopupWindow(
                                    customView,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );

                            // Set an elevation value for popup window
                            // Call requires API level 21
                            if(Build.VERSION.SDK_INT>=21){
                                mPopupWindow.setElevation(5.0f);
                            }

                            // Get a reference for the custom view close button
                            ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                            // Set a click listener for the popup window close button
                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Dismiss the popup window
                                    mPopupWindow.dismiss();
                                }
                            });
                            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.TOP,0,350);

                            mService.requestLocationUpdates();
                            feeling_safe_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showDialog();
                                }
                            });
                            sendMessage();
                        }
                    };
                    countDownTimer.start();

                }
                isClicked=!isClicked;

                break;


        }
    }

    public void showDialog() {
        //body_message.setText("Hey "+", "+" has requested that you to be his companion. Keep an eye on her as she is on the move");
        // DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Request").child()
        final Dialog dialog=new Dialog(UserMapActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_custom);
        TextView cancel_btn = dialog.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        TextView ok_btn = dialog.findViewById(R.id.ok_btn);
        TextView body_message = dialog.findViewById(R.id.body_message);
        body_message.setText("Are you sure everything is okay?");
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, ?> allEntries = emergencyList.getAll();
                if(allEntries.size()>0) {
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        String[] nameandcontact = entry.getValue().toString().split("/");
                        String requestUsername = nameandcontact[0];
                        String phone = nameandcontact[1];
                        Log.d("valueshai", requestUsername + ":phone " + phone);
                        FCMMessageReceiverService.getToken(entry.getKey(),requestUsername,UserMapActivity.this,FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+" is safe now. Tap here to view his location.");
                    }
                }
                feeling_safe_btn.setVisibility(View.GONE);
                locationshare_btn.setVisibility(View.VISIBLE);
                panicBtn.setVisibility(View.VISIBLE);
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.bg_btn));
                mSearchLocation.setHint("Where are you going?");
                mSearchLocation.setGravity(Gravity.CENTER_VERTICAL);
                mService.removeLocationUpdates();
                Toast.makeText(UserMapActivity.this, "Location updates has been stopped!!", Toast.LENGTH_SHORT).show();
                tap_cancel.setText("I need help!");
                alert_btn.setImageResource(R.drawable.alert_icon);
                if(player!=null) {
                    player.stop();
                }
                mPopupWindow.dismiss();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void sendMessage() {
        AlertUtility alertUtility=new AlertUtility(UserMapActivity.this);
        alertUtility.new SendMessageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                geoLocate(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        else if (requestCode == 0) {
            Log.d(TAG, "onActivityResult: Helllo");
            /*
            if(data!=null){
                Map<String,String> companionList= (Map<String, String>) data.getSerializableExtra("object");
                for(Map.Entry<String, ?> entry:sh.getAll().entrySet()){
                    String fullname=entry.getKey();
                    Log.d(TAG, "onActivityResult:hai "+fullname);
                    String[] firstandlast=fullname.split(" ");
                    String name;
                    if(firstandlast.length>1)
                        name=String.valueOf(firstandlast[0].charAt(0)) + String.valueOf(firstandlast[1].charAt(0));
                    else
                        name=String.valueOf(firstandlast[0].charAt(0));
                    companionInfoList.add(new CompanionInfo(name,null));

                }
                companionAdapter=new CompanionAdapter(UserMapActivity.this,companionInfoList);
                companionDisplay.setLayoutManager(new LinearLayoutManager(UserMapActivity.this,LinearLayoutManager.HORIZONTAL,false));
                companionDisplay.setAdapter(companionAdapter);
            }

             */
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }
    public void geoLocate(String placeName) {
        mCompanionAddBtn.setVisibility(View.VISIBLE);
        List<Address> geoCodeMatches = null;
        String searchTxt = placeName;
        Geocoder geocoder = new Geocoder(UserMapActivity.this);
        try {
            geoCodeMatches = geocoder.getFromLocationName(searchTxt, 1);
            if (!geoCodeMatches.isEmpty()) {
                Address address = geoCodeMatches.get(0);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap hashMap=new HashMap();
                            hashMap.put("latitude",address.getLatitude());
                            hashMap.put("longitude",address.getLongitude());
                            FirebaseDatabase.getInstance().getReference("destination").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                       // moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 15f, address.getAddressLine(0));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()),15f));
                                        MarkerOptions options = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title(address.getAddressLine(0));
                                        options.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("destination_marker",120,180)));
                                        mMap.addMarker(options);
                                    }
                                }
                            });
                        }
                    }).start();



                    }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*
        try{
            boolean success=googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.mapstyle));
            if(!success){
                Log.e(TAG,"Style parsing failed");
            }
        }catch (Resources.NotFoundException e){
            Log.e(TAG, "Can't find style. Error ", e);
        }

         */
        if (permissionGranted) {
            if (userInfo != null) {
                FirebaseDatabase.getInstance().getReference("request_status").child(userInfo.get("senderId")).child(userInfo.get("receiverId")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String status = snapshot.child("status").getValue().toString();
                        if (status.equals("accepted")) {
                            if(userInfo.get("senderId").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                /*
                                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                                    FallDetector fallDetector = new FallDetector();
                                    fallDetector.execute();
                                }

                                 */

                            }
                           // updatedUserLocation();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            getCurrentLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            if (mapView != null &&
                    mapView.findViewById(Integer.parseInt("1")) != null) {
                // Get the button view
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                        locationButton.getLayoutParams();
                // position on right bottom
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 30);
            }
        }

    }

    protected void requestPermission() {
        String[] permissions = {Constants.FINE_LOCATION, Constants.COURSE_LOCATION,Constants.SEND_SMS,Constants.CALL_PHONE};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Constants.FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Constants.COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, Constants.LOCATION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, Constants.LOCATION_REQUEST_CODE);
        }

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Constants.SEND_SMS)==PackageManager.PERMISSION_GRANTED){
            permissionGranted=true;
        }else{
            ActivityCompat.requestPermissions(this, permissions,Constants.LOCATION_REQUEST_CODE);
        }
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Constants.CALL_PHONE)==PackageManager.PERMISSION_GRANTED){
            permissionGranted=true;
        }else{
            ActivityCompat.requestPermissions(this, permissions,Constants.LOCATION_REQUEST_CODE);
        }
        /*
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
        *
         */
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = false;
                            return;
                        }
                    }
                    permissionGranted = true;
                    initMap();
                }
            }
        }
    }

    private void getCurrentLocation() {
        try {
            if (permissionGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f, "MyLocation");
                            }
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), 15f));

                        } else {
                            Toast.makeText(UserMapActivity.this, "Unable to find location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {

        }
    }


    public void updatedUserLocation(String senderId) {
        DatabaseReference updateLocation = FirebaseDatabase.getInstance().getReference("location").child(senderId);
        // Log.d(TAG, "User id: "+userInfo.get("senderId"));
        final Double[] newLat = new Double[1];
        final Double[] newLong = new Double[1];

        updateLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (ActivityCompat.checkSelfPermission(UserMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.clear();
                    newLat[0] = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                    Log.d(TAG, "latitude: " + newLat[0]);
                    newLong[0] = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                    Log.d(TAG, "longitude: " + newLong[0]);
                    moveCamera(new LatLng(newLat[0], newLong[0]), 15f, "MyLocation");

                    FirebaseDatabase.getInstance().getReference("destination").child(senderId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                Double lat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                                Double lon = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                                MarkerOptions options = new MarkerOptions().position(new LatLng(lat, lon)).title("Destination Location");
                                options.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("destination_marker", 100, 150)));
                                mMap.addMarker(options);
                            }
                            //getRoutToMarker(new LatLng(newLat[0],newLong[0]),new LatLng(lat,lon));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        options.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("current_marker",100,150)));
        mMap.addMarker(options);

    }
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        sh.edit().clear().commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            userlocation= FirebaseDatabase.getInstance().getReference("location").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                //Toast.makeText(UserMapActivity.this, Utils.getLocationText(location),Toast.LENGTH_SHORT).show();
                HashMap hashMap=new HashMap();
                hashMap.put("latitude",String.valueOf(location.getLatitude()));
                hashMap.put("longitude",String.valueOf(location.getLongitude()));

                userlocation.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            //Log.d(TAG, "Updated");
                        }else{
                            task.getException().getMessage();
                        }
                    }
                });
            }
        }
    }



    class FallDetector extends AsyncTask<Void,Void,Void> implements SensorEventListener{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            flag_buffer_ready = fallDetectAlgo.get_buffer_ready();
            flag_fall = fallDetectAlgo.set_data(event); 
            if (flag_fall) {
                if (f_send_msg) {
                    f_send_msg = false;
                    dialogShow();
                }
            } else {
                f_send_msg = true;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
        public void dialogShow(){
            final Dialog dialog=new Dialog(UserMapActivity.this);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.falldetect_dialog);
            TextView textView=(TextView)dialog.findViewById(R.id.timerTextView);
            ProgressBar progressBar=(ProgressBar)dialog.findViewById(R.id.progressBar);
            CountDownTimer countDownTimer=new CountDownTimer(10000,1000){
                public void onTick(long millisUntilFinished) {
                    textView.setText(millisUntilFinished / 1000+" seconds left");
                    progressBar.setMax(10);
                    progressBar.setProgress((int) (millisUntilFinished / 1000));
                }

                public void onFinish() {

                        playTone();
                        sendMessage();
                 
                    textView.setText("SOS triggered!");
                    dialog.dismiss();
                    feeling_safe_btn.setVisibility(View.VISIBLE);
                    locationshare_btn.setVisibility(View.GONE);
                    panicBtn.setVisibility(View.GONE);
                    relativeLayout.setBackground(getResources().getDrawable(R.drawable.alert_btn));
                    mSearchLocation.setHint("Alert Mode");
                    mSearchLocation.setGravity(Gravity.CENTER);
                    // Initialize a new instance of LayoutInflater service
                    LayoutInflater inflater = (LayoutInflater) UserMapActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);

                    // Inflate the custom layout/view
                    View customView = inflater.inflate(R.layout.popup_window,null);
                    mRelativeLayout = (RelativeLayout) customView.findViewById(R.id.rl_custom_layout);
                    TextView textView=customView.findViewById(R.id.tv);
                    textView.setText(getResources().getString(R.string.alert_mode_text));
                    mPopupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

                    // Set an elevation value for popup window
                    // Call requires API level 21
                    if(Build.VERSION.SDK_INT>=21){
                        mPopupWindow.setElevation(5.0f);
                    }

                    // Get a reference for the custom view close button
                    ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                    // Set a click listener for the popup window close button
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Dismiss the popup window
                            mPopupWindow.dismiss();
                        }
                    });
                    mPopupWindow.showAtLocation(mRelativeLayout, Gravity.TOP,0,350);

                    mService.requestLocationUpdates();
                    feeling_safe_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog();
                        }
                    });

                }
            }.start();
            progressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    countDownTimer.cancel();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        public void playTone() {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    AssetFileDescriptor afd = null;
                    try {
                        afd = getAssets().openFd("police_siren.mp3");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player = new MediaPlayer();
                    try {
                        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    player.start();
                    player.setLooping(true);

                    try {
                        Thread.sleep(10000);
                       // player.stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //      player.stop();
                }
            });
            t.start();
        }
    }
/*
    public void send_sms() {
        System.out.println("----------------------- SEND SMS");
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("9849321710", null, "code 0000 Fall detected!", null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faied, please try again later!",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }
*/
    public void enableStrictMode() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }


}