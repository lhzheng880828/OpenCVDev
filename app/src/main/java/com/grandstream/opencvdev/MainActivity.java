package com.grandstream.opencvdev;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        Button DOG = (Button) findViewById(R.id.DoG);
        //tv.setText(stringFromJNI());
        DOG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.DOG);
                startActivity(intent);
            }
        });


        Button bMean = (Button) findViewById(R.id.bMean);
        bMean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.MEAN_BLUR);
                startActivity(intent);
            }
        });

        Button gaussian = (Button) findViewById(R.id.bGaussian);
        gaussian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.GAUSSIAN_BLUR);
                startActivity(intent);
            }
        });

        Button bsharpen = (Button) findViewById(R.id.bSharpen);
        bsharpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.SHARPEN);
                startActivity(intent);
            }
        });

        Button bDilate = (Button) findViewById(R.id.bDilate);
        bDilate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.DILATE);
                startActivity(intent);
            }
        });


        Button bErote = (Button) findViewById(R.id.bErode);
        bErote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.EROTE);
                startActivity(intent);
            }
        });


        Button bThreshold = (Button) findViewById(R.id.bThreshold);
        bThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.THRESHOLD);
                startActivity(intent);
            }
        });

        Button bCanny = (Button)findViewById(R.id.bCanny);
        bCanny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.CANNY);
                startActivity(intent);
            }
        });

        Button bHarris = (Button)findViewById(R.id.bHarrisCorner);
        bHarris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.HARRIS);
                startActivity(intent);
            }
        });

        Button bHoughLines = (Button)findViewById(R.id.bHoughLines);
        bHoughLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.HOUGHLines);
                startActivity(intent);
            }
        });

        Button bHoughCircle = (Button)findViewById(R.id.bHoughCircle);
        bHoughCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);

                intent.putExtra("ACTION_MODE", Contract.HOUGHCircle);
                startActivity(intent);
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");




        }
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
}
