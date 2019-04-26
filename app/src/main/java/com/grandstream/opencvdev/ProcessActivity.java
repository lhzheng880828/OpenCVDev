package com.grandstream.opencvdev;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

/**
 * Author:linhu
 * Email:lhzheng@grandstream.cn
 * Date:18-10-9
 */
public class ProcessActivity extends AppCompatActivity {

    private static final String TAG = ProcessActivity.class.getSimpleName();

    private final int SELECT_PHOTO = 1;

    private ImageView ivImage, ivImageProcessed;

    Mat src;

    Mat originalMat;

    Bitmap currentBitmap;

    static int ACTION_MODE  = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.process_layout);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        ivImageProcessed = (ImageView)findViewById(R.id.ivImageProcessed);

        Intent intent = getIntent();

        if(intent.hasExtra("ACTION_MODE")){
            ACTION_MODE = intent.getIntExtra("ACTION_MODE", 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_load_image){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, REQ_PHOTO_CODE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static final int REQ_PHOTO_CODE = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_PHOTO_CODE:
                if(resultCode == RESULT_OK){

                    try {
                        final Uri imageUri = data.getData();

                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;

                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream, null, options);

                        src = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);

                        Utils.bitmapToMat(selectedImage, src);

                        Bitmap tmpBitmap = selectedImage.copy(Bitmap.Config.ARGB_8888, true);
                        originalMat = new Mat(tmpBitmap.getHeight(), tmpBitmap.getWidth(), CvType.CV_8U);
                        Utils.bitmapToMat(tmpBitmap, originalMat);

                        currentBitmap = selectedImage.copy(Bitmap.Config.ARGB_8888, false);

                        Log.d(TAG,"ACTION_MODE = "+ACTION_MODE);
                        switch (ACTION_MODE){

                            case Contract.MEAN_BLUR:
                                Imgproc.blur(src, src, new Size(3, 3));

                                break;

                            case Contract.GAUSSIAN_BLUR:
                                Imgproc.GaussianBlur(src, src, new Size(3, 3), 0);
                                break;

                            case Contract.SHARPEN:
                                Mat kernel = new Mat(3, 3, CvType.CV_16SC1);
                                kernel.put(0,0,0, -1, 0, -1, 5, -1, 0, -1, 0);
                                Imgproc.filter2D(src, src, src.depth(), kernel);
                                break;

                            case Contract.DILATE:
                                Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(30,30));
                                Imgproc.dilate(src, src, kernelDilate);
                                break;

                            case Contract.EROTE:
                                Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(15,15));
                                Imgproc.erode(src,src,kernelErode);
                                break;

                            case Contract.THRESHOLD:
                                Imgproc.threshold(src, src, 100, 255, Imgproc.THRESH_BINARY);
                                break;

                            case Contract.DOG:
                                differenceOfGaussian();
                                break;

                            case Contract.CANNY:
                                canny();
                                break;

                            case Contract.SOBEL:
                                sobel();
                                break;
                            case Contract.HARRIS:
                                harrisCorner();
                                break;
                            case Contract.HOUGHLines:
                                houghLines();
                                break;

                            case Contract.HOUGHCircle:
                                houghCircles();
                                break;

                        }

                        Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(src, processedImage);

                        ivImage.setImageBitmap(selectedImage);
                        ivImageProcessed.setImageBitmap(processedImage);
                        if(ACTION_MODE == Contract.DOG || ACTION_MODE == Contract.CANNY ||
                                ACTION_MODE == Contract.SOBEL || ACTION_MODE == Contract.HARRIS||
                        ACTION_MODE == Contract.HOUGHLines || ACTION_MODE == Contract.HOUGHCircle){
                            ivImageProcessed.setImageBitmap(currentBitmap);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                break;
        }

    }


    public void differenceOfGaussian(){
        Mat grayMat = new Mat();
        Mat blur1 = new Mat();
        Mat blur2 = new Mat();

        //将图像转换成灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        //以两个不同的模糊半径对图像做模糊处理
        Imgproc.GaussianBlur(grayMat, blur1, new Size(15, 15), 5);
        Imgproc.GaussianBlur(grayMat, blur2, new Size(21, 21), 5);

        //将两幅模糊后的图像相减
        Mat DOG = new Mat();
        Core.absdiff(blur1,blur2,DOG);

        Core.multiply(DOG, new Scalar(100), DOG);
        Imgproc.threshold(DOG,DOG,50, 255, Imgproc.THRESH_BINARY_INV);

        //将Mat转换回位图
        Utils.matToBitmap(DOG, currentBitmap);
    }


    public void canny(){
        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        //将图像转换为灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        Imgproc.Canny(grayMat, cannyEdges, 10, 100);

        //将Mat转换回位图
        Utils.matToBitmap(cannyEdges, currentBitmap);
    }


    public void sobel(){
        Mat grayMat = new Mat();
        Mat sobel = new Mat(); //用来保存结果Mat

        //分别用于保存梯度和绝对值梯度
        Mat grad_x = new Mat();
        Mat abs_grad_x = new Mat();

        Mat gray_y = new Mat();
        Mat abs_gray_y = new Mat();

        //将图像转换为灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        //计算水平方向的梯度
        Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);

        //计算垂直方向的梯度
        Imgproc.Sobel(grayMat, gray_y, CvType.CV_16S, 0, 1, 3, 1, 0);

        //计算两个方向上的梯度绝对值
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(gray_y, abs_gray_y);

        //计算结果梯度
        Core.addWeighted(abs_grad_x, 0.5, abs_gray_y, 0.5, 1, sobel);

        //将Mat转换为位图
        Utils.matToBitmap(sobel, currentBitmap);

    }

    public void harrisCorner(){
        Mat grayMat = new Mat();
        Mat corners = new Mat();

        //将图像转换成灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        Mat tmpDst = new Mat();
        //找出角点
        Imgproc.cornerHarris(grayMat, tmpDst, 2, 3, 0.04);

        //归一化Harrris角点的输出
        Mat tmpDstNorm = new Mat();
        Core.normalize(tmpDst, tmpDstNorm, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(tmpDstNorm, corners);

        //在新的图像上绘制角点
        Random r = new Random();
        for (int i=0; i<tmpDstNorm.cols(); i++){
            for(int j=0; j<tmpDstNorm.rows(); j++){
                double[] value = tmpDstNorm.get(j,i);
                if(value[0] > 150){
                    Imgproc.circle(corners, new Point(i,j), 5, new Scalar(r.nextInt(255), 2));
                }
            }
        }
        Utils.matToBitmap(corners, currentBitmap);
    }

    public void houghLines(){
        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();

        Mat lines = new Mat();

        //将图像转换成灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(grayMat, cannyEdges, 10, 100);
        Imgproc.HoughLinesP(cannyEdges, lines, 1, Math.PI/180, 50, 20, 20);

        Mat houghLines = new Mat();
        houghLines.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC1);

        //在图像中绘制直线
        for(int i = 0; i<lines.cols(); i++){
            double[] points = lines.get(0,i);
            double x1, y1, x2, y2;
            x1 = points[0];
            y1 = points[1];
            x2 = points[2];
            y2 = points[3];

            Point pt1 = new Point(x1, y1);
            Point pt2 = new Point(x2, y2);

            //在一副图像上绘制直线
            Imgproc.line(houghLines, pt1, pt2, new Scalar(255, 0, 0), 1);

        }

        Utils.matToBitmap(houghLines, currentBitmap);
    }

    public void houghCircles(){
        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat circle = new Mat();

        //将图像转换成灰度
        Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        Imgproc.Canny(grayMat, cannyEdges, 10, 100);

        Imgproc.HoughCircles(cannyEdges, circle, Imgproc.CV_HOUGH_GRADIENT, 1, cannyEdges.rows()/15);

        Mat houghCircles = new Mat();
        houghCircles.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC1);

        //在图像上绘制圆形
        for(int i=0; i<circle.cols(); i++){
            double[] parameters = circle.get(0, i);
            double x, y;
            int r;

            x = parameters[0];
            y = parameters[1];
            r = (int) parameters[2];

            Point center = new Point(x, y);

            //在一副图像上绘制图形
            Imgproc.circle(houghCircles, center, r, new Scalar(255, 0, 0), 1);
        }

        Utils.matToBitmap(houghCircles, currentBitmap);

    }
}

