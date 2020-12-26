package com.matvey.perelman.grapher_for_android.ui.elements;

import android.app.Dialog;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.matvey.perelman.grapher_for_android.MainActivity;
import com.matvey.perelman.grapher_for_android.R;
import com.matvey.perelman.grapher_for_android.controller.DNEditor;
import com.matvey.perelman.grapher_for_android.controller.ModelUpdater;
import com.matvey.perelman.grapher_for_android.model.FullModel;


public class MainSettings {
    private final ModelUpdater updater;
    private final MainActivity activity;

    private final TextInputEditText et_net_width;
    private final Button btn_save;
    private final Button btn_load;

    private final Dialog settings_dialog;

    public MainSettings(MainActivity activity, ModelUpdater updater){
        this.activity = activity;
        this.updater = updater;

        this.settings_dialog = new Dialog(activity);
        settings_dialog.setContentView(R.layout.main_settings);

        Button btn_open_settings = activity.findViewById(R.id.btn_settings);
        et_net_width = settings_dialog.findViewById(R.id.settings_net_width);
        Button btn_clear = settings_dialog.findViewById(R.id.btn_settings_clear);
        Button btn_rollback = settings_dialog.findViewById(R.id.btn_settings_rollback);
        btn_save = settings_dialog.findViewById(R.id.btn_settings_save);
        btn_load = settings_dialog.findViewById(R.id.btn_settings_load);
        Button btn_quick_save = settings_dialog.findViewById(R.id.btn_settings_quick_save);
        et_net_width.setImeOptions(EditorInfo.IME_ACTION_DONE);

        btn_open_settings.setOnClickListener((view)->open_settings());
        et_net_width.setOnEditorActionListener((a, b, c)->update_net_width());
        btn_clear.setOnClickListener((view)->updater.clearFully());
        btn_rollback.setOnClickListener((view)->activity.rollback());
        btn_quick_save.setOnClickListener((view)->activity.quickSave());
    }
    private String getNetWidthString(){
        return String.valueOf(updater.getCoordinateSystem().getMinDelta());
    }
    private void open_settings(){
        et_net_width.setText(getNetWidthString());
        settings_dialog.show();
    }
    private boolean update_net_width(){
        boolean good = true;
        try {
            int width = Integer.parseInt(DNEditor.getText(et_net_width));
            if(width < 10){
                throw new RuntimeException(width + " < 10");
            }
            updater.getCoordinateSystem().setMIN_DELTA(width);
            updater.runResize();
        }catch (RuntimeException e){
            et_net_width.setError(activity.getString(R.string.simple_error) + ": " + e.getMessage());
            good = false;
        }
        if(good){
            activity.hideKeyboard(settings_dialog.getCurrentFocus());
        }
        return true;
    }

    public void makeModel(FullModel fm){
        fm.main_settings = getNetWidthString();
    }
    public void fromModel(FullModel fm){
        int width = Integer.parseInt(fm.main_settings);
        updater.getCoordinateSystem().setMIN_DELTA(width);
    }
}
