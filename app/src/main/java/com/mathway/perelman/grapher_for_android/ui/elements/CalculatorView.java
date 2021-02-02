package com.mathway.perelman.grapher_for_android.ui.elements;

import android.text.Editable;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.FuncVariable;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.LambdaContainer;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.actors.Expression;

public class CalculatorView {
    private final TextView answer;
    private final MainActivity activity;
    private final EditText field;
    private Expression<Double> func;
    private final StringBuilder sb;
    private String answerText;
    @SuppressWarnings("unchecked")
    private final FuncVariable<Double>[] var = new FuncVariable[1];

    public CalculatorView(MainActivity main, Runnable calculate) {
        this.activity = main;
        answer = main.findViewById(R.id.calculator_answer);
        field = main.findViewById(R.id.calculator_field);
        sb = new StringBuilder();
        field.setImeOptions(EditorInfo.IME_ACTION_DONE);
        field.setOnEditorActionListener((view, act, ev) -> {
            calculate.run();
            return true;
        });
        var[0] = new FuncVariable<>();
    }

    public void setFunc(Expression<Double> func) {
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
        if(!text.equals(answerText)){
            answerText = text;
            answer.postOnAnimation(() -> answer.setText(text));
        }
    }

    public String getText() {
        Editable e = field.getText();
        return (e == null) ? "" : e.toString();
    }

    public void setText(String s) {
        activity.runOnUiThread(()->field.setText(s));
    }
}
