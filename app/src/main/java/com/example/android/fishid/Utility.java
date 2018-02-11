package com.example.android.fishid;

import android.hardware.Camera;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


/**
 * Created by olamide on 2/11/2018.
 */

public class Utility {
    private static final String BASE_URL = "http://api.twitter.com/1/";
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }
}
