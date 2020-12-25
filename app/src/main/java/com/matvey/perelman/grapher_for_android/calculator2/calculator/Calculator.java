package com.matvey.perelman.grapher_for_android.calculator2.calculator;

import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.*;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.actors.*;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.helpers.Helper;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.util.actions.Func;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.util.actions.Sign;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.matvey.perelman.grapher_for_android.calculator2.calculator.Element.ElementType.*;

class Calculator<T> {
    private final Helper<T> helper;
    private final Stack<Expression<T>> values;
    private final Stack<Element> other;
    private final Stack<List<FuncVariable<T>>> lambdas;
    private final Stack<List<FuncVariable<T>>> lambda_params;
    private final Stack<List<FuncVariable<T>>> lambda_params_free;
    private List<FuncVariable<T>> vars;

    private int nextArgs;

    Calculator(Helper<T> helper) {
        this.helper = helper;
        values = new Stack<>();
        other = new Stack<>();
        vars = new ArrayList<>();
        lambdas = new Stack<>();
        lambda_params = new Stack<>();
        lambda_params_free = new Stack<>();
    }

    void next(Element e) {
        if (e == null) {
            count(0);
            return;
        }
        Value<T> value;
        switch (e.type) {
            case FUNCTION:
                other.push(e);
                break;
            case SIGN:
                nextSign(e);
                break;
            case CONSTANT:
                values.push(helper.getConst(e.symbol));
                break;
            case NUMBER:
                value = new Value<>();
                value.setValue(helper.toValue(e.symbol));
                values.push(value);
                break;
            case BRACKET:
                other.push(e);
                nextBracket(e);
                break;
            case DIVIDER:
                count(0);
                break;
            case LAMBDA:
                FuncVariable<T> var;
                other.push(e);
                var = findVar(vars, e.symbol);
                if (var != null)
                    return;
                var = makeVar(e.symbol, e.type);
                vars.add(var);
                break;
            case VAR:
                var = findVar(vars, e.symbol);
                if (var != null) {
                    values.push(var);
                    return;
                }
                var = makeVar(e.symbol, e.type);
                values.push(var);
                vars.add(var);
                break;
            case LAMBDA_PARAM:
                parseLambdaParam(e);
                break;
        }
    }
    private void parseLambdaParam(Element e){
        String[] myvars = e.symbol.split(",");
        List<FuncVariable<T>> old_vars = vars;
        lambdas.push(vars);
        vars = new ArrayList<>();
        List<FuncVariable<T>> lep = (lambda_params_free.empty()?new ArrayList<>():lambda_params_free.pop());
        lambda_params.push(lep);
        for (String s : myvars) {
            if(s.length() == 0)
                continue;
            FuncVariable<T> var = findVar(old_vars, s);
            if(var != null){
                vars.add(var);
                lep.add(var);
                continue;
            }
            if (helper.isVar(s)) {
                var = makeVar(s, VAR);
            }else{
                var = makeVar(s, LAMBDA);
            }
            old_vars.add(var);
            vars.add(var);
            lep.add(var);
        }
    }
    private FuncVariable<T> makeVar(String name, Element.ElementType type){
        if(type == VAR){
            FuncVariable<T> var = new FuncVariable<>();
            var.setName(name);
            var.setValue(helper.def());
            return var;
        }else if(type == LAMBDA){
            @SuppressWarnings("unchecked")
            Expression<T>[] arr = new Expression[0];
            FuncVariable<T> var = new LambdaParameter<>(arr);
            var.setName(name);
            var.setValue(helper.def());
            return var;
        }
        return null;
    }
    private FuncVariable<T> findVar(List<FuncVariable<T>> vars, String name) {
        for (FuncVariable<T> var : vars) {
            if (var.getName().equals(name)) {
                return var;
            }
        }
        return null;
    }

    private void nextSign(Element e) {
        Sign<T> sign = helper.getSign(e.symbol);
        int prio = 0;
        if (!other.empty()) {
            Element e1 = other.peek();
            if (e1.type == SIGN) {
                prio = helper.getSign(e1.symbol).priority;
            } else if (e1.type == FUNCTION) {
                prio = helper.getFunc(e1.symbol).priority;
            } else if (e1.type == LAMBDA) {
                prio = helper.lambdaPrio();
            }
        }
        if (prio >= sign.priority) {
            count(sign.priority);
        }
        other.push(e);
    }

    private void nextBracket(Element el) {
        if (!helper.brackets.brOpens(el.symbol)) {
            for (int i = other.size() - 2; i >= 0; --i) {
                Element e = other.elementAt(i);
                if (e.type == BRACKET) {
                    if (i - 1 >= 0) {
                        if (other.elementAt(i - 1).type == SIGN) {
                            Sign<T> s = helper.getSign(other.elementAt(i - 1).symbol);
                            count(s.priority + 1);
                        } else if (other.elementAt(i - 1).type == FUNCTION) {
                            Func<T> f = helper.getFunc(other.elementAt(i - 1).symbol);
                            count(f.priority + 1);
                        } else if (other.elementAt(i - 1).type == LAMBDA) {
                            count(helper.lambdaPrio() + 1);
                        } else if (other.elementAt(i - 1).type == BRACKET) {
                            count(0);
                        }
                    } else {
                        count(0);
                    }
                    return;
                }
            }
        }
    }

    private void count(int priority) {
        if (other.empty())
            return;
        Element e = other.pop();
        if (e.type == SIGN) {
            Sign<T> sign = helper.getSign(e.symbol);
            if (sign.priority >= priority) {
                Expression<T> e2 = values.pop();
                Expression<T> e1 = values.pop();
                BinaryActor<T> actor = new BinaryActor<>();
                actor.setValues(sign.function, e1, e2, String.valueOf(sign.name));
                values.push(actor);
                count(priority);
            } else {
                other.push(e);
            }
        } else if (e.type == BRACKET) {
            boolean opens = helper.brackets.brOpens(e.symbol);
            if (!opens) {
                count(-1);
                count(priority);
            } else {
                if (priority != -1) {
                    other.push(e);
                    return;
                } else if (e.symbol.length() > 1) {
                    nextArgs = Integer.parseInt(e.symbol.substring(1));
                }
                if (helper.brackets.brLambda(e.symbol)) {
                    LambdaContainer<T> init = new LambdaContainer<>();
                    List<FuncVariable<T>> lep = lambda_params.pop();
                    lambda_params_free.push(lep);
                    for(int i = lep.size() - 1; i >= 0; --i){
                        FuncVariable<T> var = lep.remove(i);
                        vars.remove(var);
                    }
                    init.setValues(values.pop(), vars);
                    values.push(init);
                    vars = lambdas.pop();
                }
            }
        } else if (e.type == FUNCTION) {
            Func<T> f = helper.getFunc(e.symbol);
            if (f.priority >= priority) {
                if (f.args == 1) {
                    Expression<T> e1 = values.pop();
                    if (f.oneFunc != null) {
                        OneActor<T> actor = new OneActor<>();
                        actor.setValues(f.oneFunc, e1, f.name);
                        values.push(actor);
                    } else {
                        UnaryActor<T> actor = new UnaryActor<>();
                        actor.setValues(f.unarFunc, e1, f.name);
                        values.push(actor);
                    }
                } else if (f.args == 2) {
                    Expression<T> e2 = values.pop();
                    Expression<T> e1 = values.pop();

                    BinaryActor<T> actor = new BinaryActor<>();
                    actor.setValues(f.binarFunc, e1, e2, f.name);
                    values.push(actor);
                } else {
                    int args = f.args;
                    if (args == -1) {
                        if (nextArgs == -1) {
                            throw new RuntimeException("NextArgs doesn't exist.");
                        }
                        args = nextArgs;
                        nextArgs = -1;
                    }
                    @SuppressWarnings("unchecked")
                    Expression<T>[] arr = new Expression[args];
                    for (int i = args - 1; i >= 0; --i)
                        arr[i] = values.pop();
                    MultiActor<T> actor = new MultiActor<>();
                    actor.setValues(f.multiFunc, f.name, arr);
                    values.push(actor);
                }
                count(priority);
            } else {
                other.push(e);
            }
        } else if (e.type == LAMBDA) {
            if (helper.lambdaPrio() >= priority) {
                if (nextArgs == -1 || nextArgs == 0) {
                    nextArgs = -1;
                    //this is initializer
                    LambdaParameter<T> param = (LambdaParameter<T>) findVar(vars, e.symbol);

                    LambdaContainer<T> init = new LambdaContainer<>();
                    init.setValues(param, null);
                    values.push(init);
                    count(priority);
                    return;
                }
                int args = nextArgs;
                nextArgs = -1;
                @SuppressWarnings("unchecked")
                Expression<T>[] arr = new Expression[args];
                for (int i = args - 1; i >= 0; --i)
                    arr[i] = values.pop();
                LambdaActor<T> actor = new LambdaActor<>();
                LambdaParameter<T> param = (LambdaParameter<T>) findVar(vars, e.symbol);
                actor.setValues(param, e.symbol, arr);
                values.push(actor);
                count(priority);
            } else {
                other.push(e);
            }
        }
    }

    Expression<T> getExpression() {
        return values.peek();
    }

    List<FuncVariable<T>> getVars() {
        return vars;
    }

    void clear() {
        values.clear();
        other.clear();
        vars.clear();
        lambdas.clear();
        nextArgs = -1;
    }
}
