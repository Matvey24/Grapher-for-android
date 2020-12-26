package com.matvey.perelman.grapher_for_android;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.matvey.perelman.grapher_for_android.model.MainModel;
import com.matvey.perelman.grapher_for_android.controller.ModelUpdater;
import com.matvey.perelman.grapher_for_android.model.FullModel;
import com.matvey.perelman.grapher_for_android.ui.elements.CalculatorView;
import com.matvey.perelman.grapher_for_android.ui.elements.FunctionsView;
import com.matvey.perelman.grapher_for_android.ui.elements.HelperView;
import com.matvey.perelman.grapher_for_android.ui.elements.MainSettings;
import com.matvey.perelman.grapher_for_android.ui.elements.TimerSettings;
import com.matvey.perelman.grapher_for_android.ui.elements.elements_list.GraphicsAdapter;
import com.matvey.perelman.grapher_for_android.ui.elements.elements_list.TextElement;
import com.matvey.perelman.grapher_for_android.ui.elements.graphic_settings.DefaultSettings;
import com.matvey.perelman.grapher_for_android.ui.elements.graphic_settings.FunctionSettings;
import com.matvey.perelman.grapher_for_android.ui.elements.graphic_settings.ImplicitSettings;
import com.matvey.perelman.grapher_for_android.ui.elements.graphic_settings.ParametricSettings;
import com.matvey.perelman.grapher_for_android.ui.elements.graphic_settings.TranslationSettings;
import com.matvey.perelman.grapher_for_android.ui.grapher.graphics.Graphic;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.core.content.pm.PermissionInfoCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.Permission;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ModelUpdater updater;
    private DrawerLayout drawer;
    private NavController navController;

    private TextView state;

    private TimerSettings timerSettings;
    private MainSettings mainSettings;

    private DefaultSettings[] settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainModel.createInstance(this);

        RecyclerView rv = findViewById(R.id.list_graphics);
        rv.setLayoutManager(new LinearLayoutManager(this));

        GraphicsAdapter graphics = new GraphicsAdapter();
        rv.setAdapter(graphics);
        updater = MainModel.getInstance().updater;
        FunctionsView fv = new FunctionsView(this, this::functionsRecalculate);
        CalculatorView cv = new CalculatorView(this, this::recalculate);
        updater.setStringElements(graphics, fv, cv);

        Button btn_make_element = findViewById(R.id.btn_make_element);
        btn_make_element.setOnClickListener((view) -> {
            graphics.make_element();
            updater.add(graphics.getItemCount() - 1);
        });
        state = findViewById(R.id.state_view);

        timerSettings = new TimerSettings(this, updater);
        timerSettings.stopTimer();

        mainSettings = new MainSettings(this, updater);
        settings = new DefaultSettings[4];
        settings[0] = new FunctionSettings(this, updater);
        settings[1] = new ParametricSettings(this, updater);
        settings[2] = new ImplicitSettings(this, updater);
        settings[3] = new TranslationSettings(this, updater);

        MainModel.dark_theme =
                (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK)
                        == Configuration.UI_MODE_NIGHT_YES;

        HelperView helperView = new HelperView(this);

        loadEmergencySave();
        updater.dangerState = true;


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder()
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    private void functionsRecalculate() {
        updater.now_show_graphics = true;
        recalculate();
    }

    public void recalculate() {
        hideKeyboard(getCurrentFocus());
        updater.recalculate();
    }

    public void showGraphics() {
        drawer.close();
    }

    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        if (v == null) {
            v = drawer;
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void setState(String text) {
        runOnUiThread(() -> state.setText(text));
    }

    public void open_helper() {
        runOnUiThread(()-> {
            drawer.close();
            if (MainModel.getInstance().openedWindow == MainModel.GRAPHICS) {
                navController.navigate(R.id.action_nav_grapher_to_nav_helper);
            }else{
                MainModel.getInstance().helperFragment.setText();
            }
        });
    }

    private File getFileToEmergencySave() {
        return new File(getFilesDir(), "emergency_save.gr");
    }

    private void loadEmergencySave() {
        try {
            File f = getFileToEmergencySave();
            if (f.exists()) {
                updater.load(f, false);
            }
        }catch (RuntimeException ignored){}
    }
    public void rollback(){
        File f = getFileToEmergencySave();
        if(f.exists()){
            updater.dosave(false, f);
        }else{
            updater.clearFully();
        }
    }
    public void quickSave(){
        File f = getFileToEmergencySave();
        updater.dosave(true, f);
    }
    @Override
    protected void onStop() {
        super.onStop();
        updater.save(getFileToEmergencySave());
        timerSettings.stopTimer();
    }
    public void stopTimer(){
        timerSettings.stopTimer();
    }
    public void startSettings(Graphic g, TextElement e){
        settings[g.type.ordinal()].startSettings(g, e);
    }

    public double getTime() {
        return timerSettings.getTime();
    }
    public void runInBackground(Runnable r){
        updater.runInBackground(r);
    }
    @Override
    public boolean onSupportNavigateUp() {
        if (drawer.isOpen()) {
            drawer.close();
        } else {
            if (MainModel.getInstance().openedWindow == MainModel.HELPER) {
                onBackPressed();
            }
            drawer.open();
        }
        return true;
    }

    public void makeModel(FullModel m) {
        timerSettings.makeModel(m);
        mainSettings.makeModel(m);
    }

    public void fromModel(FullModel m) {
        timerSettings.fromModel(m);
        mainSettings.fromModel(m);
    }
    public void loadLog(){
        if(Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 234523);
        }else{
            updater.runInBackground(this::load);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 234523){
            if(grantResults[0] == PermissionChecker.PERMISSION_GRANTED){
                updater.runInBackground(this::load);
            }else{
                setState(getString(R.string.permission_internet));
            }
        }
    }

    private void load(){
        setState(getString(R.string.loading_log));
        try {
            URL url = new URL("https://github.com/Matvey24/Grapher/raw/master/out/artifacts/Grapher_jar/VersionLog.xml");
            Properties properties = new Properties();
            properties.loadFromXML(url.openStream());
            Set<String> names = properties.stringPropertyNames();
            String[] arr = names.toArray(new String[]{});
            Arrays.sort(arr, String::compareTo);
            String[][] log = new String[arr.length][];
            for(int i = 0; i < arr.length; ++i){
                String text = arr[i];
                log[names.size() - i - 1] = properties.getProperty(text).split("\\n");
            }
            MainModel.fullArray[3] = log;
            MainModel.log_loaded = true;
            open_helper();
        }catch (Exception e){
            setState(e.toString());
        }
        setState(getString(R.string.plus));
    }
}