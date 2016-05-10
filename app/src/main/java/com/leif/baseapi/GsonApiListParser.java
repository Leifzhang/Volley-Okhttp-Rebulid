package com.leif.baseapi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wscn20151202 on 16/2/1.
 */
public class GsonApiListParser<T> extends BaseApiParser {

    public TypeToken typeToken;
    public GsonApiListParser(TypeToken typeToken){ this.typeToken = typeToken;}
    @Override
    public Object parse(String content) throws Exception {

        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(content, typeToken.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
