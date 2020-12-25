package com.matvey.perelman.grapher_for_android.calculator2.calculator.executors;

import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.actors.Expression;

public class Value<T> implements Expression<T> {
    private T value;
    public void setValue(T value){
        this.value = value;
    }
    @Override
    public T calculate() {
        return value;
    }
    @Override
    public String getName() {
        return value.toString();
    }

    @Override
    public void free() {}

    @Override
    public String toString() {
        return getName();
    }
}
