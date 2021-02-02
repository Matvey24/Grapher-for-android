package com.mathway.perelman.grapher_for_android.controller;

import android.text.Editable;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class DNEditor {
    private final EditText text;
    private double a, b;

    public DNEditor(EditText text) {
        this.text = text;
        text.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    public String getText() {
        return getText(text);
    }
    public void parse(){
        parseString(getText());
    }
    public void parseString(String s) {
        int idx = s.indexOf(":");
        if (idx == -1) {
            throw new RuntimeException("__:__");
        }
        String _a = s.substring(0, idx);
        String _b = s.substring(idx + 1);
        a = Double.parseDouble(_a);
        b = Double.parseDouble(_b);
    }
    public static String makeString(double a, double b){
        return a + " : " + b;
    }
    public void set(double a, double b){
        text.setText(makeString(a, b));
    }
    public void setError(String error) {
        text.setError(error);
    }

    public void setListener(Runnable r) {
        text.setOnEditorActionListener((a, b, c) -> {
            r.run();
            return true;
        });
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public static String getText(EditText text) {
        Editable str = text.getText();
        if (str == null) {
            return "";
        }
        return str.toString();
    }
}
