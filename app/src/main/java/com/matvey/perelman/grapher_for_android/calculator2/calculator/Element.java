package com.matvey.perelman.grapher_for_android.calculator2.calculator;

public class Element {
    String symbol;
    ElementType type;

    Element(String symbol, ElementType type) {
        this.symbol = symbol;
        this.type = type;
    }

    @Override
    public String toString() {
        return "'" + symbol + "'";
    }
    public enum ElementType{
        NUMBER, FUNCTION, VAR, SIGN, DIVIDER, BRACKET, CONSTANT, LAMBDA, LAMBDA_PARAM
    }
}
