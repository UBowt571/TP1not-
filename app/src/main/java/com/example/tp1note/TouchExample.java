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
import androidx.core.view.GestureDetectorCompat;

import java.util.ArrayList;


public class TouchExample extends View {
    private static final String TAG = "TouchExample";
    private static final int MAX_POINTERS = 5;
    private float mScale = 1f;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;;

    BitmapDrawable bmD;
    Bitmap bm1;

    int maxHeight = 600; //image height
    int maxLength = 1080; //Display width

    int maxIm = 7; //max Image per line
    ArrayList<Bitmap> bmList = new ArrayList<Bitmap>();

    int scrollOffset = 0;


    private Paint mPaint;

    public TouchExample(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);

        mGestureDetector = new GestureDetector(context, new ZoomGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());


        bmD = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.dog1);
        bm1 = bmD.getBitmap();

        for(int i = 0; i < 30; i++) bmList.add(bmD.getBitmap());
        invalidate();
    }

    //Affiche dynamiquement les images en quadrillage
    //On suppose aue toutes les images on la meme resolution
    @Override
    public void onDraw(Canvas canvas) {
        int top;
        int left;
        int displayWidth = 1080;
        int nImageLine = displayWidth/bm1.getWidth();
        int tmpTop = bmList.get(0).getHeight();
        int tmpLeft = bmList.get(0).getWidth();

        for (int i = 0; i < bmList.size(); i++)
        {
            top = tmpTop * (i/nImageLine);
            left = tmpLeft * (i%nImageLine);

            canvas.drawBitmap(bmList.get(i), left, top + scrollOffset, mPaint);
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

        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                                float distanceY) {
            //Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
            Log.e(TAG, Float.toString(distanceX));
            scrollOffset -= distanceY;
            invalidate();
            return true;
        }
    }

    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            mScale = (mScale > 1) ? 1 : mScale;
            mScale = (mScale < 1./9) ? (float) 1/9 : mScale;
            for(int i = 0; i<bmList.size(); i++)
            {
                process_image(bmList.get(i), mScale, i);
            }

            invalidate();
            return true;
        }
    }

    //Redimensionne l'image en fonction du coefficient de zoom
    void process_image(Bitmap image, float imageScale, int index) {
        Bitmap bm = Bitmap.createScaledBitmap(image, maxLength, maxHeight, false);

        for (int i = 0; i< maxIm; i++)
        {
            if(i/(float)maxIm < imageScale && imageScale < (i+1)/(float)maxIm)
            {
                bm = Bitmap.createScaledBitmap(image, (maxLength/(maxIm-i)), (maxHeight/(maxIm-i)), false);
            }
        }
        bmList.set(index, bm);
    }
}

