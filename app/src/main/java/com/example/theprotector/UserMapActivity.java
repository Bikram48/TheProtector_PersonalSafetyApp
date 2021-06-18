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
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.theprotector.Utility.AlertUtility;
import com.example.theprotector.Utility.DialogBoxes;
import com.example.theprotector.Utility.Utils;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,View.OnClickListener,  SharedPreferences.OnSharedPreferenceChangeListener {
    private ImageView mSettingIcon,mGps,triangle,emergencyIcon,companion_circle;;
    private String TAG = "MapDemo";
    private static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AutoCompleteTextView mSearchLocation;
    private FloatingActionButton panicBtn;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private AppCompatButton mCompanionAddBtn;
    private Map<String, String> userInfo;
    private TextView companion_name;
    public static final boolean SERVERTRACE = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private FallDetectAlgo fallDetectAlgo;
    private boolean flag_fall = false,flag_buffer_ready = false,f_send_msg = true,permissionGranted, mBound = false;
    private MyReceiver myReceiver;
    private LocationUpdatesService mService = null;
    //private BottomNavigationView bottomNavigationView;
    String request_status = "request_pending";
    DatabaseReference requestref;
    List<Place.Field> fields;
    View mapView;
    ContactListActivity contactListActivity;
    DatabaseReference userlocation;
    DialogBoxes dialogBoxes;
    Map<String, String> myContacts = new HashMap<>(1);
    SharedPreferences sh;

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
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

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
        companion_name=(TextView) findViewById(R.id.companionName);
        companion_circle=(ImageView) findViewById(R.id.display_companion);
        companion_circle.setOnClickListener(this);
        mCompanionAddBtn.setOnClickListener(this::onClick);
        mSettingIcon = findViewById(R.id.setting_icon);
        mSettingIcon.setOnClickListener(this::onClick);
       // bottomNavigationView=findViewById(R.id.bottomNavigationView);
        sh = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
        fallDetectAlgo = new FallDetectAlgo();
        fallDetectAlgo.setDaemon(true);
        fallDetectAlgo.start();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //sh.edit().clear().commit();
        mGps = findViewById(R.id.ic_gps);
        mGps.setOnClickListener(this::onClick);
        emergencyIcon=(ImageView) findViewById(R.id.emergency_icon);
        emergencyIcon.setOnClickListener(this::onClick);
        panicBtn=(FloatingActionButton) findViewById(R.id.panicBtn);
        panicBtn.setOnClickListener(this);
        triangle = findViewById(R.id.triangle);
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
        dialogBoxes=new DialogBoxes(this);
        myContacts.put(Constants.KEY_CON1, sh.getString(Constants.KEY_CON1, ""));
        fallDetectAlgo = new FallDetectAlgo();
        fallDetectAlgo.setDaemon(true);
        fallDetectAlgo.start();
        init();

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
                startActivity(intent);
                break;
            case R.id.search_location:
                Intent inten = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(UserMapActivity.this);
                startActivityForResult(inten, AUTOCOMPLETE_REQUEST_CODE);
                break;
            case R.id.emergency_icon:
                startActivity(new Intent(UserMapActivity.this,AllEmergencyContacts.class));
                break;
            case R.id.display_companion:
                messageandCallDialog();
                break;
            case R.id.panicBtn:
                sendMessage();
                break;
        }
    }
    public void messageandCallDialog() {
        final Dialog dialog=new Dialog(UserMapActivity.this);
        //body_message.setText("Hey "+", "+" has requested that you to be his companion. Keep an eye on her as she is on the move");
        // DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Request").child()

        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.callandmessagedialog);
        TextView cancel_btn=dialog.findViewById(R.id.cancel_txt);
        TextView call_btn=dialog.findViewById(R.id.call_text);
        String nameandnumber=sh.getString(Constants.KEY_CON1,null);
        String[] fullname=nameandnumber.split("/");
        call_btn.setText("Call "+fullname[0]);
        TextView message_btn=dialog.findViewById(R.id.message_txt);
        message_btn.setText("Message "+fullname[0]);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked"+fullname[1]);
                Intent intent=new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+fullname[1]));
                startActivity(intent);
            }
        });
        dialog.show();
    }
    private void sendMessage() {
        AlertUtility alertUtility=new AlertUtility(UserMapActivity.this);
        alertUtility.new SendMessageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    private void init(){
        /*
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.emergency:
                        startActivity(new Intent(UserMapActivity.this,AllEmergencyContacts.class));
                        break;
                }
                return true;
            }
        });

         */

        Log.d(TAG, "check: "+sh.getString(Constants.KEY_CON1,null));
        if(sh.getString(Constants.KEY_CON1,null)!=null){
            companion_circle.setVisibility(View.VISIBLE);
            companion_name.setVisibility(View.VISIBLE);
            String nameandnumber=sh.getString(Constants.KEY_CON1,null);
            String[] fullname=nameandnumber.split("/");
            String[] firstandlast=fullname[0].split(" ");
            Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Companion request has been sent to "+firstandlast[0]+" successfully", Snackbar.LENGTH_LONG);
            snackBar.show();
            Log.d(TAG, "init: "+sh.getAll());

            if(firstandlast.length>1)
                companion_name.setText(String.valueOf(firstandlast[0].charAt(0)) + String.valueOf(firstandlast[1].charAt(0)));
            else
                companion_name.setText(String.valueOf(firstandlast[0].charAt(0)));
        }
        if (getIntent().getStringExtra("from") != null) {
            requestref = FirebaseDatabase.getInstance().getReference("request_status");
            if (getIntent().getStringExtra("from").equals("notification")) {
                userInfo = (Map<String, String>) getIntent().getSerializableExtra("userinfo");
                updateRequest();
                // Log.d(TAG, "userinfo: "+userInfo.get("senderUsername"));
                dialogBoxes.showDialog(userInfo);
            }
        }
        if (getIntent().getStringExtra("status") != null) {
            if (getIntent().getStringExtra("status").equals("accepted")) {
                userInfo = (Map<String, String>) getIntent().getSerializableExtra("userinfo");
               // gps = new GPSTracker(UserMapActivity.this, userInfo);
                acceptedRequest();
                Log.d(TAG, "userinfo: " + userInfo.get("senderUsername"));
                dialogBoxes.showAcceptedDialog(userInfo);
            }
        }
        if (getIntent().getStringExtra("status") != null) {
            if (getIntent().getStringExtra("status").equals("declined")) {
                userInfo = (Map<String, String>) getIntent().getSerializableExtra("userinfo");
                dialogBoxes.showCancelledDialog(userInfo);
            }
        }
    }

    private void acceptedRequest(){
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



    private void updateRequest() {
        HashMap hashMap = new HashMap();
        hashMap.put("status", "pending");
        if (request_status.equals("request_pending")) {
            requestref.child(userInfo.get("senderId")).child(userInfo.get("receiverId")).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        request_status = "pending";
                    } else {
                        task.getException().getMessage();
                    }
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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


    }

    public void geoLocate(String placeName) {
        mCompanionAddBtn.setVisibility(View.VISIBLE);
        triangle.setVisibility(View.VISIBLE);
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
                                        moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 15f, address.getAddressLine(0));
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
                                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                                    FallDetector fallDetector = new FallDetector();
                                    fallDetector.execute();
                                }
                                mService.requestLocationUpdates();
                            }
                            updatedUserLocation();
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[]
                                                   grantResults) {
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


    public void updatedUserLocation() {
        DatabaseReference updateLocation = FirebaseDatabase.getInstance().getReference("location").child(userInfo.get("senderId"));
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

                    FirebaseDatabase.getInstance().getReference("destination").child(userInfo.get("senderId")).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Double lat=Double.parseDouble(snapshot.child("latitude").getValue().toString());
                            Double lon=Double.parseDouble(snapshot.child("longitude").getValue().toString());
                            MarkerOptions options = new MarkerOptions().position(new LatLng(lat,lon)).title("Destination Location");
                            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationmarker));
                            mMap.addMarker(options);
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

    public static void moveCamera(LatLng latLng, float zoom, String title) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationmarker));
        mMap.addMarker(options);

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
            userlocation= FirebaseDatabase.getInstance().getReference("location").child(userInfo.get("senderId"));
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(UserMapActivity.this, Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();
                HashMap hashMap=new HashMap();
                hashMap.put("latitude",String.valueOf(location.getLatitude()));
                hashMap.put("longitude",String.valueOf(location.getLongitude()));

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
            // display if buffer is ready
            flag_buffer_ready = fallDetectAlgo.get_buffer_ready();
            if (flag_buffer_ready) {
                //button.setText("BUFFER OK");
            } else {
                //button.setText("NO BUFFER");
            }
            // store values in buffer and visualize fall
            flag_fall = fallDetectAlgo.set_data(event); // event has values minus gravity
            if (flag_fall) {
                if (f_send_msg) {
                    f_send_msg = false;

                    playTone();
                    showDialog();
                    //send_sms();
                }

                // image.setVisibility(View.VISIBLE);
            } else {
                f_send_msg = true;
                //image.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
        public void showDialog(){
            final Dialog dialog=new Dialog(UserMapActivity.this);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.falldetect_dialog);
            TextView textView=(TextView)dialog.findViewById(R.id.timerTextView);
            ProgressBar progressBar=(ProgressBar)dialog.findViewById(R.id.progressBar);
            new CountDownTimer(10000,1000){
                public void onTick(long millisUntilFinished) {
                    textView.setText(millisUntilFinished / 1000+" seconds left");
                    progressBar.setMax(10);
                    progressBar.setProgress((int) (millisUntilFinished / 1000));
                }

                public void onFinish() {
                    if (f_send_msg) {
                        f_send_msg = false;
                        //playTone();
                        //send_sms();
                    }
                    textView.setText("SOS triggered!");
                    dialog.dismiss();
                }
            }.start();
            progressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        public void playTone() {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        }
    }

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

    public void enableStrictMode() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }
    
}