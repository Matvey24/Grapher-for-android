package com.mathway.perelman.grapher_for_android.calculator2.calculator.util.actions.functions;

import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.actors.Expression;

public interface BinarFunc<T> {
    T execute(Expression<T> a, Expression<T> b);
}
