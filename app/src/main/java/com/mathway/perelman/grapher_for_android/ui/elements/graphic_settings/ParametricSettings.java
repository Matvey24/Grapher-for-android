package com.mathway.perelman.grapher_for_android.ui.elements.graphic_settings;


import android.app.Dialog;

import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.controller.DNEditor;
import com.mathway.perelman.grapher_for_android.controller.ModelUpdater;
import com.mathway.perelman.grapher_for_android.ui.elements.elements_list.TextElement;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Graphic;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Parametric;

public class ParametricSettings extends DefaultSettings{
    private Parametric parametric;
    private DNEditor dimension;
    public ParametricSettings(MainActivity activity, ModelUpdater updater){
        super(activity, updater);
    }
    @Override
    public Graphic getGraphic() {
        return parametric;
    }

    @Override
    public void startSettings(Graphic g, TextElement e) {
        this.parametric = (Parametric) g;
        if(getDialog() == null){
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.parametric_settings);
            dimension = new DNEditor(dialog.findViewById(R.id.et_parametric_dimension));
            dimension.setListener(this::fullScan);
            onCreateDialog(dialog);
        }
        dimension.set(parametric.getStartT(), parametric.getEndT());
        show();
    }

    @Override
    protected boolean onScan() {
        try{
            dimension.parse();
            parametric.updateBoards(dimension.getA(), dimension.getB());
            return true;
        }catch (RuntimeException e){
            dimension.setError(activity.getString(R.string.simple_error) + ": " + e.getMessage());
            return false;
        }
    }

}
