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
    Bitmap bmRef;

    int minLine;
    int maxLine;
    int max;
    int min;

    int maxHeight = 600; //image height
    int maxLength = 1080; //Display width
    int displayHeight = 1700;

    int maxIm = 7; //max Image per line
    ArrayList<BitmapDrawable> bmDrawList = new ArrayList<>();
    ArrayList<Bitmap> bmList = new ArrayList<>();

    int scrollOffset = 0;

    private Paint mPaint;

    public TouchExample(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);

        mGestureDetector = new GestureDetector(context, new ZoomGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());


        bmD = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.dog1);
        bmRef = bmD.getBitmap();

        for(int i = 0; i < 1000; i++) bmDrawList.add(bmD); //Fake Array
        for(int i = 0; i < bmDrawList.size(); i++) bmList.add(bmD.getBitmap());
    }

    //Affiche dynamiquement les images en quadrillage
    //On suppose aue toutes les images on la meme resolution
    @Override
    public void onDraw(Canvas canvas) {
        int top;
        int left;
        int displayWidth = 1080;
        int nImageLine = displayWidth/bmRef.getWidth();
        int tmpTop = bmRef.getHeight();
        int tmpLeft = bmRef.getWidth();

        //Scroll limits
        if (tmpTop * (bmList.size()/nImageLine) > displayHeight)
        {
            if(scrollOffset > 0)
                scrollOffset = 0;
            if(scrollOffset < -(-displayHeight + tmpTop * (bmList.size()/nImageLine) - (displayHeight/tmpTop)))
                scrollOffset = -(-displayHeight + tmpTop * (bmList.size()/nImageLine) - (displayHeight/tmpTop));
            minLine = -scrollOffset/tmpTop - 1;
            maxLine = minLine + displayHeight/tmpTop + 4;
        }
        else
        {
            minLine = 0;
            maxLine = bmList.size()/nImageLine + bmList.size()%nImageLine;
            scrollOffset = 0; //if nothing to scroll, then don't
        }

        max = (maxLine * nImageLine) + (bmList.size() % nImageLine);
        min = (minLine-1) * nImageLine;
        if (max > bmList.size()) max = bmList.size();
        if(min < 0) min = 0;

        for (int i = min; i < max; i++)
        {
            top = tmpTop * (i/nImageLine);
            left = tmpLeft * (i%nImageLine);
            bmList.set(i, bmDrawList.get(i).getBitmap());
            bmList.set(i,Bitmap.createScaledBitmap(bmList.get(i), bmRef.getWidth(), bmRef.getHeight(), false));
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
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,float distanceY) {
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
            process_image(bmRef,mScale,-1);
            for (int i = min; i < max; i++)
            {
                bmList.set(i,Bitmap.createScaledBitmap(bmList.get(i), bmRef.getWidth(), bmRef.getHeight(), false));
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
        if(index < 0) bmRef = bm;
        else bmList.set(index, bm);
    }
}

