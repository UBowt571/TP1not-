package com.example.tp1note8inf865;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.util.ArrayList;

public class imagesGetter {

    public static ArrayList<Bitmap> getBitmaps(String folderName){
        File directory = new File(folderName);
        File[] files = null;
        ArrayList<Bitmap> bitmapsArrayList = new ArrayList<>();
        Bitmap[] bitmaps = null;

        if(directory.isDirectory()){
            files = directory.listFiles();
        }

        for (File fileName : files){
            if(fileName.toString().contains(".jpg")){
                bitmapsArrayList.add(BitmapFactory.decodeFile(fileName.toString()));
            }
        }
        return bitmapsArrayList;
    }
}