package com.example.theprotector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Map;
/*
public class FallDetector extends Service implements SensorEventListener {


    Sensor sensor,sensor_main;
    double  gravity=0;
    double sum,check_sum,value;
    boolean min,max,flag;
    int i,count;
    SensorManager sm;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        count=0;
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor_main=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Log.d("detect", "onStartCommand: ");
        sm.registerListener((SensorEventListener) this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            detectFall(sensorEvent);
        }
    }

    private void detectFall(SensorEvent event) {
        sum = Math.round(Math.sqrt(Math.pow(event.values[0], 2)
                + Math.pow(event.values[1], 2)
                + Math.pow(event.values[2], 2)));
        // total.setText("Total::" + sum);
        // svalue.setText("Standard Gravity::" + sm.STANDARD_GRAVITY);

        if (sum <= 5.0) {
            min = true;

        }

        if (min == true) {
            i++;
            if (sum >= 16.5) {
                max = true;
            }
        }

        if (min == true && max == true) {
            sm.unregisterListener(this);
            Toast.makeText(this, "Suspected Fall", Toast.LENGTH_SHORT).show();
            Log.d("detect", "detectFall: suspected fall");
            // Intent test= new Intent(UserMapActivity.this,Fall_test.class);
            //startActivityForResult(test,2);

            min = false;
            max = false;

            showDialog();
            if (count > 45) {
                Toast.makeText(this,"Fall Confirmed",Toast.LENGTH_LONG).show();
                Log.d("detect", "detectFall: fallconfirmed");
                i = 0;
                count=0;
                min = false;
                max = false;

            }
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test dialog");
        builder.setMessage("Content");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Do something
                dialog.dismiss();
            }
            });


            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alert.show();

        }

}

 */
