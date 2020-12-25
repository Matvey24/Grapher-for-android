package com.matvey.perelman.grapher_for_android.controller;

import android.os.Build;

import com.matvey.perelman.grapher_for_android.MainActivity;
import com.matvey.perelman.grapher_for_android.R;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.Parser;
import com.matvey.perelman.grapher_for_android.model.FullModel;
import com.matvey.perelman.grapher_for_android.ui.elements.CalculatorView;
import com.matvey.perelman.grapher_for_android.ui.elements.FunctionsView;
import com.matvey.perelman.grapher_for_android.ui.elements.elements_list.GraphicsAdapter;
import com.matvey.perelman.grapher_for_android.ui.elements.elements_list.TextElement;
import com.matvey.perelman.grapher_for_android.ui.grapher.CoordinateSystem;
import com.matvey.perelman.grapher_for_android.ui.grapher.GraphicsView;
import com.matvey.perelman.grapher_for_android.ui.grapher.graphics.Function;
import com.matvey.perelman.grapher_for_android.ui.grapher.graphics.Graphic;
import com.matvey.perelman.grapher_for_android.ui.grapher.graphics.Implicit;
import com.matvey.perelman.grapher_for_android.ui.grapher.graphics.Parametric;
import com.matvey.perelman.grapher_for_android.ui.grapher.graphics.Translation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.graphics.Color.*;

public class ModelUpdater {
    private static final List<Integer> colors = Arrays.asList(
            BLUE,
            RED,
            rgb(0, 0xCC, 0),
            MAGENTA,
            rgb(0x33, 0xCC, 0xFF),
            rgb(0x99, 0x33, 0xCC),
            rgb(0x99, 0x66, 0x33),
            rgb(0, 0x99, 0x66),
            BLACK
    );
    private static final List<String> func_names = Arrays.asList("f", "g", "i", "j", "l", "m", "n", "o", "bl");
    private final Calculator calculator;
    //    private final SupportFrameManager supportFrameManager;
    private final MainActivity main;
    private final DataBase dataBase;
    public static int HEIGHT;
    public static int GRAPH_WIDTH;
    public final ArrayList<TextElement> elements;
    public final ArrayList<Graphic> graphics;
    private final CoordinateSystem coordinateSystem;
    public GraphicsAdapter list;
    public GraphicsView graphicsView;
    private Graphic lOG;
    private double offsetX = -3;
    private double offsetY = 5.5;
    private double scaleX = 100;
    private double scaleY = 100;
    private double lookX = 0, lookY = 2;
    public boolean dangerState = true;
    public File last_used_file;
    public boolean now_show_graphics;

    public ModelUpdater(MainActivity activity) {
        this.main = activity;
        dataBase = new DataBase();
        graphics = new ArrayList<>();
        elements = new ArrayList<>();
        coordinateSystem = new CoordinateSystem();
        calculator = new Calculator(this, activity, this::resize);
//        supportFrameManager = new SupportFrameManager(this);
    }

    void add(String func, String params) {
        String[] arr = params.split("\\n");
        String name;
        boolean colorSet = false;
        int color = 0;
        {
            String[] t = arr[0].split(" ");
            name = t[0];
            if (t.length > 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    color = Integer.parseUnsignedInt(t[1], 16);
                } else {
                    color = Integer.parseInt(t[1], 16);
                }
                colorSet = true;
            }
        }
        int map_size = Integer.parseInt(arr[1]);
        boolean feels_time = Boolean.parseBoolean(arr[2]);
        String type = arr[3];
        Graphic gr;
        switch (type) {
            case "Function":
                gr = new Function(map_size, feels_time);
                break;
            case "Parametric":
                gr = new Parametric(map_size, feels_time);
                break;
            case "Implicit":
                gr = new Implicit(this::isMousePressed, map_size, feels_time);
                break;
            case "Translation":
                gr = new Translation(this::isMousePressed, getCoordinateSystem(), map_size, feels_time);
                break;
            default:
                return;
        }
        int id = func_names.indexOf(name);
        if (id == -1) {
            id = func_names.size() - 1;
            name = func_names.get(id);
        }
        if (!colorSet) {
            color = colors.get(id);
            gr.color = color;
        } else {
            gr.changeColor(color);
        }
        TextElement e = new TextElement();
        elements.add(e);
        graphics.add(gr);
        gr.name = name;
        e.color = color;
        e.setTextFromFile(func);
        switch (type) {
            case "Function":
                e.name = name + "(x)";
                break;
            case "Parametric":
                e.name = "xy(t)";
                String startEnd = arr[4];
                String[] st = startEnd.split(":");
                ((Parametric) gr).updateBoards(Double.parseDouble(st[0]), Double.parseDouble(st[1]));
                break;
            case "Implicit":
                e.name = name + "(xy)";
                ((Implicit) gr).setSensitivity(Double.parseDouble(arr[4]));
                if (arr.length > 5)
                    ((Implicit) gr).setViewType(Integer.parseInt(arr[5]));
                break;
            case "Translation":
                e.name = "Tran";
                ((Translation) gr).setMultiplyer(Integer.parseInt(arr[4]));
                gr.setMAP_SIZE(gr.MAP_SIZE);
                break;
        }
    }

    public void add(int idx) {
        TextElement element = elements.get(idx);
        Graphic graphic = new Function();
        int id = findFreeId();
        graphics.add(graphic);
        graphic.color = colors.get(id);
        element.setColor(colors.get(id));
        setFuncName(graphic, func_names.get(id), element);
        graphic.name = func_names.get(id);
        calculator.recalculate();
    }

    public void remove(int idx, boolean need_update) {
        //            supportFrameManager.close();
//        int lo = start_make(idx);
        graphics.remove(idx);
        elements.remove(idx);
//        if (lo == idx) {
//            supportFrameManager.close();
//        }
        if (need_update)
            calculator.recalculate();
    }

    public void run(Runnable r) {
        calculator.run(r);
    }

    public void startSettings(int id) {
        Graphic g = graphics.get(id);
//        if (g instanceof Function) {
//            supportFrameManager.openFunctionSettings((Function) g, list.getElements().get(id));
//        } else if (g instanceof Parametric) {
//            supportFrameManager.openParameterSettings((Parametric) g, list.getElements().get(id));
//        } else if (g instanceof Implicit) {
//            supportFrameManager.openImplicitSettings((Implicit) g, list.getElements().get(id));
//        } else if (g instanceof Translation) {
//            supportFrameManager.openTranslationSettings((Translation) g, list.getElements().get(id));
//        }
        lOG = g;
    }

    public void makeFunction(int idx, TextElement e) {
        Graphic g = graphics.get(idx);
        Function function = new Function(Graphic.FUNCTION_MAP_SIZE, g.feelsTime);
        int lo = start_make(idx);
        function.setColor(g);
        graphics.set(idx, function);
        int id = func_names.indexOf(g.name);
        setFuncName(function, func_names.get(id), e);
        function.name = g.name;
        checkClosed(idx, lo);
    }

    public void makeParametric(int idx, TextElement e) {
        Graphic g = graphics.get(idx);
        Parametric parametric = new Parametric(Graphic.PARAMETRIC_MAP_SIZE, g.feelsTime);
        int lo = start_make(idx);
        parametric.setColor(g);
        graphics.set(idx, parametric);
        setFuncName(parametric, null, e);
        parametric.name = g.name;
        checkClosed(idx, lo);
    }

    public void makeImplicit(int idx, TextElement e) {
        Graphic g = graphics.get(idx);
        Implicit implicit = new Implicit(this::isMousePressed, Graphic.IMPLICIT_MAP_SIZE, g.feelsTime);
        int lo = start_make(idx);
        implicit.setColor(g);
        graphics.set(idx, implicit);
        int id = func_names.indexOf(g.name);
        setFuncName(implicit, func_names.get(id), e);
        implicit.name = g.name;
        checkClosed(idx, lo);
    }

    public void makeTranslation(int idx, TextElement e) {
        Graphic g = graphics.get(idx);
        Translation translation = new Translation(this::isMousePressed, getCoordinateSystem(), Graphic.TRANSLATION_MAP_SIZE, g.feelsTime);
        translation.setMAP_SIZE(translation.MAP_SIZE);
        int lo = start_make(idx);
        translation.setColor(g);
        graphics.set(idx, translation);
        setFuncName(translation, null, e);
        translation.name = g.name;
        checkClosed(idx, lo);
    }

    private void setFuncName(Graphic g, String name, TextElement e) {
        if (g instanceof Function) {
            e.setName(name + "(x)");
        } else if (g instanceof Parametric) {
            e.setName("xy(t)");
        } else if (g instanceof Implicit) {
            e.setName(name + "(xy)");
        } else if (g instanceof Translation) {
            e.setName("Tran");
        }
    }

    private int start_make(int idx) {
        int id = graphics.indexOf(lOG);
        graphics.get(idx).free();
        return id;
    }

    private void checkClosed(int idx, int lo) {
//        if (supportFrameManager.isOpenedGraphic() && idx == lo) {
//            startSettings(idx);
//        }
    }

    private int findFreeId() {
        for (int i = 0; i < func_names.size() - 1; ++i) {
            String name = func_names.get(i);
            boolean hasName = false;
            for (Graphic element : graphics) {
                if (element.name.equals(name)) {
                    hasName = true;
                    break;
                }
            }
            if (!hasName)
                return i;
        }
        return func_names.size() - 1;
    }

    public void translate(float dScreenX, float dScreenY) {
        if (dangerState)
            return;
        double dOffsetX = dScreenX / scaleX;
        double dOffsetY = dScreenY / scaleY;
        offsetX -= dOffsetX;
        offsetY += dOffsetY;
        calculator.runResize();
    }

    public void rescale(double delta, float x, float y, int line) {
        if (dangerState)
            return;
        double deltaX = x / scaleX;
        double deltaY = y / scaleY;
        if (line == 0) {
            scaleX *= delta;
            scaleY *= delta;
            offsetX += -x / scaleX + deltaX;
            offsetY += y / scaleY - deltaY;
        } else if (line == 1) {
            scaleX *= delta;
            offsetX += -x / scaleX + deltaX;
        } else if (line == 2) {
            scaleY *= delta;
            offsetY += y / scaleY - deltaY;
        }
        calculator.runResize();
    }

    public void rescaleBack() {
        if (dangerState)
            return;
        double yc = offsetY * scaleY;
        scaleY = 1 * scaleX;
        offsetY = yc / scaleY;

        calculator.runResize();
    }

    public void runResize() {
        calculator.runResize();
    }

    public void openTimer() {
//        supportFrameManager.openTimerSettings();
    }

    public void timerResize() {
        calculator.timerResize();
    }

    public void recalculate() {
        calculator.recalculate();
    }

    public void setStringElements(GraphicsAdapter adapter, FunctionsView functions, CalculatorView calculator) {
        this.list = adapter;
        this.calculator.setElements(calculator, functions);
    }

    public void findEndOf(Parser.StringToken line) {
        calculator.findEndOf(line);
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void lookAtX(double x) {
        if (Math.abs(x) <= Double.MAX_VALUE)
            offsetX = x - GRAPH_WIDTH / scaleX / 2d;
    }

    public void lookAtY(double y) {
        if (Math.abs(y) <= Double.MAX_VALUE)
            offsetY = y + HEIGHT / scaleY / 2d;
    }

    public double getLookAtX() {
        return offsetX + GRAPH_WIDTH / scaleX / 2d;
    }

    public double getLookAtY() {
        return offsetY - HEIGHT / scaleY / 2d;
    }

    public double getMouseX() {
        float x = graphicsView.mouseX;
        return offsetX + x / scaleX;
    }

    public double getMouseY() {
        float y = graphicsView.mouseY;
        return offsetY - y / scaleY;
    }

    public void setScaleX(double x) {
        if (x > 0 && x <= Double.MAX_VALUE) {
            double sX = offsetX + GRAPH_WIDTH / scaleX / 2d;
            scaleX = x;
            offsetX = sX - GRAPH_WIDTH / scaleX / 2d;
        }
    }

    public void setScaleY(double y) {
        if (y > 0 && y <= Double.MAX_VALUE) {
            double sY = offsetY - HEIGHT / scaleY / 2d;
            scaleY = y;
            offsetY = sY + HEIGHT / scaleY / 2d;
        }
    }


    //    public SupportFrameManager getSupportFrameManager() {
//        return supportFrameManager;
//    }
    public void setTime(double time) {
        calculator.resetConstant("tm", time);
    }

    public void setTimerName(String name) {
        main.setTimerName(name);
    }

    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    public void setSuccessCalculated() {
        main.setState(main.getString(R.string.plus));
        if (now_show_graphics)
            main.showGraphics();
    }

    public void setState(String text) {
        main.setState(text);
    }

    public void error(String message) {
        dangerState = true;
        setState(message);
    }

    public boolean isMousePressed() {
        return graphicsView.isMousePressed;
    }

    public void setColor(Graphic g, TextElement el, int c) {
        int id = colors.indexOf(c);
        if (id != -1) {
            g.color = c;
            String name = func_names.get(id);
            setFuncName(g, name, el);
            g.colorChanged = false;
            g.name = name;
        } else
            g.changeColor(c);
        if (g instanceof Implicit)
            ((Implicit) g).setC();
        el.setColor(c);
        calculator.repaint();
    }

    public MainActivity getMain() {
        return main;
    }

    //    public void quick_save(boolean save){
//        if(last_used_file != null){
//            dosave(save, last_used_file);
//        }else{
//            supportFrameManager.openFileChooser(save);
//        }
//    }
    public void setGraphicsView(GraphicsView gv) {
        this.graphicsView = gv;
        calculator.setRepaint(gv::myrepaint);
    }

    public void setBounds(int width, int height) {
        GRAPH_WIDTH = width;
        HEIGHT = height;
        lookAtX(lookX);
        lookAtY(lookY);
        calculator.recalculate();
    }

    public void resize() {
        coordinateSystem.resize(offsetX, offsetY, scaleX, scaleY);
        for (Graphic g : graphics)
            g.resize(offsetX, offsetY, scaleX, scaleY);
    }

    public void clear() {
        for (int i = elements.size() - 1; i >= 0; --i) {
            remove(i, false);
        }
    }

    public void save(File f) {
        FullModel m = new FullModel();
        calculator.makeModel(m);

        m.view_params = getLookAtX() + "\n" + getLookAtY() + "\n" + scaleX + "\n" + scaleY;
        main.makeModel(m);
//        supportFrameManager.getTimer().makeModel(m);
//        supportFrameManager.getMainSettings().makeModel(m);
        m.timer_info = "";
        m.main_settings = "";
        m.resize_idx = "";

        setState(dataBase.save(m, f, main));
        last_used_file = f;
    }

    public void load(File f, boolean needRecalculate) {
        try {
            last_used_file = null;
//            supportFrameManager.getTimer().stop();
            FullModel m = dataBase.load(f);
            clear();
//            supportFrameManager.close();
            calculator.fromModel(m);
            if (needRecalculate)
                list.notifyDataSetChanged();
            if (!m.view_params.isEmpty()) {
                String[] view_params = m.view_params.split("\n");
                scaleX = Double.parseDouble(view_params[2]);
                scaleY = Double.parseDouble(view_params[3]);
                lookX = Double.parseDouble(view_params[0]);
                lookY = Double.parseDouble(view_params[1]);
                if (needRecalculate) {
                    lookAtX(lookX);
                    lookAtY(lookY);
                }
            }
//            main.fromModel(m);
//            supportFrameManager.getTimer().fromModel(m);
//            supportFrameManager.getMainSettings().fromModel(m);
            last_used_file = f;
            if (needRecalculate) {
                calculator.recalculate();
                calculator.run(() -> setState(f.getName() + " " + calculator.getString(R.string.loaded)));
            }
        } catch (Exception e) {
            setState(e.toString());
        }
    }

    public void dosave(boolean save, File f) {
        if (save) {
            calculator.run(() -> save(f));
        } else {
            calculator.run(() -> load(f, true));
        }
    }
}