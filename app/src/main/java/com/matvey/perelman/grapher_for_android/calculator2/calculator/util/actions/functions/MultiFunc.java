package com.matvey.perelman.grapher_for_android.calculator2.calculator.util.actions.functions;

import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.actors.Expression;

public interface MultiFunc<T> {
    T execute(Expression<T>[] a);
}
