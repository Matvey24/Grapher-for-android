package com.mathway.perelman.grapher_for_android.ui.elements;

import android.app.Dialog;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.controller.DNEditor;
import com.mathway.perelman.grapher_for_android.controller.ModelUpdater;
import com.mathway.perelman.grapher_for_android.model.FullModel;


public class MainSettings {
    private final ModelUpdater updater;
    private final MainActivity activity;

    private final TextInputEditText et_net_width;
    private final TextInputEditText et_file_name;
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
        et_file_name = settings_dialog.findViewById(R.id.settings_file_name);
        et_file_name.setOnEditorActionListener(((v, actionId, event) -> {
            if(!activity.getSaveController().getFileNamed(DNEditor.getText(et_file_name)).exists())
                et_file_name.setError(activity.getString(R.string.file_not_found));
            return true;
        }));
        et_net_width.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_file_name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        btn_open_settings.setOnClickListener((view)->open_settings());
        et_net_width.setOnEditorActionListener((a, b, c)->update_net_width());
        btn_clear.setOnClickListener((view)->{
            updater.clearFully();
            update_texts();
        });
        btn_rollback.setOnClickListener((view)->activity.getSaveController().rollback());
        btn_quick_save.setOnClickListener((view)->activity.getSaveController().quick_save());
        btn_load.setOnClickListener((view)->{
            if(activity.getSaveController().loadNamed(DNEditor.getText(et_file_name)))
                settings_dialog.dismiss();
        });
        btn_save.setOnClickListener((view)->activity.getSaveController().saveInto(DNEditor.getText(et_file_name)));
    }
    private String getNetWidthString(){
        return (updater.draw_coordinates?"":"-") + updater.getCoordinateSystem().getMinDelta();
    }
    public void setFileName(String name){
        et_file_name.setText(name);
    }
    public String getFileName(){
        return DNEditor.getText(et_file_name);
    }
    private void update_texts(){
        et_net_width.setText(getNetWidthString());
    }
    private void open_settings(){
        update_texts();
        settings_dialog.show();
    }
    private void parseWidth(String text){
        boolean show_net = true;
        String s = text.replaceAll("[ \t\r]", "");
        if(s.startsWith("-")){
            show_net = false;
            s = s.substring(1);
        }
        int width = Integer.parseInt(s);
        if(width < 10){
            throw new RuntimeException(width + " < 10");
        }
        updater.draw_coordinates = show_net;
        updater.getCoordinateSystem().setMIN_DELTA(width);
    }
    private boolean update_net_width(){
        boolean good = true;
        try {
            parseWidth(DNEditor.getText(et_net_width));
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
        try {
            parseWidth(fm.main_settings);
        }catch (RuntimeException ignored){}
    }
}
