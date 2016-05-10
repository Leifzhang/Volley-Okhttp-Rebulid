package com.leif.baseapi;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyang on 16/4/21.
 */
public class CustomGsonApiListParser extends BaseApiParser {
    private Class mClass;

    public CustomGsonApiListParser(Class mClass) {
        this.mClass = mClass;
    }

    @Override
    public Object parse(String content) throws Exception {
        Gson gson = new Gson();
        List<Object> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(content);
        for (int i = 0; i < jsonArray.length(); i++) {
            Object model = gson.fromJson(jsonArray.optJSONObject(i).toString(), mClass);
            list.add(model);
        }
        return list;
    }


}
