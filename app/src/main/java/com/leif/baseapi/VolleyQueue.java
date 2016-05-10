package com.leif.baseapi;

import com.kronos.volley.Request;
import com.kronos.volley.RequestQueue;
import com.kronos.volley.toolbox.Volley;
import com.leif.example.AppContext;

/**
 * Created by zhangyang on 16/5/3.
 */
public class VolleyQueue {

    private static VolleyQueue instance;
    private RequestQueue requestQueue;


    public void addRequest(Request request) {
        if (request != null)
            requestQueue.add(request);
    }

    public void clearRequest() {
        requestQueue.cancelAll(new Object());
    }

    public static VolleyQueue getInstance() {
        if (instance == null) {
            synchronized (Volley.class) {
                if (instance == null) {
                    instance = new VolleyQueue();
                }
            }
        }
        return instance;
    }

    private VolleyQueue() {
        requestQueue = Volley.newRequestQueue(AppContext.getApplication());
    }

}
