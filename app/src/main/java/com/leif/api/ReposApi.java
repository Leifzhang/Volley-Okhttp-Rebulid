package com.leif.api;

import com.kronos.volley.toolbox.BaseApiParser;
import com.leif.baseapi.CustomApi;
import com.leif.baseapi.CustomGsonApiListParser;
import com.leif.baseapi.ResponseListener;
import com.leif.moudle.ReposEntity;

/**
 * Created by zhangyang on 16/6/14.
 */
public class ReposApi extends CustomApi {
    public ReposApi(ResponseListener responseListener) {
        super(responseListener);
    }

    @Override
    public String getUrl() {
        return "https://api.github.com/orgs/octokit/repos";
    }

    @Override
    public BaseApiParser getParser() {
        return new CustomGsonApiListParser(ReposEntity.class);
    }
}
