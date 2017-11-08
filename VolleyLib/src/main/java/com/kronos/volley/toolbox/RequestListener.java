package com.kronos.volley.toolbox;


import com.kronos.volley.Response;

public interface RequestListener<T> extends Response.Listener<T>, Response.ErrorListener {
}
