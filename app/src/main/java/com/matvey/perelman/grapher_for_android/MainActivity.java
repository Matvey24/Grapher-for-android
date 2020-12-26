package com.matvey.perelman.grapher_for_android;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.matvey.perelman.grapher_for_android.model.MainModel;
import com.matvey.perelman.grapher_for_android.controller.ModelUpdater;
import com.matvey.perelman.grapher_for_android.model.FullModel;
import com.matvey.perelman.grapher_for_android.ui.elements.CalculatorView;
import com.matvey.perelman.grapher_for_android.ui.elements.FunctionsView;
import com.matvey.perelman.grapher_for_android.ui.elements.MainSettings;
import com.matvey.perelman.grapher_for_android.ui.elements.TimerSettings;
import com.matvey.perelman.grapher_for_android.ui.elements.elements_list.GraphicsAdapter;

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

public class MainActivity extends AppCompatActivity {
    private ModelUpdater updater;
    private DrawerLayout drawer;
    private NavController navController;

    private TimerSettings timerSettings;
    private MainSettings mainSettings;

    private TextView state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainModel.createInstance(this);

        Button btn_help = findViewById(R.id.btn_help);
        btn_help.setOnClickListener((v) -> {
            open_helper(0);
        });

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

    public void open_helper(int help_id) {
        drawer.close();
        if (MainModel.getInstance().openedWindow == MainModel.GRAPHICS) {
            hideKeyboard(getCurrentFocus());
            navController.navigate(R.id.action_nav_grapher_to_nav_helper);
        }
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

    public double getTime() {
        return timerSettings.getTime();
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

    public static String getFromEditText(EditText text) {
        Editable e = text.getText();
        if (e == null) {
            return "";
        }
        return e.toString();
    }
}