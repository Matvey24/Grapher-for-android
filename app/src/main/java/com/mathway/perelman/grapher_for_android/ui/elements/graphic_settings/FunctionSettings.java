package com.mathway.perelman.grapher_for_android.ui.elements.graphic_settings;

import android.app.Dialog;

import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.controller.ModelUpdater;
import com.mathway.perelman.grapher_for_android.ui.elements.elements_list.TextElement;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Graphic;

public class FunctionSettings extends DefaultSettings{
    private Graphic function;
    public FunctionSettings(MainActivity activity, ModelUpdater updater){
        super(activity, updater);
    }
    @Override
    public void startSettings(Graphic g, TextElement e){
        this.function = g;
        if(getDialog() == null){
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.function_settings);
            onCreateDialog(dialog);
        }
        show();
    }

    @Override
    protected boolean onScan() {
        return true;
    }

    @Override
    public Graphic getGraphic() {
        return function;
    }
}
