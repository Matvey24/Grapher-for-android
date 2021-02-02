package com.mathway.perelman.grapher_for_android.ui.elements;

import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;

public class FunctionsView {
    private final EditText area;
    private final MainActivity activity;
    private String text;
    private boolean textSet;
    public FunctionsView(MainActivity main){
        this.activity = main;
        area = main.findViewById(R.id.functions_area);
        Button btn_update = main.findViewById(R.id.btn_update);
        btn_update.setOnClickListener((view)->main.recalculate());
        btn_update.setOnLongClickListener((view)->{
            main.recalculateOpenGraphics();
            return true;
        });
    }
    public String getText() {
        if(textSet){
            textSet = false;
            return text;
        }
        Editable e = area.getText();
        return (e == null) ? "" : e.toString();
    }
    public void setText(String text){
        this.text = text;
        this.textSet = true;
        activity.runOnUiThread(()->area.setText(text));
    }
}
