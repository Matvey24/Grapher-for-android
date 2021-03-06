package com.mathway.perelman.grapher_for_android.controller;

import android.util.Log;

import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.calculator2.ArrayCalculator;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.Parser;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.FuncVariable;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.Variable;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.actors.Expression;
import com.mathway.perelman.grapher_for_android.model.BoolAsk;
import com.mathway.perelman.grapher_for_android.model.FullModel;
import com.mathway.perelman.grapher_for_android.model.GraphType;
import com.mathway.perelman.grapher_for_android.threads.Tasks;
import com.mathway.perelman.grapher_for_android.ui.elements.CalculatorView;
import com.mathway.perelman.grapher_for_android.ui.elements.FunctionsView;
import com.mathway.perelman.grapher_for_android.ui.elements.elements_list.TextElement;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Function;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Graphic;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Implicit;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Parametric;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Translation;
import com.mathway.perelman.grapher_for_android.calculator2.values.Number;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calculator {
    private static final int gotoDefLen = 100000;

    private final ModelUpdater updater;
    private final ArrayCalculator<Double> calculator;
    private BoolAsk repaint;
    private final Tasks tasks;
    private final Runnable resize;
    private MainActivity main;
    private FunctionsView functionsView;
    private CalculatorView calculatorView;
    private List<Variable<Double>> params;
    private int const_idx;
    private int goto_count;
    private int goto_len;
    public Calculator(ModelUpdater updater, MainActivity main, Runnable resize) {
        this.resize = resize;
        this.updater = updater;
        this.tasks = new Tasks();
        this.main = main;
        makeParams();
        calculator = new ArrayCalculator<>();
    }
    public void setMain(MainActivity activity){
        this.main = activity;
    }
    public void setRepaint(BoolAsk repaint){
        this.repaint = repaint;
    }
    public String getString(int id){
        return main.getString(id);
    }
    public void recalculate() {
        tasks.clearTasks();
        tasks.runTask(() -> {
            try {
                updater.setState(getString(R.string.converting));
                updater.graphicsView.view_movable = true;
                List<String> graphs = new ArrayList<>();
                List<String> calc = new ArrayList<>();
                calc.add(calculatorView.getText());
                for (int i = 0; i < updater.elements.size(); ++i) {
                    TextElement e = updater.elements.get(i);
                    if (e.getText().contains(":")) {
                        updater.graphics.get(i).type = GraphType.DOUBLE_FUNC;
                        String text = e.text();
                        String[] expressions = text.split(":");
                        if (expressions.length == 2) {
                            calc.add(expressions[0]);
                            calc.add(expressions[1]);
                        } else {
                            throw new RuntimeException(getString(R.string.updater_errors_two_funcs));
                        }
                    } else {
                        updater.graphics.get(i).type = GraphType.SINGLE_FUNC;
                        graphs.add(updater.graphics.get(i).name + "=" + e.text());
                    }
                }
                calculator.calculate(
                        Arrays.asList(functionsView.getText().split("\n")),
                        graphs,
                        calc,
                        makeNumber()
                );
                calculatorView.setFunc(calculator.getExpressions().get(0));
                int funcs = 0;
                int parameters = 0;
                for (int i = 0; i < updater.graphics.size(); ++i) {
                    Graphic g = updater.graphics.get(i);
                    if (g.type == GraphType.DOUBLE_FUNC) {
                        List<FuncVariable<Double>> varsA = calculator.getExpressionVars().get(parameters);
                        List<FuncVariable<Double>> varsB = calculator.getExpressionVars().get(parameters + 1);
                        int ax = checkFor("x", varsA);
                        int ay = checkFor("y", varsA);
                        int bx = checkFor("x", varsB);
                        int by = checkFor("y", varsB);
                        if (varsA.size() > 1 || varsB.size() > 1
                                || ax != -1 || ay != -1 || bx != -1 || by != -1) {
                            if (varsA.size() > 2 || varsB.size() > 2 || notContainsOnly(varsA, "x", "y")
                                    || notContainsOnly(varsB, "x", "y"))
                                throw new RuntimeException(String.format(getString(R.string.updater_errors_params), i));
                            FuncVariable<Double> varAX, varAY, varBX, varBY;
                            varAX = (ax != -1) ? varsA.get(ax) : new FuncVariable<>();
                            varAY = (ay != -1) ? varsA.get(ay) : new FuncVariable<>();
                            varBX = (bx != -1) ? varsB.get(bx) : new FuncVariable<>();
                            varBY = (by != -1) ? varsB.get(by) : new FuncVariable<>();
                            if (!(g instanceof Translation)) {
                                updater.makeTranslation(i, updater.elements.get(i));
                                g = updater.graphics.get(i);
                            }else
                                g.type = GraphType.TRANSLATION;
                            Expression<Double> funcX = calculator.getExpressions().get(parameters + 1);
                            Expression<Double> funcY = calculator.getExpressions().get(parameters + 2);
                            g.update(funcX, varAX);
                            ((Translation) g).updateY(funcY, varAY, varBX, varBY);
                        } else {
                            if (varsA.size() == 0)
                                varsA.add(new FuncVariable<>());
                            if (varsB.size() == 0)
                                varsB.add(new FuncVariable<>());
                            FuncVariable<Double> varX = varsA.get(0);
                            FuncVariable<Double> varY = varsB.get(0);
                            Expression<Double> funcX = calculator.getExpressions().get(parameters + 1);
                            Expression<Double> funcY = calculator.getExpressions().get(parameters + 2);
                            if (!(g instanceof Parametric)) {
                                updater.makeParametric(i, updater.elements.get(i));
                                g = updater.graphics.get(i);
                            }else
                                g.type = GraphType.PARAMETRIC;
                            g.update(funcY, varY);
                            ((Parametric) g).updateX(funcX, varX);
                        }
                        parameters += 2;
                    } else if (g.type == GraphType.SINGLE_FUNC) {
                        List<FuncVariable<Double>> vars = calculator.getVars().get(funcs);
                        if (vars.size() >= 2) {
                            if (!(g instanceof Implicit)) {
                                updater.makeImplicit(i, updater.elements.get(i));
                                g = updater.graphics.get(i);
                            }else
                                g.type = GraphType.IMPLICIT;
                            Expression<Double> func = calculator.getGraphics().get(funcs);
                            if (vars.size() > 2)
                                throw new RuntimeException(String.format(getString(R.string.updater_errors_params), i));
                            FuncVariable<Double> varX = null;
                            FuncVariable<Double> varY = null;
                            {
                                if (vars.get(0).getName().equals("x")) {
                                    varX = vars.get(0);
                                } else if (vars.get(0).getName().equals("y")) {
                                    varY = vars.get(0);
                                } else {
                                    throw new RuntimeException(String.format(getString(R.string.updater_errors_params), i));
                                }
                                if (vars.get(1).getName().equals("x")) {
                                    varX = vars.get(1);
                                } else if (vars.get(1).getName().equals("y")) {
                                    varY = vars.get(1);
                                } else {
                                    throw new RuntimeException(String.format(getString(R.string.updater_errors_params), i));
                                }
                            }
                            g.update(func, varX);
                            ((Implicit) g).updateY(varY);
                        } else {
                            if (!(g instanceof Function)) {
                                updater.makeFunction(i, updater.elements.get(i));
                                g = updater.graphics.get(i);
                            }else
                                g.type = GraphType.FUNCTION;
                            Function f = (Function) g;
                            if (vars.size() == 0) {
                                FuncVariable<Double> var = new FuncVariable<>();
                                var.setName((f.abscissa) ? "x" : "y");
                                vars.add(var);
                            }
                            TextElement el = updater.elements.get(i);
                            FuncVariable<Double> var = vars.get(0);
                            if (var.getName().equals("y") && f.abscissa) {
                                f.abscissa = false;
                                el.setName(f.name + "(y)");
                            } else if (var.getName().equals("x") && !f.abscissa) {
                                f.abscissa = true;
                                el.setName(f.name + "(x)");
                            }
                            f.update(calculator.getGraphics().get(funcs), var);
                        }
                        ++funcs;
                    }
                }
                resetConstant("tm", updater.getMain().getTime());
                updateConstants();
                calculatorView.update();
                updater.setState(getString(R.string.updating));
                resize.run();
                updater.setSuccessCalculated();
                repaint.ask();
            } catch (Throwable e) {
                if (e instanceof NullPointerException) {
                    updater.error(e.toString());
                    Log.e("Matvey24", "while calculating", e);
                } else if (e instanceof RuntimeException) {
                    updater.error(e.getMessage());
                } else {
                    updater.error(e.toString());
                }
            }
            updater.now_show_graphics = false;
        });
    }

    private int checkFor(String name, List<FuncVariable<Double>> vars) {
        for (int i = 0; i < vars.size(); ++i)
            if (name.equals(vars.get(i).getName()))
                return i;
        return -1;
    }

    private boolean notContainsOnly(List<FuncVariable<Double>> vars, String... names) {
        int num = 0;
        for (String name : names)
            if (checkFor(name, vars) != -1)
                ++num;
        return num != vars.size();
    }

    public void updateConstants() {
        goto_count = 0;
        for (const_idx = 0; const_idx < calculator.getConsts().size(); ++const_idx)
            calculator.getConsts().get(const_idx).run();
    }
    public void repaint(){
        repaint.ask();
    }
    public void runResize() {
        if (!updater.dangerState) {
            tasks.clearTasks();
            tasks.runTask(() -> {
                try {
                    resize.run();
                    repaint.ask();
                } catch (Throwable t) {
                    updater.error(t.getMessage());
                }
            });
        }
    }

    public void timerResize() {
        tasks.clearTasks();
        if(!updater.dangerState) {
            tasks.runTask(() -> {
                try {
                    if (updater.graphicsView.painting)
                        return;
                    updateConstants();
                    for (Graphic g : updater.graphics)
                        g.timeChanged();
                    calculatorView.update();
                    resize.run();
                    repaint.ask();
                } catch (Throwable e) {
                    updater.error(e.getMessage());
                }
            });
        }
    }

    public void findEndOf(Parser.StringToken line){
        calculator.findEndOf(line);
    }
    public void resetConstant(String name, double time) {
        calculator.resetConstant(name, time);
    }

    public void setElements(CalculatorView calculatorView, FunctionsView functionsView) {
        this.calculatorView = calculatorView;
        this.functionsView = functionsView;
    }

    void run(Runnable r) {
        tasks.runTask(r);
    }
    private void makeParams(){
        params = new ArrayList<>();
        Variable<Double> lookX = new Variable<Double>("lookX", null) {
            @Override
            public void setValue(Double value) {
                updater.lookAtX(value);
            }

            @Override
            public Double calculate() {
                return updater.getLookAtX();
            }
        };
        params.add(lookX);
        Variable<Double> lookY = new Variable<Double>("lookY", null) {
            @Override
            public void setValue(Double value) {
                updater.lookAtY(value);
            }

            @Override
            public Double calculate() {
                return updater.getLookAtY();
            }
        };
        params.add(lookY);
        Variable<Double> scaleX = new Variable<Double>("scaleX", null) {
            @Override
            public void setValue(Double value) {
                updater.setScaleX(value);
            }

            @Override
            public Double calculate() {
                return updater.getScaleX();
            }
        };
        params.add(scaleX);
        Variable<Double> scaleY = new Variable<Double>("scaleY", null) {
            @Override
            public void setValue(Double value) {
                updater.setScaleY(value);
            }

            @Override
            public Double calculate() {
                return updater.getScaleY();
            }
        };
        params.add(scaleY);
        Variable<Double> _goto = new Variable<Double>("goto", null) {
            @Override
            public void setValue(Double value) {
                Calculator.this.const_idx = value.intValue() - 1;
                goto_count++;
                if (goto_count >= goto_len) {
                    throw new RuntimeException(getString(R.string.updater_errors_recursion) + " " + goto_count);
                }
            }

            @Override
            public Double calculate() {
                return (double) Calculator.this.const_idx;
            }
        };
        params.add(_goto);
        Variable<Double> goto_len = new Variable<Double>("gotoLen", null) {
            @Override
            public void setValue(Double value) {
                int val = value.intValue();
                if (val > 0)
                    Calculator.this.goto_len = val;
            }

            @Override
            public Double calculate() {
                return (double) Calculator.this.goto_len;
            }
        };
        params.add(goto_len);
        Variable<Double> view_movable = new Variable<Double>("view_movable", null){
            @Override
            public void setValue(Double value) {
                updater.graphicsView.view_movable = value != 0;
            }

            @Override
            public Double calculate() {
                return updater.graphicsView.view_movable?1d:0d;
            }
        };
        params.add(view_movable);
    }
    private Number makeNumber() {
        Number n = new Number();
        for (Variable<Double> param : params) {
            n.consts.put(param.getName(), param);
        }
        n.addFunction("update_graphic", (a)->{
            int idx = a.intValue();
            if(idx < 0)
                idx = 0;
            if(idx >= updater.graphics.size())
                idx = updater.graphics.size() - 1;
            updater.graphics.get(idx).update_graphic();
            return 0d;
        }, 10);
        n.addFunction("finish", (a)->(double)calculator.getConsts().size(), 0, 10);
        n.addFunction("isMousePressed", (a)->updater.isMousePressed()?1d:0d, 0, 10);
        n.addFunction("getMouseX", (a)->updater.getMouseX(), 0, 10);
        n.addFunction("getMouseY", (a)->updater.getMouseY(), 0, 10);
        this.goto_len = gotoDefLen;
        return n;
    }

    public FunctionsView getFunctionsView() {
        return functionsView;
    }

    public CalculatorView getCalculatorView() {
        return calculatorView;
    }

    void makeModel(FullModel m) {
        List<String> graphs = new ArrayList<>();
        List<String> graphicsInfo = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < updater.graphics.size(); ++i) {
            TextElement view = updater.elements.get(i);
            graphs.add(view.text());
            Graphic graphic = updater.graphics.get(i);
            sb.append(graphic.name);
            if(graphic.colorChanged){
                sb.append(" ").append(Integer.toHexString(graphic.color));
            }
            sb.append('\n');
            sb.append(graphic.MAP_SIZE);
            sb.append('\n');
            sb.append(graphic.feelsTime);
            sb.append('\n');
            if (graphic instanceof Function) {
                sb.append("Function");
            } else if (graphic instanceof Parametric) {
                sb.append("Parametric\n");
                sb.append(((Parametric) graphic).getStartT());
                sb.append(':');
                sb.append(((Parametric) graphic).getEndT());
            } else if (graphic instanceof Implicit) {
                sb.append("Implicit\n");
                sb.append(((Implicit) graphic).getSensitivity());
                sb.append("\n");
                sb.append(((Implicit) graphic).viewType);
            } else if (graphic instanceof Translation) {
                sb.append("Translation\n");
                sb.append(((Translation) graphic).getMultiplyer());
            }
            graphicsInfo.add(sb.toString());
            sb.setLength(0);
        }
        m.graphics = graphs;
        m.graphics_info = graphicsInfo;
        m.functions = this.functionsView.getText();
        m.calculator = this.calculatorView.getText();
    }

    public void fromModel(FullModel m) {
        for (int i = 0; i < m.graphics.size(); ++i) {
            String graphic = m.graphics.get(i);
            String params = m.graphics_info.get(i);
            if (params.equals(""))
                continue;
            updater.add(graphic, params);
        }
        calculatorView.setText(m.calculator);
        functionsView.setText(m.functions);
    }
}
