package com.example.theprotector;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharedPreference {
    Context context;
    public SharedPreference(Context context){
        this.context=context;
    }

    public static SharedPreferences getInstance(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constants.preferenceName,Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static Map<String, ?> getData(Context context){
        return getInstance(context).getAll();
    }
}
