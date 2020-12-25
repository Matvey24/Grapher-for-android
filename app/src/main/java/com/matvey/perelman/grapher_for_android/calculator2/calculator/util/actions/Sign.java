package com.matvey.perelman.grapher_for_android.calculator2.calculator.util.actions;

import com.matvey.perelman.grapher_for_android.calculator2.calculator.util.actions.functions.BinarFunc;

public class Sign<T> {
    public final BinarFunc<T> function;
    public final char name;
    public final int priority;
    public boolean canBeUnary;
    public Sign(char name, BinarFunc<T> function, int priority){
        this.name = name;
        this.function = function;
        this.priority = priority;
    }
}
