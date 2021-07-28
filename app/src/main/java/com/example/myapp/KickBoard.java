package com.example.myapp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class KickBoard extends Activity {

    TextView appname, num, per, add, percent, thousand, ten;
    Button btn1;
    ImageView imageView2;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.test);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        percent = (TextView) findViewById(R.id.percent);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView2.setImageResource(R.drawable.kickkick);

        //String[] battery = new String[]{"36%", "89%", "94%", "38%", "79%", "72%", "68%", "86%", "35%", "76%"};
        percent.setText("73%");
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //카메라랑 연결되도록 만들기
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
                Log.e("GAEUN LOG:: ", "clicked btn1");
                finish();
            }
        });
    }

}
