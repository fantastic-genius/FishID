package com.example.android.fishid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;

import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static com.example.android.fishid.Utility.getCameraInstance;

public class MainActivity extends AppCompatActivity implements Runnable,View.OnClickListener{
    private Camera mCamera;
    private CameraPreview mPreview;
    private ImageView imageView;
    private TextView textView;
    FrameLayout preview;
    ProgressBar progressBar;
    ProgressBar progressBar2;
    FrameLayout topPanel;
    Button sendButton;
    double lat;
    double lon;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {



            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                imageView.setImageBitmap(bitmap);
                preview.removeView(mPreview);
                preview.addView(imageView);
                if(textView.getVisibility()==View.VISIBLE)
                    sendButton.setEnabled(true);


            } catch (Exception e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            }
        }
    };
    private LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + location.getLatitude() + " Lng: "
                            + location.getLongitude(), Toast.LENGTH_SHORT).show();
                lat=location.getLatitude();
                lon=location.getLongitude();
               textView.setText(String.format("Current Location: Latitude -%d ,  Longitude % d",location.getLatitude(),location.getLongitude()));
               progressBar.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            if(imageView.getDrawable()!=null)
                sendButton.setEnabled(true);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        topPanel=(FrameLayout) findViewById(R.id.top);
        imageView=new ImageView(this);
        progressBar2=new ProgressBar((this));
        progressBar2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView=(TextView) findViewById(R.id.gps_data);
        progressBar=(ProgressBar) findViewById(R.id.progress);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        else{
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        Button captureButton = (Button) findViewById(R.id.capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
        sendButton=(Button) findViewById(R.id.send);
        sendButton.setOnClickListener(this
        );

    }

    @Override
    public void run() {
        topPanel.addView(progressBar2,0);
        BitmapDrawable drawable=(BitmapDrawable)imageView.getDrawable();
        Bitmap bmap = drawable.getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
        byte[] bb = bos.toByteArray();
        String image = Base64.encodeToString(bb,0);
        RequestParams requestParams=new RequestParams();
        requestParams.add("image",image);
        requestParams.add("lat",String.valueOf(lat));
        requestParams.add("long",String.valueOf(lon));
        Utility.postByUrl("", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("asd", "---------------- this is response : " + response);
                try {
                    JSONObject serverResp = new JSONObject(response.toString());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(
                        getBaseContext(),
                        errorResponse.toString(), Toast.LENGTH_SHORT).show();
            }

        });


    }

    @Override
    public void onClick(View view) {
        Thread thread=new Thread(this);
        thread.start();

    }
}
