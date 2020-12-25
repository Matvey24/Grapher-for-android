package com.matvey.perelman.grapher_for_android.ui.elements;

import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.matvey.perelman.grapher_for_android.R;

public class FunctionsView {
    private final EditText area;
    public FunctionsView(AppCompatActivity main, Runnable update){
        area = main.findViewById(R.id.functions_area);
        Button btn_update = main.findViewById(R.id.btn_update);
        btn_update.setOnClickListener((view)->{
            update.run();
        });
    }
    public String getText() {
        Editable e = area.getText();
        return (e == null) ? "" : e.toString();
    }
    public void setText(String text){
        area.setText(text);
    }
}
