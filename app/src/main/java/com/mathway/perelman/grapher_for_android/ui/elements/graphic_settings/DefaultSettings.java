package com.mathway.perelman.grapher_for_android.ui.elements.graphic_settings;

import android.app.Dialog;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ToggleButton;

import com.google.android.material.textfield.TextInputEditText;
import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.controller.DNEditor;
import com.mathway.perelman.grapher_for_android.controller.ModelUpdater;
import com.mathway.perelman.grapher_for_android.ui.elements.elements_list.TextElement;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Graphic;

public abstract class DefaultSettings {
    protected final MainActivity activity;
    protected final ModelUpdater updater;
    private TextInputEditText et_discretization;
    private ToggleButton btn_feels_time;
    private Button btn_set_color;
    private Dialog dialog;

    public DefaultSettings(MainActivity activity, ModelUpdater updater){
        this.activity = activity;
        this.updater = updater;
    }
    protected void onCreateDialog(Dialog d){
        this.dialog = d;
        et_discretization = d.findViewById(R.id.et_discretization);
        et_discretization.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_discretization.setOnEditorActionListener((a,b,c)->{
            fullScan();
            return true;
        });

        btn_feels_time = d.findViewById(R.id.btn_feels_time);
        btn_set_color = d.findViewById(R.id.btn_set_color);
        btn_feels_time.setOnClickListener((view)->feelsTimeChecked());

    }
    private void feelsTimeChecked(){
        getGraphic().feelsTime = btn_feels_time.isChecked();
    }
    protected void fullScan(){
        boolean good = true;
        try{
            int desc = Integer.parseInt(DNEditor.getText(et_discretization));
            if(desc < 2){
                throw new RuntimeException(desc  + " < 2");
            }
            getGraphic().setMAP_SIZE(desc);
            updater.runResize();
        }catch (RuntimeException e){
            et_discretization.setError(e.getMessage());
            good = false;
        }
        if(onScan() && good) {
            activity.hideKeyboard(dialog.getCurrentFocus());
            updater.runResize();
        }
    }
    protected abstract boolean onScan();
    protected Dialog getDialog(){
        return dialog;
    }
    public void show(){
        et_discretization.setText(String.valueOf(getGraphic().MAP_SIZE));
        btn_feels_time.setChecked(getGraphic().feelsTime);
        dialog.show();
    }
    public abstract Graphic getGraphic();
    public abstract void startSettings(Graphic g, TextElement e);

}
