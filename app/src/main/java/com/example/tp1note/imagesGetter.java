package com.example.tp1note;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.File;
import java.util.ArrayList;

public class imagesGetter {

    public static ArrayList<BitmapDrawable> getBitmaps(String folderName){
        File directory = new File(folderName);
        File[] files = null;
        ArrayList<BitmapDrawable> bitmapsArrayList = new ArrayList<>();
        Bitmap[] bitmaps = null;

        if(directory.isDirectory()){
            files = directory.listFiles();
        }

        for (File fileName : files){
            if(fileName.toString().contains(".jpg")){
                bitmapsArrayList.add(new BitmapDrawable(BitmapFactory.decodeFile(fileName.toString())));
            }
        }
        return bitmapsArrayList;
    }
}