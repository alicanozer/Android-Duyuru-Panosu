package com.ozer.duyurupanosu;

import android.app.Application;
import android.content.Context;

import com.apigee.sdk.data.client.ApigeeDataClient;

/**
 * Created by Can on 29.01.2015.
 */
public class ApplicationPano extends Application {
    private static final String USERGRID_SERVER_URL = "https://api.usergrid.com";
    private static final String ORGNAME = "ozer";
    private static final String APPNAME = "sandbox";
    private static ApigeeDataClient mDataClient;
    private String token;

    public void initClient(Context _context) {
        this.mDataClient = new ApigeeDataClient(ORGNAME,
                APPNAME, USERGRID_SERVER_URL, _context);
    }

    public ApigeeDataClient getClient() {
        if (null != this.mDataClient) {
            return this.mDataClient;
        }
        return null;
    }
}
