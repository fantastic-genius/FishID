package com.example.android.fishid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button mCapture;
    private ImageView mCapturedImage;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    // Create a file uri to save the image
    private Uri imageFileUri;

    private File imageFile;


    private static final String dirName = "My FishID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCapture = (Button) findViewById(R.id.capture);

        mCapturedImage = (ImageView) findViewById((R.id.captured_image));

        mCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(view);
            }
        });



    }

    private boolean checkHasCamera(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }else {
            return false;
        }

    }

    /**
     * Create intent to use the camera and take picture
     * @param view
     */
    private void takePicture(View view){
        checkHasCamera(getApplicationContext());
        imageFile = getOutputMediaFile();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        imageFileUri = Uri.fromFile(imageFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                imageFileUri = data.getData();
                //Bitmap capturedImage = BitmapFactory.decodeFile(String.valueOf(imageFile));
                //mCapturedImage.setImageBitmap(capturedImage);
            }
        }
    }

    /**
     * create a File for saving an image
     * @return file
     */
    private File getOutputMediaFile(){

        // check that the SDCard is mounted before doing this.
        if(isExternalStorageAvailable()) {

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), dirName);


            //Create the image directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(dirName, "failed to create" + dirName + " directory");
                    return null;
                }
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            File mediaFile = new File(mediaStorageDir.getPath() + File.pathSeparator
                    + "IMG_" + timestamp + ".jpg");
            return mediaFile;
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
