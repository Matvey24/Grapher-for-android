package com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.actors;

public interface Expression<T> {
    T calculate();
    String getName();
    void free();
}
