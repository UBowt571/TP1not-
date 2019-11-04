package com.example.tp1note;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class TouchExample extends View {
    private static final String TAG = "TouchExample";
    private static final int MAX_POINTERS = 5;
    private float mScale = 1f;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    BitmapDrawable bmD;
    Bitmap bm1;

    int maxHeight = 600; //image height
    int maxLength = 1080; //Display width


    private Paint mPaint;
    private float mFontSize;

    public TouchExample(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(mFontSize);

        mGestureDetector = new GestureDetector(context, new ZoomGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());

        bmD = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.dog1);
        bm1 = bmD.getBitmap();


    }

    //Affiche dynamiquement les images en quadrillage
    @Override
    public void onDraw(Canvas canvas) {
        int totalImage = 500;
        int top;
        int left;
        int displayWidth = 1080;
        int nImageLine = displayWidth/bm1.getWidth();

        for (int i = 0; i < totalImage; i++)
        {
            top = bm1.getHeight() * (i/nImageLine);
            left = bm1.getWidth() * (i%nImageLine);

            canvas.drawBitmap(bm1, left, top, mPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    public class ZoomGesture extends GestureDetector.SimpleOnGestureListener {
        private boolean normal = true;

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mScale = normal ? 3f : 1f;
            normal = !normal;
            invalidate();
            return true;
        }
    }

    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            process_image(bm1, mScale);
            invalidate();
            return true;
        }
    }

    //Redimensionne l'image en fonction du coefficient de zoom
    void process_image(Bitmap image, float imageScale) {

        int maxIm = 7; //max Image per line
        Bitmap bm;

        if(0 < imageScale && imageScale < 1./maxIm)
        {
             bm = Bitmap.createScaledBitmap(image, (int) (maxLength/7), (int) (maxHeight/7), false);
        }
        else if(1./maxIm < imageScale && imageScale < 2./maxIm)
        {
            bm = Bitmap.createScaledBitmap(image, (int) (maxLength/6), (int) (maxHeight/6), false);
        }
        else if(2./maxIm < imageScale && imageScale < 3./maxIm)
        {
            bm = Bitmap.createScaledBitmap(image, (int) (maxLength/5), (int) (maxHeight/5), false);
        }
        else if(3./maxIm < imageScale && imageScale < 4./maxIm)
        {
            bm = Bitmap.createScaledBitmap(image, (int) (maxLength/4), (int) (maxHeight/4), false);
        }
        else if(4./maxIm < imageScale && imageScale < 5./maxIm)
        {
            bm = Bitmap.createScaledBitmap(image, (int) (maxLength/3), (int) (maxHeight/3), false);
        }
        else if(5./maxIm < imageScale && imageScale < 6./maxIm)
        {
            bm = Bitmap.createScaledBitmap(image, (int) (maxLength/2), (int) (maxHeight/2), false);
        }
        else if(7./maxIm < imageScale && imageScale < 7./maxIm)
        {
            bm = Bitmap.createScaledBitmap(image, (int) (maxLength * 7./maxIm), (int) (maxHeight * 7./maxIm), false);
        }
        else
        {
            bm = Bitmap.createScaledBitmap(image, maxLength, maxHeight, false);
        }

        bm1 = bm;
    }
}
