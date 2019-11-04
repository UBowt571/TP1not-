package com.example.tp1note8inf865;

import android.os.Bundle;
import android.text.method.Touch;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TouchExample mytouch = new TouchExample(this);
        setContentView(mytouch);


    }

}
