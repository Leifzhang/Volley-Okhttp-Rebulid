package com.leif.baseapi;

import com.kronos.volley.toolbox.BaseApiParser;

/**
 * Created by zhangyang on 16/1/28.
 */
public class StringApiParser implements BaseApiParser {
    @Override
    public Object parse(String content) throws Exception {
        return content;
    }
}
