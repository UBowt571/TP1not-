package com.example.tp1note;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;


public class TouchExample extends View {
    private static final String TAG = "TouchExample";
    private final String IMAGES_FOLDER = "/storage/emulated/0/DCIM/Camera";
    private final float ASPECT_RATIO =(float) 9/16;
    private final int MAX_NUM_OF_IMAGE_PER_LINE = 7; //Nombre mMaxImageIndex d'images par ligne
    private float mScale = 1f;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    //Valeurs de référence : on applique pas les modifs directement sur les images,
    //on change dabord les valeurs de référence puis on applique les changements de ces valeurs
    //sur les images à l'écran (toutes les images n'ont pas la meme taille en fonction de si
    // on les voit à l'écran ou pas).
    int mRefWidth;                           //largeur de référence
    int mRefHeight;                          //hauteur de référence

    int mFirstLineIndex;                    //Premiere ligne de BM que l'on voit a l'écran
    int mLastLineIndex;                     //Derniere ligne de BM que l'on voit a l'écran
    int mMinImageIndex;                     //Indice mMinImageIndex de l'image à afficher, prend en compte le scroll et le dépassement
    int mMaxImageIndex;                     //Indice mMaxImageIndex de l'image à afficher, prend en compte le scroll et le dépassement

    int mMaxImageHeight;                    //Hauteur maximale d'une image a l'écran
    int mScreenWidth;                       //Largeur de l'ecran
    int mScreenHeight;                      //Hauteur de l'écran

    ArrayList<BitmapDrawable> bmDrawList;           //Contient tous les BMD que l'on veut afficher
    ArrayList<Bitmap> bmList = new ArrayList<>();   //Contient tous les BM que l'on veut afficher

    int mScrollOffset = 0;       //Coefficient de décalage du au scroll

    private Paint mPaint;

    public TouchExample(Context context) {
        super(context);

        mPaint = new Paint();

        mGestureDetector = new GestureDetector(context, new ZoomGesture());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());

        //Obtention des images depuis la mémoire
        bmDrawList = imagesGetter.getBitmaps(IMAGES_FOLDER);
        for(int i = 0; i < bmDrawList.size(); i++) bmList.add(bmDrawList.get(i).getBitmap());

        //Obtention dynamique de la largeur de l'écran (pour supporter l'inclinaison de l'écran)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mMaxImageHeight =(int) (mScreenWidth * ASPECT_RATIO);

        mRefWidth = mScreenWidth;
        mRefHeight = mMaxImageHeight;

        init();
    }

    //Affiche dynamiquement les images en quadrillage
    @Override
    public void onDraw(Canvas canvas) {
        //Offset des images
        int top;
        int left;

        int nImageLine = mScreenWidth / mRefWidth; //Actuel nombre d'images par ligne
        int tmpTop = mRefHeight; //Hauteur du BM de reference
        int tmpLeft = mRefWidth; //Largeur du BM de reference

        //Determine la limite du scroll
        if (tmpTop * (bmList.size()/nImageLine) > mScreenHeight) //Est-ce-que images ne rentrent pas toutes a l'écran ?
        {
            int lastLine = -mScreenHeight + tmpTop * (bmList.size()/nImageLine + 1) - (mScreenHeight /tmpTop); //Position en Y de la derniere ligne d'iamge de la gallery
            if(mScrollOffset > 0) //Empeche de scroll trop haut
                mScrollOffset = 0;
            if(mScrollOffset < -lastLine) //Empeche de scroll trop bas
                mScrollOffset = -lastLine;
            mFirstLineIndex = -mScrollOffset /tmpTop - 1;
            mLastLineIndex = mFirstLineIndex + mScreenHeight /tmpTop + 4;
        }
        else //Désactivation du scroll
        {
            mFirstLineIndex = 0;
            mLastLineIndex = bmList.size()/nImageLine + bmList.size()%nImageLine;
            mScrollOffset = 0;
        }

        mMaxImageIndex = (mLastLineIndex * nImageLine) + (bmList.size() % nImageLine);
        mMinImageIndex = (mFirstLineIndex -1) * nImageLine;
        if (mMaxImageIndex > bmList.size()) mMaxImageIndex = bmList.size();
        if(mMinImageIndex < 0) mMinImageIndex = 0;

        //Affichage des images que l'on peut voir a l'écran(plus une ligne en bas et en haut)
        for (int i = mMinImageIndex; i < mMaxImageIndex; i++)
        {
            top = tmpTop * (i/nImageLine);
            left = tmpLeft * (i%nImageLine);
            bmList.set(i,Bitmap.createScaledBitmap(bmDrawList.get(i).getBitmap(), mRefWidth, mRefHeight, false));
            canvas.drawBitmap(bmList.get(i), left, top + mScrollOffset, mPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }


    public class ZoomGesture extends GestureDetector.SimpleOnGestureListener {
        //Prise en compte du scroll
        @Override
        public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,float distanceY) {
            mScrollOffset -= distanceY;
            invalidate();
            return true;
        }
    }

    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //La scale ne doit pas etre trop grande ou trop petite, pour éviter des mouvements inutiles
            mScale *= detector.getScaleFactor();
            mScale = (mScale > 1) ? 1 : mScale;
            mScale = (mScale < 1./9) ? (float) 1/9 : mScale;

            setRef(mScale); //On applique les transfos sur les valeurs de reference

            //On applique les changements des valeurs de reference sur toutes les images à afficher
            for (int i = mMinImageIndex; i < mMaxImageIndex; i++)
            {
                bmList.set(i,Bitmap.createScaledBitmap(bmList.get(i), mRefWidth, mRefHeight, false));
            }
            invalidate();
            return true;
        }
    }

    //Change les valeurs de reference en fonction du coefficient de zoom
    void setRef(float imageScale) {
        //Zoom et déZoom par acoups
        for (int i = 0; i< MAX_NUM_OF_IMAGE_PER_LINE; i++)
        {
            if(i/(float) MAX_NUM_OF_IMAGE_PER_LINE < imageScale && imageScale < (i+1)/(float) MAX_NUM_OF_IMAGE_PER_LINE)
            {
                mRefWidth = mScreenWidth /(MAX_NUM_OF_IMAGE_PER_LINE -i);
                mRefHeight = mMaxImageHeight /(MAX_NUM_OF_IMAGE_PER_LINE -i);
            }
        }
    }

    //Initialisation à la construction, car c'est la fonction de déZoom qui gére le rescale des images
    void init()
    {
        mScale = 1;
        setRef(mScale); //TO CHECK
        for (int i = mMinImageIndex; i < mMaxImageIndex; i++)
        {
            bmList.set(i,Bitmap.createScaledBitmap(bmList.get(i), mRefWidth, mRefHeight, false));
        }
        invalidate();
    }

}

