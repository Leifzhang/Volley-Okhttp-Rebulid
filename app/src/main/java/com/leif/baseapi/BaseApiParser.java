package com.leif.baseapi;


public abstract class BaseApiParser implements com.kronos.volley.toolbox.BaseApiParser {

    public abstract Object parse(String content) throws Exception;

}
