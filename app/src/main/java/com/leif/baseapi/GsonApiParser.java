package com.leif.baseapi;

import com.google.gson.Gson;

/**
 * Created by zhangyang on 16/1/28.
 */
public class GsonApiParser extends BaseApiParser {
    private Class mClass;

    public GsonApiParser(Class mClass) {
        this.mClass = mClass;
    }

    @Override
    public Object parse(String content) throws Exception {
        Gson gson = new Gson();
        Object model = gson.fromJson(content, mClass);
        return model;
    }
}
