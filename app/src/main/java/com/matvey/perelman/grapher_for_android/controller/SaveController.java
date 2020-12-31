package com.matvey.perelman.grapher_for_android.controller;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.matvey.perelman.grapher_for_android.MainActivity;

import java.io.File;

public class SaveController {
    private final MainActivity activity;
    private final ModelUpdater updater;

    private File opening_external_file;

    public SaveController(MainActivity activity, ModelUpdater updater) {
        this.activity = activity;
        this.updater = updater;
    }

    public File getDefaultFile() {
        return new File(activity.getFilesDir(), "emergency_save.gr");
    }

    public void onCreate(Intent intent) {
        Uri uri = intent.getData();
        File f;
        if (uri != null) {
            String s = uri.getPath();
            opening_external_file = new File(s.substring(s.indexOf(":") + 1));
            if(Build.VERSION.SDK_INT >= 23){
                activity.requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, MainActivity.REQUEST_LOAD_PROJECT);
                return;
            }else{
                f = opening_external_file;
            }
        }else{
            f = getDefaultFile();
        }
        try{
            if(f.exists())
                updater.load(f, false);
        }catch (RuntimeException ignored){}
    }

    public void onLoadProjectPermissionGranted(){
        updater.dosave(false, opening_external_file);
    }

    public void onStop(){
        updater.save(getDefaultFile());
    }

    public void rollback(){
        File f = getDefaultFile();
        if (f.exists()) {
            updater.dosave(false, f);
        } else {
            updater.clearFully();
        }
    }
    public void quick_save(){
        File f = updater.last_used_file;
        if(f == null){
            f = getDefaultFile();
        }
        updater.dosave(true, f);
    }

}
