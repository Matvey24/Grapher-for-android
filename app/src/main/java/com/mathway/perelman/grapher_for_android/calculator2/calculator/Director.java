package com.mathway.perelman.grapher_for_android.calculator2.calculator;

import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.actors.Expression;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.FuncVariable;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.helpers.Helper;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.util.AbstractType;

import java.util.List;
import java.util.Stack;

public class Director<T> {
    private final Calculator<T> calculator;
    private final Parser<T> parser;
    private final Helper<T> helper;

    private Expression<T> tree;
    private List<FuncVariable<T>> vars;

    public Director() {
        helper = new Helper<>();
        calculator = new Calculator<>(helper);
        parser = new Parser<>(helper);
    }
    public void setType(AbstractType<T> type){
        helper.type = type;
    }
    public int parse(String str) {
        return parser.parse(str);
    }

    public void update(Stack<Element> stack) {
        parser.simpleCheck(stack);
        calculator.clear();
        while (!stack.empty()) {
            calculator.next(stack.pop());
        }
        calculator.next(null);
        tree = calculator.getExpression();
        vars = calculator.getVars();
    }
    public Stack<Element> getStack() {
        return parser.getStack();
    }

    public List<FuncVariable<T>> getVars() {
        return vars;
    }

    public Expression<T> getTree() {
        return tree;
    }

    public void findEndOf(Parser.StringToken line){
        parser.findEndOf(line);
    }
}
