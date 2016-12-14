package com.kronos.volley.toolbox;

import com.kronos.volley.ParseError;

/**
 * Created by zhangyang on 16/5/3.
 */
public interface BaseApiParser {

    Object parse(String content) throws ParseError;
}
