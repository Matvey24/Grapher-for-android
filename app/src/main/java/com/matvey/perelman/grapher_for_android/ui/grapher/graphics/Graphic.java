package com.matvey.perelman.grapher_for_android.ui.grapher.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.FuncVariable;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.actors.Expression;
import com.matvey.perelman.grapher_for_android.model.GraphType;

public abstract class Graphic {
    public static int FUNCTION_MAP_SIZE = 100;
    public static int PARAMETRIC_MAP_SIZE = 100;
    public static int IMPLICIT_MAP_SIZE = 200;
    public static int TRANSLATION_MAP_SIZE = 100;
    public GraphType type;
    public int MAP_SIZE = 500;
    public String name;
    final int MAX_DELTA = 1000;
    double[] map;
    protected Expression<Double> func;
    FuncVariable<Double> var;
    public int color;
    double offsetX;
    double offsetY;
    double scaleY;
    double scaleX;
    double graph_width;
    double graph_height;
    boolean needResize;
    public boolean feelsTime;
    public boolean colorChanged;
    Graphic() {
        color = Color.BLACK;
        feelsTime = true;
    }
    Graphic(int MAP_SIZE, boolean feelsTime){
        this.map = new double[MAP_SIZE];
        this.MAP_SIZE = MAP_SIZE;
        this.feelsTime = feelsTime;
        color = Color.BLACK;
    }
    public void update(Expression<Double> func, FuncVariable<Double> var) {
        this.var = var;
        this.func = func;
        needResize = true;
    }

    public abstract void resize(double offsetX, double offsetY, double scaleX, double scaleY);

    public void setColor(Graphic g) {
        this.color = g.color;
        this.colorChanged = g.colorChanged;
    }
    public void changeColor(int color){this.color = color;this.colorChanged = true;}

    public abstract void paint(Canvas c, Paint p);

    public void timeChanged() {
        if(feelsTime)
            needResize = true;
    }
    public void update_graphic(){
        needResize = true;
    }
    public void free(){
        map = null;
        func = null;
        var = null;
    }
    public void setMAP_SIZE(int map_size) {
        MAP_SIZE = map_size;
        needResize = true;
        map = new double[map_size];
    }
    public static boolean checkValidDiscretization(int desc){
        return desc > 1;
    }
}
