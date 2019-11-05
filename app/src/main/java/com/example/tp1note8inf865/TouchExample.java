package com.example.tp1note8inf865;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    ArrayList<Bitmap> listBmp = new ArrayList<>();

    private int maxHeight; //image height
    private int maxWidth; //Display width
    private static final int NB_MAX_IMAGES_FOR_ONE_LINE = 7;
    private static final int SPAN_SLOP = 2;
    private int nbImgLine = 4;      // default number of images for a line /!\ very important for the app


    private Paint mPaint;

    public TouchExample(Context context) {
        super(context);

        mPaint = new Paint();

        mGestureDetector = new GestureDetector(context, new ZoomGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());

        listBmp = imagesGetter.getBitmaps("/storage/emulated/0/DCIM/Camera");

    }

    //Affiche dynamiquement les images en quadrillage
    @Override
    public void onDraw(Canvas canvas) {
        maxHeight=getHeight();
        maxWidth =getWidth();
        int imgsize = (maxWidth/nbImgLine);
        int top = 0;
        int left = 0;

        for (int i = 0; i < listBmp.size(); i++)
        {
            if((left+imgsize)>maxWidth){top+=imgsize;left=0;}
            Bitmap resized = Bitmap.createScaledBitmap(listBmp.get(i), imgsize, imgsize, false);
            canvas.drawBitmap(resized,left,top,mPaint);
            left+=imgsize;
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
            if(gestureTolerance(detector)) {
                process_image(listBmp, mScale);
                invalidate();
                return true;
            }
            return true;
        }
    }

    //Redimensionne l'image en fonction du coefficient de zoom
    void process_image(ArrayList<Bitmap> listBmp, float imageScale) {

        int imgsize = (maxWidth/nbImgLine);
        if(nbImgLine==1){nbImgLine=2;}
        if( (imgsize*imageScale) > (maxWidth/(nbImgLine-1)) ){
            if(nbImgLine>0){
                nbImgLine-=1;
            }
        }else if( (imgsize*imageScale) < (maxWidth/(nbImgLine+1)) ){
            if(nbImgLine<NB_MAX_IMAGES_FOR_ONE_LINE){nbImgLine+=1;}
        }
    }

    Bitmap getBitmap(int resID){
        return ((BitmapDrawable) getContext().getResources().getDrawable(resID)).getBitmap();
    }

    private boolean gestureTolerance(ScaleGestureDetector detector) {
        final float spanDelta = Math.abs(detector.getCurrentSpan() - detector.getPreviousSpan());
        return spanDelta > SPAN_SLOP;
    }

}