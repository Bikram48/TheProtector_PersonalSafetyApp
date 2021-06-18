package com.example.theprotector.SingletonPattern;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueInstance {
    private static RequestQueueInstance requestQueueInstance;
    private static Context context;
    private static RequestQueue requestQueue;
    public RequestQueueInstance(Context context){
        this.context=context;
        requestQueue=getRequestQueue();
    }

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public static RequestQueueInstance getInstance(Context context){
        if(requestQueueInstance==null){
            requestQueueInstance=new RequestQueueInstance(context);
        }
        return requestQueueInstance;
    }

    public void addRequest(Request request){
        requestQueue.add(request);
    }
}
