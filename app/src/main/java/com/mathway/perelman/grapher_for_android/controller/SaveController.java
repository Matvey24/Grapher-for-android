package com.mathway.perelman.grapher_for_android.controller;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.ui.elements.TimerSettings;

import java.io.File;

public class SaveController {
    private static final String DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private final MainActivity activity;
    private final ModelUpdater updater;
    public SaveController(MainActivity activity, ModelUpdater updater) {
        this.activity = activity;
        this.updater = updater;
    }

    public File getDefaultFile() {
        return new File(activity.getFilesDir(), "emergency_save.gr");
    }

    public File getUsingFile() {
        if (updater.last_used_file != null)
            return updater.last_used_file;
        return getDefaultFile();
    }

    public void onCreate(Intent intent) {
        Uri uri = intent.getData();
        File f;
        if (uri != null) {
            String s = uri.getPath();
            s = s.replace("device_storage", DIR);
            s = s.substring(DIR.length() + 1);
            f = new File(DIR, s);
            if(f.exists()) {
                updater.load(f, false);
                activity.getMainSettings().setFileName(s);
            }
        } else {
            f = getDefaultFile();
            if (f.exists()) {
                updater.load(f, false);
                loadMeta();
            }
            loadSettings();
        }
    }


    public void loadConcurrentlyFile(File f) {
        updater.runInBackground(() -> updater.load(f, true));
    }

    public void onStop() {
        updater.save(getDefaultFile());
        saveMeta();
    }
    public void onDestroy(){
        saveSettings();
    }
    public void rollback() {
        File f = getUsingFile();
        if (f.exists()) {
            updater.runInBackground(() -> {
                updater.load(f, true);
                loadMeta();
            });
        } else {
            updater.clearFully();
        }
    }

    public void quick_save() {
        saveMeta();
        updater.runInBackground(() -> updater.quick_save(getDefaultFile()));
    }

    public void saveInto(String file_name) {
        File f = getFileNamed(file_name);
        updater.runInBackground(() -> updater.save(f));
    }

    public File getFileNamed(String file_name) {
        return new File(DIR, file_name);
    }

    public boolean loadNamed(String file_name) {
        File f = getFileNamed(file_name);
        if (!f.exists())
            return false;
        loadConcurrentlyFile(f);
        return true;
    }

    private void loadMeta() {
        SharedPreferences sp = activity.getSharedPreferences("meta_save", Context.MODE_PRIVATE);
        TimerSettings timer = activity.getTimer();
        boolean timer_started = sp.getBoolean("timer_started", false);
        float time = sp.getFloat("timer_value", (float) timer.getTime());
        boolean direction = sp.getBoolean("timer_direction", true);
        timer.setPosition(time, timer_started, direction);
        activity.setResizeChecked(sp.getBoolean("resize_multiple", false));
    }

    private void saveMeta() {
        SharedPreferences sp = activity.getSharedPreferences("meta_save", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        TimerSettings timer = activity.getTimer();
        ed.putBoolean("timer_started", timer.isStarted());
        ed.putFloat("timer_value", (float) timer.getTime());
        ed.putBoolean("timer_direction", timer.getDirection());
        ed.putBoolean("resize_multiple", updater.resize_multiple);
        ed.apply();
    }
    private void loadSettings(){
        SharedPreferences sp  = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String file_name = sp.getString("file_name", "");
        activity.getMainSettings().setFileName(file_name);
    }
    private void saveSettings(){
        SharedPreferences sp = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("file_name", activity.getMainSettings().getFileName());
        ed.apply();
    }
}
