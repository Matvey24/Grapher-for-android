package com.matvey.perelman.grapher_for_android.ui.elements;

import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.matvey.perelman.grapher_for_android.MainActivity;
import com.matvey.perelman.grapher_for_android.R;

public class FunctionsView {
    private final EditText area;
    private final MainActivity activity;
    public FunctionsView(MainActivity main, Runnable update){
        this.activity = main;
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
        activity.runOnUiThread(()->area.setText(text));
    }
}
