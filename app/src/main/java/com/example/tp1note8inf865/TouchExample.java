package com.example.tp1note8inf865;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;


public class TouchExample extends View {
    private float mScale = 1f;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    BitmapDrawable bmD;
    Bitmap bm1;
    ArrayList<Bitmap> listBmp = new ArrayList<Bitmap>();

    int maxHeight; //image height
    int maxWidth; //Display width
    int nbImgLine = 7;


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
        listBmp.add(bm1);
        bmD = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.dog2);
        bm1 = bmD.getBitmap();
        listBmp.add(bm1);
        bmD = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.dog3);
        bm1 = bmD.getBitmap();
        listBmp.add(bm1);
        bmD = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.stonks);
        bm1 = bmD.getBitmap();
        listBmp.add(bm1);
        bmD = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.sequence);
        bm1 = bmD.getBitmap();
        listBmp.add(bm1);

    }

    //Affiche dynamiquement les images en quadrillage
    @Override
    public void onDraw(Canvas canvas) {
        maxHeight=getHeight();
        maxWidth =getWidth();
        int top = 0;
        int left = 0;

        for (int i = 0; i < listBmp.size(); i++)
        {
            top = listBmp.get(i).getHeight() * (i/nbImgLine);
            left = listBmp.get(i).getHeight() * (i% nbImgLine);
            canvas.drawBitmap(listBmp.get(i), left, top, mPaint);
            nbImgLine = maxWidth/listBmp.get(i).getWidth();
            if(nbImgLine ==0){nbImgLine=1;}
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
            process_image(listBmp, mScale);
            invalidate();
            return true;
        }
    }

    //Redimensionne l'image en fonction du coefficient de zoom
    void process_image(ArrayList<Bitmap> listBmp, float imageScale) {

        int maxIm = 7; //max Image per line
        Bitmap bm;

        for (int i = 0; i < listBmp.size(); i++)
        {
            if(0 < imageScale && imageScale < 1./maxIm)
            {
                bm = Bitmap.createScaledBitmap(listBmp.get(i), (int) (maxWidth /7), (int) (maxHeight/7), false);
            }
            else if(1./maxIm < imageScale && imageScale < 2./maxIm)
            {
                bm = Bitmap.createScaledBitmap(listBmp.get(i), (int) (maxWidth /6), (int) (maxHeight/6), false);
            }
            else if(2./maxIm < imageScale && imageScale < 3./maxIm)
            {
                bm = Bitmap.createScaledBitmap(listBmp.get(i), (int) (maxWidth /5), (int) (maxHeight/5), false);
            }
            else if(3./maxIm < imageScale && imageScale < 4./maxIm)
            {
                bm = Bitmap.createScaledBitmap(listBmp.get(i), (int) (maxWidth /4), (int) (maxHeight/4), false);
            }
            else if(4./maxIm < imageScale && imageScale < 5./maxIm)
            {
                bm = Bitmap.createScaledBitmap(listBmp.get(i), (int) (maxWidth /3), (int) (maxHeight/3), false);
            }
            else if(5./maxIm < imageScale && imageScale < 6./maxIm)
            {
                bm = Bitmap.createScaledBitmap(listBmp.get(i), (int) (maxWidth /2), (int) (maxHeight/2), false);
            }
            else if(6./maxIm < imageScale && imageScale < 7./maxIm)
            {
                bm = Bitmap.createScaledBitmap(listBmp.get(i), (int) (maxWidth * 7./maxIm), (int) (maxHeight * 7./maxIm), false);
            }
            else
            {
                bm = Bitmap.createScaledBitmap(listBmp.get(i), maxWidth, maxHeight, false);
            }

            listBmp.set(i,bm) ;
        }

        }

}