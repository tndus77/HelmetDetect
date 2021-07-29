package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;


public class CameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;

    private Button btnTakePhoto;
    private Uri myUri;
    private Bitmap imageBitmap;

    public static final int MEDIA_TYPE_IMAGE = 2;

    private Camera.PictureCallback mPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("GAEUN LOG::", "onCreate");
        // Check permission

        setContentView(R.layout.activity_camera);
        // Check if this device has a camera
        checkCameraHardware(this);
        Log.e("GAEUN LOG::", "Check over");

        // Create an instance of Camera
        mCamera = getCameraInstance(this);
        Log.e("GAEUN LOG::", "got Camera");

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Get user's selfi
        Camera.PictureCallback mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, getApplicationContext());
                if (pictureFile == null){
                    Log.d(TAG, "Error creating media file, check storage permissions");
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    // Convert File to Bitmap
                    String filePath = pictureFile.getPath();
                    imageBitmap = BitmapFactory.decodeFile(filePath);
                    // Here, we have myBitmap;
                    Log.e("GAEUN LOG::", "Bitmap here!! " + imageBitmap);

                    float[][][][] input = new float[1][224][224][3];
                    float[][] output = new float[1][2];

                    int batchNum = 0;
                    //InputStream buf = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = imageBitmap;
                    //buf.close();

                    // x,y 최댓값 사진 크기에 따라 달라짐 (조절 해줘야함)
                    for (int x = 0; x < bitmap.getWidth(); x++) {
                        for (int y = 0; y < bitmap.getHeight(); y++) {
                            int pixel = bitmap.getPixel(x, y);
                            input[batchNum][x][y][0] = Color.red(pixel) / 1.0f;
                            input[batchNum][x][y][1] = Color.green(pixel) / 1.0f;
                            input[batchNum][x][y][2] = Color.blue(pixel) / 1.0f;
                        }
                    }

                    // 자신의 tflite 이름 써주기
                    Interpreter lite = getTfliteInterpreter("converted_model.tflite");
                    lite.run(input, output);

                    TextView tv_output = findViewById(R.id.textView2);
                    Log.e("LKE LOG", "<1>");

                    // 텍스트뷰에 무슨 버섯인지 띄우기 but error남 ㅜㅜ 붉은 사슴뿔만 주구장창
                    if (output[0][0] * 100 > 80) {
                        Log.e("LKE LOG", "<2>");
                        tv_output.setText(String.format("with_helmet, %.5f", output[0][0] * 100));
                        Intent intent = new Intent(getApplicationContext(), Authenticated.class);
                        startActivity(intent);
                    } else {
                        Log.d("without_helmet", "without_helmet");
                        tv_output.setText(String.format("without_helmet,  %.5f", output[0][1] * 100));
                    }

                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
            }
        };

        btnTakePhoto = findViewById(R.id.button_capture);
        btnTakePhoto.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                // get an image from the camera
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(Context context){
        Camera c = null;
        try {
            while(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED){
            }
            c = Camera.open(1); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e("GAEUN LOG::", "Camera is not available");
        }
        return c; // returns null if camera is unavailable
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type, Context context){
        return Uri.fromFile(getOutputMediaFile(type, context));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type, Context context){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
            String filePath = mediaFile.getPath();

        } else {
            return null;
        }

        return mediaFile;
    }

    private Bitmap uriToBitmap(Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPause() {
        Log.e("GAEUN LOG::", "On Puase");
        super.onPause();
        //while(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
        //== PackageManager.PERMISSION_GRANTED){
        //releaseCamera();
        //}
        //permission.requestPermission();
        //releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(CameraActivity.this, modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

}
