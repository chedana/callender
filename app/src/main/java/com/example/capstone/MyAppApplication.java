package com.example.capstone;

import android.app.Application;

public class MyAppApplication extends Application {
    private String pathSave = "";


    public  String getPathSave(){
        return pathSave;
    }

    public  void setPathSave(String str){
        pathSave = str;
    }
}
