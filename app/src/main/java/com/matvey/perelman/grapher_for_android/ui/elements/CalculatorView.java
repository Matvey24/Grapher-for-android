package com.matvey.perelman.grapher_for_android.ui.elements;

import android.text.Editable;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.matvey.perelman.grapher_for_android.R;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.FuncVariable;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.LambdaContainer;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.actors.Expression;

public class CalculatorView {
    private final TextView answer;
    private final EditText field;
    private Expression<Double> func;
    private final StringBuilder sb;
    @SuppressWarnings("unchecked")
    private final FuncVariable<Double>[] var = new FuncVariable[1];

    public CalculatorView(AppCompatActivity main, Runnable calculate) {
        answer = main.findViewById(R.id.calculator_answer);
        field = main.findViewById(R.id.calculator_field);
        sb = new StringBuilder();
        field.setOnKeyListener((view, key, event)->{
            if(event.getAction() == KeyEvent.ACTION_DOWN && key == KeyEvent.KEYCODE_ENTER)
                calculate.run();
            return false;
        });
        var[0] = new FuncVariable<>();
    }

    public void setAnswer(Expression<Double> func) {
        this.func = func;
    }

    public void update() {
        if (func != null) {
            if (func instanceof LambdaContainer) {
                sb.setLength(0);
                var[0].setValue(-1.);
                int size = ((LambdaContainer<Double>) func).execute(var).intValue();
                for (int i = 0; i < size; ++i) {
                    if (i != 0)
                        sb.append(',');
                    var[0].setValue(i + .0);
                    double d = ((LambdaContainer<Double>) func).execute(var);
                    sb.append(d);
                }
                setAnswer(sb.toString());
            } else
                setAnswer(String.valueOf(func.calculate()));
        }
    }

    private void setAnswer(String text) {
        answer.post(()->answer.setText(text));
    }

    public String getText() {
        Editable e = field.getText();
        return (e == null) ? "" : e.toString();
    }

    public void setText(String s) {
        field.setText(s);
    }
}
