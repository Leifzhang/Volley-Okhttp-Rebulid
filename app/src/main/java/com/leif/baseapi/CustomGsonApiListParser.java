package com.leif.baseapi;

import com.alibaba.fastjson.JSON;
import com.kronos.volley.toolbox.BaseApiParser;

/**
 * Created by zhangyang on 16/4/21.
 */
public class CustomGsonApiListParser implements BaseApiParser {
    private Class mClass;

    public CustomGsonApiListParser(Class mClass) {
        this.mClass = mClass;
    }

    @Override
    public Object parse(String content) throws Exception {
        return JSON.parseArray(content, mClass);
    }


}
