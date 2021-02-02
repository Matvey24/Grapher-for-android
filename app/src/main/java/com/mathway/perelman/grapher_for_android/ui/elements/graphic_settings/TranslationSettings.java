package com.mathway.perelman.grapher_for_android.ui.elements.graphic_settings;

import android.app.Dialog;
import android.view.inputmethod.EditorInfo;

import com.google.android.material.textfield.TextInputEditText;
import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.controller.DNEditor;
import com.mathway.perelman.grapher_for_android.controller.ModelUpdater;
import com.mathway.perelman.grapher_for_android.ui.elements.elements_list.TextElement;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Graphic;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Translation;

public class TranslationSettings extends DefaultSettings{
    private Translation translation;

    private TextInputEditText et_lines_per_cell;

    public TranslationSettings(MainActivity activity, ModelUpdater updater) {
        super(activity, updater);
    }

    @Override
    protected boolean onScan() {
        String text = DNEditor.getText(et_lines_per_cell);
        int val;
        try{
            val = Integer.parseInt(text);
        }catch (RuntimeException e){
            et_lines_per_cell.setError(activity.getString(R.string.simple_error) + ": " + e.getMessage());
            return false;
        }
        if(val < 1){
            et_lines_per_cell.setError(val + " < 1");
            return false;
        }
        translation.setMultiplyer(val);
        return true;
    }

    @Override
    public Graphic getGraphic() {
        return translation;
    }

    @Override
    public void startSettings(Graphic g, TextElement e) {
        this.translation = (Translation) g;
        if(getDialog() == null){
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.translation_settings);

            et_lines_per_cell = dialog.findViewById(R.id.et_lines_per_cell);
            et_lines_per_cell.setImeOptions(EditorInfo.IME_ACTION_DONE);
            et_lines_per_cell.setOnEditorActionListener((a, b, c) -> {
                fullScan();
                return true;
            });
            onCreateDialog(dialog);
        }
        et_lines_per_cell.setText(String.valueOf(translation.getMultiplyer()));
        show();
    }
}
